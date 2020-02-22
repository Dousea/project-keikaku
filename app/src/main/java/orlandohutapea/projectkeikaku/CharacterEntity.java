package orlandohutapea.projectkeikaku;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "chars")
public class CharacterEntity {
    @NonNull
    @PrimaryKey
    private String literal;
    @ColumnInfo(name = "box")
    private int box;
    // Is meaning check already correct for current box?
    @ColumnInfo(name = "meaning-correct")
    private boolean meaningCorrect;
    // Is reading check already correct for current box?
    @ColumnInfo(name = "reading-correct")
    private boolean readingCorrect;
    @ColumnInfo(name = "interval")
    private long interval;

    @NonNull
    public String getLiteral() {
        return literal;
    }

    public void setLiteral(@NonNull String literal) {
        this.literal = literal;
    }

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public boolean isMeaningCorrect() {
        return meaningCorrect;
    }

    public void setMeaningCorrect(boolean meaningCorrect) {
        this.meaningCorrect = meaningCorrect;
    }

    public boolean isReadingCorrect() {
        return readingCorrect;
    }

    public void setReadingCorrect(boolean readingCorrect) {
        this.readingCorrect = readingCorrect;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public CharacterEntity copy() {
        CharacterEntity entity = new CharacterEntity();
        entity.setLiteral(entity.getLiteral());
        entity.setBox(entity.getBox());
        entity.setInterval(entity.getInterval());
        entity.setMeaningCorrect(entity.isMeaningCorrect());
        entity.setReadingCorrect(entity.isReadingCorrect());
        return entity;
    }
}
