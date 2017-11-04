package afeka.katz.arkadiy.easyhr.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.function.Consumer;

import afeka.katz.arkadiy.easyhr.EasyHRContext;
import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.data.UsersDatabase;
import afeka.katz.arkadiy.easyhr.model.User;

public class StartUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private final int RC_SIGN_IN  = 1;
    private final String TAG = StartUpActivity.class.getCanonicalName();


    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mAuth = FirebaseAuth.getInstance();
        signIn();
    }

    private void signIn() {
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                AlertDialog loginFailed = new AlertDialog.Builder(this).
                        setTitle(R.string.login_failed_title).
                        setMessage(R.string.login_failed_text).
                        setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StartUpActivity.this.finish();
                            }
                        }).create();

                loginFailed.show();
            }
        }
    }

    private void updateUI (final FirebaseUser firebaseUser) {
        final UsersDatabase usersDB = UsersDatabase.getInstance();
        usersDB.getUser(firebaseUser.getUid()).thenAccept(new Consumer<User>() {
            @Override
            public void accept(User user) {
                StartUpActivity.this.findViewById(R.id.loading).setVisibility(View.GONE);
                if (user == null) {
                    Intent newUser = new Intent(StartUpActivity.this, NewUserActivity.class);
                    newUser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    StartUpActivity.this.startActivity(newUser);
                } else {
                    if (firebaseUser.isEmailVerified() && !user.getIsVerified()) {
                        user.setVerified();
                        usersDB.updateUser(user);
                    } else if (!user.getIsVerified()) {
                        AlertDialog failedDialog =
                                new AlertDialog.Builder(StartUpActivity.this).
                                        setTitle(R.string.user_not_verified_title).
                                        setMessage(R.string.user_not_verified_text).
                                        setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                StartUpActivity.this.finish();
                                            }
                                        }).
                                        setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                firebaseUser.sendEmailVerification();
                                                StartUpActivity.this.finish();
                                            }
                                        }).
                                        create();

                        failedDialog.show();
                        return;
                    }

                    EasyHRContext.getInstance().setCurrentUser(user);

                    Intent intent = new Intent(StartUpActivity.this, MainScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        AlertDialog failedDialog =
                                new AlertDialog.Builder(StartUpActivity.this).
                                        setTitle(R.string.sign_in_failed_title).
                                        setMessage(R.string.sign_in_failed).create();

                        failedDialog.show();
                    }
                }
            });
    }

}
