/*
 * Copyright @ 2008 Quan Nguyen
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

import com.recognition.software.jdeskew.ImageDeskew;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageIOHelperTest {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
    private static final String TEST_RESOURCES_DATA_PATH = "src/test/resources/test-data/";
    private static final String TEST_RESOURCES_RESULTS_PATH = "src/test/resources/test-results/";
    private static final double MINIMUM_DESKEW_THRESHOLD = 0.05d;

    public ImageIOHelperTest() {
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
     * Test of createTiffFiles method, of class ImageIOHelper.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateTiffFiles_File_int() throws Exception {
        logger.info("createTiffFiles");
        File imageFile = new File(TEST_RESOURCES_DATA_PATH, "eurotext.png");
        int index = 0;
        int expResult = 1;
        List<File> result = ImageIOHelper.createTiffFiles(imageFile, index);
        assertEquals(expResult, result.size());
        
        // cleanup
        for (File f : result) {
            f.delete();
        }
    }

    /**
     * Test of getImageFileFormat method, of class ImageIOHelper.
     */
    @Test
    public void testGetImageFileFormat() {
        logger.info("getImageFileFormat");
        File imageFile = new File(TEST_RESOURCES_DATA_PATH, "eurotext.png");
        String expResult = "png";
        String result = ImageIOHelper.getImageFileFormat(imageFile);
        assertEquals(expResult, result);
    }

    /**
     * Test of getImageFile method, of class ImageIOHelper.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetImageFile() throws Exception {
        logger.info("getImageFile");
        File inputFile = new File(TEST_RESOURCES_DATA_PATH, "eurotext.png");
        File expResult = new File(TEST_RESOURCES_DATA_PATH, "eurotext.png");
        File result = ImageIOHelper.getImageFile(inputFile);
        assertEquals(expResult, result);
    }

    /**
     * Test of getImageList method, of class ImageIOHelper.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetImageList() throws Exception {
        logger.info("getImageList");
        File imageFile = new File(TEST_RESOURCES_DATA_PATH, "eurotext.pdf");
        int expResult = 1;
        List<BufferedImage> result = ImageIOHelper.getImageList(imageFile);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getIIOImageList method, of class ImageIOHelper.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetIIOImageList_File() throws Exception {
        logger.info("getIIOImageList");
        File imageFile = new File(TEST_RESOURCES_DATA_PATH, "eurotext.pdf");
        int expResult = 1;
        List<IIOImage> result = ImageIOHelper.getIIOImageList(imageFile);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of getIIOImageList method, of class ImageIOHelper.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetIIOImageList_BufferedImage() throws Exception {
        logger.info("getIIOImageList");
        File imageFile = new File(TEST_RESOURCES_DATA_PATH, "eurotext.png");
        BufferedImage bi = ImageIO.read(imageFile);
        int expResult = 1;
        List<IIOImage> result = ImageIOHelper.getIIOImageList(bi);
        assertEquals(expResult, result.size());
    }

    /**
     * Test of mergeTiff method, of class ImageIOHelper.
     * @throws java.lang.Exception
     */
    @Test
    public void testMergeTiff_FileArr_File() throws Exception {
        logger.info("mergeTiff");
        File imageFile1 = new File(TEST_RESOURCES_DATA_PATH, "eurotext.png"); // filesize: 14,854 bytes
        File imageFile2 = new File(TEST_RESOURCES_DATA_PATH, "eurotext_deskew.png"); // filesize: 204,383 bytes
        File[] inputImages = {imageFile1, imageFile2};
        File outputTiff = new File(TEST_RESOURCES_RESULTS_PATH, "mergedTiff.tif");
        long expResult = 224337L; // filesize: 224,337 bytes
        ImageIOHelper.mergeTiff(inputImages, outputTiff);
        assertEquals(expResult, outputTiff.length());
    }

    /**
     * Test of deskewImage method, of class ImageIOHelper.
     * @throws java.lang.Exception
     */
    @Test
    public void testDeskewImage() throws Exception {
        logger.info("deskewImage");
        File imageFile = new File(TEST_RESOURCES_DATA_PATH, "eurotext_deskew.png");
        double minimumDeskewThreshold = MINIMUM_DESKEW_THRESHOLD;
        double initAngle = new ImageDeskew(ImageIO.read(imageFile)).getSkewAngle();
        File result = ImageIOHelper.deskewImage(imageFile, minimumDeskewThreshold);
        double resultAngle = new ImageDeskew(ImageIO.read(result)).getSkewAngle();
        assertTrue(Math.abs(resultAngle) < Math.abs(initAngle));
        // cleanup
        result.delete();
    }

    /**
     * Test of readImageData method, of class ImageIOHelper.
     * @throws java.io.IOException
     */
    @Test
    public void testReadImageData() throws IOException {
        logger.info("readImageData");
        File imageFile = new File(TEST_RESOURCES_DATA_PATH, "eurotext.png");
        List<IIOImage> oimages = ImageIOHelper.getIIOImageList(imageFile);
        IIOImage oimage = oimages.get(0);
        int expResultDpiX = 300;
        int expResultDpiY = 300;
        Map<String, String> result = ImageIOHelper.readImageData(oimage);
        assertEquals(String.valueOf(expResultDpiX), result.get("dpiX"));
        assertEquals(String.valueOf(expResultDpiY), result.get("dpiY"));
    }

}
