package com.genenakagaki.ayudanki.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.QuizActivity;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.data.CardDb;
import com.genenakagaki.ayudanki.data.model.Card;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MultipleChoiceFragment extends Fragment {

    private static final String TAG = MultipleChoiceFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    public static final int TYPE_REGULAR = 0;
    public static final int TYPE_REVERSE = 1;


    private static final String ARG_CARD_ID = "arg_card_id";
    private static final String ARG_CHOICES = "arg_choices";
    private static final String ARG_TYPE = "arg_type";

    private Card mCard;
    private ArrayList<String> mChoices;
    private int mType;

    public static MultipleChoiceFragment newInstance(long cardId, ArrayList<String> choices, int type) {
        Bundle args = new Bundle();
        args.putLong(ARG_CARD_ID, cardId);
        args.putStringArrayList(ARG_CHOICES, choices);
        args.putInt(ARG_TYPE, type);

        MultipleChoiceFragment fragment = new MultipleChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MultipleChoiceFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mCard = CardDb.getCard(getActivity(), args.getLong(ARG_CARD_ID));
            mChoices = args.getStringArrayList(ARG_CHOICES);
            mType = args.getInt(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_multiple_choice, container, false);

        TextView questionTextView = (TextView) rootView.findViewById(R.id.question_textview);

        if (mType == TYPE_REGULAR) {
            questionTextView.setText(mCard.getTerm());
        } else {
            questionTextView.setText(mCard.getDefinition());
        }

        CardView choiceCardView = (CardView) rootView.findViewById(R.id.choice0_cardview);
        TextView choiceTextView = (TextView) choiceCardView.findViewById(R.id.choice_left_textview);

        choiceTextView.setText(mChoices.get(0));
        choiceCardView.setVisibility(View.VISIBLE);

        if (mChoices.size() > 1) {
            choiceCardView = (CardView) rootView.findViewById(R.id.choice1_cardview);
            choiceTextView = (TextView) choiceCardView.findViewById(R.id.choice_right_textview);

            choiceTextView.setText(mChoices.get(1));
            choiceCardView.setVisibility(View.VISIBLE);
        }
        if (mChoices.size() > 2) {
            choiceCardView = (CardView) rootView.findViewById(R.id.choice2_cardview);
            choiceTextView = (TextView) choiceCardView.findViewById(R.id.choice_left_textview);

            choiceTextView.setText(mChoices.get(2));
            choiceCardView.setVisibility(View.VISIBLE);
        }
        if (mChoices.size() > 3) {
            choiceCardView = (CardView) rootView.findViewById(R.id.choice3_cardview);
            choiceTextView = (TextView) choiceCardView.findViewById(R.id.choice_right_textview);

            choiceTextView.setText(mChoices.get(3));
            choiceCardView.setVisibility(View.VISIBLE);
        }
        if (mChoices.size()  > 4) {
            choiceCardView = (CardView) rootView.findViewById(R.id.choice4_cardview);
            choiceTextView = (TextView) choiceCardView.findViewById(R.id.choice_left_textview);

            choiceTextView.setText(mChoices.get(4));
            choiceCardView.setVisibility(View.VISIBLE);
        }
        if (mChoices.size() > 5) {
            choiceCardView = (CardView) rootView.findViewById(R.id.choice5_cardview);
            choiceTextView = (TextView) choiceCardView.findViewById(R.id.choice_right_textview);

            choiceTextView.setText(mChoices.get(5));
            choiceCardView.setVisibility(View.VISIBLE);
        }


        return rootView;
    }



    public void onChoiceClick(View view) {
        CardView cardView = (CardView) view;
        TextView textView = (TextView) cardView.getChildAt(0);

        String string = textView.getText().toString();
        String answer = mCard.getDefinition();
        if (mType == TYPE_REVERSE) {
            answer = mCard.getTerm();
        }

        QuizActivity quizActivity = (QuizActivity) getActivity();
        if (string.equals(answer)) {
            cardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.correct));
            CardDb.addPoint(getActivity(), mCard.getId());
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.error));
        }

        ((QuizActivity) getActivity()).showFab();

        cardView = (CardView) getView().findViewById(R.id.choice0_cardview);
        cardView.setClickable(false);

        cardView = (CardView) getView().findViewById(R.id.choice1_cardview);
        cardView.setClickable(false);

        cardView = (CardView) getView().findViewById(R.id.choice2_cardview);
        cardView.setClickable(false);

        cardView = (CardView) getView().findViewById(R.id.choice3_cardview);
        cardView.setClickable(false);

        cardView = (CardView) getView().findViewById(R.id.choice4_cardview);
        cardView.setClickable(false);

        cardView = (CardView) getView().findViewById(R.id.choice5_cardview);
        cardView.setClickable(false);
    }
}
