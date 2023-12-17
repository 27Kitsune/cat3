package com.example.cat3.ui.volunteer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.cat3.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Log;

public class VolunteerFragment extends Fragment {

    private EditText nameEditText, numberEditText, emailEditText;
    private Button submitButton;

    // Initialize Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("rherhs");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_volunteer, container, false);

        // Initialize UI elements
        nameEditText = root.findViewById(R.id.name);
        numberEditText = root.findViewById(R.id.number);
        emailEditText = root.findViewById(R.id.email);
        submitButton = root.findViewById(R.id.submit);

        // Set a click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get entered data
                String name = nameEditText.getText().toString();
                String number = numberEditText.getText().toString();
                String email = emailEditText.getText().toString();

                // check if all fields is filled
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(email)) {
                    Toast.makeText(getActivity(), "Fill all field.", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Save data to Firebase
                    VolunteerData volunteerData = new VolunteerData(name, number, email);
                    myRef.push().setValue(volunteerData);

                    // Display a toast message
                    Toast.makeText(getActivity(), "Form Submitted", Toast.LENGTH_SHORT).show();

                    // Optionally, you can clear the EditText fields after submitting
                    nameEditText.setText("");
                    numberEditText.setText("");
                    emailEditText.setText("");
                }
            }
        });

        return root;
    }
}