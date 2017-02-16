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

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import net.sourceforge.lept4j.Boxa;
import net.sourceforge.lept4j.Pix;
import net.sourceforge.tess4j.util.LoadLibs;

/**
 * A Java wrapper for <code>Tesseract OCR 3.04 API</code> using
 * <code>JNA Interface Mapping</code>.
 */
public interface TessAPI extends Library, ITessAPI {

    /**
     * An instance of the class library.
     */
    public static final TessAPI INSTANCE = LoadLibs.getTessAPIInstance();

    /**
     * Gets the version identifier.
     *
     * @return the version identifier
     */
    String TessVersion();

    /**
     * Deallocates the memory block occupied by text.
     *
     * @param text the pointer to text
     */
    void TessDeleteText(Pointer text);

    /**
     * Deallocates the memory block occupied by text array.
     *
     * @param arr text array pointer reference
     */
    void TessDeleteTextArray(PointerByReference arr);

    /**
     * Deallocates the memory block occupied by integer array.
     *
     * @param arr int array
     */
    void TessDeleteIntArray(IntBuffer arr);

    /* Renderer API */
    TessResultRenderer TessTextRendererCreate(String outputbase);

    TessResultRenderer TessHOcrRendererCreate(String outputbase);

    TessResultRenderer TessPDFRendererCreate(String outputbase, String datadir);

    TessResultRenderer TessUnlvRendererCreate(String outputbase);

    TessResultRenderer TessBoxTextRendererCreate(String outputbase);

    void TessDeleteResultRenderer(TessResultRenderer renderer);

    void TessResultRendererInsert(TessResultRenderer renderer, TessResultRenderer next);

    TessResultRenderer TessResultRendererNext(TessResultRenderer renderer);

    int TessResultRendererBeginDocument(TessResultRenderer renderer, String title);

    int TessResultRendererAddImage(TessResultRenderer renderer, PointerByReference api);

    int TessResultRendererEndDocument(TessResultRenderer renderer);

    Pointer TessResultRendererExtention(TessResultRenderer renderer);

    Pointer TessResultRendererTitle(TessResultRenderer renderer);

    int TessResultRendererImageNum(TessResultRenderer renderer);

    /**
     * Creates an instance of the base class for all Tesseract APIs.
     *
     * @return the TesseractAPI instance
     */
    TessBaseAPI TessBaseAPICreate();

    /**
     * Disposes the TesseractAPI instance.
     *
     * @param handle the TesseractAPI instance
     */
    void TessBaseAPIDelete(TessBaseAPI handle);

    /**
     * Set the name of the input file. Needed only for training and reading a
     * UNLV zone file, and for searchable PDF output.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the input file
     */
    void TessBaseAPISetInputName(TessBaseAPI handle, String name);

    /**
     * These functions are required for searchable PDF output. We need our hands
     * on the input file so that we can include it in the PDF without
     * transcoding. If that is not possible, we need the original image.
     * Finally, resolution metadata is stored in the PDF so we need that as
     * well.
     *
     * @param handle the TesseractAPI instance
     * @return input file name
     */
    String TessBaseAPIGetInputName(TessBaseAPI handle);

    void TessBaseAPISetInputImage(TessBaseAPI handle, Pix pix);

    Pix TessBaseAPIGetInputImage(TessBaseAPI handle);

    int TessBaseAPIGetSourceYResolution(TessBaseAPI handle);

    String TessBaseAPIGetDatapath(TessBaseAPI handle);

    /**
     * Set the name of the bonus output files. Needed only for debugging.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the output file
     */
    void TessBaseAPISetOutputName(TessBaseAPI handle, String name);

    /**
     * Set the value of an internal "parameter." Supply the name of the
     * parameter and the value as a string, just as you would in a config file.
     * Returns false if the name lookup failed. E.g.,
     * <code>SetVariable("tessedit_char_blacklist", "xyz");</code> to ignore x,
     * y and z. Or <code>SetVariable("classify_bln_numeric_mode", "1");</code>
     * to set numeric-only mode. <code>SetVariable</code> may be used before
     * <code>Init</code>, but settings will revert to defaults on
     * <code>End()</code>.<br>
     * <br>
     * Note: Must be called after <code>Init()</code>. Only works for non-init
     * variables (init variables should be passed to <code>Init()</code>).
     *
     *
     * @param handle the TesseractAPI instance
     * @param name name of the input
     * @param value variable value
     * @return 1 on success
     */
    int TessBaseAPISetVariable(TessBaseAPI handle, String name, String value);

    /**
     * Get the value of an internal int parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the input
     * @param value pass the int buffer value
     * @return 1 on success
     */
    int TessBaseAPIGetIntVariable(TessBaseAPI handle, String name, IntBuffer value);

    /**
     * Get the value of an internal bool parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @param value pass the int buffer value
     * @return 1 on success
     */
    int TessBaseAPIGetBoolVariable(TessBaseAPI handle, String name, IntBuffer value);

    /**
     * Get the value of an internal double parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @param value pass the double buffer value
     * @return 1 on success
     */
    int TessBaseAPIGetDoubleVariable(TessBaseAPI handle, String name, DoubleBuffer value);

    /**
     * Get the value of an internal string parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @return the string value
     */
    String TessBaseAPIGetStringVariable(TessBaseAPI handle, String name);

    /**
     * Print Tesseract parameters to the given file.<br>
     * <br>
     * Note: Must not be the first method called after instance create.
     *
     * @param handle the TesseractAPI instance
     * @param filename name of the file where the variables will be persisted
     */
    void TessBaseAPIPrintVariablesToFile(TessBaseAPI handle, String filename);

