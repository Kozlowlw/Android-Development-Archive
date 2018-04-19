package edu.rosehulman.kozlowlw.foodraterlistview;

/**
 * Created by Kozlowlw on 3/29/2018.
 */

public class Food {

    private String name;
    private int resourceID;
    private int rating;

    public Food(String name, int ID, int rating){
        this.name = name;
        this.resourceID = ID;
        this.rating = rating;
    }

    public String getName(){
        return name;
    }

    public int getID(){
        return resourceID;
    }

    public int getRating(){
        return rating;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setID(int ID){
        this.resourceID = ID;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public String toString(){
        return "Food: " + name + ", ID: " + resourceID + ", Rating: " + rating;
    }
}
