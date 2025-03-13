package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.entity.ChatMessage;
import com.iscas.testoutline.repository.ChatMessageRepository;
import com.iscas.testoutline.service.ChatService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final OpenAiService openAiService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${openai.model}")
    private String defaultModel;

    @Value("${openai.max-tokens}")
    private Integer maxTokens;

    @Value("${openai.temperature}")
    private Double temperature;

    @Override
    @Transactional
    public ChatMessage sendMessage(Long userId, String content, String model) {
        // 保存用户消息
        ChatMessage userMessage = new ChatMessage();
        userMessage.setUserId(userId);
        userMessage.setContent(content);
        userMessage.setRole(ChatMessageRole.USER.value());
        userMessage.setModel(model != null ? model : defaultModel);
        chatMessageRepository.save(userMessage);

        // 构建请求
        List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
        messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                ChatMessageRole.USER.value(), content));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(userMessage.getModel())
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();

        // 发送请求并获取响应
        ChatCompletionResult result = openAiService.createChatCompletion(request);
        String responseContent = result.getChoices().get(0).getMessage().getContent();

        // 保存助手消息
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setUserId(userId);
        assistantMessage.setContent(responseContent);
        assistantMessage.setRole(ChatMessageRole.ASSISTANT.value());
        assistantMessage.setModel(userMessage.getModel());
        return chatMessageRepository.save(assistantMessage);
    }

    @Override
    public SseEmitter sendMessageStream(Long userId, String content, String model) {
        SseEmitter emitter = new SseEmitter();
        
        // 保存用户消息
        ChatMessage userMessage = new ChatMessage();
        userMessage.setUserId(userId);
        userMessage.setContent(content);
        userMessage.setRole(ChatMessageRole.USER.value());
        userMessage.setModel(model != null ? model : defaultModel);
        chatMessageRepository.save(userMessage);

        // 使用线程池异步处理流式响应
        executorService.execute(() -> {
            try {
                // 构建OpenAI请求
                List<com.theokanning.openai.completion.chat.ChatMessage> messages = new ArrayList<>();
                messages.add(new com.theokanning.openai.completion.chat.ChatMessage(
                        ChatMessageRole.USER.value(), content));

                ChatCompletionRequest request = ChatCompletionRequest.builder()
                        .model(userMessage.getModel())
                        .messages(messages)
                        .maxTokens(maxTokens)
                        .temperature(temperature)
                        .stream(true)
                        .build();

                StringBuilder fullResponse = new StringBuilder();

                // 处理流式响应
                openAiService.streamChatCompletion(request)
                        .doOnNext(chunk -> {
                            try {
                                String chunkContent = chunk.getChoices().get(0).getMessage().getContent();
                                if (chunkContent != null) {
                                    fullResponse.append(chunkContent);
                                    emitter.send(chunkContent);
                                }
                            } catch (IOException e) {
                                log.error("发送流式消息失败", e);
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                // 保存完整的助手回复
                                ChatMessage assistantMessage = new ChatMessage();
                                assistantMessage.setUserId(userId);
                                assistantMessage.setContent(fullResponse.toString());
                                assistantMessage.setRole(ChatMessageRole.ASSISTANT.value());
                                assistantMessage.setModel(userMessage.getModel());
                                chatMessageRepository.save(assistantMessage);

                                emitter.complete();
                            } catch (Exception e) {
                                log.error("保存助手回复失败", e);
                            }
                        })
                        .doOnError(e -> {
                            try {
                                log.error("流式响应处理失败", e);
                                // 保存错误消息
                                ChatMessage errorMessage = new ChatMessage();
                                errorMessage.setUserId(userId);
                                errorMessage.setContent("消息处理失败：" + e.getMessage());
                                errorMessage.setRole(ChatMessageRole.SYSTEM.value());
                                errorMessage.setModel(userMessage.getModel());
                                chatMessageRepository.save(errorMessage);

                                emitter.completeWithError(e);
                            } catch (Exception ex) {
                                log.error("保存错误消息失败", ex);
                            }
                        })
                        .subscribe(); // 使用 subscribe() 替代 blockLast()

            } catch (Exception e) {
                log.error("创建流式响应失败", e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.error("发送错误消息失败", ex);
                }
            }
        });

        return emitter;
    }

    @Override
    public Page<ChatMessage> getChatHistory(Long userId, Pageable pageable) {
        return chatMessageRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }

    @Override
    @Transactional
    public void clearChatHistory(Long userId) {
        chatMessageRepository.deleteByUserId(userId);
    }

    @Override
    public String[] getAvailableModels() {
        return new String[]{
            "gpt-4-turbo-preview",
            "gpt-4",
            "gpt-3.5-turbo"
        };
    }
} 