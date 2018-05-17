package edu.rosehulman.kozlowlw.historicaldocs2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.kozlowlw.historicaldocs2.Doc;
import edu.rosehulman.kozlowlw.historicaldocs2.DocListAdapter;
import edu.rosehulman.kozlowlw.historicaldocs2.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DocListFragment.Callback} interface
 *
 * to handle interaction events.
 */
public class DocListFragment extends Fragment {

    private Callback mCallback;

    public DocListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView  view = (RecyclerView) inflater.inflate(R.layout.fragment_doc_list, container, false);
        view.setLayoutManager(new LinearLayoutManager(getContext()));
        DocListAdapter adapter = new DocListAdapter(getContext(), mCallback);
        view.setAdapter(adapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onDocSelected(Doc doc) {
        if (mCallback != null) {
            mCallback.onDocSelected(doc);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface Callback {
        // TODO: Update argument type and name
        void onDocSelected(Doc doc);
    }
}
