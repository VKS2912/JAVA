package com.example.rayzi.liveStreamming;

import static com.example.rayzi.liveStreamming.HostLiveActivity.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.activity.FakeWatchLiveActivity;
import com.example.rayzi.audioLive.WatchAudioLiveActivity;
import com.example.rayzi.databinding.ItemVideoGridBinding;
import com.example.rayzi.modelclass.LiveUserRoot;
import com.example.rayzi.retrofit.Const;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<LiveUserRoot.UsersItem> userDummies = new ArrayList<>();
    private int adbanner_layout = 2;
    private int live_layout = 1;
    public static final String TAG = "LiveListAdapter";

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == 1) {
            return new VideoListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_grid, parent, false));
        } else {
            return new AdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoListViewHolder) {
            ((VideoListViewHolder) holder).setData(position);
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return userDummies.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
      /*  if (i % 3 == 0) {
            return this.adbanner_layout;
        }*/
        return this.live_layout;
    }

    public void addData(List<LiveUserRoot.UsersItem> userDummies) {

        this.userDummies.addAll(userDummies);
        notifyItemRangeInserted(this.userDummies.size(), userDummies.size());
    }

    public void clear() {
        userDummies.clear();
        notifyDataSetChanged();
    }

    public class VideoListViewHolder extends RecyclerView.ViewHolder {
        ItemVideoGridBinding binding;

        public VideoListViewHolder(View itemView) {
            super(itemView);
            binding = ItemVideoGridBinding.bind(itemView);


        }

        public void setData(int position) {
            LiveUserRoot.UsersItem userDummy = userDummies.get(position);
            binding.tvName.setText(userDummy.getName());
            binding.tvCountry.setText(userDummy.getCountry());

            Log.e("TAG", "setData: >>>>>>>>>>> 00 " + userDummy.isFake());
            Log.e("TAG", "setData: >>>>>>>>>>> 11 " + userDummy.getLink());
            if (userDummy.isFake()) {
                Glide.with(context).load(userDummy.getImage())
                        .apply(MainApplication.requestOptionsLive)
                        .centerCrop().into(binding.image);
            } else {
                Glide.with(context).load(userDummy.getImage())
                        .apply(MainApplication.requestOptionsLive)
                        .centerCrop().into(binding.image);
            }
            binding.tvViewCount.setText(String.valueOf(userDummy.getView()));
            binding.getRoot().setOnClickListener(v -> {
                if (userDummy.isFake()) {
                    Log.d(TAG, "setData: "+userDummy.toString());
                    context.startActivity(new Intent(context, FakeWatchLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                } else {
                    Log.e("TAG", "setData: isfake not");
                    context.startActivity(new Intent(context, WatchAudioLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(userDummy)));
                }

            });
        }
    }

    private class AdViewHolder extends RecyclerView.ViewHolder {
        public AdViewHolder(View inflate) {
            super(inflate);
        }
    }
}
