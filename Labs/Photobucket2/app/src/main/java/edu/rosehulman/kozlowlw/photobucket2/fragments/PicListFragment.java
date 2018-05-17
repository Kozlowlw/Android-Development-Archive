package edu.rosehulman.kozlowlw.photobucket2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import edu.rosehulman.kozlowlw.photobucket2.MainActivity;
import edu.rosehulman.kozlowlw.photobucket2.Pic;
import edu.rosehulman.kozlowlw.photobucket2.R;

public class PicListFragment extends Fragment{

    private Callback mCallback;
    public PicListAdapter adapter;

    public PicListFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        RecyclerView view = (RecyclerView) inflater.inflate(R.layout.fragment_pic_list,container,false);
        view.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PicListAdapter(mCallback);
        view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof Callback){
            mCallback = (Callback) context;
        }else{
            throw new RuntimeException(context.toString() + " must implement Callback");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallback = null;
    }

    public PicListAdapter getAdapter() {
        return adapter;
    }

    public interface Callback{
        void onClick(PicListAdapter adapter, Pic pic);
        void onEdit(PicListAdapter adapter, Pic pic);
        void changedTitle(String s);
    }


}
