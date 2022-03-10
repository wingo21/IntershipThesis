package com.example.internshipthesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 *  Classe per gestire l'attività di login
 * */
public class LoginActivity extends Activity {

    Button login, signup;
    EditText email_field, password_field;
    ProgressBar progressBar;
    int c = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String s;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //if(fAuth.getCurrentUser() != null) {
        //    openScrollingActivity();
        //}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login_button);
        email_field = findViewById(R.id.email_edit_field);
        password_field = findViewById(R.id.password_edit_field);
        signup = findViewById(R.id.signup_button);
        progressBar = findViewById(R.id.progressBar);

    }


    /** Crea la schermata di login */
    protected void onStart() {

        super.onStart();

        login.setOnClickListener(view -> {

            fAuth.signInWithEmailAndPassword(email_field.getText().toString().trim(),password_field.getText().toString().trim()).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    openScrollingActivity();
                }else{
                    Toast.makeText(LoginActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            progressBar.setVisibility(View.VISIBLE);
        });

        /*login.setOnClickListener(v -> db.collection("users").whereEqualTo("username", username_field.getText().toString()).whereEqualTo("password", password_field.getText().toString())
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot q : task.getResult()) {

                            s = q.getId();
                            c++;
                        }

                        if (c > 0) {

                            SharedPreferences settings = getApplicationContext().getSharedPreferences("Set", 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("ID", s);
                            editor.apply();

                            openScrollingActivity();
                        } else {

                            Toast.makeText(getApplicationContext(), "Username o password errata", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));*/

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
