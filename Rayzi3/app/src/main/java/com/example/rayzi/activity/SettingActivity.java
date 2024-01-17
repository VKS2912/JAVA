package com.example.rayzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.ActivitySettingBinding;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends BaseActivity {
    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.tvVerson.setText(getText(R.string.app_name) + " : 1.0");

        initView();


        initLister();
    }

    private void initView() {
        binding.switchNewRequest.setChecked(sessionManager.getUser().getNotification().isNewFollow());
        binding.switchFavLive.setChecked(sessionManager.getUser().getNotification().isFavoriteLive());
        binding.switchLikeCmt.setChecked(sessionManager.getUser().getNotification().isLikeCommentShare());
        binding.switchMessages.setChecked(sessionManager.getUser().getNotification().isMessage());

        if (isRTL(this)) {
            binding.notification.setGravity(Gravity.END);
            binding.termsofservice.setGravity(Gravity.END);
            binding.privacypolicy.setGravity(Gravity.END);
            binding.aboutus.setGravity(Gravity.END);
            binding.logout.setGravity(Gravity.END);

            binding.backbtn.setScaleX(isRTL(this) ? -1 : 1);
        }
    }

    private void initLister() {

        binding.RelNotification.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

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


        binding.btnLogout.setOnClickListener(v -> {
            new PopupBuilder(this).showReliteDiscardPopup("Are you sure you want logout?",
                    "",
                    "Continue", "Cancel", () -> {
                        GoogleSignInOptions gso = new GoogleSignInOptions.
                                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                                build();

                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                        googleSignInClient.signOut();
                        sessionManager.saveBooleanValue(Const.ISLOGIN, false);
                        Intent intent = new Intent(this, SpleshActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finishAffinity();
                        startActivity(intent);
                    });


        });
    }

    private void submitSetting(String type) {
        enableDisableButton(false);
        binding.setIsLoading(true);
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
                binding.setIsLoading(false);
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

    public void onClickPrivacy(View view) {
        WebActivity.open(this, "Privacy Policy", sessionManager.getSetting().getPrivacyPolicyLink());
    }

    public void onClickAbout(View view) {
        WebActivity.open(this, "About Us", sessionManager.getSetting().getPrivacyPolicyLink());
    }


    public void onClickTerms(View view) {
        WebActivity.open(this, "Terms of Service", sessionManager.getSetting().getPrivacyPolicyLink());
    }
}