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

public class SettingsActivity extends AppCompatActivity {

    Button editSettings, bookings;
    TextView carShower, emailShower, userprofile;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String user = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
    String email = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        db.collection("users")
                .document(user)
                .get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                String name = task.getResult().getString("name");
                String carBrand = task.getResult().getString("carBrand");
                String carModel = task.getResult().getString("carModel");
                userprofile.setText(String.format(
                        "Hey there %s! Here you can view and modify informations linked to your account.\n" +
                        "Here's what we know about you:", name));
                carShower.setText(String.format("%s %s", carBrand, carModel));
            }
        });

    }

    private void openBookingActivity() {
        Intent intent = new Intent(this, BookingActivity.class);
        startActivity(intent);
    }

    private void openEditSettingsActivity() {

        Intent intent = new Intent(this, EditSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
