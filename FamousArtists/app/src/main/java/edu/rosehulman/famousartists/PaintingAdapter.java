package edu.rosehulman.famousartists;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.famousartists.fragments.PaintingListFragment;

/**
 * Created by boutell on 1/3/17.
 */

public class PaintingAdapter extends RecyclerView.Adapter<PaintingAdapter.ViewHolder> {

    private List<Painting> mPaintings;
    private PaintingListFragment.OnPaintingSelectedListener mListener;
    private Context mContext;

    public PaintingAdapter(PaintingListFragment.OnPaintingSelectedListener listener, Context context) {
        mListener = listener;
        mContext = context;
        mPaintings = Painting.initializePaintings();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView artistTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            artistTextView = (TextView) itemView.findViewById(R.id.artist_text_view);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onPaintingSelected(mPaintings.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View view) {
            editPainting(getAdapterPosition());
            return false;
        }
    }

    private void editPainting(final int position) {
        final Painting painting = mPaintings.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_painting, null, false);
        final EditText artistEditText = (EditText) view.findViewById(R.id.dialog_painting_artist);
        artistEditText.setText(painting.getArtist());
        final EditText notesEditText = (EditText) view.findViewById(R.id.dialog_painting_notes);
        notesEditText.setText(painting.getNotes());
        builder.setTitle(R.string.dialog_painting_title);
        builder.setView(view);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                painting.setArtist(artistEditText.getText().toString());
                painting.setNotes(notesEditText.getText().toString());
                notifyItemChanged(position);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.painting_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mPaintings.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Painting painting = mPaintings.get(position);
        holder.artistTextView.setText(painting.getArtist());
    }
}
