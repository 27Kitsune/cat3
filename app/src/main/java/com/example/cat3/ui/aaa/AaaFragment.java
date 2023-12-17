package com.example.cat3.ui.aaa;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import com.example.cat3.R;

public class AaaFragment extends Fragment {
    private ArrayAdapter<String> eventAdapter;
    private DatabaseReference eventsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aaa, container, false);

        // Initialize ListView and Adapter
        ListView eventListView = root.findViewById(R.id.eventListView);
        eventAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1);
        eventListView.setAdapter(eventAdapter);

        // Retrieve event data from Firebase and add it to the adapter
        eventsRef = FirebaseDatabase.getInstance().getReference("Recruit_data");
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateEventList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });

        // Find the CardView in the layout
        androidx.cardview.widget.CardView cardView1 = root.findViewById(R.id.cardView1);
        androidx.cardview.widget.CardView cardView2 = root.findViewById(R.id.cardView2);

        // Set an OnClickListener for cardView1
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When cardView1 is clicked, show the volunteer_recruit.xml as a popup
                showVolunteerRecruitPopup(root);
            }
        });

        // Set an OnClickListener for cardView2
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When cardView2 is clicked, show the volunteer_form.xml as a popup
                showVolunteerFormPopup(root);
            }
        });

        return root;
    }

    private void updateEventList(DataSnapshot dataSnapshot) {
        eventAdapter.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            AaaData aaaData = snapshot.getValue(AaaData.class);
            if (aaaData != null && aaaData.getevent1() != null) {
                eventAdapter.add(aaaData.getevent1());
            }
        }
        eventAdapter.notifyDataSetChanged();
    }

    private void showVolunteerRecruitPopup(View root) {
        // Inflate the volunteer_recruit.xml layout
        View popupView = getLayoutInflater().inflate(R.layout.volunteer_recruit, null);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Recruit_data");

        // Find the submit1 button, name1, number1, and email1 in the popup layout
        com.google.android.material.button.MaterialButton submitButton = popupView.findViewById(R.id.submit1);
        EditText eventEditText = popupView.findViewById(R.id.event1);
        EditText nameEditText = popupView.findViewById(R.id.name1);
        EditText numberEditText = popupView.findViewById(R.id.number1);
        EditText emailEditText = popupView.findViewById(R.id.email1);

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get entered data
                String event = eventEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String number = numberEditText.getText().toString();
                String email = emailEditText.getText().toString();

                // check if all fields are filled
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Fill all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    // Save data to Firebase
                    AaaData aaaData = new AaaData(event, name, number, email, null,null, null);
                    myRef.push().setValue(aaaData);

                    // Display a toast message
                    Toast.makeText(getActivity(), "Form Submitted", Toast.LENGTH_SHORT).show();

                    // Clear the EditText fields after submitting
                    eventEditText.setText("");
                    nameEditText.setText("");
                    numberEditText.setText("");
                    emailEditText.setText("");
                }
            }
        });

        // Create an AlertDialog to display the popup
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(popupView);

        // Add any additional setup for the AlertDialog if needed

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showVolunteerFormPopup(View root) {
        // Inflate the volunteer_form.xml layout
        View popupView = getLayoutInflater().inflate(R.layout.volunteer_form, null);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Volunteer_data");

        // Find the submit1 button, name1, number1, and email1 in the popup layout
        com.google.android.material.button.MaterialButton submitButton = popupView.findViewById(R.id.submit2);
        EditText nameEditText = popupView.findViewById(R.id.name2);
        EditText numberEditText = popupView.findViewById(R.id.number2);
        EditText emailEditText = popupView.findViewById(R.id.email2);

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get entered data
                String name = nameEditText.getText().toString();
                String number = numberEditText.getText().toString();
                String email = emailEditText.getText().toString();

                // check if all fields are filled
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Fill all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    // Save data to Firebase
                    AaaData aaaData = new AaaData(null, null, null, null, name, number, email);
                    myRef.push().setValue(aaaData);

                    // Display a toast message
                    Toast.makeText(getActivity(), "Form Submitted", Toast.LENGTH_SHORT).show();

                    // Clear the EditText fields after submitting
                    nameEditText.setText("");
                    numberEditText.setText("");
                    emailEditText.setText("");
                }
            }
        });

        // Create an AlertDialog to display the popup
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(popupView);

        // Add any additional setup for the AlertDialog if needed

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
