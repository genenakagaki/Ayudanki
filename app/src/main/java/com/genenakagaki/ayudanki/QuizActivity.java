package com.genenakagaki.ayudanki;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.genenakagaki.ayudanki.animator.FabAnimator;
import com.genenakagaki.ayudanki.data.QuizDb;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;
import com.genenakagaki.ayudanki.fragment.MultipleChoiceFragment;
import com.genenakagaki.ayudanki.fragment.QuizResultFragment;
import com.genenakagaki.ayudanki.fragment.TermDefinitionFragment;
import com.genenakagaki.ayudanki.quiz.StudyQuiz;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = QuizActivity.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final String EXTRA_QUIZ_ID = "extra_quiz_id";

    public static final String QUIZ_MATCH_FRAGMENT_TAG = "quiz_match_fragment_tag";

    private long mQuizId;

    private StudyQuiz mStudyQuiz;

    private FloatingActionButton mNextFab;
    private FabAnimator mNextFabAnimator;

    private FloatingActionButton mReturnFab;
    private FabAnimator mReturnFabAnimator;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(QuizDb.getCurrentName(this));
        } catch (PreferenceNotFound preferenceNotFound) {
            if (D) Log.d(TAG, preferenceNotFound.getMessage());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable backIcon = ContextCompat.getDrawable(this, R.drawable.ic_clear);
        backIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        getSupportActionBar().setHomeAsUpIndicator(backIcon);

        mNextFab = (FloatingActionButton) findViewById(R.id.next_fab);
        mNextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStudyQuiz.createNextQuestion();
            }
        });
        mNextFabAnimator = new FabAnimator(mNextFab);

        mReturnFab = (FloatingActionButton) findViewById(R.id.return_fab);
        mReturnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuizActivity.super.onBackPressed();
            }
        });
        mReturnFabAnimator = new FabAnimator(mReturnFab);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        mQuizId = getIntent().getLongExtra(EXTRA_QUIZ_ID, -1);

        mStudyQuiz = new StudyQuiz(this, mQuizId);
        mStudyQuiz.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message_confirm_close)
                .setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        QuizActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void switchFragment(Fragment fragment) {
        if (!(fragment instanceof TermDefinitionFragment)) {
            if (fragment instanceof QuizResultFragment) {
                if (mNextFab.getVisibility() == View.VISIBLE) {
                    if (D) Log.d(TAG, "next fab is visible");
                    mNextFabAnimator.switchFab(mReturnFabAnimator);
                } else {
                    if (D) Log.d(TAG, "next fab is NOT visible");
                    mReturnFabAnimator.showFab();
                }
            } else {
                mNextFabAnimator.hideFab();
            }
        }


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, QUIZ_MATCH_FRAGMENT_TAG)
                .commit();
    }

    public void onChoiceClick(View view) {
        MultipleChoiceFragment fragment = (MultipleChoiceFragment) getSupportFragmentManager().findFragmentByTag(QUIZ_MATCH_FRAGMENT_TAG);
        fragment.onChoiceClick(view);
    }

    public void showFab() {
        mNextFabAnimator.showFab();
    }

    public void setProgressMax(int max) {
        mProgressBar.setMax(max);
    }

    public void setProgressBar(int progress) {
        mProgressBar.setProgress(progress);
    }

}
