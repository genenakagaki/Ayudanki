package com.genenakagaki.ayudanki.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RadioButton;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.fragment.QuizSetChooserFragment;
import com.genenakagaki.ayudanki.R;

import static com.genenakagaki.ayudanki.data.QuizContract.*;

/**
 * Created by gene on 2/18/17.
 */

public class QuizSetChooserCursorAdapter extends CursorAdapter {

    private static final String TAG = QuizSetChooserCursorAdapter.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static class ViewHolder {
        public final RadioButton quizSetRadioButton;

        public ViewHolder(View view) {
            quizSetRadioButton = (RadioButton) view.findViewById(R.id.quiz_set_radiobutton);
        }
    }

    private RadioButton mSelectedRadioButton;
    private long mSelectedQuizSetId;

    public QuizSetChooserCursorAdapter(Context context, Cursor cursor, int flags, long selectedQuizSetId) {
        super(context, cursor, flags);
        mSelectedQuizSetId = selectedQuizSetId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_quiz_set_chooser, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String quizSetName = cursor.getString(QuizSetEntry.INDEX_NAME);
        if (D) Log.d(TAG, "quiz set name " + quizSetName);
        viewHolder.quizSetRadioButton.setText(quizSetName);

        if (cursor.getLong(QuizSetChooserFragment.INDEX_QUIZ_SET_ID) == mSelectedQuizSetId) {
            if (D) Log.d(TAG, "current selected quiz set");
            viewHolder.quizSetRadioButton.setChecked(true);
            mSelectedRadioButton = viewHolder.quizSetRadioButton;
        }
    }

    public void onClick(Context context, View view) {
        if (mSelectedRadioButton != null) {
            mSelectedRadioButton.setChecked(false);
        }
        mSelectedRadioButton = ((ViewHolder)view.getTag()).quizSetRadioButton;
        mSelectedRadioButton.setChecked(true);

        Cursor c = context.getContentResolver().query(
                QuizSetEntry.CONTENT_URI,
                QuizSetEntry.COLUMNS,
                QuizSetEntry.COLUMN_NAME + " = ?",
                new String[] {mSelectedRadioButton.getText().toString()},
                null);

        if (c != null && c.moveToFirst()) {
            if (D) Log.d(TAG, "New quizset " + c.getString(QuizSetEntry.INDEX_NAME));
            mSelectedQuizSetId = c.getLong(QuizSetEntry.INDEX_ID);
        }
    }

    public long getSelectedQuizSetId() {
        return mSelectedQuizSetId;
    }
}
