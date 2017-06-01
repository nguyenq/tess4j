/*
 * Copyright @ 2017 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.tess4j;

import com.ochafik.lang.jnaerator.runtime.NativeSize;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.sourceforge.lept4j.Boxa;
import net.sourceforge.lept4j.Pix;

public class TessAPIImpl implements TessAPI {

    public TessAPI getInstance() {
        return TessAPI.INSTANCE;
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
    public ITessAPI.TessResultRenderer TessTextRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessResultRenderer TessHOcrRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessHOcrRendererCreate2(String outputbase, int font_info) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessResultRenderer TessPDFRendererCreate(String outputbase, String datadir) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessPDFRendererCreateTextonly(String outputbase, String datadir, int textonly) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessResultRenderer TessUnlvRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessResultRenderer TessBoxTextRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessDeleteResultRenderer(ITessAPI.TessResultRenderer renderer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessResultRendererInsert(ITessAPI.TessResultRenderer renderer, ITessAPI.TessResultRenderer next) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessResultRenderer TessResultRendererNext(ITessAPI.TessResultRenderer renderer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultRendererBeginDocument(ITessAPI.TessResultRenderer renderer, String title) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultRendererAddImage(ITessAPI.TessResultRenderer renderer, PointerByReference api) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultRendererEndDocument(ITessAPI.TessResultRenderer renderer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessResultRendererExtention(ITessAPI.TessResultRenderer renderer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessResultRendererTitle(ITessAPI.TessResultRenderer renderer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultRendererImageNum(ITessAPI.TessResultRenderer renderer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessBaseAPI TessBaseAPICreate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIDelete(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPISetInputName(ITessAPI.TessBaseAPI handle, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String TessBaseAPIGetInputName(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPISetInputImage(ITessAPI.TessBaseAPI handle, Pix pix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pix TessBaseAPIGetInputImage(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIGetSourceYResolution(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String TessBaseAPIGetDatapath(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPISetOutputName(ITessAPI.TessBaseAPI handle, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPISetVariable(ITessAPI.TessBaseAPI handle, String name, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIGetIntVariable(ITessAPI.TessBaseAPI handle, String name, IntBuffer value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIGetBoolVariable(ITessAPI.TessBaseAPI handle, String name, IntBuffer value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIGetDoubleVariable(ITessAPI.TessBaseAPI handle, String name, DoubleBuffer value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String TessBaseAPIGetStringVariable(ITessAPI.TessBaseAPI handle, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIPrintVariablesToFile(ITessAPI.TessBaseAPI handle, String filename) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIInit1(ITessAPI.TessBaseAPI handle, String datapath, String language, int oem, PointerByReference configs, int configs_size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIInit2(ITessAPI.TessBaseAPI handle, String datapath, String language, int oem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIInit3(ITessAPI.TessBaseAPI handle, String datapath, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIInit4(ITessAPI.TessBaseAPI handle, String datapath, String language, int oem, PointerByReference configs, int configs_size, PointerByReference vars_vec, PointerByReference vars_values, NativeSize vars_vec_size, int set_only_non_debug_params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String TessBaseAPIGetInitLanguagesAsString(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PointerByReference TessBaseAPIGetLoadedLanguagesAsVector(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PointerByReference TessBaseAPIGetAvailableLanguagesAsVector(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIInitLangMod(ITessAPI.TessBaseAPI handle, String datapath, String language) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIInitForAnalysePage(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIReadConfigFile(ITessAPI.TessBaseAPI handle, String filename, int init_only) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPISetPageSegMode(ITessAPI.TessBaseAPI handle, int mode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIGetPageSegMode(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIRect(ITessAPI.TessBaseAPI handle, ByteBuffer imagedata, int bytes_per_pixel, int bytes_per_line, int left, int top, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIClearAdaptiveClassifier(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPISetImage(ITessAPI.TessBaseAPI handle, ByteBuffer imagedata, int width, int height, int bytes_per_pixel, int bytes_per_line) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPISetImage2(ITessAPI.TessBaseAPI handle, Pix pix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPISetSourceResolution(ITessAPI.TessBaseAPI handle, int ppi) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPISetRectangle(ITessAPI.TessBaseAPI handle, int left, int top, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pix TessBaseAPIGetThresholdedImage(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boxa TessBaseAPIGetRegions(ITessAPI.TessBaseAPI handle, PointerByReference pixa) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boxa TessBaseAPIGetTextlines(ITessAPI.TessBaseAPI handle, PointerByReference pixa, PointerByReference blockids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boxa TessBaseAPIGetTextlines1(ITessAPI.TessBaseAPI handle, int raw_image, int raw_padding, PointerByReference pixa, PointerByReference blockids, PointerByReference paraids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boxa TessBaseAPIGetStrips(ITessAPI.TessBaseAPI handle, PointerByReference pixa, PointerByReference blockids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boxa TessBaseAPIGetWords(ITessAPI.TessBaseAPI handle, PointerByReference pixa) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boxa TessBaseAPIGetConnectedComponents(ITessAPI.TessBaseAPI handle, PointerByReference cc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boxa TessBaseAPIGetComponentImages(ITessAPI.TessBaseAPI handle, int level, int text_only, PointerByReference pixa, PointerByReference blockids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boxa TessBaseAPIGetComponentImages1(ITessAPI.TessBaseAPI handle, int level, int text_only, int raw_image, int raw_padding, PointerByReference pixa, PointerByReference blockids, PointerByReference paraids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIGetThresholdedImageScaleFactor(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIDumpPGM(ITessAPI.TessBaseAPI handle, String filename) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessPageIterator TessBaseAPIAnalyseLayout(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIRecognize(ITessAPI.TessBaseAPI handle, ITessAPI.ETEXT_DESC monitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIRecognizeForChopTest(ITessAPI.TessBaseAPI handle, ITessAPI.ETEXT_DESC monitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessResultIterator TessBaseAPIGetIterator(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessMutableIterator TessBaseAPIGetMutableIterator(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIProcessPages(ITessAPI.TessBaseAPI handle, String filename, String retry_config, int timeout_millisec, ITessAPI.TessResultRenderer renderer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIProcessPage(ITessAPI.TessBaseAPI handle, Pix pix, int page_index, String filename, String retry_config, int timeout_millisec, ITessAPI.TessResultRenderer renderer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetUTF8Text(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetHOCRText(ITessAPI.TessBaseAPI handle, int page_number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetBoxText(ITessAPI.TessBaseAPI handle, int page_number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetUNLVText(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIMeanTextConf(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IntByReference TessBaseAPIAllWordConfidences(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIAdaptToWordStr(ITessAPI.TessBaseAPI handle, int mode, String wordstr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIClear(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIEnd(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIIsValidWord(ITessAPI.TessBaseAPI handle, String word) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIGetTextDirection(ITessAPI.TessBaseAPI handle, IntBuffer out_offset, FloatBuffer out_slope) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessBaseAPIClearPersistentCache(ITessAPI.TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIDetectOrientationScript(TessBaseAPI handle, IntBuffer orient_deg, FloatBuffer orient_conf, PointerByReference script_name, FloatBuffer script_conf) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String TessBaseAPIGetUnichar(ITessAPI.TessBaseAPI handle, int unichar_id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessPageIteratorDelete(ITessAPI.TessPageIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessPageIterator TessPageIteratorCopy(ITessAPI.TessPageIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessPageIteratorBegin(ITessAPI.TessPageIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessPageIteratorNext(ITessAPI.TessPageIterator handle, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessPageIteratorIsAtBeginningOf(ITessAPI.TessPageIterator handle, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessPageIteratorIsAtFinalElement(ITessAPI.TessPageIterator handle, int level, int element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessPageIteratorBoundingBox(ITessAPI.TessPageIterator handle, int level, IntBuffer left, IntBuffer top, IntBuffer right, IntBuffer bottom) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessPageIteratorBlockType(ITessAPI.TessPageIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pix TessPageIteratorGetBinaryImage(ITessAPI.TessPageIterator handle, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pix TessPageIteratorGetImage(ITessAPI.TessPageIterator handle, int level, int padding, Pix original_image, IntBuffer left, IntBuffer top) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessPageIteratorBaseline(ITessAPI.TessPageIterator handle, int level, IntBuffer x1, IntBuffer y1, IntBuffer x2, IntBuffer y2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessPageIteratorOrientation(ITessAPI.TessPageIterator handle, IntBuffer orientation, IntBuffer writing_direction, IntBuffer textline_order, FloatBuffer deskew_angle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessPageIteratorParagraphInfo(ITessAPI.TessPageIterator handle, IntBuffer justification, IntBuffer is_list_item, IntBuffer is_crown, IntBuffer first_line_indent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessResultIteratorDelete(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessResultIterator TessResultIteratorCopy(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessPageIterator TessResultIteratorGetPageIterator(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessPageIterator TessResultIteratorGetPageIteratorConst(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultIteratorNext(ITessAPI.TessResultIterator handle, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessResultIteratorGetUTF8Text(ITessAPI.TessResultIterator handle, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float TessResultIteratorConfidence(ITessAPI.TessResultIterator handle, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String TessResultIteratorWordRecognitionLanguage(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String TessResultIteratorWordFontAttributes(ITessAPI.TessResultIterator handle, IntBuffer is_bold, IntBuffer is_italic, IntBuffer is_underlined, IntBuffer is_monospace, IntBuffer is_serif, IntBuffer is_smallcaps, IntBuffer pointsize, IntBuffer font_id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultIteratorWordIsFromDictionary(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultIteratorWordIsNumeric(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultIteratorSymbolIsSuperscript(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultIteratorSymbolIsSubscript(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessResultIteratorSymbolIsDropcap(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITessAPI.TessChoiceIterator TessResultIteratorGetChoiceIterator(ITessAPI.TessResultIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessChoiceIteratorDelete(ITessAPI.TessChoiceIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessChoiceIteratorNext(ITessAPI.TessChoiceIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String TessChoiceIteratorGetUTF8Text(ITessAPI.TessChoiceIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float TessChoiceIteratorConfidence(ITessAPI.TessChoiceIterator handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
