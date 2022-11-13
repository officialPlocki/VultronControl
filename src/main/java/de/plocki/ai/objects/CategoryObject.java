package de.plocki.ai.objects;

public class CategoryObject {

    private final String text;

    private final String type;

    private final String objectType;

    public CategoryObject(String text, SupportType type) {
        this.text = text;
        this.type = type.name();
        objectType = "category";
    }

    public String getText() {
        return text;
    }

    public SupportType getType() {
        return SupportType.valueOf(type);
    }
}
