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
import java.awt.Rectangle;
import java.awt.image.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import javax.imageio.IIOImage;
import net.sourceforge.lept4j.Box;
import net.sourceforge.lept4j.Boxa;
import static net.sourceforge.lept4j.ILeptonica.L_CLONE;
import net.sourceforge.lept4j.Leptonica1;
import static net.sourceforge.tess4j.ITessAPI.TRUE;

import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.LoggHelper;
import net.sourceforge.tess4j.util.PdfUtilities;
import org.slf4j.*;

/**
 * An object layer on top of <code>TessAPI1</code>, provides character
 * recognition support for common image formats, and multi-page TIFF images
 * beyond the uncompressed, binary TIFF format supported by Tesseract OCR
 * engine. The extended capabilities are provided by the
 * <code>Java Advanced Imaging Image I/O Tools</code>.<br>
 * <br>
 * Support for PDF documents is available through <code>Ghost4J</code>, a
 * <code>JNA</code> wrapper for <code>GPL Ghostscript</code>, which should be
 * installed and included in system path.<br>
 * <br>
 * Any program that uses the library will need to ensure that the required
 * libraries (the <code>.jar</code> files for <code>jna</code>,
 * <code>jai-imageio</code>, and <code>ghost4j</code>) are in its compile and
 * run-time <code>classpath</code>.
 */
public class Tesseract1 extends TessAPI1 implements ITesseract {

    private String language = "eng";
    private String datapath = "./";
    private RenderedFormat renderedFormat = RenderedFormat.TEXT;
    private int psm = -1;
    private int ocrEngineMode = TessOcrEngineMode.OEM_DEFAULT;
    private final Properties prop = new Properties();
    private final List<String> configList = new ArrayList<String>();

