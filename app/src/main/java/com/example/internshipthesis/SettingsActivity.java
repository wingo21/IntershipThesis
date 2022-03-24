package com.example.internshipthesis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    Button editSettings;
    TextView yourEmail, emailShower, userprofile;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String username = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
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
        yourEmail = findViewById(R.id.your_email);
        emailShower = findViewById(R.id.email_shower);
        userprofile = findViewById(R.id.userprofile);

        emailShower.setText(email);
        editSettings.setOnClickListener(view -> openEditSettingsActivity());

        db.collection("users")
                .document(username)
                .get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                String x = task.getResult().getString("name");
                userprofile.setText(String.format("Ciao %s! Qui puoi visualizzare e modificare informazioni riguardanti il tuo profilo", x));

            }
        });

    }

    private void openEditSettingsActivity() {

        Intent intent = new Intent(this, EditSettingsActivity.class);
        startActivity(intent);
    }
}
