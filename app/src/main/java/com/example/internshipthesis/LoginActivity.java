package com.example.internshipthesis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 *  This is the Activity that manages the access to the app.
 *  Users can log in, or create a new account.
 *  If the user is already logged in from a previous session,
 *  the app will remember him and skip this Activity, launching
 *  directly in ScrollingActivity.
 */

public class LoginActivity extends AppCompatActivity {

    Button login, signup;
    TextInputLayout email_field, password_field;
    ProgressBar progressBar;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization of activity and scrolling feature

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        login = findViewById(R.id.login_button);
        email_field = findViewById(R.id.email_edit_field);
        password_field = findViewById(R.id.password_edit_field);
        signup = findViewById(R.id.signup_button);
        progressBar = findViewById(R.id.progressBar);
    }

    protected void onStart() {

        super.onStart();

        // This checks if the user has already completed access in another session.
        // If he did, ScrollingActivity gets opened instead.

        if(fAuth.getCurrentUser() != null) {

            openScrollingActivity();
        }

        // This is the button that checks that the input fields are correctly filled in
        // and completes the login process

        login.setOnClickListener(view ->

                fAuth.signInWithEmailAndPassword(
                        Objects.requireNonNull(
                                email_field.getEditText())
                                .getText()
                                .toString()
                                .trim(),
                        Objects.requireNonNull(
                                password_field.getEditText())
                                .getText()
                                .toString()
                                .trim()
                ).addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {

                        Toast.makeText(LoginActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show()
                        ;
                        progressBar.setVisibility(View.VISIBLE);
                        openScrollingActivity();
                    } else {

                        Toast.makeText(LoginActivity.this,
                                "Error!" +
                                        Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show()
                        ;
                    }
                })
        );

        signup.setOnClickListener(v -> openSignupActivity());
    }

    // Function that opens ScrollingActivity

    private void openScrollingActivity() {

        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }

    // Function that opens SignupActivity

    private void openSignupActivity() {

        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

}
