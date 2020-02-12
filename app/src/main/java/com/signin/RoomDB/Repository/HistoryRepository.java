package com.signin.RoomDB.Repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import com.signin.RoomDB.AppDatabase;
import com.signin.RoomDB.Model.ProgressHistory;

import java.util.List;


public class HistoryRepository {

    private AppDatabase appDatabase;

    public HistoryRepository(Context context) {
        appDatabase = AppDatabase.getAppDatabase(context);
    }

    public void insertTask(final ProgressHistory note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.daoAccess().insertHistory(note);
                return null;
            }
        }.execute();
    }

    public void updateTask(final double progress, final int month, final int day) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.daoAccess().updateHistory(progress,month,day);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final ProgressHistory note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                appDatabase.daoAccess().deleteHistory(note);
                return null;
            }
        }.execute();
    }

    public boolean searcTask(final int month, final int day) {
        final ProgressHistory note = appDatabase.daoAccess().searchHistory(month,day);
        return note != null;
    }

    public LiveData<ProgressHistory> getTask(int id) {
        return appDatabase.daoAccess().getHistory(id);
    }

    public LiveData<List<ProgressHistory>> getTasks() {
        return appDatabase.daoAccess().fetchAllHistory();
    }

    public LiveData<List<ProgressHistory>> getTasksMonthWise(int month) {
        return appDatabase.daoAccess().fetchMonthHistory(month);
    }
}