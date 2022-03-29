package com.example.internshipthesis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import static android.content.ContentValues.TAG;

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
}
