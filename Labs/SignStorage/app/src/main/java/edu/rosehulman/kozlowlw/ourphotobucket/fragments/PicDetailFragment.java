package edu.rosehulman.kozlowlw.ourphotobucket.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import edu.rosehulman.kozlowlw.ourphotobucket.R;
import edu.rosehulman.kozlowlw.ourphotobucket.models.Pic;


public class PicDetailFragment extends Fragment {

    private static final String ARG_PIC = "pic";
    private Pic mPic;
    private Bitmap mBitmap;
    private ImageView mPicImage;


    public PicDetailFragment(){

    }

    public static PicDetailFragment newInstance(Pic pic){
        PicDetailFragment fragment = new PicDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PIC, pic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mPic = getArguments().getParcelable(ARG_PIC);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_pic_detailed, container, false);
        TextView caption = (TextView) view.findViewById(R.id.pic_detailed_caption);
        caption.setText(mPic.getCaption());
        //TODO image stuff
        ImageView image = (ImageView) view.findViewById(R.id.pic_detailed_image);
        new DownloadImageTask((ImageView) view.findViewById(R.id.pic_detailed_image))
                .execute(mPic.getUrl());
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

}
