package com.maffin.recipes;

import android.app.Application;
import android.util.Log;

import androidx.room.Room;

import com.maffin.recipes.db.AppDatabase;

public class App extends Application {
    public static App instance;

    private AppDatabase database;

    @Override
    public void onCreate() {
        Log.d("APP", "start onCreate");
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "maffin.db")
                .allowMainThreadQueries()
                .build();
        Log.d("APP", "finish onCreate");
    }

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
