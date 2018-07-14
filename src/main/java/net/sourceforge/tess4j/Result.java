package net.sourceforge.tess4j;

import java.util.List;

public class Result {

    private int confidence;

    private List<Word> words;

    public Result(int confidence, List<Word> words) {
        this.confidence = confidence;
        this.words = words;
    }

    public int getConfidence() {
        return confidence;
    }

    public List<Word> getWords() {
        return words;
    }

    @Override
    public String toString() {
        return "Total Confidence: "+getConfidence()+"% Words: "+getWords().toString();
    }
}
