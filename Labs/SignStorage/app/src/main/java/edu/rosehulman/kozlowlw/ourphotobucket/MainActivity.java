package edu.rosehulman.kozlowlw.ourphotobucket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

import edu.rosehulman.kozlowlw.ourphotobucket.fragments.LoginFragment;
import edu.rosehulman.kozlowlw.ourphotobucket.fragments.PicDetailFragment;
import edu.rosehulman.kozlowlw.ourphotobucket.fragments.PicListAdapter;
import edu.rosehulman.kozlowlw.ourphotobucket.fragments.PicListFragment;
import edu.rosehulman.kozlowlw.ourphotobucket.Constants;
import edu.rosehulman.kozlowlw.ourphotobucket.models.Pic;
import edu.rosehulman.kozlowlw.ourphotobucket.utils.Util;

public class MainActivity extends AppCompatActivity implements PicListFragment.OnPicSelectedListener, LoginFragment.OnLoginListener, GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    OnCompleteListener mOnCompleteListener;
    private GoogleApiClient mGoogleApiClient;
    private PicListAdapter mAdapter;
    private DatabaseReference mRef;
    private PicListFragment picListFragment;
    public FloatingActionButton fab;
    public static FirebaseUser mUser;

    private final static int RC_GOOGLE_LOGIN = 1;
    private static final int RC_TAKE_PICTURE = 2;
    private static final int RC_CHOOSE_PICTURE = 3;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        checkPermissions();
        mAuth = FirebaseAuth.getInstance();
        initializeListeners();
        setupGoogleSignIn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                mAuth.signOut();
                return true;
            case R.id.action_show:
                Constants.viewState = !Constants.viewState;
                picListFragment.getAdapter().showAllPics(Constants.viewState);
                item.setTitle((Constants.viewState) ? "Show Mine" : "Show All");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermissions() {
        // Check to see if we already have permissions
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // If we do not, request them from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    private void switchToLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment login = new LoginFragment();
        ft.replace(R.id.fragment_container, login, "Login");
        ft.commit();
    }

    private void switchToListFragment(String path) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment picFragment = new PicListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FIREBASE_PATH, path);
        picFragment.setArguments(args);
        ft.replace(R.id.fragment_container, picFragment, "Passwords");
        ft.commit();
    }

    private void initializeListeners() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(Constants.TAG, "Current user: " + user);
                if (user != null) {
                    mUser = user;
                    switchToListFragment("users/" + user.getUid());
                } else {
                    mUser = null;
                    switchToLoginFragment();
                }
            }
        };
        mOnCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    showLoginError("Authentication Failed");
                }
            }
        };
    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onLogin(String email, String password) {
        //DONE: Log user in with username & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mOnCompleteListener);
    }

    @Override
    public void onGoogleLogin() {
        //DONE: Log user in with Google account
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_GOOGLE_LOGIN);
    }

    @Override
    public void onRosefireLogin() {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showLoginError(connectionResult.toString());
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(Constants.TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, mOnCompleteListener);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("What is the name of this picture?");

        final EditText editText = new EditText(MainActivity.this);
        builder.setView(editText);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editText.getText().toString();
                switch (requestCode) {
                    case RC_TAKE_PICTURE:
                        if (resultCode == RESULT_OK) {
                            sendCapturedPhotoToAdapter(name, data);
                        }
                        break;
                    case RC_CHOOSE_PICTURE:
                        sendGalleryPhotoToAdapter(name, data);
                        break;
                    default:
                        Log.d("TAG", "Invalid file request code");
                        break;
                }
            }
        });

        builder.create().show();

        switch (requestCode) {
            case RC_GOOGLE_LOGIN:
                GoogleSignInResult resultGG = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (resultGG.isSuccess()) {
                    GoogleSignInAccount account = resultGG.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    showLoginError("Google Authentication failed.");
                }
                break;

        }
    }

    private void sendCapturedPhotoToAdapter(String name, Intent data) {
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        String location = MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), bitmap, name, null);
        picListFragment.getAdapter().addPicture(name, location, bitmap);
    }

    private void sendGalleryPhotoToAdapter(String name, Intent data) {
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();
            String location = uri.toString();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), uri);
                picListFragment.getAdapter().addPicture(name, location, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void showAddPhoto(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a photo source");
        builder.setMessage("Would you like to take a new picture?\nOr choose an existing one?");
        builder.setPositiveButton("Take Picture", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
                }
            }
        });

        builder.setNegativeButton("Choose Picture", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (choosePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(choosePictureIntent, RC_CHOOSE_PICTURE);
                }
            }
        });

    }

    public static void showAddEditDialog(final Pic pic, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null, false);
        final EditText caption_text = view.findViewById(R.id.dialog_caption);
        final EditText url_text = view.findViewById(R.id.dialog_url);
        if (pic != null) {
            builder.setTitle("Edit a Weatherpic");
            caption_text.setText(pic.getCaption());
            url_text.setText(pic.getUrl());
            builder.setNeutralButton(context.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity) context).picListFragment.getAdapter().delete(pic);
                }
            });
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity) context).picListFragment.getAdapter().update(pic, caption_text.getText().toString(), url_text.getText().toString());
                }
            });
        } else {
            builder.setTitle("Add a Weatherpic");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String caption = caption_text.getText().toString();
                    String url = url_text.getText().toString();
                    if (url.equals("")) {
                        url = Util.randomImageUrl();
                    }
                    Pic newPic = new Pic(caption, url, mUser.getUid());
                    ((MainActivity) context).picListFragment.getAdapter().add(newPic);
                }
            });
        }
        builder.setView(view);


        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onPicSelected(Pic pic) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, PicDetailFragment.newInstance(pic));
        ft.addToBackStack("list_fragment");
        ft.commit();
    }
}
