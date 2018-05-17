package edu.rosehulman.kozlowlw.lightsout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LightsOutGame mGame;
    private TextView mGameStateTV;
    private Button[] mLightsOutButtons;
    private int mNumButtons = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGame = new LightsOutGame(mNumButtons);
        mGameStateTV = findViewById(R.id.game_state_text_view);
        Button NewGameButton = findViewById(R.id.new_game_button);
        NewGameButton.setOnClickListener(this);
        mLightsOutButtons = new Button[mNumButtons];

        if(savedInstanceState != null){
            onRestoreInstanceState(savedInstanceState);
        }

        for (int row = 0; row < mNumButtons; row++) {
            int id = getResources().getIdentifier("button" + row, "id", getPackageName());
            mLightsOutButtons[row] = findViewById(id);
            mLightsOutButtons[row].setOnClickListener(this);
            String s = Integer.toString(mGame.getValueAtIndex(row));
            mLightsOutButtons[row].setText(s);
        }

        setGameState();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_game_button) {
            mGame = new LightsOutGame(mNumButtons);
            for (int row = 0; row < mNumButtons; row++) {
                mLightsOutButtons[row].setEnabled(true);
            }
        }
        for (int row = 0; row < mNumButtons; row++) {
            if (v.getId() == mLightsOutButtons[row].getId()) {
                Log.d("LOG", "Button pressed was " + row);
                mGame.pressedButtonAtIndex(row);
            }
        }

        for (int row = 0; row < mNumButtons; row++) {
            mLightsOutButtons[row].setText(Integer.toString(mGame.getValueAtIndex(row)));
        }

        setGameState();
    }

    public void setGameState(){
        if (mGame.getNumPresses() == 0) {
            String s = getResources().getString(R.string.message_start);
            mGameStateTV.setText(s);
        } else if (mGame.checkForWin()) {
            String s = getResources().getString(R.string.win_state);
            mGameStateTV.setText(s);
            for (int row = 0; row < mNumButtons; row++) {
                mLightsOutButtons[row].setEnabled(false);
            }
        } else {
            String s = getResources().getQuantityString(R.plurals.message_format, mGame.getNumPresses(), mGame.getNumPresses());
            mGameStateTV.setText(s);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int[] buttonValues = new int[mNumButtons];
        for (int i = 0; i < mNumButtons; i++) {
            buttonValues[i] = mGame.getValueAtIndex(i);
        }
        outState.putIntArray("buttonValues", buttonValues);
        outState.putInt("buttonPresses", mGame.getNumPresses());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        mGame.setAllValues(savedInstanceState.getIntArray("buttonValues"));
        mGame.setNumPresses(savedInstanceState.getInt("buttonPresses"));
    }
}
