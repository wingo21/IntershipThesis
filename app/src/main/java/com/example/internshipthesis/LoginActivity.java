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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 *  Classe per gestire l'attività di login
 * */
public class LoginActivity extends AppCompatActivity {

    Button login, signup;
    TextInputLayout email_field, password_field;
    ProgressBar progressBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

    /** Crea la schermata di login */
    protected void onStart() {

        super.onStart();

        if(fAuth.getCurrentUser() != null) {
            openScrollingActivity();
        }

        login.setOnClickListener(view ->
                fAuth.signInWithEmailAndPassword(Objects.requireNonNull(email_field.getEditText()).getText().toString().trim(), Objects.requireNonNull(password_field.getEditText()).getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.VISIBLE);
                                openScrollingActivity();
                            }else{
                                Toast.makeText(LoginActivity.this, "Error!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
        }));

        signup.setOnClickListener(v -> openSignupActivity());
    }

    private void openScrollingActivity() {

        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }

    private void openSignupActivity() {

        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

}
