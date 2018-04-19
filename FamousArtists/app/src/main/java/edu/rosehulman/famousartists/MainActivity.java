package edu.rosehulman.famousartists;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import edu.rosehulman.famousartists.fragments.PaintingDetailFragment;
import edu.rosehulman.famousartists.fragments.PaintingListFragment;
import edu.rosehulman.famousartists.fragments.WelcomeFragment;
import edu.rosehulman.famousartists.utils.Constants;

public class MainActivity extends AppCompatActivity
        implements WelcomeFragment.OnStartPressedListener,
        PaintingListFragment.OnPaintingSelectedListener,
        PaintingDetailFragment.OnFlingListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new WelcomeFragment();
            ft.add(R.id.fragment_container, fragment);
            ft.commit();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartPressed() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new PaintingListFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("list");
        ft.commit();
    }

    @Override
    public void onPaintingSelected(Painting painting) {
        Log.d(Constants.TAG, "Painting selected");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = PaintingDetailFragment.newInstance(painting);

        // Create a transition.
        Slide slideTransition = new Slide(Gravity.RIGHT);
        slideTransition.setDuration(200);
        fragment.setEnterTransition(slideTransition);

        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("detail");
        ft.commit();
    }

    @Override
    public void onSwipe() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStackImmediate();
        ft.commit();
    }
}
