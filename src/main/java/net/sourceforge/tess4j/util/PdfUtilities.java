/**
 * Copyright @ 2009 Quan Nguyen
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tess4j.util;

import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class PdfUtilities {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());

    /**
     * Converts PDF to TIFF format.
     *
     * @param inputPdfFile
     * @return a multi-page TIFF image
     * @throws IOException
     */
    public static File convertPdf2Tiff(File inputPdfFile) throws IOException {
        logger.debug("");
        File[] pngFiles = null;

        pngFiles = convertPdf2Png(inputPdfFile);
        File tiffFile = File.createTempFile("multipage", ".tif");

        // put PNG images into a single multi-page TIFF image for return
        ImageIOHelper.mergeTiff(pngFiles, tiffFile);

        if (pngFiles != null) {
            // delete temporary PNG images
            for (File tempFile : pngFiles) {
                tempFile.delete();
            }
        }
        return tiffFile;
    }

    /**
     * Converts PDF to PNG format.
     *
     * @param inputPdfFile
     * @return an array of PNG images
     */
    public static File[] convertPdf2Png(File inputPdfFile) {
        File imageDir = null;
        try {

            if (null == inputPdfFile.getParentFile()) {
                String userDir = System.getProperty("user.dir");
                imageDir = new File(userDir);
                logger.debug("Using image dir folder: '{}'.", imageDir.getAbsoluteFile());
            } else {
                imageDir = inputPdfFile.getParentFile();
            }

            PDDocument document = PDDocument.load(inputPdfFile);
            ImageType imageType = ImageType.BINARY;

            int endPage = document.getNumberOfPages();
            PDFRenderer renderer = new PDFRenderer(document);

            boolean success = true;
            for (int i = 0; i < endPage; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300, imageType);
                String fileName = String.format("%s/workingimage%d.png", imageDir.getPath(), i);
                success &= ImageIOUtil.writeImage(image, fileName, 300);
            }

        } catch (IOException e) {
            logger.error(e.getCause().toString(), e);
        }

        // find working files
        File[] workingFiles = imageDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().matches("workingimage\\d\\.png$");
            }
        });

        Arrays.sort(workingFiles, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });

        return workingFiles;
    }

    /**
     * Splits PDF.
     *
     * @param inputPdfFile
     * @param outputPdfFile
     * @param startPage
     * @param endPage
     */
    public static void splitPdf(File inputPdfFile, File outputPdfFile, int startPage, int endPage) {
        List<PDDocument> documents = null;
        try {

            logger.info("Splitting file: '{}' from page '{}' to page '{}' into file '{}'.", inputPdfFile.getAbsoluteFile(), startPage, endPage, outputPdfFile.getAbsoluteFile());

            PDDocument document = PDDocument.load(inputPdfFile);
            Splitter splitter = new Splitter();
            splitter.setStartPage(startPage);
            splitter.setEndPage(endPage);
            documents = splitter.split(document);

            PDDocument doc = new PDDocument();
            for (int i = 0; i < documents.size(); i++) {
                doc.addPage(documents.get(i).getPage(0));
            }
            writeDocument(doc, outputPdfFile.getAbsolutePath());
            doc.close();

        } catch (IOException e) {
            logger.error(e.getCause().toString(), e);
        }


    }

    private static final String PS_FILE = "lib/pdfpagecount.ps";
    private static final String pdfPageCountFilePath;

    static {
        File postscriptFile = LoadLibs.extractTessResources(PS_FILE);
        if (postscriptFile != null) {
            pdfPageCountFilePath = postscriptFile.getPath();
        } else {
            pdfPageCountFilePath = PS_FILE;
        }
    }

    /**
     * Gets PDF Page Count.
     *
     * @param inputPdfFile
     * @return number of pages
     */
    public static int getPdfPageCount(String inputPdfFile) {

        int result = 0;
        PDDocument document = null;

        try {
            document = PDDocument.load(new File(inputPdfFile));
            result = document.getNumberOfPages();
        } catch (IOException e) {
            logger.error(e.getCause().toString(), e);
        }

        logger.debug("Return PDF Page count: '{}'", result);
        return result;
    }

    /**
     * Merge PDF files.
     *
     * @param inputPdfFiles
     * @param outputPdfFile
     */
    public static void mergePdf(File[] inputPdfFiles, File outputPdfFile) {
        logger.debug("Merginging PDF Files '{}' into one: '{}'.", inputPdfFiles, outputPdfFile);
        final PDFMergerUtility mergePdf = new PDFMergerUtility();

        try {

            for (final File inputPdfFile : inputPdfFiles) {
                mergePdf.addSource(inputPdfFile.getAbsoluteFile());
            }

            mergePdf.setDestinationFileName(outputPdfFile.getAbsolutePath());
            mergePdf.mergeDocuments(null);

        } catch (final FileNotFoundException e) {
            logger.error(e.getCause().toString(), e);
        } catch (final IOException e) {
            logger.error(e.getCause().toString(), e);
        }

    }

    /**
     * Helper method to persist the PDF Document into the File System.
     *
     * @param doc
     * @param fileName
     * @throws IOException
     */
    private static void writeDocument(PDDocument doc, String fileName) throws IOException {
        FileOutputStream output = null;
        COSWriter writer = null;
        try {
            output = new FileOutputStream(fileName);
            writer = new COSWriter(output);
            writer.write(doc);
        } finally {
            if (output != null) {
                output.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

}
