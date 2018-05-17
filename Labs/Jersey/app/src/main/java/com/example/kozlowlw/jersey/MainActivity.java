package com.example.kozlowlw.jersey;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView mPlayerNameTV, mPlayerNumberTV;
    private JerseyModel mCurrentJersey;
    private ImageView mJerseyColor;

    private final static String PREFS = "PREFS";
    private static final String KEY_JERSEY_NAME = "KEY_JERSEY_NAME";
    private static final String KEY_JERSEY_NUMBER = "KEY_JERSEY_NUMBER";
    private static final String KEY_JERSEY_COLOR = "KEY_JERSEY_COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCurrentJersey = new JerseyModel();

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String name = prefs.getString(KEY_JERSEY_NAME,getString(R.string.default_jersey_name));
        String number = String.valueOf(prefs.getInt(KEY_JERSEY_NUMBER,Integer.parseInt(getString(R.string.default_jersey_number))));
        boolean color = prefs.getBoolean(KEY_JERSEY_COLOR, mCurrentJersey.getJerseyColor());

        mCurrentJersey.setPlayerName(name);
        mCurrentJersey.setPlayerNumber(number);
        mCurrentJersey.setJerseyColor(color);

        mPlayerNameTV = (TextView) findViewById(R.id.jersey_name);
        mPlayerNumberTV = (TextView) findViewById(R.id.jersey_number);
        mJerseyColor = (ImageView) findViewById(R.id.jersey_color);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editJersey();
            }
        });

        showCurrentJersey();

    }

    public void editJersey() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = MainActivity.this.getLayoutInflater().inflate(R.layout.update_dialog, null);
        builder.setView(view);

        final EditText nameEditText = (EditText) view.findViewById(R.id.edit_dialog_name);
        final EditText numberEditText = (EditText) view.findViewById(R.id.edit_dialog_number);
        final ToggleButton colorToggle = (ToggleButton) view.findViewById(R.id.color_toggle);

        nameEditText.setText(mCurrentJersey.getPlayerName());
        numberEditText.setText(String.valueOf(mCurrentJersey.getPlayerNumber()));
        colorToggle.setChecked(mCurrentJersey.getJerseyColor());

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString();
                String number = numberEditText.getText().toString();
                boolean isRed = colorToggle.isChecked();

                mCurrentJersey.setPlayerName(name);
                if (number.equals(""))
                    mCurrentJersey.setPlayerNumber("0");
                else mCurrentJersey.setPlayerNumber(number);
                mCurrentJersey.setJerseyColor(isRed);

                showCurrentJersey();
            }
        });
        builder.create().show();
    }

    public void showCurrentJersey() {
        mPlayerNameTV.setText(mCurrentJersey.getPlayerName());
        mPlayerNumberTV.setText(mCurrentJersey.getPlayerNumber() + "");
        if (!mCurrentJersey.getJerseyColor()) {
            mJerseyColor.setImageResource(R.drawable.red_jersey);
        } else {
            mJerseyColor.setImageResource(R.drawable.blue_jersey);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                return true;
            case R.id.action_reset:
                showConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.reset);
        builder.setMessage(R.string.reset_confirmation);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentJersey = new JerseyModel();
                showCurrentJersey();
            }
        });

        builder.create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_JERSEY_NAME, mCurrentJersey.getPlayerName());
        editor.putInt(KEY_JERSEY_NUMBER, mCurrentJersey.getPlayerNumber());
        editor.putBoolean(KEY_JERSEY_COLOR, mCurrentJersey.getJerseyColor());
        editor.commit();
    }

}
