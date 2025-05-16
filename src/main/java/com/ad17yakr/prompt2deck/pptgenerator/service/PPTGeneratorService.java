package com.ad17yakr.prompt2deck.pptgenerator.service;

import com.ad17yakr.prompt2deck.pptgenerator.dto.PresentationDTO;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PPTGeneratorService {

    private static final Color TITLE_COLOR = new Color(44, 77, 121);
    private static final Color SUBTITLE_COLOR = new Color(89, 89, 89);
    private static final Color DESCRIPTION_COLOR = new Color(67, 67, 67);
    private static final Color KEY_POINTS_COLOR = new Color(50, 50, 50);

    /**
     * Generates a PowerPoint presentation based on input parameters
     *
     * @param inputParams Map containing at minimum a "title" key
     * @return byte array containing the generated PowerPoint
     * @throws Exception if there's an error during generation
     */
    public byte[] generatePresentation(Map<String, String> inputParams) throws Exception {
        // Extract title from input params
        String title = inputParams.getOrDefault("title", "Untitled Presentation");

        // Convert input to PresentationDTO or create a mock
        PresentationDTO presentationDTO = createPresentationDTO(inputParams);

        // Create presentation with the slides
        return createPowerPoint(title, presentationDTO);
    }

    /**
     * Generates a PowerPoint presentation and saves it to the specified path
     *
     * @param inputParams Map containing at minimum a "title" key
     * @param outputPath Path where the PowerPoint file should be saved
     * @throws Exception if there's an error during generation
     */
    public void generateAndSavePresentation(Map<String, String> inputParams, Path outputPath) throws Exception {
        byte[] presentationBytes = generatePresentation(inputParams);

        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            fos.write(presentationBytes);
        }
    }

    /**
     * Creates a PowerPoint presentation from the provided DTO
     *
     * @param title Title of the presentation
     * @param presentationDTO DTO containing slide content
     * @return byte array of the PowerPoint presentation
     * @throws Exception if there's an error during generation
     */
    private byte[] createPowerPoint(String title, PresentationDTO presentationDTO) throws Exception {
        try (XMLSlideShow ppt = new XMLSlideShow()) {
            // Create title slide
            createTitleSlide(ppt, title);

            // Create content slides
            if (presentationDTO.getSlidesContent() != null) {
                for (PresentationDTO.SlideContentDTO slideContentDTO : presentationDTO.getSlidesContent()) {
                    createContentSlide(ppt, slideContentDTO);
                }
            }

            // Convert to byte array
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                ppt.write(bos);
                return bos.toByteArray();
            }
        }
    }

    /**
     * Creates the title slide for the presentation
     *
     * @param ppt XMLSlideShow to add the slide to
     * @param title Title of the presentation
     */
    private void createTitleSlide(XMLSlideShow ppt, String title) {
        XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout titleLayout = slideMaster.getLayout(SlideLayout.TITLE);
        XSLFSlide slide = ppt.createSlide(titleLayout);

        // Add title
        XSLFTextShape titleShape = slide.getPlaceholder(0);
        titleShape.setText(title);
        titleShape.setFillColor(TITLE_COLOR);

        // Add subtitle with current date
        XSLFTextShape subtitleShape = slide.getPlaceholder(1);
        subtitleShape.setText("Created: " + java.time.LocalDate.now().toString());
        subtitleShape.setFillColor(SUBTITLE_COLOR);
    }

    /**
     * Creates a content slide based on the provided slide content DTO
     *
     * @param ppt XMLSlideShow to add the slide to
     * @param slideContentDTO DTO containing slide content
     */
    private void createContentSlide(XMLSlideShow ppt, PresentationDTO.SlideContentDTO slideContentDTO) {
        XSLFSlideMaster slideMaster = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout = slideMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
        XSLFSlide slide = ppt.createSlide(layout);

        // Set slide title
        XSLFTextShape titleShape = slide.getPlaceholder(0);
        titleShape.setText(slideContentDTO.getSlide_name());
        titleShape.setFillColor(TITLE_COLOR);

        // Create content area
        XSLFTextShape contentShape = slide.getPlaceholder(1);
        contentShape.clearText();

        // Add header if present
        if (slideContentDTO.getHeader() != null && !slideContentDTO.getHeader().isEmpty()) {
            XSLFTextParagraph headerParagraph = contentShape.addNewTextParagraph();
            XSLFTextRun headerRun = headerParagraph.addNewTextRun();
            headerRun.setText(slideContentDTO.getHeader());
            headerRun.setFontSize(24.0);
            headerRun.setBold(true);
            headerRun.setFontColor(SUBTITLE_COLOR);
        }

        // Add description if present
        if (slideContentDTO.getDescription() != null && !slideContentDTO.getDescription().isEmpty()) {
            XSLFTextParagraph descParagraph = contentShape.addNewTextParagraph();
            XSLFTextRun descRun = descParagraph.addNewTextRun();
            descRun.setText(slideContentDTO.getDescription());
            descRun.setFontSize(18.0);
            descRun.setFontColor(DESCRIPTION_COLOR);

            // Add spacing after description
            descParagraph.setSpaceAfter(20.0);
        }

        // Add key points if present
        if (slideContentDTO.getKey_points() != null && !slideContentDTO.getKey_points().isEmpty()) {
            for (String point : slideContentDTO.getKey_points()) {
                XSLFTextParagraph pointParagraph = contentShape.addNewTextParagraph();
                pointParagraph.setBullet(true);
                pointParagraph.setIndentLevel(0);

                XSLFTextRun pointRun = pointParagraph.addNewTextRun();
                pointRun.setText(point);
                pointRun.setFontSize(16.0);
                pointRun.setFontColor(KEY_POINTS_COLOR);
            }
        }
    }

    /**
     * Converts the input parameters to a PresentationDTO or creates a mock
     *
     * @param inputParams Map containing input parameters
     * @return A PresentationDTO object
     */
    private PresentationDTO createPresentationDTO(Map<String, String> inputParams) {
        // This is a placeholder implementation - in a real app, you would parse the
        // inputParams to create a proper PresentationDTO

        PresentationDTO dto = new PresentationDTO();
        List<PresentationDTO.SlideContentDTO> slides = new ArrayList<>();

        // Creating a mock DTO with some example slides
        // In a real implementation, you'd parse the input params to create this

        // Slide 1: Introduction
        PresentationDTO.SlideContentDTO slide1 = new PresentationDTO.SlideContentDTO();
        slide1.setSlide_number(1);
        slide1.setSlide_name("Introduction");
        slide1.setHeader("About This Presentation");
        slide1.setDescription("This presentation was automatically generated using Apache POI.");
        slide1.setKey_points(List.of(
                "Generated from structured data",
                "Uses Apache POI 5.4.1",
                "Demonstrates dynamic PowerPoint generation"
        ));
        slides.add(slide1);

        // Slide 2: Features
        PresentationDTO.SlideContentDTO slide2 = new PresentationDTO.SlideContentDTO();
        slide2.setSlide_number(2);
        slide2.setSlide_name("Key Features");
        slide2.setHeader("Features of Generated Presentations");
        slide2.setDescription("Our PowerPoint generator offers several useful features:");
        slide2.setKey_points(List.of(
                "Consistent formatting across slides",
                "Automatic bullet points for key points",
                "Custom headers and descriptions",
                "Easily extensible for additional content"
        ));
        slides.add(slide2);

        // Slide 3: Custom from input (if provided)
        if (inputParams.containsKey("custom_slide_title") && inputParams.containsKey("custom_slide_content")) {
            PresentationDTO.SlideContentDTO slide3 = new PresentationDTO.SlideContentDTO();
            slide3.setSlide_number(3);
            slide3.setSlide_name(inputParams.get("custom_slide_title"));
            slide3.setHeader("Custom Content");
            slide3.setDescription(inputParams.get("custom_slide_content"));
            slide3.setKey_points(List.of(
                    "This slide was generated from custom input",
                    "You can add any content here",
                    "The possibilities are endless"
            ));
            slides.add(slide3);
        }

        dto.setSlidesContent(slides);
        return dto;
    }

    /**
     * Adds an image to a slide
     *
     * @param ppt XMLSlideShow to add the image to
     * @param slide Slide to add the image to
     * @param imagePath Path to the image file
     * @param x X position
     * @param y Y position
     * @param width Width of the image
     * @param height Height of the image
     * @throws Exception if there's an error adding the image
     */
    private void addImage(XMLSlideShow ppt, XSLFSlide slide, String imagePath,
                          double x, double y, double width, double height) throws Exception {
        try (InputStream is = new FileInputStream(imagePath)) {
            byte[] pictureData = IOUtils.toByteArray(is);
            PictureData pd = ppt.addPicture(pictureData, PictureData.PictureType.PNG);
            XSLFPictureShape picture = slide.createPicture(pd);
            picture.setAnchor(new Rectangle2D.Double(x, y, width, height));
        }
    }
}