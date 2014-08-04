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
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import com.ochafik.lang.jnaerator.runtime.NativeSize;

/**
 * A Java wrapper for <code>Tesseract OCR 3.02 API</code> using
 * <code>JNA Direct Mapping</code>.
 */
public class TessAPI1 implements Library {

    static final boolean WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    /**
     * Native library name.
     */
    public static final String LIB_NAME = "libtesseract303";
    public static final String LIB_NAME_NON_WIN = "tesseract";

    static {
        Native.register(WINDOWS ? LIB_NAME : LIB_NAME_NON_WIN);
    }

    /**
     * When Tesseract/Cube is initialized we can choose to instantiate/load/run
     * only the Tesseract part, only the Cube part or both along with the
     * combiner. The preference of which engine to use is stored in
     * <code>tessedit_ocr_engine_mode</code>.<br>
     * <br>
     * ATTENTION: When modifying this enum, please make sure to make the
     * appropriate changes to all the enums mirroring it (e.g. OCREngine in
     * cityblock/workflow/detection/detection_storage.proto). Such enums will
     * mention the connection to OcrEngineMode in the comments.
     */
    public static interface TessOcrEngineMode {

        /**
         * Run Tesseract only - fastest
         */
        public static final int OEM_TESSERACT_ONLY = 0;
        /**
         * Run Cube only - better accuracy, but slower
         */
        public static final int OEM_CUBE_ONLY = 1;
        /**
         * Run both and combine results - best accuracy
         */
        public static final int OEM_TESSERACT_CUBE_COMBINED = 2;
        /**
         * Specify this mode when calling <code>init_*()</code>, to indicate
         * that any of the above modes should be automatically inferred from the
         * variables in the language-specific config, command-line configs, or
         * if not specified in any of the above should be set to the default
         * <code>OEM_TESSERACT_ONLY</code>.
         */
        public static final int OEM_DEFAULT = 3;
    };

    /**
     * Possible modes for page layout analysis. These *must* be kept in order of
     * decreasing amount of layout analysis to be done, except for
     * <code>OSD_ONLY</code>, so that the inequality test macros below work.
     */
    public static interface TessPageSegMode {

        /**
         * Orientation and script detection only.
         */
        public static final int PSM_OSD_ONLY = 0;
        /**
         * Automatic page segmentation with orientation and script detection.
         * (OSD)
         */
        public static final int PSM_AUTO_OSD = 1;
        /**
         * Automatic page segmentation, but no OSD, or OCR.
         */
        public static final int PSM_AUTO_ONLY = 2;
        /**
         * Fully automatic page segmentation, but no OSD.
         */
        public static final int PSM_AUTO = 3;
        /**
         * Assume a single column of text of variable sizes.
         */
        public static final int PSM_SINGLE_COLUMN = 4;
        /**
         * Assume a single uniform block of vertically aligned text.
         */
        public static final int PSM_SINGLE_BLOCK_VERT_TEXT = 5;
        /**
         * Assume a single uniform block of text.
         */
        public static final int PSM_SINGLE_BLOCK = 6;
        /**
         * Treat the image as a single text line.
         */
        public static final int PSM_SINGLE_LINE = 7;
        /**
         * Treat the image as a single word.
         */
        public static final int PSM_SINGLE_WORD = 8;
        /**
         * Treat the image as a single word in a circle.
         */
        public static final int PSM_CIRCLE_WORD = 9;
        /**
         * Treat the image as a single character.
         */
        public static final int PSM_SINGLE_CHAR = 10;
        /**
         * Find as much text as possible in no particular order.
         */
        public static final int PSM_SPARSE_TEXT = 11;
        /**
         * Sparse text with orientation and script detection.
         */
        public static final int PSM_SPARSE_TEXT_OSD = 12;
        /**
         * Number of enum entries.
         */
        public static final int PSM_COUNT = 13;
    };

    /**
     * Enum of the elements of the page hierarchy, used in
     * <code>ResultIterator</code> to provide functions that operate on each
     * level without having to have 5x as many functions.
     */
    public static interface TessPageIteratorLevel {

        /**
         * Block of text/image/separator line.
         */
        public static final int RIL_BLOCK = 0;
        /**
         * Paragraph within a block.
         */
        public static final int RIL_PARA = 1;
        /**
         * Line within a paragraph.
         */
        public static final int RIL_TEXTLINE = 2;
        /**
         * Word within a textline.
         */
        public static final int RIL_WORD = 3;
        /**
         * Symbol/character within a word.
         */
        public static final int RIL_SYMBOL = 4;
    };

