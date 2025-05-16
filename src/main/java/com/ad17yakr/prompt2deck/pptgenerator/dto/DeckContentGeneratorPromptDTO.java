package com.ad17yakr.prompt2deck.pptgenerator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeckContentGeneratorPromptDTO {
    private String topic;
    private String subject;
    private int slideCount = 10;
    private String tone = "professional";
    private String audienceLevel = "intermediate";
    private String language = "English";

    public String getPrompt() {
        String promptIntro = "Generate a high-quality presentation consisting of " + slideCount + " informative slides " +
                "for the topic: \"" + topic + "\", under the subject: \"" + subject + "\". " +
                "The tone of the slides should be " + tone + ", and the content should be suitable for an audience with " +
                audienceLevel + " knowledge of the subject. The output language should be " + language + ".";

        String promptJsonFormat = "Provide the response in the following JSON format:\n\n" +
                "{\n" +
                "  \"slidesContent\": [\n" +
                "    {\n" +
                "      \"slide_number\": 1,\n" +
                "      \"slide_name\": \"Slide Title\",\n" +
                "      \"header\": \"Main Heading of the Slide\",\n" +
                "      \"description\": \"A detailed explanation or narrative for this slide.\",\n" +
                "      \"key_points\": [\n" +
                "        \"First important point\",\n" +
                "        \"Second highlight\",\n" +
                "        \"Another quick insight\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String promptGuidelines = "Guidelines:\n" +
                "- Ensure the number of slides is exactly " + slideCount + ".\n" +
                "- The first slide should introduce the topic, and the last should summarize or conclude.\n" +
                "- Ensure each slide builds on the topic progressively.\n" +
                "- Use clear and simple language suitable for " + audienceLevel + " audiences.\n" +
                "- Maintain a " + tone + " tone throughout.\n" +
                "- Use practical examples, analogies, or real-world relevance where appropriate.";

        return promptIntro + "\n\n" + promptJsonFormat + "\n\n" + promptGuidelines;
    }
}
