package com.gepardec.lernelamalesen.domain.port;

import com.gepardec.lernelamalesen.domain.model.DocumentImage;
import java.io.InputStream;
import java.util.List;

public interface PdfProcessorPort {
    List<DocumentImage> extractImagesFromPdf(InputStream pdfStream, String filename);
}