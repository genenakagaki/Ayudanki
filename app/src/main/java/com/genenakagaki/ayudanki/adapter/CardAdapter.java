package com.genenakagaki.ayudanki.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.genenakagaki.ayudanki.BuildConfig;
import com.genenakagaki.ayudanki.R;

import java.util.ArrayList;

/**
 * Created by gene on 3/15/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private static final String TAG = CardAdapter.class.getSimpleName();
    private static final boolean D = BuildConfig.APP_DEBUG;

    private ArrayList<String> mTerms;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTermTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mTermTextView = (TextView) itemView.findViewById(R.id.term_textview);
        }
    }

    public CardAdapter(ArrayList<String> terms) {
        mTerms = terms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card_term, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTermTextView.setText(mTerms.get(position));
    }

    @Override
    public int getItemCount() {
        return mTerms.size();
    }


}
