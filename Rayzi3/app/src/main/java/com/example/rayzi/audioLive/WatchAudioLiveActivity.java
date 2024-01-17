package com.example.rayzi.audioLive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.RayziUtils;
import com.example.rayzi.SessionManager;
import com.example.rayzi.agora.AgoraBaseActivity;
import com.example.rayzi.agora.stats.RemoteStatsData;
import com.example.rayzi.agora.stats.StatsData;
import com.example.rayzi.bottomsheets.BottomSheetCoHostDetails;
import com.example.rayzi.bottomsheets.BottomSheetReport_g;
import com.example.rayzi.bottomsheets.UserProfileBottomSheet;
import com.example.rayzi.databinding.ActivityWatchAudioLiveBinding;
import com.example.rayzi.databinding.ItemSeatBinding;
import com.example.rayzi.emoji.EmojiBottomsheetFragment;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.LiveUserRoot;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.example.rayzi.viewModel.WatchLiveViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WatchAudioLiveActivity extends AgoraBaseActivity {
    private static final String TAG = "watchliveact";
    private static int MY_UID = 0;
    ActivityWatchAudioLiveBinding binding;
    Handler handler = new Handler();
    SessionManager sessionManager;
    String token = "";
    EmojiBottomsheetFragment emojiBottomsheetFragment;
    List<SeatItem> bookedSeatItemList = new ArrayList<>();
    private WatchLiveViewModel viewModel;
    private LiveUserRoot.UsersItem host;
    private EmojiSheetViewModel giftViewModel;
    private boolean isVideoDecoded = false;
    private int uuid;
    private int selfPosition;
    private SeatAdapter seatAdapter;
    String giftReceiverId;
    List<String> uidlist = new ArrayList<>();
    ArrayList<SeatItem> coHostList = new ArrayList<>();

    private Emitter.Listener commentListner = args -> {
        if (args[0] != null) {
            runOnUiThread(() -> {

                Log.d(TAG, "commentlister : " + args[0]);
                String data = args[0].toString();
                if (!data.isEmpty()) {
                    LiveStramComment liveStramComment = new Gson().fromJson(data.toString(), LiveStramComment.class);
                    if (liveStramComment != null) {
                        viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
                        scrollAdapterLogic();
                    }

                }

            });

        }
    };
    private Emitter.Listener giftListner = args -> {

        runOnUiThread(() -> {
            Log.d(TAG, "giftloister : " + args);
            if (args[0] != null) {
                String data = args[0].toString();
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    if (jsonObject.get("gift") != null) {
                        GiftRoot.GiftItem giftData = new Gson().fromJson(jsonObject.get("gift").toString(), GiftRoot.GiftItem.class);
                        if (giftData != null) {
                            if (giftData.getType() == 2) {
                                binding.svgaImage.setVisibility(View.VISIBLE);

                                Log.e(TAG, ">>>>>>>>>>>>>>>>>>   : " + BuildConfig.BASE_URL + giftData.getImage());
                                Glide.with(this).load(BuildConfig.BASE_URL + giftData.getImage()).into(binding.svgaImage);

                                new Handler(Looper.myLooper()).postDelayed(() -> {
                                    binding.svgaImage.setVisibility(View.GONE);
                                    binding.svgaImage.clear();
                                }, 6000);

                            } else {
                                Glide.with(binding.imgGift).load(BuildConfig.BASE_URL + giftData.getImage())
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .into(binding.imgGift);
                                Glide.with(binding.imgGiftCount).load(RayziUtils.getImageFromNumber(giftData.getCount()))
                                        .into(binding.imgGiftCount);

                                String name = jsonObject.getString("userName").toString();
                                binding.tvGiftUserName.setText(name + " Sent a gift");

                                binding.lytGift.setVisibility(View.VISIBLE);
                                binding.tvGiftUserName.setVisibility(View.VISIBLE);
                                new Handler(Looper.myLooper()).postDelayed(() -> {
                                    binding.lytGift.setVisibility(View.GONE);
                                    binding.tvGiftUserName.setVisibility(View.GONE);
                                    binding.tvGiftUserName.setText("");
                                    binding.imgGift.setImageDrawable(null);
                                    binding.imgGiftCount.setImageDrawable(null);
                                }, 3000);
                                makeSound();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (args[1] != null) {  // gift sender user
                Log.d(TAG, "user string   : " + args[1].toString());
                try {
                    JSONObject jsonObject = new JSONObject(args[1].toString());
                    UserRoot.User user = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                    if (user != null) {
                        Log.d(TAG, ":getted user    " + user.toString());
                        if (user.getId().equals(sessionManager.getUser().getId())) {
                            sessionManager.saveUser(user);
                            giftViewModel.localUserCoin.setValue(user.getDiamond());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (args[2] != null) {   // host
                Log.d(TAG, "host string   : " + args[2].toString());
                try {
                    JSONObject jsonObject = new JSONObject(args[2].toString());
                    UserRoot.User host = new Gson().fromJson(jsonObject.toString(), UserRoot.User.class);
                    if (host != null) {
                        Log.d(TAG, ":getted host    " + host.toString());
                        binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        });


    };
    private Emitter.Listener viewListner = data -> {
        runOnUiThread(() -> {
            Object args = data[0];
            Log.d(TAG, "viewListner : " + args.toString());

            try {
                JSONArray jsonArray = new JSONArray(args.toString());
                JSONArray finalArray = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getBoolean("isAdd")) {
                        finalArray.put(jsonObject);
                    }
                }

                viewModel.liveViewAdapter.addData(finalArray);
                viewModel.liveViewUserAdapter.addData(jsonArray);
                binding.tvViewUserCount.setText(String.valueOf(finalArray.length()));
                Log.d(TAG, "views2 : " + jsonArray);
            } catch (JSONException e) {
                Log.d(TAG, "207: ");
                e.printStackTrace();
            }
        });


    };
    private UserProfileBottomSheet userProfileBottomSheet;
    private Emitter.Listener addRequestedListner = args -> runOnUiThread(() -> {
        if (args[0] != null) {
            Log.d(TAG, "call: addRequested " + args[0].toString());
        }
    });
    private Emitter.Listener declineInviteListner = args -> runOnUiThread(() -> {
        if (args[0] != null) {
            Log.d(TAG, "call: declineInvite " + args[0].toString());
        }
    });
    private Emitter.Listener addParticipatedLiustner = args -> runOnUiThread(() -> {
        if (args[0] != null) {
            Log.d(TAG, "call: addParticipated " + args[0].toString());

        }

    });
    private Emitter.Listener lessParticiparedListner = args -> runOnUiThread(() -> {
        if (args[0] != null) {
            Log.d(TAG, "call: lessParticipated " + args[0].toString());
        }
    });
    private Emitter.Listener muteSeatListner = args -> runOnUiThread(() -> {
        if (args[0] != null) {
            Log.d(TAG, "call: muteSeat " + args[0].toString());
        }
    });
    private Emitter.Listener lockSeatListner = args -> runOnUiThread(() -> {
        if (args[0] != null) {
            Log.d(TAG, "call: lockSeat " + args[0].toString());
        }
    });
    private Emitter.Listener allSeatLockListner = args -> runOnUiThread(() -> {
        if (args[0] != null) {
            Log.d(TAG, "call: allSeatLock " + args[0].toString());
        }
    });
    private Emitter.Listener changeThemeListner = args -> {
        runOnUiThread(() -> {
            if (args[0] != null) {
                Log.d(TAG, "call: changetheme" + args[0]);
                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());

                    String image = jsonObject.getString("background");

                    Glide.with(WatchAudioLiveActivity.this).load(image).into(binding.mainImg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    };
    private Emitter.Listener inviteListner = args -> {

        Log.d(TAG, "callinviteListner11: " + args[0]);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (args[0] != null) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(args[0].toString());

                        Log.d(TAG, "call:inviteListner " + args[0]);

                        String name = jsonObject1.getString("name");
                        String image = jsonObject1.getString("image");
                        String id = jsonObject1.getString("userId");

                        if (id.equalsIgnoreCase(sessionManager.getUser().getId())) {
                            new PopupBuilder(WatchAudioLiveActivity.this).showPkRequestPopup("Audio request received from" + host.getName(), host.getImage(), "Accept", "Decline", new PopupBuilder.OnMultButtonPopupLister() {
                                @Override
                                public void onClickCountinue() {
                                    JsonObject jsonObject = new JsonObject();
                                    try {
                                        jsonObject.addProperty("position", jsonObject1.getInt("position"));
                                        jsonObject.addProperty("liveUserMongoId", host.getId());
                                        jsonObject.addProperty("userId", sessionManager.getUser().getId());
                                        jsonObject.addProperty("name", sessionManager.getUser().getName());
                                        jsonObject.addProperty("country", sessionManager.getUser().getCountry());
                                        jsonObject.addProperty("agoraUid", MY_UID);
                                        jsonObject.addProperty("image", sessionManager.getUser().getImage());

                                        getSocket().emit(Const.EVENT_ADD_PARTICIPATED, jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onClickCancel() {

                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        });

    };
    private Emitter.Listener seatListner = args -> {

        Log.d("onAudioVolumeIndication", ": seat listner fgggg      " + args[0]);

        if (args[0] != null) {
            Log.d("onAudioVolumeIndication", ": seat listner" + args[0]);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    Log.d("onAudioVolumeIndication", "initLister: usr sty1 " + data);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
                    Log.d("onAudioVolumeIndication", "initLister: usr sty2 " + mJson);
                    Gson gson = new Gson();

                    AudioLiveStreamRoot.LiveUser userData = gson.fromJson(mJson, AudioLiveStreamRoot.LiveUser.class);
                    host.setSeat(userData.getSeat());

                    bookedSeatItemList = userData.getSeat();
                    //  updateSeat(userData.getSeat());

                    seatAdapter.updateData(userData.getSeat());

//                    rtcEngine().muteLocalAudioStream(true);
//                    rtcEngine().enableLocalAudio(false);

                    for (int i = 0; i < userData.getSeat().size(); i++) {
                        if (userData.getSeat().get(i).getUserId() != null) {
                            coHostList.add(userData.getSeat().get(i));
                            if (userData.getSeat().get(i).getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
                                int kk = rtcEngine().muteLocalAudioStream(userData.getSeat().get(i).isMute());
                                rtcEngine().muteLocalAudioStream(userData.getSeat().get(i).isMute());
                                Log.e(TAG, "run: " + userData.getSeat().get(i).isMute());
                                Log.e(TAG, "run: kk" + kk);
                            }
                        }
//                        updateSeatFunction(userData.getSeat().get(i));
                    }
                }
            });
        }
    };

    private Emitter.Listener singleUserGetListner = args -> runOnUiThread(() -> {
        String data = args[0].toString();

        Log.d(TAG, "call:single user get " + args[0]);

        if (!data.isEmpty()) {
            AudioLiveStreamRoot.LiveUser liveUser = new Gson().fromJson(data.toString(), AudioLiveStreamRoot.LiveUser.class);
            Log.d(TAG, ": liveuser" + liveUser.toString());
        }

    });
    private Emitter.Listener blockedUsersListner = args -> {
        Log.d(TAG, "blockedUsersListner: " + args[0].toString());
        runOnUiThread(() -> {
            if (args[0] != null) {
                Object data = args[0];
                try {
                    JSONObject jsonObject = new JSONObject(data.toString());
                    JSONArray blockedList = jsonObject.getJSONArray("blocked");
                    for (int i = 0; i < blockedList.length(); i++) {
                        Log.d(TAG, "block user : " + blockedList.get(i).toString());
                        if (blockedList.get(i).toString().equals(sessionManager.getUser().getId())) {
                            Toast.makeText(WatchAudioLiveActivity.this, "You are blocked by host", Toast.LENGTH_SHORT).show();
                            new Handler(Looper.myLooper()).postDelayed(() -> endLive(), 500);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    };
    private boolean isHost = false;


    private void scrollAdapterLogic() {
        if (binding.rvComments.canScrollVertically(1)) {
        } else {
            binding.rvComments.scrollToPosition(0);
        }
    }

//    private void updateSeat(List<SeatItem> jsonArray) {
//        try {
//            setImage(jsonArray.get(0), binding.image0, binding.image01);
//            setImage(jsonArray.get(1), binding.image1, binding.image11);
//            setImage(jsonArray.get(2), binding.image2, binding.image21);
//            setImage(jsonArray.get(3), binding.image3, binding.image31);
//            setImage(jsonArray.get(4), binding.image4, binding.image41);
//            setImage(jsonArray.get(5), binding.image5, binding.image51);
//            setImage(jsonArray.get(6), binding.image6, binding.image61);
//            setImage(jsonArray.get(7), binding.image7, binding.image71);
//            setImage(jsonArray.get(8), binding.image8, binding.image81);
//            setImage(jsonArray.get(9), binding.image9, binding.image91);
//        } catch (Exception o) {
//            o.printStackTrace();
//        }
//
//    }

    private void updateSeatFunction(SeatItem seat) {

//        becomeHost();
        if (seat.isMute()) {

            if (seat.getUserId() != null && seat.getUserId().equalsIgnoreCase(sessionManager.getUser().getId()))
                rtcEngine().muteLocalAudioStream(seat.isMute());
            rtcEngine().enableLocalAudio(false);
            Toast.makeText(this, "muted", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_audio_live);
        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new WatchLiveViewModel()).createFor()).get(WatchLiveViewModel.class);

        sessionManager = new SessionManager(this);
        binding.setViewModel(viewModel);
        viewModel.initLister();
        giftViewModel.getGiftCategory();

        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.DATA);
        boolean isNotification = intent.getBooleanExtra(Const.isNotification, false);

        if (isNotification) {
            ((MainApplication) getApplication()).initAgora(WatchAudioLiveActivity.this);
        }

        if (userStr != null && !userStr.isEmpty()) {
            host = new Gson().fromJson(userStr, LiveUserRoot.UsersItem.class);
            token = host.getToken();

            initSoketIo(host.getLiveStreamingId(), false);
            getSocket().on(Socket.EVENT_CONNECT, args -> {
                getSocket().on(Const.EVENT_COMMENT, commentListner);
                getSocket().on(Const.EVENT_GIFT, giftListner);
                getSocket().on(Const.EVENT_VIEW, viewListner);
                getSocket().on(Const.EVENT_BLOCK, blockedUsersListner);
                getSocket().on(Const.EVENT_ADD_REQUESTED, addRequestedListner);
                getSocket().on(Const.EVENT_DECLINEiNVITE, declineInviteListner);
                getSocket().on(Const.EVENT_ADD_PARTICIPATED, addParticipatedLiustner);
                getSocket().on(Const.EVENT_LESS_PARTICIPATED, lessParticiparedListner);
                getSocket().on(Const.EVENT_MUTESEAT, muteSeatListner);
                getSocket().on(Const.EVENT_LOCK_SEAT, lockSeatListner);
                getSocket().on(Const.EVENT_ALL_SEAT_LOCK, allSeatLockListner);
                getSocket().on(Const.EVENT_CHANGE_THEME, changeThemeListner);
                getSocket().on(Const.EVENT_SEAT, seatListner);
                getSocket().on(Const.EVENT_INVITE, inviteListner); /// addrequest emit thay tyare invite listne thashe

                Log.d(TAG, "onCreate: live send");
                //  addLessView(true);
            });

            Glide.with(this).load(host.getImage())
                    .apply(MainApplication.requestOptions)
                    .circleCrop().into(binding.imgProfile);
            binding.tvCountry.setText(String.valueOf(host.getCountry()));

            if (host.getCountry() == null || host.getCountry().isEmpty()) {
                binding.tvCountry.setVisibility(View.GONE);
            }

            binding.tvRcoins.setText(String.valueOf(host.getRCoin()));
            binding.tvName.setText(host.getName());
            binding.tvUserId.setText(host.getUniqueId());

            seatAdapter = new SeatAdapter();
            binding.rvSeat.setAdapter(seatAdapter);

            Glide.with(WatchAudioLiveActivity.this).load(host.getBackground()).into(binding.mainImg);

            Log.d(TAG, "onCreate: " + host.getBackground());

            MY_UID = new Random().nextInt(500) + 2;

            initView();
            joinChannel();

            // becomeHost();
            initLister();

            addLessView(true);

//            bookedSeatItemList=host.getSeat();
//            updateSeat(bookedSeatItemList);

            seatAdapter.addData(host.getSeat());


            binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);


        }
    }

    private void becomeHost() {
        isHost = true;
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        rtcEngine().enableAudio();
        rtcEngine().disableVideo();
        Log.d(TAG, "becomeHost: ");
    }

    private void addLessView(boolean isAdd) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("liveStreamingId", host.getLiveStreamingId());
            jsonObject.put("liveUserMongoId", host.getId());
            jsonObject.put("userId", sessionManager.getUser().getId());
            jsonObject.put("isVIP", sessionManager.getUser().isIsVIP());
            jsonObject.put("image", sessionManager.getUser().getImage());
            jsonObject.put("name", sessionManager.getUser().getName());
            jsonObject.put("gender", sessionManager.getUser().getGender());
            jsonObject.put("country", sessionManager.getUser().getCountry());

            if (isAdd) {
                getSocket().emit(Const.EVENT_ADDVIEW, jsonObject);
            } else {
                Log.d(TAG, "addLessView: less view call   " + getSocket().connected());
                getSocket().emit(Const.EVENT_LESSVIEW, jsonObject);
//                getSocket().disconnect();
            }

            getSocket().on(Const.EVENT_VIEW, viewListner);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void joinChannel() {
        // Initialize token, extra info here before joining channel
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name and uid that
        // you use to generate this token.
        try {


            if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
                token = null; // default, no token
            }

            // Sets the channel profile of the Agora RtcEngine.
            // The Agora RtcEngine differentiates channel profiles and applies different optimization algorithms accordingly. For example, it prioritizes smoothness and low latency for a video call, and prioritizes video quality for a video broadcast.
            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().disableVideo();
            rtcEngine().enableAudioVolumeIndication(1000, 3, true); // atyare kon bole chhe ae detect karva mate

            // configVideo();
            Log.d("TAG", "joinChannel: " + config().getChannelName());
            rtcEngine().joinChannel(token, host.getChannel(), "", MY_UID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        emojiBottomsheetFragment = new EmojiBottomsheetFragment();
        userProfileBottomSheet = new UserProfileBottomSheet(this);

        if (rtcEngine() == null) {
            Log.d(TAG, "initView: rtc engine null");
            return;
        }
        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        isHost = false;

    }

    @Override
    public void onBackPressed() {
        endLive();
        super.onBackPressed();
    }

    private void endLive() {
        addLessView(false);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("position", selfPosition);
        jsonObject.addProperty("liveUserMongoId", host.getId());

        getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
        rtcEngine().disableAudio();
        rtcEngine().disableVideo();

        try {
            //removeRtcVideo(0, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //mVideoGridContainer.removeUserVideo(0, true);

        getSocket().disconnect();
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statsManager().clearAllData();
    }

    private void initLister() {

        binding.btnMute.setOnClickListener(v -> {
            viewModel.isMuted = !viewModel.isMuted;
            int kk = rtcEngine().muteLocalAudioStream(viewModel.isMuted);
            Log.e(TAG, "initLister: kkk  " + kk);
            if (viewModel.isMuted) {
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.mute));
                Toast.makeText(WatchAudioLiveActivity.this, "Muted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WatchAudioLiveActivity.this, "Unmuted", Toast.LENGTH_SHORT).show();
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(WatchAudioLiveActivity.this, R.drawable.unmute));
            }
        });

        viewModel.clickedComment.observe(this, user -> {
            getUser(user.getId());
        });
        viewModel.clickedUser.observe(this, user -> {
            try {
                getUser(user.get("userId").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        binding.lytHost.setOnClickListener(v -> getUser(host.getLiveUserId()));
        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(WatchAudioLiveActivity.this, "You not have enough diamonds to send gift", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("senderUserId", sessionManager.getUser().getId());
                    jsonObject.put("receiverUserId", host.getLiveUserId());
                    jsonObject.put("liveStreamingId", host.getLiveStreamingId());
                    jsonObject.put("userName", sessionManager.getUser().getName());
                    jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                    jsonObject.put("gift", new Gson().toJson(giftItem));
                    getSocket().emit(Const.EVENT_NORMALUSER_GIFT, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.imggift.setOnClickListener(v -> {
            emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
        });

        seatAdapter.setOnSeatClick(new SeatAdapter.onSeatClick() {
            @Override
            public void OnClickSeat(SeatItem seatItem, int position) {
                Log.d(TAG, "OnClickSeat: ");
                doWork(seatItem, position);
                selfPosition = position;
            }

            @Override
            public void onMuteClick(SeatItem seatItem, int positon) {
                Log.d(TAG, "onMuteClick: ");
                viewModel.isMuted = !viewModel.isMuted;
                rtcEngine().muteLocalAudioStream(!viewModel.isMuted);
//                rtcEngine().enableLocalAudio(true);
            }

            @Override
            public void onUnmuteClick(SeatItem seatItem, int position) {
                Log.d(TAG, "onUnmuteClick: ");
                viewModel.isMuted = !viewModel.isMuted;
                rtcEngine().muteLocalAudioStream(!viewModel.isMuted);
//                rtcEngine().enableLocalAudio(true);
            }

            @Override
            public void onReserved(SeatItem seatItem, int position) {
                Log.d(TAG, "onReserved: ");
                becomeHost();
                selfPosition = position;
                //todo host user ne seat aape ae aya set karvanu baki

            }

            @Override
            public void onRedervedFalse(SeatItem seatItem, int position) {
                Log.d(TAG, "onRedervedFalse: ");
                rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                rtcEngine().disableAudio();
                rtcEngine().disableVideo();
                isHost = false;
            }

            @Override
            public void onSpeakingTrue(SeatItem seatItem, int pos, ItemSeatBinding binding) {
                Log.e(TAG, "onSpeakingTrue: watch " + seatItem.isSpeaking());
                if (seatItem.isSpeaking() && seatItem.getUserId() != null && seatItem.getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
                    binding.animationView1.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.animationView1.setVisibility(View.GONE);
                        }
                    }, 3000);

                }
            }


        });

    }

    private void doWork(SeatItem seatItem, int i) {
        Log.d(TAG, "doWork: isReseved " + seatItem.isReserved());

        if (seatItem.isReserved() && seatItem.getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
            new PopupBuilder(WatchAudioLiveActivity.this).showRemovePopup(() -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", host.getId());

                getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);
                Log.d(TAG, "doWork: become audence");
                rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE);
                //rtcEngine().disableAudio();
                rtcEngine().disableVideo();

                isHost = false;

            });
            return;
        }


        if (seatItem.isReserved()) {
            Toast.makeText(this, "Please Choose Onother Seat", Toast.LENGTH_SHORT).show();
        } else if (seatItem.isLock()) {
            Toast.makeText(this, "This seat is Locked by host", Toast.LENGTH_SHORT).show();
        } else {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("position", i);
            jsonObject.addProperty("liveUserMongoId", host.getId());
            jsonObject.addProperty("userId", sessionManager.getUser().getId());
            jsonObject.addProperty("name", sessionManager.getUser().getName());
            jsonObject.addProperty("country", sessionManager.getUser().getCountry());
            jsonObject.addProperty("agoraUid", MY_UID);
            jsonObject.addProperty("image", sessionManager.getUser().getImage());

            getSocket().emit(Const.EVENT_ADD_PARTICIPATED, jsonObject);
            Log.d(TAG, "doWork: add partiicpate emit " + jsonObject);

            becomeHost();
        }
    }


    private void getUser(String userId) {
        getSocket().on(Const.EVENT_GET_USER, args1 -> {
            runOnUiThread(() -> {
                if (args1[0] != null) {
                    String data = args1[0].toString();
//                    Log.d(TAG, "initLister: usr sty1 " + data);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
//                    Log.d(TAG, "initLister: usr sty2 " + mJson);
                    Gson gson = new Gson();
                    GuestProfileRoot.User userData = gson.fromJson(mJson, GuestProfileRoot.User.class);

                    if (userData != null) {
                        if (userData.getUserId().equals(host.getLiveUserId())) {
                            userProfileBottomSheet.show(false, userData, host.getLiveStreamingId());
                        } else {
                            userProfileBottomSheet.show(false, userData, "");
                        }
                    }
                }
            });
            getSocket().off(Const.EVENT_GET_USER);
        });
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
            getSocket().emit(Const.EVENT_GET_USER, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            binding.etComment.setText("");
            LiveStramComment liveStramComment = new LiveStramComment(host.getLiveStreamingId(), comment, sessionManager.getUser(), false);
            getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("liveStreamingId", host.getLiveStreamingId());
//                jsonObject.put("comment", new Gson().toJson(liveStramComment));
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void onclickShare(View view) {


        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("Watch Live Video")
                .setContentDescription("By : " + host.getName())
                .setContentImageUrl(host.getImage())
                .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(host)));

        LinkProperties lp = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")

                .addControlParameter("", "")
                .addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(this, lp, (url, error) -> {
            Log.d(TAG, "initListnear: branch url" + url);
            try {
                Log.d(TAG, "initListnear: share");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = url;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                Log.d(TAG, "initListnear: " + e.getMessage());
                //e.toString();
            }
        });
    }


    @Override
    public void onErr(int err) {
        Log.d(TAG, "onErr: " + err);
    }

    @Override
    public void onConnectionLost() {
        Log.d(TAG, "onConnectionLost: ");
    }

    @Override
    public void onVideoStopped() {
        Log.d(TAG, "onVideoStopped: ");
    }


//    public void onclickGiftIcon(View view) {
//        emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
//    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
//        runOnUiThread(() -> {
//            isVideoDecoded = true;
//            renderRemoteUser(uid);
//            addLessView(true);
//        });
    }

    private void renderRemoteUser(int uid) {
        Log.d(TAG, "renderRemoteUser: ");
//        SurfaceView surface = prepareRtcVideo(uid, false);
//        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
        LiveStramComment liveStramComment = new LiveStramComment(host.getLiveStreamingId(), "", sessionManager.getUser(), true);
        getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
        addLessView(true);
//        try {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("liveStreamingId", host.getLiveStreamingId());
//            jsonObject.put("comment", new Gson().toJson(liveStramComment));
//            getSocket().emit(Const.EVENT_COMMENT, jsonObject);
//
//            addLessView(true);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
//        mVideoGridContainer.removeUserVideo(uid, false);
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.d(TAG, "onLeaveChannel: stts" + stats);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess: ");
        this.uuid = uid;

//      runOnUiThread((() -> {
//          new Handler().postDelayed(() -> {
//              if (isVideoDecoded) {
//                  Log.d(TAG, "sssss=- run: yreeeeeeehhhhh  video decoded");
//              } else {
//                  Toast.makeText(WatchAudioLiveActivity.this, "Somwthing went wrong", Toast.LENGTH_SHORT).show();
//                  endLive();
//              }
//          }, 5000);
//      }));

    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline: " + uid + " reason" + reason);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // removeRemoteUser(uid);
                if (uid == 1) {
                    endLive();
                }
            }
        });
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        Log.d(TAG, "onUserJoined: " + uid + "  elapsed" + elapsed);
    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
        if (!statsManager().isEnabled()) return;


    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        if (!statsManager().isEnabled()) return;

    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
        if (!statsManager().isEnabled()) return;

        StatsData data = statsManager().getStatsData(uid);
        if (data == null) return;

        data.setSendQuality(statsManager().qualityToString(txQuality));
        data.setRecvQuality(statsManager().qualityToString(rxQuality));
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
        if (!statsManager().isEnabled()) return;

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setAudioNetDelay(stats.networkTransportDelay);
        data.setAudioNetJitter(stats.jitterBufferDelay);
        data.setAudioLoss(stats.audioLossRate);
        data.setAudioQuality(statsManager().qualityToString(stats.quality));
    }

    @Override
    public void onFirstLocalAudioFramePublished(int elapsed) {

    }

    @Override
    public void onFirstRemoteAudioFrame(int uid, int elapsed) {

    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {

    }

    @Override
    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        runOnUiThread(() -> {
            if (totalVolume <= 0) return;

            for (int i = 0; i < speakers.length; i++) {
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: uid " + speakers[i].uid);
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: channelid" + speakers[i].channelId);
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: volumne" + speakers[i].volume);
                Log.d("onAudioVolumeIndication", "onAudioVolumeIndication: vad" + speakers[i].vad);


//                if (speakers[i].uid == 0 && bookedSeatItemList.get(selfPosition).isReserved()) {
//                    //animateLay(selfPosition);
//                    //todo lottie fervvani baki
//                }

                if (speakers[i].volume > 10) {
                    Log.e(TAG, "onAudioVolumeIndication: >>>>>>>  " + speakers[i].uid + "   " + speakers[i]);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("liveUserMongoId", host.getId());
                    jsonObject.addProperty("agoraUID", speakers[i].uid);   //TODO  speakers[i].uid check karva 37 lidhi 6
                    jsonObject.addProperty("isSpeaking", true);

                    getSocket().emit(Const.Speaking, jsonObject);

                    Log.e(TAG, "onAudioVolumeIndication:  true  ?" + jsonObject.toString());

                    String uid = String.valueOf(speakers[i].uid);

                    uidlist.add(uid);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: falseemit ");
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("liveUserMongoId", host.getId());
                            jsonObject.addProperty("agoraUid", uid);
                            jsonObject.addProperty("isspeaking", false);

                            getSocket().emit(Const.Speaking, jsonObject);
                            Log.e(TAG, "onAudioVolumeIndication:  false ? " + jsonObject.toString());

                            if (uidlist != null && !uidlist.isEmpty() && uidlist.size() > 0) {
                                uidlist.remove(uid);
                            }
                        }
                    }, 3000);
                }
            }
        });
    }

//    private void animateLay(int selfPosition) {
//        switch (selfPosition) {
//            case 0:
//                binding.animationView1.setVisibility(View.VISIBLE);
//                break;
//            case 1:
//                binding.animationView2.setVisibility(View.VISIBLE);
//                break;
//            case 2:
//                binding.animationView3.setVisibility(View.VISIBLE);
//                break;
//            case 3:
//                binding.animationView4.setVisibility(View.VISIBLE);
//                break;
//            case 4:
//                binding.animationView5.setVisibility(View.VISIBLE);
//                break;
//            case 5:
//                binding.animationView6.setVisibility(View.VISIBLE);
//                break;
//            case 6:
//                binding.animationView7.setVisibility(View.VISIBLE);
//                break;
//            case 7:
//                binding.animationView8.setVisibility(View.VISIBLE);
//                break;
//            case 8:
//                binding.animationView9.setVisibility(View.VISIBLE);
//                break;
//            case 9:
//                binding.animationView10.setVisibility(View.VISIBLE);
//                break;
//
//        }
//    }

    @Override
    public void onActiveSpeaker(int uid) {
        Log.d(TAG, "onActiveSpeaker: " + uid);
    }

    @Override
    public void onAudioMixingStateChanged(int state, int reason) {

    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();
    }

    public void onClickReport(View view) {
        new BottomSheetReport_g(this, host.getLiveUserId(), () -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.customtoastlyt));


            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

        });
    }
}