package com.genenakagaki.ayudanki;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.genenakagaki.ayudanki.adapter.QuizSetCursorAdapter;
import com.genenakagaki.ayudanki.animator.AddFabAnimator;
import com.genenakagaki.ayudanki.animator.FabAnimator;
import com.genenakagaki.ayudanki.data.AyudankiPreferences;
import com.genenakagaki.ayudanki.data.QuizSetDb;
import com.genenakagaki.ayudanki.dialog.ConfirmDeleteDialogFragment;
import com.genenakagaki.ayudanki.dialog.EditQuizSetDialogFragment;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;
import com.genenakagaki.ayudanki.fragment.QuizSetFragment;

import static com.genenakagaki.ayudanki.data.QuizContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private static final String QUIZ_SET_FRAGMENT_TAG = "quiz_set_fragment_tag";

    public static final int QUIZ_SET_LOADER = 0;

    private static final int MSG_SHOW_ADD_QUIZ_DIALOG = 0;

    private Handler mHandler;

    private DrawerLayout mDrawerLayout;
    private TextView mTitleTextView;
    private FloatingActionButton mFab;
    private FabAnimator mFabAnimator;

    private QuizSetCursorAdapter mQuizSetCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();

        if (D) Log.d(TAG, "setup content fragment");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, new QuizSetFragment(), QUIZ_SET_FRAGMENT_TAG)
                .commit();

        if (D) Log.d(TAG, "setup floating action button");
        mFab = (FloatingActionButton) findViewById(R.id.quiz_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 AddFabAnimator.transformToFullScreen(MainActivity.this, mFab);
                Intent intent = new Intent(MainActivity.this, EditQuizActivity.class);
                intent.putExtra(EditQuizActivity.EXTRA_QUIZ_ID, EditQuizActivity.NEW_QUIZ_ID);
                AddFabAnimator.transformToFullScreen(MainActivity.this, mFab);
                startActivity(intent);
            }
        });
        mFabAnimator = new FabAnimator(mFab);

        setupNavigationDrawer();

        mHandler =  new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == MSG_SHOW_ADD_QUIZ_DIALOG) {
                    DialogFragment dialog = new EditQuizSetDialogFragment();
                    dialog.show(getSupportFragmentManager(), "");
                }
            }
        };
    }

    private void setupToolbar() {
        if (D) Log.d(TAG, "setupToolbar");

        mTitleTextView = (TextView) findViewById(R.id.toolbar_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        try {
            setTitle(QuizSetDb.getCurrentName(this));
        } catch (PreferenceNotFound preferenceNotFound) {
            if (D) Log.d(TAG, preferenceNotFound.getMessage());

            QuizSetDb.setCurrent(this);
        }

        ImageView drawerImageView = (ImageView) findViewById(R.id.show_drawer_action);
        drawerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (D) Log.d(TAG, "open drawer");
                openDrawer();
            }
        });
    }

    private void setupNavigationDrawer() {
        if (D) Log.d(TAG, "setupNavigationDrawer()");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mQuizSetCursorAdapter = new QuizSetCursorAdapter(this, null, 0);

        View footerView = getLayoutInflater().inflate(R.layout.footer_view_quiz_set_list, null, false);

        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.addFooterView(footerView);
        drawerList.setAdapter(mQuizSetCursorAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == mQuizSetCursorAdapter.getCount()) {
                    DialogFragment dialog = EditQuizSetDialogFragment.newInstance(true);
                    dialog.show(getSupportFragmentManager(), "");
                } else {
                    Cursor c = (Cursor) mQuizSetCursorAdapter.getItem(position);
                    mTitleTextView.setText(c.getString(QuizSetEntry.INDEX_NAME));
                    closeDrawer();

                    AyudankiPreferences.setCurrentQuizSet(
                            MainActivity.this, c.getLong(QuizSetEntry.INDEX_ID));

                    mQuizSetCursorAdapter.setCurrentQuizSet(view);
                }

                // restart loader
                QuizSetFragment fragment = (QuizSetFragment) getSupportFragmentManager()
                        .findFragmentByTag(QUIZ_SET_FRAGMENT_TAG);
                fragment.getLoaderManager().restartLoader(QuizSetFragment.QUIZ_LOADER, null, fragment);
            }
        });

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open_description,
                R.string.drawer_close_description) {

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_SETTLING) {
                    if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        if (D) Log.d(TAG, "drawer is opening");

                        mFabAnimator.hideFab();
                    } else {
                        if (D) Log.d(TAG, "drawer is closing");


                        mFabAnimator.showFab();
                    }
                }
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().initLoader(QUIZ_SET_LOADER, null, this);

        mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
        mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add));
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem editMenuItem = menu.findItem(R.id.action_edit);
        Drawable editIcon = editMenuItem.getIcon();
        // If we don't mutate the drawable, then all drawable's with this id will have a color
        // filter applied to it.
        editIcon.mutate();
        editIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);

        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);

        mTitleTextView.setText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_edit:
                DialogFragment dialog = EditQuizSetDialogFragment.newInstance(false);
                dialog.show(getSupportFragmentManager(), "");
                return true;
            case R.id.action_delete:
                long quizSetId = -1;
                try {
                    quizSetId = QuizSetDb.getCurrentId(this);
                } catch (PreferenceNotFound preferenceNotFound) {
                    preferenceNotFound.printStackTrace();
                }

                ConfirmDeleteDialogFragment confirmDeleteDialogFragment =
                        ConfirmDeleteDialogFragment.newInstance(
                                ConfirmDeleteDialogFragment.TYPE_QUIZ_SET, quizSetId);
                confirmDeleteDialogFragment.show(getSupportFragmentManager(), null);

                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuizSetEntry.CONTENT_URI, QuizSetEntry.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (D) Log.d(TAG, "onLoadFinished");

        mQuizSetCursorAdapter.swapCursor(data);

        if (!data.moveToFirst()) {
            if (D) Log.d(TAG, "No quiz set in database");

            QuizSetDb.insertDefault(this);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQuizSetCursorAdapter.swapCursor(null);
    }

    public void restartLoaders() {
        getSupportLoaderManager().restartLoader(MainActivity.QUIZ_SET_LOADER, null, this);

        QuizSetFragment fragment = (QuizSetFragment) getSupportFragmentManager().findFragmentByTag(QUIZ_SET_FRAGMENT_TAG);
        getSupportLoaderManager().restartLoader(QuizSetFragment.QUIZ_LOADER, null, fragment);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }
}
