package com.example.internshipthesis;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
 * This is the main activity of the app, here the user can see
 * all available appointments, change how they are sorted
 * (by best worker first, by first available appointment,
 * by default sorting order), access each worker profile
 * and finally access SettingsActivity.
 */

public class ScrollingActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    LinearLayout layout;
    String name;
    float rating;
    FloatingActionButton fab_main;
    ExtendedFloatingActionButton fab_worker;
    ExtendedFloatingActionButton fab_first_available;
    ExtendedFloatingActionButton fab_default_sort;
    String workerID;
    int mode = 0;
    LocalDate currentDate = now();
    int currentDay = currentDate.getDayOfWeek().getValue();
    LocalTime currentTime = LocalTime.now();
    int currentHour = currentTime.getHour();
    Boolean isFABOpen = false;
    int empty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Initialization of activity and scrolling feature

        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        layout = findViewById(R.id.thisone);

        Log.d(TAG, "Current day: " + currentDay);
        Log.d(TAG, "Current hour: " + currentHour);

        deleteOldAppointmentsStart();
        addHelper(mode);
        addAllCards();

        fab_main = findViewById(R.id.fab_main);
        fab_worker = findViewById(R.id.fab_worker);
        fab_first_available = findViewById(R.id.fab_first_available);
        fab_default_sort = findViewById(R.id.fab_default);
        fab_worker.hide();
        fab_first_available.hide();
        fab_default_sort.hide();

        // This is the FAB that opens the FAB menu that allows the user
        // to change the sorting of the cards

        fab_main.setOnClickListener(view -> {

            if(!isFABOpen){

                showFABMenu();
            } else {

                closeFABMenu();
            }
        });

        // This is the fab that changes the sorting to "best worker first"
        // If the current sorting is already on "best worker first", the FAB menu just closes

        fab_worker.setOnClickListener(view -> {

            if(mode!=2) {

                mode = 2;
                removeCards();
                addHelper(mode);
                addCardWorker();
            }
            closeFABMenu();
        });

        // This is the fab that changes the sorting to "first available appointment"
        // If the current sorting is already on "first available appointment", the FAB menu just closes

        fab_first_available.setOnClickListener(view -> {

            if(mode != 1) {

                mode = 1;
                removeCards();
                addHelper(mode);
                addCardFirstAvailableStart();
            }
            closeFABMenu();
        });

        // This is the fab that changes the sorting to "default order"
        // If the current sorting is already on "default order", the FAB menu just closes

        fab_default_sort.setOnClickListener(view -> {

            if(mode != 0) {

                mode = 0;
                removeCards();
                addHelper(mode);
                addAllCards();
            }
            closeFABMenu();
        });
    }

    // Before any card is created, the app checks the current day and hour so if there are currently
    // booked appointment that have expired, they will be made available again.
    // This function just checks if the condition is met, if there is the need to update
    // something deleteOldAppointments() will be called.

    private void deleteOldAppointmentsStart() {

        db.collection("workers")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            workerID = document.getId();
                            deleteOldAppointments(workerID);
                        }
                    } else {

                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
        );
    }

    // This function actually accesses and modifies the database, changing expired
    // appointments to make them available again

    private void deleteOldAppointments(String workerID) {

        db.collection("workers")
                .document(workerID)
                .collection("schedule")
                .get()
                .addOnCompleteListener(task1 -> {

                    if(task1.isSuccessful()) {

                        for (QueryDocumentSnapshot document1 : task1.getResult()) {

                            String documentID = document1.getId();
                            int day = Integer.parseInt(
                                    Objects.requireNonNull(
                                            document1.getString("day")
                                    )
                            );
                            int hour = Integer.parseInt(
                                    Objects.requireNonNull(
                                            document1.getString("hour")
                                    )
                            );

                            if(day < currentDay) {

                                db.collection("workers")
                                        .document(workerID)
                                        .collection("schedule")
                                        .document(documentID)
                                        .update("booked", "false")
                                ;

                                db.collection("workers")
                                        .document(workerID)
                                        .collection("schedule")
                                        .document(documentID)
                                        .update("bookedby", "")
                                ;
                            }

                            if(day == currentDay) {

                                if(hour <= currentHour) {

                                    db.collection("workers")
                                            .document(workerID)
                                            .collection("schedule")
                                            .document(documentID)
                                            .update("booked", "false")
                                    ;

                                    db.collection("workers")
                                            .document(workerID)
                                            .collection("schedule")
                                            .document(documentID)
                                            .update("bookedby", "")
                                    ;
                                }
                            }
                        }
                    }
                }
        );
    }

    // Function that removes all cards currently generated, this is done when the sorting is changed

    private void removeCards() {

        layout.removeAllViews();
    }

    // This function creates a card that helps the user better understand what he is looking at.
    // The card is different for each sorting mode.

    @SuppressLint("SetTextI18n")
    private void addHelper(int mode) {

        View helper = getLayoutInflater().inflate(R.layout.helper_layout, layout, false);
        ImageView helperImage = helper.findViewById(R.id.helperImage);
        TextView helperText = helper.findViewById(R.id.helperText);
        String user = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        if(mode == 0) {

            helperImage.setImageResource(R.drawable.ic_baseline_waving_hand_24);
            db.collection("users")
                    .document(Objects.requireNonNull(user))
                    .get().addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            String username = task.getResult().getString("name");
                            helperText.setText(String.format("Welcome %s!\n" +
                                    "Here's a list of all the people that are ready to help you, " +
                                    "Just click on their cards to see when they are available.\n" +
                                    "Click on the button on the top right to change the order of the cards.\n" +
                                    "Have a wonderful day!", username))
                            ;
                        }
                    }
            );
        }

        if(mode == 1) {

            helperImage.setImageResource(R.drawable.ic_baseline_timer_24);
            db.collection("users")
                    .document(Objects.requireNonNull(user))
                    .get().addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            helperText.setText("No time to lose!\n" +
                                    "Here's all our staff available today!")
                            ;
                        }
                    }
            );
        }

        if(mode == 2) {

            helperImage.setImageResource(R.drawable.ic_baseline_star_24);
            db.collection("users")
                    .document(Objects.requireNonNull(user))
                    .get().addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            String username = task.getResult().getString("name");
                            helperText.setText(String.format("You know your stuff %s!\n" +
                                    "Here's all our guys, from best to slightly-less best.\n" +
                                    "At your service!", username))
                            ;
                        }
                    }
            );
        }

        layout.addView(helper);
    }

    // This is the function that actually creates the card, fills it with information from
    // the database and adds it to the layout

    @SuppressLint("SetTextI18n")
    private void addCard(int workerNum) {

        View card = getLayoutInflater().inflate(R.layout.card_layout, layout, false);
        TextView nameWorker = card.findViewById(R.id.nameWorker);
        RatingBar RatingBar = card.findViewById(R.id.ratingBar);
        Button submitButton = card.findViewById(R.id.submitButton);
        ImageView workerImage = card.findViewById(R.id.workerImage);
        TextView first_available_slot = card.findViewById(R.id.textView3);
        String workerImgSrc = "worker"+ workerNum; //  this is image file name
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        int imgId = getResources().getIdentifier(PACKAGE_NAME+":drawable/"+workerImgSrc , null, null);

        DocumentReference docRef = db.collection("workers").document(String.valueOf(workerNum));
        docRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    // All the informations about the worker are filled out

                    name = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getName();
                    nameWorker.setText(name);
                    rating = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getRating();
                    RatingBar.setRating(rating);
                    workerImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),imgId));

                    // Checks if the worker is available and changes the Textview accordingly.
                    // If there are no available appointments for that worker, the TextView will be
                    // left unchanged, which means that it till display "Currently not available"

                    db.collection("workers")
                            .document(String.valueOf(workerNum))
                            .collection("schedule")
                            .whereEqualTo("booked", "false")
                            .get()
                            .addOnCompleteListener(task1 -> {

                                if (task1.isSuccessful()) {

                                    for (QueryDocumentSnapshot document1 : task1.getResult()) {

                                        int day = Integer.parseInt(
                                                Objects.requireNonNull(
                                                        document1.getString("day")
                                                )
                                        );
                                        int hour = Integer.parseInt(
                                                Objects.requireNonNull(
                                                        document1.getString("hour")
                                                )
                                        );
                                        if(day > currentDay) {

                                            first_available_slot.setText(
                                                    "Currently available. Click to see when")
                                            ;
                                        }

                                        if(day == currentDay) {

                                            if(hour > currentHour) {

                                                first_available_slot.setText(
                                                        "Currently available. Click to see when")
                                                ;
                                            }
                                        }
                                    }
                                } else {

                                    Log.d(TAG, "Error getting documents: ", task.getException());
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

        // This button allows the user to change the rating of the worker and correctly save the
        // changes to the database

        submitButton.setOnClickListener(v -> {

            DocumentReference changerating = db.collection("workers").document(String.valueOf(workerNum));
            changerating.update("rating", RatingBar.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            String rating1 = "On a scale of 5 Stars\n New rating = " + RatingBar.getRating();
            Toast.makeText(getApplicationContext(),
                    rating1, Toast.LENGTH_LONG)
                    .show()
            ;
        });

        card.setOnClickListener(view -> openWorkerActivity(workerNum));
        layout.addView(card);
    }

    // This function gets called when the user decides to sort the cards by
    // "best worker first".
    // This function will feed the workerIDs to addCard in the desired order

    private void addCardWorker() {

        db.collection("workers")
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            workerID = document.getId();
                            addCard(Integer.parseInt(workerID));
                        }
                    } else {

                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
        );
    }

    // This function gets called when the app generates the list of cards when starting,
    // or when the user selects "default order".
    // This function will feed the workerIDs to addCard in the desired order

    private void addAllCards() {

        db.collection("workers")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            workerID = document.getId();
                            addCard(Integer.parseInt(workerID));
                        }
                    } else {

                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
        );
    }

    // This function gets called when the user decides to sort the cards by
    // "first available appointment".
    // The function checks the current day, hour and workerID and gives it as
    // input to addCardFirstAvailable().

    private void addCardFirstAvailableStart() {

        if(currentDay == 7) {

            currentDay = 1;
        }

        db.collection("schedules")
                .document(String.valueOf(currentDay))
                .collection("appointments")
                .orderBy("hour", Query.Direction.ASCENDING)
                .whereGreaterThan("hour", currentHour)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            empty++;
                            workerID = document.getString("workerID");
                            String day = String.valueOf(currentDay);
                            Object hour = document.get("hour");
                            addCardFirstAvailable(workerID, day, hour);
                        }

                        if(empty == 0) {

                            addEmptyListHelper();
                        }
                    } else {

                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
        );
    }

    // This function gets called when the user decides to sort the cards by
    // "first available appointment".
    // The function looks at "Schedules" collection in the database and
    // feeds the correct workerIDs to addCard() based on the current day and hour available
    // appointment. If there are no appointments still available
    // (in the schedule, doesn't check if the appointment is booked as of now)
    // for the rest of the day, the list will be empty.
    // This function will feed the workerIDs to addCard in the desired order

    private void addCardFirstAvailable(String workerID, String day, Object hour) {

        db.collection("workers")
                .document(workerID)
                .collection("schedule")
                .get()
                .addOnCompleteListener(task1 -> {

                    if (task1.isSuccessful()) {

                        for (QueryDocumentSnapshot document1 : task1.getResult()) {

                            String booked = document1.getString("booked");
                            String documentday = document1.getString("day");
                            String documenthour = document1.getString("hour");

                            if(Objects.requireNonNull(documentday).equals(day)
                                    && Objects.requireNonNull(documenthour).equals(String.valueOf(hour))) {

                                if(Objects.requireNonNull(booked).equals("false")) {

                                    addCard(Integer.parseInt(Objects.requireNonNull(workerID)));
                                }
                            }
                        }
                    }
                }
        );
    }

    // If the user has no appointments booked, the screen would be empty.
    // If called, this function will create a helper card telling the user that
    // there are no appointments booked.

    @SuppressLint("SetTextI18n")
    private void addEmptyListHelper() {

        View emptyHelper = getLayoutInflater().inflate(R.layout.empty_list_helper_layout, layout, false);
        ImageView emptyHelperImage = emptyHelper.findViewById(R.id.emptyHelperImage);
        TextView emptyHelperText = emptyHelper.findViewById(R.id.emptyHelperText);

        emptyHelperImage.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24);
        emptyHelperText.setText("That's it for the day, sorry!\n" +
                "Come back tomorrow to see new appointments available!")
        ;

        layout.addView(emptyHelper);
    }

    // Opens the FAB menu with animations

    private void showFABMenu(){

        isFABOpen=true;
        fab_worker.show();
        fab_first_available.show();
        fab_default_sort.show();
        fab_worker.animate().translationY(getResources().getDimension(R.dimen.standard_55));
        fab_first_available.animate().translationY(getResources().getDimension(R.dimen.standard_105));
        fab_default_sort.animate().translationY(getResources().getDimension(R.dimen.standard_155));
    }

    // Closes the FAB menu with animations

    private void closeFABMenu(){

        isFABOpen=false;
        fab_worker.animate().translationY(0);
        fab_first_available.animate().translationY(0);
        fab_default_sort.animate().translationY(0);
        fab_worker.hide();
        fab_first_available.hide();
        fab_default_sort.hide();
    }

    // Create the menu where the user can decide to either go to SettingsActivity or Log-out

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    // This specifies what each element in the menu will do

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            openSettingsActivity();
        }

        if(id == R.id.action_logout) {

            openLoginActivity();
            fAuth.signOut();
            Toast.makeText(ScrollingActivity.this,
                    "Log-out Successful",
                    Toast.LENGTH_SHORT).show()
            ;
        }
        return super.onOptionsItemSelected(item);
    }

    // This is the function that opens the Login Activity

    private void openLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    // This is the function that opens the Settings Activity

    private void openSettingsActivity() {

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // This is the function that opens the Worker Activity, it also saves
    // the workerID on a bundle so that WorkerActivity can retrieve it and
    // pull correctly information from the database

    private void openWorkerActivity(int workerNum) {

        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("key",workerNum);
        startActivity(intent);
    }
}