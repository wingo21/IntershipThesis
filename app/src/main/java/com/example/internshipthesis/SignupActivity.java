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

/**
 * This Activity manages the user creation process
 */

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

        // Initialization of activity and scrolling feature

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

        // The following String arrays are used in the menus that allow the user to select
        // which car and model he owns

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

        // The actual menus

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

        // This implements the functionality of the menu when clicked

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

        // This is the button that completes the sign-in process, pulls all the input from
        // the fields and saves it in the database. Once the data is all correctly saved, the user is
        // automatically logged in and brought to ScrollingActivity

        signupButton.setOnClickListener(v -> {

            // Checks if fields are empty

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
                        "Username, Email and Password cannot be empty",
                        Toast.LENGTH_SHORT).show()
                ;
            }

            // Checks if E-Mail is valid

            else if (!isValidEmail(
                    inputEmail.getEditText()
                            .getText()
                            .toString()
                            .trim())) {
                Toast.makeText(getApplicationContext(),
                        "Invalid Email",
                        Toast.LENGTH_SHORT).show()
                ;
            }

            // Checks if Password is at least 8 characters long

            else if (inputPassword.getEditText()
                    .getText()
                    .toString()
                    .length() < 8) {
                Toast.makeText(getApplicationContext(),
                        "Password must be at least 8 characters long",
                        Toast.LENGTH_SHORT).show()
                ;
            }

            // Checks that Password does not contain space character

            else if (inputPassword.getEditText().getText().toString().contains(" ")) {

                Toast.makeText(getApplicationContext(),
                        "Password cannot contain space character",
                        Toast.LENGTH_SHORT).show()
                ;
            }

            // Checks that Password and Confirm Password match

            else if (!Objects.requireNonNull(
                    confirmPassword.getEditText())
                    .getText()
                    .toString()
                    .equals(
                            inputPassword
                                    .getEditText()
                                    .getText()
                                    .toString())) {
                Toast.makeText(getApplicationContext(),
                        "Passwords do not match",
                        Toast.LENGTH_SHORT).show()
                ;
            }

            // Checks that the car brand and model are not left empty

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
                Toast.makeText(getApplicationContext(),
                        "Car Brand or Model cannot be empty",
                        Toast.LENGTH_SHORT).show()
                ;
            }

            // Everything is good, we can create the user

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

                        Toast.makeText(SignupActivity.this,
                                "User Created",
                                Toast.LENGTH_SHORT).show()
                        ;
                        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                        DocumentReference documentReference = db.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("name", inputUsername.getEditText().getText().toString().trim());
                        user.put("email", inputEmail.getEditText().getText().toString().trim());
                        user.put("password", inputPassword.getEditText().getText().toString().trim());
                        user.put("carBrand", autoCompleteTextViewCarBrand.getText().toString().trim());
                        user.put("carModel", autoCompleteTextViewCarModel.getText().toString().trim());
                        documentReference.set(user).addOnSuccessListener(aVoid ->
                                Log.d("TAG",
                                        "onSuccess: User profile is created for " + userID))
                                ;
                        openLoginActivity();
                    } else {

                        Toast.makeText(SignupActivity.this,
                                "Error!" +
                                        Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show()
                        ;
                    }
                });
            }
        });

        backButton.setOnClickListener(v -> openLoginActivity());
    }

    // Function that opens LoginActivity

    private void openLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // Checks if E-Mail is valid

    public static boolean isValidEmail(CharSequence target) {

        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}