package com.genenakagaki.ayudanki;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.genenakagaki.ayudanki.animator.FabAnimator;
import com.genenakagaki.ayudanki.animator.QuizFabAnimator;
import com.genenakagaki.ayudanki.data.CardDb;
import com.genenakagaki.ayudanki.data.QuizDb;
import com.genenakagaki.ayudanki.dialog.ConfirmDeleteDialogFragment;
import com.genenakagaki.ayudanki.dialog.EditCardDialogFragment;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;
import com.genenakagaki.ayudanki.fragment.QuizInfoFragment;

import java.util.List;

public class QuizInfoActivity extends AppCompatActivity {

    private static final String TAG = QuizInfoActivity.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private static final String QUIZ_FRAGMENT_TAG = "quiz_fragment_tag";

    private TextView mTitleTextView;
    private RelativeLayout mCardCheckBoxLayout;

    private FloatingActionButton mQuizFab;
    private FloatingActionButton mAddFab;

    private FabAnimator mAddFabAnimator;
    private QuizFabAnimator mQuizFabAnimator;

    private boolean mIsEditCardMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final long quizId;
        try {
            quizId = QuizDb.getCurrentId(this);
        } catch (PreferenceNotFound preferenceNotFound) {
            if (D) Log.d(TAG, preferenceNotFound.getMessage());
            return;
        }

        mTitleTextView = (TextView) findViewById(R.id.toolbar_title);
        mCardCheckBoxLayout = (RelativeLayout) findViewById(R.id.toolbar_checkbox_layout);
        CheckBox checkBox = (CheckBox) findViewById(R.id.toolbar_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                QuizInfoFragment fragment = (QuizInfoFragment) getSupportFragmentManager().findFragmentByTag(QUIZ_FRAGMENT_TAG);

                if (isChecked) {
                    fragment.checkAll();
                } else {
                    fragment.uncheckAll();
                }
            }
        });

        try {
            setTitle(QuizDb.getCurrentName(this));
        } catch (PreferenceNotFound preferenceNotFound) {
            if (D) Log.d(TAG, preferenceNotFound.getMessage());
        }

        mQuizFabAnimator = new QuizFabAnimator(findViewById(R.id.main_content));

        mQuizFab = (FloatingActionButton) findViewById(R.id.quiz_fab);
        mQuizFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cardCount = 0;
                try {
                    cardCount = QuizDb.getCurrentCardCount(QuizInfoActivity.this);
                } catch (PreferenceNotFound preferenceNotFound) {
                    if (D) Log.d(TAG, preferenceNotFound.getMessage());
                }

                if (cardCount > 1) {
//                    mQuizFabAnimator.showToolbar();
                    Intent intent = new Intent(QuizInfoActivity.this, QuizActivity.class);
                    intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, quizId);
                    startActivity(intent);
                } else {
                    Snackbar mySnackbar = Snackbar.make(
                            view, R.string.snackbar_message_not_enough_cards, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
            }
        });
//        Button studyButton = (Button) findViewById(R.id.study_button);
//        studyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(QuizInfoActivity.this, QuizActivity.class);
//                intent.putExtra(QuizActivity.EXTRA_QUIZ_ID, quizId);
//                startActivity(intent);
//            }
//        });

        mAddFab = (FloatingActionButton) findViewById(R.id.add_fab);
        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCardDialogFragment dialog = EditCardDialogFragment.newInstance(EditCardDialogFragment.NEW_CARD_ID);
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        mAddFabAnimator = new FabAnimator(mAddFab);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, QuizInfoFragment.newInstance(quizId), QUIZ_FRAGMENT_TAG)
                    .commit();
        }
    }

    public void showEditCardMenu() {
        mIsEditCardMode = true;

        mTitleTextView.setVisibility(View.GONE);
        mCardCheckBoxLayout.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mAddFabAnimator.hideFab();
        mQuizFabAnimator.hideFab();

        supportInvalidateOptionsMenu();
    }

    public void showQuizMenu() {
        mIsEditCardMode = false;

        mCardCheckBoxLayout.setVisibility(View.GONE);
        mTitleTextView.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAddFabAnimator.showFab();
        mQuizFabAnimator.showFab();

        supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsEditCardMode) {
            getMenuInflater().inflate(R.menu.menu_edit_card, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_quiz, menu);

            MenuItem editMenuItem = menu.findItem(R.id.action_edit);
            Drawable editIcon = editMenuItem.getIcon();
            // If we don't mutate the drawable, then all drawable's with this id will have a color
            // filter applied to it.
            editIcon.mutate();
            editIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mIsEditCardMode) {
            switch (item.getItemId()) {
                case R.id.action_cancel:
                    onBackPressed();
                    break;
                case R.id.action_delete:
                    QuizInfoFragment fragment =
                            (QuizInfoFragment) getSupportFragmentManager().findFragmentByTag(QUIZ_FRAGMENT_TAG);

                    int cardCount = fragment.getCardCount();
                    List<Long> checkedCardIds = fragment.getCheckedCardIds();

                    ConfirmDeleteDialogFragment confirmDeleteDialogFragment =
                            ConfirmDeleteDialogFragment.newInstance(checkedCardIds);
                    confirmDeleteDialogFragment.show(getSupportFragmentManager(), null);
                    break;
            }
        } else {
            long quizId = 0;
            try {
                quizId = QuizDb.getCurrentId(this);
            } catch (PreferenceNotFound preferenceNotFound) {
                if (D) Log.d(TAG, preferenceNotFound.getMessage());
            }

            switch (item.getItemId()) {
                case android.R.id.home:
                    if (D) Log.d(TAG, "back button pressed");
                    super.onBackPressed();
                    return true;
                case R.id.action_edit:
                    Intent intent = new Intent(this, EditQuizActivity.class);
                    intent.putExtra(EditQuizActivity.EXTRA_QUIZ_ID, quizId);

//            if (Build.VERSION.SDK_INT >= 16) {
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        this, mTitleTextView, getString(R.string.transition_name_quiz));
//                startActivity(intent, options.toBundle());
//            } else {
//                startActivity(intent);
//            }
                    startActivity(intent);
                    return true;
                case R.id.action_delete:
                    ConfirmDeleteDialogFragment confirmDeleteDialogFragment =
                            ConfirmDeleteDialogFragment.newInstance(
                                    ConfirmDeleteDialogFragment.TYPE_QUIZ,
                                    quizId);
                    confirmDeleteDialogFragment.show(getSupportFragmentManager(), null);

                    return true;
                case R.id.action_settings:
                    return true;
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (D) Log.d(TAG, "onBackPressed");
        if (mIsEditCardMode) {
            if (D) Log.d(TAG, "In edit card mode");
            QuizInfoFragment fragment = (QuizInfoFragment)getSupportFragmentManager().findFragmentByTag(QUIZ_FRAGMENT_TAG);
            fragment.hideCheckBoxes();
        } else {
            if (D) Log.d(TAG, "Not in edit card mode");
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        mTitleTextView.setText(title);
    }

    public long getQuizId() {
        try {
            return QuizDb.getCurrentId(this);
        } catch (PreferenceNotFound preferenceNotFound) {
            if (D) Log.d(TAG, preferenceNotFound.getMessage());
        }

        return 0;
    }
}
