package ru.xbitly.wallet.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardDao {

    @Query("SELECT * FROM card")
    List<Card> getAll();

    @Insert
    void insert(Card card);

    @Delete
    void delete(Card card);

    @Update
    void update(Card card);

}