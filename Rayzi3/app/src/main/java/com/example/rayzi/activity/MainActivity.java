
package com.example.rayzi.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.rayzi.BuildConfig;
import com.example.rayzi.MainApplication;
import com.example.rayzi.NetWorkChangeReceiver;
import com.example.rayzi.R;
import com.example.rayzi.audioLive.HostLiveAudioActivity;
import com.example.rayzi.audioLive.AudioLiveStreamRoot;
import com.example.rayzi.chat.MessageFragment;
import com.example.rayzi.databinding.ActivityMainBinding;
import com.example.rayzi.databinding.BottomSheetChoicesBinding;
import com.example.rayzi.home.HomeFragment;
import com.example.rayzi.liveStreamming.WatchLiveActivity;
import com.example.rayzi.modelclass.LiveUserRoot;
import com.example.rayzi.modelclass.PostRoot;
import com.example.rayzi.modelclass.ReliteRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.popups.PrivacyPopup_g;
import com.example.rayzi.posts.FeedFragmentMain;
import com.example.rayzi.posts.FeedListActivity;
import com.example.rayzi.posts.UploadPostActivity;
import com.example.rayzi.reels.ReelsActivity;
import com.example.rayzi.reels.record.RecorderActivity;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.user.ProfileFragment;
import com.example.rayzi.user.guestUser.GuestActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.Polling;
import io.socket.engineio.client.transports.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    boolean isPrivate = false;
    private BottomSheetDialog bottomSheetDialog;
    private NetWorkChangeReceiver netWorkChangeReceiver;

    @Override
    protected void onStart() {
        super.onStart();
        MainApplication.isAppOpen = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initRequest();

    }

    private void initRequest() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_IMAGES},
                        1);
            }
        } else {
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
        if (!sessionManager.getBooleanValue(Const.POLICY_ACCEPTED)) {
            new PrivacyPopup_g(this, new PrivacyPopup_g.OnSubmitClickListnear() {
                @Override
                public void onAccept() {
                    sessionManager.saveBooleanValue(Const.POLICY_ACCEPTED, true);
                }

                @Override
                public void onDeny() {
                    finishAffinity();
                }
            });
        }

        getStrickers();
        getAdsKeys();
        initMain();
        ((MainApplication) getApplication()).initAgora(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.per_deny), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            // Do something for lollipop and above versions
        } else {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.per_deny), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        initMain();
    }

    private void showLongToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

    }


    private void initMain() {
        startReceiver();
        handleBranchData();
//        getGlobalSocket();
        createUserStatusSocket();
        makeOnlineUser();
        setDefaultBottomBar();
        setUpFragment(new HomeFragment(), binding.animHome);
        initBottomBar();
        binding.imgBall.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme);
            bottomSheetDialog.setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });

            BottomSheetChoicesBinding bottomSheetChoicesBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.bottom_sheet_choices, null, false);
            bottomSheetDialog.setContentView(bottomSheetChoicesBinding.getRoot());
            bottomSheetDialog.show();
            bottomSheetChoicesBinding.imgClose.setOnClickListener(v1 -> bottomSheetDialog.dismiss());

            bottomSheetChoicesBinding.lytLive.setOnClickListener(view -> {
                bottomSheetDialog.dismiss();

                if (!sessionManager.getUser().getLevel().getAccessibleFunction().isLiveStreaming()) {
                    Log.d(TAG, "initMain: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
                    new PopupBuilder(this).showSimplePopup("You are not able to go live at your level", "Dismiss", () -> {
                    });
                    return;
                }

                try {
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("userId", sessionManager.getUser().getId());
                    jsonObject.addProperty("channel", sessionManager.getUser().getId());
                    jsonObject.addProperty("isPublic", !isPrivate);

                    if (sessionManager.getInt(Const.BACKGROUND) == 0) {  //todo
                        jsonObject.addProperty("background", "https://bsma.digicean.com/storage/background.png");
                    } else {
                        jsonObject.addProperty("background", sessionManager.getInt(Const.BACKGROUND));
                    }

                    jsonObject.addProperty("audio", true);
                    Random random = new Random();
                    int agoraUID = random.nextInt(999999 - 111111) + 111111;
                    jsonObject.addProperty("agoraUID", 0);  // just for unique host int id

                    Call<AudioLiveStreamRoot> call = RetrofitBuilder.create().makeAudioLiveUser(jsonObject);
                    call.enqueue(new Callback<AudioLiveStreamRoot>() {
                        @Override
                        public void onResponse(Call<AudioLiveStreamRoot> call, Response<AudioLiveStreamRoot> response) {
                            if (response.code() == 200) {
                                if (response.body().isStatus()) {
                                    Intent intent = new Intent(MainActivity.this, HostLiveAudioActivity.class);
                                    intent.putExtra(Const.DATA, new Gson().toJson(response.body().getLiveUser()));
                                    intent.putExtra(Const.PRIVACY, isPrivate ? "Private" : "Public");
                                    startActivity(intent);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AudioLiveStreamRoot> call, Throwable t) {
                            Log.d(TAG, "onFailure: " + t.toString());
                        }

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            bottomSheetChoicesBinding.lytVideos.setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
                if (!sessionManager.getUser().getLevel().getAccessibleFunction().isUploadVideo()) {
                    new PopupBuilder(this).showSimplePopup("You are not able to create relite at your level", "Dismiss", () -> {
                    });
                    return;
                }

                startActivity(new Intent(this, RecorderActivity.class));
            });

            bottomSheetChoicesBinding.lytMoments.setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
                if (!sessionManager.getUser().getLevel().getAccessibleFunction().isUploadPost()) {
                    new PopupBuilder(this).showSimplePopup("You are not able to upload post at your level", "Dismiss", () -> {
                    });
                    return;
                }
                startActivity(new Intent(this, UploadPostActivity.class));
            });


        });
    }

    private void createUserStatusSocket() {
        IO.Options options = IO.Options.builder()
                // IO factory options
                .setForceNew(false)
                .setMultiplex(true)

                // low-level engine options
                .setTransports(new String[]{Polling.NAME, WebSocket.NAME})
                .setUpgrade(true)
                .setRememberUpgrade(false)
                .setPath("/socket.io/")
                .setQuery("userRoom=" + sessionManager.getUser().getId() + "")
                .setExtraHeaders(null)

                // Manager options
                .setReconnection(true)
                .setReconnectionAttempts(Integer.MAX_VALUE)
                .setReconnectionDelay(1_000)
                .setReconnectionDelayMax(5_000)
                .setRandomizationFactor(0.5)
                .setTimeout(20_000)

                // Socket options
                .setAuth(null)
                .build();

        URI uri = URI.create(BuildConfig.BASE_URL);
        Socket callSocket = IO.socket(uri, options);
        callSocket.connect();

        callSocket.on(Socket.EVENT_CONNECT, args -> {
            Log.e("TAG", "createUserStatusSocket: connected");
        });

    }

    private void handleBranchData() {
        Intent intent = getIntent();
        String branchData = intent.getStringExtra(Const.DATA);
        String type = intent.getStringExtra(Const.TYPE);
        if (branchData != null && !branchData.isEmpty()) {
            if (type.equals("POST")) {
                PostRoot.PostItem post = new Gson().fromJson(branchData, PostRoot.PostItem.class);
                List<PostRoot.PostItem> list = new ArrayList<>();
                list.add(post);
                startActivity(new Intent(this, FeedListActivity.class)

                        .putExtra(Const.POSITION, 0)
                        .putExtra(Const.DATA, new Gson().toJson(list)));

            } else if (type.equals("RELITE")) {
                ReliteRoot.VideoItem post = new Gson().fromJson(branchData, ReliteRoot.VideoItem.class);
                List<ReliteRoot.VideoItem> list = new ArrayList<>();
                list.add(post);
                startActivity(new Intent(this, ReelsActivity.class).putExtra(Const.POSITION, 0).putExtra(Const.DATA, new Gson().toJson(list)));
            } else if (type.equals("PROFILE")) {
                String userId = branchData;
                startActivity(new Intent(this, GuestActivity.class).putExtra(Const.USERID, userId));

            } else if (type.equals("LIVE")) {
                LiveUserRoot.UsersItem usersItem = new Gson().fromJson(branchData, LiveUserRoot.UsersItem.class);
                Log.d("TAG", "handleBranchData: live  " + usersItem.toString());
                startActivity(new Intent(this, WatchLiveActivity.class).putExtra(Const.DATA, new Gson().toJson(usersItem)));
            }
        }
    }

    private void setDefaultBottomBar() {
        binding.animHome.setImageTintList(ContextCompat.getColorStateList(this, R.color.gray_icon));
        binding.animDiscover.setImageTintList(ContextCompat.getColorStateList(this, R.color.gray_icon));
        binding.animChat.setImageTintList(ContextCompat.getColorStateList(this, R.color.gray_icon));
        binding.animProfile.setImageTintList(ContextCompat.getColorStateList(this, R.color.gray_icon));
    }

    private void setUpFragment(Fragment fragment, ImageView animHome) {
        setDefaultBottomBar();
        animHome.setImageTintList(ContextCompat.getColorStateList(this, R.color.offwhite));
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
    }

    private void initBottomBar() {
        binding.lytHome.setOnClickListener(v -> {
            setUpFragment(new HomeFragment(), binding.animHome);

        });
        binding.lytDiscover.setOnClickListener(v -> {
            setUpFragment(new FeedFragmentMain(), binding.animDiscover);

        });
        binding.lytChat.setOnClickListener(v -> {
            setUpFragment(new MessageFragment(), binding.animChat);

        });
        binding.lytProfile.setOnClickListener(v -> {
            setUpFragment(new ProfileFragment(), binding.animProfile);
        });
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        MainApplication.isAppOpen = false;
        super.onDestroy();
    }

    protected void startReceiver() {
        netWorkChangeReceiver = new NetWorkChangeReceiver(this::showHideInternet);
        registerNetworkBroadcastForNougat();
    }

    private void registerNetworkBroadcastForNougat() {
        registerReceiver(netWorkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(netWorkChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    private void showHideInternet(Boolean isOnline) {
        Log.d("TAG", "showHideInternet: " + isOnline);
        final TextView tvInternetStatus = findViewById(R.id.tv_internet_status);

        if (isOnline) {
            if (tvInternetStatus != null && tvInternetStatus.getVisibility() == View.VISIBLE && tvInternetStatus.getText().toString().equalsIgnoreCase(getString(R.string.no_internet_connection))) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                tvInternetStatus.setText(R.string.back_online);
                new Handler().postDelayed(() -> slideToTop(tvInternetStatus), 200);
            }
        } else {
            if (tvInternetStatus != null) {
                tvInternetStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                tvInternetStatus.setText(R.string.no_internet_connection);
                if (tvInternetStatus.getVisibility() == View.GONE) {
                    slideToBottom(tvInternetStatus);
                }
            }
        }
    }

    private void slideToTop(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_up);

       /* TranslateAnimation animation = new TranslateAnimation(0f, 0f,  0f,view.getHeight());
        animation.setDuration(1000);
        view.startAnimation(animation);*/
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        view.startAnimation(animation);
    }

    private void slideToBottom(final View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enter_down);

       /* TranslateAnimation animation = new TranslateAnimation(0f, 0f,  0f,view.getHeight());
        animation.setDuration(1000);
        view.startAnimation(animation);*/
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });
        view.startAnimation(animation);
    }
}