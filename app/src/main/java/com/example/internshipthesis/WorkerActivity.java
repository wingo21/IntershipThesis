package com.example.internshipthesis;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class WorkerActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String user = fAuth.getUid();
    int workerNum;
    String name;
    float rating;
    String slot;
    LinearLayout layout;

    //TODO: Add more info to the worker profile (maybe a little description, full schedule, professional info, ecc)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            workerNum = extras.getInt("key");
            //The key argument here must match that used in the other activity
        }

        TextView nameWorker = findViewById(R.id.nameWorker);
        RatingBar RatingBar = findViewById(R.id.ratingBar);
        Button submitButton = findViewById(R.id.submitButton);
        ImageView workerImage = findViewById(R.id.workerImage);
        layout = findViewById(R.id.workerone);
        String workerImgSrc = "worker"+ workerNum; //  this is image file name
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        int imgId = getResources().getIdentifier(PACKAGE_NAME+":drawable/"+workerImgSrc , null, null);

        DocumentReference docRef = db.collection("workers").document(String.valueOf(workerNum));
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getName();
                    nameWorker.setText(name);
                    rating = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getRating();
                    RatingBar.setRating(rating);
                    workerImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),imgId));

                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // perform click event on button
        submitButton.setOnClickListener(v -> {
            DocumentReference changerating = db.collection("workers").document(String.valueOf(workerNum));
            changerating.update("rating", RatingBar.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            // get values and then displayed in a toast
            String rating1 = "On a scale of 5 Stars\n New rating = " + RatingBar.getRating();
            Toast.makeText(getApplicationContext(), rating1, Toast.LENGTH_LONG).show();
        });

        getInfoForAppointments(workerNum);
    }

    private void getInfoForAppointments(int workerNum){
        db.collection("workers")
                .document(String.valueOf(workerNum))
                .collection("schedule")
                .whereEqualTo("booked", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Log.d(TAG, "Document ID: " + document.getId());

                            String documentID = document.getId();

                            Log.d(TAG, "Document ID variable inside getInfoForAppointments: " + documentID);

                            slot = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getSlot();
                            Log.d(TAG, "String for slot: " + slot);
                            addAppointment(slot, documentID);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void addAppointment(String slot, String documentID) {
        View appointment = getLayoutInflater().inflate(R.layout.appointment_layout, layout, false);

        TextView appointment_slot_textview = appointment.findViewById(R.id.appointment_slot);
        Button bookButton = appointment.findViewById(R.id.bookButton);

        appointment_slot_textview.setText(slot);

        // perform click event on button
        bookButton.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(WorkerActivity.this)
                    .setTitle("You are about to book this appointment. Are you sure?")
                    .setPositiveButton("Confirm", (dialog12, whichButton) -> {
                        db.collection("workers")
                                .document(String.valueOf(workerNum))
                                .collection("schedule")
                                .document(documentID)
                                .update("booked", true);

                        db.collection("workers")
                                .document(String.valueOf(workerNum))
                                .collection("schedule")
                                .document(documentID)
                                .update("bookedby", user);

                        AlertDialog dialog1 = new AlertDialog.Builder(WorkerActivity.this)
                                .setTitle("You successfully booked your appointment!")
                                .setNeutralButton("Yay", (dialog2, whichButton1) -> openScrollingActivity())
                                .create();
                        dialog1.show();
                    })
                    .setNegativeButton("Cancel", null).create();
            dialog.show();
        });

        layout.addView(appointment);
    }

    private void openScrollingActivity() {
        Intent intent = new Intent(this, ScrollingActivity.class);
        startActivity(intent);
    }
}