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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.sourceforge.lept4j.Box;
import net.sourceforge.lept4j.Boxa;
import static net.sourceforge.lept4j.ILeptonica.L_CLONE;
import net.sourceforge.lept4j.Leptonica1;
import net.sourceforge.lept4j.Pix;
import net.sourceforge.lept4j.util.LeptUtils;
import static net.sourceforge.tess4j.ITessAPI.FALSE;
import static net.sourceforge.tess4j.ITessAPI.TRUE;

import net.sourceforge.tess4j.ITessAPI.TessBaseAPI;
import net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode;
import net.sourceforge.tess4j.ITessAPI.TessPageIterator;
import net.sourceforge.tess4j.ITessAPI.TessResultIterator;
import net.sourceforge.tess4j.ITessAPI.TessResultRenderer;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.LoggHelper;
import org.slf4j.*;

/**
 * An object layer on top of <code>TessAPI</code>, provides character
 * recognition support for common image formats, and multi-page TIFF images
 * beyond the uncompressed, binary TIFF format supported by Tesseract OCR
 * engine. The extended capabilities are provided by the
 * <code>Java Advanced Imaging Image I/O Tools</code>.<br>
 * <br>
 * Support for PDF documents is available through <code>PDFBox</code>.<br>
 * <br>
 * Any program that uses the library will need to ensure that the required
 * libraries (the <code>.jar</code> files for <code>jna</code> and
 * <code>jai-imageio</code>) are in its compile and run-time
 * <code>classpath</code>.
 */
public class Tesseract implements ITesseract {

    private String language = "eng";
    private String datapath;
    private int psm = -1;
    private int ocrEngineMode = TessOcrEngineMode.OEM_DEFAULT;
    private final Properties prop = new Properties();
    private final List<String> configList = new ArrayList<>();

    private TessAPI api;
    private TessBaseAPI handle;

