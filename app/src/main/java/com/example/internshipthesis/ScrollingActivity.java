package com.example.internshipthesis;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class ScrollingActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    LinearLayout layout;
    String name;
    float rating;
    FloatingActionButton fab_main;
    ExtendedFloatingActionButton fab_worker;
    ExtendedFloatingActionButton fab_first_available;

    Boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        for (int i = 1; i<7; i++){
            addCard(i);
        }

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
    }

    private void addCard(int workerNum) {
        layout = findViewById(R.id.thisone);
        View card = getLayoutInflater().inflate(R.layout.card_layout, layout, false);

        TextView nameWorker = card.findViewById(R.id.nameWorker);
        RatingBar RatingBar = card.findViewById(R.id.ratingBar);
        Button submitButton = card.findViewById(R.id.submitButton);
        ImageView workerImage = card.findViewById(R.id.workerImage);
        String workerImgSrc = "worker"+ workerNum; //  this is image file name
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        int imgId = getResources().getIdentifier(PACKAGE_NAME+":drawable/"+workerImgSrc , null, null);
        System.out.println("IMG ID :: "+imgId);
        System.out.println("PACKAGE_NAME :: "+PACKAGE_NAME);

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
                    //    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),imgId);
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

        layout.addView(card);
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