    /**
     * Instances are now mostly thread-safe and totally independent, but some
     * global parameters remain. Basically it is safe to use multiple
     * TessBaseAPIs in different threads in parallel, UNLESS you use
     * <code>SetVariable</code> on some of the Params in classify and textord.
     * If you do, then the effect will be to change it for all your
     * instances.<br>
     * <br>
     * Start tesseract. Returns zero on success and -1 on failure. NOTE that the
     * only members that may be called before <code>Init</code> are those listed
     * above here in the class definition.<br>
     * <br>
     * It is entirely safe (and eventually will be efficient too) to call
     * <code>Init</code> multiple times on the same instance to change language,
     * or just to reset the classifier. Languages may specify internally that
     * they want to be loaded with one or more other languages, so the <i>~</i>
     * sign is available to override that. E.g., if <code>hin</code> were set to
     * load <code>eng</code> by default, then <code>hin+~eng</code> would force
     * loading only <code>hin</code>. The number of loaded languages is limited
     * only by memory, with the caveat that loading additional languages will
     * impact both speed and accuracy, as there is more work to do to decide on
     * the applicable language, and there is more chance of hallucinating
     * incorrect words. WARNING: On changing languages, all Tesseract parameters
     * are reset back to their default values. (Which may vary between
     * languages.) If you have a rare need to set a Variable that controls
     * initialization for a second call to <code>Init</code> you should
     * explicitly call <code>End()</code> and then use <code>SetVariable</code>
     * before <code>Init</code>.<br>
     * This is only a very rare use case, since there are very few uses that
     * require any parameters to be set before <code>Init</code>.<br>
     * <br>
     * If <code>set_only_non_debug_params</code> is true, only params that do
     * not contain "debug" in the name will be set.
     *
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;]
     * indicating that multiple languages are to be loaded. E.g.,
     * <code>hin+eng</code> will load Hindi and English.
     * @param oem ocr engine mode
     * @param configs pointer configuration
     * @param configs_size pointer configuration size
     * @return 0 on success and -1 on initialization failure
     */
    int TessBaseAPIInit1(TessBaseAPI handle, String datapath, String language, int oem,
            PointerByReference configs, int configs_size);

    /**
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;]
     * indicating that multiple languages are to be loaded. E.g.,
     * <code>hin+eng</code> will load Hindi and English.
     * @param oem ocr engine mode
     * @return 0 on success and -1 on initialization failure
     */
    int TessBaseAPIInit2(TessBaseAPI handle, String datapath, String language, int oem);

    /**
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;]
     * indicating that multiple languages are to be loaded. E.g.,
     * <code>hin+eng</code> will load Hindi and English.
     * @return 0 on success and -1 on initialization failure
     */
    int TessBaseAPIInit3(TessBaseAPI handle, String datapath, String language);

    /**
     *
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;]
     * indicating that multiple languages are to be loaded. E.g.,
     * <code>hin+eng</code> will load Hindi and English.
     * @param oem ocr engine mode
     * @param configs pointer configuration
     * @param configs_size pointer configuration size
     * @param vars_vec
     * @param vars_values
     * @param vars_vec_size
     * @param set_only_non_debug_params
     * @return 0 on success and -1 on initialization failure
     */
    int TessBaseAPIInit4(TessBaseAPI handle, String datapath, String language, int oem, PointerByReference configs, int configs_size, PointerByReference vars_vec, PointerByReference vars_values, NativeSize vars_vec_size, int set_only_non_debug_params);

    /**
     * Returns the languages string used in the last valid initialization. If
     * the last initialization specified "deu+hin" then that will be returned.
     * If <code>hin</code> loaded <code>eng</code> automatically as well, then
     * that will not be included in this list. To find the languages actually
     * loaded, use <code>GetLoadedLanguagesAsVector</code>. The returned string
     * should NOT be deleted.
     *
     * @param handle the TesseractAPI instance
     * @return languages as string
     */
    String TessBaseAPIGetInitLanguagesAsString(TessBaseAPI handle);

    /**
     * Returns the loaded languages in the vector of STRINGs. Includes all
     * languages loaded by the last <code>Init</code>, including those loaded as
     * dependencies of other loaded languages.
     *
     * @param handle the TesseractAPI instance
     * @return loaded languages as vector
     */
    PointerByReference TessBaseAPIGetLoadedLanguagesAsVector(TessBaseAPI handle);

    /**
     * Returns the available languages in the vector of STRINGs.
     *
     * @param handle the TesseractAPI instance
     * @return available languages as vector
     */
    PointerByReference TessBaseAPIGetAvailableLanguagesAsVector(TessBaseAPI handle);

    /**
     * Init only the lang model component of Tesseract. The only functions that
     * work after this init are <code>SetVariable</code> and
     * <code>IsValidWord</code>. WARNING: temporary! This function will be
     * removed from here and placed in a separate API at some future time.
     *
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to eng. The language may be a
     * string of the form [~]&lt;lang&gt;[+[~]&lt;lang&gt;] indicating that
     * multiple languages are to be loaded. E.g., hin+eng will load Hindi and
     * English.
     * @return api init language mode
     */
    int TessBaseAPIInitLangMod(TessBaseAPI handle, String datapath, String language);

    /**
     * Init only for page layout analysis. Use only for calls to
     * <code>SetImage</code> and <code>AnalysePage</code>. Calls that attempt
     * recognition will generate an error.
     *
     * @param handle the TesseractAPI instance
     */
    void TessBaseAPIInitForAnalysePage(TessBaseAPI handle);

