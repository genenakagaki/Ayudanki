package com.genenakagaki.ayudanki.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import com.genenakagaki.ayudanki.QuizInfoActivity;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.data.CardDb;
import com.genenakagaki.ayudanki.data.QuizContract;
import com.genenakagaki.ayudanki.exception.NameAlreadyExistsException;
import com.genenakagaki.ayudanki.utility.TextInputValidator;

/**
 * Created by gene on 3/2/17.
 */

public class EditCardDialogFragment extends DialogFragment {

    private static final String TAG = EditCardDialogFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final long NEW_CARD_ID = -1;

    private static final String ARG_CARD_ID = "arg_card_id";

    private long mCardId;

    private TextInputEditText mTermInput;
    private TextInputValidator mTermValidator;

    private TextInputEditText mDefinitionInput;
    private TextInputValidator mDefinitionValidator;

    private boolean mIsValid = false;

    public static EditCardDialogFragment newInstance(long cardId) {
        Bundle args = new Bundle();
        args.putLong(ARG_CARD_ID, cardId);

        EditCardDialogFragment fragment = new EditCardDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EditCardDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mCardId = args.getLong(ARG_CARD_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_card, null);

        mTermInput = (TextInputEditText) view.findViewById(R.id.card_term_textinput);
        TextInputLayout termInputLayout = (TextInputLayout) view.findViewById(R.id.card_term_inputlayout);
        mTermValidator = new TextInputValidator(
                termInputLayout, mTermInput, getString(R.string.input_term_empty_error));

        mDefinitionInput = (TextInputEditText) view.findViewById(R.id.card_definition_textinput);
        TextInputLayout definitionInputLayout = (TextInputLayout) view.findViewById(R.id.card_definition_inputlayout);
        mDefinitionValidator = new TextInputValidator(
                definitionInputLayout, mDefinitionInput, getString(R.string.input_definition_empty_error));

        if (mCardId != NEW_CARD_ID) {
            // enter existing values of card
            Cursor c = getActivity().getContentResolver().query(
                    QuizContract.CardEntry.buildCardUri(mCardId),
                    QuizContract.CardEntry.COLUMNS,
                    null, null, null);
            c.moveToFirst();

            mTermInput.setText(c.getString(QuizContract.CardEntry.INDEX_TERM));
            mDefinitionInput.setText(c.getString(QuizContract.CardEntry.INDEX_DEFINITION));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // this button is overridden in onStart() to prevent dialog from closing
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
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
                    mTermValidator.validate();
                    mDefinitionValidator.validate();

                    if (mTermValidator.isValid() && mDefinitionValidator.isValid()) {
                        String cardTerm = mTermInput.getText().toString().trim();
                        String cardDefinition = mDefinitionInput.getText().toString().trim();


                        if (mCardId == NEW_CARD_ID) {
                            addCard(dialog, cardTerm, cardDefinition);
                        } else {
                            updateCard(dialog, cardTerm, cardDefinition);
                        }
                    }


                    if(mIsValid)
                        dialog.dismiss();
                }
            });
        }
    }

    private void addCard(AlertDialog dialog, String cardTerm, String cardDefinition) {
        if (D) Log.d(TAG, "addCard()");

        long quizId = ((QuizInfoActivity)getActivity()).getQuizId();

        try {
            CardDb.insert(getActivity(), quizId, cardTerm, cardDefinition);
        } catch (NameAlreadyExistsException e) {
            if (D) Log.d(TAG, e.getMessage());

            mTermValidator.showError(getString(R.string.input_term_duplicate_error));
            return;
        }

        dialog.dismiss();
    }

    private void updateCard(AlertDialog dialog, String cardTerm, String cardDefinition) {
        if (D) Log.d(TAG, "udpateCard");

        long quizId = ((QuizInfoActivity)getActivity()).getQuizId();

        try {
            CardDb.update(getActivity(), mCardId, quizId, cardTerm, cardDefinition);
        } catch (NameAlreadyExistsException e) {
            if (D) Log.d(TAG, e.getMessage());

            mTermValidator.showError(getString(R.string.input_term_duplicate_error));
            return;
        }

        dialog.dismiss();
    }
}
