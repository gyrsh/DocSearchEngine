package com.searchengine.crawlservice.handler;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

@Component
public class PdfParser {

    private static final Logger logger = LoggerFactory.getLogger(PdfParser.class);
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 30_000;
    private static final String USER_AGENT = "SearchEngineCrawler/0.1";

    public String handle(String documentLocation) {
        if (documentLocation == null || documentLocation.isBlank()) {
            logger.warn("Skipping PDF parse because document location is blank");
            return "";
        }

        Path temporaryDownload = null;
        try {
            File pdfFile;
            if (isRemoteLocation(documentLocation)) {
                temporaryDownload = downloadToTemporaryFile(documentLocation);
                pdfFile = temporaryDownload.toFile();
            } else {
                Path localPath = Path.of(documentLocation);
                if (!Files.isRegularFile(localPath)) {
                    logger.warn("Skipping PDF parse because file does not exist: {}", documentLocation);
                    return "";
                }
                pdfFile = localPath.toFile();
            }

            try (PDDocument document = Loader.loadPDF(pdfFile)) {
                if (document.isEncrypted()) {
                    logger.warn("Skipping encrypted PDF: {}", documentLocation);
                    return "";
                }

                return extractText(document);
            }
        } catch (IOException | IllegalArgumentException ex) {
            logger.error("Failed to parse PDF: {}", documentLocation, ex);
            return "";
        } finally {
            deleteTemporaryFile(temporaryDownload);
        }
    }

    private String extractText(PDDocument document) throws IOException {
        if (document.getNumberOfPages() == 0) {
            return "";
        }

        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setSortByPosition(true);
        String text = pdfStripper.getText(document);
        return normalizeText(text);
    }

    private Path downloadToTemporaryFile(String documentUrl) throws IOException {
        Path temporaryFile = Files.createTempFile("crawl-pdf-", ".pdf");
        URLConnection connection = URI.create(documentUrl).toURL().openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setRequestProperty("User-Agent", USER_AGENT);

        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(inputStream, temporaryFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return temporaryFile;
        } catch (IOException ex) {
            deleteTemporaryFile(temporaryFile);
            throw ex;
        }
    }

    private boolean isRemoteLocation(String documentLocation) {
        String normalizedLocation = documentLocation.toLowerCase(Locale.ROOT);
        return normalizedLocation.startsWith("http://") || normalizedLocation.startsWith("https://");
    }

    private String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text
                .replace('\u0000', ' ')
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }

        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException ex) {
            logger.warn("Failed to delete temporary PDF file: {}", temporaryFile, ex);
        }
    }
}
