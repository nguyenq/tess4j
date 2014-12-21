/**
 * Copyright @ 2012 Quan Nguyen
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

import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.PointerByReference;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.*;
import java.util.Arrays;
import javax.imageio.ImageIO;

import static net.sourceforge.tess4j.ITessAPI.TRUE;
import net.sourceforge.vietocr.ImageIOHelper;
import net.sourceforge.tess4j.util.Utils;
import net.sourceforge.tess4j.ITessAPI.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TessAPI1Test {

    private final String datapath = "src/main/resources";
    private final String testResourcesDataPath = "src/test/resources/test-data";
    String language = "eng";
    String expOCRResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";

    TessAPI1.TessBaseAPI handle;
    
    public TessAPI1Test() {
        System.setProperty("jna.encoding", "UTF8");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        handle = TessAPI1.TessBaseAPICreate();
    }

    @After
    public void tearDown() {
        TessAPI1.TessBaseAPIDelete(handle);
    }

    /**
     * Test of TessBaseAPIRect method, of class TessDllAPI1.
     */
    @Test
    public void testTessBaseAPIRect() throws Exception {
        System.out.println("TessBaseAPIRect");
        String expResult = expOCRResult;
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        File tiff = new File(filename);
        BufferedImage image = ImageIO.read(tiff); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        Pointer utf8Text = TessAPI1.TessBaseAPIRect(handle, buf, bytespp, bytespl, 0, 0, 1024, 800);
        String result = utf8Text.getString(0);
        TessAPI1.TessDeleteText(utf8Text);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of TessBaseAPIGetUTF8Text method, of class TessDllAPI1.
     */
    @Test
    public void testTessBaseAPIGetUTF8Text() throws Exception {
        System.out.println("TessBaseAPIGetUTF8Text");
        String expResult = expOCRResult;
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        File tiff = new File(filename);
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        TessAPI1.TessBaseAPISetRectangle(handle, 0, 0, 1024, 800);
        Pointer utf8Text = TessAPI1.TessBaseAPIGetUTF8Text(handle);
        String result = utf8Text.getString(0);
        TessAPI1.TessDeleteText(utf8Text);
        System.out.println(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of TessVersion method, of class TessAPI1.
     */
    @Test
    public void testTessVersion() {
        System.out.println("TessVersion");
        String expResult = "3.02";
        String result = TessAPI1.TessVersion();
        System.out.println(result);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPICreate method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPICreate() {
        System.out.println("TessBaseAPICreate");
        TessAPI1.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        assertNotNull(handle);
        TessAPI1.TessBaseAPIDelete(handle);
    }

    /**
     * Test of TessBaseAPIDelete method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIDelete() {
        System.out.println("TessBaseAPIDelete");
        TessAPI1.TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
        TessAPI1.TessBaseAPIDelete(handle);
    }

    /**
     * Test of TessBaseAPISetInputName method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetInputName() {
        System.out.println("TessBaseAPISetInputName");
        String name = "eurotext.tif";
        TessAPI1.TessBaseAPISetInputName(handle, name);
    }

    /**
     * Test of TessBaseAPISetOutputName method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetOutputName() {
        System.out.println("TessBaseAPISetOutputName");
        String name = "out";
        TessAPI1.TessBaseAPISetOutputName(handle, name);
    }

    /**
     * Test of TessBaseAPISetVariable method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetVariable() {
        System.out.println("TessBaseAPISetVariable");
        String name = "tessedit_create_hocr";
        String value = "1";
        int expResult = 1;
        int result = TessAPI1.TessBaseAPISetVariable(handle, name, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetBoolVariable method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetBoolVariable() {
        System.out.println("TessBaseAPIGetBoolVariable");
        String name = "tessedit_create_hocr";
        TessAPI1.TessBaseAPISetVariable(handle, name, "1");
        IntBuffer value = IntBuffer.allocate(1);
        int result = -1;
        if (TessAPI1.TessBaseAPIGetBoolVariable(handle, "tessedit_create_hocr", value) == TRUE) {
            result = value.get(0);
        }
        int expResult = 1;
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIPrintVariables method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIPrintVariablesToFile() throws Exception {
        System.out.println("TessBaseAPIPrintVariablesToFile");
        String var = "tessedit_char_whitelist";
        String value = "0123456789";
        TessAPI1.TessBaseAPISetVariable(handle, var, value);
        String filename = "printvar.txt";
        TessAPI1.TessBaseAPIPrintVariablesToFile(handle, filename); // will crash if not invoked after some method
        File file = new File(filename);
        BufferedReader input = new BufferedReader(new FileReader(file));
        StringBuilder strB = new StringBuilder();
        String line;
        String EOL = System.getProperty("line.separator");
        while ((line = input.readLine()) != null) {
            strB.append(line).append(EOL);
        }
        input.close();
        file.delete();
        assertTrue(strB.toString().contains(var + "\t" + value));
    }

    /**
     * Test of TessBaseAPIInit1 method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIInit1() {
        System.out.println("TessBaseAPIInit1");
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        String[] args = {"hocr"};
        StringArray sarray = new StringArray(args);
        PointerByReference configs = new PointerByReference();
        configs.setPointer(sarray);
        int configs_size = args.length;
        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit1(handle, datapath, language, oem, configs, configs_size);
        assertEquals(expResult, result);

        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        String retry_config = null;
        int timeout_millisec = 0;
        Pointer utf8Text = TessAPI1.TessBaseAPIProcessPages(handle, filename, retry_config, timeout_millisec);
        String html = utf8Text.getString(0);
        TessAPI1.TessDeleteText(utf8Text);
        assertTrue(html.contains("<html"));
    }

    /**
     * Test of TessBaseAPIInit2 method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIInit2() {
        System.out.println("TessBaseAPIInit2");
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit2(handle, datapath, language, oem);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit3 method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIInit3() {
        System.out.println("TessBaseAPIInit3");
        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetInitLanguagesAsString method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetInitLanguagesAsString() {
        System.out.println("TessBaseAPIGetInitLanguagesAsString");
        String expResult = "";
        String result = TessAPI1.TessBaseAPIGetInitLanguagesAsString(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetLoadedLanguagesAsVector method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetLoadedLanguagesAsVector() {
        System.out.println("TessBaseAPIGetLoadedLanguagesAsVector");
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        String[] expResult = { "eng" };
        String[] result = TessAPI1.TessBaseAPIGetLoadedLanguagesAsVector(handle).getPointer().getStringArray(0);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetAvailableLanguagesAsVector method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetAvailableLanguagesAsVector() {
        System.out.println("TessBaseAPIGetAvailableLanguagesAsVector");
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        String[] expResult = { "eng" };
        String[] result = TessAPI1.TessBaseAPIGetAvailableLanguagesAsVector(handle).getPointer().getStringArray(0);
        assertTrue(Arrays.asList(result).containsAll(Arrays.asList(expResult)));
    }

    /**
     * Test of TessBaseAPISetPageSegMode method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetPageSegMode() {
        System.out.println("TessBaseAPISetPageSegMode");
        int mode = TessPageSegMode.PSM_AUTO;
        TessAPI1.TessBaseAPISetPageSegMode(handle, mode);
    }

    /**
     * Test of TessBaseAPIGetPageSegMode method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetPageSegMode() {
        System.out.println("TessBaseAPIGetPageSegMode");
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_SINGLE_CHAR);
        int expResult = TessPageSegMode.PSM_SINGLE_CHAR;
        int result = TessAPI1.TessBaseAPIGetPageSegMode(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPISetImage method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetImage() {
        System.out.println("TessBaseAPISetImage");
        ByteBuffer imagedata = null;
        int width = 0;
        int height = 0;
        int bytes_per_pixel = 0;
        int bytes_per_line = 0;
        TessAPI1.TessBaseAPISetImage(handle, imagedata, width, height, bytes_per_pixel, bytes_per_line);
    }

    /**
     * Test of TessBaseAPISetRectangle method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetRectangle() {
        System.out.println("TessBaseAPISetRectangle");
        int left = 0;
        int top = 0;
        int width = 0;
        int height = 0;
        TessAPI1.TessBaseAPISetRectangle(handle, left, top, width, height);
    }

    /**
     * Test of TessBaseAPIProcessPages method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIProcessPages() {
        System.out.println("TessBaseAPIProcessPages");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        String retry_config = null;
        int timeout_millisec = 0;
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        String expResult = expOCRResult;
        Pointer utf8Text = TessAPI1.TessBaseAPIProcessPages(handle, filename, retry_config, timeout_millisec);
        String result = utf8Text.getString(0);
        TessAPI1.TessDeleteText(utf8Text);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPIGetHOCRText method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetHOCRText() throws Exception {
        System.out.println("TessBaseAPIGetHOCRText");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        File tiff = new File(filename);
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        int page_number = 0;
        Pointer utf8Text = TessAPI1.TessBaseAPIGetHOCRText(handle, page_number);
        String result = utf8Text.getString(0);
        TessAPI1.TessDeleteText(utf8Text);
        assertTrue(result.contains("<div class='ocr_page'"));
    }

    /**
     * Test of Orientation and script detection (OSD).
     */
    @Test
    public void testOSD() throws Exception {
        System.out.println("OSD");
        int expResult = TessPageSegMode.PSM_AUTO_OSD;
        IntBuffer orientation = IntBuffer.allocate(1);
        IntBuffer direction = IntBuffer.allocate(1);
        IntBuffer order = IntBuffer.allocate(1);
        FloatBuffer deskew_angle = FloatBuffer.allocate(1);
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        File tiff = new File(filename);
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, expResult);
        int actualResult = TessAPI1.TessBaseAPIGetPageSegMode(handle);
        System.out.println("PSM: " + Utils.getConstantName(actualResult, TessPageSegMode.class));
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        int success = TessAPI1.TessBaseAPIRecognize(handle, null);
        if (success == 0) {
            TessAPI1.TessPageIterator pi = TessAPI1.TessBaseAPIAnalyseLayout(handle);
            TessAPI1.TessPageIteratorOrientation(pi, orientation, direction, order, deskew_angle);
            System.out.println(String.format("Orientation: %s\nWritingDirection: %s\nTextlineOrder: %s\nDeskew angle: %.4f\n",
                Utils.getConstantName(orientation.get(), TessOrientation.class), 
                Utils.getConstantName(direction.get(), TessWritingDirection.class), 
                Utils.getConstantName(order.get(), TessTextlineOrder.class), 
                deskew_angle.get()));
        }
        
        assertEquals(expResult, actualResult);
    }

    /**
     * Test of ResultIterator and PageIterator.
     *
     * @throws Exception
     */
    @Test
    public void testResultIterator() throws Exception {
        System.out.println("TessBaseAPIGetIterator");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        File tiff = new File(filename);
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        TessAPI1.TessBaseAPIRecognize(handle, null);
        TessAPI1.TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(handle);
        TessAPI1.TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
        TessAPI1.TessPageIteratorBegin(pi);
        System.out.println("Bounding boxes:\nchar(s) left top right bottom confidence font-attributes");

//        int height = image.getHeight();
        do {
            Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, TessPageIteratorLevel.RIL_WORD);
            String word = ptr.getString(0);
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
            System.out.print(String.format("%s %d %d %d %d %f", word, left, top, right, bottom, confidence));
//            System.out.println(String.format("%s %d %d %d %d", str, left, height - bottom, right, height - top)); // training box coordinates     

            IntBuffer boldB = IntBuffer.allocate(1);
            IntBuffer italicB = IntBuffer.allocate(1);
            IntBuffer underlinedB = IntBuffer.allocate(1);
            IntBuffer monospaceB = IntBuffer.allocate(1);
            IntBuffer serifB = IntBuffer.allocate(1);
            IntBuffer smallcapsB = IntBuffer.allocate(1);
            IntBuffer pointSizeB = IntBuffer.allocate(1);
            IntBuffer fontIdB = IntBuffer.allocate(1);
            String fontName = TessAPI1.TessResultIteratorWordFontAttributes(ri, boldB, italicB, underlinedB,
                    monospaceB, serifB, smallcapsB, pointSizeB, fontIdB);
            boolean bold = boldB.get() == TRUE;
            boolean italic = italicB.get() == TRUE;
            boolean underlined = underlinedB.get() == TRUE;
            boolean monospace = monospaceB.get() == TRUE;
            boolean serif = serifB.get() == TRUE;
            boolean smallcaps = smallcapsB.get() == TRUE;
            int pointSize = pointSizeB.get();
            int fontId = fontIdB.get();
            System.out.println(String.format("  font: %s, size: %d, font id: %d, bold: %b," +
                       " italic: %b, underlined: %b, monospace: %b, serif: %b, smallcap: %b", 
                    fontName, pointSize, fontId, bold, italic, underlined, monospace, serif, smallcaps));            
        } while (TessAPI1.TessPageIteratorNext(pi, TessPageIteratorLevel.RIL_WORD) == TRUE);
        
        assertTrue(true);
    }

    /**
     * Test of TessBaseAPIClear method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIClear() {
        System.out.println("TessBaseAPIClear");
        TessAPI1.TessBaseAPIClear(handle);
    }

    /**
     * Test of TessBaseAPIEnd method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIEnd() {
        System.out.println("TessBaseAPIEnd");
        TessAPI1.TessBaseAPIEnd(handle);
    }
}
