package com.example.internshipthesis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

/**
 * This is the activity that shows the user the appointments he already booked.
 * Users are allowed to book only one appointment with each worker
 * until it expires or gets cancelled.
 */

public class BookingActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String user = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
    LinearLayout layout;
    String workerID;
    String name;
    String slot;
    String documentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization of activity, implementation of scrolling feature requirements

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        layout = findViewById(R.id.thisonenew);
        getInfoForBookings();

    }

    // Function that pulls the workerIDs from the database, this function will pass
    // the IDs to addAllBookings().

    private void getInfoForBookings() {

        db.collection("workers")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            workerID = document.getId();
                            addAllBookings(Integer.parseInt(workerID));
                        }
                    } else {

                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
        );
    }

    // This function checks that only appointments that are booked by the user are shown
    // in the list.
    // For each appointment found, it calls addBooked() that will create the actual card.

    private void addAllBookings(int workerNum){

        db.collection("workers")
                .document(String.valueOf(workerNum))
                .collection("schedule")
                .whereEqualTo("booked", true)
                .whereEqualTo("bookedby", user)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            addBooked(workerNum);
                        }
                    } else {

                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
        );
    }

    // Function that pulls all the data from the database to fill the appointment card with

    @SuppressLint("SetTextI18n")
    private void addBooked(int workerNum) {

        View booking = getLayoutInflater().inflate(R.layout.booking_layout, layout, false);
        TextView nameWorker = booking.findViewById(R.id.nameWorker);
        Button cancelBookingButton = booking.findViewById(R.id.cancel_booking_Button);
        ImageView workerImage = booking.findViewById(R.id.workerImage);
        TextView your_appointment = booking.findViewById(R.id.textView3);
        String workerImgSrc = "worker"+ workerNum; //  this is image file name
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        int imgId = getResources().getIdentifier(PACKAGE_NAME+":drawable/"+workerImgSrc , null, null);

        DocumentReference docRef = db.collection("workers").document(String.valueOf(workerNum));
        docRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    name = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getName();
                    nameWorker.setText(name);
                    workerImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),imgId));

                    db.collection("workers")
                            .document(String.valueOf(workerNum))
                            .collection("schedule")
                            .whereEqualTo("booked", true)
                            .whereEqualTo("bookedby", user)
                            .get()
                            .addOnCompleteListener(task1 -> {

                                if (task1.isSuccessful()) {

                                    for (QueryDocumentSnapshot document1 : task1.getResult()) {

                                        documentID = document1.getId();
                                        slot = (Objects.requireNonNull(document1.toObject(Classes.Worker.class))).getSlot();
                                        your_appointment.setText(slot);
                                    }
                                } else {

                                    Log.d(TAG, "Error getting documents: ", task1.getException());
                                }
                            }
                    );
                } else {

                    Log.d(TAG, "No such document");
                }
            } else {

                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // This is the button that allows the user to cancel the appointment.
        // If clicked, a dialog will pop up asking the user to confirm his choice.
        // If the user clicks "Cancel", the dialog gets closed and nothing happens.
        // If the user clicks "Confirm", the database gets updated deleting user info
        // from the appropriate fields, making the appointment available to be
        // booked again, then the activity gets closed.

        cancelBookingButton.setOnClickListener(v -> {

            AlertDialog dialog = new AlertDialog.Builder(BookingActivity.this)
                    .setTitle("You are about to cancel this appointment. Are you sure?")
                    .setPositiveButton("Confirm", (dialog12, whichButton) -> {

                        db.collection("workers")
                                .document(String.valueOf(workerNum))
                                .collection("schedule")
                                .document(documentID)
                                .update("booked", false)
                        ;

                        db.collection("workers")
                                .document(String.valueOf(workerNum))
                                .collection("schedule")
                                .document(documentID)
                                .update("bookedby", "")
                        ;

                        AlertDialog dialog1 = new AlertDialog.Builder(BookingActivity.this)
                                .setTitle("You successfully cancelled your appointment")
                                .setNeutralButton("Ok", (dialog2, whichButton1) -> finish())
                                .create();
                        dialog1.show();
                    })
                    .setNegativeButton("Cancel", null).create();
            dialog.show();
        });

        booking.setOnClickListener(view -> openWorkerActivity(workerNum));
        layout.addView(booking);
    }

    // Function that opens WorkerActivity. Before doing that, it saves the workerNum in the
    // bundle so that WorkerActivity will be able to fetch it and correctly pull information
    // of the worker from the database.

    private void openWorkerActivity(int workerNum) {

        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("key",workerNum);
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
