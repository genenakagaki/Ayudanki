package com.genenakagaki.ayudanki.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.R;
import com.genenakagaki.ayudanki.adapter.CardAdapter;

import java.util.ArrayList;

/**
 * Created by gene on 3/14/17.
 */

public class QuizResultFragment extends Fragment {

    private static final String TAG = QuizResultFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private static final String ARG_TERMS = "arg_terms";

    private ArrayList<String> mTerms;

    public static QuizResultFragment newInstance( ArrayList<String> terms) {
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TERMS, terms);

        QuizResultFragment fragment = new QuizResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public QuizResultFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mTerms = args.getStringArrayList(ARG_TERMS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quiz_result, container, false);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        CardAdapter mAdapter = new CardAdapter(mTerms);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }
}
