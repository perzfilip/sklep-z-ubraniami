package com.mikolajewald.sklepzubraniami.raport;

import com.itextpdf.text.pdf.BaseFont;
import com.mikolajewald.sklepzubraniami.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class PdfGenaratorUtil {

    private String fontPath = "./resources/ARIALUNI.TTF";
    public String raportOutputPath = "./raporty/";


    @Autowired
    private TemplateEngine templateEngine;

    public Path createPdf(String templateName, List<Item> items) throws Exception {
        Assert.notNull(templateName, "The templateName can not be null");
        Context ctx = new Context();
        ctx.setVariable("items", items);


        String processedHtml = templateEngine.process(templateName, ctx);
        FileOutputStream os = null;
        String fileName = "raport";
        try {
            final File outputFile = File.createTempFile(fileName, ".pdf", new File(raportOutputPath));
            os = new FileOutputStream(outputFile);

            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont(fontPath,
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);

            renderer.setDocumentFromString(processedHtml);

            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();
            System.out.println("PDF created successfully");
            System.out.println(outputFile.toString() + " " + outputFile.getAbsolutePath());
            Path path = Paths.get(outputFile.getAbsolutePath());
            return path;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) { /*ignore*/ }
            }
        }
    }
}