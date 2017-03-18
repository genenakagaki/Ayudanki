package com.genenakagaki.ayudanki.quiz;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.data.CardDb;
import com.genenakagaki.ayudanki.QuizActivity;
import com.genenakagaki.ayudanki.data.model.Card;
import com.genenakagaki.ayudanki.fragment.MultipleChoiceFragment;
import com.genenakagaki.ayudanki.fragment.QuizResultFragment;
import com.genenakagaki.ayudanki.fragment.TermDefinitionFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by gene on 3/10/17.
 */

public class StudyQuiz {

    private static final String TAG = StudyQuiz.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private static final int NUM_TERMS_TO_STUDY = 6;

    private static final Integer[] defaultOrder = new Integer[] {
            0,
            1,
            100,
            101,
            2,
            200,
            102,
            3,
            201,
            103,
            4,
            202,
            104,
            5,
            203,
            105,
            200,
            201,
            204,
            205,
            202,
            203,
            204,
            205,
    };

    private Context mContext;
    private long mQuizId;

    private List<Card> mCards;

    private List<Integer> mCardOrder;
    private int mCurrentIndex;

    private int mCardCount;

    public StudyQuiz(Context context, long quizId) {
        mContext = context;
        mQuizId = quizId;
    }

    public void start() {
        if (D) Log.d(TAG, "start()");

        mCards = CardDb.getCardList(mContext, mQuizId);

        if (mCards.size() >= NUM_TERMS_TO_STUDY) {
            mCardCount = NUM_TERMS_TO_STUDY;
            mCardOrder = new ArrayList<>(Arrays.asList(defaultOrder));
        } else {
            mCardCount = mCards.size();

            for (int i = 0; i < defaultOrder.length; i++) {
                int index = defaultOrder[i];

                if (index >= 100) {
                    index = defaultOrder[i] - 100;
                }

                if (index == mCards.size()) {
                    mCardOrder = new ArrayList<>(Arrays.asList(defaultOrder).subList(0, i));
                    for (int j = 0; j < mCards.size(); j++) {
                        mCardOrder.add(j + 200);
                    }
                    break;
                }
            }
        }

        if (D) Log.d(TAG, "mCardOrder created");

        mCurrentIndex = 0;

        ((QuizActivity)mContext).setProgressMax(mCardOrder.size());

        createNextQuestion();
    }

    public void createNextQuestion() {
        ((QuizActivity)mContext).setProgressBar(mCurrentIndex);

        if (mCurrentIndex == mCardOrder.size()) {
            if (D) Log.d(TAG, "show quiz results");

            ArrayList<String> terms = new ArrayList<String>();

            for (int i = 0; i < mCardCount; i++) {
                terms.add(mCards.get(i).getTerm());
            }

            ((QuizActivity)mContext).switchFragment(QuizResultFragment.newInstance(terms));
            return;
        }

        int currentCardOrder = mCardOrder.get(mCurrentIndex);
        int currentCardIndex = currentCardOrder;

        // 100: MultipleChoice regular
        // 200: MultipleChoice reverse

        if (currentCardOrder < 100) {
            Card card = mCards.get(currentCardIndex);

            Fragment fragment = TermDefinitionFragment.newInstance(card.getTerm(), card.getDefinition());
            ((QuizActivity) mContext).switchFragment(fragment);
        } else if (currentCardOrder >= 100) {
            if (currentCardOrder < 200) {
                currentCardIndex -= 100;
            } else {
                currentCardIndex -= 200;
            }

            // create choiceCards
            ArrayList<Card> choiceCards = new ArrayList(mCards);
            Collections.shuffle(choiceCards);

            Card card = mCards.get(currentCardIndex);
            boolean answerInChoices = false;

            for (int i = 0; i < mCardCount; i++) {
                if (choiceCards.get(i).equals(card)) {
                    answerInChoices = true;
                }
            }

            if (!answerInChoices) {
                Random rand = new Random();
                int max = mCardCount - 1;
                int answerPos = rand.nextInt(max + 1);

                choiceCards.add(answerPos, card);
            }

            if (currentCardOrder < 200) {
                if (D) Log.d(TAG, "Regular MultipleChoice");

                ArrayList<String> choices = new ArrayList<>();

                for (int i = 0; i < mCardCount; i++) {
                    choices.add(choiceCards.get(i).getDefinition());
                }

                Fragment fragment = MultipleChoiceFragment.newInstance(
                        card.getId(), choices, MultipleChoiceFragment.TYPE_REGULAR);
                ((QuizActivity) mContext).switchFragment(fragment);
            } else {
                if (D) Log.d(TAG, "Reversed MultipleChoice");

                ArrayList<String> choices = new ArrayList<>();

                for (int i = 0; i < mCardCount; i++) {
                    choices.add(choiceCards.get(i).getTerm());
                }

                Fragment fragment = MultipleChoiceFragment.newInstance(
                        card.getId(), choices, MultipleChoiceFragment.TYPE_REVERSE);
                ((QuizActivity) mContext).switchFragment(fragment);
            }
        }

        mCurrentIndex++;
    }

    public void setNextAsTermDefinition() {
        mCurrentIndex--;

        int currentCardOrder = mCardOrder.get(mCurrentIndex);

        while (currentCardOrder >= 100) {
            currentCardOrder -= 100;
        }

        if (D) Log.d(TAG, "current card order: " + currentCardOrder);

        mCardOrder.set(mCurrentIndex, currentCardOrder);
    }
}
