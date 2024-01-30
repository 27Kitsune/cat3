package com.example.cat3.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.cat3.ui.UploadNews.UploadNews;
import com.example.cat3.databinding.NewsRecyclerItemBinding;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
public class Homeadapter extends RecyclerView.Adapter<Homeadapter.NewsViewHolder> {
    private ArrayList<UploadNews> NewsdataList;
    private Context context;
    private HomeFragment activity;
    public Homeadapter(Context context, ArrayList<UploadNews> dataList, HomeFragment activity) {
        this.context = context;
        this.NewsdataList = dataList;
        this.activity = activity;
    }
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NewsRecyclerItemBinding binding = NewsRecyclerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NewsViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Glide.with(context).load(NewsdataList.get(position).getNewsImageUrl()).into(holder.NewsImage);
        holder.NewsCaption.setText(NewsdataList.get(position).getCaption());
        holder.NewsLink.setText(NewsdataList.get(position).getWebsitelink());
    }
    @Override
    public int getItemCount() {
        return NewsdataList.size();
    }
    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        NewsRecyclerItemBinding binding;
        ImageView NewsImage;
        TextView NewsCaption, NewsLink;
        public NewsViewHolder(@NonNull NewsRecyclerItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            NewsImage = binding.newsImage;
            NewsCaption = binding.newsCaption;
            NewsLink = binding.newsLink;

        }
    }
}