    /**
     * Read a "config" file containing a set of param, value pairs. Searches the
     * standard places: <code>tessdata/configs</code>,
     * <code>tessdata/tessconfigs</code> and also accepts a relative or absolute
     * path name. Note: only non-init params will be set (init params are set by
     * <code>Init()</code>).
     *
     *
     * @param handle the TesseractAPI instance
     * @param filename relative or absolute path for the "config" file
     * containing a set of param and value pairs
     * @param init_only
     */
    void TessBaseAPIReadConfigFile(TessBaseAPI handle, String filename, int init_only);

    /**
     * Set the current page segmentation mode. Defaults to
     * <code>PSM_SINGLE_BLOCK</code>. The mode is stored as an IntParam so it
     * can also be modified by <code>ReadConfigFile</code> or
     * <code>SetVariable("tessedit_pageseg_mode", mode as string)</code>.
     *
     * @param handle the TesseractAPI instance
     * @param mode tesseract page segment mode
     */
    void TessBaseAPISetPageSegMode(TessBaseAPI handle, int mode);

    /**
     * Return the current page segmentation mode.
     *
     * @param handle the TesseractAPI instance
     * @return page segment mode value
     */
    int TessBaseAPIGetPageSegMode(TessBaseAPI handle);

    /**
     * Recognize a rectangle from an image and return the result as a string.
     * May be called many times for a single <code>Init</code>. Currently has no
     * error checking. Greyscale of 8 and color of 24 or 32 bits per pixel may
     * be given. Palette color images will not work properly and must be
     * converted to 24 bit. Binary images of 1 bit per pixel may also be given
     * but they must be byte packed with the MSB of the first byte being the
     * first pixel, and a 1 represents WHITE. For binary images set
     * bytes_per_pixel=0. The recognized text is returned as a char* which is
     * coded as UTF8 and must be freed with the delete [] operator.<br>
     * <br>
     * Note that <code>TesseractRect</code> is the simplified convenience
     * interface. For advanced uses, use <code>SetImage</code>, (optionally)
     * <code>SetRectangle</code>, <code>Recognize</code>, and one or more of the
     * <code>Get*Text</code> functions below.
     *
     * @param handle the TesseractAPI instance
     * @param imagedata image byte buffer
     * @param bytes_per_pixel bytes per pixel
     * @param bytes_per_line bytes per line
     * @param left image left
     * @param top image top
     * @param width image width
     * @param height image height
     * @return the pointer to recognized text
     */
    Pointer TessBaseAPIRect(TessBaseAPI handle, ByteBuffer imagedata, int bytes_per_pixel, int bytes_per_line,
            int left, int top, int width, int height);

    /**
     * Call between pages or documents etc to free up memory and forget adaptive
     * data.
     *
     * @param handle the TesseractAPI instance
     */
    void TessBaseAPIClearAdaptiveClassifier(TessBaseAPI handle);

    /**
     * Provide an image for Tesseract to recognize. Format is as
     * <code>TesseractRect</code> above. Does not copy the image buffer, or take
     * ownership. The source image may be destroyed after <code>Recognize</code> is called,
     * either explicitly or implicitly via one of the <code>Get*Text</code>
     * functions. <code>SetImage</code> clears all recognition results, and sets
     * the rectangle to the full image, so it may be followed immediately by a
     * <code>GetUTF8Text</code>, and it will automatically perform recognition.
     *
     * @param handle the TesseractAPI instance
     * @param imagedata image byte buffer
     * @param width image width
     * @param height image height
     * @param bytes_per_pixel bytes per pixel
     * @param bytes_per_line bytes per line
     */
    void TessBaseAPISetImage(TessBaseAPI handle, ByteBuffer imagedata, int width, int height,
            int bytes_per_pixel, int bytes_per_line);

    /**
     * Provide an image for Tesseract to recognize. As with
     * <code>SetImage</code> above, Tesseract doesn't take a copy or ownership
     * or <code>pixDestroy</code> the image, so it must persist until after
     * <code>Recognize</code>. <code>Pix</code> vs raw, which to use? Use
     * <code>Pix</code> where possible. A future version of Tesseract may choose
     * to use <code>Pix</code> as its internal representation and discard
     * <code>IMAGE</code> altogether. Because of that, an implementation that
     * sources and targets <code>Pix</code> may end up with less copies than an
     * implementation that does not.
     *
     * @param handle the TesseractAPI instance
     * @param pix image
     */
    void TessBaseAPISetImage2(TessBaseAPI handle, Pix pix);

    /**
     * Set the resolution of the source image in pixels per inch so font size
     * information can be calculated in results. Call this after
     * <code>SetImage()</code>.
     *
     * @param handle the TesseractAPI instance
     * @param ppi source resolution value
     */
    void TessBaseAPISetSourceResolution(TessBaseAPI handle, int ppi);

    /**
     * Restrict recognition to a sub-rectangle of the image. Call after
     * <code>SetImage</code>. Each <code>SetRectangle</code> clears the
     * recognition results so multiple rectangles can be recognized with the
     * same image.
     *
     * @param handle the TesseractAPI instance
     * @param left value
     * @param top value
     * @param width value
     * @param height value
     */
    void TessBaseAPISetRectangle(TessBaseAPI handle, int left, int top, int width, int height);

    /**
     * ONLY available after <code>SetImage</code> if you have Leptonica
     * installed. Get a copy of the internal thresholded image from Tesseract.
     *
     * @param handle the TesseractAPI instance
     * @return internal thresholded image
     */
    Pix TessBaseAPIGetThresholdedImage(TessBaseAPI handle);