    private boolean alreadyInvoked;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());

    public Tesseract() {
        try {
            datapath = System.getenv("TESSDATA_PREFIX");
        } catch (Exception e) {
            // ignore
        } finally {
            if (datapath == null) {
                datapath = "./";
            }
        }
    }

    /**
     * Returns TessAPI object.
     *
     * @return api
     */
    protected TessAPI getAPI() {
        return api;
    }

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
     * Set the value of Tesseract's internal parameter.
     *
     * @param key variable name, e.g., <code>tessedit_create_hocr</code>,
     * <code>tessedit_char_whitelist</code>, etc.
     * @param value value for corresponding variable, e.g., "1", "0",
     * "0123456789", etc.
     */
    @Override
    public void setVariable(String key, String value) {
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
     * @param inputFile an image file
     * @param rects list of the bounding rectangles defines the regions of the
     * image to be recognized. A rectangle of zero dimension or
     * <code>null</code> indicates the whole image.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(File inputFile, List<Rectangle> rects) throws TesseractException {
        try {
            File imageFile = ImageIOHelper.getImageFile(inputFile);
            String imageFileFormat = ImageIOHelper.getImageFileFormat(imageFile);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFileFormat);
            if (!readers.hasNext()) {
                throw new RuntimeException(ImageIOHelper.JAI_IMAGE_READER_MESSAGE);
            }
            ImageReader reader = readers.next();
            StringBuilder result = new StringBuilder();
            try (ImageInputStream iis = ImageIO.createImageInputStream(imageFile);) {
                reader.setInput(iis);
                int imageTotal = reader.getNumImages(true);

                if ("pdf".equals(org.apache.commons.io.FilenameUtils.getExtension(inputFile.getName()).toLowerCase())) {
                    setVariable("user_defined_dpi", "300");
                }

                init();
                setVariables();

                for (int i = 0; i < imageTotal; i++) {
                    IIOImage oimage = reader.readAll(i, reader.getDefaultReadParam());
                    result.append(doOCR(oimage, inputFile.getPath(), rects, i + 1));
                }

                if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_create_hocr"))) {
                    result.insert(0, htmlBeginTag).append(htmlEndTag);
                }
            } finally {
                // delete temporary TIFF image for PDF
                if (imageFile != null && imageFile.exists() && imageFile != inputFile && imageFile.getName().startsWith("multipage") && imageFile.getName().endsWith(ImageIOHelper.TIFF_EXT)) {
                    imageFile.delete();
                }
                reader.dispose();
                dispose();
            }

            return result.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TesseractException(e);
        }
    }

    /**
     * Performs OCR operation.
     *
     * @param imageList a list of <code>IIOImage</code> objects
     * @param filename input file name. Needed only for training and reading a
     * UNLV zone file.
     * @param roiss list of list of the bounding rectangles defines the regions
     * of the images to be recognized. A rectangle of zero dimension or
     * <code>null</code> indicates the whole image.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(List<IIOImage> imageList, String filename, List<List<Rectangle>> roiss) throws TesseractException {
        init();
        setVariables();

        try {
            StringBuilder sb = new StringBuilder();
            int pageNum = 0;

            for (IIOImage oimage : imageList) {
                List<Rectangle> rois;
                if (roiss == null || roiss.isEmpty() || pageNum >= roiss.size()) {
                    rois = null;
                } else {
                    rois = roiss.get(pageNum);
                }
                sb.append(doOCR(oimage, filename, rois, ++pageNum));
            }

            if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_create_hocr"))) {
                sb.insert(0, htmlBeginTag).append(htmlEndTag);
            }

            return sb.toString();
        } finally {
            dispose();
        }
    }

    /**
     * Performs OCR operation.
     * <br>
     * Note: <code>init()</code> and <code>setTessVariables()</code> must be
     * called before use; <code>dispose()</code> should be called afterwards.
     *
     * @param oimage an <code>IIOImage</code> object
     * @param filename input file name
     * @param rois list of the bounding rectangles defines the regions of the
     * image to be recognized. A rectangle of zero dimension or
     * <code>null</code> indicates the whole image.
     * @param pageNum page number
     * @return the recognized text
     * @throws TesseractException
     */
    private String doOCR(IIOImage oimage, String filename, List<Rectangle> rois, int pageNum) throws TesseractException {
        StringBuilder sb = new StringBuilder();

        try {
            setImage(oimage.getRenderedImage());
            if (rois != null && !rois.isEmpty()) {
                for (Rectangle rect : rois) {
                    setROI(rect);
                    sb.append(getOCRText(filename, pageNum));
                }
            } else {
                sb.append(getOCRText(filename, pageNum));
            }
        } catch (IOException ioe) {
            // skip the problematic image
            logger.warn(ioe.getMessage(), ioe);
        }

        return sb.toString();
    }

    /**
     * Performs OCR operation. Use <code>SetImage</code>, (optionally)
     * <code>SetRectangle</code>, and one or more of the <code>Get*Text</code>
     * functions.
     *
     * @param xsize width of image
     * @param ysize height of image
     * @param buf pixel data
     * @param bpp bits per pixel, represents the bit depth of the image, with 1
     * for binary bitmap, 8 for gray, and 24 for color RGB.
     * @param filename input file name. Needed only for training and reading a
     * UNLV zone file.
     * @param rects list of the bounding rectangle defines the regions of the
     * image to be recognized. A rectangle of zero dimension or
     * <code>null</code> indicates the whole image.
     * @return the recognized text
     * @throws TesseractException
     */
    @Override
    public String doOCR(int xsize, int ysize, ByteBuffer buf, int bpp, String filename, List<Rectangle> rects) throws TesseractException {
        init();
        setVariables();

        try {
            StringBuilder sb = new StringBuilder();
            setImage(xsize, ysize, buf, bpp);
            if (rects != null && !rects.isEmpty()) {
                for (Rectangle rect : rects) {
                    setROI(rect);
                    sb.append(getOCRText(filename, 1));
                }
            } else {
                sb.append(getOCRText(filename, 1));
            }
            return sb.toString();
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
        api = TessAPI.INSTANCE;
        handle = api.TessBaseAPICreate();
        StringArray sarray = new StringArray(configList.toArray(new String[0]));
        PointerByReference configs = new PointerByReference();
        configs.setPointer(sarray);
        api.TessBaseAPIInit1(handle, datapath, language, ocrEngineMode, configs, configList.size());
        if (psm > -1) {
            api.TessBaseAPISetPageSegMode(handle, psm);
        }
                
        validateDatapathAndLanguagePacks();
    }
    
    /**
     * Validates specified datapath and language data existence.
     */
    void validateDatapathAndLanguagePacks() {
        String dataPath = api.TessBaseAPIGetDatapath(handle);
        if (!new File(dataPath).exists()) {
            throw new IllegalArgumentException("Specified datapath " + dataPath + " does not exist.");
        }

        Pointer ptr = api.TessBaseAPIGetLoadedLanguagesAsVector(handle).getPointer();
        String[] loadedLangs = ptr.getStringArray(0);
        PointerByReference pref = new PointerByReference();
        pref.setPointer(ptr);
        api.TessDeleteTextArray(pref);
        ptr = api.TessBaseAPIGetAvailableLanguagesAsVector(handle).getPointer();
        String[] availLangs = ptr.getStringArray(0);
        pref.setPointer(ptr);
        api.TessDeleteTextArray(pref);

        if (!Arrays.asList(availLangs).containsAll(Arrays.asList(loadedLangs))) {
            throw new IllegalArgumentException("Specified language data does not exist.");
        }
    }

    /**
     * Sets Tesseract's internal parameters.
     */
    protected void setVariables() {
        Enumeration<?> em = prop.propertyNames();
        while (em.hasMoreElements()) {
            String key = (String) em.nextElement();
            api.TessBaseAPISetVariable(handle, key, prop.getProperty(key));
        }
    }

    /**
     * Sets image to be processed.
     * <br>
     * <code>Pix</code> vs raw, which to use? <code>Pix</code> is the preferred
     * input for efficiency, since raw buffers are copied.
     * <br>
     * <code>SetImage</code> for Pix clones its input, so the source pix may be
     * <code>pixDestroyed</code> immediately after, but may not go away until
     * after the Thresholder has finished with it.
     *
     * @param image a rendered image
     * @throws java.io.IOException
     */
    protected void setImage(RenderedImage image) throws IOException {
        Pix pix = null;
        try {
            pix = LeptUtils.convertImageToPix((BufferedImage) image);
            api.TessBaseAPISetImage2(handle, pix);
        } finally {
            LeptUtils.dispose(pix);
        }
    }

    /**
     * Sets image to be processed.
     * <br>
     * Greyscale of 8 and color of 24 or 32 bits per pixel may be given. Palette
     * color images will not work properly and must be converted to 24 bit.
     * <br>
     * Binary images of 1 bit per pixel may also be given but they must be byte
     * packed with the MSB of the first byte being the first pixel, and a one
     * pixel is WHITE.
     *
     * @param xsize width of image
     * @param ysize height of image
     * @param buf pixel data
     * @param bpp bits per pixel, represents the bit depth of the image, with 1
     * for binary bitmap, 8 for gray, and 24 for color RGB.
     */
    protected void setImage(int xsize, int ysize, ByteBuffer buf, int bpp) {
        int bytespp = bpp / 8;
        int bytespl = (int) Math.ceil(xsize * bpp / 8.0);
        api.TessBaseAPISetImage(handle, buf, xsize, ysize, bytespp, bytespl);
    }

    /**
     * Sets region of interest.
     *
     * @param rect region of interest
     */
    protected void setROI(Rectangle rect) {
        if (rect != null && !rect.isEmpty()) {
            api.TessBaseAPISetRectangle(handle, rect.x, rect.y, rect.width, rect.height);
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
            api.TessBaseAPISetInputName(handle, filename);
        }

        Pointer textPtr;
        if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_create_hocr"))) {
            textPtr = api.TessBaseAPIGetHOCRText(handle, pageNum - 1);
        } else if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_write_unlv"))) {
            textPtr = api.TessBaseAPIGetUNLVText(handle);
        } else if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_create_alto"))) {
            textPtr = api.TessBaseAPIGetAltoText(handle, pageNum - 1);
        } else if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_create_page_xml"))) {
            textPtr = api.TessBaseAPIGetPAGEText(handle, pageNum - 1);
        } else if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_create_lstmbox"))) {
            textPtr = api.TessBaseAPIGetLSTMBoxText(handle, pageNum - 1);
        } else if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_create_tsv"))) {
            textPtr = api.TessBaseAPIGetTsvText(handle, pageNum - 1);
        } else if (String.valueOf(TRUE).equals(prop.getProperty("tessedit_create_wordstrbox"))) {
            textPtr = api.TessBaseAPIGetWordStrBoxText(handle, pageNum - 1);
        } else {
            textPtr = api.TessBaseAPIGetUTF8Text(handle);
        }
        String str = textPtr.getString(0);
        api.TessDeleteText(textPtr);
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
                        renderer = api.TessTextRendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessTextRendererCreate(outputbase));
                    }
                    break;
                case HOCR:
                    if (renderer == null) {
                        renderer = api.TessHOcrRendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessHOcrRendererCreate(outputbase));
                    }
                    break;
                case PDF:
                case PDF_TEXTONLY:
                    String dataPath = api.TessBaseAPIGetDatapath(handle);
                    boolean textonly = String.valueOf(TRUE).equals(prop.getProperty("textonly_pdf")) || format == RenderedFormat.PDF_TEXTONLY;
                    if (renderer == null) {
                        renderer = api.TessPDFRendererCreate(outputbase, dataPath, textonly ? TRUE : FALSE);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessPDFRendererCreate(outputbase, dataPath, textonly ? TRUE : FALSE));
                    }
                    break;
                case BOX:
                    if (renderer == null) {
                        renderer = api.TessBoxTextRendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessBoxTextRendererCreate(outputbase));
                    }
                    break;
                case UNLV:
                    if (renderer == null) {
                        renderer = api.TessUnlvRendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessUnlvRendererCreate(outputbase));
                    }
                    break;
                case ALTO:
                    if (renderer == null) {
                        renderer = api.TessAltoRendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessAltoRendererCreate(outputbase));
                    }
                    break;
                case PAGE:
                    if (renderer == null) {
                        renderer = api.TessPAGERendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessPAGERendererCreate(outputbase));
                    }
                    break;
                case TSV:
                    if (renderer == null) {
                        renderer = api.TessTsvRendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessTsvRendererCreate(outputbase));
                    }
                    break;
                case LSTMBOX:
                    if (renderer == null) {
                        renderer = api.TessLSTMBoxRendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessLSTMBoxRendererCreate(outputbase));
                    }
                    break;
                case WORDSTRBOX:
                    if (renderer == null) {
                        renderer = api.TessWordStrBoxRendererCreate(outputbase);
                    } else {
                        api.TessResultRendererInsert(renderer, api.TessWordStrBoxRendererCreate(outputbase));
                    }
                    break;
            }
        }

        return renderer;
    }

    /**
     * Creates documents for given renderer.
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
        setVariables();

        try {
            for (int i = 0; i < filenames.length; i++) {
                File inputFile = new File(filenames[i]);
                File imageFile = null;

                try {
                    // if PDF, convert to multi-page TIFF
                    imageFile = ImageIOHelper.getImageFile(inputFile);

                    TessResultRenderer renderer = createRenderers(outputbases[i], formats);
                    createDocuments(imageFile.getPath(), renderer);
                    api.TessDeleteResultRenderer(renderer);
                } catch (Exception e) {
                    // skip the problematic image file
                    logger.warn(e.getMessage(), e);
                } finally {
                    // delete temporary TIFF image for PDF
                    if (imageFile != null && imageFile.exists() && imageFile != inputFile && imageFile.getName().startsWith("multipage") && imageFile.getName().endsWith(ImageIOHelper.TIFF_EXT)) {
                        imageFile.delete();
                    }
                }
            }
        } finally {
            dispose();
        }
    }

    /**
     * Creates documents for given renderer.
     *
     * @param filename input file
     * @param renderer renderer
     * @return the average text confidence for Tesseract page result
     * @throws TesseractException
     */
    private int createDocuments(String filename, TessResultRenderer renderer) throws TesseractException {
        api.TessBaseAPISetInputName(handle, filename); //for reading a UNLV zone file
        int result = api.TessBaseAPIProcessPages(handle, filename, null, 0, renderer);

//        if (result == ITessAPI.FALSE) {
//            throw new TesseractException("Error during processing page.");
//        }
        return api.TessBaseAPIMeanTextConf(handle);
    }

    /**
     * Creates documents for given renderer.
     *
     * @param bi buffered image
     * @param filename filename (optional)
     * @param renderer renderer
     * @return the average text confidence for Tesseract page result
     * @throws Exception
     */
    private int createDocuments(BufferedImage bi, String filename, TessResultRenderer renderer) throws Exception {
        Pix pix = LeptUtils.convertImageToPix(bi);
        String title = api.TessBaseAPIGetStringVariable(handle, DOCUMENT_TITLE);
        api.TessResultRendererBeginDocument(renderer, title);
        int result = api.TessBaseAPIProcessPage(handle, pix, 0, filename, null, 0, renderer);
        api.TessResultRendererEndDocument(renderer);
        LeptUtils.dispose(pix);

//        if (result == ITessAPI.FALSE) {
//            throw new TesseractException("Error during processing page.");
//        }
        return api.TessBaseAPIMeanTextConf(handle);
    }

    /**
     * Gets segmented regions at specified page iterator level.
     *
     * @param bi input buffered image
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of <code>Rectangle</code>
     * @throws TesseractException
     */
    @Override
    public List<Rectangle> getSegmentedRegions(BufferedImage bi, int pageIteratorLevel) throws TesseractException {
        init();
        setVariables();

        try {
            List<Rectangle> list = new ArrayList<>();
            setImage(bi);

            Boxa boxes = api.TessBaseAPIGetComponentImages(handle, pageIteratorLevel, TRUE, null, null);
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
            logger.warn(ioe.getMessage(), ioe);
            throw new TesseractException(ioe);
        } finally {
            dispose();
        }
    }

    /**
     * Gets recognized words at specified page iterator level.
     *
     * @param biList list of input buffered images
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of <code>Word</code>
     */
    @Override
    public List<Word> getWords(List<BufferedImage> biList, int pageIteratorLevel) {
        if (!alreadyInvoked) {
            this.init();
            this.setVariables();
        }

        String pageSeparator = api.TessBaseAPIGetStringVariable(handle, PAGE_SEPARATOR);
        List<Word> words = new ArrayList<>();

        try {
            for (BufferedImage bi : biList) {
                setImage(bi);

                api.TessBaseAPIRecognize(handle, null);
                TessResultIterator ri = api.TessBaseAPIGetIterator(handle);
                TessPageIterator pi = api.TessResultIteratorGetPageIterator(ri);
                api.TessPageIteratorBegin(pi);

                do {
                    Pointer ptr = api.TessResultIteratorGetUTF8Text(ri, pageIteratorLevel);
                    if (ptr == null) {
                        continue;
                    }
                    String text = ptr.getString(0);
                    api.TessDeleteText(ptr);
                    float confidence = api.TessResultIteratorConfidence(ri, pageIteratorLevel);
                    IntBuffer leftB = IntBuffer.allocate(1);
                    IntBuffer topB = IntBuffer.allocate(1);
                    IntBuffer rightB = IntBuffer.allocate(1);
                    IntBuffer bottomB = IntBuffer.allocate(1);
                    api.TessPageIteratorBoundingBox(pi, pageIteratorLevel, leftB, topB, rightB, bottomB);
                    int left = leftB.get();
                    int top = topB.get();
                    int right = rightB.get();
                    int bottom = bottomB.get();
                    Word word = new Word(text, confidence, new Rectangle(left, top, right - left, bottom - top));
                    words.add(word);
                } while (api.TessPageIteratorNext(pi, pageIteratorLevel) == TRUE);
//            api.TessPageIteratorDelete(pi);
                api.TessResultIteratorDelete(ri);

                words.add(new Word(pageSeparator, 100, new Rectangle())); // add page separator
            }

            // remove last page separator
            if (!words.isEmpty()) {
                words.remove(words.size() - 1);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (!alreadyInvoked) {
                dispose();
            }
        }

        return words;
    }

    /**
     * Creates documents with OCR result for given renderers at specified page
     * iterator level.
     *
     * @param bi input buffered image
     * @param filename filename (optional)
     * @param outputbase output filenames without extension
     * @param formats types of renderer
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return OCR result
     * @throws TesseractException
     */
    @Override
    public OCRResult createDocumentsWithResults(BufferedImage bi, String filename, String outputbase, List<ITesseract.RenderedFormat> formats, int pageIteratorLevel) throws TesseractException {
        List<OCRResult> results = createDocumentsWithResults(new BufferedImage[]{bi}, new String[]{filename}, new String[]{outputbase}, formats, pageIteratorLevel);
        if (!results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * Creates documents with OCR results for given renderers at specified page
     * iterator level.
     *
     * @param bis array of input buffered images
     * @param filenames array of filenames
     * @param outputbases array of output filenames without extension
     * @param formats types of renderer
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of OCR results
     * @throws TesseractException
     */
    @Override
    public List<OCRResult> createDocumentsWithResults(BufferedImage[] bis, String[] filenames, String[] outputbases, List<ITesseract.RenderedFormat> formats, int pageIteratorLevel) throws TesseractException {
        if (bis.length != filenames.length || bis.length != outputbases.length) {
            throw new RuntimeException("The three arrays must match in length.");
        }

        init();
        setVariables();

        List<OCRResult> results = new ArrayList<>();

        try {
            for (int i = 0; i < bis.length; i++) {
                try {
                    TessResultRenderer renderer = createRenderers(outputbases[i], formats);
                    int meanTextConfidence = createDocuments(bis[i], filenames[i], renderer);
                    api.TessDeleteResultRenderer(renderer);
                    List<Word> words = meanTextConfidence > 0 ? getRecognizedWords(pageIteratorLevel) : new ArrayList<>();
                    results.add(new OCRResult(meanTextConfidence, words));
                } catch (Exception e) {
                    // skip the problematic image file
                    logger.warn(e.getMessage(), e);
                }
            }
        } finally {
            dispose();
        }

        return results;
    }

    /**
     * Creates documents with OCR result for given renderers at specified page
     * iterator level.
     *
     * @param filename input file
     * @param outputbase output filenames without extension
     * @param formats types of renderer
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return OCR result
     * @throws TesseractException
     */
    @Override
    public OCRResult createDocumentsWithResults(String filename, String outputbase, List<ITesseract.RenderedFormat> formats, int pageIteratorLevel) throws TesseractException {
        List<OCRResult> results = createDocumentsWithResults(new String[]{filename}, new String[]{outputbase}, formats, pageIteratorLevel);
        if (!results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * Creates documents with OCR results for given renderers at specified page
     * iterator level.
     *
     * @param filenames array of input files
     * @param outputbases array of output filenames without extension
     * @param formats types of renderer
     * @return list of OCR results
     * @throws TesseractException
     */
    @Override
    public List<OCRResult> createDocumentsWithResults(String[] filenames, String[] outputbases, List<ITesseract.RenderedFormat> formats, int pageIteratorLevel) throws TesseractException {
        if (filenames.length != outputbases.length) {
            throw new RuntimeException("The two arrays must match in length.");
        }

        init();
        setVariables();

        List<OCRResult> results = new ArrayList<>();

        try {
            for (int i = 0; i < filenames.length; i++) {
                File inputFile = new File(filenames[i]);
                File imageFile = null;

                try {
                    // if PDF, convert to multi-page TIFF
                    imageFile = ImageIOHelper.getImageFile(inputFile);

                    TessResultRenderer renderer = createRenderers(outputbases[i], formats);
                    int meanTextConfidence = createDocuments(imageFile.getPath(), renderer);
                    api.TessDeleteResultRenderer(renderer);
                    List<Word> words = meanTextConfidence > 0 ? getRecognizedWords(imageFile, pageIteratorLevel) : new ArrayList<>();
                    results.add(new OCRResult(meanTextConfidence, words));
                } catch (Exception e) {
                    // skip the problematic image file
                    logger.warn(e.getMessage(), e);
                } finally {
                    // delete temporary TIFF image for PDF
                    if (imageFile != null && imageFile.exists() && imageFile != inputFile && imageFile.getName().startsWith("multipage") && imageFile.getName().endsWith(ImageIOHelper.TIFF_EXT)) {
                        imageFile.delete();
                    }
                }
            }
        } finally {
            dispose();
        }

        return results;
    }

    /**
     * Gets the detected orientation of the input image and apparent script
     * (alphabet).
     *
     * @param imageFile an image file
     * @return image orientation and script name
     */
    @Override
    public OSDResult getOSD(File imageFile) {
        try {
            // if PDF, convert to multi-page TIFF
            imageFile = ImageIOHelper.getImageFile(imageFile);
            BufferedImage bi = ImageIO.read(new FileInputStream(imageFile));
            return getOSD(bi);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }

        return new OSDResult();
    }

    /**
     * Gets the detected orientation of the input image and apparent script
     * (alphabet).
     *
     * @param bi a buffered image
     * @return image orientation and script name
     */
    @Override
    public OSDResult getOSD(BufferedImage bi) {
        init();
        setVariables();

        try {
            api.TessBaseAPIInit3(handle, datapath, "osd");
            setImage(bi);

            IntBuffer orient_degB = IntBuffer.allocate(1);
            FloatBuffer orient_confB = FloatBuffer.allocate(1);
            PointerByReference script_nameB = new PointerByReference();
            FloatBuffer script_confB = FloatBuffer.allocate(1);

            int result = api.TessBaseAPIDetectOrientationScript(handle, orient_degB, orient_confB, script_nameB, script_confB);
            if (result == TRUE) {
                int orient_deg = orient_degB.get();
                float orient_conf = orient_confB.get();
                String script_name = script_nameB.getValue().getString(0);
                float script_conf = script_confB.get();
                return new OSDResult(orient_deg, orient_conf, script_name, script_conf);
            }
        } catch (IOException ioe) {
            logger.warn(ioe.getMessage(), ioe);
        } finally {
            dispose();
        }

        return new OSDResult();
    }

    /**
     * Gets result words at specified page iterator level from a recognized
     * page.
     *
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of <code>Word</code>
     */
    private List<Word> getRecognizedWords(int pageIteratorLevel) {
        List<Word> words = new ArrayList<>();

        try {
            TessResultIterator ri = api.TessBaseAPIGetIterator(handle);
            TessPageIterator pi = api.TessResultIteratorGetPageIterator(ri);
            api.TessPageIteratorBegin(pi);

            do {
                Pointer ptr = api.TessResultIteratorGetUTF8Text(ri, pageIteratorLevel);
                if (ptr == null) {
                    continue;
                }
                String text = ptr.getString(0);
                api.TessDeleteText(ptr);
                float confidence = api.TessResultIteratorConfidence(ri, pageIteratorLevel);
                IntBuffer leftB = IntBuffer.allocate(1);
                IntBuffer topB = IntBuffer.allocate(1);
                IntBuffer rightB = IntBuffer.allocate(1);
                IntBuffer bottomB = IntBuffer.allocate(1);
                api.TessPageIteratorBoundingBox(pi, pageIteratorLevel, leftB, topB, rightB, bottomB);
                int left = leftB.get();
                int top = topB.get();
                int right = rightB.get();
                int bottom = bottomB.get();
                Word word = new Word(text, confidence, new Rectangle(left, top, right - left, bottom - top));
                words.add(word);
            } while (api.TessPageIteratorNext(pi, pageIteratorLevel) == TRUE);
//            api.TessPageIteratorDelete(pi);
            api.TessResultIteratorDelete(ri);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

        return words;
    }

    /**
     * Gets result words at specified page iterator level from pages. For
     * multi-page images, it reruns recognition, doubling processing time.
     *
     * @param inputFile input file
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of <code>Word</code>
     */
    private List<Word> getRecognizedWords(File inputFile, int pageIteratorLevel) {
        List<Word> words = new ArrayList<>();

        try {
            List<BufferedImage> biList = ImageIOHelper.getImageList(inputFile);

            if (biList.isEmpty()) {
                return words;
            } else if (biList.size() == 1) {
                return getRecognizedWords(pageIteratorLevel);
            } else {
                alreadyInvoked = true;
                return getWords(biList, pageIteratorLevel);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        } finally {
            alreadyInvoked = false;
        }

        return words;
    }

    /**
     * Releases all of the native resources used by this instance.
     */
    protected void dispose() {
        if (api != null && handle != null) {
            api.TessBaseAPIDelete(handle);
        }
    }
}
