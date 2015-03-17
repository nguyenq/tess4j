/**
 * Copyright @ 2010 Quan Nguyen
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
package net.sourceforge.tess4j;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import com.sun.jna.Pointer;

import net.sourceforge.tess4j.util.Utils;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.ITesseract.RenderedFormat;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;

import com.recognition.software.jdeskew.ImageDeskew;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Tesseract1Test {

    static final double MINIMUM_DESKEW_THRESHOLD = 0.05d;
    Tesseract1 instance;

    private final String datapath;
    private final String testResourcesDataPath;

    public Tesseract1Test() {
        datapath = new File(Tesseract.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getPath();
        testResourcesDataPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "test-data").getPath();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        instance = new Tesseract1();
        instance.setDatapath(new File(datapath).getPath());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_File() throws Exception {
        System.out.println("doOCR on a PNG image");
        File imageFile = new File(this.testResourcesDataPath, "eurotext.png");
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageFile);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_UNLV_Zone_File() throws Exception {
        System.out.println("doOCR on a PNG image with UNLV zone file .uzn");
        //UNLV zone format: left top width height label
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext_unlv.png");
        File imageFile = new File(filename);
        String expResult = "& duck/goose, as 12.5% of E-mail\n\n"
                + "from aspammer@website.com is spam.\n\n"
                + "The (quick) [brown] {fox} jumps!\n"
                + "Over the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageFile);
        System.out.println(result);
        assertEquals(expResult, result.trim());
    }

    /**
     * Test of doOCR method, of class Tesseract.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_File_With_Configs() throws Exception {
        System.out.println("doOCR with configs");
        File imageFile = new File(this.testResourcesDataPath, "eurotext.png");
        String expResult = "[-0123456789.\n ]+";
        List<String> configs = Arrays.asList("digits");
        instance.setConfigs(configs);
        String result = instance.doOCR(imageFile);
        System.out.println(result);
        assertTrue(result.matches(expResult));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_File_Rectangle() throws Exception {
        System.out.println("doOCR on a BMP image with bounding rectangle");
        File imageFile = new File(this.testResourcesDataPath, "eurotext.bmp");
        Rectangle rect = new Rectangle(0, 0, 1024, 800); // define an equal or smaller region of interest on the image
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageFile, rect);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_List_Rectangle() throws Exception {
        System.out.println("doOCR on a PDF document");
        File imageFile = new File(this.testResourcesDataPath, "eurotext.pdf");
        List<IIOImage> imageList = ImageIOHelper.getIIOImageList(imageFile);
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageList, null);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_BufferedImage() throws Exception {
        System.out.println("doOCR on a buffered image of a PNG");
        File imageFile = new File(this.testResourcesDataPath, "eurotext.png");
        BufferedImage bi = ImageIO.read(imageFile);
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(bi);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of deskew algorithm.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_SkewedImage() throws Exception {
        System.out.println("doOCR on a skewed PNG image");
        File imageFile = new File(this.testResourcesDataPath, "eurotext_deskew.png");
        BufferedImage bi = ImageIO.read(imageFile);
        ImageDeskew id = new ImageDeskew(bi);
        double imageSkewAngle = id.getSkewAngle(); // determine skew angle
        if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
            bi = ImageHelper.rotateImage(bi, -imageSkewAngle); // deskew image
        }

        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(bi);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of createDocuments method, of class Tesseract.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateDocuments() throws Exception {
        System.out.println("createDocuments for an image");
        File imageFile1 = new File(this.testResourcesDataPath, "eurotext.pdf");
        File imageFile2 = new File(this.testResourcesDataPath, "eurotext.png");
        String outputbase1 = "target/test-classes/test-results/docrenderer1-1";
        String outputbase2 = "target/test-classes/test-results/docrenderer1-2";
        List<RenderedFormat> formats = new ArrayList<RenderedFormat>(Arrays.asList(RenderedFormat.HOCR, RenderedFormat.PDF, RenderedFormat.TEXT));
        instance.createDocuments(new String[]{imageFile1.getPath(), imageFile2.getPath()}, new String[]{outputbase1, outputbase2}, formats);
        assertTrue(new File(outputbase1 + ".pdf").exists());
    }

    /**
     * Test of extending Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testExtendingTesseract1() throws Exception {
        System.out.println("Extends Tesseract1");
        File imageFile = new File(this.testResourcesDataPath, "eurotext.tif");

        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String[] expResults = expResult.split("\\s");

        Tess1Extension instance1 = new Tess1Extension();
        instance1.setDatapath(new File(datapath).getPath());
        int pageIteratorLevel = TessPageIteratorLevel.RIL_WORD;
        System.out.println("PageIteratorLevel: "
                + Utils.getConstantName(pageIteratorLevel, TessPageIteratorLevel.class));
        List<Word> result = instance1.getWords(imageFile, pageIteratorLevel);

        // print the complete result
        for (Word word : result) {
            System.out.println(word);
        }

        List<String> text = new ArrayList<String>();
        for (Word word : result.subList(0, expResults.length)) {
            text.add(word.getText());
        }

        assertArrayEquals(expResults, text.toArray());
    }

    class Tess1Extension extends Tesseract1 {

        public List<Word> getWords(File file, int pageIteratorLevel) {
            this.init();
            this.setTessVariables();

            List<Word> words = new ArrayList<Word>();
            try {
                BufferedImage bi = ImageIO.read(file);
                setImage(bi, null);

                TessBaseAPIRecognize(this.getHandle(), null);
                TessResultIterator ri = TessBaseAPIGetIterator(this.getHandle());
                TessPageIterator pi = TessResultIteratorGetPageIterator(ri);
                TessPageIteratorBegin(pi);

                do {
                    Pointer ptr = TessResultIteratorGetUTF8Text(ri, pageIteratorLevel);
                    String text = ptr.getString(0);
                    TessAPI1.TessDeleteText(ptr);
                    float confidence = TessResultIteratorConfidence(ri, pageIteratorLevel);
                    IntBuffer leftB = IntBuffer.allocate(1);
                    IntBuffer topB = IntBuffer.allocate(1);
                    IntBuffer rightB = IntBuffer.allocate(1);
                    IntBuffer bottomB = IntBuffer.allocate(1);
                    TessPageIteratorBoundingBox(pi, pageIteratorLevel, leftB, topB, rightB, bottomB);
                    int left = leftB.get();
                    int top = topB.get();
                    int right = rightB.get();
                    int bottom = bottomB.get();
                    Word word = new Word(text, confidence, new Rectangle(left, top, right - left, bottom - top));
                    words.add(word);
                } while (TessPageIteratorNext(pi, pageIteratorLevel) == TRUE);

                return words;
            } catch (Exception e) {
                return words;
            } finally {
                this.dispose();
            }
        }
    }
}
