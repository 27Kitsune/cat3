package com.example.cat3.ui.donation;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cat3.databinding.FragmentDonationBinding;
import com.example.cat3.ui.UploadQR.UploadQr;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class DonationFragment extends Fragment {

    private FragmentDonationBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<UploadQr> dataList;
    private UploadQrAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Qrimages");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout using the generated binding class
        binding = FragmentDonationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = binding.qrRecyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        dataList = new ArrayList<>();
        adapter = new UploadQrAdapter(requireContext(), dataList, DonationFragment.this);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UploadQr dataClass = dataSnapshot.getValue(UploadQr.class);
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Handle the permission result here
        switch (requestCode) {
            case 1: {
                // Check if the permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, handle accordingly
                } else {
                    // Permission denied, handle accordingly
                }
                return;
            }
            // Add more cases if you have multiple permissions to check

            // ...
        }
    }
}
