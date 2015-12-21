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
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.LoggHelper;
import net.sourceforge.tess4j.util.Utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import net.sourceforge.lept4j.Box;
import net.sourceforge.lept4j.Boxa;
import static net.sourceforge.lept4j.ILeptonica.L_CLONE;
import net.sourceforge.lept4j.Leptonica;
import net.sourceforge.lept4j.Pix;

import net.sourceforge.tess4j.ITessAPI.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.sourceforge.tess4j.ITessAPI.FALSE;
import static net.sourceforge.tess4j.ITessAPI.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TessAPITest {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
    private final String datapath = "src/main/resources";
    private final String testResourcesDataPath = "src/test/resources/test-data";
    String language = "eng";
    String expOCRResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";

    TessAPI api;
    TessBaseAPI handle;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        api = new TessAPIImpl().getInstance();
        handle = api.TessBaseAPICreate();
    }

    @After
    public void tearDown() {
        api.TessBaseAPIDelete(handle);
    }

    /**
     * Test of TessBaseAPIRect method, of class TessDllLibrary.
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
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        Pointer utf8Text = api.TessBaseAPIRect(handle, buf, bytespp, bytespl, 90, 50, 862, 614);
        String result = utf8Text.getString(0);
        api.TessDeleteText(utf8Text);
        logger.info(result);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPIGetUTF8Text method, of class TessDllLibrary.
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
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        api.TessBaseAPISetRectangle(handle, 90, 50, 862, 614);
        Pointer utf8Text = api.TessBaseAPIGetUTF8Text(handle);
        String result = utf8Text.getString(0);
        api.TessDeleteText(utf8Text);
        logger.info(result);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPIGetUTF8Text method, of class TessAPI.
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
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetImage2(handle, pix);
        Pointer utf8Text = api.TessBaseAPIGetUTF8Text(handle);
        String result = utf8Text.getString(0);
        api.TessDeleteText(utf8Text);
        logger.info(result);

        //release Pix resource
        PointerByReference pRef = new PointerByReference();
        pRef.setValue(pix.getPointer());
        leptInstance.pixDestroy(pRef);

        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPIGetComponentImages method, of class TessAPI.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testTessBaseAPIGetComponentImages() throws Exception {
        logger.info("TessBaseAPIGetComponentImages");
        File image = new File(this.testResourcesDataPath, "eurotext.png");
        int expResult = 12; // number of lines in the test image
        Leptonica leptInstance = Leptonica.INSTANCE;
        Pix pix = leptInstance.pixRead(image.getPath());
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetImage2(handle, pix);
        PointerByReference pixa = null;
        PointerByReference blockids = null;
        Boxa boxes = api.TessBaseAPIGetComponentImages(handle, TessPageIteratorLevel.RIL_TEXTLINE, TRUE, pixa, blockids);
//        boxes = api.TessBaseAPIGetRegions(handle, pixa); // equivalent to TessPageIteratorLevel.RIL_BLOCK
        int boxCount = leptInstance.boxaGetCount(boxes);
        for (int i = 0; i < boxCount; i++) {
            Box box = leptInstance.boxaGetBox(boxes, i, L_CLONE);
            if (box == null) {
                continue;
            }
            api.TessBaseAPISetRectangle(handle, box.x, box.y, box.w, box.h);
            Pointer utf8Text = api.TessBaseAPIGetUTF8Text(handle);
            String ocrResult = utf8Text.getString(0);
            api.TessDeleteText(utf8Text);
            int conf = api.TessBaseAPIMeanTextConf(handle);
            System.out.print(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d, confidence: %d, text: %s", i, box.x, box.y, box.w, box.h, conf, ocrResult));
        }

        //release Pix resource
        PointerByReference pRef = new PointerByReference();
        pRef.setValue(pix.getPointer());
        leptInstance.pixDestroy(pRef);

        assertEquals(expResult, boxCount);
    }

    /**
     * Test of TessVersion method, of class TessAPI.
     */
    @Test
    public void testTessVersion() {
        logger.info("TessVersion");
        String expResult = "3.04";
        String result = api.TessVersion();
        logger.info(result);
        assertTrue(result.startsWith(expResult));
    }

    /**
     * Test of TessBaseAPISetVariable method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPISetVariable() {
        logger.info("TessBaseAPISetVariable");
        String name = "tessedit_create_hocr";
        String value = "1";
        int expResult = 1;
        int result = api.TessBaseAPISetVariable(handle, name, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetBoolVariable method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIGetBoolVariable() {
        logger.info("TessBaseAPIGetBoolVariable");
        String name = "tessedit_create_hocr";
        api.TessBaseAPISetVariable(handle, name, "1");
        IntBuffer value = IntBuffer.allocate(1);
        int result = -1;
        if (api.TessBaseAPIGetBoolVariable(handle, "tessedit_create_hocr", value) == TRUE) {
            result = value.get(0);
        }
        int expResult = 1;
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIPrintVariables method, of class TessAPI.
     *
     * @throws Exception while persisting variables to file
     */
    @Test
    public void testTessBaseAPIPrintVariablesToFile() throws Exception {
        logger.info("TessBaseAPIPrintVariablesToFile");
        String var = "tessedit_char_whitelist";
        String value = "0123456789";
        api.TessBaseAPISetVariable(handle, var, value);
        String filename = "printvar.txt";
        api.TessBaseAPIPrintVariablesToFile(handle, filename); // will crash if not invoked after some method
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
     * Test of TessBaseAPIInit1 method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIInit1() {
        logger.info("TessBaseAPIInit1");
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        String[] args = {"hocr"};
        StringArray sarray = new StringArray(args);
        PointerByReference configs = new PointerByReference();
        configs.setPointer(sarray);
        int configs_size = args.length;
        int expResult = 0;
        int result = api.TessBaseAPIInit1(handle, datapath, language, oem, configs, configs_size);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit2 method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIInit2() {
        logger.info("TessBaseAPIInit2");
        int oem = TessOcrEngineMode.OEM_DEFAULT;
        int expResult = 0;
        int result = api.TessBaseAPIInit2(handle, datapath, language, oem);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit3 method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIInit3() {
        logger.info("TessBaseAPIInit3");
        int expResult = 0;
        int result = api.TessBaseAPIInit3(handle, datapath, language);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIInit4 method, of class TessAPI.
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
        int result = api.TessBaseAPIInit4(handle, datapath, language, oem, configs, configs_size, vars_vec, vars_values, vars_vec_size, FALSE);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetInitLanguagesAsString method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIGetInitLanguagesAsString() {
        logger.info("TessBaseAPIGetInitLanguagesAsString");
        String expResult = "";
        String result = api.TessBaseAPIGetInitLanguagesAsString(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetLoadedLanguagesAsVector method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIGetLoadedLanguagesAsVector() {
        logger.info("TessBaseAPIGetLoadedLanguagesAsVector");
        api.TessBaseAPIInit3(handle, datapath, language);
        String[] expResult = {"eng"};
        String[] result = api.TessBaseAPIGetLoadedLanguagesAsVector(handle).getPointer().getStringArray(0);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetAvailableLanguagesAsVector method, of class
     * TessAPI.
     */
    @Test
    public void testTessBaseAPIGetAvailableLanguagesAsVector() {
        logger.info("TessBaseAPIGetAvailableLanguagesAsVector");
        api.TessBaseAPIInit3(handle, datapath, language);
        String[] expResult = {"eng"};
        String[] result = api.TessBaseAPIGetAvailableLanguagesAsVector(handle).getPointer().getStringArray(0);
        assertTrue(Arrays.asList(result).containsAll(Arrays.asList(expResult)));
    }

    /**
     * Test of TessBaseAPIGetPageSegMode method, of class TessAPI.
     */
    @Test
    public void testTessBaseAPIGetPageSegMode() {
        logger.info("TessBaseAPIGetPageSegMode");
        api.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_SINGLE_CHAR);
        int expResult = TessPageSegMode.PSM_SINGLE_CHAR;
        int result = api.TessBaseAPIGetPageSegMode(handle);
        assertEquals(expResult, result);
    }

    /**
     * Test of TessBaseAPIGetHOCRText method, of class TessAPI.
     *
     * @throws Exception while getting hocr text
     */
    @Test
    public void testTessBaseAPIGetHOCRText() throws Exception {
        logger.info("TessBaseAPIGetHOCRText");
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        File tiff = new File(filename);
        BufferedImage image = ImageIO.read(new FileInputStream(tiff)); // require jai-imageio lib to read TIFF
        ByteBuffer buf = ImageIOHelper.convertImageData(image);
        int bpp = image.getColorModel().getPixelSize();
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);
        api.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        int page_number = 0;
        Pointer utf8Text = api.TessBaseAPIGetHOCRText(handle, page_number);
        String result = utf8Text.getString(0);
        api.TessDeleteText(utf8Text);
        assertTrue(result.contains("<div class='ocr_page'"));
    }

    /**
     * Test of Orientation and script detection (OSD).
     *
     * @throws Exception while processing image
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
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO_OSD);
        int actualResult = api.TessBaseAPIGetPageSegMode(handle);
        logger.info("PSM: " + Utils.getConstantName(actualResult, TessPageSegMode.class));
        api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        int success = api.TessBaseAPIRecognize(handle, null);
        if (success == 0) {
            TessPageIterator pi = api.TessBaseAPIAnalyseLayout(handle);
            api.TessPageIteratorOrientation(pi, orientation, direction, order, deskew_angle);
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
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
        api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        ETEXT_DESC monitor = new ETEXT_DESC();
        TimeVal timeout = new TimeVal();
        timeout.tv_sec = new NativeLong(0L); // time > 0 causes blank ouput
        monitor.end_time = timeout;
        ProgressMonitor pmo = new ProgressMonitor(monitor);
        pmo.start();
        api.TessBaseAPIRecognize(handle, monitor);
        logger.info("Message: " + pmo.getMessage());
        TessResultIterator ri = api.TessBaseAPIGetIterator(handle);
        TessPageIterator pi = api.TessResultIteratorGetPageIterator(ri);
        api.TessPageIteratorBegin(pi);
        logger.info("Bounding boxes:\nchar(s) left top right bottom confidence font-attributes");
        int level = TessPageIteratorLevel.RIL_WORD;

        // int height = image.getHeight();
        do {
            Pointer ptr = api.TessResultIteratorGetUTF8Text(ri, level);
            String word = ptr.getString(0);
            api.TessDeleteText(ptr);
            float confidence = api.TessResultIteratorConfidence(ri, level);
            IntBuffer leftB = IntBuffer.allocate(1);
            IntBuffer topB = IntBuffer.allocate(1);
            IntBuffer rightB = IntBuffer.allocate(1);
            IntBuffer bottomB = IntBuffer.allocate(1);
            api.TessPageIteratorBoundingBox(pi, level, leftB, topB, rightB, bottomB);
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
            String fontName = api.TessResultIteratorWordFontAttributes(ri, boldB, italicB, underlinedB, monospaceB,
                    serifB, smallcapsB, pointSizeB, fontIdB);
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
        } while (api.TessPageIteratorNext(pi, level) == TRUE);

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
        api.TessBaseAPIInit3(handle, datapath, language);
        api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
        api.TessBaseAPISetVariable(handle, "save_blob_choices", "T");
        api.TessBaseAPISetRectangle(handle, 37, 228, 548, 31);
        ETEXT_DESC monitor = new ETEXT_DESC();
        ProgressMonitor pmo = new ProgressMonitor(monitor);
        pmo.start();
        api.TessBaseAPIRecognize(handle, monitor);
        logger.info("Message: " + pmo.getMessage());
        TessResultIterator ri = api.TessBaseAPIGetIterator(handle);
        int level = TessPageIteratorLevel.RIL_SYMBOL;

        if (ri != null) {
            do {
                Pointer symbol = api.TessResultIteratorGetUTF8Text(ri, level);
                float conf = api.TessResultIteratorConfidence(ri, level);
                if (symbol != null) {
                    logger.info(String.format("symbol %s, conf: %f", symbol.getString(0), conf));
                    boolean indent = false;
                    TessChoiceIterator ci = api.TessResultIteratorGetChoiceIterator(ri);
                    do {
                        if (indent) {
                            System.out.print("\t");
                        }
                        System.out.print("\t- ");
                        String choice = api.TessChoiceIteratorGetUTF8Text(ci);
                        logger.info(String.format("%s conf: %f", choice, api.TessChoiceIteratorConfidence(ci)));
                        indent = true;
                    } while (api.TessChoiceIteratorNext(ci) == TessAPI1.TRUE);
                    api.TessChoiceIteratorDelete(ci);
                }
                logger.info("---------------------------------------------");
                api.TessDeleteText(symbol);
            } while (api.TessResultIteratorNext(ri, level) == TessAPI1.TRUE);
        }

        assertTrue(true);
    }

    /**
     * Test of ResultRenderer method, of class TessAPI.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testResultRenderer() throws Exception {
        logger.info("TessResultRenderer");
        String image = String.format("%s/%s", this.testResourcesDataPath, "eurotext.tif");
        String output = "capi-test.txt";
        int set_only_init_params = FALSE;
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

        api.TessBaseAPISetOutputName(handle, output);

        int rc = api.TessBaseAPIInit4(handle, datapath, language,
                oem, configs, configs_size, vars_vec, vars_values, vars_vec_size, set_only_init_params);

        if (rc != 0) {
            api.TessBaseAPIDelete(handle);
            logger.error("Could not initialize tesseract.");
            return;
        }

        String outputbase = "target/test-classes/test-results/outputbase";
        TessResultRenderer renderer = api.TessHOcrRendererCreate(outputbase);
        api.TessResultRendererInsert(renderer, api.TessBoxTextRendererCreate(outputbase));
        api.TessResultRendererInsert(renderer, api.TessTextRendererCreate(outputbase));
        String dataPath = api.TessBaseAPIGetDatapath(handle);
        api.TessResultRendererInsert(renderer, api.TessPDFRendererCreate(outputbase, dataPath));
        int result = api.TessBaseAPIProcessPages(handle, image, null, 0, renderer);

        if (result == FALSE) {
            logger.error("Error during processing.");
            return;
        }

        for (; renderer != null; renderer = api.TessResultRendererNext(renderer)) {
            String ext = api.TessResultRendererExtention(renderer).getString(0);
            logger.info(String.format("TessResultRendererExtention: %s\nTessResultRendererTitle: %s\nTessResultRendererImageNum: %d",
                    ext,
                    api.TessResultRendererTitle(renderer).getString(0),
                    api.TessResultRendererImageNum(renderer)));
        }

        api.TessDeleteResultRenderer(renderer);
        assertTrue(new File(outputbase + ".pdf").exists());
    }

    public class TessAPIImpl implements TessAPI {

        public TessAPI getInstance() {
            return INSTANCE;
        }

        public void TessAPIEndPage() {
        }

        public void TessAPIRelease() {
        }

        @Override
        public String TessVersion() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessDeleteText(Pointer text) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessDeleteTextArray(PointerByReference arr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessDeleteIntArray(IntBuffer arr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessTextRendererCreate(String outputbase) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessHOcrRendererCreate(String outputbase) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessPDFRendererCreate(String outputbase, String datadir) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessUnlvRendererCreate(String outputbase) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessBoxTextRendererCreate(String outputbase) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessDeleteResultRenderer(TessResultRenderer renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessResultRendererInsert(TessResultRenderer renderer, TessResultRenderer next) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultRenderer TessResultRendererNext(TessResultRenderer renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererBeginDocument(TessResultRenderer renderer, String title) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererAddImage(TessResultRenderer renderer, PointerByReference api) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererEndDocument(TessResultRenderer renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessResultRendererExtention(TessResultRenderer renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessResultRendererTitle(TessResultRenderer renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultRendererImageNum(TessResultRenderer renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessBaseAPI TessBaseAPICreate() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIDelete(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetInputName(TessBaseAPI handle, String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetInputName(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetInputImage(TessBaseAPI handle, Pix pix) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pix TessBaseAPIGetInputImage(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetSourceYResolution(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetDatapath(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetOutputName(TessBaseAPI handle, String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPISetVariable(TessBaseAPI handle, String name, String value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetIntVariable(TessBaseAPI handle, String name, IntBuffer value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetBoolVariable(TessBaseAPI handle, String name, IntBuffer value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetDoubleVariable(TessBaseAPI handle, String name, DoubleBuffer value) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetStringVariable(TessBaseAPI handle, String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIPrintVariablesToFile(TessBaseAPI handle, String filename) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInit1(TessBaseAPI handle, String datapath, String language, int oem, PointerByReference configs, int configs_size) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInit2(TessBaseAPI handle, String datapath, String language, int oem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInit3(TessBaseAPI handle, String datapath, String language) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInit4(TessBaseAPI handle, String datapath, String language, int oem, PointerByReference configs, int configs_size, PointerByReference vars_vec, PointerByReference vars_values, NativeSize vars_vec_size, int set_only_non_debug_params) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetInitLanguagesAsString(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PointerByReference TessBaseAPIGetLoadedLanguagesAsVector(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public PointerByReference TessBaseAPIGetAvailableLanguagesAsVector(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIInitLangMod(TessBaseAPI handle, String datapath, String language) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIInitForAnalysePage(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIReadConfigFile(TessBaseAPI handle, String filename, int init_only) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetPageSegMode(TessBaseAPI handle, int mode) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetPageSegMode(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIRect(TessBaseAPI handle, ByteBuffer imagedata, int bytes_per_pixel, int bytes_per_line, int left, int top, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIClearAdaptiveClassifier(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetImage(TessBaseAPI handle, ByteBuffer imagedata, int width, int height, int bytes_per_pixel, int bytes_per_line) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetImage2(TessBaseAPI handle, Pix pix) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetSourceResolution(TessBaseAPI handle, int ppi) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPISetRectangle(TessBaseAPI handle, int left, int top, int width, int height) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pix TessBaseAPIGetThresholdedImage(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boxa TessBaseAPIGetRegions(TessBaseAPI handle, PointerByReference pixa) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boxa TessBaseAPIGetTextlines(TessBaseAPI handle, PointerByReference pixa, PointerByReference blockids) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boxa TessBaseAPIGetTextlines1(TessBaseAPI handle, int raw_image, int raw_padding, PointerByReference pixa, PointerByReference blockids, PointerByReference paraids) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boxa TessBaseAPIGetStrips(TessBaseAPI handle, PointerByReference pixa, PointerByReference blockids) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boxa TessBaseAPIGetWords(TessBaseAPI handle, PointerByReference pixa) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boxa TessBaseAPIGetConnectedComponents(TessBaseAPI handle, PointerByReference cc) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boxa TessBaseAPIGetComponentImages(TessBaseAPI handle, int level, int text_only, PointerByReference pixa, PointerByReference blockids) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Boxa TessBaseAPIGetComponentImages1(TessBaseAPI handle, int level, int text_only, int raw_image, int raw_padding, PointerByReference pixa, PointerByReference blockids, PointerByReference paraids) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetThresholdedImageScaleFactor(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIDumpPGM(TessBaseAPI handle, String filename) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessPageIterator TessBaseAPIAnalyseLayout(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIRecognize(TessBaseAPI handle, ETEXT_DESC monitor) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIRecognizeForChopTest(TessBaseAPI handle, ETEXT_DESC monitor) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultIterator TessBaseAPIGetIterator(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessMutableIterator TessBaseAPIGetMutableIterator(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIProcessPages(TessBaseAPI handle, String filename, String retry_config, int timeout_millisec, TessResultRenderer renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIProcessPage(TessBaseAPI handle, Pix pix, int page_index, String filename, String retry_config, int timeout_millisec, TessResultRenderer renderer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIGetUTF8Text(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIGetHOCRText(TessBaseAPI handle, int page_number) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIGetBoxText(TessBaseAPI handle, int page_number) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessBaseAPIGetUNLVText(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIMeanTextConf(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public IntByReference TessBaseAPIAllWordConfidences(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIAdaptToWordStr(TessBaseAPI handle, int mode, String wordstr) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIClear(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIEnd(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIIsValidWord(TessBaseAPI handle, String word) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessBaseAPIGetTextDirection(TessBaseAPI handle, IntBuffer out_offset, FloatBuffer out_slope) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessBaseAPIClearPersistentCache(TessBaseAPI handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessBaseAPIGetUnichar(TessBaseAPI handle, int unichar_id) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessPageIteratorDelete(TessPageIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessPageIterator TessPageIteratorCopy(TessPageIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessPageIteratorBegin(TessPageIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorNext(TessPageIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorIsAtBeginningOf(TessPageIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorIsAtFinalElement(TessPageIterator handle, int level, int element) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorBoundingBox(TessPageIterator handle, int level, IntBuffer left, IntBuffer top, IntBuffer right, IntBuffer bottom) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorBlockType(TessPageIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pix TessPageIteratorGetBinaryImage(TessPageIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pix TessPageIteratorGetImage(TessPageIterator handle, int level, int padding, Pix original_image, IntBuffer left, IntBuffer top) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessPageIteratorBaseline(TessPageIterator handle, int level, IntBuffer x1, IntBuffer y1, IntBuffer x2, IntBuffer y2) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessPageIteratorOrientation(TessPageIterator handle, IntBuffer orientation, IntBuffer writing_direction, IntBuffer textline_order, FloatBuffer deskew_angle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessPageIteratorParagraphInfo(TessPageIterator handle, IntBuffer justification, IntBuffer is_list_item, IntBuffer is_crown, IntBuffer first_line_indent) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessResultIteratorDelete(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessResultIterator TessResultIteratorCopy(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessPageIterator TessResultIteratorGetPageIterator(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessPageIterator TessResultIteratorGetPageIteratorConst(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorNext(TessResultIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Pointer TessResultIteratorGetUTF8Text(TessResultIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public float TessResultIteratorConfidence(TessResultIterator handle, int level) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessResultIteratorWordRecognitionLanguage(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessResultIteratorWordFontAttributes(TessResultIterator handle, IntBuffer is_bold, IntBuffer is_italic, IntBuffer is_underlined, IntBuffer is_monospace, IntBuffer is_serif, IntBuffer is_smallcaps, IntBuffer pointsize, IntBuffer font_id) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorWordIsFromDictionary(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorWordIsNumeric(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorSymbolIsSuperscript(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorSymbolIsSubscript(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessResultIteratorSymbolIsDropcap(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TessChoiceIterator TessResultIteratorGetChoiceIterator(TessResultIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void TessChoiceIteratorDelete(TessChoiceIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int TessChoiceIteratorNext(TessChoiceIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String TessChoiceIteratorGetUTF8Text(TessChoiceIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public float TessChoiceIteratorConfidence(TessChoiceIterator handle) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
