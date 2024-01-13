package com.example.cat3.ui.volunteer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.Activity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.widget.ListView;

import com.example.cat3.R;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VolunteerFragment extends Fragment {
    private UserEventAdapter eventAdapter;
    private DatabaseReference eventsRef;
    private Uri selectedImageUri;
    private String imageUrl;
    private TextView selectedImageUriTextView;
    private ProgressBar progressBar;
    private Context context;
    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle image selection
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();

                        // Extract the file name from the URI
                        String fileName = getFileName(selectedImageUri);

                        // Display the file name under the proof button
                        if (selectedImageUriTextView != null) {
                            selectedImageUriTextView.setText(fileName);
                        }
                    }
                }
            });

    private void uploadImageToFirebaseStorage(Uri imageUri, ProgressBar progressBar, Button submitButton,
                                              EditText eventEditText, EditText locationEditText, EditText dateEditText, EditText nameEditText,
                                              EditText numberEditText, EditText emailEditText) {
        if (imageUri != null) {
            // Extract the file name from the URI
            String fileName = getFileName(imageUri);

            // Create a storage reference with a unique name (e.g., using the timestamp)
            String storagePath = "Proof/" + System.currentTimeMillis() + "_" + fileName;
            StorageReference storageRef = FirebaseStorage.getInstance().getReference(storagePath);

            // Upload the image to Firebase Storage
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {

                        // Update the progress bar during the upload
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressBar.setProgress((int) progress);
                    })
                    .addOnSuccessListener(taskSnapshot -> {

                        // Image uploaded successfully, get the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the image URL to the global variable
                            imageUrl = uri.toString();
                            // Display the file name under the proof button
                            if (selectedImageUriTextView != null) {
                                selectedImageUriTextView.setText(fileName);
                            }

                            // Hide the progress bar after successful upload
                            progressBar.setVisibility(View.GONE);

                            // Enable the submit button
                            submitButton.setEnabled(true);

                            // Clear the TextView and force redraw:
                            selectedImageUriTextView.setText("");
                            selectedImageUriTextView.invalidate();

                            // Save data to Firebase inside the getDownloadUrl onSuccessListener
                            saveDataToFirebase(eventEditText, locationEditText, dateEditText, nameEditText, numberEditText, emailEditText);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error during image upload
                        Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();

                        // Hide the progress bar on failure
                        progressBar.setVisibility(View.GONE);

                        // Enable the submit button
                        submitButton.setEnabled(true);
                    });
        }
    }

    // Helper method to save data to Firebase
    private void saveDataToFirebase(EditText eventEditText, EditText locationEditText, EditText dateEditText,EditText nameEditText,
                                    EditText numberEditText, EditText emailEditText) {
        String event = eventEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String date = dateEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String number = numberEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String eventId = generateEventId();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Pending_Events");

        DatabaseReference newEventRef = myRef.push();  // Get a reference to the new event node
        VolunteerData volunteerData = new VolunteerData(event, location, date, name, number, email, imageUrl, null, null, null, null, eventId);
        // Use the obtained reference to set the value in the database
            newEventRef.setValue(volunteerData);

        Toast.makeText(getActivity(), "Form Submitted", Toast.LENGTH_SHORT).show();

        // Clear the EditText fields after submitting
        eventEditText.setText("");
        locationEditText.setText("");
        dateEditText.setText("");
        nameEditText.setText("");
        numberEditText.setText("");
        emailEditText.setText("");
        selectedImageUriTextView.setText("");
        selectedImageUriTextView.invalidate();
        imageUrl = null;

        // Create a delayed handler to show the Toast after a 5-second delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Show the Toast after the delay
                Toast.makeText(getActivity(), "Thank you for your application. Our team will evaluate your submission.", Toast.LENGTH_LONG).show();
            }
        }, 5000); // 5000 milliseconds (5 seconds) delay
    }

    // Custom method to generate a unique event ID as an incrementing number
    private String generateEventId() {
        // Retrieve the current value of the event counter from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int eventCounter = sharedPreferences.getInt("eventCounter", 0);

        // Increment the counter for the next event
        eventCounter++;

        // Save the updated counter back to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("eventCounter", eventCounter);
        editor.apply();

        // Return the generated event ID
        return String.valueOf(eventCounter);
    }



    // Helper method to get the file name from the URI
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_volunteer, container, false);

        // Initialize ListView and Adapter
        ListView eventListView = root.findViewById(R.id.eventListView);
        eventAdapter = new UserEventAdapter(requireContext(), new ArrayList<>(), this);
        eventListView.setAdapter(eventAdapter);

        context = requireContext();

        // Retrieve event data from Firebase and add it to the adapter to display in the list
        eventsRef = FirebaseDatabase.getInstance().getReference("Approved_Events");
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
                VolunteerFormPopUp.showVolunteerFormPopup(requireContext(), root);
            }
        });

        return root;
    }

    private void updateEventList(DataSnapshot dataSnapshot) {
        eventAdapter.clear();

        // Create a list to hold the events in reverse order
        List<VolunteerData> reversedEvents = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            VolunteerData volunteerData = snapshot.getValue(VolunteerData.class);

            if (volunteerData != null && volunteerData.getevent1() != null) {
                reversedEvents.add(0, volunteerData); // Add at the beginning of the list
            }
        }

        // Add the reversed list to the adapter
        for (VolunteerData event : reversedEvents) {
            eventAdapter.add(event);
        }
    }

    // Custom method to show a popup with event details in user UI
    public void showEventDetailsPopup2(VolunteerData volunteerData) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.user_dialog_event_details, null);

        // Get references to the TextViews, ImageView, and Buttons in the custom layout
        TextView textTitle = dialogView.findViewById(R.id.textTitle1);
        TextView textEvent = dialogView.findViewById(R.id.textEvent1);
        ImageView imageEvent = dialogView.findViewById(R.id.imageEvent1);


        // Set text and image in the TextViews and ImageView
        textTitle.setText("Event Details");
        textEvent.setText("Event: " + volunteerData.getevent1() + "\n"
                + "Location: " + volunteerData.getlocation1() + "\n"
                + "Date: " + volunteerData.getDate1() + "\n"
                + "Name: " + volunteerData.getName1() + "\n"
                + "Number: " + volunteerData.getNumber1() + "\n"
                + "Email: " + volunteerData.getEmail1());

        // Load the image from the URL using Picasso library

        Picasso.get().load(volunteerData.getImageUrl1()).into(imageEvent);

        // Build and show an AlertDialog with the custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Handle OK button click
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }



    private void showVolunteerRecruitPopup(View root) {
        // Inflate the volunteer_recruit.xml layout
        View popupView = getLayoutInflater().inflate(R.layout.volunteer_recruit, null);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Pending_Events");

        // Find the submit1 button, name1, number1, and email1 in the popup layout
        com.google.android.material.button.MaterialButton submitButton = popupView.findViewById(R.id.submit1);
        EditText eventEditText = popupView.findViewById(R.id.event1);
        EditText nameEditText = popupView.findViewById(R.id.name1);
        EditText locationEditText = popupView.findViewById(R.id.location1);
        EditText dateEditText = popupView.findViewById(R.id.date1);
        EditText numberEditText = popupView.findViewById(R.id.number1);
        EditText emailEditText = popupView.findViewById(R.id.email1);
        Button proofButton = popupView.findViewById(R.id.proofButton);
        selectedImageUriTextView = popupView.findViewById(R.id.selectedImageUriTextView);
        ProgressBar progressBar = popupView.findViewById(R.id.progressBar);

        proofButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch image picker intent
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }
        });

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get entered data
                String event = eventEditText.getText().toString();
                String location = locationEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String number = numberEditText.getText().toString();
                String email = emailEditText.getText().toString();

                // check if all fields are filled
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Fill all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if an image is selected
                    if (selectedImageUri != null) {

                        // Show progress bar during image upload
                        progressBar.setVisibility(View.VISIBLE);

                        // Upload the selected image to Firebase Storage
                        uploadImageToFirebaseStorage(selectedImageUri, progressBar, submitButton,
                                eventEditText, locationEditText, dateEditText, nameEditText, numberEditText, emailEditText);

                    } else {
                        // Handle the case when no image is selected
                        imageUrl = null; // Set imageUrl to null explicitly
                        // Handle the case when no image is selected
                        Toast.makeText(getActivity(), "Please provide Proof", Toast.LENGTH_SHORT).show();
                    }
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