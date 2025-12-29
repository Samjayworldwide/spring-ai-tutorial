package com.samjay.spring_ai_demo.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataLoaderServiceImplementation implements DataLoaderService {

    @Override
    public List<Document> loadDocumentsFromJsonFile(Resource resource) {

        var jsonReader = new JsonReader(resource);

        return jsonReader.read();
    }

    @Override
    public List<Document> loadDocumentsFromPDFByPage(Resource resource) {

        var pagePdfReader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig
                .builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter
                        .builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build()
                )
                .withPagesPerDocument(1)
                .build()
        );

        return pagePdfReader.read();
    }

    @Override
    public List<Document> loadDocumentsFromPdfByParagraph(Resource resource) {


        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build()
                        )
                        .withPagesPerDocument(1)
                        .build()
        );

        return pdfReader.read();
    }
}
