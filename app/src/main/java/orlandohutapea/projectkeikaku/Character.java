package orlandohutapea.projectkeikaku;


import java.util.HashMap;

public class Character {
    static HashMap<String, Character> map = null;
    static Character[] array = null;

    static class Reading {
        String reading;
        String type;
        boolean isCommon;

        Reading(String type, String reading, boolean isCommon) {
            this.type = type;
            this.reading = reading;
            this.isCommon = isCommon;
        }
    }

    private String literal;
    private Reading reading[];
    private String meaning[];

    Character(String literal, Reading reading[], String meaning[]) {
        this.literal = literal;
        this.reading = reading;
        this.meaning = meaning;
    }

    public String getLiteral() { return this.literal; }

    public Reading[] getReadings() { return this.reading; }

    public String[] getMeanings() { return this.meaning; }
}