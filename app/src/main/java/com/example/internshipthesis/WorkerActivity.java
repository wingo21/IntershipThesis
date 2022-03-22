package com.example.internshipthesis;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

import java.util.Date;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class WorkerActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int workerNum;
    String name;
    float rating;
    Date slot;

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
        TextView first_available_slot = findViewById(R.id.textView3);
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
                    db.collection("schedules")
                            /*.whereEqualTo("workerId", String.valueOf(workerNum))*/
                            .orderBy("slot", Query.Direction.ASCENDING)
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                        slot = (Objects.requireNonNull(document1.toObject(Classes.Schedules.class))).getSlot();
                                        /*first_available_slot.setText(slot.toString());*/
                                        LinearLayout linearLayout = new LinearLayout(this);
                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                        TextView textView = new TextView(this);
                                        textView.setText(slot.toString());
                                        linearLayout.addView(textView);
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
    }
}