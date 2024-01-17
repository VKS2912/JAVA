package com.example.rayzi.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemViewBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class LiveViewAdapter extends RecyclerView.Adapter<LiveViewAdapter.ChatUserViewHolder> {


    Context context;
    OnLiveUserAdapterClickLisnter onLiveUserAdapterClickLisnter;
    private JSONArray users = new JSONArray();

    @Override
    public ChatUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    public OnLiveUserAdapterClickLisnter getOnLiveUserAdapterClickLisnter() {
        return onLiveUserAdapterClickLisnter;
    }

    public void setOnLiveUserAdapterClickLisnter(OnLiveUserAdapterClickLisnter onLiveUserAdapterClickLisnter) {
        this.onLiveUserAdapterClickLisnter = onLiveUserAdapterClickLisnter;
    }

    @Override
    public void onBindViewHolder(ChatUserViewHolder holder, int position) {
        holder.setData(position);

    }

    @Override
    public int getItemCount() {
        return users.length();
    }

    public void addData(JSONArray jsonArray) {

        users = jsonArray;
        notifyDataSetChanged();
     /*   for (int i = 0; i < jsonArray.length(); i++) {
            try {
            JSONObject object=jsonArray.getJSONObject(i);

              *//*  for (int j = 0; j < this.users.length(); j++) {
                    JSONObject user=users.getJSONObject(j);
                    if (user.get("userId").equals(object.get("userId"))){
                        users.remove(j);
                    }
                }*//*
                this.users.put(jsonArray.get(i));
                notifyItemInserted(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    public interface OnLiveUserAdapterClickLisnter {
        void onUserClick(JSONObject userDummy);
    }

    public class ChatUserViewHolder extends RecyclerView.ViewHolder {
        ItemViewBinding binding;

        public ChatUserViewHolder(View itemView) {
            super(itemView);
            binding = ItemViewBinding.bind(itemView);
        }

        public void setData(int position) {

            try {
                JSONObject userDummy = users.getJSONObject(position);
                Log.d("TAG", position + " setData: viewlist  " + position + userDummy.getString("image"));

                if (userDummy.getBoolean("isAdd")) {
                    Glide.with(context).load(userDummy.getString("image").toString()).circleCrop().into(binding.imgview);
                }

//                binding.imgview.setUserImage(userDummy.getString("image").toString(), userDummy.getBoolean("isVIP"));
                binding.imgview.setOnClickListener(v -> {
                    onLiveUserAdapterClickLisnter.onUserClick(userDummy);
                });
            } catch (Exception o) {
                Log.e("TAG", "setData:viewadapter " + o.getMessage());
            }
        }
    }
}
