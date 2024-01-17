package com.example.rayzi.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ActivityNotificationBinding;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        sessionManager = new SessionManager(this);
        initView();
        initlistener();
    }

    private void initlistener() {

        binding.imgback.setOnClickListener(v -> finish());
        binding.switchNewRequest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            submitSetting("newFollow");
            // buttonView.setChecked(isChecked);
        });
        binding.switchFavLive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            submitSetting("favoriteLive");
            // buttonView.setChecked(isChecked);
        });
        binding.switchLikeCmt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            submitSetting("likeCommentShare");
            // buttonView.setChecked(isChecked);
        });
        binding.switchMessages.setOnCheckedChangeListener((buttonView, isChecked) -> {
            submitSetting("message");
            // buttonView.setChecked(isChecked);
        });
    }

    private void submitSetting(String type) {
        enableDisableButton(false);
//        binding.setIsLoading(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("type", type);
        Call<UserRoot> call = RetrofitBuilder.create().changeUserNotificationSetting(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        sessionManager.saveUser(response.body().getUser());
                        initView();
                    }
                }
//                binding.setIsLoading(false);
                enableDisableButton(true);
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {

            }
        });
    }

    private void enableDisableButton(boolean b) {
        binding.switchNewRequest.setEnabled(b);
        binding.switchFavLive.setEnabled(b);
        binding.switchMessages.setEnabled(b);
        binding.switchLikeCmt.setEnabled(b);
    }

    private void initView() {
        binding.switchNewRequest.setChecked(sessionManager.getUser().getNotification().isNewFollow());
        binding.switchFavLive.setChecked(sessionManager.getUser().getNotification().isFavoriteLive());
        binding.switchLikeCmt.setChecked(sessionManager.getUser().getNotification().isLikeCommentShare());
        binding.switchMessages.setChecked(sessionManager.getUser().getNotification().isMessage());

    }
}