package com.example.cat3.ui.donation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.cat3.ui.UploadQR.UploadQr;
import com.example.cat3.databinding.QrRecyclerItemBinding;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UploadQrAdapter extends RecyclerView.Adapter<UploadQrAdapter.MyViewHolder> {
    private final ArrayList<UploadQr> dataList;
    private final Context context;
    public UploadQrAdapter(Context context, ArrayList<UploadQr> dataList) {
        this.context = context;
        this.dataList = dataList;
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
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private QrRecyclerItemBinding binding;
        ImageView recyclerImage;
        TextView recyclerCaption;
        public MyViewHolder(@NonNull QrRecyclerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            recyclerImage = binding.recyclerImage;
            recyclerCaption = binding.recyclerCaption;
        }
    }
}