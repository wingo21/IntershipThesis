package com.example.internshipthesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *  Classe per gestire l'attività di registrazione
 * */
public class SignupActivity extends Activity {

    Button signupButton, backButton;
    TextInputLayout inputUsername, inputEmail, inputPassword, confirmPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        signupButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.backButton);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        confirmPassword = findViewById(R.id.confirmPassword);

        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {

        super.onStart();

        // Sezione per il settaggio del pulsante signupButton, con controlli sugli elementi username, email e password
        signupButton.setOnClickListener(v -> {

            if (Objects.requireNonNull(inputUsername.getEditText()).getText().toString().trim().equals("") || Objects.requireNonNull(inputEmail.getEditText()).getText().toString().trim().equals("") || Objects.requireNonNull(inputPassword.getEditText()).getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Username, Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
            }
            else if (!isValidEmail(inputEmail.getEditText().getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
            }
            else if (inputPassword.getEditText().getText().toString().length() < 8) {
                Toast.makeText(getApplicationContext(), "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            }
            else if (inputPassword.getEditText().getText().toString().contains(" ")) {
                Toast.makeText(getApplicationContext(), "Password cannot contain space character", Toast.LENGTH_SHORT).show();
            }
            else if (!Objects.requireNonNull(confirmPassword.getEditText()).getText().toString().equals(inputPassword.getEditText().getText().toString())) {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else {

                fAuth.createUserWithEmailAndPassword(inputEmail.getEditText().getText().toString().trim(),inputPassword.getEditText().getText().toString().trim()).addOnCompleteListener((task) -> {
                    if(task.isSuccessful()){
                        Toast.makeText(SignupActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("name", inputUsername.getEditText().getText().toString().trim());
                        user.put("email", inputEmail.getEditText().getText().toString().trim());
                        user.put("password", inputPassword.getEditText().getText().toString().trim());
                        documentReference.set(user).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: User profile is created for " + userID));
                        openLoginActivity();
                    }else{
                        Toast.makeText(SignupActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backButton.setOnClickListener(v -> openLoginActivity());
    }

    private void openLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Metodo per controllare se l'email inserita ha pattern accettabile
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}