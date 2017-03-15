package com.genenakagaki.ayudanki.fragment;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.QuizInfoActivity;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.data.AyudankiPreferences;
import com.genenakagaki.ayudanki.data.QuizContract;
import com.genenakagaki.ayudanki.data.QuizDb;
import com.genenakagaki.ayudanki.data.QuizSetDb;
import com.genenakagaki.ayudanki.exception.NameAlreadyExistsException;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;
import com.genenakagaki.ayudanki.utility.TextInputValidator;

import static com.genenakagaki.ayudanki.data.QuizContract.*;

public class EditQuizFragment extends Fragment {

    private static final String TAG = EditQuizFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final long NEW_QUIZ_ID = -1;

    private static final String ARG_QUIZ_ID = "arg_quiz_id";

    private long mQuizSetId = -1;
    private long mQuizId;

    private TextInputEditText mQuizNameInput;
    private TextInputEditText mQuizSetNameInput;

    private TextInputValidator mQuizNameValidator;

    public static EditQuizFragment newInstance(long quizId) {
        Bundle args = new Bundle();
        args.putLong(ARG_QUIZ_ID, quizId);

        EditQuizFragment fragment = new EditQuizFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EditQuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();

        if (args != null) {
            mQuizId = args.getLong(ARG_QUIZ_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView()");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_quiz, container, false);

        // quiz name input
        TextInputLayout quizNameInputLayout =
                (TextInputLayout) rootView.findViewById(R.id.quiz_name_inputlayout);
        mQuizNameInput = (TextInputEditText) rootView.findViewById(R.id.quiz_name_textinput);
        mQuizNameValidator = new TextInputValidator(
                quizNameInputLayout, mQuizNameInput, getString(R.string.input_quiz_name_empty_error));

        mQuizSetNameInput = (TextInputEditText) rootView.findViewById(R.id.quiz_set_name_textinput);
        mQuizSetNameInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                mQuizNameValidator.hideError();

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, QuizSetChooserFragment.newInstance(mQuizSetId))
                        .addToBackStack(null)
                        .commit();
            }
        });

        if (mQuizId != NEW_QUIZ_ID) {
            if (D) Log.d(TAG, "Set quiz name in input");
            Cursor cursor = getActivity().getContentResolver().query(
                    ContentUris.withAppendedId(QuizEntry.CONTENT_URI, mQuizId),
                    QuizEntry.COLUMNS,
                    null, null, null);

            if (cursor.moveToFirst()) {
                mQuizNameInput.setText(cursor.getString(QuizEntry.INDEX_NAME));
            }
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Drawable backIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_clear);
        backIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        ((AppCompatActivity) getActivity()).getSupportActionBar()
                .setHomeAsUpIndicator(backIcon);

        if (mQuizSetId == -1) {
            if (D) Log.d(TAG, "Quiz set not chosen");

            try {
                mQuizSetId = QuizSetDb.getCurrentId(getActivity());
            } catch (PreferenceNotFound preferenceNotFound) {
                preferenceNotFound.printStackTrace();
            }
        } else {
            if (D) Log.d(TAG, "Quiz set chosen in QuizSetChooser");
        }

        Cursor quizSetCursor = getActivity().getContentResolver().query(
                ContentUris.withAppendedId(QuizSetEntry.CONTENT_URI, mQuizSetId),
                QuizSetEntry.COLUMNS,
                null, null, null);

        if (quizSetCursor != null && quizSetCursor.moveToFirst()) {
            String quizSetName = quizSetCursor.getString(QuizSetEntry.INDEX_NAME);
            if (D) Log.d(TAG, "quiz set name is: " + quizSetName);
            mQuizSetNameInput.setText(quizSetName);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_edit_quiz, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            onSaveClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSaveClick() {
        if (D) Log.d(TAG, "onSaveClick()");

        mQuizNameValidator.validate();

        if (mQuizNameValidator.isValid()) {
            String quizName = mQuizNameInput.getText().toString().trim();

            if (mQuizId == NEW_QUIZ_ID) {
                Uri quizUri;
                try {
                    quizUri = QuizDb.insert(getActivity(), quizName, mQuizSetId);
                } catch (NameAlreadyExistsException e) {
                    if (D) Log.d(TAG, e.getMessage());
                    mQuizNameValidator.showError(getString(R.string.input_quiz_name_duplicate_error));
                    return;
                }

                AyudankiPreferences.setCurrentQuizSet(getActivity(), mQuizSetId);
                AyudankiPreferences.setCurrentQuiz(getActivity(), quizUri);

                Intent intent = new Intent(getActivity(), QuizInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), mQuizNameInput, getString(R.string.transition_name_quiz));
                startActivity(intent, options.toBundle());
            } else {
                try {
                    QuizDb.update(getActivity(), mQuizId, quizName, mQuizSetId);
                } catch (NameAlreadyExistsException e) {
                    if (D) Log.d(TAG, e.getMessage());

                    mQuizNameValidator.showError(getString(R.string.input_quiz_name_duplicate_error));
                    return;
                }
                AyudankiPreferences.setCurrentQuizSet(getActivity(), mQuizSetId);

                Intent intent = new Intent(getActivity(), QuizInfoActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), mQuizNameInput, getString(R.string.transition_name_quiz));
                startActivity(intent, options.toBundle());
            }
        }
    }

    public void updateQuizSetId(long quizSetId) {
        if (D) {
            Log.d(TAG, "updateQuizSetId()");

            Uri uri = ContentUris.withAppendedId(QuizSetEntry.CONTENT_URI, quizSetId);
            Cursor c = getActivity().getContentResolver().query(
                    uri,
                    QuizSetEntry.COLUMNS,
                    null, null, null);
            c.moveToFirst();
            Log.d(TAG, "quiz set name: " + c.getString(QuizSetEntry.INDEX_NAME));
        }

        mQuizSetId = quizSetId;
    }
}
