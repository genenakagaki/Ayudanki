package com.genenakagaki.ayudanki.fragment;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.QuizInfoActivity;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.adapter.CardCursorAdapter;
import com.genenakagaki.ayudanki.data.QuizContract;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizInfoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = QuizInfoFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final int CARD_LOADER = 0;

    private static final String ARG_QUIZ_ID = "arg_quiz_id";

    private long mQuizId;
    private CardCursorAdapter mCardAdapter;
    private ListView mListView;

    public static QuizInfoFragment newInstance(long quizId) {
        QuizInfoFragment fragment = new QuizInfoFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_QUIZ_ID, quizId);
        fragment.setArguments(args);
        return fragment;
    }

    public QuizInfoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mQuizId = args.getLong(ARG_QUIZ_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz_info, container, false);

        mCardAdapter = new CardCursorAdapter(getActivity(), null, 0);

        mListView = (ListView) rootView.findViewById(R.id.listview);
        mListView.setAdapter(mCardAdapter);
        mListView.setEmptyView(rootView.findViewById(R.id.listview_empty));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCardAdapter.onClick(view, getActivity(), mCardAdapter.getCursor());
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showCheckBoxes();
                CardCursorAdapter.ViewHolder viewHolder = (CardCursorAdapter.ViewHolder) view.getTag();
                viewHolder.checkbox.setChecked(true);
                return true;
            }
        });

        return rootView;
    }

    public void showCheckBoxes() {
        mCardAdapter.setShowCheckBox(true);
        mListView.invalidateViews();
        ((QuizInfoActivity)getActivity()).showEditCardMenu();
    }

    public void hideCheckBoxes() {
        mCardAdapter.setShowCheckBox(false);
        mListView.invalidateViews();
        ((QuizInfoActivity)getActivity()).showQuizMenu();
    }

    public void checkAll() {
        mCardAdapter.checkAll();
        mListView.invalidateViews();
    }

    public void uncheckAll() {
        mCardAdapter.uncheckAll();
        mListView.invalidateViews();
    }

    public List<Long> getCheckedCardIds() {
        return mCardAdapter.getCheckedCardIds();
    }

    public int getCardCount() {
        int count = mCardAdapter.getCursor().getCount();

        if (D) Log.d(TAG, "getCardCount() result: " + count);
        return count;
    }

    @Override
    public void onResume() {
        getActivity().getSupportLoaderManager().initLoader(CARD_LOADER, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                QuizContract.CardEntry.CONTENT_URI,
                QuizContract.CardEntry.COLUMNS,
                QuizContract.CardEntry.COLUMN_QUIZ_ID + " = ?",
                new String[] {String.valueOf(mQuizId)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCardAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardAdapter.swapCursor(null);
    }
}
