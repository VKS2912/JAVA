package com.example.rayzi.activity;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraX;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ActivityVideoCallFakeBinding;
import com.example.rayzi.modelclass.ChatUserListRoot;
import com.example.rayzi.models.CoinRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.utils.AutoFitPreviewBuilder;
import com.example.rayzi.viewModel.FakeVideoCallViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FakeVideoCallActivity extends AppCompatActivity {
    private static final String TAG = "fakevideocallact";
    ActivityVideoCallFakeBinding binding;
    SessionManager sessionManager;
    private FakeVideoCallViewModel viewModel;
    private SimpleExoPlayer player;
    private String videoURL;
    private ChatUserListRoot.ChatUserItem user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_call_fake);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new FakeVideoCallViewModel()).createFor()).get(FakeVideoCallViewModel.class);
        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.CHATROOM);
        Log.e(TAG, "onCreate: >>>>>>>>>>>> " + userStr);
        if (userStr != null && !userStr.isEmpty()) {
            user = new Gson().fromJson(userStr, ChatUserListRoot.ChatUserItem.class);
            videoURL = user.getLink();
        }
        initCamera();
        setPlayer();
        getCoin();

      /*  Glide.with(this).load("https://images.unsplash.com/photo-1623582456659-1bfff5017808?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=735&q=80")
                .centerCrop().into(binding.image1);
        Glide.with(this).load("https://images.unsplash.com/photo-1583864697784-a0efc8379f70?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=688&q=80")
                .centerCrop().into(binding.image2);*/


        binding.btnDecline.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        initCamera();
    }

    public MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void setPlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        binding.playerview.setPlayer(player);
//        binding.playerview.setShowBuffering(1);
        Log.d(TAG, "setvideoURL: " + videoURL);
        Uri uri = Uri.parse(videoURL);
        MediaSource mediaSource = buildMediaSource(uri);
        Log.d(TAG, "initializePlayer: " + uri);
        player.setPlayWhenReady(true);
        player.seekTo(0, 0);
        player.prepare(mediaSource, false, false);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case STATE_BUFFERING:
                        Log.d(TAG, "buffer: " + uri);
                        break;
                    case STATE_ENDED:
                        Toast.makeText(FakeVideoCallActivity.this, "Call Ended!", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        Log.d(TAG, "end: " + uri);
                        break;
                    case STATE_IDLE:
                        Log.d(TAG, "idle: " + uri);
                        break;

                    case STATE_READY:
//                        binding.animationView.setVisibility(View.GONE);
//                        if (!timestarted) {
//                            timestarted = true;
//                            timerHandler.postDelayed(timerRunnable, 1000);
//                        }

                        Log.d(TAG, "ready: " + uri);

                        break;
                    default:
                        break;
                }
            }
        });

    }


    @SuppressLint("RestrictedApi")
    private void initCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    1);
        } else {
            TextureView viewFinder = binding.viewFinder;
            AspectRatio ratio = AspectRatio.RATIO_4_3;
            viewModel.builder = new PreviewConfig.Builder();
            viewModel.previewConfig = viewModel.builder.setTargetAspectRatio(ratio)
                    .setLensFacing(viewModel.lensFacing)
                    .setTargetRotation(Surface.ROTATION_90)
                    .build();
            viewModel.preview = AutoFitPreviewBuilder.Companion.build(viewModel.previewConfig, viewFinder);
            viewModel.builder1 = new VideoCaptureConfig.Builder();
            viewModel.videoCaptureConfig = viewModel.builder1.setTargetAspectRatio(ratio)
                    .setLensFacing(viewModel.lensFacing)
                    .setVideoFrameRate(24)
                    .setTargetRotation(Surface.ROTATION_0)
                    .build();
            viewModel.videoCapture = new VideoCapture(viewModel.videoCaptureConfig);
            CameraX.bindToLifecycle(this, viewModel.preview, viewModel.videoCapture);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        CameraX.unbindAll();
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    public void onSwitchCameraClicked(View view) {
//
        if (viewModel.lensFacing == CameraX.LensFacing.FRONT) {
            viewModel.lensFacing = CameraX.LensFacing.BACK;
        } else {
            viewModel.lensFacing = CameraX.LensFacing.FRONT;
        }
        CameraX.unbindAll();
        new Handler(Looper.getMainLooper()).postDelayed(this::initCamera, 1000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void getCoin() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("senderUserId", sessionManager.getUser().getId());
        jsonObject.addProperty("coin", sessionManager.getSetting().getCallCharge());
        jsonObject.addProperty("receiverUserId", "");
        jsonObject.addProperty("type", Const.CAll);
        Call<CoinRoot> call;
        call = RetrofitBuilder.create().getCoin(jsonObject);
        call.enqueue(new Callback<CoinRoot>() {
            @Override
            public void onResponse(Call<CoinRoot> call, Response<CoinRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Log.e("TAG", "onResponse: >>>>>>>>>>>> " + response.body().getSenderUser());
//                        binding.tvCoin.setText(String.valueOf(response.body().getSenderUser().getDiamond()));

                    }
                }
            }

            @Override
            public void onFailure(Call<CoinRoot> call, Throwable t) {

            }
        });
    }
}