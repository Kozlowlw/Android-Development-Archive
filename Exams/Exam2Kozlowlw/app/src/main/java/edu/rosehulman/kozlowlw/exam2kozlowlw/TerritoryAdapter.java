package edu.rosehulman.kozlowlw.exam2kozlowlw;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TerritoryAdapter extends RecyclerView.Adapter<TerritoryAdapter.ViewHolder> {

    private Context mContext;
    private AdapterCallback adapterCallback;
    private RecyclerView mRecyclerView;
    private Territory[] mStates;
    private ArrayList<Territory> mCurrentStates = new ArrayList<>();
    private Random mRandom = new Random();
    private boolean isCheckingCaptial;
    private int[] highlightedPositions;
    public static int score;


    public TerritoryAdapter(Context context, AdapterCallback adapterCallback) {
        mContext = context;
        score = 0;
        isCheckingCaptial = true;
        this.adapterCallback = adapterCallback;
        score = 0;
        ObjectMapper mapper = new ObjectMapper();
        try {
            mStates = mapper.readValue(context.getResources().openRawResource(R.raw.states), Territory[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.adapterCallback.changedTitle(mContext.getResources().getString(R.string.app_name));
        getRandomTerritory(5);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.territory_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TerritoryAdapter.ViewHolder holder, int position) {
        Territory currentState = mCurrentStates.get(position);
        holder.mStateTV.setText(currentState.getStateName());
        if (mCurrentStates.get(position).isHighlighted()) {
            holder.cv.setBackgroundColor(mContext.getResources().getColor(R.color.colorHighlight));
        } else {
            holder.cv.setBackgroundColor(mContext.getResources().getColor(R.color.cardview_light_background));
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return mCurrentStates.size();
    }

    public void getRandomTerritory(int limit) {
        this.adapterCallback.changedTitle(mContext.getResources().getString(R.string.app_name));
        score = 0;
        mCurrentStates = new ArrayList<>();
        ArrayList<Territory> tempStates = new ArrayList<>();
        for (Territory t : mStates) {
            tempStates.add(t);
        }
        Collections.shuffle(tempStates);
        for (int i = 0; i < limit; i++) {
            mCurrentStates.add(0, tempStates.get(i));
        }
        notifyDataSetChanged();

    }

    public void shuffleTerritory() {
        Collections.shuffle(mCurrentStates);
        for (int i = 0; i < mCurrentStates.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public void removeTerritory(int position) {
        mCurrentStates.remove(position);
        notifyItemRemoved(position);
        if (getItemCount() == 0) {
            Intent intent = new Intent(mContext, QuizOverActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCORE, this.getScore());
            mContext.startActivity(intent);
        }
    }

    public void setScore(int score) {
        this.score = score;
        this.adapterCallback.changedTitle(mContext.getString(R.string.score_reference) + " " + score);
    }

    public int getScore() {
        return this.score;
    }

    public void setHighlightedPositions() {
        if (mCurrentStates.size() > 1 && !isAnyHighlighted()) {
            highlightedPositions = new int[]{mRandom.nextInt(mCurrentStates.size()), mRandom.nextInt(mCurrentStates.size())};
            if (highlightedPositions[0] == highlightedPositions[1]) {
                highlightedPositions[0] = (highlightedPositions[1] + 1) % mCurrentStates.size();
            }
            mCurrentStates.get(highlightedPositions[0]).setHighlighted(true);
            mCurrentStates.get(highlightedPositions[1]).setHighlighted(true);
            this.isCheckingCaptial = false;
            notifyDataSetChanged();
        }
    }

    public boolean isAnyHighlighted() {
        return !isCheckingCaptial;
    }

    public interface AdapterCallback {
        void changedTitle(String s);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mStateTV;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            mStateTV = (TextView) itemView.findViewById(R.id.state_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCheckingCaptial) {
                        Snackbar snack = Snackbar.make(v, mCurrentStates.get(getAdapterPosition()).getStateCapital(), Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextSize(mContext.getResources().getDimension(R.dimen.text_large_zie) / mContext.getResources().getDisplayMetrics().density);
                        snack.show();
                    } else if (!isCheckingCaptial && (getAdapterPosition() == highlightedPositions[0] || getAdapterPosition() == highlightedPositions[1])) {
                        Territory me = mCurrentStates.get(getAdapterPosition());
                        Territory t1 = null;
                        if (getAdapterPosition() == highlightedPositions[0])
                            t1 = mCurrentStates.get(highlightedPositions[1]);
                        else
                            t1 = mCurrentStates.get(highlightedPositions[0]);
                        if (me.getStateArea() > t1.getStateArea()) {
                            Snackbar.make(v, mContext.getResources().getString(R.string.snack_yes_pre) + me.toStringArea() + mContext.getResources().getString(R.string.inequality) + t1.toStringArea(), Snackbar.LENGTH_INDEFINITE).show();
                            setScore(getScore() + 4);
                        } else {
                            Snackbar.make(v, mContext.getResources().getString(R.string.snack_no_pre) + t1.toStringArea() + mContext.getResources().getString(R.string.inequality) + me.toStringArea(), Snackbar.LENGTH_INDEFINITE).show();
                            setScore(getScore() - 3);
                        }
                        mCurrentStates.get(highlightedPositions[0]).setHighlighted(false);
                        mCurrentStates.get(highlightedPositions[1]).setHighlighted(false);
                        highlightedPositions = null;
                        isCheckingCaptial = true;
                        notifyDataSetChanged();
                    }
                }
            });
            cv = itemView.findViewById(R.id.card_view);

        }
    }
}
