package com.genenakagaki.ayudanki.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.genenakagaki.ayudanki.fragment.QuizSetFragment;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.data.QuizContract;

/**
 * Created by gene on 2/15/17.
 */

public class
QuizCursorAdapter extends CursorAdapter {

    public static class ViewHolder {

        public final TextView quizNameTextView;
        public final TextView termCountTextView;

        public ViewHolder(View view) {
            quizNameTextView = (TextView) view.findViewById(R.id.quiz_name_textview);
            termCountTextView = (TextView) view.findViewById(R.id.term_count_textview);
        }

    }

    public QuizCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_quiz, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String quizName = cursor.getString(QuizContract.QuizEntry.INDEX_NAME);
        viewHolder.quizNameTextView.setText(quizName);

        long quizId = cursor.getLong(QuizContract.QuizEntry.INDEX_ID);

        Cursor c = context.getContentResolver().query(
                QuizContract.CardEntry.CONTENT_URI,
                new String[] {"count(*) AS count"},
                QuizContract.CardEntry.COLUMN_QUIZ_ID + " = ?",
                new String[]{String.valueOf(quizId)},
                null);

        c.moveToFirst();
        int cardCount = c.getInt(0);

        viewHolder.termCountTextView.setText(String.valueOf(cardCount));
    }
}
