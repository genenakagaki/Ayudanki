package com.genenakagaki.ayudanki.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.MainActivity;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.data.AyudankiPreferences;
import com.genenakagaki.ayudanki.data.QuizDb;
import com.genenakagaki.ayudanki.data.QuizSetDb;
import com.genenakagaki.ayudanki.exception.NameAlreadyExistsException;
import com.genenakagaki.ayudanki.exception.PreferenceNotFound;
import com.genenakagaki.ayudanki.utility.TextInputValidator;

import static com.genenakagaki.ayudanki.data.QuizContract.*;

public class EditQuizSetDialogFragment extends DialogFragment {

    private static final String TAG = EditQuizSetDialogFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private static final String ARG_IS_NEW_QUIZ_SET = "arg_is_new_quiz_set";

    private boolean mIsNewQuizSet;

    private TextInputEditText mQuizSetTextInput;
    private TextInputLayout mQuizSetInputLayout;
    private TextInputValidator mQuizSetNameValidator;

    public static EditQuizSetDialogFragment newInstance(boolean isNewQuizSet) {
        EditQuizSetDialogFragment fragment = new EditQuizSetDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_QUIZ_SET, isNewQuizSet);
        fragment.setArguments(args);
        return fragment;
    }

    public EditQuizSetDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mIsNewQuizSet = args.getBoolean(ARG_IS_NEW_QUIZ_SET);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_quiz_set, null);
        mQuizSetTextInput = (TextInputEditText) view.findViewById(R.id.quiz_set_name_textinput);
        mQuizSetInputLayout = (TextInputLayout) view.findViewById(R.id.quiz_set_name_inputlayout);

        mQuizSetNameValidator = new TextInputValidator(
                mQuizSetInputLayout,
                mQuizSetTextInput,
                getString(R.string.input_quiz_set_empty_error));

        String dialogTitle;
        if (mIsNewQuizSet) {
            dialogTitle = getString(R.string.dialog_add_quiz_set_title);
        } else {
            dialogTitle = getString(R.string.dialog_edit_quiz_set_title);

            // set textinput text to current QuizSet name
            try {
                mQuizSetTextInput.setText(QuizSetDb.getCurrentName(getActivity()));
            } catch (PreferenceNotFound preferenceNotFound) {
                if (D) Log.d(TAG, preferenceNotFound.getMessage());
            }
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle)
                .setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // this button is overridden in onStart() to prevent dialog from closing
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        // super.onStart() is where dialog.show() is called on the underlying dialog,
        // so we set the onClickListener here to prevent dialog from closing after pressing save
        super.onStart();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null)
        {
            Button positiveButton = (Button) dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mQuizSetNameValidator.validate();

                    if (mQuizSetNameValidator.isValid()) {
                        if (mIsNewQuizSet) {
                            addQuizSet(dialog);
                        } else {
                            updateQuizSet(dialog);
                        }
                    }
                }
            });
        }
    }

    private void addQuizSet(AlertDialog dialog) {
        String quizSetName = mQuizSetTextInput.getText().toString().trim();

        Uri quizSetUri = null;
        try {
            quizSetUri = QuizSetDb.insert(getActivity(), quizSetName);
        } catch (NameAlreadyExistsException e) {
            if (D) Log.d(TAG, e.getMessage());

            mQuizSetNameValidator.showError(getString(R.string.input_quiz_set_duplicate_error));
            return;
        }

        AyudankiPreferences.setCurrentQuizSet(getActivity(), quizSetUri);
        dialog.dismiss();
        getActivity().setTitle(quizSetName);
        ((MainActivity)getActivity()).restartLoaders();
        ((MainActivity)getActivity()).closeDrawer();
    }

    private void updateQuizSet(AlertDialog dialog) {
        String quizSetName = mQuizSetTextInput.getText().toString().trim();

        try {
            QuizSetDb.update(getActivity(), quizSetName);
        } catch (NameAlreadyExistsException e) {
            if (D) Log.d(TAG, e.getMessage());

            mQuizSetNameValidator.showError(getString(R.string.input_quiz_set_duplicate_error));
            return;
        } catch (PreferenceNotFound preferenceNotFound) {
            if (D) Log.d(TAG, preferenceNotFound.getMessage());
        }

        dialog.dismiss();
        getActivity().setTitle(quizSetName);
    }
}
