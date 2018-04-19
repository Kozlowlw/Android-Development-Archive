package com.example.kozlowlw.jersey;

/**
 * Created by Kozlowlw on 3/14/2018.
 */

public class JerseyModel {
    private String playerName;
    private int playerNumber;
    private boolean isRed;

    public JerseyModel() {
        this("ANDROID", 17, false);
    }

    public JerseyModel(String playerName, int playerNumber, boolean isRed) {
        this.playerName = playerName;
        this.playerNumber = playerNumber;
        this.isRed = isRed;
    }

    public void setPlayerName(String playerName) {
        if (playerName.equals(""))
            this.playerName = "ANDROID";
        else {
            this.playerName = playerName;
        }
    }

    public void setPlayerNumber(String playerNumber) {
        if(playerNumber.equals("")){
            this.playerNumber = 0;
        }
        this.playerNumber = Integer.parseInt(playerNumber);
    }

    public void setJerseyColor(boolean isRed) {
        this.isRed = isRed;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public int getPlayerNumber() {
        return this.playerNumber;
    }

    public boolean getJerseyColor() {
        return this.isRed;
    }
}
