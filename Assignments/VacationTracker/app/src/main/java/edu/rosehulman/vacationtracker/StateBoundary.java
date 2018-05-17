package edu.rosehulman.vacationtracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt Boutell on 1/27/2016.
 * Rose-Hulman Institute of Technology.
 * Covered by MIT license.
 */
public class StateBoundary {
    private String name;
    private int color;
    private List<LatLng> vertices;


    public StateBoundary(String name, int color) {
        this.name = name;
        this.color = color;
        this.vertices = new ArrayList<>();
    }

    public void addLatLng(LatLng latLng) {
        vertices.add(latLng);
    }

    @Override
    public String toString() {
        return "StateBoundary{" +
                "name='" + name + '\'' +
                ", color=" + color +
                ", vertices=" + vertices +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

	 public List<LatLng> getVertices() {
        return vertices;
    }

    public void setVertices(List<LatLng> vertices) {
        this.vertices = vertices;
    }
}

