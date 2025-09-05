package com.gepardec.lernelamalesen.infrastructure.adapter;

import com.gepardec.lernelamalesen.domain.model.DocumentImage;
import com.gepardec.lernelamalesen.domain.port.PdfProcessorPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jboss.logging.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PdfProcessorAdapter implements PdfProcessorPort {

    private static final Logger LOG = Logger.getLogger(PdfProcessorAdapter.class);
    private static final float DPI = 300f;

    @Override
    public List<DocumentImage> extractImagesFromPdf(InputStream pdfStream, String filename) {
        List<DocumentImage> images = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfStream.readAllBytes())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                LOG.infof("Processing page %d of %s", page + 1, filename);

                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, DPI, ImageType.RGB);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bim, "PNG", baos);
                byte[] imageBytes = baos.toByteArray();

                DocumentImage image = new DocumentImage(
                        imageBytes,
                        "image/png",
                        filename + "_page_" + (page + 1) + ".png"
                );

                images.add(image);
            }

            LOG.infof("Extracted %d images from PDF: %s", images.size(), filename);

        } catch (IOException e) {
            LOG.errorf(e, "Error processing PDF: %s", filename);
            throw new RuntimeException("Failed to process PDF: " + filename, e);
        }

        return images;
    }
}