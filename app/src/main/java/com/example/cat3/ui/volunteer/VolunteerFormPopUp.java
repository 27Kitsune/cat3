package com.example.cat3.ui.volunteer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cat3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VolunteerFormPopUp {

    public static void showVolunteerFormPopup(Context context, View root, String fcmToken) {
        // Use the provided context to get the LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the volunteer_form.xml layout
        View popupView = inflater.inflate(R.layout.volunteer_form, null);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recruitRef = database.getReference("Approved_Events");
        DatabaseReference volunteerRef = database.getReference("Pending_Volunteer_Forms");

        // Find the submit2 button, name2, number2, and email2 in the popup layout
        com.google.android.material.button.MaterialButton submitButton = popupView.findViewById(R.id.submit2);
        EditText eventEditText = popupView.findViewById(R.id.event1);
        EditText nameEditText = popupView.findViewById(R.id.name2);
        EditText icEditText = popupView.findViewById(R.id.ic2);
        EditText numberEditText = popupView.findViewById(R.id.number2);
        EditText emailEditText = popupView.findViewById(R.id.email2);


        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get entered data
                String event = eventEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String number = numberEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String ic = icEditText.getText().toString();

                // Retrieve the current user's UID
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                String userUid = (currentUser != null) ? currentUser.getUid():"default";


                // check if all fields are filled
                if (TextUtils.isEmpty(event) || TextUtils.isEmpty(name) || TextUtils.isEmpty(ic) || TextUtils.isEmpty(number) || TextUtils.isEmpty(email)) {
                    showToast(context, "Fill all fields.");
                } else {
                    // Check if the entered event exists in the recruit data
                    recruitRef.orderByChild("event1").equalTo(event).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Save data to Firebase
                                VolunteerData volunteerData = new VolunteerData(event, null, null, null, null, null,null, name, number, email, ic, null, fcmToken, userUid);
                                volunteerRef.push().setValue(volunteerData);

                                // Display a toast message
                                showToast(context, "Form Submitted");

                                // Clear the EditText fields after submitting
                                eventEditText.setText("");
                                nameEditText.setText("");
                                icEditText.setText("");
                                numberEditText.setText("");
                                emailEditText.setText("");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Show the Toast after the delay
                                        showToast(context, "Thank you for your application. Our team will evaluate your submission.");
                                    }
                                }, 5000); // 5000 milliseconds (5 seconds) delay
                            } else {
                                // Display a toast message if the event doesn't exist
                                showToast(context, "Event does not exist.");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle the error
                        }
                    });
                }
            }
        });

        // Create an AlertDialog to display the popup
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(popupView);

        // Add any additional setup for the AlertDialog if needed

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}

