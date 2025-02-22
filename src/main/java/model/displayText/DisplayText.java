package model.displayText;

public class DisplayText {
    private String displayText;
    private int value;

    public DisplayText(String displayText, int value) {
        this.displayText = displayText;
        this.value = value;
    }

    @Override
    public String toString() {
        return displayText;
    }

    public int getValue() {
        return value;
    }
}
