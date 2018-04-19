package edu.rosehulman.kozlowlw.foodraterrecycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kozlowlw on 3/29/2018.
 */

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private Context mContext;
    final ArrayList<Food> mFood = new ArrayList<Food>();
    private Random mRandom = new Random();
    private RecyclerView mRecyclerView;
    final ArrayList<String> usedFood = new ArrayList<String>();
    private int startFoodCount = 3;

    public FoodAdapter(Context context) {
        mContext = context;
        for (int i = 0; i < startFoodCount; i++) {
            mFood.add(getRandomFood());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(FoodAdapter.ViewHolder holder, int position) {
        Food currentFood = mFood.get(position);
        holder.foodIV.setImageResource(currentFood.getID());
        holder.foodTV.setText(currentFood.getName());
        holder.foodRB.setRating(currentFood.getRating());
    }


    @Override
    public int getItemCount() {
        return mFood.size();
    }

    public void itemsChanged() {
        for (int i = 0; i < mFood.size(); i++) {
            notifyItemChanged(i);
        }
    }

    public Food getRandomFood() {
        String[] names = new String[]{"banana", "broccoli", "homemade bread",
                "chicken", "chocolate", "ice cream", "lima beans", "steak"};
        int i = mRandom.nextInt(names.length);
        if (mFood.size() < names.length) {
            while (true) {
                if (!usedFood.contains(names[i])) {
                    usedFood.add(names[i]);
                    return new Food(names[i], getID(names[i]), 0);
                }
                i = mRandom.nextInt(names.length);
            }
        }
        return null;
    }

    public int getID(String name) {
        switch (name) {
            case "banana":
                return R.drawable.banana;
            case "broccoli":
                return R.drawable.broccoli;
            case "homemade bread":
                return R.drawable.bread;
            case "chicken":
                return R.drawable.chicken;
            case "chocolate":
                return R.drawable.chocolate;
            case "ice cream":
                return R.drawable.icecream;
            case "lima beans":
                return R.drawable.limabeans;
            case "steak":
                return R.drawable.steak;
            default:
                return 0;
        }
    }

    public boolean addFood() {
        Food tmp = getRandomFood();
        if (tmp != null) {
            mFood.add(0, tmp);
            notifyItemInserted(0);
            mRecyclerView.getLayoutManager().scrollToPosition(0);
            itemsChanged();
            return true;
        }
        return false;
    }

    public void removeFood(int position) {
        usedFood.remove(mFood.get(position).getName());
        mFood.remove(position);
        notifyItemRemoved(position);
        itemsChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView foodIV;
        private TextView foodTV;
        private RatingBar foodRB;

        public ViewHolder(View itemView) {
            super(itemView);
            foodIV = (ImageView) itemView.findViewById(R.id.food_image);
            foodTV = (TextView) itemView.findViewById(R.id.food_text);
            foodRB = (RatingBar) itemView.findViewById(R.id.food_rating);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeFood(getAdapterPosition());
                    return true;
                }
            });
            foodRB.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(fromUser){
                        mFood.get(getAdapterPosition()).setRating((int)rating);
                        ratingBar.setRating(mFood.get(getAdapterPosition()).getRating());
                        ratingBar .setNumStars(5);
                    }
                }
            });
        }
    }
}
