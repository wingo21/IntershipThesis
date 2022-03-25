package com.example.internshipthesis;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Dialog extends AppCompatDialogFragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String user = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
    int workerNum;

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Bundle extras = requireActivity().getIntent().getExtras();
        if (extras != null) {
            workerNum = extras.getInt("key");
            //The key argument here must match that used in the other activity
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Book Appointment")
                .setMessage("You are about to book this appointment, confirm?")
                .setPositiveButton("Yes", (dialogInterface, i) -> updateFirestoreYes())
                .setNegativeButton("No", ((dialogInterface, i) -> {

                }));
        return builder.create();
    }

    private void updateFirestoreYes() {
        //TODO:
        // The dialog right now does nothing.
        // The problem is that to update the document we need to know the document id,
        // but as of now schedules documents have automatically generated it
    }

}
