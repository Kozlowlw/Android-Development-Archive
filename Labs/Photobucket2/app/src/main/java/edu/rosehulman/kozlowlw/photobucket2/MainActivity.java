package edu.rosehulman.kozlowlw.photobucket2;


import android.Manifest;
import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

import edu.rosehulman.kozlowlw.photobucket2.fragments.Constants;
import edu.rosehulman.kozlowlw.photobucket2.fragments.LoginFragment;
import edu.rosehulman.kozlowlw.photobucket2.fragments.PicDetailFragment;
import edu.rosehulman.kozlowlw.photobucket2.fragments.PicListAdapter;
import edu.rosehulman.kozlowlw.photobucket2.fragments.PicListFragment;
import edu.rosehulman.rosefire.Rosefire;
import edu.rosehulman.rosefire.RosefireResult;

public class MainActivity extends AppCompatActivity implements PicListFragment.Callback, LoginFragment.OnLoginListener {

    private PicListAdapter mAdapter;
    private DatabaseReference mRef;
    private PicListFragment picListFragment;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    OnCompleteListener mOnCompleteListener;
    private GoogleApiClient mGoogleApiClient;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    private final static int RC_GOOGLE_LOGIN = 1;
    private final static int RC_ROSEFIRE_LOGIN = 2;
    private final static int RC_GITHUB_LOGIN = 3;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 4;
    private static final int RC_TAKE_PICTURE = 5;
    private static final int RC_CHOOSE_PICTURE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPhoto();
            }
        });

//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        mProgressBar.setVisibility(View.GONE);

        checkPermissions();
        mAuth = FirebaseAuth.getInstance();
        initializeListeners();
        //setupGoogleSignIn();


    }

    public void switchToLoginFragment() {
        toolbar.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new LoginFragment(), "Login");
        ft.commit();
    }

    public void switchToListFragment(String path) {
        toolbar.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        picListFragment = new PicListFragment();
        Bundle args = new Bundle();
        args.putString("PATH", path);
        picListFragment.setArguments(args);
        ft.replace(R.id.fragment_container, picListFragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fab.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
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
        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                return true;
            case R.id.action_show:
                Constants.viewAll = !Constants.viewAll;
                item.setTitle((Constants.viewAll) ? "Show Mine" : "Show All");
                picListFragment.getAdapter().updateList(Constants.viewAll);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddPhoto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        Log.d("OPB", "Opening dialog");
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
        builder.create().show();

    }

    private void showAddEditDialog(final Pic pic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add, null, false);
        builder.setTitle(getString(pic == null ? R.string.dialog_add_title : R.string.dialog_edit_title));
        builder.setView(view);
        final EditText captionET = view.findViewById(R.id.dialog_caption);
        final EditText urlET = view.findViewById(R.id.dialog_url);
        if (pic != null) {
            urlET.setText(pic.getUrl());
            captionET.setText(pic.getCaption());
            TextWatcher textWatcher = new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String url = urlET.getText().toString();
                    String caption = captionET.getText().toString();
                    picListFragment.getAdapter().update(pic, caption, url, Constants.currentUID);

                }
            };
            captionET.addTextChangedListener(textWatcher);
            urlET.addTextChangedListener(textWatcher);
            builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    picListFragment.getAdapter().remove(pic);
                }
            });
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pic == null) {
                    String url = urlET.getText().toString();
                    String caption = captionET.getText().toString();
                    picListFragment.getAdapter().add(new Pic(caption, url, Constants.currentUID));
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);


        builder.create().show();
    }


    @Override
    public void onClick(PicListAdapter adapter, Pic pic) {
        mAdapter = adapter;
        fab.setVisibility(View.GONE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment detailed = PicDetailFragment.newInstance(pic);
        ft.replace(R.id.fragment_container, detailed);
        ft.addToBackStack("detail");
        ft.commit();
    }

    @Override
    public void onEdit(PicListAdapter adapter, Pic pic) {
        mAdapter = adapter;
        if (Constants.currentUID.equals(pic.getUid())) {
            showAddEditDialog(pic);
        } else {
            Snackbar.make(picListFragment.getView(), R.string.cant_edit, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void changedTitle(String s) {
        setTitle(s);
    }

//    private void setupGoogleSignIn() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
//    }

    public void onLogin(String email, String password) {
        //DONE: Log user in with username & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mOnCompleteListener);
    }

    public void onGoogleLogin() {
        //DONE: Log user in with Google account
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_GOOGLE_LOGIN);
    }

    public void onRosefireLogin() {
        //DONE: Log user in with RoseFire account
        Intent signInIntent = Rosefire.getSignInIntent(this, getString(R.string.rosefire_key));
        startActivityForResult(signInIntent, RC_ROSEFIRE_LOGIN);
    }

    @Override
    public void onGithubLogin() {
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        showLoginError(connectionResult.toString());
//    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }

    private void initializeListeners() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("OPB", "Current user: " + user);
                if (user != null) {
                    Constants.currentUID = user.getUid();
                    switchToListFragment("users/" + user.getUid());
                } else {
                    Constants.currentUID = "";
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("OPB", "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, mOnCompleteListener);
    }

    private void firebaseAuthWithRoseFire(RosefireResult result) {
        mAuth.signInWithCustomToken(result.getToken()).addOnCompleteListener(mOnCompleteListener);
    }

    public void firebaseAuthWithGithub(String token){
        AuthCredential credential  = GithubAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential).addOnCompleteListener(mOnCompleteListener);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            case RC_ROSEFIRE_LOGIN:
                RosefireResult resultRF = Rosefire.getSignInResultFromIntent(data);
                if (resultRF.isSuccessful()) {
                    firebaseAuthWithRoseFire(resultRF);
                } else {
                    showLoginError("Rosefire Authentication failed");
                }
                break;
            case RC_GITHUB_LOGIN:
                Uri token = data.getData();

                Log.d("OPB",token.toString());
                token.getQueryParameter("code");
                //firebaseAuthWithGithub();
                break;
        }
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

    }


    private void checkPermissions() {
        // Check to see if we already have permissions
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            // If we do not, request them from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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

    private void sendCapturedPhotoToAdapter(String name, Intent data) {
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        String location = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, name, null);
//        picListFragment.getAdapter().setProgressBar(mProgressBar);
        picListFragment.getAdapter().addPicture(name, location, bitmap);
    }

    private void sendGalleryPhotoToAdapter(String name, Intent data) {
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();
            String location = uri.toString();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                picListFragment.getAdapter().setProgressBar(mProgressBar);
                picListFragment.getAdapter().addPicture(name, location, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