    /**
     * Get the result of page layout analysis as a Leptonica-style
     * <code>Boxa</code>, <code>Pixa</code> pair, in reading order. Can be
     * called before or after <code>Recognize</code>.
     *
     * @param handle the TesseractAPI instance
     * @param pixa array of Pix
     * @return array of Box
     */
    Boxa TessBaseAPIGetRegions(TessBaseAPI handle, PointerByReference pixa);

    /**
     * Get the textlines as a Leptonica-style <code>Boxa</code>,
     * <code>Pixa</code> pair, in reading order. Can be called before or after
     * <code>Recognize</code>. If <code>blockids</code> is not <code>NULL</code>, the
     * block-id of each line is also returned as an array of one element per
     * line. delete [] after use. If <code>paraids</code> is not
     * <code>NULL</code>, the paragraph-id of each line within its block is also
     * returned as an array of one element per line. delete [] after use.<br>
     * Helper method to extract from the thresholded image (most common usage).
     *
     * @param handle the TesseractAPI instance
     * @param pixa array of Pix
     * @param blockids
     * @return array of Box
     */
    Boxa TessBaseAPIGetTextlines(TessBaseAPI handle, PointerByReference pixa, PointerByReference blockids);

    /**
     * Get the textlines as a Leptonica-style <code>Boxa</code>,
     * <code>Pixa</code> pair, in reading order. Can be called before or after
     * <code>Recognize</code>. If <code>blockids</code> is not <code>NULL</code>, the
     * block-id of each line is also returned as an array of one element per
     * line. delete [] after use. If <code>paraids</code> is not
     * <code>NULL</code>, the paragraph-id of each line within its block is also
     * returned as an array of one element per line. delete [] after use.
     *
     * @param handle the TesseractAPI instance
     * @param raw_image
     * @param raw_padding
     * @param pixa array of Pix
     * @param blockids
     * @param paraids
     * @return array of Box
     */
    Boxa TessBaseAPIGetTextlines1(TessBaseAPI handle, int raw_image, int raw_padding, PointerByReference pixa, PointerByReference blockids, PointerByReference paraids);

    /**
     * Get textlines and strips of image regions as a Leptonica-style
     * <code>Boxa</code>, <code>Pixa</code> pair, in reading order. Enables
     * downstream handling of non-rectangular regions. Can be called before or
     * after <code>Recognize</code>. If <code>blockids</code> is not NULL, the block-id of
     * each line is also returned as an array of one element per line. delete []
     * after use.
     *
     * @param handle the TesseractAPI instance
     * @param pixa array of Pix
     * @param blockids
     * @return array of Box
     */
    Boxa TessBaseAPIGetStrips(TessBaseAPI handle, PointerByReference pixa, PointerByReference blockids);

    /**
     * Get the words as a Leptonica-style <code>Boxa</code>, <code>Pixa</code>
     * pair, in reading order. Can be called before or after
     * <code>Recognize</code>.
     *
     * @param handle the TesseractAPI instance
     * @param pixa array of Pix
     * @return array of Box
     */
    Boxa TessBaseAPIGetWords(TessBaseAPI handle, PointerByReference pixa);

    /**
     * Gets the individual connected (text) components (created after pages
     * segmentation step, but before recognition) as a Leptonica-style
     * <code>Boxa</code>, <code>Pixa</code> pair, in reading order. Can be
     * called before or after <code>Recognize</code>.
     *
     * @param handle the TesseractAPI instance
     * @param cc array of Pix
     * @return array of Box
     */
    Boxa TessBaseAPIGetConnectedComponents(TessBaseAPI handle, PointerByReference cc);

    /**
     * Get the given level kind of components (block, textline, word etc.) as a
     * Leptonica-style <code>Boxa</code>, <code>Pixa</code> pair, in reading
     * order. Can be called before or after <code>Recognize</code>. If <code>blockids</code>
     * is not <code>NULL</code>, the block-id of each component is also returned
     * as an array of one element per component. delete [] after use. If
     * <code>text_only</code> is true, then only text components are returned.
     * Helper function to get binary images with no padding (most common usage).
     *
     * @param handle the TesseractAPI instance
     * @param level PageIteratorLevel
     * @param text_only
     * @param pixa array of Pix
     * @param blockids
     * @return array of Box
     */
    Boxa TessBaseAPIGetComponentImages(TessBaseAPI handle, int level, int text_only, PointerByReference pixa, PointerByReference blockids);

    /**
     * Get the given level kind of components (block, textline, word etc.) as a
     * Leptonica-style <code>Boxa</code>, <code>Pixa</code> pair, in reading
     * order. Can be called before or after <code>Recognize</code>. If <code>blockids</code>
     * is not <code>NULL</code>, the block-id of each component is also returned
     * as an array of one element per component. delete [] after use. If
     * <code>paraids</code> is not <code>NULL</code>, the paragraph-id of each
     * component with its block is also returned as an array of one element per
     * component. delete [] after use. If <code>raw_image</code> is true, then
     * portions of the original image are extracted instead of the thresholded
     * image and padded with raw_padding. If <code>text_only</code> is true,
     * then only text components are returned.
     *
     * @param handle the TesseractAPI instance
     * @param level PageIteratorLevel
     * @param text_only
     * @param raw_image
     * @param raw_padding
     * @param pixa array of Pix
     * @param blockids
     * @param paraids
     * @return
     */
    Boxa TessBaseAPIGetComponentImages1(TessBaseAPI handle, int level, int text_only, int raw_image, int raw_padding, PointerByReference pixa, PointerByReference blockids, PointerByReference paraids);

