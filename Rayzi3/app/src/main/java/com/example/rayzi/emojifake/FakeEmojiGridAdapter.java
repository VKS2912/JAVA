package com.example.rayzi.emojifake;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemEmojiGridBinding;
import com.example.rayzi.modelclass.FakeGiftRoot;

import java.util.ArrayList;
import java.util.List;

public class FakeEmojiGridAdapter extends RecyclerView.Adapter<FakeEmojiGridAdapter.EmojiViewHolder> {

    FakeOnEmojiSelectLister onEmojiSelectLister;
    List<FakeGiftRoot> giftRoots = new ArrayList<>();
    int Count;
    private Context context;

    public FakeEmojiGridAdapter(int count) {
        Count = count;
    }

    public FakeOnEmojiSelectLister getOnEmojiSelectLister() {
        return onEmojiSelectLister;
    }

    public void setOnEmojiSelectLister(FakeOnEmojiSelectLister onEmojiSelectLister) {
        this.onEmojiSelectLister = onEmojiSelectLister;
    }

    @Override
    public EmojiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new EmojiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(EmojiViewHolder holder, int position) {

        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return giftRoots.size();

    }

    public void addData(List<FakeGiftRoot> giftRoot) {
        this.giftRoots.addAll(giftRoot);
        notifyItemRangeInserted(this.giftRoots.size(), giftRoot.size());
    }

    public class EmojiViewHolder extends RecyclerView.ViewHolder {
        ItemEmojiGridBinding binding;

        public EmojiViewHolder(View itemView) {
            super(itemView);
            binding = ItemEmojiGridBinding.bind(itemView);
        }

        public void setData(int position) {
            Glide.with(binding.getRoot()).load(giftRoots.get(position).getUrl()).into(binding.imgEmoji);
            binding.tvCoin.setText(String.valueOf(giftRoots.get(position).getCoin()));
            binding.getRoot().setOnClickListener(v -> {
                Log.e("TAG", "setData: gift click  ");
                binding.itememoji.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_selected_5dp));
                onEmojiSelectLister.onEmojiSelect(binding, giftRoots.get(position), String.valueOf(Count));
            });
        }
    }
}
