package com.genenakagaki.ayudanki.animator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.genenakagaki.ayudanki.R;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.endY;
import static java.security.AccessController.getContext;

/**
 * Created by gene on 3/5/17.
 */

public class AddFabAnimator {

    private static ColorStateList mFabBackgroundColor;
    private static Drawable mFabDrawable;

    private static ScaleAnimation createFullScreenFabAnimation() {
        ScaleAnimation anim = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(150);
        anim.setInterpolator(new DecelerateInterpolator());

        return anim;
    }

    public static void transformToFullScreen(final Context context, final FloatingActionButton fab) {
        // Get screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;

        AnimationSet animationSet = new AnimationSet(false);

        // expand fab to cover whole width of screen
        float scale = (float)Math.ceil((float)screenWidth*2 / fab.getWidth());

        ScaleAnimation fabScaleAnim = new ScaleAnimation(1f, scale, 1f, scale,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fabScaleAnim.setDuration(150);
        fabScaleAnim.setInterpolator(new DecelerateInterpolator());

        animationSet.addAnimation(fabScaleAnim);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent)));
                fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // change background color
        mFabBackgroundColor = fab.getBackgroundTintList();
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));

        mFabDrawable = fab.getDrawable();
        fab.setImageDrawable(null);

        // move up
        RelativeLayout parent = (RelativeLayout) fab.getParent();
        ScaleAnimation parentScaleAnim = new ScaleAnimation(
                1f, 1f, 1f, 3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        parentScaleAnim.setDuration(150);

        parent.startAnimation(parentScaleAnim);
        fab.startAnimation(animationSet);

    }
}