    /**
     * @param handle the TesseractAPI instance
     * @return Scale factor from original image.
     */
    int TessBaseAPIGetThresholdedImageScaleFactor(TessBaseAPI handle);

    /**
     * Dump the internal binary image to a PGM file.
     *
     * @param handle the TesseractAPI instance
     * @param filename pgm file name
     */
    void TessBaseAPIDumpPGM(TessBaseAPI handle, String filename);

    /**
     * Runs page layout analysis in the mode set by <code>SetPageSegMode</code>.
     * May optionally be called prior to <code>Recognize</code> to get access to
     * just the page layout results. Returns an iterator to the results. Returns
     * <code>NULL</code> on error. The returned iterator must be deleted after
     * use. WARNING! This class points to data held within the
     * <code>TessBaseAPI</code> class, and therefore can only be used while the
     * <code>TessBaseAPI</code> class still exists and has not been subjected to
     * a call of <code>Init</code>, <code>SetImage</code>,
     * <code>Recognize</code>, <code>Clear</code>, <code>End</code>, DetectOS,
     * or anything else that changes the internal <code>PAGE_RES</code>.
     *
     * @param handle the TesseractAPI instance
     * @return returns an iterator to the results. Returns NULL on error. The
     * returned iterator must be deleted after use.
     */
    TessPageIterator TessBaseAPIAnalyseLayout(TessBaseAPI handle);

    /**
     * Recognize the image from <code>SetAndThresholdImage</code>, generating
     * Tesseract internal structures. Returns 0 on success. Optional. The
     * <code>Get*Text</code> functions below will call <code>Recognize</code> if
     * needed. After <code>Recognize</code>, the output is kept internally until
     * the next <code>SetImage</code>.
     *
     * @param handle the TesseractAPI instance
     * @param monitor the result as Tesseract internal structures
     * @return 0 on success
     */
    int TessBaseAPIRecognize(TessBaseAPI handle, ETEXT_DESC monitor);

    /**
     * Variant on <code>Recognize</code> used for testing chopper.
     *
     * @param handle the TesseractAPI instance
     * @param monitor the result as Tesseract internal structures
     * @return 0 on success
     */
    int TessBaseAPIRecognizeForChopTest(TessBaseAPI handle, ETEXT_DESC monitor);

    /**
     * Get a reading-order iterator to the results of LayoutAnalysis and/or
     * <code>Recognize</code>. The returned iterator must be deleted after use.
     * WARNING! This class points to data held within the
     * <code>TessBaseAPI</code> class, and therefore can only be used while the
     * <code>TessBaseAPI</code> class still exists and has not been subjected to
     * a call of <code>Init</code>, <code>SetImage</code>,
     * <code>Recognize</code>, <code>Clear</code>, <code>End</code>, DetectOS,
     * or anything else that changes the internal PAGE_RES.
     *
     * @param handle the TesseractAPI instance
     * @return the result iterator
     */
    TessResultIterator TessBaseAPIGetIterator(TessBaseAPI handle);

    /**
     * Get a mutable iterator to the results of LayoutAnalysis and/or
     * <code>Recognize</code>. The returned iterator must be deleted after use.
     * WARNING! This class points to data held within the
     * <code>TessBaseAPI</code> class, and therefore can only be used while the
     * <code>TessBaseAPI</code> class still exists and has not been subjected to
     * a call of <code>Init</code>, <code>SetImage</code>,
     * <code>Recognize</code>, <code>Clear</code>, <code>End</code>, DetectOS,
     * or anything else that changes the internal <code>PAGE_RES</code>.
     *
     * @param handle the TesseractAPI instance
     * @return the mutable iterator
     */
    TessMutableIterator TessBaseAPIGetMutableIterator(TessBaseAPI handle);

    /**
     * Recognizes all the pages in the named file, as a multi-page tiff or list
     * of filenames, or single image, and gets the appropriate kind of text
     * according to parameters: <code>tessedit_create_boxfile</code>,
     * <code>tessedit_make_boxes_from_boxes</code>,
     * <code>tessedit_write_unlv</code>, <code>tessedit_create_hocr</code>.
     * Calls ProcessPage on each page in the input file, which may be a
     * multi-page tiff, single-page other file format, or a plain text list of
     * images to read. If tessedit_page_number is non-negative, processing
     * begins at that page of a multi-page tiff file, or filelist. The text is
     * returned in text_out. Returns false on error. If non-zero
     * timeout_millisec terminates processing after the timeout on a single
     * page. If non-NULL and non-empty, and some page fails for some reason, the
     * page is reprocessed with the retry_config config file. Useful for
     * interactively debugging a bad page.
     *
     * @param handle the TesseractAPI instance
     * @param filename multi-page tiff or list of filenames
     * @param retry_config retry config values
     * @param timeout_millisec timeout value
     * @param renderer result renderer
     * @return the status
     */
    int TessBaseAPIProcessPages(TessBaseAPI handle, String filename, String retry_config, int timeout_millisec, TessResultRenderer renderer);

    int TessBaseAPIProcessPage(TessBaseAPI handle, Pix pix, int page_index, String filename, String retry_config, int timeout_millisec, TessResultRenderer renderer);

    /**
     * The recognized text is returned as a char* which is coded as UTF-8 and
     * must be freed with the delete [] operator.
     *
     * @param handle the TesseractAPI instance
     * @return the pointer to output text
     */
    Pointer TessBaseAPIGetUTF8Text(TessBaseAPI handle);

