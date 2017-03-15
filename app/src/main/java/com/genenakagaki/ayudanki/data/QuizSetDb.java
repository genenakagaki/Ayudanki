package com.genenakagaki.ayudanki.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.exception.NameAlreadyExistsException;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;

import static android.R.attr.name;
import static com.genenakagaki.ayudanki.data.QuizContract.*;

/**
 * Created by gene on 3/14/17.
 */

public class QuizSetDb {

    private static final String TAG = QuizSetDb.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static void insertDefault(Context context) {
        ContentValues values = new ContentValues();
        values.put(QuizSetEntry.COLUMN_NAME, context.getString(R.string.default_quiz_set_name));
        Uri uri = context.getContentResolver().insert(
                QuizSetEntry.CONTENT_URI,
                values);

        Cursor c = null;
        try {
            c = context.getContentResolver().query(uri, QuizSetEntry.COLUMNS, null, null, null);
            c.moveToFirst();

            AyudankiPreferences.setCurrentQuizSet(context, c.getLong(QuizSetEntry.INDEX_ID));
        } finally {
            if (c != null) c.close();
        }

    }

    public static Uri insert(Context context, String quizSetName) throws NameAlreadyExistsException {
        if (D) Log.d(TAG, "insertQuizSet");

        // check if name already exists
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    QuizSetEntry.CONTENT_URI,
                    QuizSetEntry.COLUMNS,
                    QuizSetEntry.COLUMN_NAME + " = ?",
                    new String[] {quizSetName},
                    null);
            if (c != null && c.moveToFirst()) {
                throw new NameAlreadyExistsException("QuizSet with name '" + quizSetName + "' already exists.");
            }

        } finally {
            if (c != null) c.close();
        }

        // NameAlreadyExistsException was not thrown
        ContentValues values = new ContentValues();
        values.put(QuizSetEntry.COLUMN_NAME, quizSetName);

        return context.getContentResolver().insert(
                QuizSetEntry.CONTENT_URI,
                values);
    }

    public static void update(Context context, String quizSetName)
            throws PreferenceNotFound, NameAlreadyExistsException {
        if (D) Log.d(TAG, "updateQuizSet");

        Uri uri = AyudankiPreferences.getCurrentQuizSet(context);

        Cursor currentQuizSetCursor = null;
        Cursor c = null;
        long quizSetId = -1;
        try {
            currentQuizSetCursor = context.getContentResolver().query(uri, QuizSetEntry.COLUMNS, null, null, null);
            currentQuizSetCursor.moveToFirst();

            quizSetId = currentQuizSetCursor.getLong(QuizSetEntry.INDEX_ID);

            c = context.getContentResolver().query(
                    QuizSetEntry.CONTENT_URI,
                    QuizSetEntry.COLUMNS,
                    QuizSetEntry._ID + " != ? AND " + QuizSetEntry.COLUMN_NAME + " = ?",
                    new String[] {String.valueOf(quizSetId), quizSetName},
                    null);
            if (c.moveToFirst()) {
                throw new NameAlreadyExistsException("QuizSet with name '" + quizSetName + "' already exists.");
            }

        } finally {
            if (currentQuizSetCursor != null) currentQuizSetCursor.close();
            if (c != null) currentQuizSetCursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(QuizSetEntry.COLUMN_NAME, quizSetName);

        context.getContentResolver().update(
                QuizSetEntry.CONTENT_URI,
                values,
                QuizSetEntry._ID + " = ?",
                new String[] {String.valueOf(quizSetId)});
    }

    public static void deleteCurrent(Context context) throws PreferenceNotFound {
        if (D) Log.d(TAG, "deleteCurrent()");

        Uri uri = AyudankiPreferences.getCurrentQuizSet(context);

        Cursor currentQuizSetCursor = null;
        Cursor quizCursor = null;
        long quizSetId = -1;
        try {
            // get QuizSet ID
            currentQuizSetCursor = context.getContentResolver().query(
                    uri, QuizSetEntry.COLUMNS, null, null, null);
            currentQuizSetCursor.moveToFirst();

            quizSetId = currentQuizSetCursor.getLong(QuizSetEntry.INDEX_ID);

            // delete cards in quizzes in QuizSet
            quizCursor = context.getContentResolver().query(
                    QuizEntry.CONTENT_URI,
                    QuizEntry.COLUMNS,
                    QuizEntry.COLUMN_QUIZ_SET_ID + " = ?",
                    new String[] {String.valueOf(quizSetId)},
                    null);

            while (quizCursor.moveToNext()) {
                long quizId = quizCursor.getLong(QuizEntry.INDEX_ID);

                context.getContentResolver().delete(
                        CardEntry.CONTENT_URI,
                        CardEntry.COLUMN_QUIZ_ID + " = ?",
                        new String[] {String.valueOf(quizId)});
            }
        } finally {
            if (currentQuizSetCursor != null) currentQuizSetCursor.close();
            if (quizCursor != null) quizCursor.close();
        }

        // delete quizzes in QuizSet
        context.getContentResolver().delete(
                QuizEntry.CONTENT_URI,
                QuizEntry.COLUMN_QUIZ_SET_ID + " = ?",
                new String[] {String.valueOf(quizSetId)});

        // delete QuizSet
        context.getContentResolver().delete(
                QuizSetEntry.CONTENT_URI,
                QuizSetEntry._ID + " = ?",
                new String[] {String.valueOf(quizSetId)});

        // set current QuizSet
        setCurrent(context);
    }

    public static void setCurrent(Context context) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    QuizSetEntry.CONTENT_URI, QuizSetEntry.COLUMNS, null, null, null);

            if (c.moveToFirst()) {
                if (D) Log.d(TAG, "current QuizSet is now " + c.getString(QuizSetEntry.INDEX_NAME));
                AyudankiPreferences.setCurrentQuizSet(context, c.getLong(QuizSetEntry.INDEX_ID));
            } else {
                if (D) Log.d(TAG, "Creating default QuizSet since there are no QuizSet in database.");
                insertDefault(context);
            }
        } finally {
            if (c != null) c.close();
        }
    }

    public static long getCurrentId(Context context) throws PreferenceNotFound {
        Uri currentQuizSetUri = AyudankiPreferences.getCurrentQuizSet(context);

        Cursor currentQuizSetCursor = null;
        long id = -1;
        try {
            currentQuizSetCursor = context.getContentResolver().query(
                    currentQuizSetUri, QuizSetEntry.COLUMNS, null, null, null);
            currentQuizSetCursor.moveToFirst();

            id = currentQuizSetCursor.getLong(QuizSetEntry.INDEX_ID);
        } finally {
            if (currentQuizSetCursor != null) currentQuizSetCursor.close();
        }

        return id;
    }

    public static String getCurrentName(Context context) throws PreferenceNotFound {
        Uri currentQuizSetUri = AyudankiPreferences.getCurrentQuizSet(context);

        Cursor currentQuizSetCursor = null;
        String name = null;
        try {
            currentQuizSetCursor = context.getContentResolver().query(
                    currentQuizSetUri, QuizSetEntry.COLUMNS, null, null, null);
            currentQuizSetCursor.moveToFirst();

            name = currentQuizSetCursor.getString(QuizSetEntry.INDEX_NAME);
        } finally {
            if (currentQuizSetCursor != null) currentQuizSetCursor.close();
        }

        return name;
    }
}