    /**
     * Possible types for a POLY_BLOCK or ColPartition. Must be kept in sync
     * with <code>kPBColors</code> in polyblk.cpp and <code>PTIs*Type</code>
     * functions below, as well as <code>kPolyBlockNames</code> in
     * publictypes.cpp. Used extensively by ColPartition, and POLY_BLOCK.
     */
    public static interface TessPolyBlockType {

        /**
         * Type is not yet known. Keep as the first element.
         */
        public static final int PT_UNKNOWN = 0;
        /**
         * Text that lives inside a column.
         */
        public static final int PT_FLOWING_TEXT = 1;
        /**
         * Text that spans more than one column.
         */
        public static final int PT_HEADING_TEXT = 2;
        /**
         * Text that is in a cross-column pull-out region.
         */
        public static final int PT_PULLOUT_TEXT = 3;
        /**
         * Partition belonging to an equation region.
         */
        public static final int PT_EQUATION = 4;
        /**
         * Partition has inline equation.
         */
        public static final int PT_INLINE_EQUATION = 5;
        /**
         * Partition belonging to a table region.
         */
        public static final int PT_TABLE = 6;
        /**
         * Text-line runs vertically.
         */
        public static final int PT_VERTICAL_TEXT = 7;
        /**
         * Text that belongs to an image.
         */
        public static final int PT_CAPTION_TEXT = 8;
        /**
         * Image that lives inside a column.
         */
        public static final int PT_FLOWING_IMAGE = 9;
        /**
         * Image that spans more than one column.
         */
        public static final int PT_HEADING_IMAGE = 10;
        /**
         * Image that is in a cross-column pull-out region.
         */
        public static final int PT_PULLOUT_IMAGE = 11;
        /**
         * Horizontal Line.
         */
        public static final int PT_HORZ_LINE = 12;
        /**
         * Vertical Line.
         */
        public static final int PT_VERT_LINE = 13;
        /**
         * Lies outside of any column.
         */
        public static final int PT_NOISE = 14;
        /**
         * Number of enum entries.
         */
        public static final int PT_COUNT = 15;
    };

    /**
     * <pre>
     *  +------------------+
     *  | 1 Aaaa Aaaa Aaaa |
     *  | Aaa aa aaa aa    |
     *  | aaaaaa A aa aaa. |
     *  |                2 |
     *  |   #######  c c C |
     *  |   #######  c c c |
     *  | < #######  c c c |
     *  | < #######  c   c |
     *  | < #######  .   c |
     *  | 3 #######      c |
     *  +------------------+
     * </pre> Orientation Example:
     * <br>
     * ====================
     * <br>
     * Above is a diagram of some (1) English and (2) Chinese text and a (3)
     * photo credit.<br>
     * <br>
     * Upright Latin characters are represented as A and a. '<' represents a
     * latin character rotated anti-clockwise 90 degrees. Upright Chinese
     * characters are represented C and c.<br> <br> NOTA BENE: enum values here
     * should match goodoc.proto<br>
     * <br> If you orient your head so that "up" aligns with Orientation, then
     * the characters will appear "right side up" and readable.<br>
     * <br>
     * In the example above, both the English and Chinese paragraphs are
     * oriented so their "up" is the top of the page (page up). The photo credit
     * is read with one's head turned leftward ("up" is to page left).<br>
     * <br>
     * The values of this enum match the convention of Tesseract's osdetect.h
     */
    public static interface TessOrientation {

        public static final int ORIENTATION_PAGE_UP = 0;
        public static final int ORIENTATION_PAGE_RIGHT = 1;
        public static final int ORIENTATION_PAGE_DOWN = 2;
        public static final int ORIENTATION_PAGE_LEFT = 3;
    };

    /**
     * The grapheme clusters within a line of text are laid out logically in
     * this direction, judged when looking at the text line rotated so that its
     * Orientation is "page up".<br>
     * <br>
     * For English text, the writing direction is left-to-right. For the Chinese
     * text in the above example, the writing direction is top-to-bottom.
     */
    public static interface TessWritingDirection {

        public static final int WRITING_DIRECTION_LEFT_TO_RIGHT = 0;
        public static final int WRITING_DIRECTION_RIGHT_TO_LEFT = 1;
        public static final int WRITING_DIRECTION_TOP_TO_BOTTOM = 2;
    };

    /**
     * The text lines are read in the given sequence.<br>
     * <br>
     * In English, the order is top-to-bottom. In Chinese, vertical text lines
     * are read right-to-left. Mongolian is written in vertical columns top to
     * bottom like Chinese, but the lines order left-to right.<br>
     * <br>
     * Note that only some combinations make sense. For example,
     * <code>WRITING_DIRECTION_LEFT_TO_RIGHT</code> implies
     * <code>TEXTLINE_ORDER_TOP_TO_BOTTOM</code>.
     */
    public static interface TessTextlineOrder {