    /**
     * Make a HTML-formatted string with hOCR markup from the internal data
     * structures. page_number is 0-based but will appear in the output as
     * 1-based.
     *
     * @param handle the TesseractAPI instance
     * @param page_number page number
     * @return the pointer to hOCR text
     */
    Pointer TessBaseAPIGetHOCRText(TessBaseAPI handle, int page_number);

    /**
     * The recognized text is returned as a char* which is coded as a UTF8 box
     * file and must be freed with the delete [] operator. page_number is a
     * 0-base page index that will appear in the box file.
     *
     * @param handle the TesseractAPI instance
     * @param page_number number of the page
     * @return the pointer to box text
     */
    Pointer TessBaseAPIGetBoxText(TessBaseAPI handle, int page_number);

    /**
     * The recognized text is returned as a char* which is coded as UNLV format
     * Latin-1 with specific reject and suspect codes and must be freed with the
     * delete [] operator.
     *
     * @param handle the TesseractAPI instance
     * @return the pointer to UNLV text
     */
    Pointer TessBaseAPIGetUNLVText(TessBaseAPI handle);

    /**
     * Returns the average word confidence for Tesseract page result.
     *
     * @param handle the TesseractAPI instance
     * @return the (average) confidence value between 0 and 100.
     */
    int TessBaseAPIMeanTextConf(TessBaseAPI handle);

    /**
     * Returns an array of all word confidences, terminated by -1. The calling
     * function must delete [] after use. The number of confidences should
     * correspond to the number of space-delimited words in
     * <code>GetUTF8Text</code>.
     *
     * @param handle the TesseractAPI instance
     * @return all word confidences (between 0 and 100) in an array, terminated
     * by -1
     */
    IntByReference TessBaseAPIAllWordConfidences(TessBaseAPI handle);

    /**
     * Applies the given word to the adaptive classifier if possible. The word
     * must be SPACE-DELIMITED UTF-8 - l i k e t h i s , so it can tell the
     * boundaries of the graphemes. Assumes that
     * <code>SetImage</code>/<code>SetRectangle</code> have been used to set the
     * image to the given word. The mode arg should be
     * <code>PSM_SINGLE_WORD</code> or <code>PSM_CIRCLE_WORD</code>, as that
     * will be used to control layout analysis. The currently set PageSegMode is
     * preserved.
     *
     * @param handle the TesseractAPI instance
     * @param mode tesseract page segment mode
     * @param wordstr The word must be SPACE-DELIMITED UTF-8 - l i k e t h i s ,
     * so it can tell the boundaries of the graphemes.
     * @return false if adaption was not possible for some reason.
     */
    int TessBaseAPIAdaptToWordStr(TessBaseAPI handle, int mode, String wordstr);

    /**
     * Free up recognition results and any stored image data, without actually
     * freeing any recognition data that would be time-consuming to reload.
     * Afterwards, you must call <code>SetImage</code> or
     * <code>TesseractRect</code> before doing any <code>Recognize</code> or
     * <code>Get*</code> operation.
     *
     * @param handle the TesseractAPI instance
     */
    void TessBaseAPIClear(TessBaseAPI handle);

    /**
     * Close down tesseract and free up all memory. <code>End()</code> is
     * equivalent to destructing and reconstructing your TessBaseAPI. Once
     * <code>End()</code> has been used, none of the other API functions may be
     * used other than <code>Init</code> and anything declared above it in the
     * class definition.
     *
     * @param handle the TesseractAPI instance
     */
    void TessBaseAPIEnd(TessBaseAPI handle);

    /**
     * Check whether a word is valid according to Tesseract's language model.
     *
     * @param handle the TesseractAPI instance
     * @param word word value
     * @return 0 if the word is invalid, non-zero if valid
     */
    int TessBaseAPIIsValidWord(TessBaseAPI handle, String word);

    /**
     * Gets text direction.
     *
     * @param handle the TesseractAPI instance
     * @param out_offset offset
     * @param out_slope slope
     * @return TRUE if text direction is valid
     */
    int TessBaseAPIGetTextDirection(TessBaseAPI handle, IntBuffer out_offset, FloatBuffer out_slope);

    /**
     * Clear any library-level memory caches. There are a variety of
     * expensive-to-load constant data structures (mostly language dictionaries)
     * that are cached globally -- surviving the <code>Init()</code> and
     * <code>End()</code> of individual TessBaseAPI's. This function allows the
     * clearing of these caches.
     *
     * @param handle the TesseractAPI instance
     */
    void TessBaseAPIClearPersistentCache(TessBaseAPI handle);

    /**
     * Detect the orientation of the input image and apparent script (alphabet).
     * <code>orient_deg</code> is the detected clockwise rotation of the input image in
     * degrees (0, 90, 180, 270); <code>orient_conf</code> is the confidence (15.0 is
     * reasonably confident); <code>script_name</code> is an ASCII string, the name of the
     * script, e.g. "Latin"; <code>script_conf</code> is confidence level in the script.
     * 
     * @return TRUE on success and writes values to each parameter as an output
     */
    int TessBaseAPIDetectOrientationScript(TessBaseAPI handle, IntBuffer orient_deg, FloatBuffer orient_conf, PointerByReference script_name, FloatBuffer script_conf);

    /**
     * Gets the string of the specified unichar.
     *
     * @param handle the TesseractAPI instance
     * @param unichar_id the unichar id
     * @return the string form of the specified unichar.
     */
    String TessBaseAPIGetUnichar(TessBaseAPI handle, int unichar_id);

