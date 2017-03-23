package com.genenakagaki.ayudanki.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.data.model.Card;
import com.genenakagaki.ayudanki.exception.NameAlreadyExistsException;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.R.attr.max;
import static com.genenakagaki.ayudanki.data.QuizContract.*;

/**
 * Created by gene on 3/14/17.
 */

public class CardDb {

    private static final String TAG = CardDb.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;


    public static Uri insert(Context context, long quizId, String term, String definition)
            throws NameAlreadyExistsException {
        if (D) Log.d(TAG, "insert");

        // check if term already exist in quiz
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    CardEntry.CONTENT_URI,
                    CardEntry.COLUMNS,
                    CardEntry.COLUMN_QUIZ_ID + " = ? AND " + CardEntry.COLUMN_TERM + " = ?",
                    new String[] {String.valueOf(quizId), term},
                    null);
            if (c.moveToFirst()) {
                throw new NameAlreadyExistsException("Card with term '" + term + "' already exists.");
            }
        } finally {
            if (c != null) c.close();
        }

        ContentValues values = new ContentValues();
        values.put(CardEntry.COLUMN_TERM, term);
        values.put(CardEntry.COLUMN_DEFINITION, definition);
        values.put(CardEntry.COLUMN_QUIZ_ID, quizId);
        values.put(CardEntry.COLUMN_DATE, 0);
        values.put(CardEntry.COLUMN_POINTS, 0);

        return context.getContentResolver().insert(
                CardEntry.CONTENT_URI,
                values);
    }

    public static void update(Context context, long cardId, long quizId, String term, String definition)
            throws NameAlreadyExistsException {
        if (D) Log.d(TAG, "update");

        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    CardEntry.CONTENT_URI,
                    CardEntry.COLUMNS,
                    CardEntry._ID + " != ? AND " +
                            CardEntry.COLUMN_QUIZ_ID + " = ? AND " +
                            CardEntry.COLUMN_TERM + " = ?",
                    new String[] {String.valueOf(cardId), String.valueOf(quizId), term},
                    null);

            if (c.moveToFirst()) {
                throw new NameAlreadyExistsException("Card with term '" + term + "' already exists.");
            }
        } finally {
            if (c != null) c.close();
        }

        ContentValues values = new ContentValues();
        values.put(CardEntry.COLUMN_TERM, term);
        values.put(CardEntry.COLUMN_DEFINITION, definition);

        context.getContentResolver().update(
                CardEntry.CONTENT_URI,
                values,
                CardEntry._ID + " = ?",
                new String[] {String.valueOf(cardId)});
    }

    public static void addPoint(Context context, long cardId) {
        Cursor c = null;
        int points = 0;
        try {
            c = context.getContentResolver().query(
                    CardEntry.buildCardUri(cardId),
                    CardEntry.COLUMNS, null, null, null);
            c.moveToFirst();

            points = c.getInt(CardEntry.INDEX_POINTS);
        } finally {
            if (c != null) {
                c.close();
            }
        }

        if (points == 10) {
            return;
        }

        points++;

        ContentValues values = new ContentValues();
        values.put(CardEntry.COLUMN_POINTS, points);

        context.getContentResolver().update(
                CardEntry.CONTENT_URI,
                values,
                CardEntry._ID + " = ?",
                new String[] {String.valueOf(cardId)});
    }

    public static void delete(Context context, List<Long> cardIds) {
        for (long cardId : cardIds) {
            context.getContentResolver().delete(
                    CardEntry.CONTENT_URI,
                    CardEntry._ID + " = ?",
                    new String[] {String.valueOf(cardId)});
        }
    }

    public static int getCount(Context context, long quizId) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    QuizContract.CardEntry.CONTENT_URI,
                    new String[] {"count(*) AS count"},
                    QuizContract.CardEntry.COLUMN_QUIZ_ID + " = ?",
                    new String[]{String.valueOf(quizId)},
                    null);
            c.moveToFirst();
            return c.getInt(0);
        } finally {
            if (c != null) c.close();
        }
    }

    public static String getTerm(Context context, long cardId) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    CardEntry.CONTENT_URI,
                    CardEntry.COLUMNS,
                    CardEntry._ID + " = ?",
                    new String[] {String.valueOf(cardId)},
                    null);
            c.moveToFirst();

            return c.getString(CardEntry.INDEX_TERM);
        } finally {
            if (c != null) c.close();
        }
    }

    public static Card getCard(Context context, long cardId) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    CardEntry.CONTENT_URI,
                    CardEntry.COLUMNS,
                    CardEntry._ID + " = ?",
                    new String[] {String.valueOf(cardId)},
                    null);
            c.moveToFirst();

            String term = c.getString(CardEntry.INDEX_TERM);
            String definition = c.getString(CardEntry.INDEX_DEFINITION);
            int points = c.getInt(CardEntry.INDEX_POINTS);

            return new Card(cardId, term, definition, points);
        } finally {
            if (c != null) c.close();
        }
    }

    public static List<Card> getCardList(Context context, long quizId) {
        List<Card> cards = new ArrayList<>();

        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    CardEntry.CONTENT_URI,
                    CardEntry.COLUMNS,
                    CardEntry.COLUMN_QUIZ_ID + " = ?",
                    new String[] {String.valueOf(quizId)},
                    CardEntry.COLUMN_POINTS + " ASC");

            while (c.moveToNext()) {
                long id = c.getLong(CardEntry.INDEX_ID);
                String term = c.getString(CardEntry.INDEX_TERM);
                String definition = c.getString(CardEntry.INDEX_DEFINITION);
                int points = c.getInt(CardEntry.INDEX_POINTS);

                cards.add(new Card(id, term, definition, points));
            }

            return cards;
        } finally {
            if (c != null) c.close();
        }
    }

    // gets random card from current quiz
    public static Card getRandomCard(Context context) throws PreferenceNotFound {
        long quizId = QuizDb.getCurrentId(context);

        Cursor c = null;
        try {
            c = context.getContentResolver().query(
                    CardEntry.CONTENT_URI,
                    CardEntry.COLUMNS,
                    CardEntry.COLUMN_QUIZ_ID + " = ?",
                    new String[] {String.valueOf(quizId)},
                    CardEntry.COLUMN_POINTS + " ASC");

            Random rand = new Random();
            int cursorPosition = rand.nextInt(c.getCount());
            c.moveToPosition(cursorPosition);

            long id = c.getLong(CardEntry.INDEX_ID);
            String term = c.getString(CardEntry.INDEX_TERM);
            String definition = c.getString(CardEntry.INDEX_DEFINITION);
            int points = c.getInt(CardEntry.INDEX_POINTS);

            return new Card(id, term, definition, points);

        } finally {
            if (c != null) c.close();
        }
    }
}
