package edu.rosehulman.kozlowlw.foodraterlistview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Kozlowlw on 3/29/2018.
 */

public class FoodAdapter extends BaseAdapter {

    private Context mContext;
    private Random mRandom = new Random();
    private ArrayList<Food> mFood = new ArrayList<Food>();

    public FoodAdapter(Context context) {
        mContext = context;
        getFood(false);

    }

    @Override
    public int getCount() {
        return mFood.size();
    }

    @Override
    public Food getItem(int position) {
        return mFood.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null)
            view = LayoutInflater.from(mContext).inflate(R.layout.food_view, parent, false);
        else
            view = convertView;

        Log.d("TTT", mFood.get(position).toString());

        ImageView foodIV = (ImageView) view.findViewById(R.id.food_image);
        foodIV.setImageResource(mFood.get(position).getID());

        TextView nameTV = (TextView) view.findViewById(R.id.food_text);
        nameTV.setText(mFood.get(position).getName());

        RatingBar foodRB = (RatingBar) view.findViewById(R.id.food_rating);
        foodRB.setNumStars(mFood.get(position).getRating());
        foodRB.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser){
                    mFood.get(position).setRating((int)rating);
                    ratingBar.setRating(mFood.get(position).getRating());
                    ratingBar.setNumStars(5);
                }
            }
        });

        return view;
    }

    private void getFood(boolean isRandom) {
        String[] names = new String[]{"banana", "broccoli", "homemade bread",
                "chicken", "chocolate", "ice cream", "lima beans", "steak"};
        if (!isRandom) {
            //Arbritary offset so we can add stuff later
            for (int i = 0; i < names.length-3; i++) {
                mFood.add(new Food(names[i], getID(names[i]), 0));
            }
        } else {
            int i = mRandom.nextInt(names.length);
            mFood.add(0, new Food(names[i], getID(names[i]), 0));
        }
    }

    public void addFood() {
        getFood(true);
        notifyDataSetChanged();
    }

    public void removeFood(int position) {
        mFood.remove(position);
        notifyDataSetChanged();
    }

    public int getID(String name){
        switch(name){
            case "banana": return R.drawable.banana;
            case "broccoli": return R.drawable.broccoli;
            case "homemade bread": return R.drawable.bread;
            case "chicken":return R.drawable.chicken;
            case "chocolate":return R.drawable.chocolate;
            case "ice cream": return R.drawable.icecream;
            case "lima beans": return R.drawable.limabeans;
            case "steak": return R.drawable.steak;
            default: return 0;
        }
    }
}