    /**
     * Deletes the specified PageIterator instance.
     *
     * @param handle the TessPageIterator instance
     */
    void TessPageIteratorDelete(TessPageIterator handle);

    /**
     * Creates a copy of the specified PageIterator instance.
     *
     * @param handle the TessPageIterator instance
     * @return page iterator copy
     */
    TessPageIterator TessPageIteratorCopy(TessPageIterator handle);

    /**
     * Resets the iterator to point to the start of the page.
     *
     * @param handle the TessPageIterator instance
     */
    void TessPageIteratorBegin(TessPageIterator handle);

    /**
     * Moves to the start of the next object at the given level in the page
     * hierarchy, and returns false if the end of the page was reached. NOTE
     * (CHANGED!) that ALL PageIteratorLevel level values will visit each
     * non-text block at least once.<br>
     * Think of non text blocks as containing a single para, with at least one
     * line, with a single imaginary word, containing a single symbol. The
     * bounding boxes mark out any polygonal nature of the block, and
     * <code>PTIsTextType(BLockType())</code> is false for non-text blocks.<br>
     * Calls to Next with different levels may be freely intermixed. This
     * function iterates words in right-to-left scripts correctly, if the
     * appropriate language has been loaded into Tesseract.
     *
     * @param handle the TessPageIterator instance
     * @param level tesseract page level
     * @return next iterator object
     */
    int TessPageIteratorNext(TessPageIterator handle, int level);

    /**
     * Returns TRUE if the iterator is at the start of an object at the given
     * level. Possible uses include determining if a call to Next(RIL_WORD)
     * moved to the start of a RIL_PARA.
     *
     * @param handle the TessPageIterator instance
     * @param level tesseract page level
     * @return 1 if true
     */
    int TessPageIteratorIsAtBeginningOf(TessPageIterator handle, int level);

    /**
     * Returns whether the iterator is positioned at the last element in a given
     * level. (e.g. the last word in a line, the last line in a block).
     *
     * @param handle the TessPageIterator instance
     * @param level tesseract page level
     * @param element page iterator level
     * @return 1 if true
     */
    int TessPageIteratorIsAtFinalElement(TessPageIterator handle, int level, int element);

    /**
     * Returns the bounding rectangle of the current object at the given level
     * in coordinates of the original image.
     *
     * @param handle the TessPageIterator instance
     * @param level tesseract page level
     * @param left int buffer position
     * @param top int buffer position
     * @param right int buffer position
     * @param bottom int buffer position
     * @return FALSE if there is no such object at the current position
     */
    int TessPageIteratorBoundingBox(TessPageIterator handle, int level, IntBuffer left, IntBuffer top,
            IntBuffer right, IntBuffer bottom);

    /**
     * Returns the type of the current block.
     *
     * @param handle the TessPageIterator instance
     * @return TessPolyBlockType value
     */
    int TessPageIteratorBlockType(TessPageIterator handle);

    /**
     * Returns a binary image of the current object at the given level. The
     * position and size match the return from BoundingBoxInternal, and so this
     * could be upscaled with respect to the original input image. Use
     * <code>pixDestroy</code> to delete the image after use. The following
     * methods are used to generate the images: <code>RIL_BLOCK</code>: mask the
     * page image with the block polygon. <code>RIL_TEXTLINE</code>: Clip the
     * rectangle of the line box from the page image. TODO(rays) fix this to
     * generate and use a line polygon. <code>RIL_WORD</code>: Clip the
     * rectangle of the word box from the page image. <code>RIL_SYMBOL</code>:
     * Render the symbol outline to an image for cblobs (prior to recognition)
     * or the bounding box otherwise. A reconstruction of the original image
     * (using xor to check for double representation) should be reasonably
     * accurate, apart from removed noise, at the block level. Below the block
     * level, the reconstruction will be missing images and line separators. At
     * the symbol level, kerned characters will be invade the bounding box if
     * rendered after recognition, making an xor reconstruction inaccurate, but
     * an or construction better. Before recognition, symbol-level
     * reconstruction should be good, even with xor, since the images come from
     * the connected components.
     *
     * @param handle the TessPageIterator instance
     * @param level PageIteratorLevel
     * @return
     */
    Pix TessPageIteratorGetBinaryImage(TessPageIterator handle, int level);

    /**
     * Returns an image of the current object at the given level in greyscale if
     * available in the input. To guarantee a binary image use BinaryImage. NOTE
     * that in order to give the best possible image, the bounds are expanded
     * slightly over the binary connected component, by the supplied padding, so
     * the top-left position of the returned image is returned in (left,top).
     * These will most likely not match the coordinates returned by BoundingBox.
     * If you do not supply an original image, you will get a binary one. Use
     * <code>pixDestroy</code> to delete the image after use.
     *
     * @param handle the TessPageIterator instance
     * @param level PageIteratorLevel
     * @param padding
     * @param original_image
     * @param left
     * @param top
     * @return
     */
    Pix TessPageIteratorGetImage(TessPageIterator handle, int level, int padding, Pix original_image, IntBuffer left, IntBuffer top);

    /**
     * Returns the baseline of the current object at the given level. The
     * baseline is the line that passes through (x1, y1) and (x2, y2).<br>
     * WARNING: with vertical text, baselines may be vertical!
     *
     * @param handle the TessPageIterator instance
     * @param level PageIteratorLevel
     * @param x1 int buffer position
     * @param y1 int buffer position
     * @param x2 int buffer position
     * @param y2 int buffer position
     * @return TRUE if the baseline is valid
     */
    int TessPageIteratorBaseline(TessPageIterator handle, int level, IntBuffer x1, IntBuffer y1, IntBuffer x2,
            IntBuffer y2);

