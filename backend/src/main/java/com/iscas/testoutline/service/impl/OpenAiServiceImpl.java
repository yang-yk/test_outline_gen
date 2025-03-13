package com.iscas.testoutline.service.impl;

import com.iscas.testoutline.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OpenAiServiceImpl implements OpenAiService {
    private final com.theokanning.openai.service.OpenAiService openAiService;

    public OpenAiServiceImpl(com.theokanning.openai.service.OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public ChatCompletionResult createChatCompletion(ChatCompletionRequest request) {
        return openAiService.createChatCompletion(request);
    }

    @Override
    public String generateTestOutline(
            String requirement,
            String templateContent,
            String knowledgeBase,
            Double temperature,
            Integer maxTokens,
            String apiKey
    ) {
        List<ChatMessage> messages = new ArrayList<>();
        
        // 系统提示
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), "你是一个专业的测试工程师，负责编写测试大纲。"));
        
        // 添加知识库内容（如果有）
        if (knowledgeBase != null && !knowledgeBase.isEmpty()) {
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
                "请参考以下测试知识和最佳实践：\n" + knowledgeBase));
        }
        
        // 添加模板内容（如果有）
        if (templateContent != null && !templateContent.isEmpty()) {
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
                "请按照以下模板格式生成测试大纲：\n" + templateContent));
        }
        
        // 用户需求
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), 
            "请根据以下需求生成测试大纲：\n" + requirement));
        
        // 创建请求
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
        
        // 发送请求并获取响应
        ChatCompletionResult result = createChatCompletion(request);
        
        // 返回生成的测试大纲
        return result.getChoices().get(0).getMessage().getContent();
    }
} 