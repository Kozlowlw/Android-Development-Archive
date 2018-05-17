package edu.rosehulman.kozlowlw.exam1kozlowlw;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Triple mTriple;
    private PythagoreanGenerator mPyGen;
    private TextView mCorrectTV, mIncorrectTV, mTripleTV;
    private int cor = 0, incor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPyGen = new PythagoreanGenerator();
        mTriple = mPyGen.generatePotentialTriple();

        mCorrectTV = (TextView) findViewById(R.id.correct_amount_text);
        mIncorrectTV = (TextView) findViewById(R.id.incorrect_text_amount);
        mTripleTV = (TextView) findViewById(R.id.center_triples);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTriple();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            final PythagoreanGenerator temp_pygen = mPyGen;
            final Triple temp_trip = mTriple;
            final int temp_cor = cor;
            final int temp_incor = incor;

            mPyGen = new PythagoreanGenerator();
            cor = 0;
            incor = 0;

            showCurrentTriple(true);

            Snackbar.make((View) findViewById(R.id.coordinator_layout), R.string.game_reset, Snackbar.LENGTH_LONG).
                    setAction(R.string.snackbar_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTriple = temp_trip;
                            mPyGen = temp_pygen;
                            cor = temp_cor;
                            incor = temp_incor;
                            showCurrentTriple(false);
                            mTripleTV.setText(getString(R.string.triple_string_prefix)+" "+ mPyGen.getTrueTriple().toString());
                            Snackbar.make((View) findViewById(R.id.coordinator_layout), R.string.game_restored, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editTriple() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_confirmation, null);
        builder.setView(view);

        TextView tripleTV = (TextView) view.findViewById(R.id.dialog_triple);
        tripleTV.setText(mTriple.toString());

        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPyGen.isPotentialATrueTriple()) {
                    incor++;
                    mIncorrectTV.setText(String.valueOf(incor));
                    mTripleTV.setText(getString(R.string.triple_string_prefix) +" "+ mPyGen.getTrueTriple().toString());
                } else {
                    cor++;
                    mCorrectTV.setText(String.valueOf(cor));
                    mTripleTV.setText(getString(R.string.triple_string_prefix) +" "+ mTriple.toString());
                }
                mTriple = mPyGen.generatePotentialTriple();
            }
        });

        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mPyGen.isPotentialATrueTriple()) {
                    incor++;
                    mIncorrectTV.setText(String.valueOf(incor));
                    mTripleTV.setText(getString(R.string.triple_string_prefix)+" "+ mPyGen.getTrueTriple().toString());
                } else {
                    cor++;
                    mCorrectTV.setText(String.valueOf(cor));
                    mTripleTV.setText(getString(R.string.triple_string_prefix)+" "+ mTriple.toString());
                }
                mTriple = mPyGen.generatePotentialTriple();
            }
        });
        builder.create().show();
    }

    public void showCurrentTriple(boolean reset) {
        mCorrectTV.setText(String.valueOf(cor));
        mIncorrectTV.setText(String.valueOf(incor));
        mTripleTV.setText(getString(R.string.triple_string_prefix)+" "+ mTriple.toString());

    }
}
