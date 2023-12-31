package com.example.cat3.ui.UploadQR;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cat3.databinding.FragmentUploadDonationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadDonationFragment extends Fragment {
    private FragmentUploadDonationBinding binding;

    Button mUploadQrButton;
    Button mChooseQrButton;
    ProgressBar mUploadQrProgressBar;
    Uri mUploadQruri;
    StorageReference UploadDonationStorage;
    DatabaseReference UploadDonationDatabase;
    ActivityResultLauncher<String> mQrImage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UploadDonationViewModel UploaddonationViewModel = new ViewModelProvider(this).get(UploadDonationViewModel.class);

        binding = FragmentUploadDonationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mUploadQrButton = binding.ButtonUploadQr;
        mChooseQrButton = binding.ButtonChooseQr;
        mUploadQrProgressBar = binding.progressBarUploadQR;


        final TextView mUploadQrText = binding.UploadQrText;
        UploaddonationViewModel.getText().observe(getViewLifecycleOwner(), mUploadQrText::setText);


        UploadDonationStorage = FirebaseStorage.getInstance().getReference("uploadQr");
        UploadDonationDatabase = FirebaseDatabase.getInstance().getReference("uploadQr");

        mQrImage = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        binding.ImageUploadQr.setImageURI(result);
                        mUploadQruri = result;
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
                uploadQr();
            }
        });

        return root;
    }
    private void uploadQr() {

            if (mUploadQruri != null) {
                StorageReference fileQrRef = UploadDonationStorage.child(System.currentTimeMillis() + "." + getImageQrExtension(mUploadQruri));

                fileQrRef.putFile(mUploadQruri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        mUploadQrProgressBar.setVisibility(View.GONE);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mUploadQrProgressBar.setProgress(0);
                            }
                        }, 5000);

                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                        UploadQr uploadQr = new UploadQr(mUploadQruri.toString());
                        String uploadQrID = UploadDonationDatabase.push().getKey();
                        UploadDonationDatabase.child(uploadQrID).setValue(uploadQr);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        mUploadQrProgressBar.setProgress((int) progress);
                    }
                });

            } else {
                Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
            }
    }

    private String getImageQrExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