        public static final int TEXTLINE_ORDER_LEFT_TO_RIGHT = 0;
        public static final int TEXTLINE_ORDER_RIGHT_TO_LEFT = 1;
        public static final int TEXTLINE_ORDER_TOP_TO_BOTTOM = 2;
    };

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    /**
     * Gets the version identifier.
     *
     * @return the version identifier
     */
    public static native String TessVersion();

    /**
     * Deallocates the memory block occupied by text.
     *
     * @param text the pointer to text
     */
    public static native void TessDeleteText(Pointer text);

    /**
     * Deallocates the memory block occupied by text array.
     *
     * @param arr text array pointer reference
     */
    public static native void TessDeleteTextArray(PointerByReference arr);

    /**
     * Deallocates the memory block occupied by integer array.
     *
     * @param arr int array
     */
    public static native void TessDeleteIntArray(IntBuffer arr);

    /* Renderer API */
    public static native TessAPI1.TessResultRenderer TessTextRendererCreate(String outputbase);

    public static native TessAPI1.TessResultRenderer TessHOcrRendererCreate(String outputbase);

    public static native TessAPI1.TessResultRenderer TessPDFRendererCreate(String outputbase, String datadir);

    public static native TessAPI1.TessResultRenderer TessUnlvRendererCreate(String outputbase);

    public static native TessAPI1.TessResultRenderer TessBoxTextRendererCreate(String outputbase);

    public static native void TessDeleteResultRenderer(TessAPI1.TessResultRenderer renderer);

    public static native void TessResultRendererInsert(TessAPI1.TessResultRenderer renderer, TessAPI1.TessResultRenderer next);

    public static native TessAPI1.TessResultRenderer TessResultRendererNext(TessAPI1.TessResultRenderer renderer);

    public static native int TessResultRendererBeginDocument(TessAPI1.TessResultRenderer renderer, String title);

    public static native int TessResultRendererAddImage(TessAPI1.TessResultRenderer renderer, PointerByReference api);

    public static native int TessResultRendererEndDocument(TessAPI1.TessResultRenderer renderer);

    public static native Pointer TessResultRendererExtention(TessAPI1.TessResultRenderer renderer);

    public static native Pointer TessResultRendererTitle(TessAPI1.TessResultRenderer renderer);

    public static native int TessResultRendererImageNum(TessAPI1.TessResultRenderer renderer);

    /**
     * Creates an instance of the base class for all Tesseract APIs.
     *
     * @return the TesseractAPI instance
     */
    public static native TessAPI1.TessBaseAPI TessBaseAPICreate();

    /**
     * Disposes the TesseractAPI instance.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIDelete(TessAPI1.TessBaseAPI handle);

    /**
     * Set the name of the input file. Needed only for training and reading a
     * UNLV zone file.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the input file
     */
    public static native void TessBaseAPISetInputName(TessAPI1.TessBaseAPI handle, String name);

    /**
     * These functions are required for searchable PDF output. We need our hands
     * on the input file so that we can include it in the PDF without
     * transcoding. If that is not possible, we need the original image.
     * Finally, resolution metadata is stored in the PDF so we need that as
     * well.
     */
    public static native String TessBaseAPIGetInputName(TessAPI1.TessBaseAPI handle);

    public static native int TessBaseAPIGetSourceYResolution(TessAPI1.TessBaseAPI handle);

    public static native String TessBaseAPIGetDatapath(TessAPI1.TessBaseAPI handle);

    /**
     * Set the name of the bonus output files. Needed only for debugging.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the output file
     */
    public static native void TessBaseAPISetOutputName(TessAPI1.TessBaseAPI handle, String name);

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
    public static native int TessBaseAPISetVariable(TessAPI1.TessBaseAPI handle, String name, String value);

    /**
     * Get the value of an internal int parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name name of the input
     * @param value pass the int buffer value
     * @return 1 on success
     */
    public static native int TessBaseAPIGetIntVariable(TessAPI1.TessBaseAPI handle, String name, IntBuffer value);

    /**
     * Get the value of an internal bool parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @param value pass the int buffer value
     * @return 1 on success
     */
    public static native int TessBaseAPIGetBoolVariable(TessAPI1.TessBaseAPI handle, String name, IntBuffer value);

