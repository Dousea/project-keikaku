package orlandohutapea.projectkeikaku;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {CharacterEntity.class}, version = 1)
public abstract class CharacterDatabase extends RoomDatabase {
    private static CharacterDatabase INSTANCE;

    public abstract CharacterDao characterDao();

    public static CharacterDatabase getDatabase(Context context) {
        synchronized (CharacterDatabase.class) {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        CharacterDatabase.class, "character-database")
                        .build();
        }

        return INSTANCE;
    }
}
