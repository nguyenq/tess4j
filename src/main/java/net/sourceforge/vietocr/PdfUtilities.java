/**
 * Copyright @ 2009 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.vietocr;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import org.ghost4j.*;

public class PdfUtilities {

    public static final String GS_INSTALL = "\nPlease download, install GPL Ghostscript from http://sourceforge.net/projects/ghostscript/files\nand/or set the appropriate environment variable.";

    private final static Logger logger = Logger.getLogger(PdfUtilities.class.getName());

    /**
     * Convert PDF to TIFF format.
     *
     * @param inputPdfFile
     * @return a multi-page TIFF image
     */
    public static File convertPdf2Tiff(File inputPdfFile) throws IOException  {
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
     * Convert PDF to PNG format.
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

        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //prepare Ghostscript interpreter parameters
        //refer to Ghostscript documentation for parameter usage
        List<String> gsArgs = new ArrayList<String>();
        gsArgs.add("-gs");
        gsArgs.add("-dNOPAUSE");
        gsArgs.add("-dBATCH");
        gsArgs.add("-dSAFER");
        gsArgs.add("-sDEVICE=pnggray");
        gsArgs.add("-r300");
        gsArgs.add("-dGraphicsAlphaBits=4");
        gsArgs.add("-dTextAlphaBits=4");
        gsArgs.add("-sOutputFile=" + imageDir.getPath() + "/workingimage%03d.png");
        gsArgs.add(inputPdfFile.getPath());

        //execute and exit interpreter
        try {
            gs.initialize(gsArgs.toArray(new String[0]));
            gs.exit();
        } catch (GhostscriptException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
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
     * Split PDF.
     * @param inputPdfFile
     * @param outputPdfFile
     * @param firstPage
     * @param lastPage
     */
    public static void splitPdf(String inputPdfFile, String outputPdfFile, String firstPage, String lastPage) {
        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

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

        //execute and exit interpreter
        try {
            gs.initialize(gsArgs.toArray(new String[0]));
            gs.exit();
        } catch (GhostscriptException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        } catch (UnsatisfiedLinkError ule) {
            throw new RuntimeException(getMessage(ule.getMessage()));
        } catch (NoClassDefFoundError ncdfe) {
            throw new RuntimeException(getMessage(ncdfe.getMessage()));
        }
    }

    /**
     * Get PDF Page Count.
     *
     * @param inputPdfFile
     * @return number of pages
     */
    public static int getPdfPageCount(String inputPdfFile) {
        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //prepare Ghostscript interpreter parameters
        //refer to Ghostscript documentation for parameter usage
        //gs -q -sPDFname=test.pdf pdfpagecount.ps
        List<String> gsArgs = new ArrayList<String>();
        gsArgs.add("-gs");
        gsArgs.add("-dNOPAUSE");
        gsArgs.add("-dQUIET");
        gsArgs.add("-dBATCH");
        gsArgs.add("-sPDFname=" + inputPdfFile);
        gsArgs.add("lib/pdfpagecount.ps");

        int pageCount = 0;
        ByteArrayOutputStream os = null;

        //execute and exit interpreter
        try {
            //output
            os = new ByteArrayOutputStream();
            gs.setStdOut(os);
            gs.initialize(gsArgs.toArray(new String[0]));
            pageCount = Integer.parseInt(os.toString().replace("%%Pages: ", ""));
            os.close();
        } catch (GhostscriptException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return pageCount;
    }

    /**
     * Merge PDF files.
     * 
     * @param inputPdfFiles
     * @param outputPdfFile
     */
    public static void mergePdf(File[] inputPdfFiles, File outputPdfFile) {
        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //prepare Ghostscript interpreter parameters
        //refer to Ghostscript documentation for parameter usage
        //gs -sDEVICE=pdfwrite -dNOPAUSE -dQUIET -dBATCH -sOutputFile=out.pdf in1.pdf in2.pdf in3.pdf
        List<String> gsArgs = new ArrayList<String>();
        gsArgs.add("-gs");
        gsArgs.add("-dNOPAUSE");
        gsArgs.add("-dQUIET");
        gsArgs.add("-dBATCH");
        gsArgs.add("-sDEVICE=pdfwrite");
        gsArgs.add("-sOutputFile=" + outputPdfFile.getPath());

        for (File inputPdfFile : inputPdfFiles) {
            gsArgs.add(inputPdfFile.getPath());
        }

        //execute and exit interpreter
        try {
            gs.initialize(gsArgs.toArray(new String[0]));
            gs.exit();
        } catch (GhostscriptException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        } catch (UnsatisfiedLinkError ule) {
            throw new RuntimeException(getMessage(ule.getMessage()));
        } catch (NoClassDefFoundError ncdfe) {
            throw new RuntimeException(getMessage(ncdfe.getMessage()));
        }
    }

    static String getMessage(String message) {
        if (message.contains("library 'gs") || message.contains("ghost4j")) {
            return message + GS_INSTALL;
        }
        return message;
    }
}
