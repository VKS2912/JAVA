package com.example.rayzi.FakeChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.FakeChat.fakemodelclass.ChatRootFake;
import com.example.rayzi.R;
import com.example.rayzi.databinding.FakeItemChatBinding;
import com.example.rayzi.modelclass.UserRoot;

import java.util.ArrayList;
import java.util.List;

public class FakeChatAdapter extends RecyclerView.Adapter<FakeChatAdapter.ChatTextViewHolder> {

    private static final int TEXT_TYPE = 3;
    private static final int PHOTO_TYPE = 1;
    private static final int EMOJI_TYPE = 2;
    String message;
    private Context context;
    //    private List<Chat> chatList = new ArrayList<>();
    private UserRoot.User guestUser;
    private UserRoot.User user;
    private List<ChatRootFake> list = new ArrayList<>();

//    @Override
//    public int getItemViewType(int position) {
//        Log.d("TAG", "getItemViewType: " + chatList.get(position).getType());
//        return chatList.get(position).getType();
//      /*  if (chatList.get(position).getType()==Chat.TEXT){
//            return TEXT_TYPE;
//        }else if (chatList.get(position).getType()==Chat.PHOTO){
//            Log.d("TAG", "getItemViewType: imagre "+position);
//            return PHOTO_TYPE;
//        }else {
//            return EMOJI_TYPE;
//        }*/
//    }

    @Override
    public FakeChatAdapter.ChatTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ChatTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fake_item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatTextViewHolder holder, int position) {
        holder.binding.tvuser.setVisibility(View.GONE);
        holder.binding.lytimagerobot.setVisibility(View.GONE);
        holder.binding.lytimageuser.setVisibility(View.GONE);
        ChatRootFake messageRoot = list.get(position);
        if (messageRoot.getFlag() == 1) {
            if (!messageRoot.getMessage().equals("")) {
                holder.binding.tvuser.setText(messageRoot.getMessage());
                Glide.with(holder.binding.imgUser2).load(messageRoot.getImage()).circleCrop().into(holder.binding.imgUser2);
                holder.binding.tvuser.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    public void addData(List<Chat> chatList) {
//        Log.e("TAG", "addData: >>>>>>>>>>>> " +chatList );
//        this.chatList.addAll(chatList);
//        notifyItemRangeInserted(this.chatList.size(), chatList.size());
//    }
//
//    public void initGuestUser(UserRoot.User guestUser) {
//
//        this.guestUser = guestUser;
//    }
//
//    public void initLocalUser(UserRoot.User user) {
//
//        this.user = user;
//    }

    public void addSingleMessage(ChatRootFake chatRootFake) {
        list.add(chatRootFake);
        notifyItemInserted(list.size() - 1);
    }

    public class ChatTextViewHolder extends RecyclerView.ViewHolder {
        FakeItemChatBinding binding;

        public ChatTextViewHolder(View itemView) {
            super(itemView);
            binding = FakeItemChatBinding.bind(itemView);
        }

        public void setData(int position) {

        }
    }
}
