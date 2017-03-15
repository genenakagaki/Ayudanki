package com.genenakagaki.ayudanki.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gene on 2/9/17.
 */

public class QuizDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "ayudanki.db";

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_QUIZ_SET_TABLE = "CREATE TABLE " + QuizContract.QuizSetEntry.TABLE_NAME +
                " (" +
                QuizContract.QuizSetEntry._ID + " INTEGER PRIMARY KEY, " +
                QuizContract.QuizSetEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL " +
                " );";

        final String SQL_CREATE_QUIZ_TABLE = "CREATE TABLE " + QuizContract.QuizEntry.TABLE_NAME +
                " (" +
                QuizContract.QuizEntry._ID + " INTEGER PRIMARY KEY, " +
                QuizContract.QuizEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                QuizContract.QuizEntry.COLUMN_QUIZ_SET_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + QuizContract.QuizEntry.COLUMN_QUIZ_SET_ID + ") REFERENCES " +
                QuizContract.QuizSetEntry.TABLE_NAME + " (" + QuizContract.QuizSetEntry._ID + ") " +
                " );";

        final String SQL_CREATE_CARD_TABLE = "CREATE TABLE " + QuizContract.CardEntry.TABLE_NAME +
                " (" +
                QuizContract.CardEntry._ID + " INTEGER PRIMARY KEY, " +
                QuizContract.CardEntry.COLUMN_QUIZ_ID + " INTEGER NOT NULL, " +
                QuizContract.CardEntry.COLUMN_TERM + " TEXT NOT NULL, " +
                QuizContract.CardEntry.COLUMN_DEFINITION + " TEXT NOT NULL, " +
                QuizContract.CardEntry.COLUMN_POINTS + " INTEGER NOT NULL, " +
                QuizContract.CardEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + QuizContract.CardEntry.COLUMN_QUIZ_ID + ") REFERENCES " +
                QuizContract.QuizEntry.TABLE_NAME + " (" + QuizContract.QuizEntry._ID + ") " +
                " );";

        db.execSQL(SQL_CREATE_QUIZ_SET_TABLE);
        db.execSQL(SQL_CREATE_QUIZ_TABLE);
        db.execSQL(SQL_CREATE_CARD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuizSetEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuizEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.CardEntry.TABLE_NAME);
        onCreate(db);
    }
}
