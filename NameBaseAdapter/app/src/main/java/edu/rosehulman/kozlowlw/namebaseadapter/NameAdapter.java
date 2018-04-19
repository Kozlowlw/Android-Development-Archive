package edu.rosehulman.kozlowlw.namebaseadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kozlowlw on 3/22/2018.
 */

public class NameAdapter extends BaseAdapter {

    private Context mContext;
    final ArrayList<String> mNames = new ArrayList<>();
    private Random mRandom = new Random();


    public NameAdapter(Context context) {
        mContext = context;
        for (int i = 0; i < 5; i++) {
            mNames.add(getRandomName());
        }
    }

    @Override
    public int getCount() {
        return mNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.name_view, parent, false);
        }else{
            view = convertView;
        }

        String name = mNames.get(position);
        TextView nameTV = (TextView) view.findViewById(R.id.name_view);
        nameTV.setText(name);
        TextView posTV = (TextView) view.findViewById(R.id.position_view);
        posTV.setText(String.format("Im #%d", position+1));

        return view;
    }

    private String getRandomName() {
        String[] names = new String[]{
                "Hannah", "Emily", "Sarah", "Madison", "Brianna",
                "Kaylee", "Kaitlyn", "Hailey", "Alexis", "Elizabeth",
                "Michael", "Jacob", "Matthew", "Nicholas", "Christopher",
                "Joseph", "Zachary", "Joshua", "Andrew", "William"
        };
        return names[mRandom.nextInt(names.length)];

    }

    public void addName(){
        mNames.add(0, getRandomName());
        notifyDataSetChanged();
    }

    public void removeName(int position){
        mNames.remove(position);
        notifyDataSetChanged();
    }

}
