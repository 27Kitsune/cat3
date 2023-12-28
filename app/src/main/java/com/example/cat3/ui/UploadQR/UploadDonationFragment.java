package com.example.cat3.ui.UploadQR;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cat3.databinding.FragmentUploadDonationBinding;

public class UploadDonationFragment extends Fragment {
    private FragmentUploadDonationBinding binding;

    ActivityResultLauncher<String> mQrImage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UploadDonationViewModel UploaddonationViewModel = new ViewModelProvider(this).get(UploadDonationViewModel.class);

        binding = FragmentUploadDonationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView mUploadQrText = binding.UploadQrText;
        UploaddonationViewModel.getText().observe(getViewLifecycleOwner(), mUploadQrText::setText);

        Button mUploadQrButton = binding.ButtonUploadQr;
        Button mChooseQrButton = binding.ButtonChooseQr;
        ProgressBar mUploadQrProgressBar = binding.progressBarUploadQR;

        mQrImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        binding.ImageUploadQr.setImageURI(result);
                    }
                }
        );

        mChooseQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQrImage.launch("image/*");
            }
        });

        mUploadQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
