package com.example.internshipthesis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    Button editSettings;
    TextView yourEmail, emailShower;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String username = fAuth.getCurrentUser().getDisplayName();
    String email = fAuth.getCurrentUser().getEmail();
    String id = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editSettings = findViewById(R.id.edit_settings_button);
        yourEmail = findViewById(R.id.your_email);
        emailShower = findViewById(R.id.email_shower);

        emailShower.setText(email);

        editSettings.setOnClickListener(view -> openEditSettingsActivity());
    }

    private void openEditSettingsActivity() {

        Intent intent = new Intent(this, EditSettingsActivity.class);
        startActivity(intent);
    }
}
