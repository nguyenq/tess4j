/**
 * Copyright @ 2018 Quan Nguyen
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
import java.nio.file.Files;
import java.nio.file.Path;
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

/**
 * PDF utilities based on PDFBox.
 *
 * @author Robert Drysdale
 * @author Quan Nguyen
 */
public class PdfBoxUtilities {

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
        } finally {
            if (pngFiles != null && pngFiles.length > 0) {
                // get the working directory of the PNG files
                File pngDirectory = new File(pngFiles[0].getParent());
                // delete temporary PNG images
                for (File tempFile : pngFiles) {
                    tempFile.delete();
                }

                pngDirectory.delete();
            }
        }
    }

    /**
     * Converts PDF to PNG format.
     *
     * @param inputPdfFile input file
     * @return an array of PNG images
     * @throws java.io.IOException
     */
    public static File[] convertPdf2Png(File inputPdfFile) throws IOException {
        Path path = Files.createTempDirectory("tessimages");
        File imageDir = path.toFile();

        PDDocument document = null;
        try {
            document = PDDocument.load(inputPdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

                // suffix in filename will be used as the file format
                String filename = String.format("workingimage%04d.png", page + 1);
                ImageIOUtil.writeImage(bim, new File(imageDir, filename).getAbsolutePath(), 300);
            }
        } catch (IOException ioe) {
            logger.error("Error extracting PDF Document => " + ioe);
        } finally {
            if (imageDir.list().length == 0) {
                imageDir.delete();
            }

            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                }
            }
        }

        // find working files
        File[] workingFiles = imageDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().matches("workingimage\\d{4}\\.png$");
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
            splitter.setSplitAtPage(lastPage - firstPage + 1);

            List<PDDocument> documents = splitter.split(document);

            if (documents.size() == 1) {
                PDDocument outputPdf = documents.get(0);
                outputPdf.save(outputPdfFile);
                outputPdf.close();
            } else {
                logger.error("Splitter returned " + documents.size() + " documents rather than expected of 1");
            }
        } catch (IOException ioe) {
            logger.error("Exception splitting PDF => " + ioe);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                }
            }
        }
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
        } catch (IOException ioe) {
            logger.error("Error counting PDF pages => " + ioe);
            return - 1;
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception e) {
                }
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
        } catch (IOException ioe) {
            logger.error("Error counting PDF pages => " + ioe);
        }
    }
}
