package com.example.kozlowlw.pointofsale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private TextView mNameText, mQuantityText, mDeliveryDateText;
    private Item mCurrentItem;
    private ArrayList<Item> mItems = new ArrayList<Item>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNameText = (TextView) findViewById(R.id.name_text);
        mQuantityText = (TextView) findViewById(R.id.quantity_text);
        mDeliveryDateText = (TextView) findViewById(R.id.date_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditItem(false);
            }
        });

        registerForContextMenu(mNameText);
    }

    private void addEditItem(final boolean isEditing) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_add, null);
        builder.setView(view);

        //capture
        final EditText namedEditText = (EditText) view.findViewById(R.id.edit_name);
        final EditText quantityEditText = (EditText) view.findViewById(R.id.edit_quantity);
        final CalendarView deliveryDateView = (CalendarView) view.findViewById(R.id.calendar_view);
        final GregorianCalendar calendar = new GregorianCalendar();

        if (isEditing) {
            namedEditText.setText(mCurrentItem.getName());
            quantityEditText.setText(String.valueOf(mCurrentItem.getQuantity()));
            deliveryDateView.setDate(mCurrentItem.getDeliveryDateTime());
        }

        deliveryDateView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
            }
        });

        //buttons
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO
                String name = namedEditText.getText().toString();
                String quantityString = quantityEditText.getText().toString();
                int quantity = 0;
                if (!quantityString.equals("")) {
                    quantity = Integer.parseInt(quantityString);
                }
                if (isEditing) {
                    mCurrentItem.setName(name);
                    mCurrentItem.setQuantity(quantity);
                    mCurrentItem.setDeliveryDate(calendar);
                } else {
                    mCurrentItem = new Item(name, quantity, calendar);
                    mItems.add(mCurrentItem);
                }
                showCurrentItem();
            }
        });
        builder.create().show();
    }

    private void showCurrentItem() {
        mNameText.setText(mCurrentItem.getName());
        mQuantityText.setText(getString(R.string.quantity_format, mCurrentItem.getQuantity()));
        mDeliveryDateText.setText(getString(R.string.date_format, mCurrentItem.getDeliveryDateString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_context_edit:
                addEditItem(true);
                return true;
            case R.id.menu_context_remove:
                mItems.remove(mCurrentItem);
                Log.d("POS","mItems : " + mItems.size());
                if (mItems.size() != 0) {
                    mCurrentItem = mItems.get(0);
                    showCurrentItem();
                } else {
                    mCurrentItem = new Item();
                    showCurrentItem();
                    mDeliveryDateText.setText(R.string.date_start);
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_reset:
                final Item clearedItem = mCurrentItem;
                mCurrentItem = new Item();
                showCurrentItem();
                Snackbar.make((View) findViewById(R.id.coordinator_layout), "Item cleared", Snackbar.LENGTH_LONG).
                        setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCurrentItem = clearedItem;
                                showCurrentItem();
                                Snackbar.make((View) findViewById(R.id.coordinator_layout), "Item restored", Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }).show();
                return true;
            case R.id.action_search:
                showSearchDialog();
                return true;
            case R.id.action_clear:
                showConfirmationDialog();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.search_dialog_title);
        builder.setItems(getNames(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCurrentItem = mItems.get(which);
                showCurrentItem();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private String[] getNames() {
        String[] names = new String[mItems.size()];
        for (int i = 0; i < mItems.size(); i++) {
            names[i] = mItems.get(i).getName();
        }
        return names;
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        mDeliveryDateText = (TextView) findViewById(R.id.date_text);

        builder.setTitle("Remove");
        builder.setMessage("Are your sure you want to remove all items?");
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mItems.clear();
                mCurrentItem = new Item();
                showCurrentItem();
                mDeliveryDateText.setText(R.string.date_start); //Reseting the date string
            }
        });

        builder.create().show();
    }

}