    /**
     * Get the value of an internal double parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @param value pass the double buffer value
     * @return 1 on success
     */
    public static native int TessBaseAPIGetDoubleVariable(TessAPI1.TessBaseAPI handle, String name, DoubleBuffer value);

    /**
     * Get the value of an internal string parameter.
     *
     * @param handle the TesseractAPI instance
     * @param name pass the name of the variable
     * @return the string value
     */
    public static native String TessBaseAPIGetStringVariable(TessAPI1.TessBaseAPI handle, String name);

    /**
     * Print Tesseract parameters to the given file.<br>
     * <br>
     * Note: Must not be the first method called after instance create.
     *
     * @param handle the TesseractAPI instance
     * @param filename name of the file where the variables will be persisted
     */
    public static native void TessBaseAPIPrintVariablesToFile(TessAPI1.TessBaseAPI handle, String filename);

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
     * directory of <code>tessdata<code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]<lang>[+[~]<lang>] indicating
     * that multiple languages are to be loaded. E.g., <code>hin+eng</code> will
     * load Hindi and English.
     * @param oem ocr engine mode
     * @param configs pointer configuration
     * @param configs_size pointer configuration size
     * @return 0 on success and -1 on initialization failure
     */
    public static native int TessBaseAPIInit1(TessAPI1.TessBaseAPI handle, String datapath, String language, int oem,
            PointerByReference configs, int configs_size);

    /**
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata<code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]<lang>[+[~]<lang>] indicating
     * that multiple languages are to be loaded. E.g., <code>hin+eng</code> will
     * load Hindi and English.
     * @param oem ocr engine mode
     * @return 0 on success and -1 on initialization failure
     */
    public static native int TessBaseAPIInit2(TessAPI1.TessBaseAPI handle, String datapath, String language, int oem);

    /**
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of <code>tessdata</code> and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to <code>eng</code>. The
     * language may be a string of the form [~]<lang>[+[~]<lang>] indicating
     * that multiple languages are to be loaded. E.g., <code>hin+eng</code> will
     * load Hindi and English.
     * @return 0 on success and -1 on initialization failure
     */
    public static native int TessBaseAPIInit3(TessAPI1.TessBaseAPI handle, String datapath, String language);

    public static native int TessBaseAPIInit4(TessAPI1.TessBaseAPI handle, String datapath, String language, int oem, PointerByReference configs, int configs_size, PointerByReference vars_vec, PointerByReference vars_values, NativeSize vars_vec_size, int set_only_non_debug_params);

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
    public static native String TessBaseAPIGetInitLanguagesAsString(TessAPI1.TessBaseAPI handle);

    /**
     * Returns the loaded languages in the vector of STRINGs. Includes all
     * languages loaded by the last <code>Init</code>, including those loaded as
     * dependencies of other loaded languages.
     *
     * @param handle the TesseractAPI instance
     * @return loaded languages as vector
     */
    public static native PointerByReference TessBaseAPIGetLoadedLanguagesAsVector(TessAPI1.TessBaseAPI handle);

    /**
     * Returns the available languages in the vector of STRINGs.
     *
     * @param handle the TesseractAPI instance
     * @return available languages as vector
     */
    public static native PointerByReference TessBaseAPIGetAvailableLanguagesAsVector(TessAPI1.TessBaseAPI handle);

    /**
     * Init only the lang model component of Tesseract. The only functions that
     * work after this init are <code>SetVariable</code> and
     * <code>IsValidWord</code>. WARNING: temporary! This function will be
     * removed from here and placed in a separate API at some future time.
     *
     * @param handle the TesseractAPI instance
     * @param datapath The <code>datapath</code> must be the name of the parent
     * directory of tessdata and must end in
     * <i>/</i>. Any name after the last <i>/</i> will be stripped.
     * @param language The language is (usually) an <code>ISO 639-3</code>
     * string or <code>NULL</code> will default to eng. The language may be a
     * string of the form [~]<lang>[+[~]<lang>] indicating that multiple
     * languages are to be loaded. E.g., hin+eng will load Hindi and English.
     * @return api init language mode
     */
    public static native int TessBaseAPIInitLangMod(TessAPI1.TessBaseAPI handle, String datapath, String language);

    /**
     * Init only for page layout analysis. Use only for calls to
     * <code>SetImage</code> and <code>AnalysePage</code>. Calls that attempt
     * recognition will generate an error.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIInitForAnalysePage(TessAPI1.TessBaseAPI handle);

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
    public static native void TessBaseAPIReadConfigFile(TessAPI1.TessBaseAPI handle, String filename, int init_only);

    /**
     * Set the current page segmentation mode. Defaults to
     * <code>PSM_SINGLE_BLOCK</code>. The mode is stored as an IntParam so it
     * can also be modified by <code>ReadConfigFile</code> or
     * <code>SetVariable("tessedit_pageseg_mode", mode as string)</code>.
     *
     * @param handle the TesseractAPI instance
     * @param mode tesseract page segment mode
     */
    public static native void TessBaseAPISetPageSegMode(TessAPI1.TessBaseAPI handle, int mode);

    /**
     * Return the current page segmentation mode.
     *
     * @param handle the TesseractAPI instance
     * @return page segment mode value
     */
    public static native int TessBaseAPIGetPageSegMode(TessAPI1.TessBaseAPI handle);

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
    public static native Pointer TessBaseAPIRect(TessAPI1.TessBaseAPI handle, ByteBuffer imagedata,
            int bytes_per_pixel, int bytes_per_line, int left, int top, int width, int height);

    /**
     * Call between pages or documents etc to free up memory and forget adaptive
     * data.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIClearAdaptiveClassifier(TessAPI1.TessBaseAPI handle);

    /**
     * Provide an image for Tesseract to recognize. Format is as
     * <code>TesseractRect</code> above. Does not copy the image buffer, or take
     * ownership. The source image may be destroyed after Recognize is called,
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
    public static native void TessBaseAPISetImage(TessAPI1.TessBaseAPI handle, ByteBuffer imagedata, int width,
            int height, int bytes_per_pixel, int bytes_per_line);

    /**
     * Set the resolution of the source image in pixels per inch so font size
     * information can be calculated in results. Call this after
     * <code>SetImage()</code>.
     *
     * @param handle the TesseractAPI instance
     * @param ppi source resolution value
     */
    public static native void TessBaseAPISetSourceResolution(TessAPI1.TessBaseAPI handle, int ppi);

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
    public static native void TessBaseAPISetRectangle(TessAPI1.TessBaseAPI handle, int left, int top, int width,
            int height);

    /**
     * @param handle the TesseractAPI instance
     * @return Scale factor from original image.
     */
    public static native int TessBaseAPIGetThresholdedImageScaleFactor(TessAPI1.TessBaseAPI handle);

    /**
     * Dump the internal binary image to a PGM file.
     *
     * @param handle the TesseractAPI instance
     * @param filename pgm file name
     */
    public static native void TessBaseAPIDumpPGM(TessAPI1.TessBaseAPI handle, String filename);

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
    public static native TessAPI1.TessPageIterator TessBaseAPIAnalyseLayout(TessAPI1.TessBaseAPI handle);

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
    public static native int TessBaseAPIRecognize(TessAPI1.TessBaseAPI handle, TessAPI1.ETEXT_DESC monitor);

    /**
     * Variant on Recognize used for testing chopper.
     *
     * @param handle the TesseractAPI instance
     * @param monitor the result as Tesseract internal structures
     * @return 0 on success
     */
    public static native int TessBaseAPIRecognizeForChopTest(TessAPI1.TessBaseAPI handle, TessAPI1.ETEXT_DESC monitor);

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
    public static native TessAPI1.TessResultIterator TessBaseAPIGetIterator(TessAPI1.TessBaseAPI handle);

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
    public static native TessAPI1.TessMutableIterator TessBaseAPIGetMutableIterator(TessAPI1.TessBaseAPI handle);

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
    public static native int TessBaseAPIProcessPages(TessAPI1.TessBaseAPI handle, String filename, String retry_config, int timeout_millisec, TessAPI1.TessResultRenderer renderer);

    /**
     * The recognized text is returned as a char* which is coded as UTF-8 and
     * must be freed with the delete [] operator.
     *
     * @param handle the TesseractAPI instance
     * @return the pointer to output text
     */
    public static native Pointer TessBaseAPIGetUTF8Text(TessAPI1.TessBaseAPI handle);

    /**
     * Make a HTML-formatted string with hOCR markup from the internal data
     * structures. page_number is 0-based but will appear in the output as
     * 1-based.
     *
     * @param handle the TesseractAPI instance
     * @param page_number page number
     * @return the pointer to hOCR text
     */
    public static native Pointer TessBaseAPIGetHOCRText(TessAPI1.TessBaseAPI handle, int page_number);

    /**
     * The recognized text is returned as a char* which is coded as a UTF8 box
     * file and must be freed with the delete [] operator. page_number is a
     * 0-base page index that will appear in the box file.
     *
     * @param handle the TesseractAPI instance
     * @param page_number number of the page
     * @return the pointer to box text
     */
    public static native Pointer TessBaseAPIGetBoxText(TessAPI1.TessBaseAPI handle, int page_number);

    /**
     * The recognized text is returned as a char* which is coded as UNLV format
     * Latin-1 with specific reject and suspect codes and must be freed with the
     * delete [] operator.
     *
     * @param handle the TesseractAPI instance
     * @return the pointer to UNLV text
     */
    public static native Pointer TessBaseAPIGetUNLVText(TessAPI1.TessBaseAPI handle);

    /**
     * Returns the average word confidence for Tesseract page result.
     *
     * @param handle the TesseractAPI instance
     * @return the (average) confidence value between 0 and 100.
     */
    public static native int TessBaseAPIMeanTextConf(TessAPI1.TessBaseAPI handle);

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
    public static native IntByReference TessBaseAPIAllWordConfidences(TessAPI1.TessBaseAPI handle);

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
    public static native int TessBaseAPIAdaptToWordStr(TessAPI1.TessBaseAPI handle, int mode, String wordstr);

    /**
     * Free up recognition results and any stored image data, without actually
     * freeing any recognition data that would be time-consuming to reload.
     * Afterwards, you must call <code>SetImage</code> or
     * <code>TesseractRect</code> before doing any <code>Recognize</code> or
     * <code>Get*</code> operation.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIClear(TessAPI1.TessBaseAPI handle);

    /**
     * Close down tesseract and free up all memory. <code>End()</code> is
     * equivalent to destructing and reconstructing your TessBaseAPI. Once
     * <code>End()</code> has been used, none of the other API functions may be
     * used other than <code>Init</code> and anything declared above it in the
     * class definition.
     *
     * @param handle the TesseractAPI instance
     */
    public static native void TessBaseAPIEnd(TessAPI1.TessBaseAPI handle);

    /**
     * Check whether a word is valid according to Tesseract's language model.
     *
     * @param handle the TesseractAPI instance
     * @param word word value
     * @return 0 if the word is invalid, non-zero if valid
     */
    public static native int TessBaseAPIIsValidWord(TessAPI1.TessBaseAPI handle, String word);

    /**
     * Gets text direction.
     *
     * @param handle the TesseractAPI instance
     * @param out_offset offset
     * @param out_slope slope
     * @return TRUE if text direction is valid
     */
    public static native int TessBaseAPIGetTextDirection(TessAPI1.TessBaseAPI handle, IntBuffer out_offset,
            FloatBuffer out_slope);

    /**
     * Gets the string of the specified unichar.
     *
     * @param handle the TesseractAPI instance
     * @param unichar_id the unichar id
     * @return the string form of the specified unichar.
     */
    public static native String TessBaseAPIGetUnichar(TessAPI1.TessBaseAPI handle, int unichar_id);

    /**
     * Deletes the specified PageIterator instance.
     *
     * @param handle the TessPageIterator instance
     */
    public static native void TessPageIteratorDelete(TessAPI1.TessPageIterator handle);

    /**
     * Creates a copy of the specified PageIterator instance.
     *
     * @param handle the TessPageIterator instance
     * @return page iterator copy
     */
    public static native TessAPI1.TessPageIterator TessPageIteratorCopy(TessAPI1.TessPageIterator handle);

    /**
     * Resets the iterator to point to the start of the page.
     *
     * @param handle the TessPageIterator instance
     */
    public static native void TessPageIteratorBegin(TessAPI1.TessPageIterator handle);

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
    public static native int TessPageIteratorNext(TessAPI1.TessPageIterator handle, int level);

    /**
     * Returns TRUE if the iterator is at the start of an object at the given
     * level. Possible uses include determining if a call to Next(RIL_WORD)
     * moved to the start of a RIL_PARA.
     *
     * @param handle the TessPageIterator instance
     * @param level tesseract page level
     * @return 1 if true
     */
    public static native int TessPageIteratorIsAtBeginningOf(TessAPI1.TessPageIterator handle, int level);

    /**
     * Returns whether the iterator is positioned at the last element in a given
     * level. (e.g. the last word in a line, the last line in a block).
     *
     * @param handle the TessPageIterator instance
     * @param level tesseract page level
     * @param element page iterator level
     * @return 1 if true
     */
    public static native int TessPageIteratorIsAtFinalElement(TessAPI1.TessPageIterator handle, int level, int element);

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
    public static native int TessPageIteratorBoundingBox(TessAPI1.TessPageIterator handle, int level, IntBuffer left,
            IntBuffer top, IntBuffer right, IntBuffer bottom);

    /**
     * Returns the type of the current block.
     *
     * @param handle the TessPageIterator instance
     * @return TessPolyBlockType value
     */
    public static native int TessPageIteratorBlockType(TessAPI1.TessPageIterator handle);

    /**
     * Returns the baseline of the current object at the given level. The
     * baseline is the line that passes through (x1, y1) and (x2, y2).<br>
     * WARNING: with vertical text, baselines may be vertical!
     *
     * @param handle the TessPageIterator instance
     * @param level tesseract page level
     * @param x1 int buffer position
     * @param y1 int buffer position
     * @param x2 int buffer position
     * @param y2 int buffer position
     * @return TRUE if the baseline is valid
     */
    public static native int TessPageIteratorBaseline(TessAPI1.TessPageIterator handle, int level, IntBuffer x1,
            IntBuffer y1, IntBuffer x2, IntBuffer y2);

    /**
     * Returns the orientation.
     *
     * @param handle the TessPageIterator instance
     * @param orientation orientation value
     * @param writing_direction writing direction value
     * @param textline_order text line order
     * @param deskew_angle deskew angle
     */
    public static native void TessPageIteratorOrientation(TessAPI1.TessPageIterator handle, IntBuffer orientation,
            IntBuffer writing_direction, IntBuffer textline_order, FloatBuffer deskew_angle);

    /**
     * Deletes the specified ResultIterator handle.
     *
     * @param handle the TessResultIterator instance
     */
    public static native void TessResultIteratorDelete(TessAPI1.TessResultIterator handle);

    /**
     * Creates a copy of the specified ResultIterator instance.
     *
     * @param handle the TessResultIterator instance
     * @return the copy object
     */
    public static native TessAPI1.TessResultIterator TessResultIteratorCopy(TessAPI1.TessResultIterator handle);

    /**
     * Gets the PageIterator of the specified ResultIterator instance.
     *
     * @param handle the TessResultIterator instance
     * @return the page iterator
     */
    public static native TessAPI1.TessPageIterator TessResultIteratorGetPageIterator(TessAPI1.TessResultIterator handle);

    /**
     * Gets the PageIterator of the specified ResultIterator instance.
     *
     * @param handle the TessResultIterator instance
     * @return the page iterator constant
     */
    public static native TessAPI1.TessPageIterator TessResultIteratorGetPageIteratorConst(
            TessAPI1.TessResultIterator handle);

    public static native int TessResultIteratorNext(TessAPI1.TessResultIterator handle, int level);

    /**
     * Returns the null terminated UTF-8 encoded text string for the current
     * object at the given level. Use delete [] to free after use.
     *
     * @param handle the TessResultIterator instance
     * @param level tesseract page level
     * @return the pointer to recognized text
     */
    public static native Pointer TessResultIteratorGetUTF8Text(TessAPI1.TessResultIterator handle, int level);

    /**
     * Returns the mean confidence of the current object at the given level. The
     * number should be interpreted as a percent probability (0.0f-100.0f).
     *
     * @param handle the TessResultIterator instance
     * @param level tesseract page level
     * @return confidence value
     */
    public static native float TessResultIteratorConfidence(TessAPI1.TessResultIterator handle, int level);

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
    public static native String TessResultIteratorWordFontAttributes(TessAPI1.TessResultIterator handle,
            IntBuffer is_bold, IntBuffer is_italic, IntBuffer is_underlined, IntBuffer is_monospace,
            IntBuffer is_serif, IntBuffer is_smallcaps, IntBuffer pointsize, IntBuffer font_id);

    /**
     * Returns TRUE if the current word was found in a dictionary.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if word is from dictionary
     */
    public static native int TessResultIteratorWordIsFromDictionary(TessAPI1.TessResultIterator handle);

    /**
     * Returns TRUE if the current word is numeric.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if word is numeric
     */
    public static native int TessResultIteratorWordIsNumeric(TessAPI1.TessResultIterator handle);

    /**
     * Returns TRUE if the current symbol is a superscript. If iterating at a
     * higher level object than symbols, e.g., words, then this will return the
     * attributes of the first symbol in that word.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if symbol is superscript
     */
    public static native int TessResultIteratorSymbolIsSuperscript(TessAPI1.TessResultIterator handle);

    /**
     * Returns TRUE if the current symbol is a subscript. If iterating at a
     * higher level object than symbols, e.g., words, then this will return the
     * attributes of the first symbol in that word.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if symbol is subscript
     */
    public static native int TessResultIteratorSymbolIsSubscript(TessAPI1.TessResultIterator handle);

    /**
     * Returns TRUE if the current symbol is a dropcap. If iterating at a higher
     * level object than symbols, e.g., words, then this will return the
     * attributes of the first symbol in that word.
     *
     * @param handle the TessResultIterator instance
     * @return 1 if symbol is dropcap
     */
    public static native int TessResultIteratorSymbolIsDropcap(TessAPI1.TessResultIterator handle);

    /* Choice iterator */
    public static native TessAPI1.TessChoiceIterator TessResultIteratorGetChoiceIterator(TessAPI1.TessResultIterator handle);

    public static native void TessChoiceIteratorDelete(TessAPI1.TessChoiceIterator handle);

    public static native int TessChoiceIteratorNext(TessAPI1.TessChoiceIterator handle);

    public static native String TessChoiceIteratorGetUTF8Text(TessAPI1.TessChoiceIterator handle);

    public static native float TessChoiceIteratorConfidence(TessAPI1.TessChoiceIterator handle);

    /**
     * Base class for all tesseract APIs. Specific classes can add ability to
     * work on different inputs or produce different outputs. This class is
     * mostly an interface layer on top of the Tesseract instance class to hide
     * the data types so that users of this class don't have to include any
     * other Tesseract headers.
     */
    public static class TessBaseAPI extends PointerType {

        public TessBaseAPI(Pointer address) {
            super(address);
        }

        public TessBaseAPI() {
            super();
        }
    };

    /**
     * Description of the output of the OCR engine. This structure is used as
     * both a progress monitor and the final output header, since it needs to be
     * a valid progress monitor while the OCR engine is storing its output to
     * shared memory. During progress, all the buffer info is -1. Progress
     * starts at 0 and increases to 100 during OCR. No other constraint. Every
     * progress callback, the OCR engine must set <code>ocr_alive</code> to 1.
     * The HP side will set <code>ocr_alive</code> to 0. Repeated failure to
     * reset to 1 indicates that the OCR engine is dead. If the cancel function
     * is not null then it is called with the number of user words found. If it
     * returns true then operation is cancelled.
     */
    public static class ETEXT_DESC extends PointerType {

        public ETEXT_DESC(Pointer address) {
            super(address);
        }

        public ETEXT_DESC() {
            super();
        }
    };

    /**
     * Class to iterate over tesseract page structure, providing access to all
     * levels of the page hierarchy, without including any tesseract headers or
     * having to handle any tesseract structures.<br>
     * WARNING! This class points to data held within the TessBaseAPI class, and
     * therefore can only be used while the TessBaseAPI class still exists and
     * has not been subjected to a call of <code>Init</code>,
     * <code>SetImage</code>, <code>Recognize</code>, <code>Clear</code>,
     * <code>End</code> <code>DetectOS</code>, or anything else that changes the
     * internal <code>PAGE_RES</code>. See <code>apitypes.h</code> for the
     * definition of <code>PageIteratorLevel</code>. See also
     * <code>ResultIterator</code>, derived from <code>PageIterator</code>,
     * which adds in the ability to access OCR output with text-specific
     * methods.
     */
    public static class TessPageIterator extends PointerType {

        public TessPageIterator(Pointer address) {
            super(address);
        }

        public TessPageIterator() {
            super();
        }
    };

    /**
     * MutableIterator adds access to internal data structures.
     */
    public static class TessMutableIterator extends PointerType {

        public TessMutableIterator(Pointer address) {
            super(address);
        }

        public TessMutableIterator() {
            super();
        }
    };

    /**
     * Iterator for tesseract results that is capable of iterating in proper
     * reading order over Bi Directional (e.g. mixed Hebrew and English) text.
     * ResultIterator adds text-specific methods for access to OCR output.
     */
    public static class TessResultIterator extends PointerType {

        public TessResultIterator(Pointer address) {
            super(address);
        }

        public TessResultIterator() {
            super();
        }
    };

    public static class TessChoiceIterator extends PointerType {

        public TessChoiceIterator(Pointer address) {
            super(address);
        }

        public TessChoiceIterator() {
            super();
        }
    };

    /**
     * Interface for rendering tesseract results into a document, such as text,
     * HOCR or pdf. This class is abstract. Specific classes handle individual
     * formats. This interface is then used to inject the renderer class into
     * tesseract when processing images.
     *
     * For simplicity implementing this with tesseract version 3.01, the
     * renderer contains document state that is cleared from document to
     * document just as the TessBaseAPI is. This way the base API can just
     * delegate its rendering functionality to injected renderers, and the
     * renderers can manage the associated state needed for the specific formats
     * in addition to the heuristics for producing it.
     */
    public static class TessResultRenderer extends PointerType {

        public TessResultRenderer(Pointer address) {
            super(address);
        }

        public TessResultRenderer() {
            super();
        }
    };
}
