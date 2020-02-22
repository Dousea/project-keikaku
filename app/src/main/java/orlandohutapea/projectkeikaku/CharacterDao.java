package orlandohutapea.projectkeikaku;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CharacterDao {
    @Query("DELETE FROM chars")
    void nukeTable();
    @Query("SELECT * FROM chars")
    List<CharacterEntity> getAll();
    @Query("SELECT * FROM chars WHERE box > 0")
    List<CharacterEntity> getNeedReviews();
    @Query("SELECT * FROM chars WHERE literal LIKE :literal")
    CharacterEntity findByLiteral(String literal);
    @Query("SELECT COUNT(*) FROM chars")
    int length();
    @Insert
    void insert(CharacterEntity entity);
    @Update
    void update(CharacterEntity entity);
    @Delete
    void delete(CharacterEntity entity);
}
