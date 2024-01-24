package com.example.cat3.ui.UploadQR;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cat3.R;
import com.example.cat3.databinding.FragmentUploadDonationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class UploadDonationFragment extends Fragment {

    private FragmentUploadDonationBinding binding;
    private FloatingActionButton uploadButton;
    private ImageView uploadImage;
    private EditText uploadCaption;
    private ProgressBar progressBar;
    private Uri imageUri;
    private Spinner uploadspinner;
    private Button uploaddeletebutton;
    ValueEventListener listener;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Qrimages");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference("Qrimages");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUploadDonationBinding.inflate(inflater, container, false);

        uploadButton = binding.uploadButton;
        uploadCaption = binding.uploadCaption;
        uploadImage = binding.uploadImage;
        progressBar = binding.progressBar;
        progressBar.setVisibility(View.INVISIBLE);
        uploadspinner = binding.uploadqrspinner;
        uploaddeletebutton = binding.uploadqrdeletebutton;

        list = new ArrayList<String>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
        uploadspinner.setAdapter(adapter);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            uploadImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null){
                    uploadToFirebase(imageUri);
                } else  {
                    Toast.makeText(getContext(), "Please select image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploaddeletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected caption from the spinner
                String selectedCaption = uploadspinner.getSelectedItem().toString();

                // Check if the selected caption is not empty
                if (!selectedCaption.isEmpty()) {
                    // Call the method to delete the data
                    deleteFromFirebase(selectedCaption);
                } else {
                    Toast.makeText(getContext(), "Please select a caption to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fetchdata();
        return binding.getRoot();
    }
    //Outside onCreate
    private void uploadToFirebase(Uri uri){
        String caption = uploadCaption.getText().toString();
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UploadQr uploadQr = new UploadQr(uri.toString(), caption);
                        String key = databaseReference.push().getKey();
                        databaseReference.child(key).setValue(uploadQr);
                        progressBar.setVisibility(View.INVISIBLE);

                        list.clear();
                        fetchdata();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                        uploadImage.setImageResource(R.drawable.greenupload);
                        uploadCaption.setText(null);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchdata() {
        listener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear the list before adding new items
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Get the caption from the "caption" property
                    String caption = dataSnapshot.child("caption").getValue(String.class);
                    // Add the caption to the list
                    list.add(caption);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void deleteFromFirebase(String selectedCaption) {
        // Query the database to find the data with the selected caption
        databaseReference.orderByChild("caption").equalTo(selectedCaption).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the key of the data to be deleted
                    String key = snapshot.getKey();
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    // Delete data from the database
                    databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data deleted from the database, now delete from storage
                            deleteFromStorage(imageUrl);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error deleting data from database", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteFromStorage(String imageUrl) {
        // Construct the storage reference based on the file name
        StorageReference imageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        // Delete file from storage
        imageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(getContext(), "Deleted from storage", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Error deleting file from storage
                Toast.makeText(getContext(), "Error deleting from storage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}