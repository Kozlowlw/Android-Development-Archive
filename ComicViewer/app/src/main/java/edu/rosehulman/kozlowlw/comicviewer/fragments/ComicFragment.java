package edu.rosehulman.kozlowlw.comicviewer.fragments;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import edu.rosehulman.kozlowlw.comicviewer.util.Comic;
import edu.rosehulman.kozlowlw.comicviewer.util.ComicWrapper;
import edu.rosehulman.kozlowlw.comicviewer.R;

public class ComicFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_XKCD_COMIC = "ARG_XKCD_COMIC";

    private ComicWrapper mComic;
    private PhotoView mComicImage;
    private Bitmap mBitmap;
    private TextView mComicTitle;

    public ComicFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ComicFragment newInstance(ComicWrapper comicWrapper) {
        ComicFragment fragment = new ComicFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_XKCD_COMIC, comicWrapper);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mComic = getArguments().getParcelable(ARG_XKCD_COMIC);
        }
        String urlString = String.format(Locale.US, getString(R.string.xkcd_url_string_format), mComic.getXkcdIssue());
        new GetComicTask().execute(urlString);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootView.setBackgroundColor(getResources().getColor(mComic.getColor()));
        mComicTitle = (TextView) rootView.findViewById(R.id.section_label);
        mComicImage = (PhotoView) rootView.findViewById(R.id.comic_image);
        if (mBitmap != null) {
            mComicImage.setImageBitmap(mBitmap);
        }
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            showAltMessage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAltMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (mComic.getXkcdIssue() != 0) {
            builder.setTitle(getString(R.string.mouse_over, mComic.getXkcdIssue()));
        }
        if (mComic.getComic().getAlt() != "") {
            builder.setMessage(mComic.getComic().getAlt());
        } else {
            builder.setMessage(R.string.alt_error_message);
        }
        builder.create().show();
    }


    class GetComicTask extends AsyncTask<String, Void, Comic> {

        public GetComicTask() {

        }

        @Override
        protected Comic doInBackground(String... strings) {
            String urlString = strings[0];
            Comic comic = null;
            try {
                comic = new ObjectMapper().readValue(new URL(urlString), Comic.class);
            } catch (IOException e) {
                Log.d("CV",e.getLocalizedMessage());
            }
            return comic;
        }

        @Override
        protected void onPostExecute(Comic comic) {
            super.onPostExecute(comic);
            if (comic != null) {
                mComic.setComic(comic);
                if (mComicTitle != null)
                    mComicTitle.setText(comic.getSafe_title());
                mComic.setXkcdIssue(comic.getNum());

                new GetImageTask().execute(comic.getImg());
            } else {
                Log.d("CV", "Issue loading comic!");
            }
        }
    }

    class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        public GetImageTask(){

        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlString = strings[0];
            InputStream in = null;
            try {
                in = new URL(urlString).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            mBitmap = bitmap;
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                if (mComicImage != null)
                    mComicImage.setImageBitmap(bitmap);
            } else {
                Log.d("CV", "Issue loading bitmap!");
            }
        }
    }
}