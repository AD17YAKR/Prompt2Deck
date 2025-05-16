package com.ad17yakr.prompt2deck.geminiwrapper.controller;

import com.ad17yakr.prompt2deck.geminiwrapper.service.GeminiLangChainService;
import com.ad17yakr.prompt2deck.geminiwrapper.dto.DeckContentGeneratorPromptDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gemini")
public class GeminiLangChainController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeminiLangChainService geminiLangchainService;

    @Autowired
    public GeminiLangChainController(GeminiLangChainService geminiLangchainService) {
        this.geminiLangchainService = geminiLangchainService;
    }

    @PostMapping({"generate", "generate/"})
    public JsonNode generateResponse(@RequestBody DeckContentGeneratorPromptDTO promptDTO) throws JsonProcessingException {
        String prompt = promptDTO.getPrompt();
        String response = geminiLangchainService.generateResponseAsJson(prompt);
        return objectMapper.readTree(response);
    }
}
