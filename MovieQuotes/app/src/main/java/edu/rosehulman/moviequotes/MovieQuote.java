package edu.rosehulman.moviequotes;

import com.google.firebase.database.Exclude;

/**
 * Created by Matt Boutell on 12/15/2015, based on earlier work by Dave Fisher.
 */
public class MovieQuote {
    private String quote;
    private String movie;
    private String key;

    public MovieQuote(){

    }

    public MovieQuote(String quote, String movie) {
        this.movie = movie;
        this.quote = quote;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValues(MovieQuote values) {
        this.movie = values.movie;
        this.quote = values.quote;
    }
}
