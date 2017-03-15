package com.genenakagaki.ayudanki.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
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
import com.genenakagaki.ayudanki.adapter.QuizCursorAdapter;
import com.genenakagaki.ayudanki.data.AyudankiPreferences;
import com.genenakagaki.ayudanki.data.QuizSetDb;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;

import static com.genenakagaki.ayudanki.data.QuizContract.QuizEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuizSetFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = QuizSetFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final int QUIZ_LOADER = 1;

    private QuizCursorAdapter mQuizCursorAdapter;

    public QuizSetFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz_set, container, false);

        mQuizCursorAdapter = new QuizCursorAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(mQuizCursorAdapter);
        listView.setEmptyView(rootView.findViewById(R.id.listview_empty));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), QuizInfoActivity.class);
                Cursor c = mQuizCursorAdapter.getCursor();
                if (c.moveToPosition(position)) {
                    if (D) Log.d(TAG, "cursor moved to position");
                    AyudankiPreferences.setCurrentQuiz(getActivity(), c.getLong(QuizEntry.INDEX_ID));

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(), view, getString(R.string.transition_name_quiz));
                    startActivity(intent, options.toBundle());
                } else {

                    if (D) Log.d(TAG, "cursor cannot be moved to position");
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        getActivity().getSupportLoaderManager().initLoader(QUIZ_LOADER, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long quizSetId = -1;
        try {
            quizSetId = QuizSetDb.getCurrentId(getActivity());
        } catch (PreferenceNotFound preferenceNotFound) {
            preferenceNotFound.printStackTrace();
        }

        return new CursorLoader(getActivity(),
                QuizEntry.CONTENT_URI,
                QuizEntry.COLUMNS,
                QuizEntry.COLUMN_QUIZ_SET_ID + " = ?",
                new String[] {String.valueOf(quizSetId)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mQuizCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuizCursorAdapter.swapCursor(null);
    }
}
