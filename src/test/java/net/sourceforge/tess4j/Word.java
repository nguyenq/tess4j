package net.sourceforge.tess4j;

import java.awt.Rectangle;

/**
 * Encapsulates Tesseract results.
 */
class Word {

    private final String text;
    private final float confidence;
    private final Rectangle rect;

    public Word(String text, float confidence, Rectangle rect) {
        this.text = text;
        this.confidence = confidence;
        this.rect = rect;
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
    public Rectangle getRect() {
        return rect;
    }

    @Override
    public String toString() {
        return String.format("%s\t[Confidence: %f Bounding box: %d %d %d %d]", text, confidence, rect.x, rect.y, rect.width, rect.height);
    }
}
