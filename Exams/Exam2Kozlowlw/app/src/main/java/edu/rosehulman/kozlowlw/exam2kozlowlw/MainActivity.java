package edu.rosehulman.kozlowlw.exam2kozlowlw;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements TerritoryAdapter.AdapterCallback{
    private TerritoryAdapter mAdapter;
    public static final String EXTRA_SCORE = "EXTRA_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAdapter = new TerritoryAdapter(this, this);
        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new ItemTouchHelperAdapter(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_sort_button:
                mAdapter.setHighlightedPositions();
                return true;
            case R.id.action_settings_button:
                showSettingsDialog();
                return true;
            case R.id.action_refresh_button:
                mAdapter.shuffleTerritory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSettingsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.dialog_how_many);
        builder.setItems(getResources().getStringArray(R.array.settings_items), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0: mAdapter.getRandomTerritory(5);  break;
                    case 1: mAdapter.getRandomTerritory(10); break;
                    case 2: mAdapter.getRandomTerritory(50); break;
                }
                mAdapter.setScore(0);
            }
        });;

        builder.create().show();
    }

    @Override
    public void changedTitle(String s) {
        setTitle(s);
    }
}
