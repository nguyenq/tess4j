/**
 * Copyright @ 2014 Quan Nguyen
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
import java.nio.ByteBuffer;
import java.util.List;
import javax.imageio.IIOImage;

/**
 * An interface represents common OCR methods.
 */
public interface ITesseract {

    String htmlBeginTag = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\""
            + " \"http://www.w3.org/TR/html4/loose.dtd\">\n"
            + "<html>\n<head>\n<title></title>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html;"
            + "charset=utf-8\" />\n<meta name='ocr-system' content='tesseract'/>\n"
            + "</head>\n<body>\n";
    String htmlEndTag = "</body>\n</html>\n";

    /**
     * Rendered formats supported by Tesseract.
     */
    public enum RenderedFormat {

        TEXT, HOCR, PDF, UNLV, BOX
    }

    /**
     * Performs OCR operation.
     *
     * @param imageFile an image file
     * @return the recognized text
     * @throws TesseractException
     */
    String doOCR(File imageFile) throws TesseractException;

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
    String doOCR(File imageFile, Rectangle rect) throws TesseractException;

    /**
     * Performs OCR operation.
     *
     * @param bi a buffered image
     * @return the recognized text
     * @throws TesseractException
     */
    String doOCR(BufferedImage bi) throws TesseractException;

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
    String doOCR(BufferedImage bi, Rectangle rect) throws TesseractException;

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
    String doOCR(List<IIOImage> imageList, Rectangle rect) throws TesseractException;

    /**
     * Performs OCR operation.
     *
     * @param imageList a list of <code>IIOImage</code> objects
     * @param filename input file name. Needed only for training and reading a
     * UNLV zone file.
     * @param rect the bounding rectangle defines the region of the image to be
     * recognized. A rectangle of zero dimension or <code>null</code> indicates
     * the whole image.
     * @return the recognized text
     * @throws TesseractException
     */
    String doOCR(List<IIOImage> imageList, String filename, Rectangle rect) throws TesseractException;

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
    String doOCR(int xsize, int ysize, ByteBuffer buf, Rectangle rect, int bpp) throws TesseractException;

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
    String doOCR(int xsize, int ysize, ByteBuffer buf, String filename, Rectangle rect, int bpp) throws TesseractException;

    /**
     * Sets tessdata path.
     *
     * @param datapath the tessdata path to set
     */
    void setDatapath(String datapath);

    /**
     * Sets language for OCR.
     *
     * @param language the language code, which follows ISO 639-3 standard.
     */
    void setLanguage(String language);

    /**
     * Sets OCR engine mode.
     *
     * @param ocrEngineMode the OcrEngineMode to set
     */
    void setOcrEngineMode(int ocrEngineMode);

    /**
     * Sets page segmentation mode.
     *
     * @param mode the page segmentation mode to set
     */
    void setPageSegMode(int mode);

    /**
     * Sets the value of Tesseract's internal parameter.
     *
     * @param key variable name, e.g., <code>tessedit_create_hocr</code>,
     * <code>tessedit_char_whitelist</code>, etc.
     * @param value value for corresponding variable, e.g., "1", "0",
     * "0123456789", etc.
     */
    void setTessVariable(String key, String value);

    /**
     * Sets configs to be passed to Tesseract's <code>Init</code> method.
     *
     * @param configs list of config filenames, e.g., "digits", "bazaar",
     * "quiet"
     */
    void setConfigs(List<String> configs);

    /**
     * Creates documents for given renderers.
     *
     * @param filename input image
     * @param outputbase output filename without extension
     * @param formats types of renderers
     * @throws TesseractException
     */
    void createDocuments(String filename, String outputbase, List<RenderedFormat> formats) throws TesseractException;

    /**
     * Creates documents for given renderers.
     *
     * @param filenames array of input files
     * @param outputbases array of output filenames without extension
     * @param formats types of renderers
     * @throws TesseractException
     */
    void createDocuments(String[] filenames, String[] outputbases, List<RenderedFormat> formats) throws TesseractException;

    /**
     * Gets segmented regions at specified page iterator level.
     *
     * @param bi input image
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of <code>Rectangle</code>
     * @throws TesseractException
     */
    List<Rectangle> getSegmentedRegions(BufferedImage bi, int pageIteratorLevel) throws TesseractException;
    
    /**
     * Gets recognized words at specified page iterator level.
     * 
     * @param bi input image
     * @param pageIteratorLevel TessPageIteratorLevel enum
     * @return list of <code>Word</code>
     */
    List<Word> getWords(BufferedImage bi, int pageIteratorLevel);
}
