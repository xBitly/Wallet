package ru.xbitly.wallet.Database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private static ru.xbitly.wallet.Database.DatabaseClient mInstance;
    private final AppDatabase appDatabase;

    private DatabaseClient(Context mCtx) {
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "Cards").build();
    }

    public static synchronized ru.xbitly.wallet.Database.DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new ru.xbitly.wallet.Database.DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
