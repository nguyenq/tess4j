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

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/**
 * An interface represents common TessAPI classes/constants.
 */
public interface ITessAPI {

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
         * Specify this mode when calling init_*(), to indicate that any of the
         * above modes should be automatically inferred from the variables in
         * the language-specific config, command-line configs, or if not
         * specified in any of the above should be set to the default
         * OEM_TESSERACT_ONLY.
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
     * </pre>
     * Orientation Example:
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
     * <br>
     * If you orient your head so that "up" aligns with Orientation, then the 
     * characters will appear "right side up" and readable.<br>
     * <br>
     * In the example above, both the English and Chinese paragraphs are
     * oriented so their "up" is the top of the page (page up). The photo credit
     * is read with one's head turned leftward ("up" is to page left).<br>
     * <br> The values of this enum match the convention of Tesseract's
     * osdetect.h
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

    public static class TessBaseAPI extends PointerType {

        public TessBaseAPI(Pointer address) {
            super(address);
        }

        public TessBaseAPI() {
            super();
        }
    };

    public static class ETEXT_DESC extends PointerType {

        public ETEXT_DESC(Pointer address) {
            super(address);
        }

        public ETEXT_DESC() {
            super();
        }
    };

    public static class TessPageIterator extends PointerType {

        public TessPageIterator(Pointer address) {
            super(address);
        }

        public TessPageIterator() {
            super();
        }
    };

    public static class TessMutableIterator extends PointerType {

        public TessMutableIterator(Pointer address) {
            super(address);
        }

        public TessMutableIterator() {
            super();
        }
    };

    public static class TessResultIterator extends PointerType {

        public TessResultIterator(Pointer address) {
            super(address);
        }

        public TessResultIterator() {
            super();
        }
    };
}
