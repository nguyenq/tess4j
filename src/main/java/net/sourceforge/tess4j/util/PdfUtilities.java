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

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
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
        File imageDir = inputPdfFile.getParentFile();

        if (imageDir == null) {
            String userDir = System.getProperty("user.dir");
            imageDir = new File(userDir);
        }

        try {

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
     * @param inputPdfFile
     * @param outputPdfFile
     * @param firstPage
     * @param lastPage
     */
    public static void splitPdf(String inputPdfFile, String outputPdfFile, String firstPage, String lastPage) {
        //TODO #20
        //get Ghostscript instance

        //prepare Ghostscript interpreter parameters
        //refer to Ghostscript documentation for parameter usage
        //gs -sDEVICE=pdfwrite -dNOPAUSE -dQUIET -dBATCH -dFirstPage=m -dLastPage=n -sOutputFile=out.pdf in.pdf
        List<String> gsArgs = new ArrayList<String>();
        gsArgs.add("-gs");
        gsArgs.add("-dNOPAUSE");
        gsArgs.add("-dQUIET");
        gsArgs.add("-dBATCH");
        gsArgs.add("-sDEVICE=pdfwrite");

        if (!firstPage.trim().isEmpty()) {
            gsArgs.add("-dFirstPage=" + firstPage);
        }

        if (!lastPage.trim().isEmpty()) {
            gsArgs.add("-dLastPage=" + lastPage);
        }

        gsArgs.add("-sOutputFile=" + outputPdfFile);
        gsArgs.add(inputPdfFile);


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

//    /**
//     * Gets PDF Page Count using Ghost4J's new high-level API available in Ghost4J 0.4.0.
//     * (Taken out due to many required additional libraries.)
//     *
//     * @param inputPdfFile
//     * @return number of pages
//     */
//    public static int getPdfPageCount1(String inputPdfFile) {
//        int pageCount = 0;
//
//        try {
//            // load PDF document
//            PDFDocument document = new PDFDocument();
//            document.load(new File(inputPdfFile));
//            pageCount = document.getPageCount();
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, e.getMessage(), e);
//        }
//        return pageCount;
//    }

    /**
     * Merge PDF files.
     *
     * @param inputPdfFiles
     * @param outputPdfFile
     */
    public static void mergePdf(File[] inputPdfFiles, File outputPdfFile) {
        PDFMergerUtility mergePdf = new PDFMergerUtility();

        try {
            for (File inputPdfFile : inputPdfFiles) {
                mergePdf.addSource(outputPdfFile.getPath() + File.separator + inputPdfFile.getAbsolutePath());
            }
            mergePdf.mergeDocuments();
        } catch (FileNotFoundException e) {
            logger.error(e.getCause().toString(), e);
        } catch (IOException e) {
            logger.error(e.getCause().toString(), e);
        }

    }

}
