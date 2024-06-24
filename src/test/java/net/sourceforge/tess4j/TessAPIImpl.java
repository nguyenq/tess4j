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
    public TessResultRenderer TessTextRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessHOcrRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessHOcrRendererCreate2(String outputbase, int font_info) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessAltoRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessPAGERendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessTsvRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessPDFRendererCreate(String outputbase, String datadir, int textonly) {
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
    public TessResultRenderer TessLSTMBoxRendererCreate(String outputbase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TessResultRenderer TessWordStrBoxRendererCreate(String outputbase) {
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
    public int TessBaseAPIInit5(TessBaseAPI handle, String data, int data_size, String language, int oem, PointerByReference configs, int configs_size, PointerByReference vars_vec, PointerByReference vars_values, NativeSize vars_vec_size, int set_only_non_debug_params) {
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
    public float TessBaseAPIGetGradient(TessBaseAPI handle) {
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
    public TessPageIterator TessBaseAPIAnalyseLayout(TessBaseAPI handle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessBaseAPIRecognize(TessBaseAPI handle, ETEXT_DESC monitor) {
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
    public Pointer TessBaseAPIGetAltoText(TessBaseAPI handle, int page_number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetPAGEText(TessBaseAPI handle, int page_number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetTsvText(TessBaseAPI handle, int page_number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetBoxText(TessBaseAPI handle, int page_number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetLSTMBoxText(TessBaseAPI handle, int page_number) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessBaseAPIGetWordStrBoxText(TessBaseAPI handle, int page_number) {
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
    public int TessBaseAPIDetectOrientationScript(TessBaseAPI handle, IntBuffer orient_deg, FloatBuffer orient_conf, PointerByReference script_name, FloatBuffer script_conf) {
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

    @Override
    public ETEXT_DESC TessMonitorCreate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessMonitorDelete(ETEXT_DESC monitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessMonitorSetCancelFunc(ETEXT_DESC monitor, TessCancelFunc cancelFunc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessMonitorSetCancelThis(ETEXT_DESC monitor, Pointer cancelThis) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Pointer TessMonitorGetCancelThis(ETEXT_DESC monitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessMonitorSetProgressFunc(ETEXT_DESC monitor, TessProgressFunc progressFunc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int TessMonitorGetProgress(ETEXT_DESC monitor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void TessMonitorSetDeadlineMSecs(ETEXT_DESC monitor, int deadline) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
