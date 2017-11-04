package afeka.katz.arkadiy.easyhr.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.function.Consumer;

import afeka.katz.arkadiy.easyhr.EasyHRContext;
import afeka.katz.arkadiy.easyhr.R;
import afeka.katz.arkadiy.easyhr.activities.fragments.UserInfoFragment;
import afeka.katz.arkadiy.easyhr.data.UsersDatabase;
import afeka.katz.arkadiy.easyhr.model.User;

public class NewUserActivity extends AppCompatActivity implements UserInfoFragment.OnFragmentInteractionListener {
    private FirebaseAuth mAuth;

    @Override
    public void onUserDataFilled(String name, String familyName) {
        final UsersDatabase database = UsersDatabase.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final User user = new User(mAuth.getCurrentUser());
        user.setEmail(mAuth.getCurrentUser().getEmail());
        user.setFamily(familyName);
        user.setName(name);

        if (firebaseUser.isEmailVerified()) {
            user.setVerified();
            database.updateUser(user).thenAccept(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {
                    if (aBoolean) {
                        Intent intent = new Intent(NewUserActivity.this, NewUserActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        NewUserActivity.this.startActivity(intent);
                    } else {
                        finish();
                    }
                }
            });
            return;
        }
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    AlertDialog verificationNotSent =
                            new AlertDialog.Builder(NewUserActivity.this).
                                    setTitle(R.string.user_verify_sent_title).
                                    setMessage(R.string.user_verify_not_sent_text).
                                    setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            NewUserActivity.this.finish();
                                        }
                                    }).
                                    create();
                    verificationNotSent.show();
                } else {
                    AlertDialog verifyDialog =
                            new AlertDialog.Builder(NewUserActivity.this).
                                    setTitle(R.string.user_verify_sent_title).
                                    setMessage(R.string.user_verify_sent_text).
                                    setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            NewUserActivity.this.finish();
                                        }
                                    }).
                                    create();
                    verifyDialog.show();
                    database.updateUser(user);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        UsersDatabase database = UsersDatabase.getInstance();
        database.getUser(firebaseUser.getUid()).thenAccept(user -> {
            if (user != null && user.getIsVerified()) {
                EasyHRContext.getInstance().setCurrentUser(user);
                AlertDialog userTypeDialog =
                        new AlertDialog.Builder(NewUserActivity.this).
                                setTitle(R.string.user_type_title).
                                setMessage(R.string.user_type_text).
                                setPositiveButton(R.string.user_type_choice_manager, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(NewUserActivity.this, NewCompanyActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        NewUserActivity.this.startActivity(intent);
                                    }
                                }).
                                setNegativeButton(R.string.user_type_choice_worker, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(NewUserActivity.this, MainScreenActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        NewUserActivity.this.startActivity(intent);
                                    }
                                }).create();

                userTypeDialog.show();
            } else if (user == null){
                Fragment userData = UserInfoFragment.newInstance(null);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.content_panel, userData).commit();
                findViewById(R.id.loading).setVisibility(View.GONE);
            }
        });
    }
}
