package com.example.internshipthesis;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * This Activity will show the user some information stored in the
 * database about him (like his E-Mail or his car), it will also allow him
 * to edit this information and go to the activity that shows currently booked appointments
 */

public class SettingsActivity extends AppCompatActivity {

    Button editSettings, bookings;
    TextView carShower, emailShower, userprofile;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String user = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
    String email = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initialization of the app and scrolling feature

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editSettings = findViewById(R.id.edit_settings_button);
        bookings = findViewById(R.id.bookings);
        carShower = findViewById(R.id.car_shower);
        emailShower = findViewById(R.id.email_shower);
        userprofile = findViewById(R.id.userprofile);

        emailShower.setText(email);
        editSettings.setOnClickListener(view -> openEditSettingsActivity());
        bookings.setOnClickListener(view -> openBookingActivity());

        // Pulls the username from the database to create Textview accordingly
        // Also pulls info from the user profile to be shown

        db.collection("users")
                .document(user)
                .get().addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String name = task.getResult().getString("name");
                        String carBrand = task.getResult().getString("carBrand");
                        String carModel = task.getResult().getString("carModel");
                        userprofile.setText(String.format(
                                "Hey there %s!\n" +
                                "Here you can view and modify informations linked to your account.\n" +
                                "Here's what we know about you:", name));
                        carShower.setText(String.format("%s %s", carBrand, carModel));
                    }
                }
        );

    }

    // Function that opens the Activity where the user can see currently booked appointments

    private void openBookingActivity() {

        Intent intent = new Intent(this, BookingActivity.class);
        startActivity(intent);
    }

    // Function that opens the Activity where the user can modify his profile data

    private void openEditSettingsActivity() {

        Intent intent = new Intent(this, EditSettingsActivity.class);
        startActivity(intent);
    }

    // If the back arrow is pressed, the activity gets closed
    // and the user is brought back to ScrollingActivity

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
