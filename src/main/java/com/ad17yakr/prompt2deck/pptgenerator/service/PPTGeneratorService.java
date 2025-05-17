package com.ad17yakr.prompt2deck.pptgenerator.service;

import com.ad17yakr.prompt2deck.pptgenerator.dto.PresentationDTO;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class PPTGeneratorService {

    private static final Color TITLE_COLOR        = new Color(44, 77, 121);
    private static final Color SUBTITLE_COLOR     = new Color(89, 89, 89);
    private static final Color DESCRIPTION_COLOR  = new Color(67, 67, 67);
    private static final Color KEY_POINTS_COLOR   = new Color(50, 50, 50);

    /**
     * Generates a PowerPoint presentation based on the provided DTO.
     *
     * @param title           Presentation title
     * @param presentationDTO DTO containing slide definitions
     * @return byte[]        PPTX file contents
     * @throws Exception     on any I/O or generation error
     */
    public byte[] generatePresentation(String title,
                                       PresentationDTO presentationDTO) throws Exception {
        Objects.requireNonNull(presentationDTO, "PresentationDTO must not be null");
        return createPowerPoint(title, presentationDTO);
    }

    /**
     * Generates a PowerPoint presentation and writes it to disk.
     *
     * @param title           Presentation title
     * @param presentationDTO DTO containing slide definitions
     * @param outputPath      Path to write the .pptx file
     * @throws Exception      on any I/O or generation error
     */
    public void generateAndSavePresentation(String title,
                                            PresentationDTO presentationDTO,
                                            Path outputPath) throws Exception {
        byte[] pptBytes = generatePresentation(title, presentationDTO);
        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
            fos.write(pptBytes);
        }
    }

    // ---------------------------------------------------------------
    // Internals: building the slides
    // ---------------------------------------------------------------

    private byte[] createPowerPoint(String title,
                                    PresentationDTO dto) throws Exception {
        try (XMLSlideShow ppt = new XMLSlideShow();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            // 1) Title slide
            createTitleSlide(ppt, title);

            // 2) Content slides driven by DTO
            if (dto.getSlidesContent() != null && !dto.getSlidesContent().isEmpty()) {
                for (PresentationDTO.SlideContentDTO slide : dto.getSlidesContent()) {
                    createContentSlide(ppt, slide);
                }
            }

            // write out
            ppt.write(bos);
            return bos.toByteArray();
        }
    }

    private void createTitleSlide(XMLSlideShow ppt, String title) {
        XSLFSlideMaster master     = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout     = master.getLayout(SlideLayout.TITLE);
        XSLFSlide slide            = ppt.createSlide(layout);

        XSLFTextShape titleShape   = slide.getPlaceholder(0);
        titleShape.setText(title);
        titleShape.setFillColor(TITLE_COLOR);

        XSLFTextShape subtitle     = slide.getPlaceholder(1);
        subtitle.setText("Created: " + java.time.LocalDate.now());
        subtitle.setFillColor(SUBTITLE_COLOR);
    }

    private void createContentSlide(XMLSlideShow ppt,
                                    PresentationDTO.SlideContentDTO content) {
        XSLFSlideMaster master     = ppt.getSlideMasters().get(0);
        XSLFSlideLayout layout     = master.getLayout(SlideLayout.TITLE_AND_CONTENT);
        XSLFSlide slide            = ppt.createSlide(layout);

        // Slide title
        XSLFTextShape titleShape   = slide.getPlaceholder(0);
        titleShape.setText(content.getSlide_name());
        titleShape.setFillColor(TITLE_COLOR);

        XSLFTextShape bodyShape    = slide.getPlaceholder(1);
        bodyShape.clearText();

        // Header
        if (content.getHeader() != null && !content.getHeader().isBlank()) {
            XSLFTextParagraph p = bodyShape.addNewTextParagraph();
            XSLFTextRun run     = p.addNewTextRun();
            run.setText(content.getHeader());
            run.setFontSize(24d);
            run.setBold(true);
            run.setFontColor(SUBTITLE_COLOR);
        }

        // Description
        if (content.getDescription() != null && !content.getDescription().isBlank()) {
            XSLFTextParagraph p = bodyShape.addNewTextParagraph();
            XSLFTextRun run     = p.addNewTextRun();
            run.setText(content.getDescription());
            run.setFontSize(18d);
            run.setFontColor(DESCRIPTION_COLOR);
            p.setSpaceAfter(20.);
        }

        // Bullet points
        if (content.getKey_points() != null && !content.getKey_points().isEmpty()) {
            for (String point : content.getKey_points()) {
                XSLFTextParagraph p = bodyShape.addNewTextParagraph();
                p.setBullet(true);
                XSLFTextRun run     = p.addNewTextRun();
                run.setText(point);
                run.setFontSize(16d);
                run.setFontColor(KEY_POINTS_COLOR);
            }
        }
    }

    /**
     * Adds an image to a slide at the specified position.
     *
     * @param ppt       the slide show
     * @param slide     the slide to modify
     * @param imagePath path to the image file (PNG)
     * @param x         x-coordinate
     * @param y         y-coordinate
     * @param w         width
     * @param h         height
     * @throws Exception on I/O error
     */
    private void addImage(XMLSlideShow ppt,
                          XSLFSlide slide,
                          String imagePath,
                          double x, double y, double w, double h) throws Exception {
        try (InputStream is = new FileInputStream(imagePath)) {
            byte[] bytes = IOUtils.toByteArray(is);
            PictureData pd = ppt.addPicture(bytes, PictureData.PictureType.PNG);
            XSLFPictureShape picture = slide.createPicture(pd);
            picture.setAnchor(new Rectangle2D.Double(x, y, w, h));
        }
    }
}
