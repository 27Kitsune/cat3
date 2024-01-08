package com.example.cat3.ui.donation;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cat3.R;
import com.example.cat3.databinding.FragmentDonationBinding;
import com.example.cat3.ui.UploadQR.UploadQr;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class DonationFragment extends Fragment {
    private FragmentDonationBinding binding;
    private List<UploadQr> duploadQr;

    private DonationFragment context;
    private ImageView dImage;
    TextView dtextView;

    FirebaseDatabase dfirebaseDatabase;
    DatabaseReference ddatabaseReference;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DonationViewModel donationViewModel = new ViewModelProvider(this).get(DonationViewModel.class);

        binding = FragmentDonationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dtextView = binding.textDonation;
        dImage = binding.donationImage;
        context = this;


        dfirebaseDatabase = FirebaseDatabase.getInstance();
        ddatabaseReference = dfirebaseDatabase.getReference("uploadQr").child("");

        ddatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dpostsnapshot : snapshot.getChildren()) {
                    // Get the imageUrl from the current snapshot
                    String imageUrl = dpostsnapshot.child("imageUrl").getValue(String.class);

                    Log.d("FirstYourTag", "ImageUrl: " + imageUrl);
                    Log.d("FirstimageUrl", imageUrl);

                    // Load the image into the ImageView using Picasso
                    if (imageUrl != null) {
                        Picasso.get().load(Uri.parse(imageUrl)).into(dImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Do something when the image is loaded successfully
                                Log.d("Picasso", "Image loaded successfully");
                            }

                            @Override
                            public void onError(Exception e) {
                                // Handle error
                                Log.e("Picasso", "Error loading image: " + e.getMessage());
                            }
                        });
                        donationViewModel.getText().observe(getViewLifecycleOwner(), dtextView::setText);

                        break; // If you only want to display the first image, break out of the loop
                    }
                }

//                for (DataSnapshot dpostsnapshot : snapshot.getChildren()) {
//                    // Get the imageUrl from the current snapshot
//                    String imageUrl = dpostsnapshot.child("imageUrl").getValue(String.class);
//
//                    Log.d("FirstYourTag", "ImageUrl: " + imageUrl);
//
//                    // Resolve content URI to a file path
//                    String imagePath = getImagePathFromUri(requireActivity(), Uri.parse(imageUrl));
//
//                    // Load the image into the ImageView using Picasso
//                    if (imagePath != null) {
//                        Picasso.get().load(new File(imagePath)).into(dImage, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Log.d("Picasso", "Image loaded successfully");
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//                                Log.e("Picasso", "Error loading image: " + e.getMessage());
//                            }
//                        });
//                        donationViewModel.getText().observe(getViewLifecycleOwner(), dtextView::setText);
//                        break;
//                    }
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

//    private String getImagePathFromUri(Context context, Uri uri) {String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
//
//        if (cursor != null) {
//            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            String imagePath = cursor.getString(columnIndex);
//            cursor.close();
//            return imagePath;
//        }
//
//        return null;
//    }

//        public void onStart() {
//        super.onStart();
//        ddatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String message = snapshot.child("imageUrl").getValue(String.class);
//                Picasso.get().load(message).into(dImage);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}




//        ddatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                    // 'childSnapshot' now contains data for each child node
//                    String contentUri = childSnapshot.child("imageUrl").getValue(String.class);
//
//                    // Resolve the content URI to a file path
//                    String filePath = resolveContentUriToFilePath(contentUri);
//
//                    // Load the image into an ImageView using Picasso
//                    ImageView imageView = new ImageView(requireContext());
//                    Picasso.get().load(new File(filePath)).into(imageView);
//
//                    // Add the ImageView to your layout or use it as needed
//                    // For example, if you have a LinearLayout with id 'container':
//                    // LinearLayout container = findViewById(R.id.container);
//                    // container.addView(imageView);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//            }
//        });

//        getImage.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(
//                            @NonNull DataSnapshot dataSnapshot)
//                    {
//                        // getting a DataSnapshot for the
//                        // location at the specified relative
//                        // path and getting in the link variable
//                        String link = dataSnapshot.child("uploadQr").getValue(String.class);
//
//                        // loading that data into rImage
//                        // variable which is ImageView
//                        Picasso.get().load(link).into(dImage);
//
//                    }
//
//                    // this will called when any problem
//                    // occurs in getting data
//                    @Override
//                    public void onCancelled(
//                            @NonNull DatabaseError databaseError)
//                    {
//                        // we are showing that error message in
//                        // toast
//                        Toast.makeText(requireContext(), "Error Loading Image", Toast.LENGTH_SHORT).show();
//                    }
//                });


//    private String resolveContentUriToFilePath(String contentUri) {
//        // Use the contentResolver to get the file path from the content URI
//        String filePath = null;
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = requireActivity().getContentResolver().query(Uri.parse(contentUri), projection, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            filePath = cursor.getString(columnIndex);
//            cursor.close();
//        }
//        return filePath;
//    }