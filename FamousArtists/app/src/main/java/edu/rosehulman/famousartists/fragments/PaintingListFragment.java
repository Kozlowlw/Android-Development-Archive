package edu.rosehulman.famousartists.fragments;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.famousartists.Painting;
import edu.rosehulman.famousartists.PaintingAdapter;
import edu.rosehulman.famousartists.R;
import edu.rosehulman.famousartists.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPaintingSelectedListener} interface
 * to handle interaction events.
 */
public class PaintingListFragment extends Fragment {

    private OnPaintingSelectedListener mListener;
    private PaintingAdapter mAdapter;

    public PaintingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The adapter needs the listener so that when a painting is selected, it can
        // ask the listener (the MainActivity) to switch out the fragment.
        mAdapter = new PaintingAdapter(mListener, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_painting_list, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.painting_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPaintingSelectedListener) {
            mListener = (OnPaintingSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPaintingSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPaintingSelectedListener {
        void onPaintingSelected(Painting painting);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Need to wait for the activity to be created to have an action bar.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        int defaultColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        actionBar.setBackgroundDrawable(new ColorDrawable(defaultColor));
    }

}