    private TessBaseAPI handle;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());

    /**
     * Returns API handle.
     *
     * @return handle
     */
    protected TessBaseAPI getHandle() {
        return handle;
    }

    /**
     * Sets path to <code>tessdata</code>.
     *
     * @param datapath the tessdata path to set
     */
    @Override
    public void setDatapath(String datapath) {
        this.datapath = datapath;
    }

    /**
     * Sets language for OCR.
     *
     * @param language the language code, which follows ISO 639-3 standard.
     */
    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Sets OCR engine mode.
     *
     * @param ocrEngineMode the OcrEngineMode to set
     */
    @Override
    public void setOcrEngineMode(int ocrEngineMode) {
        this.ocrEngineMode = ocrEngineMode;
    }

    /**
     * Sets page segmentation mode.
     *
     * @param mode the page segmentation mode to set
     */
    @Override
    public void setPageSegMode(int mode) {
        this.psm = mode;
    }

    /**
     * Enables hocr output.
     *
     * @param hocr to enable or disable hocr output
     */
    public void setHocr(boolean hocr) {
        this.renderedFormat = hocr ? RenderedFormat.HOCR : RenderedFormat.TEXT;
        prop.setProperty("tessedit_create_hocr", hocr ? "1" : "0");
    }

    /**
     * Set the value of Tesseract's internal parameter.
     *
     * @param key variable name, e.g., <code>tessedit_create_hocr</code>,
     * <code>tessedit_char_whitelist</code>, etc.
     * @param value value for corresponding variable, e.g., "1", "0",
     * "0123456789", etc.
     */
    @Override
    public void setTessVariable(String key, String value) {
        prop.setProperty(key, value);
    }

    /**
     * Sets configs to be passed to Tesseract's <code>Init</code> method.
     *
     * @param configs list of config filenames, e.g., "digits", "bazaar",
     * "quiet"
     */
    @Override
    public void setConfigs(List<String> configs) {
        configList.clear();
        if (configs != null) {
            configList.addAll(configs);
        }
    }

    /**
     * Performs OCR operation.
     *
     * @param imageFile an image file
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(File imageFile) throws TesseractException {
        return doOCR(imageFile, null);
    }

    /**
     * Performs OCR operation.
     *
     * @param imageFile an image file
     * @param rect the bounding rectangle defines the region of the image to be
     * recognized. A rectangle of zero dimension or <code>null</code> indicates
     * the whole image.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(File imageFile, Rectangle rect) throws TesseractException {
        try {
            return doOCR(ImageIOHelper.getIIOImageList(imageFile), imageFile.getPath(), rect);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TesseractException(e);
        }
    }

    /**
     * Performs OCR operation.
     *
     * @param bi a buffered image
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(BufferedImage bi) throws TesseractException {
        return doOCR(bi, null);
    }

    /**
     * Performs OCR operation.
     *
     * @param bi a buffered image
     * @param rect the bounding rectangle defines the region of the image to be
     * recognized. A rectangle of zero dimension or <code>null</code> indicates
     * the whole image.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(BufferedImage bi, Rectangle rect) throws TesseractException {
        try {
            return doOCR(ImageIOHelper.getIIOImageList(bi), rect);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TesseractException(e);
        }
    }

    /**
     * Performs OCR operation.
     *
     * @param imageList a list of <code>IIOImage</code> objects
     * @param rect the bounding rectangle defines the region of the image to be
     * recognized. A rectangle of zero dimension or <code>null</code> indicates
     * the whole image.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(List<IIOImage> imageList, Rectangle rect) throws TesseractException {
        return doOCR(imageList, null, rect);
    }

    /**
     * Performs OCR operation.
     *
     * @param imageList a list of <code>IIOImage</code> objects
     * @param filename input file name
     * @param rect the bounding rectangle defines the region of the image to be
     * recognized. A rectangle of zero dimension or <code>null</code> indicates
     * the whole image.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(List<IIOImage> imageList, String filename, Rectangle rect) throws TesseractException {
        init();
        setTessVariables();

        try {
            StringBuilder sb = new StringBuilder();
            int pageNum = 0;

            for (IIOImage oimage : imageList) {
                pageNum++;
                try {
                    setImage(oimage.getRenderedImage(), rect);
                    sb.append(getOCRText(filename, pageNum));
                } catch (IOException ioe) {
                    // skip the problematic image
                    logger.error(ioe.getMessage(), ioe);
                }
            }

            if (renderedFormat == RenderedFormat.HOCR) {
                sb.insert(0, htmlBeginTag).append(htmlEndTag);
            }

            return sb.toString();
        } finally {
            dispose();
        }
    }

    /**
     * Performs OCR operation. Use <code>SetImage</code>, (optionally)
     * <code>SetRectangle</code>, and one or more of the <code>Get*Text</code>
     * functions.
     *
     * @param xsize width of image
     * @param ysize height of image
     * @param buf pixel data
     * @param rect the bounding rectangle defines the region of the image to be
     * recognized. A rectangle of zero dimension or <code>null</code> indicates
     * the whole image.
     * @param bpp bits per pixel, represents the bit depth of the image, with 1
     * for binary bitmap, 8 for gray, and 24 for color RGB.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(int xsize, int ysize, ByteBuffer buf, Rectangle rect, int bpp) throws TesseractException {
        return doOCR(xsize, ysize, buf, null, rect, bpp);
    }

    /**
     * Performs OCR operation. Use <code>SetImage</code>, (optionally)
     * <code>SetRectangle</code>, and one or more of the <code>Get*Text</code>
     * functions.
     *
     * @param xsize width of image
     * @param ysize height of image
     * @param buf pixel data
     * @param filename input file name. Needed only for training and reading a
     * UNLV zone file.
     * @param rect the bounding rectangle defines the region of the image to be
     * recognized. A rectangle of zero dimension or <code>null</code> indicates
     * the whole image.
     * @param bpp bits per pixel, represents the bit depth of the image, with 1
     * for binary bitmap, 8 for gray, and 24 for color RGB.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(int xsize, int ysize, ByteBuffer buf, String filename, Rectangle rect, int bpp) throws TesseractException {
        init();
        setTessVariables();

        try {
            setImage(xsize, ysize, buf, rect, bpp);
            return getOCRText(filename, 1);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TesseractException(e);
        } finally {
            dispose();
        }
    }

    /**
     * Initializes Tesseract engine.
     */
    protected void init() {
        handle = TessBaseAPICreate();
        StringArray sarray = new StringArray(configList.toArray(new String[0]));
        PointerByReference configs = new PointerByReference();
        configs.setPointer(sarray);
        TessBaseAPIInit1(handle, datapath, language, ocrEngineMode, configs, configList.size());
        if (psm > -1) {
            TessBaseAPISetPageSegMode(handle, psm);
        }
    }

    /**
     * Sets Tesseract's internal parameters.
     */
    protected void setTessVariables() {
        Enumeration<?> em = prop.propertyNames();
        while (em.hasMoreElements()) {
            String key = (String) em.nextElement();
            TessBaseAPISetVariable(handle, key, prop.getProperty(key));
        }
    }

    /**
     * A wrapper for {@link #setImage(int, int, ByteBuffer, Rectangle, int)}.
     *
     * @param image a rendered image
     * @param rect region of interest
     * @throws java.io.IOException
     */
    protected void setImage(RenderedImage image, Rectangle rect) throws IOException {
        setImage(image.getWidth(), image.getHeight(), ImageIOHelper.getImageByteBuffer(image), rect, image
                .getColorModel().getPixelSize());
    }

    /**
     * Sets image to be processed.
     *
     * @param xsize width of image
     * @param ysize height of image
     * @param buf pixel data
     * @param rect the bounding rectangle defines the region of the image to be
     * recognized. A rectangle of zero dimension or <code>null</code> indicates
     * the whole image.
     * @param bpp bits per pixel, represents the bit depth of the image, with 1
     * for binary bitmap, 8 for gray, and 24 for color RGB.
     */
    protected void setImage(int xsize, int ysize, ByteBuffer buf, Rectangle rect, int bpp) {
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(xsize * bpp / 8.0);
        TessBaseAPISetImage(handle, buf, xsize, ysize, bytespp, bytespl);

        if (rect != null && !rect.isEmpty()) {
            TessBaseAPISetRectangle(handle, rect.x, rect.y, rect.width, rect.height);
        }
    }

    /**
     * Gets recognized text.
     *
     * @param filename input file name. Needed only for reading a UNLV zone
     * file.
     * @param pageNum page number; needed for hocr paging.
     * @return the recognized text
     */
    protected String getOCRText(String filename, int pageNum) {
        if (filename != null && !filename.isEmpty()) {
            TessBaseAPISetInputName(handle, filename);
        }

        Pointer utf8Text = renderedFormat == RenderedFormat.HOCR ? TessBaseAPIGetHOCRText(handle, pageNum - 1) : TessBaseAPIGetUTF8Text(handle);
        String str = utf8Text.getString(0);
        TessDeleteText(utf8Text);
        return str;
    }

    /**
     * Creates renderers for given formats.
     *
     * @param outputbase
     * @param formats
     * @return
     */
    private TessResultRenderer createRenderers(String outputbase, List<RenderedFormat> formats) {
        TessResultRenderer renderer = null;

        for (RenderedFormat format : formats) {
            switch (format) {
                case TEXT:
                    if (renderer == null) {
                        renderer = TessTextRendererCreate(outputbase);
                    } else {
                        TessResultRendererInsert(renderer, TessTextRendererCreate(outputbase));
                    }
                    break;
                case HOCR:
                    if (renderer == null) {
                        renderer = TessHOcrRendererCreate(outputbase);
                    } else {
                        TessResultRendererInsert(renderer, TessHOcrRendererCreate(outputbase));
                    }
                    break;
                case PDF:
                    String dataPath = TessBaseAPIGetDatapath(handle);
                    if (renderer == null) {
                        renderer = TessPDFRendererCreate(outputbase, dataPath);
                    } else {
                        TessResultRendererInsert(renderer, TessPDFRendererCreate(outputbase, dataPath));
                    }
                    break;
                case BOX:
                    if (renderer == null) {
                        renderer = TessBoxTextRendererCreate(outputbase);
                    } else {
                        TessResultRendererInsert(renderer, TessBoxTextRendererCreate(outputbase));
                    }
                    break;
                case UNLV:
                    if (renderer == null) {
                        renderer = TessUnlvRendererCreate(outputbase);
                    } else {
                        TessResultRendererInsert(renderer, TessUnlvRendererCreate(outputbase));
                    }
                    break;
            }
        }

        return renderer;
    }

    /**
     * Creates documents for given renderer.
     *
     * @param filename input image
     * @param outputbase output filename without extension
     * @param formats types of renderer
     * @throws TesseractException
     */
    @Override
    public void createDocuments(String filename, String outputbase, List<RenderedFormat> formats) throws TesseractException {
        createDocuments(new String[]{filename}, new String[]{outputbase}, formats);
    }

    /**
     * Creates documents.
     *
     * @param filenames array of input files
     * @param outputbases array of output filenames without extension
     * @param formats types of renderer
     * @throws TesseractException
     */
    @Override
    public void createDocuments(String[] filenames, String[] outputbases, List<RenderedFormat> formats) throws TesseractException {
        if (filenames.length != outputbases.length) {
            throw new RuntimeException("The two arrays must match in length.");
        }

        init();
        setTessVariables();

        try {
            for (int i = 0; i < filenames.length; i++) {
                File workingTiffFile = null;
                try {
                    String filename = filenames[i];

                    // if PDF, convert to multi-page TIFF
                    if (filename.toLowerCase().endsWith(".pdf")) {
                        workingTiffFile = PdfUtilities.convertPdf2Tiff(new File(filename));
                        filename = workingTiffFile.getPath();
                    }

                    TessResultRenderer renderer = createRenderers(outputbases[i], formats);
                    createDocuments(filename, renderer);
                    TessDeleteResultRenderer(renderer);
                } catch (Exception e) {
                    // skip the problematic image file
                    logger.error(e.getMessage(), e);
                } finally {
                    if (workingTiffFile != null && workingTiffFile.exists()) {
                        workingTiffFile.delete();
                    }
                }
            }
        } finally {
            dispose();
        }
    }

    /**
     * Creates documents.
     *
     * @param filename input file
     * @param renderer renderer
     * @throws TesseractException
     */
    private void createDocuments(String filename, TessResultRenderer renderer) throws TesseractException {
        TessBaseAPISetInputName(handle, filename); //for reading a UNLV zone file
        int result = TessBaseAPIProcessPages(handle, filename, null, 0, renderer);

//        if (result == ITessAPI.FALSE) {
//            throw new TesseractException("Error during processing page.");
//        }
    }

    /**
     * Gets segmented regions at specified page iterator level.
     *
     * @param bi input image
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of <code>Rectangle</code>
     * @throws TesseractException
     */
    @Override
    public List<Rectangle> getSegmentedRegions(BufferedImage bi, int pageIteratorLevel) throws TesseractException {
        init();
        setTessVariables();

        try {
            List<Rectangle> list = new ArrayList<Rectangle>();
            setImage(bi, null);

            Boxa boxes = TessBaseAPIGetComponentImages(handle, pageIteratorLevel, TRUE, null, null);
            int boxCount = Leptonica1.boxaGetCount(boxes);
            for (int i = 0; i < boxCount; i++) {
                Box box = Leptonica1.boxaGetBox(boxes, i, L_CLONE);
                if (box == null) {
                    continue;
                }
                list.add(new Rectangle(box.x, box.y, box.w, box.h));
                PointerByReference pRef = new PointerByReference();
                pRef.setValue(box.getPointer());
                Leptonica1.boxDestroy(pRef);
            }

            PointerByReference pRef = new PointerByReference();
            pRef.setValue(boxes.getPointer());
            Leptonica1.boxaDestroy(pRef);

            return list;
        } catch (IOException ioe) {
            // skip the problematic image
            logger.error(ioe.getMessage(), ioe);
            throw new TesseractException(ioe);
        } finally {
            dispose();
        }
    }

    /**
     * Gets recognized words at specified page iterator level.
     *
     * @param bi input image
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of <code>Word</code>
     */
    @Override
    public List<Word> getWords(BufferedImage bi, int pageIteratorLevel) {
        this.init();
        this.setTessVariables();

        List<Word> words = new ArrayList<Word>();

        try {
            setImage(bi, null);

            TessBaseAPIRecognize(handle, null);
            TessResultIterator ri = TessBaseAPIGetIterator(handle);
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
            dispose();
        }
    }

    /**
     * Releases all of the native resources used by this instance.
     */
    protected void dispose() {
        TessBaseAPIDelete(handle);
    }
}
