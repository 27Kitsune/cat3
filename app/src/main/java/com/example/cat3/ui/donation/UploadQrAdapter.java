package com.example.cat3.ui.donation;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import android.Manifest;
import com.example.cat3.ui.UploadQR.UploadQr;
import com.example.cat3.databinding.QrRecyclerItemBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class UploadQrAdapter extends RecyclerView.Adapter<UploadQrAdapter.MyViewHolder> {
    private final ArrayList<UploadQr> dataList;
    private final Context context;
    private final DonationFragment activity;
    public UploadQrAdapter(Context context, ArrayList<UploadQr> dataList, DonationFragment activity) {
        this.context = context;
        this.dataList = dataList;
        this.activity = activity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the generated binding class to inflate the layout
        QrRecyclerItemBinding binding = QrRecyclerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImageUrl()).into(holder.recyclerImage);
        holder.recyclerCaption.setText(dataList.get(position).getCaption());

        holder.Downloadqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage(holder);
            }
        });
    }

    private void saveImage(MyViewHolder holder) {
        Log.d(TAG, "saveimage: downloading selected Image");

        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        FileOutputStream fileOutputStream = null;
        File file = getDisc();

        if (!file.exists() && !file.mkdirs()){
            file.mkdirs();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "IMG" + date + ".jpg";
        String file_name = file.getAbsolutePath() + "/" + name;
        File new_file = new File(file_name);

        try {

            BitmapDrawable draw = (BitmapDrawable) holder.recyclerImage.getDrawable();
            Bitmap bitmap = draw.getBitmap();
            fileOutputStream = new FileOutputStream(new_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(context, "save Image Success", Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

        refreshgallery(new_file);
    }

    private File getDisc() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(file, "");
    }

    private void refreshgallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private QrRecyclerItemBinding binding;
        ImageView recyclerImage, Downloadqr;
        TextView recyclerCaption;
        public MyViewHolder(@NonNull QrRecyclerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            recyclerImage = binding.recyclerImage;
            Downloadqr = binding.downloadQr;
            recyclerCaption = binding.recyclerCaption;
        }
    }
}