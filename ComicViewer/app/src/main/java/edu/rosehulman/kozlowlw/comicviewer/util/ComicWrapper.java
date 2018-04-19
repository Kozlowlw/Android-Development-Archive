package edu.rosehulman.kozlowlw.comicviewer.util;

import android.os.Parcel;
import android.os.Parcelable;

public class ComicWrapper implements Parcelable {

    private int xkcdIssue;
    private int color;
    private Comic mComic = null;

    public ComicWrapper(int issue, int position) {
        xkcdIssue = issue;
        setColor(position);
    }

    protected ComicWrapper(Parcel in) {
        xkcdIssue = in.readInt();
        color = in.readInt();
    }

    public static final Creator<ComicWrapper> CREATOR = new Creator<ComicWrapper>() {
        @Override
        public ComicWrapper createFromParcel(Parcel in) {
            return new ComicWrapper(in);
        }

        @Override
        public ComicWrapper[] newArray(int size) {
            return new ComicWrapper[size];
        }
    };

    public int getXkcdIssue() {
        if(mComic != null)
            return mComic.getNum();
        return xkcdIssue;
    }

    public void setXkcdIssue(int xkcdIssue) {
        this.xkcdIssue = xkcdIssue;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int i) {
        switch (i % 4) {
            case 0:
                this.color = android.R.color.holo_green_light;
                break;
            case 1:
                this.color = android.R.color.holo_blue_light;
                break;
            case 2:
                this.color = android.R.color.holo_orange_light;
                break;
            case 3:
                this.color = android.R.color.holo_red_light;
                break;
            default:
                this.color = android.R.color.holo_green_light;
                break;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(xkcdIssue);
        dest.writeInt(color);
    }

    public Comic getComic() {
        return mComic;
    }

    public void setComic(Comic mComic) {
        this.mComic = mComic;
    }
}
