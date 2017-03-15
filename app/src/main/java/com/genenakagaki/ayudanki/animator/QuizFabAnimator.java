package com.genenakagaki.ayudanki.animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.genenakagaki.ayudanki.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gene on 2/28/17.
 */

public class QuizFabAnimator {

    private Context mContext;

    private FloatingActionButton mFab;
    private RelativeLayout mFabContainer;
    private LinearLayout mFabToolbar;

    private Drawable mFabDrawable;

    private ObjectAnimator mXAnimator;
    private ObjectAnimator mYAnimator;
    private ObjectAnimator mScaleAnimator;

    private FabAnimator mFabAnimator;

    private boolean mIsToolbar = false;

    public QuizFabAnimator(View rootView) {
        mContext = rootView.getContext();

        mFab = (FloatingActionButton) rootView.findViewById(R.id.quiz_fab);
        mFabContainer = (RelativeLayout) rootView.findViewById(R.id.quiz_fab_container);
        mFabToolbar = (LinearLayout) rootView.findViewById(R.id.quiz_fab_toolbar);

        mFabAnimator = new FabAnimator(mFab);
    }

    public void showToolbar() {
        mIsToolbar = true;

        List<Animator> animators = new ArrayList<>();

        // translate animation
        float endX = mFabContainer.getWidth()/2 - mFab.getWidth() / 2;
        float endY = mFabContainer.getY() + mContext.getResources().getDimension(R.dimen.fab_margin);

        mXAnimator = ObjectAnimator.ofFloat(mFab, "x", endX);
        mYAnimator = ObjectAnimator.ofFloat(mFabContainer, "y", endY);

        mXAnimator.setInterpolator(new AccelerateInterpolator());
        mYAnimator.setInterpolator(new DecelerateInterpolator());

        mXAnimator.setDuration(150);
        mYAnimator.setDuration(150);

        animators.add(mXAnimator);
        animators.add(mYAnimator);

        // fab drawable
        mFabDrawable = mFab.getDrawable();
        mFab.setImageDrawable(null);

        // scale animation
        float scale = (float)Math.ceil((float)mFabContainer.getWidth() / mFab.getWidth());

        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", scale);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", scale);

        mScaleAnimator = ObjectAnimator.ofPropertyValuesHolder(mFab, pvhScaleX, pvhScaleY);
        mScaleAnimator.setDuration(150);
        mScaleAnimator.setStartDelay(100);

        animators.add(mScaleAnimator);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFab.setVisibility(View.INVISIBLE);
                mFabToolbar.setVisibility(View.VISIBLE);
            }
        });
        animatorSet.start();
    }

    public void hideToolbar() {
        mIsToolbar = false;

        mFabToolbar.setVisibility(View.INVISIBLE);
        mFab.setVisibility(View.VISIBLE);

        List<Animator> reverseAnimators = new ArrayList<>();

        mScaleAnimator.setDuration(100);
        mScaleAnimator.setStartDelay(0);
        mScaleAnimator.reverse();
        mXAnimator.setDuration(100);
        mXAnimator.setStartDelay(80);
        mYAnimator.setDuration(100);
        mYAnimator.setStartDelay(80);

        mXAnimator.reverse();
        mYAnimator.reverse();

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(reverseAnimators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFab.setImageDrawable(mFabDrawable);
            }
        });
        animatorSet.start();
    }

    public void hideFab() {
        if (mIsToolbar) {
            hideToolbar();
        }

        mFabAnimator.hideFab();
    }

    public void showFab() {
        mFabAnimator.showFab();
    }
}
