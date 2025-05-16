package com.ad17yakr.prompt2deck.pptgenerator.controller;

import com.ad17yakr.prompt2deck.pptgenerator.dto.PresentationDTO;
import com.ad17yakr.prompt2deck.pptgenerator.service.PPTGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("generateppt")
public class PPTGeneratorController {

    @Autowired
    private PPTGeneratorService pptGeneratorService;

    /**
     * Endpoint to generate and download a PowerPoint presentation
     *
     * @param inputParams Map containing presentation parameters with at least a "title" field
     * @return ResponseEntity with the PowerPoint file for download
     */
    @PostMapping("/download")
    public ResponseEntity<ByteArrayResource> generateAndDownloadPPT(@RequestBody Map<String, String> inputParams) {
        try {
            // Generate the presentation using the service
            byte[] presentationBytes = pptGeneratorService.generatePresentation(inputParams);

            // Create a resource from the byte array
            ByteArrayResource resource = new ByteArrayResource(presentationBytes);

            // Set up the headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"presentation_" +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                            ".pptx\"");

            // Return the response with the file
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                    .contentLength(presentationBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint to generate and save a PowerPoint presentation to the server
     *
     * @param inputParams Map containing presentation parameters with at least a "title" field
     * @return ResponseEntity with success message or error
     */
    @PostMapping("/save")
    public ResponseEntity<String> generateAndSavePPT(@RequestBody Map<String, String> inputParams) {
        try {
            // Generate filename with timestamp
            String filename = "presentation_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                    ".pptx";

            // Create path for saving the file in downloads/generatedppts folder
            String saveDir = "downloads/generatedppts";

            // Ensure directory exists
            Path dirPath = Paths.get(saveDir);
            if (!dirPath.toFile().exists()) {
                dirPath.toFile().mkdirs();
            }

            Path savePath = dirPath.resolve(filename);

            // Generate and save the presentation
            pptGeneratorService.generateAndSavePresentation(inputParams, savePath);

            // Return success message with file path
            return ResponseEntity.ok("Presentation successfully saved to: " + savePath.toAbsolutePath().toString());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating presentation: " + e.getMessage());
        }
    }

    /**
     * Simple health check endpoint
     *
     * @return Status message
     */
    @GetMapping("/status")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("PPT Generator service is running");
    }

    /**
     * Endpoint to generate a presentation with a structured DTO input
     * This provides an alternative to the Map-based input for more complex scenarios
     *
     * @param presentationDTO Structured input containing all presentation details
     * @return ResponseEntity with the PowerPoint file for download
     */
    @PostMapping("/generate-from-dto")
    public ResponseEntity<ByteArrayResource> generateFromDTO(@RequestBody PresentationDTO presentationDTO) {
        try {
            // Convert DTO to Map for service compatibility
            Map<String, String> inputParams = Map.of(
                    "title", "Presentation from DTO",
                    "useProvidedDTO", "true"
            );

            // You would need to modify the service to accept the full DTO
            // For now, we're using the existing service with the map
            byte[] presentationBytes = pptGeneratorService.generatePresentation(inputParams);

            // Create a resource from the byte array
            ByteArrayResource resource = new ByteArrayResource(presentationBytes);

            // Set up the headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"dto_presentation_" +
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                            ".pptx\"");

            // Return the response with the file
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
                    .contentLength(presentationBytes.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}