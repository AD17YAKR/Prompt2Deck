package com.ad17yakr.prompt2deck.geminiwrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class GeminiLangchainService {
    private final ChatModel gemini;
    private final ObjectMapper objectMapper;

    public GeminiLangchainService(@Value("${gemini.api.key}") String apiKey, @Value("${gemini.api.model}") String apiModel) {

        gemini = GoogleAiGeminiChatModel
                .builder()
                .apiKey(apiKey)
                .modelName(apiModel)
                .temperature(1.0)
                .topP(0.95)
                .topK(64)
                .maxOutputTokens(8192)
                .timeout(Duration.ofSeconds(60))
                .responseFormat(ResponseFormat.JSON)
                .allowCodeExecution(false)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String generateResponseAsJson(String prompt) {
        ChatResponse chatResponse = gemini.chat(ChatRequest.builder().messages(UserMessage.from(prompt)).build());
        return chatResponse.aiMessage().text();
    }
}