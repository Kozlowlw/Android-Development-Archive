package edu.rosehulman.favoritethings;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mColorTextView;
    private TextView mNumberTextView;
    private long mNumber;
    private static final String TAG = "FAVES";
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRef = FirebaseDatabase.getInstance().getReference();

        mColorTextView = (TextView) findViewById(R.id.color_text_view);
        mNumberTextView = (TextView) findViewById(R.id.number_text_view);
        mNumber = 17;

        findViewById(R.id.red_button).setOnClickListener(this);
        findViewById(R.id.white_button).setOnClickListener(this);
        findViewById(R.id.blue_button).setOnClickListener(this);
        findViewById(R.id.update_color_button).setOnClickListener(this);
        findViewById(R.id.increment_number_button).setOnClickListener(this);
        findViewById(R.id.decrement_number_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.red_button:
                mRef.child("color").setValue(getString(R.string.red));
                return;
            case R.id.white_button:
                mRef.child("color").setValue(getString(R.string.white));
                return;
            case R.id.blue_button:
                mRef.child("color").setValue(getString(R.string.blue));
                return;
            case R.id.update_color_button:
                Log.d(TAG, "Updating from Firebase");
                mRef.child("color").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mColorTextView.setText((String)dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Database Error: " + databaseError);
                    }
                });
                mRef.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mNumber = dataSnapshot.getValue(Long.class);
                        mNumberTextView.setText("" + mNumber);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return;
            case R.id.increment_number_button:
                mNumber++;
                mRef.child("number").setValue(mNumber);
                return;
            case R.id.decrement_number_button:
                mNumber--;
                mRef.child("number").setValue(mNumber);
                return;

        }
    }
}
