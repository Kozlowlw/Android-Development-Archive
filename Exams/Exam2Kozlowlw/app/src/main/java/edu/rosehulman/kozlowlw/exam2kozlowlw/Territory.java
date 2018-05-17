package edu.rosehulman.kozlowlw.exam2kozlowlw;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Territory {
    @JsonProperty("name")
    private String stateName;
    @JsonProperty("abbreviation")
    private String stateAbbreviation;
    @JsonProperty("capital")
    private String stateCapital;
    @JsonProperty("nickname")
    private String stateNickname;
    @JsonProperty("governor")
    private String stateGovernor;
    @JsonProperty("area")
    private int stateArea;

    private boolean isHighlighted;

    public Territory() {
        isHighlighted = false;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateAbbreviation() {
        return stateAbbreviation;
    }

    public void setStateAbbreviation(String stateAbbreviation) {
        this.stateAbbreviation = stateAbbreviation;
    }

    public String getStateCapital() {
        return stateCapital;
    }

    public void setStateCapital(String stateCapital) {
        this.stateCapital = stateCapital;
    }

    public String getStateNickname() {
        return stateNickname;
    }

    public void setStateNickname(String stateNickname) {
        this.stateNickname = stateNickname;
    }

    public String getStateGovernor() {
        return stateGovernor;
    }

    public void setStateGovernor(String stateGovernor) {
        this.stateGovernor = stateGovernor;
    }

    public int getStateArea() {
        return stateArea;
    }

    public void setStateArea(int stateArea) {
        this.stateArea = stateArea;
    }

    public String toString() {
        return this.stateName + "(" + this.stateAbbreviation + "), Capital: " + this.stateCapital
                + ", Area: " + this.stateArea + ", Nickname: " + this.stateNickname +
                ", Governor: " + this.stateGovernor;
    }

    //Alternative toString without the extra information
    public String toStringArea() {
        return " " + this.stateName + " (" + this.stateArea + ") ";
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }
}
