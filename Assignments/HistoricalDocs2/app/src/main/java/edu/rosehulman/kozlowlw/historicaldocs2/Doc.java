package edu.rosehulman.kozlowlw.historicaldocs2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matt Boutell on 12/1/2015.
 */
public class Doc implements Parcelable{
    private String title;
    private String text;

    public Doc(String title, String text) {
        this.title = title;
        this.text = text;
    }

    protected Doc(Parcel in){
        title = in.readString();
        text = in.readString();
    }

    public static final Creator<Doc> CREATOR = new Creator<Doc>() {
        @Override
        public Doc createFromParcel(Parcel in) {
            return new Doc(in);
        }

        @Override
        public Doc[] newArray(int size) {
            return new Doc[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(text);
    }
}
