package com.example.internshipthesis;

import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        RatingBar simpleRatingBar1 = findViewById(R.id.ratingBar1); // initiate a rating bar
        Float ratingNumber1 = simpleRatingBar1.getRating(); // get rating number from a rating bar
        simpleRatingBar1.setRating((float) 0.5); // set default rating

        Button submitButton1 = findViewById(R.id.submitButton1);
        // perform click event on button
        submitButton1.setOnClickListener(v -> {
            // get values and then displayed in a toast
            String totalStars1 = "Total Stars = " + simpleRatingBar1.getNumStars();
            String rating1 = "Rating = " + simpleRatingBar1.getRating();
            Toast.makeText(getApplicationContext(), totalStars1 + "\n" + rating1, Toast.LENGTH_LONG).show();
        });

        RatingBar simpleRatingBar2 = findViewById(R.id.ratingBar2); // initiate a rating bar
        Float ratingNumber2 = simpleRatingBar2.getRating(); // get rating number from a rating bar
        simpleRatingBar2.setRating((float) 1.0); // set default rating

        Button submitButton2 = findViewById(R.id.submitButton2);
        // perform click event on button
        submitButton2.setOnClickListener(v -> {
            // get values and then displayed in a toast
            String totalStars2 = "Total Stars = " + simpleRatingBar2.getNumStars();
            String rating2 = "Rating = " + simpleRatingBar2.getRating();
            Toast.makeText(getApplicationContext(), totalStars2 + "\n" + rating2, Toast.LENGTH_LONG).show();
        });

        RatingBar simpleRatingBar3 = findViewById(R.id.ratingBar3); // initiate a rating bar
        Float ratingNumber3 = simpleRatingBar3.getRating(); // get rating number from a rating bar
        simpleRatingBar3.setRating((float) 2.5); // set default rating

        Button submitButton3 = findViewById(R.id.submitButton3);
        // perform click event on button
        submitButton3.setOnClickListener(v -> {
            // get values and then displayed in a toast
            String totalStars3 = "Total Stars = " + simpleRatingBar3.getNumStars();
            String rating3 = "Rating = " + simpleRatingBar3.getRating();
            Toast.makeText(getApplicationContext(), totalStars3 + "\n" + rating3, Toast.LENGTH_LONG).show();
        });

        RatingBar simpleRatingBar4 = findViewById(R.id.ratingBar4); // initiate a rating bar
        Float ratingNumber = simpleRatingBar4.getRating(); // get rating number from a rating bar
        simpleRatingBar4.setRating((float) 1.5); // set default rating

        Button submitButton4 = findViewById(R.id.submitButton4);
        // perform click event on button
        submitButton4.setOnClickListener(v -> {
            // get values and then displayed in a toast
            String totalStars4 = "Total Stars = " + simpleRatingBar4.getNumStars();
            String rating4 = "Rating = " + simpleRatingBar4.getRating();
            Toast.makeText(getApplicationContext(), totalStars4 + "\n" + rating4, Toast.LENGTH_LONG).show();
        });

        RatingBar simpleRatingBar5 = findViewById(R.id.ratingBar5); // initiate a rating bar
        Float ratingNumber5 = simpleRatingBar5.getRating(); // get rating number from a rating bar
        simpleRatingBar5.setRating((float) 2.0); // set default rating

        Button submitButton5 = findViewById(R.id.submitButton5);
        // perform click event on button
        submitButton5.setOnClickListener(v -> {
            // get values and then displayed in a toast
            String totalStars5 = "Total Stars = " + simpleRatingBar5.getNumStars();
            String rating5 = "Rating = " + simpleRatingBar5.getRating();
            Toast.makeText(getApplicationContext(), totalStars5 + "\n" + rating5, Toast.LENGTH_LONG).show();
        });

        RatingBar simpleRatingBar6 = findViewById(R.id.ratingBar6); // initiate a rating bar
        Float ratingNumber6 = simpleRatingBar6.getRating(); // get rating number from a rating bar
        simpleRatingBar6.setRating((float) 5.0); // set default rating

        Button submitButton6 = findViewById(R.id.submitButton6);
        // perform click event on button
        submitButton6.setOnClickListener(v -> {
            // get values and then displayed in a toast
            String totalStars6 = "Total Stars = " + simpleRatingBar6.getNumStars();
            String rating6 = "Rating = " + simpleRatingBar6.getRating();
            Toast.makeText(getApplicationContext(), totalStars6 + "\n" + rating6, Toast.LENGTH_LONG).show();
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}