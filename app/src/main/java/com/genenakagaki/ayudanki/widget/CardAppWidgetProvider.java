package com.genenakagaki.ayudanki.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.QuizInfoActivity;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.data.CardDb;
import com.genenakagaki.ayudanki.data.model.Card;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;

/**
 * Created by gene on 3/22/17.
 */

public class CardAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = CardAppWidgetProvider.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private static final String SHOW_DEFINITION = "show_definition";
    private static final String SHOW_NEW_CARD = "show_new_card";

    private static boolean mIsInitiated = false;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (!mIsInitiated) {
            try {
                getPendingSelfIntent(context, SHOW_NEW_CARD).send();
            } catch (PendingIntent.CanceledException e) {
                if (D) Log.d(TAG, "initialize widget failed. " + e.getMessage());
            }
            mIsInitiated = true;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_card);
        ComponentName cardWidget = new ComponentName(context, CardAppWidgetProvider.class);

        remoteViews.setOnClickPendingIntent(R.id.icon, PendingIntent.getActivity(
                context,
                0,
                new Intent(context, QuizInfoActivity.class),
                0));
        remoteViews.setOnClickPendingIntent(R.id.layout, getPendingSelfIntent(context, SHOW_DEFINITION));

        appWidgetManager.updateAppWidget(cardWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_card);
        ComponentName watchWidget = new ComponentName(context, CardAppWidgetProvider.class);

        if (intent.getAction().equals(SHOW_DEFINITION)) {
            remoteViews.setViewVisibility(R.id.hint_textview, View.GONE);
            remoteViews.setViewVisibility(R.id.definition_textview, View.VISIBLE);

            remoteViews.setOnClickPendingIntent(R.id.layout, getPendingSelfIntent(context, SHOW_NEW_CARD));

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        } else if (intent.getAction().equals(SHOW_NEW_CARD)) {
            remoteViews.setViewVisibility(R.id.definition_textview, View.GONE);
            remoteViews.setViewVisibility(R.id.hint_textview, View.VISIBLE);

            try {
                Card card = CardDb.getRandomCard(context);

                remoteViews.setTextViewText(R.id.term_textview, card.getTerm());
                remoteViews.setTextViewText(R.id.definition_textview, card.getDefinition());
            } catch (PreferenceNotFound preferenceNotFound) {
                if (D) Log.d(TAG, preferenceNotFound.getMessage());

                remoteViews.setViewVisibility(R.id.hint_textview, View.GONE);
            }

            remoteViews.setOnClickPendingIntent(R.id.layout, getPendingSelfIntent(context, SHOW_DEFINITION));

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static void reset() {
        mIsInitiated = false;
    }
}
