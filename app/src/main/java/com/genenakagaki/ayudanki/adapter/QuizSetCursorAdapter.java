package com.genenakagaki.ayudanki.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.data.QuizSetDb;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;

import static com.genenakagaki.ayudanki.data.QuizContract.*;

/**
 * Created by gene on 2/10/17.
 */

public class QuizSetCursorAdapter extends CursorAdapter {

    private static final String TAG = QuizSetCursorAdapter.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private View mCurrentListItem = null;
    private ColorStateList mDefaultTextColor = null;

    public static class ViewHolder {
        public final TextView quizSetTextView;

        public ViewHolder(View view) {
            quizSetTextView = (TextView) view.findViewById(R.id.quiz_set_textview);
        }
    }

    public QuizSetCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_quiz_set, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String quizSetName = cursor.getString(QuizSetEntry.INDEX_NAME);
        viewHolder.quizSetTextView.setText(quizSetName);

        String currentQuizSetName = null;
        try {
            currentQuizSetName = QuizSetDb.getCurrentName(context);
        } catch (PreferenceNotFound preferenceNotFound) {
            if (D) Log.d(TAG, preferenceNotFound.getMessage());
        }

        if (currentQuizSetName != null && currentQuizSetName.equals(quizSetName)) {
            if (D) Log.d(TAG, "Is current quiz");

            setCurrentQuizSet(view);
        }
    }

    public void setCurrentQuizSet(View view) {
        ViewHolder viewHolder;

        if (mCurrentListItem != null) {
            // remove colors for current QuizSet
            viewHolder = (ViewHolder) mCurrentListItem.getTag();

            LinearLayout parent = (LinearLayout) viewHolder.quizSetTextView.getParent();
            parent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            viewHolder.quizSetTextView.setTextColor(mDefaultTextColor);

        }

        mCurrentListItem = view;

        viewHolder = (ViewHolder) view.getTag();

        if (mDefaultTextColor == null) {
            mDefaultTextColor = viewHolder.quizSetTextView.getTextColors();
        }
        LinearLayout parent = (LinearLayout) viewHolder.quizSetTextView.getParent();
        parent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        viewHolder.quizSetTextView.setTextColor(ContextCompat.getColor(mContext, R.color.white));
    }
}
