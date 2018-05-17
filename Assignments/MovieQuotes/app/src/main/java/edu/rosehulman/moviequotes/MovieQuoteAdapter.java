package edu.rosehulman.moviequotes;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt Boutell on 12/15/2015.
 */
public class MovieQuoteAdapter extends RecyclerView.Adapter<MovieQuoteAdapter.ViewHolder> {

    private List<MovieQuote> mMovieQuotes;
    private Callback mCallback;
    private DatabaseReference mMovieQuotesRef;


    public MovieQuoteAdapter(Callback callback) {
        mCallback = callback;
        mMovieQuotes = new ArrayList<>();
        mMovieQuotesRef = FirebaseDatabase.getInstance().getReference().child("quotes");
        mMovieQuotesRef.addChildEventListener(new QuotesChildEventsListener());
        //mMovieQuotesRef.keepSynced(true);
    }

    class QuotesChildEventsListener implements ChildEventListener{

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            MovieQuote quote = dataSnapshot.getValue(MovieQuote.class);
            quote.setKey(dataSnapshot.getKey());
            mMovieQuotes.add(0, quote);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            MovieQuote updatedMovieQuote = dataSnapshot.getValue(MovieQuote.class);
            for(MovieQuote mq: mMovieQuotes){
                if(mq.getKey().equals(key)){
                    mq.setValues(updatedMovieQuote);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            for (MovieQuote mq: mMovieQuotes){
                if(mq.getKey().equals(key)){
                    mMovieQuotes.remove(mq);
                    break;
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            //empty
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("MQ", databaseError.getMessage());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_quote_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MovieQuote movieQuote = mMovieQuotes.get(position);
        holder.mQuoteTextView.setText(movieQuote.getQuote());
        holder.mMovieTextView.setText(movieQuote.getMovie());
    }

    public void remove(MovieQuote movieQuote) {
        //: Remove the next line(s) and use Firebase instead
        mMovieQuotesRef.child(movieQuote.getKey()).removeValue();
    }


    @Override
    public int getItemCount() {
        return mMovieQuotes.size();
    }

    public void add(MovieQuote movieQuote) {
        //mMovieQuotes.add(0, movieQuote);
        //notifyDataSetChanged();
        mMovieQuotesRef.push().setValue(movieQuote);

    }

    public void update(MovieQuote movieQuote, String newQuote, String newMovie) {
        movieQuote.setQuote(newQuote);
        movieQuote.setMovie(newMovie);
        mMovieQuotesRef.child(movieQuote.getKey()).setValue(movieQuote);
    }

    public interface Callback {
        void onEdit(MovieQuote movieQuote);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mQuoteTextView;
        private TextView mMovieTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mQuoteTextView = itemView.findViewById(R.id.quote_text);
            mMovieTextView = itemView.findViewById(R.id.movie_text);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MovieQuote movieQuote = mMovieQuotes.get(getAdapterPosition());
            mCallback.onEdit(movieQuote);
        }

        @Override
        public boolean onLongClick(View v) {
            remove(mMovieQuotes.get(getAdapterPosition()));
            return true;
        }
    }
}
