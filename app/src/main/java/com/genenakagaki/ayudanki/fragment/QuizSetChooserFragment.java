package com.genenakagaki.ayudanki.fragment;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.adapter.QuizSetChooserCursorAdapter;
import com.genenakagaki.ayudanki.data.QuizContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuizSetChooserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = QuizSetChooserFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final String[] QUIZ_SET_COLUMNS = QuizContract.QuizSetEntry.COLUMNS;

    public static final int INDEX_QUIZ_SET_ID = 0;
    public static final int INDEX_QUIZ_SET_NAME = 1;

    private static final String ARG_QUIZ_SET_ID = "arg_quiz_set_id";

    private static final int QUIZ_SET_LOADER = 0;

    private OnQuizSetSelectedListener mOnQuizSetSelectedListener;

    private QuizSetChooserCursorAdapter mQuizSetChooserCursorAdapter;

    private long mQuizSetId;

    public interface OnQuizSetSelectedListener {
        void onQuizSelected(long quizSetId);
    }

    public static QuizSetChooserFragment newInstance(long quizSetId) {
        Bundle args = new Bundle();
        args.putLong(ARG_QUIZ_SET_ID, quizSetId);

        QuizSetChooserFragment fragment = new QuizSetChooserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public QuizSetChooserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(0);

        Bundle args = getArguments();
        if (args != null) {
            mQuizSetId = args.getLong(ARG_QUIZ_SET_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz_set_chooser, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Choose");

        mQuizSetChooserCursorAdapter = new QuizSetChooserCursorAdapter(getActivity(), null, 0, mQuizSetId);

        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(mQuizSetChooserCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (D) Log.d(TAG, "quiz set item clicked");
                mQuizSetChooserCursorAdapter.onClick(getActivity(), view);
                mOnQuizSetSelectedListener.onQuizSelected(mQuizSetChooserCursorAdapter.getSelectedQuizSetId());
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        getActivity().getSupportLoaderManager().initLoader(QUIZ_SET_LOADER, null, this);

        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnQuizSetSelectedListener = (OnQuizSetSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = QuizContract.QuizSetEntry.CONTENT_URI;
        return new CursorLoader(getActivity(), uri, QUIZ_SET_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mQuizSetChooserCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuizSetChooserCursorAdapter.swapCursor(null);
    }
}
