package com.ad17yakr.prompt2deck.pptgenerator.dto;

import lombok.Data;

import java.util.List;

@Data
public class PresentationDTO {
    private List<SlideContentDTO> slidesContent;

    @Data
    public static class SlideContentDTO {
        private int slide_number;
        private String slide_name;
        private String header;
        private String description;
        private List<String> key_points;
    }
}
