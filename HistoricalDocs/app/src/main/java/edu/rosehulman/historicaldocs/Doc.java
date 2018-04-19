package edu.rosehulman.historicaldocs;

/**
 * Created by Matt Boutell on 12/1/2015.
 */
public class Doc {
    private String title;
    private String text;

    public Doc(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
