package com.example.cat3.ui.volunteer;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cat3.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class VolunteerFragment extends Fragment {
    private EventAdapter eventAdapter;
    private DatabaseReference eventsRef;
    private Uri selectedImageUri;
    private String imageUrl;
    private TextView selectedImageUriTextView;
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

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri != null) {
            // Extract the file name from the URI
            String fileName = getFileName(imageUri);

            // Create a storage reference with a unique name (e.g., using the timestamp)
            String storagePath = "images/" + System.currentTimeMillis() + "_" + fileName;
            StorageReference storageRef = FirebaseStorage.getInstance().getReference(storagePath);

            // Upload the image to Firebase Storage
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the image URL to the global variable
                            imageUrl = uri.toString();
                            // Display the file name under the proof button
                            if (selectedImageUriTextView != null) {
                                selectedImageUriTextView.setText(fileName);
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error during image upload
                        Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
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
        eventAdapter = new EventAdapter(requireContext(), new ArrayList<>());
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
        EditText locationEditText = popupView.findViewById(R.id.location1);
        EditText numberEditText = popupView.findViewById(R.id.number1);
        EditText emailEditText = popupView.findViewById(R.id.email1);
        Button proofButton = popupView.findViewById(R.id.proofButton);
        selectedImageUriTextView = popupView.findViewById(R.id.selectedImageUriTextView);

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

                // Upload the selected image to Firebase Storage
                uploadImageToFirebaseStorage(selectedImageUri);

                // check if all fields are filled
                if (TextUtils.isEmpty(event) || TextUtils.isEmpty(location) || TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Fill all fields.", Toast.LENGTH_SHORT).show();
                } else {

                    // Save data to Firebase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Recruit_data");

                    // Use the imageUrl variable in the VolunteerData object
                    VolunteerData volunteerData = new VolunteerData(event, location, name, number, email, imageUrl, null, null, null);
                    myRef.push().setValue(volunteerData);

                    // Display a toast message
                    Toast.makeText(getActivity(), "Form Submitted", Toast.LENGTH_SHORT).show();

                    // Clear the EditText fields after submitting
                    eventEditText.setText("");
                    nameEditText.setText("");
                    numberEditText.setText("");
                    emailEditText.setText("");
                    selectedImageUriTextView.setText(""); // Clear the image URL
                    imageUrl = null;
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