package com.genenakagaki.ayudanki.data;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;
import com.genenakagaki.ayudanki.widget.CardAppWidgetProvider;

/**
 * Created by gene on 2/15/17.
 */

public class AyudankiPreferences {

    private static final String TAG = AyudankiPreferences.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final String PREF_QUIZ_SET_URI_STRING = "Preference_quiz_set_uri_string";
    public static final String PREF_QUIZ_URI_STRING = "Preference_quiz_uri_string";

    /* ----------------------------------------
        Quiz Set
    ---------------------------------------- */
    public static void setCurrentQuizSet(Context context, long quizSetId) {
        Uri quizSetUri = ContentUris.withAppendedId(QuizContract.QuizSetEntry.CONTENT_URI, quizSetId);
        setCurrentQuizSet(context, quizSetUri);
    }

    public static void setCurrentQuizSet(Context context, Uri quizSetUri) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(PREF_QUIZ_SET_URI_STRING, quizSetUri.toString());
        editor.commit();
    }

    public static Uri getCurrentQuizSet(Context context) throws PreferenceNotFound {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String quizSetUriString = sharedPref.getString(PREF_QUIZ_SET_URI_STRING, null);

        if (quizSetUriString == null) {
            throw new PreferenceNotFound(PREF_QUIZ_SET_URI_STRING + " not found in shared preferences.");
        } else  {
            if (D) Log.d(TAG, PREF_QUIZ_SET_URI_STRING + " found in shared preferences.");
            return Uri.parse(quizSetUriString);
        }

    }

     /* ----------------------------------------
            Quiz
     ---------------------------------------- */
     public static void setCurrentQuiz(Context context, long quizId) {
         Uri quizUri = QuizContract.QuizEntry.buildQuizUri(quizId);
         setCurrentQuiz(context, quizUri);
     }

     public static void setCurrentQuiz(Context context, Uri quizUri) {
         SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
         SharedPreferences.Editor editor = sharedPref.edit();

         editor.putString(PREF_QUIZ_URI_STRING, quizUri.toString());
         editor.commit();

         // update widget
         CardAppWidgetProvider.reset();
     }

     public static Uri getCurrentQuiz(Context context) throws PreferenceNotFound {
         SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
         String quizUriString = sharedPref.getString(PREF_QUIZ_URI_STRING, null);

         if (quizUriString == null) {
             throw new PreferenceNotFound(PREF_QUIZ_URI_STRING + " not found in shared preferences.");
         } else {
             if (D) Log.d(TAG, PREF_QUIZ_URI_STRING + " found in shared preferences.");
             return Uri.parse(quizUriString);
         }
     }

}
