package com.genenakagaki.ayudanki.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.data.QuizContract;
import com.genenakagaki.ayudanki.dialog.EditCardDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gene on 3/1/17.
 */

public class CardCursorAdapter extends CursorAdapter {

    private static final String TAG = CardCursorAdapter.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private Map<Long, Boolean> mCheckedCardMap;
    private boolean mShowCheckBox = false;
    private boolean mCheckAll = false;

    public static class ViewHolder {
        public final TextView termTextView;
        public final TextView definitionTextView;
        public final CheckBox checkbox;

        public ViewHolder(View view) {
            termTextView = (TextView) view.findViewById(R.id.question_textview);
            definitionTextView = (TextView) view.findViewById(R.id.definition_textview);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    public CardCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);

        mCheckedCardMap = new HashMap<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_card, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        String term = cursor.getString(QuizContract.CardEntry.INDEX_TERM);
        String definition = cursor.getString(QuizContract.CardEntry.INDEX_DEFINITION);
        viewHolder.termTextView.setText(term);
        viewHolder.definitionTextView.setText(definition);

        final long cardId = cursor.getLong(QuizContract.CardEntry.INDEX_ID);

        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckedCardMap.put(cardId, isChecked);
            }
        });

        if (mShowCheckBox) {
            if (!mCheckedCardMap.containsKey(cursor.getLong(QuizContract.CardEntry.INDEX_ID))) {
                mCheckedCardMap.put(cursor.getLong(QuizContract.CardEntry.INDEX_ID), false);
            }

            viewHolder.checkbox.setVisibility(View.VISIBLE);
            viewHolder.checkbox.setChecked(mCheckedCardMap.get(cardId));
        } else {
            viewHolder.checkbox.setVisibility(View.GONE);
        }
    }

    public void onClick(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final long cardId = cursor.getLong(QuizContract.CardEntry.INDEX_ID);

        if (mShowCheckBox) {
            viewHolder.checkbox.setChecked(!viewHolder.checkbox.isChecked());
        } else {
            final FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();

            EditCardDialogFragment dialog = EditCardDialogFragment.newInstance(cardId);
            dialog.show(fm, null);
        }
    }

    public void setShowCheckBox(boolean showCheckBox) {
        mShowCheckBox = showCheckBox;

        if (!mShowCheckBox) {
            uncheckAll();
        }
    }

    public void checkAll() {
        for (Map.Entry<Long, Boolean> entry: mCheckedCardMap.entrySet()) {
            mCheckedCardMap.put(entry.getKey(), true);
        }
    }

    public void uncheckAll() {
        for (Map.Entry<Long, Boolean> entry: mCheckedCardMap.entrySet()) {
            mCheckedCardMap.put(entry.getKey(), false);
        }
    }

    public List<Long> getCheckedCardIds() {
        List<Long> checkedCardIds = new ArrayList<>();
        for (Map.Entry<Long, Boolean> entry: mCheckedCardMap.entrySet()) {
            if (mCheckedCardMap.get(entry.getKey())) {
                checkedCardIds.add(entry.getKey());
            }
        }

        return checkedCardIds;
    }
}
