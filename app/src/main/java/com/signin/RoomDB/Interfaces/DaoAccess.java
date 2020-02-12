package com.signin.RoomDB.Interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.signin.RoomDB.Model.ProgressHistory;

import java.util.List;


@Dao
public interface DaoAccess {

    /*
     * History CRUD
     */

    @Insert
    Long insertHistory(ProgressHistory progressHistory);

    @Query("SELECT * FROM ProgressHistory")
    LiveData<List<ProgressHistory>> fetchAllHistory();

    @Query("SELECT * FROM ProgressHistory WHERE month =:month")
    LiveData<List<ProgressHistory>> fetchMonthHistory(int month);

    @Query("SELECT * FROM ProgressHistory WHERE id =:historyId")
    LiveData<ProgressHistory> getHistory(int historyId);

    @Query("SELECT * FROM ProgressHistory WHERE month =:month and day =:day")
    ProgressHistory searchHistory(int month, int day);

    @Query("UPDATE ProgressHistory SET progress =:progress WHERE month =:month and day =:day")
    void updateHistory(double progress, int month, int day);

    @Update
    void updateHistory(ProgressHistory progressHistory);

    @Delete
    void deleteHistory(ProgressHistory progressHistory);

}
