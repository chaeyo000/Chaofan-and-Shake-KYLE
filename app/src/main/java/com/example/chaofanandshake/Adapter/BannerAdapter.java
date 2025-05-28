package com.example.chaofanandshake.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chaofanandshake.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<Integer> bannerImages;
    private ViewPager2 viewPager;
    private static final int MULTIPLIER = 1000; // For infinite looping

    public BannerAdapter(List<Integer> bannerImages, ViewPager2 viewPager) {
        this.bannerImages = bannerImages;
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        // For infinite looping
        int actualPosition = position % bannerImages.size();
        holder.bannerImage.setImageResource(bannerImages.get(actualPosition));

        // Add click listener if needed
        holder.itemView.setOnClickListener(v -> {
            // Handle banner click
        });
    }

    @Override
    public int getItemCount() {
        // Return a large number for infinite looping
        if (bannerImages.size() == 0) return 0;
        return bannerImages.size() * MULTIPLIER;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
        }
    }

    // Add this method to set initial position for infinite looping
    public void setInitialPosition() {
        if (bannerImages.size() > 0) {
            int startPosition = Integer.MAX_VALUE / 2;
            // Adjust to make sure we start at a multiple of bannerImages.size()
            startPosition = startPosition - (startPosition % bannerImages.size());
            viewPager.setCurrentItem(startPosition, false);
        }
    }
}