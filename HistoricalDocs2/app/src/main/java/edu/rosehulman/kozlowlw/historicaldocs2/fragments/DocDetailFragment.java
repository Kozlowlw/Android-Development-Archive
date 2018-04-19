package edu.rosehulman.kozlowlw.historicaldocs2.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.rosehulman.kozlowlw.historicaldocs2.Doc;
import edu.rosehulman.kozlowlw.historicaldocs2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DocDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DocDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DOC = "doc";

    // TODO: Rename and change types of parameters
    private Doc mDoc;


    public DocDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param doc
     * @return A new instance of fragment DocDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DocDetailFragment newInstance(Doc doc) {
        DocDetailFragment fragment = new DocDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DOC, doc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDoc = getArguments().getParcelable(ARG_DOC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doc_detail, container, false);
        TextView titleView = (TextView) view.findViewById(R.id.fragment_doc_detail_title);
        titleView.setText(mDoc.getTitle());
        TextView bodyView = (TextView) view.findViewById(R.id.fragment_doc_detail_body);
        bodyView.setText(mDoc.getText());
        return view;
    }

}
