package com.example.internshipthesis;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    Button signupButton, backButton;
    TextInputLayout inputUsername, inputEmail, inputPassword, confirmPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth;
    String userID;
    String brand;
    private AutoCompleteTextView autoCompleteTextViewCarBrand;
    private AutoCompleteTextView autoCompleteTextViewCarModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        signupButton = findViewById(R.id.signupButton);
        backButton = findViewById(R.id.backButton);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        autoCompleteTextViewCarBrand = findViewById(R.id.autoCompleteCarBrand);
        autoCompleteTextViewCarModel = findViewById(R.id.autoCompleteCarModel);
        fAuth = FirebaseAuth.getInstance();

        final String[] carBrands = new String[]{

                "Lamborghini",
                "Bugatti",
                "Porsche",
                "Bentley",
                "Ferrari",
                "Mercedes",
        };

        final String[] carModels = new String[]{

                "Choose a car brand first",
        };

        final String[] lamborghiniModels = new String[]{

                "Aventador",
                "Urus",
                "Sian",
                "Huracàn",

        };

        final String[] bugattiModels = new String[]{

                "Centodieci",
                "Chiron",
                "Veyron",
                "Bolide",

        };

        final String[] porscheModels = new String[]{

                "Cayenne",
                "Macan",
                "Taycan",
                "Panamera",

        };

        final String[] bentleyModels = new String[]{

                "Continental",
                "Flying Spur",
                "Bentayga",

        };

        final String[] ferrariModels = new String[]{

                "Roma",
                "Portofino",
                "California",
                "LaFerrari",

        };

        final String[] mercedesModels = new String[]{

                "Classe A",
                "Classe C",
                "Classe E",
                "Classe G",

        };

        ArrayAdapter<String> adapterCarBrand = new ArrayAdapter<>(
                SignupActivity.this,
                R.layout.dropdown_item,
                carBrands
        );
        autoCompleteTextViewCarBrand.setAdapter(adapterCarBrand);

        ArrayAdapter<String> adapterCarModel = new ArrayAdapter<>(
                SignupActivity.this,
                R.layout.dropdown_item,
                carModels
        );
        autoCompleteTextViewCarModel.setAdapter(adapterCarModel);

        autoCompleteTextViewCarBrand.setOnItemClickListener((parent, view, position, id) -> {

            brand = autoCompleteTextViewCarBrand.getText().toString();
            if (autoCompleteTextViewCarBrand.getText().toString().equals("Lamborghini")) {

                ArrayAdapter<String> adapterLamborghini = new ArrayAdapter<>(
                        SignupActivity.this,
                        R.layout.dropdown_item,
                        lamborghiniModels
                );

                autoCompleteTextViewCarModel.setAdapter(adapterLamborghini);
            } else if (brand.equals("Bugatti")) {

                ArrayAdapter<String> adapterBugatti = new ArrayAdapter<>(
                        SignupActivity.this,
                        R.layout.dropdown_item,
                        bugattiModels
                );

                autoCompleteTextViewCarModel.setAdapter(adapterBugatti);
            } else if (brand.equals("Porsche")) {

                ArrayAdapter<String> adapterPorsche = new ArrayAdapter<>(
                        SignupActivity.this,
                        R.layout.dropdown_item,
                        porscheModels
                );

                autoCompleteTextViewCarModel.setAdapter(adapterPorsche);
            } else if (brand.equals("Ferrari")) {

                ArrayAdapter<String> adapterFerrari = new ArrayAdapter<>(
                        SignupActivity.this,
                        R.layout.dropdown_item,
                        ferrariModels
                );

                autoCompleteTextViewCarModel.setAdapter(adapterFerrari);
            } else if (brand.equals("Bentley")) {

                ArrayAdapter<String> adapterBentley = new ArrayAdapter<>(
                        SignupActivity.this,
                        R.layout.dropdown_item,
                        bentleyModels
                );

                autoCompleteTextViewCarModel.setAdapter(adapterBentley);
            } else if (brand.equals("Mercedes")){

                ArrayAdapter<String> adapterMercedes = new ArrayAdapter<>(
                        SignupActivity.this,
                        R.layout.dropdown_item,
                        mercedesModels
                );

                autoCompleteTextViewCarModel.setAdapter(adapterMercedes);
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();

        // Sezione per il settaggio del pulsante signupButton, con controlli sugli elementi username,
        // email e password
        signupButton.setOnClickListener(v -> {

            if (Objects.requireNonNull(
                    inputUsername.getEditText())
                    .getText()
                    .toString()
                    .trim()
                    .equals("")
                    || Objects.requireNonNull(
                            inputEmail.getEditText())
                    .getText().toString()
                    .trim()
                    .equals("")
                    || Objects.requireNonNull(
                            inputPassword.getEditText())
                    .getText()
                    .toString()
                    .trim()
                    .equals("")) {
                Toast.makeText(getApplicationContext(),
                        "Username, Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
            }

            else if (!isValidEmail(
                    inputEmail.getEditText()
                            .getText()
                            .toString()
                            .trim())) {
                Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
            }

            else if (inputPassword.getEditText()
                    .getText()
                    .toString()
                    .length() < 8) {
                Toast.makeText(getApplicationContext(),
                        "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            }

            else if (inputPassword.getEditText().getText().toString().contains(" ")) {

                Toast.makeText(getApplicationContext(),
                        "Password cannot contain space character", Toast.LENGTH_SHORT).show();
            }

            else if (!Objects.requireNonNull(
                    confirmPassword.getEditText())
                    .getText()
                    .toString()
                    .equals(
                            inputPassword
                                    .getEditText()
                                    .getText()
                                    .toString())) {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else if (Objects.requireNonNull(
                    autoCompleteTextViewCarBrand)
                    .getText()
                    .toString()
                    .trim()
                    .equals("")
                    || Objects.requireNonNull(
                            autoCompleteTextViewCarModel)
                    .getText()
                    .toString()
                    .trim()
                    .equals("")
                    || Objects.requireNonNull(
                            autoCompleteTextViewCarModel)
                    .getText()
                    .toString()
                    .trim()
                    .equals("Choose a car brand first")) {
                Toast.makeText(getApplicationContext(), "Car Brand or Model cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }
            else {

                fAuth.createUserWithEmailAndPassword(
                        inputEmail.getEditText()
                                .getText()
                                .toString()
                                .trim(),
                        inputPassword.getEditText()
                                .getText()
                                .toString()
                                .trim())
                        .addOnCompleteListener((task) -> {

                    if(task.isSuccessful()){

                        Toast.makeText(SignupActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                        DocumentReference documentReference = db.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("name", inputUsername.getEditText().getText().toString().trim());
                        user.put("email", inputEmail.getEditText().getText().toString().trim());
                        user.put("password", inputPassword.getEditText().getText().toString().trim());
                        user.put("carBrand", autoCompleteTextViewCarBrand.getText().toString().trim());
                        user.put("carModel", autoCompleteTextViewCarModel.getText().toString().trim());
                        documentReference.set(user).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: User profile is created for " + userID));
                        openLoginActivity();
                    }else{

                        Toast.makeText(SignupActivity.this, "Error!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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