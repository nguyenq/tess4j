/**
 * Copyright @ 2009 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tess4j.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.LoggerFactory;

public class PdfUtilities {

    public static final String GS_INSTALL = "\nPlease download, install GPL Ghostscript from http://www.ghostscript.com\nand/or set the appropriate environment variable.";

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());

    /**
     * Converts PDF to TIFF format.
     *
     * @param inputPdfFile input file
     * @return a multi-page TIFF image
     * @throws IOException
     */
    public static File convertPdf2Tiff(File inputPdfFile) throws IOException {
        File[] pngFiles = null;

        try {
            pngFiles = convertPdf2Png(inputPdfFile);
            File tiffFile = File.createTempFile("multipage", ".tif");

            // put PNG images into a single multi-page TIFF image for return
            ImageIOHelper.mergeTiff(pngFiles, tiffFile);
            return tiffFile;
        } catch (UnsatisfiedLinkError ule) {
            throw new RuntimeException(getMessage(ule.getMessage()));
        } catch (NoClassDefFoundError ncdfe) {
            throw new RuntimeException(getMessage(ncdfe.getMessage()));
        } finally {
            if (pngFiles != null) {
                // delete temporary PNG images
                for (File tempFile : pngFiles) {
                    tempFile.delete();
                }
            }
        }
    }

    /**
     * Converts PDF to PNG format.
     *
     * @param inputPdfFile input file
     * @return an array of PNG images
     */
    public static File[] convertPdf2Png(File inputPdfFile)  {
        File imageDir = inputPdfFile.getParentFile();

        if (imageDir == null) {
            String userDir = System.getProperty("user.dir");
            imageDir = new File(userDir);
        }

        PDDocument document = null;
        try {
            document = PDDocument.load(inputPdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

                // suffix in filename will be used as the file format
                String filename = String.format("workingimage%03d.png", page + 1);
                ImageIOUtil.writeImage(bim, new File(imageDir, filename).getAbsolutePath(), 300);
            }
        }
        catch (IOException ioe) {
            logger.error("Error extracting PDF Document => " + ioe);
        }
        finally {
            if (document != null) {
                try {
                    document.close();
                }
                catch (Exception e) {}
            }
        }

        // find working files
        File[] workingFiles = imageDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().matches("workingimage\\d{3}\\.png$");
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
     * @deprecated As of Release 3.0.
     *
     * @param inputPdfFile input file
     * @param outputPdfFile output file
     * @param firstPage begin page
     * @param lastPage end page
     */
    public static void splitPdf(String inputPdfFile, String outputPdfFile, String firstPage, String lastPage) {
        if (firstPage.trim().isEmpty()) {
            firstPage = "0";
        }
        if (lastPage.trim().isEmpty()) {
            lastPage = "0";
        }

        splitPdf(new File(inputPdfFile), new File(outputPdfFile), Integer.parseInt(firstPage), Integer.parseInt(lastPage));
    }

    /**
     * Splits PDF.
     *
     * @param inputPdfFile input file
     * @param outputPdfFile output file
     * @param firstPage begin page
     * @param lastPage end page
     */
    public static void splitPdf(File inputPdfFile, File outputPdfFile, int firstPage, int lastPage) {
        PDDocument document = null;
        try {
            document = PDDocument.load(inputPdfFile);
            Splitter splitter = new Splitter();

            splitter.setStartPage(firstPage);
            splitter.setEndPage(lastPage);
            splitter.setSplitAtPage(lastPage-firstPage);

            List<PDDocument> documents = splitter.split(document);

            if (documents.size() == 1) {
                PDDocument outputPdf = documents.get(0);
                outputPdf.save(outputPdfFile);
                outputPdf.close();
            }
            else {
                logger.error("Splitter returned " + documents.size() + " documents rather than expected of 1");
            }
        }
        catch (IOException ioe) {
            logger.error("Exception splitting PDF => " + ioe);
        }
        finally {
            if (document != null) {
                try {
                    document.close();
                }
                catch (Exception e) {}
            }
        }
    }

    private static final String PS_FILE = "lib/pdfpagecount.ps";
    private static final String pdfPageCountFilePath;

    static {
        File postscriptFile = LoadLibs.extractTessResources(PS_FILE);
        if (postscriptFile != null && postscriptFile.exists()) {
            pdfPageCountFilePath = postscriptFile.getPath();
        } else {
            pdfPageCountFilePath = PS_FILE;
        }
    }

    /**
     * Gets PDF Page Count.
     *
     * @deprecated As of Release 3.0.
     *
     * @param inputPdfFile input file
     * @return number of pages
     */
    public static int getPdfPageCount(String inputPdfFile) {
        return getPdfPageCount(new File(inputPdfFile));
    }

    /**
     * Gets PDF Page Count.
     *
     * @param inputPdfFile input file
     * @return number of pages
     */
    public static int getPdfPageCount(File inputPdfFile) {
        PDDocument document = null;
        try {
            document = PDDocument.load(inputPdfFile);
            return document.getNumberOfPages();
        }
        catch (IOException ioe) {
            logger.error("Error counting PDF pages => " + ioe);
            return - 1;
        }
        finally {
            if (document != null) {
                try {
                    document.close();
                }
                catch (Exception e) {}
            }
        }
    }

    /**
     * Merges PDF files.
     *
     * @param inputPdfFiles array of input files
     * @param outputPdfFile output file
     */
    public static void mergePdf(File[] inputPdfFiles, File outputPdfFile) {
        try {
            PDFMergerUtility mergerUtility = new PDFMergerUtility();
            mergerUtility.setDestinationFileName(outputPdfFile.getPath());
            for (File inputPdfFile : inputPdfFiles) {
                mergerUtility.addSource(inputPdfFile);
            }
            mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        }
        catch (IOException ioe) {
            logger.error("Error counting PDF pages => " + ioe);
        }
    }

    static String getMessage(String message) {
        if (message.contains("library 'gs") || message.contains("ghost4j")) {
            return message + GS_INSTALL;
        }
        return message;
    }
}
