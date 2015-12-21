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

import static org.junit.Assert.assertArrayEquals;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.util.LoggHelper;
import net.sourceforge.tess4j.util.Utils;
import net.sourceforge.tess4j.util.ImageIOHelper;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.NativeLong;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.PointerByReference;
import net.sourceforge.lept4j.Box;
import net.sourceforge.lept4j.Boxa;
import static net.sourceforge.lept4j.ILeptonica.L_CLONE;
import net.sourceforge.lept4j.Leptonica;
import net.sourceforge.lept4j.Leptonica1;
import net.sourceforge.lept4j.Pix;

import net.sourceforge.tess4j.ITessAPI.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.sourceforge.tess4j.ITessAPI.FALSE;
import static net.sourceforge.tess4j.ITessAPI.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TessAPI1Test {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
    private final String datapath = "src/main/resources";
    private final String testResourcesDataPath = "src/test/resources/test-data";
    String language = "eng";
    String expOCRResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";

    TessBaseAPI handle;

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
     *
     * @throws Exception while processing the image
     */
    @Test
    public void testTessBaseAPIRect() throws Exception {
        logger.info("TessBaseAPIRect");
        String expResult = expOCRResult;
        File tiff = new File(this.testResourcesDataPath, "eurotext.tif");
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
        logger.info(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of TessBaseAPIGetUTF8Text method, of class TessDllAPI1.
     *
     * @throws Exception while processing the image
     */
    @Test
    public void testTessBaseAPIGetUTF8Text() throws Exception {
        logger.info("TessBaseAPIGetUTF8Text");
        String expResult = expOCRResult;
        File tiff = new File(this.testResourcesDataPath, "eurotext.tif");
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
        logger.info(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of TessBaseAPIGetUTF8Text method, of class TessAPI1.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testTessBaseAPIGetUTF8Text_Pix() throws Exception {
        logger.info("TessBaseAPIGetUTF8Text_Pix");
        String expResult = expOCRResult;
        File tiff = new File(this.testResourcesDataPath, "eurotext.tif");
        Leptonica leptInstance = Leptonica.INSTANCE;
        Pix pix = leptInstance.pixRead(tiff.getPath());
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetImage2(handle, pix);
        Pointer utf8Text = TessAPI1.TessBaseAPIGetUTF8Text(handle);
        String result = utf8Text.getString(0);
        TessAPI1.TessDeleteText(utf8Text);
        logger.info(result);

        //release Pix resource
        PointerByReference pRef = new PointerByReference();
        pRef.setValue(pix.getPointer());
        leptInstance.pixDestroy(pRef);

        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of TessBaseAPIGetComponentImages method, of class TessAPI1.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testTessBaseAPIGetComponentImages() throws Exception {
        logger.info("TessBaseAPIGetComponentImages");
        File image = new File(this.testResourcesDataPath, "eurotext.png");
        int expResult = 12; // number of lines in the test image
        Pix pix = Leptonica1.pixRead(image.getPath());
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetImage2(handle, pix);
        PointerByReference pixa = null;
        PointerByReference blockids = null;
        Boxa boxes = TessAPI1.TessBaseAPIGetComponentImages(handle, TessPageIteratorLevel.RIL_TEXTLINE, TRUE, pixa, blockids);
//        boxes = TessAPI1.TessBaseAPIGetRegions(handle, pixa); // equivalent to TessPageIteratorLevel.RIL_BLOCK
        int boxCount = Leptonica1.boxaGetCount(boxes);
        for (int i = 0; i < boxCount; i++) {
            Box box = Leptonica1.boxaGetBox(boxes, i, L_CLONE);
            if (box == null) {
                continue;
            }
            TessAPI1.TessBaseAPISetRectangle(handle, box.x, box.y, box.w, box.h);
            Pointer utf8Text = TessAPI1.TessBaseAPIGetUTF8Text(handle);
            String ocrResult = utf8Text.getString(0);
            TessAPI1.TessDeleteText(utf8Text);
            int conf = TessAPI1.TessBaseAPIMeanTextConf(handle);
            System.out.print(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d, confidence: %d, text: %s", i, box.x, box.y, box.w, box.h, conf, ocrResult));
        }

        //release Pix resource
        PointerByReference pRef = new PointerByReference();
        pRef.setValue(pix.getPointer());
        Leptonica1.pixDestroy(pRef);

        assertEquals(expResult, boxCount);
    }

    /**
     * Test of TessVersion method, of class TessAPI1.
     */
    @Test
    public void testTessVersion() {
        logger.info("TessVersion");
        String expResult = "3.04";
        String result = TessAPI1.TessVersion();
        logger.info(result);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPISetVariable method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPISetVariable() {
        logger.info("TessBaseAPISetVariable");
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
        logger.info("TessBaseAPIGetBoolVariable");
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
     *
     * @throws Exception while persisting variables into a file.
     */
    @Test
    public void testTessBaseAPIPrintVariablesToFile() throws Exception {
        logger.info("TessBaseAPIPrintVariablesToFile");
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
    public void testTessBaseAPIInit1() throws Exception {
        logger.info("TessBaseAPIInit1");
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        String[] args = {"hocr"};
        StringArray sarray = new StringArray(args);
        PointerByReference configs = new PointerByReference();
        configs.setPointer(sarray);
        int configs_size = args.length;
        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit1(handle, datapath, language, oem, configs, configs_size);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit2 method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIInit2() {
        logger.info("TessBaseAPIInit2");
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
        logger.info("TessBaseAPIInit3");
        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit4 method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIInit4() {
        logger.info("TessBaseAPIInit4");
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        PointerByReference configs = null;
        int configs_size = 0;
        // disable loading dictionaries
        String[] args = new String[]{"load_system_dawg", "load_freq_dawg"};
        StringArray sarray = new StringArray(args);
        PointerByReference vars_vec = new PointerByReference();
        vars_vec.setPointer(sarray);

        args = new String[]{"F", "F"};
        sarray = new StringArray(args);
        PointerByReference vars_values = new PointerByReference();
        vars_values.setPointer(sarray);

        NativeSize vars_vec_size = new NativeSize(args.length);

        int expResult = 0;
        int result = TessAPI1.TessBaseAPIInit4(handle, datapath, language, oem, configs, configs_size, vars_vec, vars_values, vars_vec_size, FALSE);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetInitLanguagesAsString method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetInitLanguagesAsString() {
        logger.info("TessBaseAPIGetInitLanguagesAsString");
        String expResult = "";
        String result = TessAPI1.TessBaseAPIGetInitLanguagesAsString(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetLoadedLanguagesAsVector method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetLoadedLanguagesAsVector() {
        logger.info("TessBaseAPIGetLoadedLanguagesAsVector");
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        String[] expResult = {"eng"};
        String[] result = TessAPI1.TessBaseAPIGetLoadedLanguagesAsVector(handle).getPointer().getStringArray(0);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetAvailableLanguagesAsVector method, of class
     * TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetAvailableLanguagesAsVector() {
        logger.info("TessBaseAPIGetAvailableLanguagesAsVector");
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        String[] expResult = {"eng"};
        String[] result = TessAPI1.TessBaseAPIGetAvailableLanguagesAsVector(handle).getPointer().getStringArray(0);
        assertTrue(Arrays.asList(result).containsAll(Arrays.asList(expResult)));
    }

    /**
     * Test of TessBaseAPIGetPageSegMode method, of class TessAPI1.
     */
    @Test
    public void testTessBaseAPIGetPageSegMode() {
        logger.info("TessBaseAPIGetPageSegMode");
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_SINGLE_CHAR);
        int expResult = TessPageSegMode.PSM_SINGLE_CHAR;
        int result = TessAPI1.TessBaseAPIGetPageSegMode(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetHOCRText method, of class TessAPI1.
     *
     * @throws Exception while getting ocr text from image.
     */
    @Test
    public void testTessBaseAPIGetHOCRText() throws Exception {
        logger.info("TessBaseAPIGetHOCRText");
        File tiff = new File(this.testResourcesDataPath, "eurotext.tif");
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
     *
     * @throws Exception while processing the image.
     */
    @Test
    public void testOSD() throws Exception {
        logger.info("OSD");
        int expResult = TessPageSegMode.PSM_AUTO_OSD;
        IntBuffer orientation = IntBuffer.allocate(1);
        IntBuffer direction = IntBuffer.allocate(1);
        IntBuffer order = IntBuffer.allocate(1);
        FloatBuffer deskew_angle = FloatBuffer.allocate(1);
        File tiff = new File(this.testResourcesDataPath, "eurotext.tif");
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, expResult);
        int actualResult = TessAPI1.TessBaseAPIGetPageSegMode(handle);
        logger.info("PSM: " + Utils.getConstantName(actualResult, TessPageSegMode.class));
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        int success = TessAPI1.TessBaseAPIRecognize(handle, null);
        if (success == 0) {
            TessAPI1.TessPageIterator pi = TessAPI1.TessBaseAPIAnalyseLayout(handle);
            TessAPI1.TessPageIteratorOrientation(pi, orientation, direction, order, deskew_angle);
            logger.info(String.format(
                    "Orientation: %s\nWritingDirection: %s\nTextlineOrder: %s\nDeskew angle: %.4f\n",
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
        logger.info("TessBaseAPIGetIterator");
        File tiff = new File(this.testResourcesDataPath, "eurotext.tif");
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        ETEXT_DESC monitor = new ETEXT_DESC();
        ITessAPI.TimeVal timeout = new ITessAPI.TimeVal();
        timeout.tv_sec = new NativeLong(0L); // time > 0 causes blank ouput
        monitor.end_time = timeout;
        ProgressMonitor pmo = new ProgressMonitor(monitor);
        pmo.start();
        TessAPI1.TessBaseAPIRecognize(handle, monitor);
        logger.info("Message: " + pmo.getMessage());
        TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(handle);
        TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
        TessAPI1.TessPageIteratorBegin(pi);
        logger.info("Bounding boxes:\nchar(s) left top right bottom confidence font-attributes");
        int level = TessPageIteratorLevel.RIL_WORD;

        // int height = image.getHeight();
        do {
            Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, level);
            String word = ptr.getString(0);
            TessAPI1.TessDeleteText(ptr);
            float confidence = TessAPI1.TessResultIteratorConfidence(ri, level);
            IntBuffer leftB = IntBuffer.allocate(1);
            IntBuffer topB = IntBuffer.allocate(1);
            IntBuffer rightB = IntBuffer.allocate(1);
            IntBuffer bottomB = IntBuffer.allocate(1);
            TessAPI1.TessPageIteratorBoundingBox(pi, level, leftB, topB, rightB, bottomB);
            int left = leftB.get();
            int top = topB.get();
            int right = rightB.get();
            int bottom = bottomB.get();
            System.out.print(String.format("%s %d %d %d %d %f", word, left, top, right, bottom, confidence));
            // logger.info(String.format("%s %d %d %d %d", str, left, height - bottom, right, height - top)); //
            // training box coordinates

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
            logger.info(String.format("  font: %s, size: %d, font id: %d, bold: %b,"
                    + " italic: %b, underlined: %b, monospace: %b, serif: %b, smallcap: %b", fontName, pointSize,
                    fontId, bold, italic, underlined, monospace, serif, smallcaps));
        } while (TessAPI1.TessPageIteratorNext(pi, level) == TRUE);

        assertTrue(true);
    }

    /**
     * Test of ChoiceIterator.
     *
     * @throws Exception
     */
    @Test
    public void testChoiceIterator() throws Exception {
        logger.info("TessResultIteratorGetChoiceIterator");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        File tiff = new File(filename);
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        TessAPI1.TessBaseAPIInit3(handle, datapath, language);
        TessAPI1.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        TessAPI1.TessBaseAPISetVariable(handle, "save_blob_choices", "T");
        TessAPI1.TessBaseAPISetRectangle(handle, 37, 228, 548, 31);
        ETEXT_DESC monitor = new ETEXT_DESC();
        ProgressMonitor pmo = new ProgressMonitor(monitor);
        pmo.start();
        TessAPI1.TessBaseAPIRecognize(handle, monitor);
        logger.info("Message: " + pmo.getMessage());
        TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(handle);
        int level = TessPageIteratorLevel.RIL_SYMBOL;

        if (ri != null) {
            do {
                Pointer symbol = TessAPI1.TessResultIteratorGetUTF8Text(ri, level);
                float conf = TessAPI1.TessResultIteratorConfidence(ri, level);
                if (symbol != null) {
                    logger.info(String.format("symbol %s, conf: %f", symbol.getString(0), conf));
                    boolean indent = false;
                    TessChoiceIterator ci = TessAPI1.TessResultIteratorGetChoiceIterator(ri);
                    do {
                        if (indent) {
                            System.out.print("\t");
                        }
                        System.out.print("\t- ");
                        String choice = TessAPI1.TessChoiceIteratorGetUTF8Text(ci);
                        logger.info(String.format("%s conf: %f", choice, TessAPI1.TessChoiceIteratorConfidence(ci)));
                        indent = true;
                    } while (TessAPI1.TessChoiceIteratorNext(ci) == TessAPI1.TRUE);
                    TessAPI1.TessChoiceIteratorDelete(ci);
                }
                logger.info("---------------------------------------------");
                TessAPI1.TessDeleteText(symbol);
            } while (TessAPI1.TessResultIteratorNext(ri, level) == TessAPI1.TRUE);
        }

        assertTrue(true);
    }

    /**
     * Test of ResultRenderer method, of class TessAPI1.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testResultRenderer() throws Exception {
        logger.info("TessResultRenderer");
        String image = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        String output = "capi-test.txt";
        int set_only_init_params = TessAPI.FALSE;
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        PointerByReference configs = null;
        int configs_size = 0;

        String[] params = {"load_system_dawg", "tessedit_char_whitelist"};
        String vals[] = {"F", ""}; //0123456789-.IThisalotfpnex
        PointerByReference vars_vec = new PointerByReference();
        vars_vec.setPointer(new StringArray(params));
        PointerByReference vars_values = new PointerByReference();
        vars_values.setPointer(new StringArray(vals));
        NativeSize vars_vec_size = new NativeSize(params.length);

        TessAPI1.TessBaseAPISetOutputName(handle, output);

        int rc = TessAPI1.TessBaseAPIInit4(handle, datapath, language,
                oem, configs, configs_size, vars_vec, vars_values, vars_vec_size, set_only_init_params);

        if (rc != 0) {
            TessAPI1.TessBaseAPIDelete(handle);
            logger.error("Could not initialize tesseract.");
            return;
        }

        String outputbase = "target/test-classes/test-results/outputbase1";
        TessResultRenderer renderer = TessAPI1.TessHOcrRendererCreate(outputbase);
        TessAPI1.TessResultRendererInsert(renderer, TessAPI1.TessBoxTextRendererCreate(outputbase));
        TessAPI1.TessResultRendererInsert(renderer, TessAPI1.TessTextRendererCreate(outputbase));
        String dataPath = TessAPI1.TessBaseAPIGetDatapath(handle);
        TessAPI1.TessResultRendererInsert(renderer, TessAPI1.TessPDFRendererCreate(outputbase, dataPath));
        int result = TessAPI1.TessBaseAPIProcessPages(handle, image, null, 0, renderer);

//        if (result == FALSE) {
//            logger.error("Error during processing.");
//            return;
//        }
        for (; renderer != null; renderer = TessAPI1.TessResultRendererNext(renderer)) {
            String ext = TessAPI1.TessResultRendererExtention(renderer).getString(0);
            logger.info(String.format("TessResultRendererExtention: %s\nTessResultRendererTitle: %s\nTessResultRendererImageNum: %d",
                    ext,
                    TessAPI1.TessResultRendererTitle(renderer).getString(0),
                    TessAPI1.TessResultRendererImageNum(renderer)));
        }

        TessAPI1.TessDeleteResultRenderer(renderer);
        assertTrue(new File(outputbase + ".pdf").exists());
    }
}
