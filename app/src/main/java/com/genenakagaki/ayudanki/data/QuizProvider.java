package com.genenakagaki.ayudanki.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by gene on 2/9/17.
 */

public class QuizProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int CARD = 100;
    static final int CARD_ID = 101;
    static final int QUIZ = 200;
    static final int QUIZ_ID = 201;
    static final int QUIZ_SET = 300;
    static final int QUIZ_SET_ID = 301;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = QuizContract.CONTENT_AUTHORITY;

        // for each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, QuizContract.PATH_CARD, CARD);
        matcher.addURI(authority, QuizContract.PATH_CARD_ID, CARD_ID);
        matcher.addURI(authority, QuizContract.PATH_QUIZ, QUIZ);
        matcher.addURI(authority, QuizContract.PATH_QUIZ_ID, QUIZ_ID);
        matcher.addURI(authority, QuizContract.PATH_QUIZ_SET, QUIZ_SET);
        matcher.addURI(authority, QuizContract.PATH_QUIZ_SET_ID, QUIZ_SET_ID);

        return matcher;
    }

    private QuizDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new QuizDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CARD:
                cursor = mOpenHelper.getReadableDatabase().query(
                        QuizContract.CardEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CARD_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        QuizContract.CardEntry.TABLE_NAME,
                        projection,
                        QuizContract.CardEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            case QUIZ:
                cursor = mOpenHelper.getReadableDatabase().query(
                        QuizContract.QuizEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case QUIZ_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        QuizContract.QuizEntry.TABLE_NAME,
                        projection,
                        QuizContract.QuizEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            case QUIZ_SET:
                cursor = mOpenHelper.getReadableDatabase().query(
                        QuizContract.QuizSetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case QUIZ_SET_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        QuizContract.QuizSetEntry.TABLE_NAME,
                        projection,
                        QuizContract.QuizSetEntry._ID + " = ?",
                        new String[] {uri.getPathSegments().get(1)},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CARD:
                return QuizContract.CardEntry.CONTENT_TYPE;
            case QUIZ:
                return QuizContract.QuizEntry.CONTENT_TYPE;
            case QUIZ_SET:
                return QuizContract.QuizSetEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id;

        switch (sUriMatcher.match(uri)) {
            case CARD:
                _id = db.insert(QuizContract.CardEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = QuizContract.CardEntry.buildCardUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case QUIZ:
                _id = db.insert(QuizContract.QuizEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = QuizContract.QuizEntry.buildQuizUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case QUIZ_SET:
                _id = db.insert(QuizContract.QuizSetEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = QuizContract.QuizSetEntry.buildQuizSetUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;

        // a null deletes all rows
        if (selection == null) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case CARD:
                rowsDeleted = db.delete(
                        QuizContract.CardEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case QUIZ:
                rowsDeleted = db.delete(
                        QuizContract.QuizEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case QUIZ_SET:
                rowsDeleted = db.delete(
                        QuizContract.QuizSetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CARD:
                rowsUpdated = db.update(
                        QuizContract.CardEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case QUIZ:
                rowsUpdated = db.update(
                        QuizContract.QuizEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case QUIZ_SET:
                rowsUpdated = db.update(
                        QuizContract.QuizSetEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
