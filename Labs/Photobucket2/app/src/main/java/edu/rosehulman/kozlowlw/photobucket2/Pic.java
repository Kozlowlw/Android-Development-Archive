package edu.rosehulman.kozlowlw.photobucket2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Pic implements Parcelable {

    private String key;
    private String url;
    private String caption;
    private String uid;

    public Pic(){

    }

    public Pic(String caption, String url, String uid){

        this.caption = caption;
        this.url = url;
        this.uid = uid;
    }

    protected Pic(Parcel in) {
        key = in.readString();
        url = in.readString();
        caption = in.readString();
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(url);
        dest.writeString(caption);
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pic> CREATOR = new Creator<Pic>() {
        @Override
        public Pic createFromParcel(Parcel in) {
            return new Pic(in);
        }

        @Override
        public Pic[] newArray(int size) {
            return new Pic[size];
        }
    };

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setValue(Pic pic){
        this.caption = pic.getCaption();
        this.url = pic.getUrl();
        this.uid = pic.getUid();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
