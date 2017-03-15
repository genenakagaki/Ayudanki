package com.genenakagaki.ayudanki.animator;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by gene on 3/4/17.
 */

public class FabAnimator {

    private FloatingActionButton mFab;

    private ScaleAnimation mHideAnim;
    private ScaleAnimation mShowAnim;

    public FabAnimator(final FloatingActionButton fab) {
        mFab = fab;

        mHideAnim = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mHideAnim.setDuration(150);
        mHideAnim.setInterpolator(new DecelerateInterpolator());

        mShowAnim = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mShowAnim.setDuration(200);
        mShowAnim.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void hideFab() {
        mFab.clearAnimation();

        mHideAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {mFab.setEnabled(false);}

            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mFab.startAnimation(mHideAnim);
    }

    public void showFab() {
        mFab.clearAnimation();

        mShowAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mFab.setVisibility(View.VISIBLE);
                mFab.setEnabled(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mFab.startAnimation(mShowAnim);
    }

    public void switchFab(final FabAnimator fabAnimator) {
        mFab.clearAnimation();

        mHideAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setVisibility(View.GONE);
                fabAnimator.showFab();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mFab.startAnimation(mHideAnim);
    }


}
