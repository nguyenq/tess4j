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

import java.io.File;
import java.io.IOException;

/**
 * PDF utilities based on Ghostscript or PDFBox with Ghostscript as default. If
 * Ghostscript is not available on the system, then PDFBox is used. Call
 * <code>System.setProperty(PDF_LIBRARY, PDFBOX);</code> to set PDFBox as
 * default.
 */
public class PdfUtilities {

    public static final String PDF_LIBRARY = "pdf.library";
    public static final String PDFBOX = "pdfbox";

    /**
     * Converts PDF to TIFF format.
     *
     * @param inputPdfFile input file
     * @return a multi-page TIFF image
     * @throws IOException
     */
    public static File convertPdf2Tiff(File inputPdfFile) throws IOException {
        if (PDFBOX.equals(System.getProperty(PDF_LIBRARY))) {
            return PdfBoxUtilities.convertPdf2Tiff(inputPdfFile);
        } else {
            try {
                return PdfGsUtilities.convertPdf2Tiff(inputPdfFile);
            } catch (Exception e) {
                System.setProperty(PDF_LIBRARY, PDFBOX);
                return convertPdf2Tiff(inputPdfFile);
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
        if (PDFBOX.equals(System.getProperty(PDF_LIBRARY))) {
            return PdfBoxUtilities.convertPdf2Png(inputPdfFile);
        } else {
            try {
                return PdfGsUtilities.convertPdf2Png(inputPdfFile);
            } catch (Exception e) {
                System.setProperty(PDF_LIBRARY, PDFBOX);
                return convertPdf2Png(inputPdfFile);
            }
        }
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
        if (PDFBOX.equals(System.getProperty(PDF_LIBRARY))) {
            PdfBoxUtilities.splitPdf(inputPdfFile, outputPdfFile, firstPage, lastPage);
        } else {
            try {
                PdfGsUtilities.splitPdf(inputPdfFile, outputPdfFile, firstPage, lastPage);
            } catch (Exception e) {
                System.setProperty(PDF_LIBRARY, PDFBOX);
                splitPdf(inputPdfFile, outputPdfFile, firstPage, lastPage);
            }
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
        if (PDFBOX.equals(System.getProperty(PDF_LIBRARY))) {
            return PdfBoxUtilities.getPdfPageCount(inputPdfFile);
        } else {
            try {
                return PdfGsUtilities.getPdfPageCount(inputPdfFile);
            } catch (Exception e) {
                System.setProperty(PDF_LIBRARY, PDFBOX);
                return getPdfPageCount(inputPdfFile);
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
        if (PDFBOX.equals(System.getProperty(PDF_LIBRARY))) {
            PdfBoxUtilities.mergePdf(inputPdfFiles, outputPdfFile);
        } else {
            try {
                PdfGsUtilities.mergePdf(inputPdfFiles, outputPdfFile);
            } catch (Exception e) {
                System.setProperty(PDF_LIBRARY, PDFBOX);
                mergePdf(inputPdfFiles, outputPdfFile);
            }
        }
    }
}
