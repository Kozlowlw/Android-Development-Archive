package edu.rosehulman.kozlowlw.comicviewer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import edu.rosehulman.kozlowlw.comicviewer.fragments.ComicFragment;
import edu.rosehulman.kozlowlw.comicviewer.util.ComicWrapper;
import edu.rosehulman.kozlowlw.comicviewer.util.Utils;

public class ComicPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<ComicWrapper> mComics = new ArrayList<>();
    private int initialSize = 5;
    private Context mContext;

    public ComicPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        for (int i = 0; i < initialSize; i++) {
            addComic();
        }
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a ComicFragment (defined as a static inner class below).
        return ComicFragment.newInstance(mComics.get(position));
    }

    @Override
    public int getCount() {
        // Show 5 total pages.
        return mComics.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(R.string.section_format, mComics.get(position).getXkcdIssue());
    }

    public void addComic() {
        mComics.add(new ComicWrapper(Utils.getRandomCleanIssue(), getCount() + 1));

        notifyDataSetChanged();
    }
}