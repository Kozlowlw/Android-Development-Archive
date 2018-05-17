package edu.rosehulman.famousartists;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by boutell on 1/3/17.
 */

public class Painting implements Parcelable {
    private String artist;
    private String name;
    private String notes;
    private int resourceId;

    public Painting(String artist, String name, int resourceId) {
        this.artist = artist;
        this.name = name;
        this.resourceId = resourceId;
    }

    protected Painting(Parcel in) {
        artist = in.readString();
        name = in.readString();
        notes = in.readString();
        resourceId = in.readInt();
    }

    public static final Creator<Painting> CREATOR = new Creator<Painting>() {
        @Override
        public Painting createFromParcel(Parcel in) {
            return new Painting(in);
        }

        @Override
        public Painting[] newArray(int size) {
            return new Painting[size];
        }
    };

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static List<Painting> initializePaintings() {
        // https://commons.wikimedia.org/wiki/Leonardo_da_Vinci#/media/File:Leonardo_da_Vinci_-_Mona_Lisa.jpg
        // By Leonardo da Vinci - (Upload Sept. 19, 2010) Au plus près des oeuvres ! - http://musee.louvre.fr/zoom/index.html (Microsoft Silverlight required), Public Domain, https://commons.wikimedia.org/w/index.php?curid=51499

        // https://upload.wikimedia.org/wikipedia/commons/d/d8/Hands_of_God_and_Adam.jpg

        // By Vincent van Gogh - bgEuwDxel93-Pg at Google Cultural Institute, zoom level maximum, Public Domain, https://commons.wikimedia.org/w/index.php?curid=25498286

        // https://upload.wikimedia.org/wikipedia/commons/1/14/Claude_Monet_-_Water_Lilies_-_Google_Art_Project.jpg

        //https://commons.wikimedia.org/wiki/File%3ARembrandt_Harmensz_van_Rijn_-_Return_of_the_Prodigal_Son_-_Google_Art_Project.jpg
        //Rembrandt [Public domain], via Wikimedia Commons
        // Alternate: http://www.everypainterpaintshimself.com/article/rembrandts_raising_of_the_cross

        // https://upload.wikimedia.org/wikipedia/commons/d/d7/Meisje_met_de_parel.jpg

        // https://upload.wikimedia.org/wikipedia/en/d/d1/Picasso_three_musicians_moma_2006.jpg

        List<Painting> paintings = new ArrayList<>();
        paintings.add(new Painting("Michaelangelo", "Sistine Chapel", R.drawable.hands_of_god_and_adam));
        paintings.add(new Painting("Leonardo da Vinci", "Mona Lisa", R.drawable.mona_lisa));
        paintings.add(new Painting("Vincent van Gogh", "Starry Night", R.drawable.starry_night));
        paintings.add(new Painting("Claude Monet", "Water Lilies", R.drawable.water_lilies));
        paintings.add(new Painting("Rembrandt van Rijn", "Return of the Prodigal Son", R.drawable.prodigal_son));
        paintings.add(new Painting("Johannes Vermeer", "Girl with a Pearl Earring", R.drawable.meisje_met_de_parel));
        paintings.add(new Painting("Pablo Picasso", "Three Musicians", R.drawable.three_musicians));
        return paintings;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(artist);
        parcel.writeString(name);
        parcel.writeString(notes);
        parcel.writeInt(resourceId);
    }
}
