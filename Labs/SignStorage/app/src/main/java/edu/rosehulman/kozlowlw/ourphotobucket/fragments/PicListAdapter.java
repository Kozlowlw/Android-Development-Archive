package edu.rosehulman.kozlowlw.ourphotobucket.fragments;

import android.graphics.Bitmap;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.kozlowlw.ourphotobucket.MainActivity;
import edu.rosehulman.kozlowlw.ourphotobucket.R;
import edu.rosehulman.kozlowlw.ourphotobucket.models.Pic;
import edu.rosehulman.kozlowlw.ourphotobucket.Constants;

public class PicListAdapter extends RecyclerView.Adapter<PicListAdapter.ViewHolder>{
    private List<Pic> mPics;
    private PicListFragment.OnPicSelectedListener mPicSelectedListener;
    private DatabaseReference mRef;

    public PicListAdapter(PicListFragment.OnPicSelectedListener picSelectedListener, boolean viewState){
        mPics = new ArrayList<>();
        mPicSelectedListener = picSelectedListener;
        mRef = FirebaseDatabase.getInstance().getReference();
        Query query = mRef.child("pic").orderByChild("uid").equalTo(Constants.Current_UID);
        query.addChildEventListener(new PicChildEventListener());

    }

    @NonNull
    @Override
    public PicListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_pic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicListAdapter.ViewHolder holder, int position) {
        final Pic pic = mPics.get(position);
        holder.captionView.setText(pic.getCaption());
        holder.urlView.setText(pic.getUrl());
        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicSelectedListener.onPicSelected(pic);
            }
        });
        holder.mCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MainActivity.showAddEditDialog(pic, holder.mCard.getContext());
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPics.size();
    }

    public void delete(Pic pic) {
        mRef.child("pic").child(pic.getKey()).removeValue();
    }

    public void update(Pic pic, String s, String s1) {
        pic.setCaption(s);
        pic.setUrl(s1);
        mRef.child("pic").child(pic.getKey()).setValue(pic);
    }

    public void add(Pic newPic) {
        newPic.setUid(Constants.Current_UID);
        mRef.child("pic").push().setValue(newPic);
    }

    public void showAllPics(boolean showAll){
        if(showAll){
            mRef.child("pic").addChildEventListener(new PicChildEventListener());
            //mRef.keepSynced(true);
        }else{
            Query query = mRef.child("pic").orderByChild("uid").equalTo(Constants.Current_UID);
            query.addChildEventListener(new PicChildEventListener());
            //query.keepSynced(true);
        }
    }

    public void addPicture(String name, String location, Bitmap bitmap){
        Pic pic = new Pic();
        pic.setCaption(name);
        pic.setUrl(location);
        pic.setBitmap(bitmap);
    }

    private class PicChildEventListener implements ChildEventListener{

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Pic pic = dataSnapshot.getValue(Pic.class);
            mPics.add(pic);
            pic.setKey(dataSnapshot.getKey());
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String keyTOChange = dataSnapshot.getKey();
            for(Pic pic : mPics){
                if(pic.getKey().equals(keyTOChange)){
                    pic.setCaption((String) dataSnapshot.child("caption").getValue());
                    pic.setUrl((String) dataSnapshot.child("url").getValue());
                    break;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String keyTODelete = dataSnapshot.getKey();

            for(Pic pic : mPics){
                if(pic.getKey().equals(keyTODelete)){
                    mPics.remove(pic);
                    break;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mCard;
        TextView captionView;
        TextView urlView;

        public ViewHolder(View itemView) {
            super(itemView);
            mCard = itemView;
            captionView = (TextView) itemView.findViewById(R.id.pic_list_caption);
            urlView = (TextView) itemView.findViewById(R.id.pic_list_url);
        }
    }
}
