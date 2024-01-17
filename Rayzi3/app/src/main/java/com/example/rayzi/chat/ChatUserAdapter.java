package com.example.rayzi.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.activity.FakeChatActivity;
import com.example.rayzi.databinding.ItemChatusersBinding;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.retrofit.Const;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder> {

    private Context context;
    private List<ChatUserListRoot.ChatUserItem> chatUserDummies = new ArrayList<>();

    @Override
    public ChatUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatusers, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatUserAdapter.ChatUserViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return chatUserDummies.size();
    }

    public void addData(List<ChatUserListRoot.ChatUserItem> chatUserDummies) {

        this.chatUserDummies.addAll(chatUserDummies);
        notifyItemRangeInserted(this.chatUserDummies.size(), chatUserDummies.size());
    }

    public void clear() {
        chatUserDummies.clear();
        notifyDataSetChanged();
    }

    public class ChatUserViewHolder extends RecyclerView.ViewHolder {
        ItemChatusersBinding binding;

        public ChatUserViewHolder(View itemView) {
            super(itemView);
            binding = ItemChatusersBinding.bind(itemView);
        }

        public void setData(int position) {
            ChatUserListRoot.ChatUserItem chatUserDummy = chatUserDummies.get(position);
            binding.imguser.setUserImage(chatUserDummy.getImage(), chatUserDummy.isVIP(), context);
            binding.tvusername.setText(chatUserDummy.getName());

            binding.tvlastchet.setText(chatUserDummy.getMessage());
            binding.tvtime.setText(chatUserDummy.getTime());
            binding.tvcountry.setText(chatUserDummy.getCountry());
            binding.getRoot().setOnClickListener(v -> {
                Log.e("TAG", "setData: >>>>>>>>>>>>>>>>>  " + chatUserDummy.isFake());
                if (!chatUserDummy.isFake()) {
                    context.startActivity(new Intent(context, ChatActivity.class).putExtra(Const.CHATROOM, new Gson().toJson(chatUserDummy)));

                } else {
                    Log.e("TAG", "setData: >>>>>>>>>>>>>>>>>> " + chatUserDummy.toString());
                    context.startActivity(new Intent(context, FakeChatActivity.class).putExtra(Const.CHATROOM, new Gson().toJson(chatUserDummy)));
                }
            });
        }
    }
}
