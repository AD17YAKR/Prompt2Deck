package com.ad17yakr.prompt2deck.pptgenerator.controller;

import com.ad17yakr.prompt2deck.pptgenerator.dto.PresentationDTO;
import com.ad17yakr.prompt2deck.pptgenerator.service.PPTGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/ppt")
public class PPTGeneratorController {

    private final PPTGeneratorService pptService;

    @Autowired
    public PPTGeneratorController(PPTGeneratorService pptService) {
        this.pptService = pptService;
    }

    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadPPT(
            @RequestParam(defaultValue = "Untitled Presentation") String title,
            @RequestBody PresentationDTO dto) {
        try {
            byte[] pptBytes = pptService.generatePresentation(title, dto);
            ByteArrayResource resource = new ByteArrayResource(pptBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(title.replaceAll("\\s+", "_") + ".pptx").build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                    .contentLength(pptBytes.length)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> savePPT(
            @RequestParam(defaultValue = "Untitled_Presentation") String title,
            @RequestBody PresentationDTO dto) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = title.replaceAll("\\s+", "_") + "_" + timestamp + ".pptx";
            Path dir = Paths.get("downloads/generatedppts");
            if (!dir.toFile().exists()) {
                dir.toFile().mkdirs();
            }
            Path filePath = dir.resolve(filename);
            pptService.generateAndSavePresentation(title, dto, filePath);
            return ResponseEntity.ok("Presentation saved at " + filePath.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating presentation: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("PPT Generator service is running");
    }
}
