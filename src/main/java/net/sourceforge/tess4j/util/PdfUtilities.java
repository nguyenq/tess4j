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
 * PDF utilities based on PDFBox.
 */
public class PdfUtilities {

    /**
     * Converts PDF to TIFF format.
     *
     * @param inputPdfFile input file
     * @return a multi-page TIFF image
     * @throws IOException
     */
    public static File convertPdf2Tiff(File inputPdfFile) throws IOException {
        return PdfBoxUtilities.convertPdf2Tiff(inputPdfFile);
    }

    /**
     * Converts PDF to PNG format.
     *
     * @param inputPdfFile input file
     * @return an array of PNG images
     * @throws java.io.IOException
     */
    public static File[] convertPdf2Png(File inputPdfFile) throws IOException {
        return PdfBoxUtilities.convertPdf2Png(inputPdfFile);
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
        PdfBoxUtilities.splitPdf(inputPdfFile, outputPdfFile, firstPage, lastPage);
    }

    /**
     * Gets PDF Page Count.
     *
     * @param inputPdfFile input file
     * @return number of pages
     */
    public static int getPdfPageCount(File inputPdfFile) {
        return PdfBoxUtilities.getPdfPageCount(inputPdfFile);
    }

    /**
     * Merges PDF files.
     *
     * @param inputPdfFiles array of input files
     * @param outputPdfFile output file
     */
    public static void mergePdf(File[] inputPdfFiles, File outputPdfFile) {
        PdfBoxUtilities.mergePdf(inputPdfFiles, outputPdfFile);
    }
}
