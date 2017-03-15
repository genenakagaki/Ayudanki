package com.genenakagaki.ayudanki.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.R;

/**
 * Created by gene on 3/10/17.
 */

public class TermDefinitionFragment extends Fragment {

    private static final String TAG = TermDefinitionFragment.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private static final String ARG_TERM = "arg_term";
    private static final String ARG_DEFINITION = "arg_definition";

    private String mTerm;
    private String mDefinition;

    public static TermDefinitionFragment newInstance(String term, String definition) {
        Bundle args = new Bundle();
        args.putString(ARG_TERM, term);
        args.putString(ARG_DEFINITION, definition);

        TermDefinitionFragment fragment = new TermDefinitionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TermDefinitionFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mTerm = args.getString(ARG_TERM);
            mDefinition = args.getString(ARG_DEFINITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_term_definition, container, false);

        TextView termTextView = (TextView) rootView.findViewById(R.id.question_textview);
        TextView definitionTextView = (TextView) rootView.findViewById(R.id.definition_textview);

        termTextView.setText(mTerm);
        definitionTextView.setText(mDefinition);

        return rootView;
    }
}
