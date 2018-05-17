package edu.rosehulman.kozlowlw.namerecycleradapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Kozlowlw on 3/22/2018.
 */

public class NameAdapter extends RecyclerView.Adapter<NameAdapter.ViewHolder> {

    private Context mContext;
    final ArrayList<String> mNames = new ArrayList<>();
    private Random mRandom = new Random();
    private RecyclerView mRecyclerView;


    public NameAdapter(Context context) {
        mContext = context;
        for (int i = 0; i < 5; i++) {
            mNames.add(getRandomName());
        }
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        mRecyclerView = recyclerView;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = mNames.get(position);
        holder.nameTV.setText(name);
        holder.posTV.setText(String.format("Im #%d", position+1));
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
        notifyItemInserted(0);
        //notifyDataSetChanged();
        mRecyclerView.getLayoutManager().scrollToPosition(0);
        itemChanged();

    }

    public void removeName(int position){
        mNames.remove(position);
        notifyItemRemoved(position);
        //notifyDataSetChanged();
        itemChanged();
    }

    /**
     * Just loops through all items and notifies there was a change
     * Quick fix for the position numbering
     */
    public void itemChanged(){
        for (int i = 0; i < mNames.size(); i++){
            notifyItemChanged(i);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTV;
        private TextView posTV;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTV = (TextView) itemView.findViewById(R.id.name_view);
            posTV = (TextView) itemView.findViewById(R.id.position_view);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeName(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
