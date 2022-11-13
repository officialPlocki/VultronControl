package de.plocki.ai.objects;

public class CategoryObjectResult {

    private String words;
    private SupportType type;

    public CategoryObjectResult(String words, SupportType type) {
        this.words = words;
        this.type = type;
    }

    public String getWords() {
        return words;
    }

    public SupportType getType() {
        return type;
    }
}
