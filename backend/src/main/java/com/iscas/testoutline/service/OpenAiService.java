package com.iscas.testoutline.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;

public interface OpenAiService {
    ChatCompletionResult createChatCompletion(ChatCompletionRequest request);
    
    String generateTestOutline(
            String requirement,
            String templateContent,
            String knowledgeBase,
            Double temperature,
            Integer maxTokens,
            String apiKey
    );
} 