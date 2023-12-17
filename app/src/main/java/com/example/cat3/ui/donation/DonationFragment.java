package com.example.cat3.ui.donation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cat3.databinding.FragmentDonationBinding;

public class DonationFragment extends Fragment {

    private FragmentDonationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DonationViewModel donationViewModel =
                new ViewModelProvider(this).get(DonationViewModel.class);

        binding = FragmentDonationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDonation;
        donationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}