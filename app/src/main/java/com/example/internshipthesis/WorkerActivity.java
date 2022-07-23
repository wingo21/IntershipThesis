package com.example.internshipthesis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static java.time.LocalDate.now;

/**
 * This is the activity that gets opened when the user clicks on the worker card on
 * ScrollingActivity or on the appointment card on BookingActivity.
 * It shows more in depth information about the worker and all available appointments
 * for the rest of the week.
 **/

public class WorkerActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String user = fAuth.getUid();
    int workerNum;
    String name;
    float rating;
    String slot;
    LinearLayout layout;
    LocalDate currentDate = now();
    int currentDay = currentDate.getDayOfWeek().getValue();
    LocalTime currentTime = LocalTime.now();
    int currentHour = currentTime.getHour();

    // TODO: Add more info to the worker profile
    //  (maybe a little description, professional info, ecc)

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization of the activity layout, toolbar is custom to implement scrolling capability

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // WorkerActivity gets opened as a result of a click on the worker card on ScrollingActivity
        // or the appointment on BookingActivity, this means that the workerNum needs to be
        // shared from one activity to another to correctly pull information from the database

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            workerNum = extras.getInt("key");
            //The key argument here must match that used in the other activity
        }

        // Initialization of fields that will be filled from database

        TextView nameWorker = findViewById(R.id.nameWorker);
        RatingBar RatingBar = findViewById(R.id.ratingBar);
        Button submitButton = findViewById(R.id.submitButton);
        ImageView workerImage = findViewById(R.id.workerImage);
        layout = findViewById(R.id.workerone);
        String workerImgSrc = "worker" + workerNum; //  this is image file name
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        int imgId = getResources().getIdentifier(PACKAGE_NAME+":drawable/"+workerImgSrc , null, null);

        // Looking at the database to extract the worker information

        DocumentReference docRef = db.collection("workers").document(String.valueOf(workerNum));
        docRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

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

        // Setting up the button that will be able to change the worker rating
        // and update it on the database accordingly

        submitButton.setOnClickListener(v -> {

            DocumentReference changerating = db.collection("workers").document(String.valueOf(workerNum));
            changerating.update("rating", RatingBar.getRating()).addOnSuccessListener(aVoid ->

                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e))
            ;

            // get values and then displayed in a toast
            String rating1 = "On a scale of 5 Stars\n New rating = " + RatingBar.getRating();
            Toast.makeText(getApplicationContext(), rating1, Toast.LENGTH_LONG).show();
        });

        addListHelper();
        getInfoForAppointments(workerNum);
    }

    // This function adds a little helper at the start of the list of appointments
    // in the worker profile, so that it feels more clean and cohesive

    @SuppressLint("SetTextI18n")
    private void addListHelper() {

        View emptyHelper = getLayoutInflater().inflate(R.layout.empty_list_helper_layout, layout, false);
        ImageView emptyHelperImage = emptyHelper.findViewById(R.id.emptyHelperImage);
        TextView emptyHelperText = emptyHelper.findViewById(R.id.emptyHelperText);

        emptyHelperImage.setImageResource(R.drawable.ic_baseline_waving_hand_24);
        emptyHelperText.setText("Hey there friend!\n" +
                "Here are all available appointments for this great guy of ours!\n" +
                "If you don't see any, that means that he is all booked up for this week," +
                " sorry for the inconvenience!")
        ;

        layout.addView(emptyHelper);
    }

    // This function interrogates the database to access the correct collection
    // to pull the data from, while doing this it also checks to not create a card
    // for appointments that are either from earlier days in the week or earlier hours
    // in the days (if it's Wednesday 10 AM, I shouldn't be able to book an appointment
    // for Monday at any hour or for Wednesday at 9 AM for example)

    private void getInfoForAppointments(int workerNum) {

        db.collection("workers")
                .document(String.valueOf(workerNum))
                .collection("schedule")
                .whereEqualTo("booked", "false")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String documentID = document.getId();
                            slot = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getSlot();
                            String nowDay = document.getString("day");
                            String nowHour = document.getString("hour");

                            if(Integer.parseInt(Objects.requireNonNull(nowDay)) == currentDay) {

                                if(Integer.parseInt(Objects.requireNonNull(nowHour)) > currentHour) {

                                    addAppointment(slot, documentID);
                                }
                            }

                            if(Integer.parseInt(Objects.requireNonNull(nowDay)) > currentDay) {

                                addAppointment(slot, documentID);
                            }

                        }
                    } else {

                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // Now that getInfoForAppointments() has correctly selected which appointments to create,
    // addAppointment can interrogate the database to pull the correct data and then create the
    // appointment card to add to the screen

    private void addAppointment(String slot, String documentID) {

        View appointment = getLayoutInflater().inflate(R.layout.appointment_layout, layout, false);
        TextView appointment_slot_textview = appointment.findViewById(R.id.appointment_slot);
        Button bookButton = appointment.findViewById(R.id.bookButton);
        appointment_slot_textview.setText(slot);

        // This is the button that allows the user to book the appointment
        // if the button is pressed, a dialog will pop up, asking the user to confirm his
        // choice.
        // If the user clicks "Cancel", the dialog closes and nothing happens.
        // If the user clicks "Confirm", another dialog pops up confirming that the
        // the appointment has been successfully booked and asks the user if he wants
        // to travel to the car dealership right away.
        // If the user clicks "Yes", navigation towards the car dealership will start
        // right away (on phone if Head Unit is not connected).
        // If the user clicks "No" the dialog will close.
        // After the dialog closes, WorkerActivity gets closed.

        bookButton.setOnClickListener(v -> {

            AlertDialog dialog = new AlertDialog.Builder(WorkerActivity.this)
                    .setTitle("You are about to book this appointment. Are you sure?")
                    .setPositiveButton("Confirm", (dialog12, whichButton) ->
                            db.collection("workers")
                            .document(String.valueOf(workerNum))
                            .collection("schedule")
                            .get()
                            .addOnCompleteListener(task -> {

                                if(task.isSuccessful()) {

                                    boolean found = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        String bookedby = document.getString("bookedby");
                                        if(Objects.requireNonNull(bookedby).equals(user)) {

                                            found = true;
                                            AlertDialog dialog2 = new AlertDialog.Builder(WorkerActivity.this)
                                                    .setTitle("Cannot book appointment")
                                                    .setMessage("You have already a booked appointment with this operator," +
                                                            " please cancel to book a different appointment")
                                                    .setNeutralButton("ok", null).create();
                                            dialog2.show();
                                        }
                                    }

                                    if(!found) {

                                        db.collection("workers")
                                                .document(String.valueOf(workerNum))
                                                .collection("schedule")
                                                .document(documentID)
                                                .update("booked", "true")
                                        ;

                                        db.collection("workers")
                                                .document(String.valueOf(workerNum))
                                                .collection("schedule")
                                                .document(documentID)
                                                .update("bookedby", user)
                                        ;

                                        AlertDialog dialog1 = new AlertDialog.Builder(WorkerActivity.this)
                                                .setTitle("You successfully booked your appointment!")
                                                .setMessage("Do you want to navigate to the car dealership right now?")
                                                .setPositiveButton("Yes", (dialog2, whichButton1) -> {

                                                    openGoogleMaps();
                                                    finish();
                                                })
                                                .setNegativeButton("No", (dialog2, whichButton1) -> finish())
                                                .create();
                                        dialog1.show();
                                    }
                                }
                            }
                    )).setNegativeButton("Cancel", null).create();
            dialog.show();
        });

        layout.addView(appointment);
    }

    // This is the function that allows the app to open google maps and start
    // navigation towards the car dealership if the user chooses to.

    private void openGoogleMaps() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=711 11th Ave, New York, NY 10019"));
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