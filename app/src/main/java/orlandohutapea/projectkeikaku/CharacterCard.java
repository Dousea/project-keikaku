package orlandohutapea.projectkeikaku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CharacterCard {
    static HashMap<String, CharacterCard> map = null;
    static List<CharacterCard>           list = null;

    static final int LESSON                   = 0x0000;
    static final int REVIEW_READING           = 0x1000;
    static final int REVIEW_READING_CORRECT   = 0x1010;
    static final int REVIEW_READING_INCORRECT = 0x1011;
    static final int REVIEW_MEANING           = 0x1100;
    static final int REVIEW_MEANING_CORRECT   = 0x1110;
    static final int REVIEW_MEANING_INCORRECT = 0x1111;

    int type;
    Character character;

    CharacterCard(int type, Character character) {
        this.type = type;
        this.character = character;
    }

    static void initialize() {
        try {
            if (map == null && list == null) {
                map = new HashMap<>();
                list = new ArrayList<>();
            } else
                throw new Exception("'map' or 'list' is not null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void insert(CharacterCard card, String literal) {
        try {
            if (list != null && map != null) {
                if (!list.contains(card))
                    list.add(card);
                else
                    throw new Exception("it already contains the 'card' in the list");

                if (!map.containsKey(literal))
                    map.put(literal, card);
                else
                    throw new Exception("it already contains the 'card' in the map");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
