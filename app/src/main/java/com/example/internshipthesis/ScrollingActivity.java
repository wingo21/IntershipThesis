package com.example.internshipthesis;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class ScrollingActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String name1;
    String name2;
    String name3;
    String name4;
    String name5;
    String name6;

    float rating1;
    float rating2;
    float rating3;
    float rating4;
    float rating5;
    float rating6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        TextView nameWorker1 = findViewById(R.id.nameWorker1);
        TextView nameWorker2 = findViewById(R.id.nameWorker2);
        TextView nameWorker3 = findViewById(R.id.nameWorker3);
        TextView nameWorker4 = findViewById(R.id.nameWorker4);
        TextView nameWorker5 = findViewById(R.id.nameWorker5);
        TextView nameWorker6 = findViewById(R.id.nameWorker6);

        RatingBar RatingBar1 = findViewById(R.id.ratingBar1);
        RatingBar RatingBar2 = findViewById(R.id.ratingBar2);
        RatingBar RatingBar3 = findViewById(R.id.ratingBar3);
        RatingBar RatingBar4 = findViewById(R.id.ratingBar4);
        RatingBar RatingBar5 = findViewById(R.id.ratingBar5);
        RatingBar RatingBar6 = findViewById(R.id.ratingBar6);

        Button submitButton1 = findViewById(R.id.submitButton1);
        Button submitButton2 = findViewById(R.id.submitButton2);
        Button submitButton3 = findViewById(R.id.submitButton3);
        Button submitButton4 = findViewById(R.id.submitButton4);
        Button submitButton5 = findViewById(R.id.submitButton5);
        Button submitButton6 = findViewById(R.id.submitButton6);

        DocumentReference docRef1 = db.collection("workers").document("worker1");
        docRef1.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name1 = document.toObject(Classes.Worker.class).getName();
                    nameWorker1.setText(name1);
                    rating1 = document.toObject(Classes.Worker.class).getRating();
                    RatingBar1.setRating(rating1);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        DocumentReference docRef2 = db.collection("workers").document("worker2");
        docRef2.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name2 = document.toObject(Classes.Worker.class).getName();
                    nameWorker2.setText(name2);
                    rating2 = document.toObject(Classes.Worker.class).getRating();
                    RatingBar2.setRating(rating2);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        DocumentReference docRef3 = db.collection("workers").document("worker3");
        docRef3.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name3 = document.toObject(Classes.Worker.class).getName();
                    nameWorker3.setText(name3);
                    rating3 = document.toObject(Classes.Worker.class).getRating();
                    RatingBar3.setRating(rating3);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        DocumentReference docRef4 = db.collection("workers").document("worker4");
        docRef4.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name4 = document.toObject(Classes.Worker.class).getName();
                    nameWorker4.setText(name4);
                    rating4 = document.toObject(Classes.Worker.class).getRating();
                    RatingBar4.setRating(rating4);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        DocumentReference docRef5 = db.collection("workers").document("worker5");
        docRef5.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name5 = document.toObject(Classes.Worker.class).getName();
                    nameWorker5.setText(name5);
                    rating5 = document.toObject(Classes.Worker.class).getRating();
                    RatingBar5.setRating(rating5);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        DocumentReference docRef6 = db.collection("workers").document("worker6");
        docRef6.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    name6 = document.toObject(Classes.Worker.class).getName();
                    nameWorker6.setText(name6);
                    rating6 = document.toObject(Classes.Worker.class).getRating();
                    RatingBar6.setRating(rating6);
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        // perform click event on button
        submitButton1.setOnClickListener(v -> {
            DocumentReference changerating1 = db.collection("workers").document("worker1");
            changerating1.update("rating", RatingBar1.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            // get values and then displayed in a toast
            String rating1 = "On a scale of 5 Stars\n New rating = " + RatingBar1.getRating();
            Toast.makeText(getApplicationContext(), rating1, Toast.LENGTH_LONG).show();
        });

        // perform click event on button
        submitButton2.setOnClickListener(v -> {
            DocumentReference changerating2 = db.collection("workers").document("worker2");
            changerating2.update("rating", RatingBar2.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            // get values and then displayed in a toast
            String rating2 = "On a scale of 5 Stars\n New rating = " + RatingBar2.getRating();
            Toast.makeText(getApplicationContext(), rating2, Toast.LENGTH_LONG).show();
        });

        // perform click event on button
        submitButton3.setOnClickListener(v -> {
            DocumentReference changerating3 = db.collection("workers").document("worker3");
            changerating3.update("rating", RatingBar3.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            // get values and then displayed in a toast
            String rating3 = "On a scale of 5 Stars\n New rating = " + RatingBar3.getRating();
            Toast.makeText(getApplicationContext(), rating3, Toast.LENGTH_LONG).show();
        });

        // perform click event on button
        submitButton4.setOnClickListener(v -> {
            DocumentReference changerating4 = db.collection("workers").document("worker4");
            changerating4.update("rating", RatingBar4.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            // get values and then displayed in a toast
            String rating4 = "On a scale of 5 Stars\n New rating = " + RatingBar4.getRating();
            Toast.makeText(getApplicationContext(), rating4, Toast.LENGTH_LONG).show();
        });

        // perform click event on button
        submitButton5.setOnClickListener(v -> {
            DocumentReference changerating5 = db.collection("workers").document("worker5");
            changerating5.update("rating", RatingBar5.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            // get values and then displayed in a toast
            String rating5 = "On a scale of 5 Stars\n New rating = " + RatingBar5.getRating();
            Toast.makeText(getApplicationContext(), rating5, Toast.LENGTH_LONG).show();
        });

        // perform click event on button
        submitButton6.setOnClickListener(v -> {
            DocumentReference changerating6 = db.collection("workers").document("worker6");
            changerating6.update("rating", RatingBar6.getRating()).addOnSuccessListener(aVoid ->
                    Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            // get values and then displayed in a toast
            String rating6 = "On a scale of 5 Stars\n New rating = " + RatingBar6.getRating();
            Toast.makeText(getApplicationContext(), rating6, Toast.LENGTH_LONG).show();
        });
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