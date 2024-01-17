package com.example.rayzi.audioLive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemAudioBgBinding;

import java.util.ArrayList;
import java.util.List;

public class BakgroundAdapter extends RecyclerView.Adapter<BakgroundAdapter.BgHolder> {

    ArrayList<ThemeRoot.ThemeItem> ImageList = new ArrayList<>();
    Context context;
    onImageClick onImageClick;

    public BakgroundAdapter.onImageClick getOnImageClick() {
        return onImageClick;
    }

    public void setOnImageClick(BakgroundAdapter.onImageClick onImageClick) {
        this.onImageClick = onImageClick;
    }

    @NonNull
    @Override
    public BgHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_bg, parent, false);
        return new BgHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BgHolder holder, int position) {
        Glide.with(context).load(BuildConfig.BASE_URL + ImageList.get(position).getTheme()).into(holder.binding.image);

        holder.itemView.setOnClickListener(v -> {
            onImageClick.onClick(BuildConfig.BASE_URL + ImageList.get(position).getTheme());
        });
    }

    @Override
    public int getItemCount() {
        return ImageList.size();
    }

    public void addData(List<ThemeRoot.ThemeItem> theme) {
        ImageList.addAll(theme);
        notifyItemRangeInserted(ImageList.size(), theme.size());
    }

    public interface onImageClick {
        void onClick(String image);
    }

    public class BgHolder extends RecyclerView.ViewHolder {
        ItemAudioBgBinding binding;

        public BgHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAudioBgBinding.bind(itemView);
        }
    }
}
