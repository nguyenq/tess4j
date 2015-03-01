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
package net.sourceforge.vietocr;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class PdfUtilitiesTest {

    private final String testResourcesDataPath;

    public PdfUtilitiesTest() {
        testResourcesDataPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "test-data").getPath();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of convertPdf2Tiff method, of class PdfUtilities.
     */
    @Test
    public void testConvertPdf2Tiff() throws Exception {
        System.out.println("convertPdf2Tiff");
        File inputPdfFile = new File(this.testResourcesDataPath, "eurotext.pdf");
        File result = PdfUtilities.convertPdf2Tiff(inputPdfFile);
        result.deleteOnExit();
        assertTrue(result.exists());
    }

    /**
     * Test of convertPdf2Png method, of class PdfUtilities.
     */
    @Test
    public void testConvertPdf2Png() {
        System.out.println("convertPdf2Png");
        File inputPdfFile = new File(this.testResourcesDataPath, "eurotext.pdf");
        File[] results = PdfUtilities.convertPdf2Png(inputPdfFile);
        for (File result : results) {
            result.deleteOnExit();
        }
        assertTrue(results.length > 0);
    }

    /**
     * Test of splitPdf method, of class PdfUtilities.
     */
    @Ignore
    @Test
    public void testSplitPdf() {
        System.out.println("splitPdf");
        String inputPdfFile = "";
        String outputPdfFile = "";
        String firstPage = "";
        String lastPage = "";
        PdfUtilities.splitPdf(inputPdfFile, outputPdfFile, firstPage, lastPage);
    }

    /**
     * Test of getPdfPageCount method, of class PdfUtilities.
     */
    @Test
    public void testGetPdfPageCount() {
        System.out.println("getPdfPageCount");
        File inputPdfFile = new File(this.testResourcesDataPath, "eurotext.pdf");
        int expResult = 1;
        int result = PdfUtilities.getPdfPageCount(inputPdfFile.getPath());
        assertEquals(expResult, result);
    }

    /**
     * Test of mergePdf method, of class PdfUtilities.
     */
    @Ignore
    @Test
    public void testMergePdf() {
        System.out.println("mergePdf");
        File[] inputPdfFiles = null;
        File outputPdfFile = null;
        PdfUtilities.mergePdf(inputPdfFiles, outputPdfFile);
    }

}
