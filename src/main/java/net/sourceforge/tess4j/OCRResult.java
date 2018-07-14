/**
 * Copyright @ 2018 Quan Nguyen
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

import java.util.List;

/**
 * Encapsulates Tesseract OCR results at file level.
 */
public class OCRResult {

    private final int confidence;

    private final List<Word> words;

    /**
     * Constructor.
     *
     * @param confidence average text confidence
     * @param words recognized words
     */
    public OCRResult(int confidence, List<Word> words) {
        this.confidence = confidence;
        this.words = words;
    }

    /**
     *
     * @return the average text confidence
     */
    public int getConfidence() {
        return confidence;
    }

    /**
     *
     * @return the recognized words
     */
    public List<Word> getWords() {
        return words;
    }

    @Override
    public String toString() {
        return "Average Text Confidence: " + getConfidence() + "% Words: " + getWords().toString();
    }
}
