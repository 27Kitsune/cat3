package com.example.cat3.ui.UploadQR;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cat3.databinding.FragmentUploadDonationBinding;
import com.example.cat3.ui.UploadQR.UploadDonationViewModel;

public class UploadDonationFragment extends Fragment {
    private FragmentUploadDonationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UploadDonationViewModel UploaddonationViewModel;
        UploaddonationViewModel = new ViewModelProvider(this).get(UploadDonationViewModel.class);

        binding = FragmentUploadDonationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textUploadDonation;
        UploaddonationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
