package com.example.rayzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.FakeChat.FakeChatAdapter;
import com.example.rayzi.FakeChat.fakemodelclass.ChatRootFake;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ActivityFakeChatBinding;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.retrofit.Const;
import com.google.gson.Gson;

public class FakeChatActivity extends BaseActivity {
    ActivityFakeChatBinding binding;
    FakeChatAdapter chatAdapter = new FakeChatAdapter();
    SessionManager sessionManager;
    private ChatUserListRoot.ChatUserItem chatUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fake_chat);
        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.CHATROOM);
        Log.e(TAG, "onCreate: >>>>>>>>>>>> " + userStr);
        if (userStr != null && !userStr.isEmpty()) {
            chatUser = new Gson().fromJson(userStr, ChatUserListRoot.ChatUserItem.class);
            initView();
        }
        initlistener();
    }

    private void initlistener() {
        binding.tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messgae = binding.etChat.getText().toString();
                if (messgae.equals("")) {
                    Toast.makeText(FakeChatActivity.this, "Type message first", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.etChat.setText("");
                chatAdapter.addSingleMessage(new ChatRootFake(1, messgae, chatUser.getImage()));
                binding.rvChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }

    private void initView() {


        Glide.with(this).load(chatUser.getImage()).circleCrop().into(binding.imgUser);
        binding.tvUserNamew.setText(chatUser.getName());
        binding.rvChat.setAdapter(chatAdapter);

//        chatAdapter.addData(chatmsg);
    }

    public void onClickVideoCall(View view) {
        startActivity(new Intent(this, FakeCallRequestActivity.class).putExtra(Const.CHATROOM, new Gson().toJson(chatUser)));
    }

    public void onClickUser(View view) {
        if (chatUser != null) {
//            startActivity(new Intent(this, GuestActivity.class).putExtra(Const.USER_STR, new Gson().toJson(chatUser.getUser())));
        }
    }

    public void onClickCamara(View view) {
    }
}