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
                });

    }

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
                });
    }

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

                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
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
                                        Log.d(TAG, "BOOKING Current document: " + documentID);
                                        slot = (Objects.requireNonNull(document1.toObject(Classes.Worker.class))).getSlot();
                                        your_appointment.setText(slot);
                                    }
                                } else {

                                    Log.d(TAG, "Error getting documents: ", task1.getException());
                                }
                            });

                } else {

                    Log.d(TAG, "No such document");
                }
            } else {

                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        // perform click event on button
        cancelBookingButton.setOnClickListener(v -> {

            //TODO Bug: if the user books multiple appointments from the same worker,
            // all the time slots will have the same value and the button that cancels the appointment
            // will only cancel one card, leaving the others with a wrong booking information
            // and unable to be cancelled
            Log.d(TAG, "BUTTON This button belongs to: " + name);
            Log.d(TAG, "BUTTON Current document: " + documentID);
            AlertDialog dialog = new AlertDialog.Builder(BookingActivity.this)
                    .setTitle("You are about to cancel this appointment. Are you sure?")
                    .setPositiveButton("Confirm", (dialog12, whichButton) -> {

                        db.collection("workers")
                                .document(String.valueOf(workerNum))
                                .collection("schedule")
                                .document(documentID)
                                .update("booked", false);

                        db.collection("workers")
                                .document(String.valueOf(workerNum))
                                .collection("schedule")
                                .document(documentID)
                                .update("bookedby", "");

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

    private void openWorkerActivity(int workerNum) {

        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("key",workerNum);
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
