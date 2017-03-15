package com.genenakagaki.ayudanki.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gene on 2/9/17.
 */

public class QuizContract {

    public static final String CONTENT_AUTHORITY = "com.genenakagaki.ayudanki";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CARD = "card";
    public static final String PATH_CARD_ID = PATH_CARD + "/#";
    public static final String PATH_QUIZ = "quiz";
    public static final String PATH_QUIZ_ID = PATH_QUIZ + "/#";
    public static final String PATH_QUIZ_SET = "quiz_set";
    public static final String PATH_QUIZ_SET_ID = PATH_QUIZ_SET + "/#";

    public static final class QuizSetEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUIZ_SET).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUIZ;

        public static final String TABLE_NAME = "quiz_set";

        public static final String COLUMN_NAME = "name";

        public static final String[] COLUMNS = {
                TABLE_NAME + "." + _ID,
                COLUMN_NAME
        };
        public static final int INDEX_ID = 0;
        public static final int INDEX_NAME = 1;

        public static Uri buildQuizSetUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class QuizEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUIZ).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUIZ_SET;

        public static final String TABLE_NAME = "quiz";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUIZ_SET_ID = "quiz_set_id";

        public static final String[] COLUMNS = {
                TABLE_NAME + "." + _ID,
                COLUMN_NAME,
                COLUMN_QUIZ_SET_ID
        };
        public static final int INDEX_ID = 0;
        public static final int INDEX_NAME = 1;
        public static final int INDEX_QUIZ_SET_ID = 2;

        public static Uri buildQuizUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CardEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARD).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARD;

        public static final String TABLE_NAME = "card";

        public static final String COLUMN_TERM = "term";
        public static final String COLUMN_DEFINITION = "definition";
        public static final String COLUMN_QUIZ_ID = "quiz_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_POINTS = "points";

        public static final String[] COLUMNS = {
                TABLE_NAME + "." + _ID,
                COLUMN_TERM,
                COLUMN_DEFINITION,
                COLUMN_QUIZ_ID,
                COLUMN_DATE,
                COLUMN_POINTS
        };
        public static final int INDEX_ID = 0;
        public static final int INDEX_TERM = 1;
        public static final int INDEX_DEFINITION = 2;
        public static final int INDEX_QUIZ_ID = 3;
        public static final int INDEX_DATE = 4;
        public static final int INDEX_POINTS = 5;

        public static Uri buildCardUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
