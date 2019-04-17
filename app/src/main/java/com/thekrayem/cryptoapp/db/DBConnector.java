package com.thekrayem.cryptoapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Mhamad on 17/01/2017.
 */

public class DBConnector {

    private static DBConnector instance;

    public final SQLiteDatabase db;
    public final DaoMaster daoMaster;
    public final DaoSession daoSession;

    public final fileDao file;
    public final chat_userDao chat_user;
    public final chat_messageDao chat_message;

    public static DBConnector getInstance(Context app, String DB_NAME) {
        if (instance == null){
            instance = new DBConnector(app, DB_NAME);
        }
        return instance;
    }

    public static void terminateInstance() {
        instance = null;

    }

    private DBConnector(Context app, String DB_NAME) {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(app, DB_NAME, null);

        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        file = daoSession.getFileDao();
        chat_user = daoSession.getChat_userDao();
        chat_message = daoSession.getChat_messageDao();
    }
}
