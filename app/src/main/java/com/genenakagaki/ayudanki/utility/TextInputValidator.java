package com.genenakagaki.ayudanki.utility;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.genenakagaki.ayudanki.BuildConfig;

/**
 * Created by gene on 2/16/17.
 */

public class TextInputValidator implements View.OnFocusChangeListener {

    private static final String TAG = TextInputValidator.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private TextInputLayout mInputLayout;
    private TextInputEditText mInput;
    private String mErrorMessage;

    private boolean mIsValid = false;

    public TextInputValidator(
            TextInputLayout inputLayout,
            TextInputEditText input,
            String errorMessage) {
        mInputLayout = inputLayout;
        mInput = input;
        mErrorMessage = errorMessage;

        mInput.setOnFocusChangeListener(this);
    }

    public boolean isValid() {
        return mIsValid;
    }

    public void validate() {
        if (mInput.getText().toString().trim().isEmpty()) {
            mIsValid = false;
            showError(mErrorMessage);
        } else {
            mIsValid = true;
        }
    }

    public void showError(String errorMessage) {
        mInputLayout.setErrorEnabled(true);
        mInputLayout.setError(errorMessage);
    }

    public void hideError() {
        mInputLayout.setErrorEnabled(false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mInputLayout.setErrorEnabled(false);
        } else {
            validate();
        }
    }
}
