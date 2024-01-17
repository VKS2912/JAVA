package com.example.rayzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ActivityCallRequestBinding;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.retrofit.Const;
import com.google.gson.Gson;

public class FakeCallRequestActivity extends AppCompatActivity {
    ActivityCallRequestBinding binding;
    Handler handler = new Handler();
    private int sec = 0;
    private ChatUserListRoot.ChatUserItem chatUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_request);
        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.CHATROOM);
        Log.e("TAG", "onCreate: >>>>>>>>>>>>  fake " + userStr);
        if (userStr != null && !userStr.isEmpty()) {
            chatUser = new Gson().fromJson(userStr, ChatUserListRoot.ChatUserItem.class);
            binding.tvName.setText(chatUser.getName());
            Log.e("TAG", "onCreate: >>>>>>>>>>>>  fake " + chatUser.getImage());
            Glide.with(this).load(chatUser.getImage()).circleCrop().into(binding.imgUser);
        }
        handler.postDelayed(runnable, 1000);
        binding.btnDecline.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (sec >= 3) {
                binding.tvStatus.setText("Ringing...");
            }
            handler.postDelayed(this, 1000);
            if (sec >= 5) {

                handler.removeCallbacks(runnable);
                //   startActivity(new Intent(CallRequestActivity.this, CallIncomeActivity.class));
                startActivity(new Intent(FakeCallRequestActivity.this, FakeVideoCallActivity.class).putExtra(Const.CHATROOM, new Gson().toJson(chatUser)));
            }
            sec++;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        finish();
    }


}