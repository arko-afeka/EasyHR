package afeka.katz.arkadiy.easyhr.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.activities.fragments.UserDataFragment;
import afeka.katz.arkadiy.easyhr.data.UsersDatabase;
import afeka.katz.arkadiy.easyhr.model.User;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, UserDataFragment.OnFragmentInteractionListener {
    private final int RC_SIGN_IN  = 1;
    private final String TAG = MainActivity.class.getCanonicalName();


    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                finish();
            }
        }
    }

    private void updateUI (FirebaseUser firebaseUser) {
        UsersDatabase usersDB = new UsersDatabase();
        usersDB.getUser(firebaseUser.getUid()).thenAccept(new Consumer<User>() {
            @Override
            public void accept(User user) {
                MainActivity.this.findViewById(R.id.loading).setVisibility(View.GONE);
                if (user == null) {
                    Fragment userData = UserDataFragment.newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.add(R.id.contentPanel, userData).commit();
                } else {
                    if (!user.isVerified()) {
                        AlertDialog failedDialog =
                                new AlertDialog.Builder(MainActivity.this).
                                        setTitle(R.string.user_not_verified_title).
                                        setMessage(R.string.user_not_verified_text).
                                        setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                MainActivity.this.finish();
                                            }
                                        }).
                                        create();

                        failedDialog.show();
                        return;
                    }

                    if (user.getCompanies().size() > 0) {

                    } else {
                        AlertDialog failedDialog =
                                new AlertDialog.Builder(MainActivity.this).
                                        setTitle(R.string.user_type_title).
                                        setMessage(R.string.user_type_text).
                                        setItems(R.array.user_type_items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (which == 0) {

                                                } else {

                                                }
                                            }
                                        }).
                                        create();

                        failedDialog.show();
                    }
                }
            }
        });
    }

    @Override
    public void onUserDataFilled(String name, String familyName, String email) {
        final UsersDatabase database = new UsersDatabase();

        final User user = new User(mAuth.getCurrentUser());
        user.setEmail(email);
        user.setFamily(familyName);
        user.setName(name);

        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    //TODO: Add verify email not sent
                } else {
                    AlertDialog verifyDialog =
                            new AlertDialog.Builder(MainActivity.this).
                                    setTitle(R.string.user_verify_sent_title).
                                    setMessage(R.string.user_verify_sent_text).
                                    setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.this.finish();
                                        }
                                    }).
                                    create();
                    verifyDialog.show();
                    database.addUser(user);
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
                                new AlertDialog.Builder(MainActivity.this).
                                        setTitle(R.string.sign_in_failed_title).
                                        setMessage(R.string.sign_in_failed).create();

                        failedDialog.show();
                    }
                }
            });
    }

}
