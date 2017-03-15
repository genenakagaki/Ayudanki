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

import java.util.List;

import static com.genenakagaki.ayudanki.data.QuizContract.*;

/**
 * Created by gene on 3/3/17.
 */

public class QuizDb {

    private static final String TAG = QuizDb.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static Uri insert(Context context, String quizName, long quizSetId) throws NameAlreadyExistsException {
        if (D) Log.d(TAG, "insert");

        // check if name already exists
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    QuizEntry.CONTENT_URI,
                    QuizEntry.COLUMNS,
                    QuizEntry.COLUMN_QUIZ_SET_ID + " = ? AND " + QuizEntry.COLUMN_NAME + " = ?",
                    new String[] {String.valueOf(quizSetId), quizName},
                    null);
            if (c.moveToFirst()) {
                throw new NameAlreadyExistsException("Quiz with name '" + quizName + "' already exists.");
            }

        } finally {
            if (c != null) c.close();
        }

        ContentValues values = new ContentValues();
        values.put(QuizEntry.COLUMN_NAME, quizName);
        values.put(QuizEntry.COLUMN_QUIZ_SET_ID, quizSetId);

        return context.getContentResolver().insert(
                QuizEntry.CONTENT_URI,
                values);
    }

    public static void update(Context context, long quizId, String quizName, long quizSetId)
            throws NameAlreadyExistsException {
        if (D) Log.d(TAG, "update");

        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    QuizEntry.CONTENT_URI,
                    QuizEntry.COLUMNS,
                    QuizEntry._ID + " != ? AND " +
                            QuizEntry.COLUMN_QUIZ_SET_ID + " = ? AND " +
                            QuizEntry.COLUMN_NAME + " = ?",
                    new String[] {String.valueOf(quizId), String.valueOf(quizSetId), quizName},
                    null);

            if (c.moveToFirst()) {
                throw new NameAlreadyExistsException("Quiz with name '" + quizName + "' already exists.");
            }
        } finally {
            if (c != null) c.close();
        }

        ContentValues values = new ContentValues();
        values.put(QuizEntry.COLUMN_NAME, quizName);
        values.put(QuizEntry.COLUMN_QUIZ_SET_ID, quizSetId);

        context.getContentResolver().update(
                QuizEntry.CONTENT_URI,
                values,
                QuizEntry._ID + " = ?",
                new String[] {String.valueOf(quizId)});
    }

    public static void delete(Context context, long quizId) {
        // delete all Cards under quiz
        context.getContentResolver().delete(
                CardEntry.CONTENT_URI,
                CardEntry.COLUMN_QUIZ_ID + " = ?",
                new String[] {String.valueOf(quizId)});

        // delete card
        context.getContentResolver().delete(
                QuizEntry.CONTENT_URI,
                QuizEntry._ID + " = ?",
                new String[] {String.valueOf(quizId)});

    }

    public static long getCurrentId(Context context) throws PreferenceNotFound {
        Uri currentQuizUri = AyudankiPreferences.getCurrentQuiz(context);

        Cursor currentQuizCursor = null;
        long id = -1;
        try {
            currentQuizCursor = context.getContentResolver().query(
                    currentQuizUri, QuizEntry.COLUMNS, null, null, null);
            currentQuizCursor.moveToFirst();

            id = currentQuizCursor.getLong(QuizEntry.INDEX_ID);

        } finally {
            if (currentQuizCursor != null) currentQuizCursor.close();
        }

        return id;
    }

    public static String getCurrentName(Context context) throws PreferenceNotFound {
        Uri currentQuizUri = AyudankiPreferences.getCurrentQuiz(context);

        Cursor currentQuizCursor = null;
        try {
            currentQuizCursor = context.getContentResolver().query(
                    currentQuizUri, QuizEntry.COLUMNS, null, null, null);
            currentQuizCursor.moveToFirst();

            return currentQuizCursor.getString(QuizEntry.INDEX_NAME);
        } finally {
            if (currentQuizCursor != null) currentQuizCursor.close();
        }

    }

    public static int getCurrentCardCount(Context context) throws PreferenceNotFound {
        Uri currentQuizUri = AyudankiPreferences.getCurrentQuiz(context);

        Cursor currentQuizCursor = null;
        Cursor cardCursor = null;
        try {
            currentQuizCursor = context.getContentResolver().query(
                    currentQuizUri, QuizEntry.COLUMNS, null, null, null);
            currentQuizCursor.moveToFirst();

            long quizId = currentQuizCursor.getLong(QuizEntry.INDEX_ID);

            cardCursor = context.getContentResolver().query(
                    CardEntry.CONTENT_URI,
                    CardEntry.COLUMNS,
                    CardEntry.COLUMN_QUIZ_ID + " = ?",
                    new String[] {String.valueOf(quizId)},
                    null);
            return cardCursor.getCount();

        } finally {
            if (currentQuizCursor != null) currentQuizCursor.close();
            if (cardCursor != null) cardCursor.close();
        }

    }
}
