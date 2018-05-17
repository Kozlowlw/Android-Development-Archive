package edu.rosehulman.kozlowlw.photobucket2.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import edu.rosehulman.kozlowlw.photobucket2.Pic;
import edu.rosehulman.kozlowlw.photobucket2.R;
import edu.rosehulman.kozlowlw.photobucket2.Util;

public class PicListAdapter extends RecyclerView.Adapter<PicListAdapter.ViewHolder> {

    private ArrayList<Pic> mPics;
    private PicListFragment.Callback mCallback;
    private DatabaseReference mPicRef;
    private StorageReference mPicStorageRef;
    private DatabaseReference mTitleRef;
    private PicListAdapter adapter;
    private Query mQuery;
    private PicChildEventListener mPicChildEventListener;
    private boolean queryHasListener = false, refHasListener = false;


    public PicListAdapter(PicListFragment.Callback callback) {
        mCallback = callback;
        mPics = new ArrayList<>();
        mPicRef = FirebaseDatabase.getInstance().getReference().child("pics");
        mPicChildEventListener = new PicChildEventListener();
        mPicStorageRef = FirebaseStorage.getInstance().getReference().child("pics");
        updateList(Constants.viewAll);
        mTitleRef = FirebaseDatabase.getInstance().getReference().child("app");
        mTitleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCallback.changedTitle((String) dataSnapshot.getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = this;
    }

    @Override
    public PicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_pic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PicListAdapter.ViewHolder holder, int position) {
        final Pic pic = mPics.get(position);
        holder.mCaption.setText(pic.getCaption());
        if (pic.getUrl().length() > 120)
            holder.mURL.setText(pic.getUrl().substring(0, 117)+"...");
        else
            holder.mURL.setText(pic.getUrl());

    }

    @Override
    public int getItemCount() {
        return mPics.size();
    }

    public void update(Pic pic, String caption, String url, String uid) {
        pic.setCaption(caption);
        pic.setUrl(url);
        pic.setUid(uid);
        mPicRef.child(pic.getKey()).setValue(pic);
    }

    public void add(Pic pic) {
        if (pic.getUrl().equals(""))
            pic.setUrl(Util.randomImageUrl());

        mPicRef.push().setValue(pic);
    }

    public void addPicture(final String name, String location, Bitmap bitmap) {
        final String photoDatabaseKey = mPicRef.push().getKey();
        final Pic pic = new Pic();
        pic.setCaption(name);
        pic.setUid(Constants.currentUID);
        pic.setUrl(location);
        pic.setKey(photoDatabaseKey);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mPicStorageRef.child(photoDatabaseKey).putBytes(data);
        final Uri[] downloadUri = new Uri[1];

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw task.getException();
                return mPicStorageRef.child(photoDatabaseKey).getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri[0] = task.getResult();
                    Log.d("download uri: ", downloadUri[0].toString());
                    pic.setUrl(downloadUri[0].toString());
                    mPicRef.push().setValue(pic);
                } else
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });

    }

    public void remove(Pic pic) {
        mPicRef.child(pic.getKey()).removeValue();
    }

    public void updateList(boolean showAll) {
        if (showAll) {
            mPics.clear();
            if (queryHasListener) {
                mQuery.removeEventListener(mPicChildEventListener);
                queryHasListener = false;
            }
            mPicRef.addChildEventListener(mPicChildEventListener);
            refHasListener = true;
        } else {
            mPics.clear();
            if (refHasListener) {
                mPicRef.removeEventListener(mPicChildEventListener);
                refHasListener = false;
            }
            mQuery = mPicRef.orderByChild("uid").equalTo(Constants.currentUID);
            mQuery.addChildEventListener(mPicChildEventListener);
            queryHasListener = true;

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mCaption;
        TextView mURL;

        public ViewHolder(View itemView) {
            super(itemView);
            mCaption = (TextView) itemView.findViewById(R.id.pic_list_caption);
            mURL = (TextView) itemView.findViewById(R.id.pic_list_url);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Pic p = mPics.get(getAdapterPosition());
            mCallback.onClick(adapter, p);
        }

        @Override
        public boolean onLongClick(View v) {
            Pic p = mPics.get(getAdapterPosition());
            mCallback.onEdit(adapter, p);
            return false;
        }
    }

    private class PicChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Pic p = dataSnapshot.getValue(Pic.class);
            p.setKey(dataSnapshot.getKey());
            mPics.add(0, p);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Pic updatedP = dataSnapshot.getValue(Pic.class);
            for (Pic p : mPics) {
                if (p.getKey().equals(key)) {
                    p.setValue(updatedP);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for (Pic p : mPics) {
                if (p.getKey().equals(key)) {
                    mPics.remove(p);
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
            Log.e("PB", databaseError.getMessage());
        }
    }
}
