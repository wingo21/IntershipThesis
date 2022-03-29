package com.example.internshipthesis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    String email = Objects.requireNonNull(fAuth.getCurrentUser()).getEmail();
    LinearLayout layout;
    String workerID;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());


        layout = findViewById(R.id.thisonenew);
        addAllBookings();

    }

    private void addAllBookings() {

        db.collection("workers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            workerID = document.getId();
                            addBooked(Integer.parseInt(workerID));
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
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                        your_appointment.setText("Currently available. Click to see when");
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
        /*cancelBookingButton.setOnClickListener(v -> {
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
        });*/

        booking.setOnClickListener(view -> openWorkerActivity(workerNum));

        layout.addView(booking);
    }

    private void openWorkerActivity(int workerNum) {
        Intent intent = new Intent(this, WorkerActivity.class);
        intent.putExtra("key",workerNum);
        startActivity(intent);
    }
}
