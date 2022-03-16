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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *  Classe per gestire le impostazioni
 * */
public class EditSettingsActivity extends AppCompatActivity {

    Button saveEmailButton, savePasswordButton;
    TextInputLayout inputUsername, inputEmail, inputPassword, confirmPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    String email = fAuth.getCurrentUser().getEmail();
    String id = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_settings);

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

                Map<String,Object> newEmail = new HashMap<>();
                newEmail.put("email", e);

                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot.getId();
                                db.collection("users")
                                        .document(documentID)
                                        .update(newEmail)
                                        .addOnSuccessListener(aVoid -> {

                                            Toast.makeText(EditSettingsActivity.this, "Email successfully updated", Toast.LENGTH_SHORT).show();
                                            openScrollingActivity();
                                        });

                                user.updateEmail(e).addOnSuccessListener(aVoid -> {
                                    DocumentReference documentReference = db.collection("users").document(documentID);
                                    documentReference.update(newEmail);
                                    Toast.makeText(EditSettingsActivity.this, "Email successfully updated part2", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e1 ->
                                        Toast.makeText(EditSettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());



                                /*for (QueryDocumentSnapshot q : task.getResult()) {
                                    email = e;
                                }
                                if (username) {
                                    Toast.makeText(getApplicationContext(), "Email not available", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    db.collection("users").document(id).update("email", e);

                                    Toast.makeText(getApplicationContext(), "Changed successfully, reboot might be necessary", Toast.LENGTH_LONG).show();
                                    openScrollingActivity();
                                }*/
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