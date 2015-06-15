/*
 * Copyright 2014 Quan Nguyen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.tess4j.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfUtilitiesTest {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
    private static final String TEST_RESOURCES_DATA_PATH = "src/test/resources/test-data/";
    private final String TEST_RESOURCES_RESULTS_PATH = "src/test/resources/test-results/";

    /**
     * Test of convertPdf2Tiff method, of class PdfUtilities.
     */
    @Test
    public void testConvertPdf2Tiff() throws Exception {
        logger.info("convertPdf2Tiff");
        File inputPdfFile = new File(this.TEST_RESOURCES_DATA_PATH, "eurotext.pdf");
        File result = PdfUtilities.convertPdf2Tiff(inputPdfFile);
        result.deleteOnExit();
        assertTrue(result.exists());
    }

    /**
     * Test of convertPdf2Png method, of class PdfUtilities.
     */
    @Test
    public void testConvertPdf2Png() {
        logger.info("convertPdf2Png");
        File inputPdfFile = new File(this.TEST_RESOURCES_DATA_PATH, "eurotext.pdf");
        File[] results = PdfUtilities.convertPdf2Png(inputPdfFile);
        for (File result : results) {
            result.deleteOnExit();
        }
        assertTrue(results.length > 0);
    }

    /**
     * Test of splitPdf method, of class PdfUtilities.
     */
    @Test
    public void testSplitPdf() {
        logger.info("splitPdf");
        PDDocument document = null;

        try {
            File inputPdfFile = new File(String.format("%s/%s", TEST_RESOURCES_DATA_PATH, "multipage-pdf.pdf"));
            File outputPdfFile = new File(String.format("%s/%s", TEST_RESOURCES_RESULTS_PATH, "multipage-pdf_splitted.pdf"));
            int startPage = 2;
            int endPage = 3;
            PdfUtilities.splitPdf(inputPdfFile, outputPdfFile, startPage, endPage);
            document = PDDocument.load(outputPdfFile);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        assertTrue(2 == document.getNumberOfPages());
    }

    /**
     * Test of getPdfPageCount method, of class PdfUtilities.
     */
    @Test
    public void testGetPdfPageCount() {
        logger.info("getPdfPageCount");
        File inputPdfFile = new File(this.TEST_RESOURCES_DATA_PATH, "eurotext.pdf");
        int expResult = 1;
        int result = PdfUtilities.getPdfPageCount(inputPdfFile.getPath());
        assertEquals(expResult, result);
    }

    /**
     * Test of mergePdf method, of class PdfUtilities.
     */
    @Test
    public void testMergePdf() {
        logger.info("mergePdf");
        File pdfPartOne = new File(String.format("%s/%s", TEST_RESOURCES_DATA_PATH, "eurotext.pdf"));
        File pdfPartTwo = new File(String.format("%s/%s", TEST_RESOURCES_DATA_PATH, "multipage-pdf.pdf"));
        File outputPdfFile = new File(String.format("%s/%s", TEST_RESOURCES_RESULTS_PATH, "multipage-pdf_merged.pdf"));
        File[] inputPdfFiles = {pdfPartOne, pdfPartTwo};
        PdfUtilities.mergePdf(inputPdfFiles, outputPdfFile);
        assertTrue(6 == PdfUtilities.getPdfPageCount(outputPdfFile.getPath()));
    }

}
