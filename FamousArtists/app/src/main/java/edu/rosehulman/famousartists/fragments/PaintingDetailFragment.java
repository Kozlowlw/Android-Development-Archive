package edu.rosehulman.famousartists.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import edu.rosehulman.famousartists.Painting;
import edu.rosehulman.famousartists.R;
import edu.rosehulman.famousartists.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFlingListener} interface
 * to handle interaction events.
 * Use the {@link PaintingDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaintingDetailFragment extends Fragment {
    private static final String ARG_PAINTING = "painting";

    private Painting mPainting;
    private OnFlingListener mListener;
    private GestureDetectorCompat mGestureDetector;


    public PaintingDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param painting Parameter 1.
     * @return A new instance of fragment PaintingDetailFragment.
     */
    public static PaintingDetailFragment newInstance(Painting painting) {
        PaintingDetailFragment fragment = new PaintingDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PAINTING, painting);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPainting = getArguments().getParcelable(ARG_PAINTING);
        }
        mGestureDetector = new GestureDetectorCompat(getContext(),
                new MyGestureDetector());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Need to wait for the activity to be created to have an action bar.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(mPainting.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_painting_detail, container, false);
//        TextView titleView = (TextView) view.findViewById(R.id.painting_detail_title);
//        titleView.setText(mPainting.getName());

        ImageView imageView = (ImageView) view.findViewById(R.id.painting_detail_image);
        int imageResourceId = mPainting.getResourceId();

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
        loadBitmap(imageResourceId, imageView);
        return view;
    }

    // TODO: Create a custom GestureDetector


    public void loadBitmap(final int resId, final ImageView imageView) {
        // This code fails because some images are too high resolution to fit in memory.
        //imageView.setImageResource(imageResourceId);

        // The technique in this method and its helpers are
        // from https://developer.android.com/training/displaying-bitmaps/load-bitmap.html

        // First, setting the flag will just get the image size instead of decoding the whole image.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        Log.d(Constants.TAG, "image " + imageHeight + " " + imageWidth);

        // To load the bitmap at the right resolution, we need the size of the imageview first.
        // This code executes after the image view has been given a size
        // http://stackoverflow.com/questions/3591784/getwidth-and-getheight-of-view-returns-0
        // and https://developer.android.com/reference/android/view/View.html#post(java.lang.Runnable)
        imageView.post(new Runnable() {
            @Override
            public void run() {
                // Width set, but height still 0 since it wraps content, which hasn't been set.
                // As a result, rescaling mostly works but one image failed (meisje...) - possibly
                // because of square aspect ratio not being handled correctly?
                // Also, Water lilies fails on both orientations and mona lisa fails in portrait orientation.
                // So need to make smaller (hack in calculateSampleSize below is a workaround)
                int viewWidth = imageView.getWidth();
                int viewHeight = imageView.getHeight();
                Log.d(Constants.TAG, "imageview " + viewWidth + " " + viewHeight);

                // Set the sample size to reduce resolution.
                options.inSampleSize = calculateInSampleSize(options, viewWidth, viewHeight);
                new BitmapWorkerTask(imageView, getResources(), options).execute(resId);
            }
        });
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int resId = 0;
        private Resources mResources;
        private BitmapFactory.Options mOptions;

        BitmapWorkerTask(ImageView imageView, Resources resources, BitmapFactory.Options options) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
            mResources = resources;
            mOptions = options;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            resId = params[0];
            mOptions.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(mResources, resId, mOptions);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    Log.d(Constants.TAG, "bitmap size " + bitmap.getHeight() + bitmap.getWidth());

                    // Extract palette. Doing off UI thread.
                    // https://developer.android.com/training/material/palette-colors.html
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        public void onGenerated(Palette p) {
                            int defaultColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                            // There is always a dominant color
                            int dom = p.getDominantColor(defaultColor);
                            Log.d(Constants.TAG, "Dominant color " + dom + " default color " + defaultColor);
                            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                            actionBar.setBackgroundDrawable(new ColorDrawable(dom));
                            // If it found a dark vibrant color, use it instead.
                            Palette.Swatch swatch = p.getDarkVibrantSwatch();
                            if (swatch != null) {
                                actionBar.setBackgroundDrawable(new ColorDrawable(swatch.getRgb()));
                            } else {
                                Log.e(Constants.TAG, "No swatch");
                            }
                        }
                    });
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            // CONSIDER: The /2 is a hack that fixes the memory errors above still with good resolution
            // on a Nexus 5, but needs to be tested on other devices.
            while ((halfHeight / inSampleSize) >= reqHeight / 2
                    && (halfWidth / inSampleSize) >= reqWidth / 2) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFlingListener) {
            mListener = (OnFlingListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFlingListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // This will be used with a Fling gesture.
    public interface OnFlingListener {
        void onSwipe();
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(Constants.TAG, "onFling: " + event1.toString() + event2.toString());
            mListener.onSwipe();
            return true;
        }
    }

}
