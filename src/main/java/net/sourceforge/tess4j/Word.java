/**
 * Copyright @ 2015 Quan Nguyen
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

/**
 * Encapsulates Tesseract OCR results.
 */
public class Word {

    private final String text;
    private final float confidence;
    private final Rectangle rect;

    /**
     * Constructor.
     * 
     * @param text
     * @param confidence
     * @param boundingBox 
     */
    public Word(String text, float confidence, Rectangle boundingBox) {
        this.text = text;
        this.confidence = confidence;
        this.rect = boundingBox;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the confidence
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * @return the bounding box
     */
    public Rectangle getBoundingBox() {
        return rect;
    }

    @Override
    public String toString() {
        return String.format("%s [Confidence: %f Bounding box: %d %d %d %d]", text, confidence, rect.x, rect.y, rect.width, rect.height);
    }
}