    /**
     * Returns the orientation.
     *
     * @param handle the TessPageIterator instance
     * @param orientation orientation value
     * @param writing_direction writing direction value
     * @param textline_order text line order
     * @param deskew_angle deskew angle
     */
    void TessPageIteratorOrientation(TessPageIterator handle, IntBuffer orientation,
            IntBuffer writing_direction, IntBuffer textline_order, FloatBuffer deskew_angle);

    /**
     * Gets paragraph information.
     *
     * @param handle the TessPageIterator instance
     * @param justification justification type
     * @param is_list_item list item
     * @param is_crown very first or continuation
     * @param first_line_indent first line indentation
     */
    void TessPageIteratorParagraphInfo(TessPageIterator handle, IntBuffer justification,
            IntBuffer is_list_item, IntBuffer is_crown, IntBuffer first_line_indent);

    /**
     * Deletes the specified ResultIterator handle.
     *
     * @param handle the TessResultIterator instance
     */
    void TessResultIteratorDelete(TessResultIterator handle);

    /**
     * Creates a copy of the specified ResultIterator instance.
     *
     * @param handle the TessResultIterator instance
     * @return the copy object
     */
    TessResultIterator TessResultIteratorCopy(TessResultIterator handle);

    /**
     * Gets the PageIterator of the specified ResultIterator instance.
     *
     * @param handle the TessResultIterator instance
     * @return the page iterator
     */
    TessPageIterator TessResultIteratorGetPageIterator(TessResultIterator handle);

    /**
     * Gets the PageIterator of the specified ResultIterator instance.
     *
     * @param handle the TessResultIterator instance
     * @return the page iterator constant
     */
    TessPageIterator TessResultIteratorGetPageIteratorConst(TessResultIterator handle);

    int TessResultIteratorNext(TessResultIterator handle, int level);

    /**
     * Returns the null terminated UTF-8 encoded text string for the current
     * object at the given level. Use delete [] to free after use.
     *
     * @param handle the TessResultIterator instance
     * @param level tesseract page level
     * @return the pointer to recognized text
     */
    Pointer TessResultIteratorGetUTF8Text(TessResultIterator handle, int level);

    /**
     * Returns the mean confidence of the current object at the given level. The
     * number should be interpreted as a percent probability (0.0f-100.0f).
     *
     * @param handle the TessResultIterator instance
     * @param level tesseract page level
     * @return confidence value
     */
    float TessResultIteratorConfidence(TessResultIterator handle, int level);

    String TessResultIteratorWordRecognitionLanguage(TessResultIterator handle);

    /**
     * Returns the font attributes of the current word. If iterating at a higher
     * level object than words, e.g., textlines, then this will return the
     * attributes of the first word in that textline. The actual return value is
     * a string representing a font name. It points to an internal table and
     * SHOULD NOT BE DELETED. Lifespan is the same as the iterator itself, ie
     * rendered invalid by various members of TessBaseAPI, including
     * <code>Init</code>, <code>SetImage</code>, <code>End</code> or deleting
     * the TessBaseAPI. Pointsize is returned in printers points (1/72 inch).
     *
     * @param handle the TessResultIterator instance
     * @param is_bold font attribute
     * @param is_italic font attribute
     * @param is_underlined font attribute
     * @param is_monospace font attribute
     * @param is_serif font attribute
     * @param is_smallcaps font attribute
     * @param pointsize font attribute
     * @param font_id font attribute
     * @return font name
     */
    String TessResultIteratorWordFontAttributes(TessResultIterator handle, IntBuffer is_bold,
            IntBuffer is_italic, IntBuffer is_underlined, IntBuffer is_monospace, IntBuffer is_serif,
            IntBuffer is_smallcaps, IntBuffer pointsize, IntBuffer font_id);

    /**
     * Returns TRUE if the current word was found in a dictionary.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if word is from dictionary
     */
    int TessResultIteratorWordIsFromDictionary(TessResultIterator handle);

    /**
     * Returns TRUE if the current word is numeric.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if word is numeric
     */
    int TessResultIteratorWordIsNumeric(TessResultIterator handle);

    /**
     * Returns TRUE if the current symbol is a superscript. If iterating at a
     * higher level object than symbols, e.g., words, then this will return the
     * attributes of the first symbol in that word.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if symbol is superscript
     */
    int TessResultIteratorSymbolIsSuperscript(TessResultIterator handle);

    /**
     * Returns TRUE if the current symbol is a subscript. If iterating at a
     * higher level object than symbols, e.g., words, then this will return the
     * attributes of the first symbol in that word.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if symbol is subscript
     */
    int TessResultIteratorSymbolIsSubscript(TessResultIterator handle);

    /**
     * Returns TRUE if the current symbol is a dropcap. If iterating at a higher
     * level object than symbols, e.g., words, then this will return the
     * attributes of the first symbol in that word.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if symbol is dropcap
     */
    int TessResultIteratorSymbolIsDropcap(TessResultIterator handle);

    /* Choice iterator */
    TessChoiceIterator TessResultIteratorGetChoiceIterator(TessResultIterator handle);

    void TessChoiceIteratorDelete(TessChoiceIterator handle);

    int TessChoiceIteratorNext(TessChoiceIterator handle);

    String TessChoiceIteratorGetUTF8Text(TessChoiceIterator handle);

    float TessChoiceIteratorConfidence(TessChoiceIterator handle);
}
