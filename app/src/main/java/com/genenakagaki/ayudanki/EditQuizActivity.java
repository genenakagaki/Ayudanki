package com.genenakagaki.ayudanki;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.genenakagaki.ayudanki.fragment.EditQuizFragment;
import com.genenakagaki.ayudanki.fragment.QuizSetChooserFragment;

public class EditQuizActivity extends AppCompatActivity implements QuizSetChooserFragment.OnQuizSetSelectedListener {

    private static final String TAG = EditQuizActivity.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final String EXTRA_QUIZ_ID = "extra_quiz_id";
    public static final long NEW_QUIZ_ID = -1;

    public static final String EDIT_QUIZ_FRAGMENT_TAG = "edit_quiz_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        long quizId = intent.getExtras().getLong(EXTRA_QUIZ_ID);

        if (quizId == NEW_QUIZ_ID) {
            if (D) Log.d(TAG, "Creating new quiz");
            getSupportActionBar().setTitle(R.string.title_edit_quiz_fragment_create);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,
                            EditQuizFragment.newInstance(EditQuizFragment.NEW_QUIZ_ID),
                            EDIT_QUIZ_FRAGMENT_TAG)
                    .commit();
        } else {
            if (D) Log.d(TAG, "Editing existing quiz");
            getSupportActionBar()
                    .setTitle(R.string.title_edit_quiz_fragment_edit);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container,
                            EditQuizFragment.newInstance(quizId),
                            EDIT_QUIZ_FRAGMENT_TAG)
                    .commit();
        }

        overridePendingTransition(R.animator.add_quiz_enter, R.animator.add_quiz_exit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (D) Log.d(TAG, "onBackPressed");

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count > 0) {
            if (D) Log.d(TAG, "popping backstack");
            getSupportFragmentManager().popBackStack();
        } else {
            if (D) Log.d(TAG, "fragment backstack is empty.");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            Intent intent = getIntent();
            long quizId = intent.getExtras().getLong(EXTRA_QUIZ_ID);

            if (quizId == NEW_QUIZ_ID) {
                builder.setMessage(R.string.dialog_confirm_discard_add);
            } else {
                builder.setMessage(R.string.dialog_confirm_discard_edit);
            }
            builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditQuizActivity.super.onBackPressed();
                    overridePendingTransition(R.animator.add_quiz_enter, R.animator.add_quiz_exit);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void onQuizSelected(long quizSetId) {
        if (D) Log.d(TAG, "onQuizSelected()");
        EditQuizFragment fragment = (EditQuizFragment) getSupportFragmentManager()
                .findFragmentByTag(EDIT_QUIZ_FRAGMENT_TAG);
        fragment.updateQuizSetId(quizSetId);
    }
}
