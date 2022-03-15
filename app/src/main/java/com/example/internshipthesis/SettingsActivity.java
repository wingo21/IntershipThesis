package com.example.internshipthesis;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 *  Classe per gestire le impostazioni
 * */
public class SettingsActivity extends AppCompatActivity {

    Button saveEmailButton, savePasswordButton;
    TextInputLayout inputUsername, inputEmail, inputPassword, confirmPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean username = false, email = false;
    String id = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        saveEmailButton = findViewById(R.id.saveEmailButton);
        savePasswordButton = findViewById(R.id.savePasswordButton);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
    }

    @Override
    protected void onStart() {

        super.onStart();

        //TODO: STILL DOESN'T ACTUALLY WORK

        // Sezione per il settaggio del pulsante saveEmailButton
        saveEmailButton.setOnClickListener(v -> {
            final String e = Objects.requireNonNull(inputEmail.getEditText()).getText().toString().trim();
            if (e.equals("")) {
                Toast.makeText(getApplicationContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show();
            }
            else if (!isValidEmail(e)) {
                Toast.makeText(getApplicationContext(), "Email invalid", Toast.LENGTH_SHORT).show();
            }
            else {

                db.collection("users")
                        .whereEqualTo("email", e)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot q : task.getResult()) {
                                    email = true;
                                }
                                if (username) {
                                    Toast.makeText(getApplicationContext(), "Email not available", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.collection("users").document(id).update("email", e);
                                    email = false;
                                    Toast.makeText(getApplicationContext(), "Changed successfully, reboot might be necessary", Toast.LENGTH_LONG).show();
                                    openScrollingActivity();
                                }
                            } else {
                                Log.d("TAGERROR", "ERRORE");
                            }
                        });
            }
        });

        // Sezione per il settaggio del pulsante savePasswordButton
        savePasswordButton.setOnClickListener(v -> {
            final String ip = Objects.requireNonNull(inputPassword.getEditText()).getText().toString().trim();
            final String cp = Objects.requireNonNull(confirmPassword.getEditText()).getText().toString().trim();
            if (ip.equals("")) {
                Toast.makeText(getApplicationContext(), "Field cannot be empty", Toast.LENGTH_SHORT).show();
            }
            else if (!ip.equals(cp)) {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else if (ip.length() < 8) {
                Toast.makeText(getApplicationContext(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            }
            else {
                db.collection("users").document(id).update("password", ip);
                Toast.makeText(getApplicationContext(), "Changed successfully, reboot might be necessary", Toast.LENGTH_LONG).show();
                openScrollingActivity();
            }
        });
    }

    // Metodo per controllare se l'email inserita ha pattern accettabile
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void openScrollingActivity() {

        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }
}