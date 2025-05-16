package com.ad17yakr.prompt2deck.geminiwrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("gemini")
public class GeminiLangchainController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeminiLangchainService geminiLangchainService;

    @Autowired
    public GeminiLangchainController(GeminiLangchainService geminiLangchainService) {
        this.geminiLangchainService = geminiLangchainService;
    }

    @PostMapping({"generate", "generate/"})
    public JsonNode generateResponse(@RequestBody Map<String, String> requestBody) throws JsonProcessingException {
        String prompt = requestBody.get("prompt");
        String response = geminiLangchainService.generateResponseAsJson(prompt);
        return objectMapper.readTree(response);
    }
}
