package com.example.cat3.ui.Monitoring;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cat3.R;
import com.example.cat3.databinding.FragmentMonitoringBinding;
//import com.example.cat3.user.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MonitoringFragment extends Fragment {

    private FragmentMonitoringBinding binding;
    private DatabaseReference floodSensorReference;

    private DatabaseReference accelerometerReference;
    private ValueEventListener waterLevelValueEventListener;
    private ValueEventListener accelerometerValueEventListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MonitoringViewModel monitoringViewModel =
                new ViewModelProvider(this).get(MonitoringViewModel.class);

        binding = FragmentMonitoringBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textViewWaterLevel;
        final TextView waterLevelTextView = binding.textViewWLNum;
        final TextView textViewDangerScale = binding.textViewWLdangerscale;

        monitoringViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialize Firebase database reference
        floodSensorReference = FirebaseDatabase.getInstance().getReference().child("FloodSensor").child("WaterLevel");
        accelerometerReference = FirebaseDatabase.getInstance().getReference().child("accelerometer");

        // Create a ValueEventListener to listen for changes in the database
        waterLevelValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle data changes
                if (dataSnapshot.exists()) {
                    // Get the new water level value
                    int waterLevel = dataSnapshot.getValue(Integer.class);


                    // Update the TextView with the new water level
                    waterLevelTextView.setText(String.valueOf(waterLevel));

                    if (waterLevel < 5) {
                        textViewDangerScale.setText("Normal");
                    } else if (waterLevel < 8) {
                        textViewDangerScale.setText("Moderate");
                    } else {
                        textViewDangerScale.setText("Danger");
                        //callnotifunc
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        };

        // Create a ValueEventListener to listen for changes in the accelerometer data
        accelerometerValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle accelerometer data changes
                if (dataSnapshot.exists()) {
                    int xValue = dataSnapshot.child("X").getValue(Integer.class);
                    int yValue = dataSnapshot.child("Y").getValue(Integer.class);
                    int zValue = dataSnapshot.child("Z").getValue(Integer.class);
                    updateAccelerometerUI(xValue, yValue, zValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        };

        // Add the ValueEventListener to the database reference
        floodSensorReference.addValueEventListener(waterLevelValueEventListener);
        accelerometerReference.addValueEventListener(accelerometerValueEventListener);

        return root;
    }

    private void updateAccelerometerUI(int xValue, int yValue, int zValue) {
        TextView xTextView = binding.textViewEQXNum;
        TextView yTextView = binding.textViewEQYNum;
        TextView zTextView = binding.textViewEQZNum;

        xTextView.setText(String.valueOf(xValue));
        yTextView.setText(String.valueOf(yValue));
        zTextView.setText(String.valueOf(zValue));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove the ValueEventListeners when the Fragment is destroyed to avoid memory leaks
        if (floodSensorReference != null && waterLevelValueEventListener != null) {
            floodSensorReference.removeEventListener(waterLevelValueEventListener);
        }

        if (accelerometerReference != null && accelerometerValueEventListener != null) {
            accelerometerReference.removeEventListener(accelerometerValueEventListener);
        }
        binding = null;
    }
}