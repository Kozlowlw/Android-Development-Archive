package edu.rosehulman.passwordkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

import edu.rosehulman.rosefire.Rosefire;
import edu.rosehulman.rosefire.RosefireResult;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginListener, PasswordFragment.OnLogoutListener, GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    OnCompleteListener mOnCompleteListener;
    private GoogleApiClient mGoogleApiClient;

    private final static int RC_GOOGLE_LOGIN = 1;
    private final static int RC_ROSEFIRE_LOGIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        initializeListeners();
        setupGoogleSignIn();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
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
        //DONE: Log user in with RoseFire account
        Intent signInIntent = Rosefire.getSignInIntent(this, getString(R.string.rosefire_key));
        startActivityForResult(signInIntent, RC_ROSEFIRE_LOGIN);
    }

    @Override
    public void onLogout() {
        //DONE: Log the user out.
        mAuth.signOut();
    }

    @Override
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    // MARK: Provided Helper Methods
    private void switchToLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, new LoginFragment(), "Login");
        ft.commit();
    }

    private void switchToPasswordFragment(String path) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment passwordFragment = new PasswordFragment();
        Bundle args = new Bundle();
        args.putString(Constants.FIREBASE_PATH, path);
        passwordFragment.setArguments(args);
        ft.replace(R.id.fragment, passwordFragment, "Passwords");
        ft.commit();
    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }

    private void initializeListeners(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(Constants.TAG, "Current user: " + user);
                if(user != null){
                    switchToPasswordFragment("users/"+user.getUid());
                }else{
                    switchToLoginFragment();
                }
            }
        };
        mOnCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(!task.isSuccessful()){
                    showLoginError("Authentication Failed");
                }
            }
        };
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showLoginError(connectionResult.toString());
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d(Constants.TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, mOnCompleteListener);
    }

    private void firebaseAuthWithRoseFire(RosefireResult result){
        mAuth.signInWithCustomToken(result.getToken()).addOnCompleteListener(mOnCompleteListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        }
    }
}
