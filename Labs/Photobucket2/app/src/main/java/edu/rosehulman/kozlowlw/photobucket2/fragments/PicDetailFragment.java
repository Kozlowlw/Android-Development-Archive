package edu.rosehulman.kozlowlw.photobucket2.fragments;


import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import edu.rosehulman.kozlowlw.photobucket2.Pic;
import edu.rosehulman.kozlowlw.photobucket2.R;

public class PicDetailFragment extends Fragment {

    private static final String ARG_PIC = "pic";
    private Pic mPic;
    private Bitmap mBitmap;
    private ImageView mPicImage;


    public PicDetailFragment() {

    }

    public static PicDetailFragment newInstance(Pic pic) {
        PicDetailFragment fragment = new PicDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PIC, pic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPic = getArguments().getParcelable(ARG_PIC);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pic_detailed, container, false);
        TextView caption = (TextView) view.findViewById(R.id.pic_detailed_caption);
        caption.setText(mPic.getCaption());
        //TODO image stuff
        ImageView image = (ImageView) view.findViewById(R.id.pic_detailed_image);
//        new DownloadImageTask((ImageView) view.findViewById(R.id.pic_detailed_image))
////                .execute(mPic.getUrl());
        if (!mPic.getUrl().contains("content://")) {
            new DownloadImageTask(image).execute(mPic.getUrl());
        } else {
            new LoadLocalBitmapTask(image).execute(mPic.getUrl());
        }
        return view;
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class LoadLocalBitmapTask extends AsyncTask<String, Void, Bitmap> {
        private ContentResolver resolver = getActivity().getContentResolver();
        private ImageView image;

        public LoadLocalBitmapTask(ImageView image) {

            this.image = image;
        }

        @Override
        protected Bitmap doInBackground(String... locations) {
            Bitmap bitmap = null;
            // https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
            Uri uri = Uri.parse(locations[0]);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(Constants.TAG, "Local Bitmap Bytes: " + bitmap.getByteCount());
            Log.d(Constants.TAG, "Local Bitmap Size: " + bitmap.getHeight() + " " + bitmap.getWidth());
            image.setImageBitmap(bitmap);
        }
    }


}
