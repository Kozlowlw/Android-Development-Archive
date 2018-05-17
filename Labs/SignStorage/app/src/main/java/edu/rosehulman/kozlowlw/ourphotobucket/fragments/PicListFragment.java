package edu.rosehulman.kozlowlw.ourphotobucket.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.rosehulman.kozlowlw.ourphotobucket.Constants;
import edu.rosehulman.kozlowlw.ourphotobucket.MainActivity;
import edu.rosehulman.kozlowlw.ourphotobucket.R;
import edu.rosehulman.kozlowlw.ourphotobucket.models.Pic;

public class PicListFragment extends Fragment implements Toolbar.OnMenuItemClickListener{
    private OnPicSelectedListener mListener;
    private PicListAdapter mAdapter;
    private boolean viewState = false;

    public PicListFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_pic_list, container, false);
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title = dataSnapshot.getValue(String.class);
                toolbar.setTitle(title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.keepSynced(true);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MainActivity.showAddEditDialog(null, getContext());
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PicListAdapter(mListener, viewState);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    public void onPicSelected(Pic pic){
        if(mListener!=null){
            mListener.onPicSelected(pic);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPicSelectedListener) {
            mListener = (OnPicSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public PicListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show:
                Constants.viewState = !Constants.viewState;
                this.getAdapter().showAllPics(Constants.viewState);
                item.setTitle((Constants.viewState) ? "Show Mine" : "Show All");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnPicSelectedListener {
        // TODO: Update argument type and name
        void onPicSelected(Pic pic);
    }
}
