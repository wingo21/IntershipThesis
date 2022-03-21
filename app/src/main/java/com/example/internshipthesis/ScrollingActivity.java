package com.example.internshipthesis;

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

import java.util.Date;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ScrollingActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    LinearLayout layout;
    String name;
    float rating;
    Date slot;
    FloatingActionButton fab_main;
    ExtendedFloatingActionButton fab_worker;
    ExtendedFloatingActionButton fab_first_available;
    String workerID;

    Boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        addAllCards();

        fab_main = findViewById(R.id.fab_main);
        fab_worker = findViewById(R.id.fab_worker);
        fab_first_available = findViewById(R.id.fab_first_available);
        fab_worker.hide();
        fab_first_available.hide();

        fab_main.setOnClickListener(view -> {
            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        });

        fab_worker.setOnClickListener(view -> {
            removeCards();
            addCardWorker();
            closeFABMenu();
        });

        fab_first_available.setOnClickListener(view -> {
            removeCards();
            addCardFirstAvailable();
            closeFABMenu();
        });
    }

    private void removeCards() {
        layout = findViewById(R.id.thisone);
        layout.removeAllViews();
    }

    private void addCard(int workerNum) {
        layout = findViewById(R.id.thisone);
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
        DocumentReference docRefSlot = db.collection("schedules").document();
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
                    /*docRefSlot.get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            DocumentSnapshot document2 = task2.getResult();
                            if (document.exists()) {
                                slot = (Objects.requireNonNull(document2.toObject(Classes.Schedules.class))).getSlot();
                                first_available_slot.setText(slot.toString());
                            }else {
                                Log.d(TAG, "No such document");
                            }
                        }else {
                            Log.d(TAG, "get failed with ", task2.getException());
                        }

                    });*/


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

        db.collection("schedules")
                .orderBy("slot", Query.Direction.ASCENDING)
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
        fab_worker.animate().translationY(getResources().getDimension(R.dimen.standard_55));
        fab_first_available.animate().translationY(getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab_worker.animate().translationY(0);
        fab_first_available.animate().translationY(0);
        fab_worker.hide();
        fab_first_available.hide();
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
}