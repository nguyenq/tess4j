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

import net.sourceforge.vietocr.ImageHelper;
import net.sourceforge.vietocr.ImageIOHelper;
import com.recognition.software.jdeskew.ImageDeskew;
import com.sun.jna.Pointer;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.IIOImage;
import static net.sourceforge.tess4j.ITessAPI.TRUE;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class Tesseract1Test {

    static final double MINIMUM_DESKEW_THRESHOLD = 0.05d;
    Tesseract1 instance;

    private final String datapath = "src/main/resources";
    private final String testResourcesDataPath = "src/test/resources/test-data";

    public Tesseract1Test() {
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
        instance.setDatapath(datapath);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_File() throws Exception {
        System.out.println("doOCR on a PNG image");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.png");
        File imageFile = new File(filename);
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageFile);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract.
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_File_With_Configs() throws Exception {
        System.out.println("doOCR with \"digits\" configs");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.png");
        File imageFile = new File(filename);
        String expResult = "[-0123456789.\n ]+";
        List<String> configs = Arrays.asList("digits");
        instance.setConfigs(configs);
        String result = instance.doOCR(imageFile);
        System.out.println(result);
        assertTrue(result.matches(expResult));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_File_Rectangle() throws Exception {
        System.out.println("doOCR on a BMP image with bounding rectangle");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.bmp");
        File imageFile = new File(filename);
        Rectangle rect = new Rectangle(0, 0, 1024, 800); // define an equal or smaller region of interest on the image
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageFile, rect);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_List_Rectangle() throws Exception {
        System.out.println("doOCR on a PDF document");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.pdf");
        File imageFile = new File(filename);
        List<IIOImage> imageList = ImageIOHelper.getIIOImageList(imageFile);
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageList, null);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_BufferedImage() throws Exception {
        System.out.println("doOCR on a buffered image of a GIF");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.gif");
        File imageFile = new File(filename);
        BufferedImage bi = ImageIO.read(imageFile);
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(bi);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of deskew algorithm.
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_SkewedImage() throws Exception {
        System.out.println("doOCR on a skewed PNG image");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext_deskew.png");
        File imageFile = new File(filename);
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
     * Test of extending Tesseract1.
     * @throws java.lang.Exception
     */
    @Test
    public void testExtendingTesseract1() throws Exception {
        System.out.println("Extends Tesseract1");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        File imageFile = new File(filename);

        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String[] expResults = expResult.split("\\s");

        Tess1Extension instance1 = new Tess1Extension();
        instance1.setDatapath(this.datapath);
        List<Word> result = instance1.getWords(imageFile);

        //print the complete result
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

        public List<Word> getWords(File file) {
            this.init();
            this.setTessVariables();

            List<Word> words = new ArrayList<Word>();
            try {
                BufferedImage bi = ImageIO.read(file);
                setImage(bi, null);

                TessAPI1.TessBaseAPIRecognize(this.getHandle(), null);
                TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(this.getHandle());
                TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
                TessAPI1.TessPageIteratorBegin(pi);

                do {
                    Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, TessPageIteratorLevel.RIL_WORD);
                    String text = ptr.getString(0);
                    TessAPI1.TessDeleteText(ptr);
                    float confidence = TessAPI1.TessResultIteratorConfidence(ri, TessPageIteratorLevel.RIL_WORD);
                    IntBuffer leftB = IntBuffer.allocate(1);
                    IntBuffer topB = IntBuffer.allocate(1);
                    IntBuffer rightB = IntBuffer.allocate(1);
                    IntBuffer bottomB = IntBuffer.allocate(1);
                    TessAPI1.TessPageIteratorBoundingBox(pi, TessPageIteratorLevel.RIL_WORD, leftB, topB, rightB, bottomB);
                    int left = leftB.get();
                    int top = topB.get();
                    int right = rightB.get();
                    int bottom = bottomB.get();
                    Word word = new Word(text, confidence, new Rectangle(left, top, right - left, bottom - top));
                    words.add(word);
                } while (TessAPI1.TessPageIteratorNext(pi, TessPageIteratorLevel.RIL_WORD) == TRUE);

                return words;
            } catch (Exception e) {
                return words;
            } finally {
                this.dispose();
            }
        }
    }

    class Word {

        private final String text;
        private final float confidence;
        private final Rectangle rect;

        public Word(String text, float confidence, Rectangle rect) {
            this.text = text;
            this.confidence = confidence;
            this.rect = rect;
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         * @return the confidence
         */
        public float getConfidence() {
            return confidence;
        }

        /**
         * @return the bounding box
         */
        public Rectangle getRect() {
            return rect;
        }

        @Override
        public String toString() {
            return String.format("%s\t[Confidence: %f Bounding box: %d %d %d %d]", text, confidence, rect.x, rect.y, rect.width, rect.height);
        }
    }
}
