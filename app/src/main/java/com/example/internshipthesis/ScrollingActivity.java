package com.example.internshipthesis;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        layout = findViewById(R.id.thisone);

        Log.d(TAG, "Current day: " + currentDay);
        Log.d(TAG, "Current hour: " + currentHour);

        deleteOldAppointments();
        addAllCards();

        fab_main = findViewById(R.id.fab_main);
        fab_worker = findViewById(R.id.fab_worker);
        fab_first_available = findViewById(R.id.fab_first_available);
        fab_default_sort = findViewById(R.id.fab_default);
        fab_worker.hide();
        fab_first_available.hide();
        fab_default_sort.hide();

        fab_main.setOnClickListener(view -> {
            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        });

        fab_worker.setOnClickListener(view -> {
            if(mode!=2) {
                removeCards();
                addCardWorker();
                mode = 2;
            }
            closeFABMenu();
        });

        fab_first_available.setOnClickListener(view -> {
            if(mode != 1) {
                removeCards();
                addCardFirstAvailable();
                mode = 1;
            }
            closeFABMenu();
        });

        fab_default_sort.setOnClickListener(view -> {
            if(mode != 0) {
                removeCards();
                addAllCards();
                mode = 0;
            }
            closeFABMenu();
        });
    }

    private void deleteOldAppointments() {
        //TODO This doesn't work
        db.collection("workers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            workerID = document.getId();
                            Log.d(TAG, "DELETE current worker document: " + workerID);
                            db.collection("workers")
                                    .document(workerID)
                                    .collection("schedule")
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                String documentID = document1.getId();
                                                Log.d(TAG, "DELETE current schedule day: " + documentID);
                                                int day = Integer.parseInt(Objects.requireNonNull(document1.getString("day")));
                                                int hour = Integer.parseInt(Objects.requireNonNull(document1.getString("hour")));
                                                Log.d(TAG, "DELETE current day: " + day);
                                                Log.d(TAG, "DELETE current hour: " + hour);
                                                if(day < currentDay){
                                                    Log.d(TAG, "DELETE day " + day
                                                            + " is lower than currentDay " + currentDay
                                                            + " so this should be updated");
                                                    Log.d(TAG, "current document that i'm about to change" + documentID);
                                                    db.collection("workers")
                                                            .document(String.valueOf(workerID))
                                                            .collection("schedule")
                                                            .document(documentID)
                                                            .update("booked", false);

                                                    db.collection("workers")
                                                            .document(String.valueOf(workerID))
                                                            .collection("schedule")
                                                            .document(documentID)
                                                            .update("bookedby", "");
                                                }
                                                if(day == currentDay){
                                                    if(hour <= currentHour){
                                                        Log.d(TAG, "DELETE day " + day
                                                                + " is equal to currentDay " + currentDay
                                                                + " but hour " + hour
                                                                + " is lower or equal currentHour " + currentHour
                                                                + " so this should be updated ");
                                                        db.collection("workers")
                                                                .document(String.valueOf(workerID))
                                                                .collection("schedule")
                                                                .document(documentID)
                                                                .update("booked", false);

                                                        db.collection("workers")
                                                                .document(String.valueOf(workerID))
                                                                .collection("schedule")
                                                                .document(documentID)
                                                                .update("bookedby", "");
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void removeCards() {
        layout.removeAllViews();
    }

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
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getName();
                    nameWorker.setText(name);
                    rating = (Objects.requireNonNull(document.toObject(Classes.Worker.class))).getRating();
                    RatingBar.setRating(rating);
                    workerImage.setImageBitmap(BitmapFactory.decodeResource(getResources(),imgId));

                    db.collection("workers")
                            .document(String.valueOf(workerNum))
                            .collection("schedule")
                            .whereEqualTo("booked", false)
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                        int day = Integer.parseInt(Objects.requireNonNull(document1.getString("day")));
                                        int hour = Integer.parseInt(Objects.requireNonNull(document1.getString("hour")));
                                        if(day > currentDay){
                                            first_available_slot.setText("Currently available. Click to see when");
                                        }
                                        if(day == currentDay){
                                            if(hour > currentHour){
                                                first_available_slot.setText("Currently available. Click to see when");
                                            }
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
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
        submitButton.setOnClickListener(v -> {
            DocumentReference changerating = db.collection("workers").document(String.valueOf(workerNum));
            changerating.update("rating", RatingBar.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            // get values and then displayed in a toast
            String rating1 = "On a scale of 5 Stars\n New rating = " + RatingBar.getRating();
            Toast.makeText(getApplicationContext(), rating1, Toast.LENGTH_LONG).show();
        });

        card.setOnClickListener(view -> openWorkerActivity(workerNum));

        layout.addView(card);
    }

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
                });

    }

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
                });

    }

    private void addCardFirstAvailable(){

        //TODO: Right now if you book an appointment, the worker still shows up on the list of available workers for the day
        // Even though he shouldn't be no longer available that day
        // Maybe add a button that let's the user choose which day he wants to see

        if(currentDay == 7){
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
                            workerID = document.getString("workerID");
                            addCard(Integer.parseInt(Objects.requireNonNull(workerID)));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }

    private void showFABMenu(){
        isFABOpen=true;
        fab_worker.show();
        fab_first_available.show();
        fab_default_sort.show();
        fab_worker.animate().translationY(getResources().getDimension(R.dimen.standard_55));
        fab_first_available.animate().translationY(getResources().getDimension(R.dimen.standard_105));
        fab_default_sort.animate().translationY(getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab_worker.animate().translationY(0);
        fab_first_available.animate().translationY(0);
        fab_default_sort.animate().translationY(0);
        fab_worker.hide();
        fab_first_available.hide();
        fab_default_sort.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettingsActivity();
        }

        if(id == R.id.action_logout) {
            openLoginActivity();
            fAuth.signOut();
            Toast.makeText(ScrollingActivity.this, "Log-out Successful", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openLoginActivity() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openWorkerActivity(int workerNum) {
        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("key",workerNum);
        startActivity(intent);
    }
}