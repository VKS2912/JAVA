package com.example.rayzi.audioLive;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.R;
import com.example.rayzi.RayziUtils;
import com.example.rayzi.SessionManager;
import com.example.rayzi.agora.AgoraBaseActivity;
import com.example.rayzi.agora.stats.RemoteStatsData;
import com.example.rayzi.agora.stats.StatsData;
import com.example.rayzi.agora.ui.RtcStatsView;
import com.example.rayzi.databinding.ActivityHostLiveAudioBinding;
import com.example.rayzi.databinding.BottomSheetOnlineProfileBinding;
import com.example.rayzi.databinding.ItemSeatBinding;
import com.example.rayzi.emoji.EmojiBottomsheetFragment;
import com.example.rayzi.liveStreamming.LiveSummaryActivity;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.popups.PopupBuilder;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.HostLiveViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HostLiveAudioActivity extends AgoraBaseActivity {

    public static final String TAG = "hostliveactivity";

    ActivityHostLiveAudioBinding binding;
    SessionManager sessionManager;
    JSONArray blockedUsersList = new JSONArray();
    JSONArray jsonArray;
    List<SeatItem> bookedSeatItemList = new ArrayList<>();
    SeatAdapter seatAdapter;

    EmojiBottomsheetFragment emojiBottomsheetFragment;

    int pos;
    private EmojiSheetViewModel giftViewModel;
    private AudioLiveStreamRoot.LiveUser liveUser;
    boolean isspeak = false;
    private HostLiveViewModel viewModel;
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
        Log.d(TAG, "call: muteSeat " + args[0].toString());
        if (args[0] != null) {
            Log.d(TAG, "call: muteSeat " + args[0].toString());
//            rtcEngine().muteLocalAudioStream(!viewModel.isMuted);
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

    private Emitter.Listener viewListner = new Emitter.Listener() {
        @Override
        public void call(Object... data) {
            HostLiveAudioActivity.this.runOnUiThread(() -> {
                Object args = data[0];
                Log.d(TAG, "viewListner : " + args.toString());

                try {
                    jsonArray = new JSONArray(args.toString());
                    JSONArray finalArray = new JSONArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getBoolean("isAdd")) {
                            finalArray.put(jsonObject);
                        }
                    }
                    viewModel.liveViewAdapter.addData(finalArray);
                    binding.tvViewUserCount.setText(String.valueOf(finalArray.length()));
                    Log.d(TAG, "views2 : " + jsonArray);
                    binding.tvNoOneJoined.setVisibility(jsonArray.length() > 0 ? View.GONE : View.VISIBLE);

                } catch (JSONException e) {
                    Log.d(TAG, "207: ");
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("blocked", blockedUsersList);
                    HostLiveAudioActivity.this.getSocket().emit(Const.EVENT_BLOCK, jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

        }
    };

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
//                        binding.rvComments.smoothScrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);
                    }

                }

            });

        }
    };
    private RtcStatsView rtcStatsView;
    private int uuid;
    private int userPosition, hostPosition;
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

//                                SVGAImageView imageView = binding.svgaImage;
//                                SVGAParser parser = new SVGAParser(WatchAudioLiveActivity.this);
//
//                                try {
//                                    parser.decodeFromURL(new URL(BuildConfig.BASE_URL + giftData.getImage()), new SVGAParser.ParseCompletion() {
//                                        @Override
//                                        public void onComplete(@NonNull SVGAVideoEntity svgaVideoEntity) {
//                                            SVGADrawable drawable = new SVGADrawable(svgaVideoEntity);
//                                            imageView.setImageDrawable(drawable);
//                                            imageView.startAnimation();
//                                        }
//
//                                        @Override
//                                        public void onError() {
//
//                                        }
//                                    }, new SVGAParser.PlayCallback() {
//                                        @Override
//                                        public void onPlay(@NonNull List<? extends File> list) {
//
//                                        }
//                                    });
//                                } catch (MalformedURLException e) {
//                                    e.printStackTrace();
//                                }
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
    private Emitter.Listener changeThemeListner = args -> {
        runOnUiThread(() -> {
            if (args[0] != null) {
                Log.d(TAG, "call: changetheme" + args[0]);

                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());

                    String image = jsonObject.getString("background");

                    Log.d(TAG, ":call changetheme " + image);

                    Glide.with(HostLiveAudioActivity.this).load(image).into(binding.mainImg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    };

    private Emitter.Listener seatListner = args -> {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (args[0] != null) {
                    Log.d("onAudioVolumeIndication", ": seat listner" + args[0]);

                    String data = args[0].toString();
                    Log.d("onAudioVolumeIndication", "initLister: usr sty1 " + data);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
                    Log.d("onAudioVolumeIndication", "initLister: usr sty2 " + mJson);
                    Gson gson = new Gson();

                    liveUser = gson.fromJson(mJson, AudioLiveStreamRoot.LiveUser.class);
                    bookedSeatItemList = liveUser.getSeat();
                    seatAdapter.updateData(liveUser.getSeat());


                    //todo first time seat set
                    // updateSeat(liveUser.getSeat());
                }
            }
        });
    };
    private int selfPosition = 0;


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

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
//    }


    private Emitter.Listener liveEndByEnd = args -> {
        if (args[0] != null) {
            runOnUiThread(() -> {

                removeRtcVideo(0, true);
//                mVideoGridContainer.removeUserVideo(0, true);

                PopupBuilder popupBuilder = new PopupBuilder(HostLiveAudioActivity.this);
                popupBuilder.showLiveEndPopup(() -> {

                    startActivity(new Intent(this, LiveSummaryActivity.class).putExtra(Const.DATA, liveUser.getLiveStreamingId()));
                    finish();
                    Toast.makeText(this, "End Live Video", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "liveEndByEnd: liveEndByEnd" + args[0].toString());

                });

            });
        }
    };

//    Handler handler = new Handler(Looper.myLooper());
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//
//            Call<UpdateLiveTime> call = RetrofitBuilder.create().updateLiveTime(sessionManager.getUser().getId());
//            call.enqueue(new Callback<UpdateLiveTime>() {
//                @Override
//                public void onResponse(Call<UpdateLiveTime> call, Response<UpdateLiveTime> response) {
//
//                    Log.e(TAG, "onResponse: " + response.body());
//
//                }
//
//                @Override
//                public void onFailure(Call<UpdateLiveTime> call, Throwable t) {
//
//                }
//            });
//
//            Log.e(TAG, "run: live================================");
//
//            handler.postDelayed(this, 60000);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_host_live_audio);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new HostLiveViewModel()).createFor()).get(HostLiveViewModel.class);
        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        sessionManager = new SessionManager(this);
        giftViewModel.getGiftCategory();
        binding.setViewModel(viewModel);

        emojiBottomsheetFragment = new EmojiBottomsheetFragment();

        binding.rvComments.smoothScrollToPosition(0);
        viewModel.initLister();

        seatAdapter = new SeatAdapter();
        binding.rvSeat.setAdapter(seatAdapter);


        Intent intent = getIntent();

        if (intent != null) {
            String data = intent.getStringExtra(Const.DATA);
            String privacy = intent.getStringExtra(Const.PRIVACY);

            if (data != null && !data.isEmpty()) {
                liveUser = new Gson().fromJson(data, AudioLiveStreamRoot.LiveUser.class);
                liveUser.setAgoraUID(1);
                binding.tvRcoins.setText(String.valueOf(liveUser.getRCoin()));

                Log.d(TAG, "onCreate: live room id " + liveUser.getLiveStreamingId());
                initSoketIo(liveUser.getLiveStreamingId(), true);

                bookedSeatItemList = liveUser.getSeat();

                seatAdapter.addData(liveUser.getSeat());
            }
        }


        rtcStatsView = findViewById(R.id.single_host_rtc_stats);
        initLister();
        joinChannel();
        startBroadcast();


        getSocket().on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
            getSocket().on(Const.LIVEENDBYEND, liveEndByEnd);
            getSocket().on(Const.EVENT_COMMENT, commentListner);
            getSocket().on(Const.EVENT_GIFT, giftListner);
            getSocket().on(Const.EVENT_VIEW, viewListner);
            getSocket().on(Const.EVENT_ADD_REQUESTED, addRequestedListner);
            getSocket().on(Const.EVENT_DECLINEiNVITE, declineInviteListner);
            getSocket().on(Const.EVENT_ADD_PARTICIPATED, addParticipatedLiustner);
            getSocket().on(Const.EVENT_LESS_PARTICIPATED, lessParticiparedListner);
            getSocket().on(Const.EVENT_MUTESEAT, muteSeatListner);
            getSocket().on(Const.EVENT_LOCK_SEAT, lockSeatListner);
            getSocket().on(Const.EVENT_ALL_SEAT_LOCK, allSeatLockListner);
            getSocket().on(Const.EVENT_CHANGE_THEME, changeThemeListner);
            getSocket().on(Const.EVENT_SEAT, seatListner);
        }));


        Log.d(TAG, "onCreate: host live " + liveUser.getBackground());

        Log.d(TAG, "onCreate: " + sessionManager.getInt(Const.BACKGROUND));

    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            binding.etComment.setText("");
            LiveStramComment liveStramComment = new LiveStramComment(liveUser.getLiveStreamingId(), comment, sessionManager.getUser(), false);
            getSocket().emit(Const.EVENT_COMMENT, new Gson().toJson(liveStramComment));
            hideKeyboard(HostLiveAudioActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        endLive();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void endLive() {
        new PopupBuilder(this).showReliteDiscardPopup(getString(R.string.exit), "", getString(R.string.yes), getString(R.string.cancel), new PopupBuilder.OnPopupClickListner() {
            @Override
            public void onClickCountinue() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("liveRoom", liveUser.getLiveStreamingId());
                    jsonObject.put("liveHostRoom", sessionManager.getUser().getId());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                getSocket().emit("liveHostEnd", jsonObject);
                startActivity(new Intent(HostLiveAudioActivity.this, LiveSummaryActivity.class).putExtra(Const.DATA, liveUser.getLiveStreamingId()));
                finish();
            }
        });


    }

    private void joinChannel() {
        try {
            rtcEngine().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            rtcEngine().joinChannel(liveUser.getToken(), liveUser.getChannel(), "", liveUser.getAgoraUID());
            rtcEngine().enableAudioVolumeIndication(1000, 3, true); // atyare kon bole chhe ae detect karva mate
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startBroadcast() {

        Log.d(TAG, "startBroadcast: ");
        try {
            rtcEngine().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
            rtcEngine().enableAudio();
            rtcEngine().disableVideo();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initLister() {

        binding.btnMute.setOnClickListener(v -> {
            viewModel.isMuted = !viewModel.isMuted;
            rtcEngine().muteLocalAudioStream(viewModel.isMuted);
            Log.e(TAG, "initLister: >>>>>>>>>>>>>  " + viewModel.isMuted);
            if (viewModel.isMuted) {
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(HostLiveAudioActivity.this, R.drawable.mute));
                Toast.makeText(HostLiveAudioActivity.this, "Muted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HostLiveAudioActivity.this, "Unmuted", Toast.LENGTH_SHORT).show();
                binding.btnMute.setImageDrawable(ContextCompat.getDrawable(HostLiveAudioActivity.this, R.drawable.unmute));
            }
        });

        binding.btnClose.setOnClickListener(v -> {
            endLive();
        });

        giftViewModel.finelGift.observe(this, giftItem -> {
            if (giftItem != null) {
                int totalCoin = giftItem.getCoin() * giftItem.getCount();
                if (sessionManager.getUser().getDiamond() < totalCoin) {
                    Toast.makeText(HostLiveAudioActivity.this, "You not have enough diamonds to send gift", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("senderUserId", sessionManager.getUser().getId());
                    jsonObject.put("receiverUserId", liveUser.getLiveUserId());
                    jsonObject.put("liveStreamingId", liveUser.getLiveStreamingId());
                    jsonObject.put("userName", sessionManager.getUser().getName());
                    jsonObject.put("coin", giftItem.getCoin() * giftItem.getCount());
                    jsonObject.put("gift", new Gson().toJson(giftItem));
                    getSocket().emit(Const.EVENT_NORMALUSER_GIFT, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        seatAdapter.setOnSeatClick(new SeatAdapter.onSeatClick() {
            @Override
            public void OnClickSeat(SeatItem seatItem, int position) {
                doWork(seatItem, position);
                selfPosition = position;
            }

            @Override
            public void onMuteClick(SeatItem seatItem, int positon) {
                viewModel.isMuted = !viewModel.isMuted;
                rtcEngine().muteLocalAudioStream(!viewModel.isMuted);
            }

            @Override
            public void onUnmuteClick(SeatItem seatItem, int position) {
                viewModel.isMuted = !viewModel.isMuted;
                rtcEngine().muteLocalAudioStream(viewModel.isMuted);
            }

            @Override
            public void onReserved(SeatItem seatItem, int position) {
            }

            @Override
            public void onRedervedFalse(SeatItem seatItem, int position) {
            }

            @Override
            public void onSpeakingTrue(SeatItem seatItem, int pos, ItemSeatBinding binding) {
                Log.e(TAG, "onSpeakingTrue:  host >>>>>>>>>  " + seatItem.isSpeaking() + "  " + isspeak);

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
                Log.e(TAG, "onSpeakingTrue: >>>>>   " + pos);
            }


        });

        binding.options.setOnClickListener(v -> {
            new BottomSheetTheme(HostLiveAudioActivity.this, new BottomSheetTheme.OnClickListner() {
                @Override
                public void onGalleryClick() {
                    new BottomSheetOtions(HostLiveAudioActivity.this, image -> {
                        Dialog dialog = new Dialog(HostLiveAudioActivity.this);
                        dialog.setContentView(R.layout.switch_popup);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        Button sure = dialog.findViewById(R.id.sure);
                        Button cancel = dialog.findViewById(R.id.cancel);

                        sure.setOnClickListener(v1 -> {

                            Glide.with(HostLiveAudioActivity.this).load(BuildConfig.BASE_URL + image).into(binding.mainImg);
                            dialog.dismiss();

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("liveUserMongoId", liveUser.getId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                jsonObject.put("background", image);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

//                            JsonObject jsonObject = new JsonObject();
//                            jsonObject.addProperty("liveUserMongoId", liveUser.getId());
//                            jsonObject.addProperty("background", image);
                            getSocket().emit(Const.EVENT_CHANGE_THEME, jsonObject);

                            Log.d(TAG, "onGalleryClick: " + jsonObject.toString());

                        });

//                        sessionManager.saveInt(Const.BACKGROUND, integer);

                        cancel.setOnClickListener(v1 -> dialog.dismiss());

                        dialog.show();

                    });

                }

                @Override
                public void onCancelClick() {

                }

            });

        });

    }

    private void doWork(SeatItem seatItem, int i) {
        Log.e(TAG, "doWork: >>>>>>>>>>  " + i);

        if (seatItem.isReserved() && seatItem.getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
            new PopupBuilder(this).showRemovePopup(() -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());

                getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

                Log.d(TAG, "doWork: remove sit by it self" + jsonObject.toString());

            });
            return;
        }

        if (seatItem.isReserved()) {
            getUser(seatItem.getUserId(), i, seatItem);
            return;
        }

        new BottomSheetHostMic(HostLiveAudioActivity.this, seatItem, new BottomSheetHostMic.OnClickListner() {
            @Override
            public void onTakeMic() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                jsonObject.addProperty("name", sessionManager.getUser().getName());
                jsonObject.addProperty("country", sessionManager.getUser().getCountry());
                jsonObject.addProperty("agoraUid", liveUser.getAgoraUID());
                jsonObject.addProperty("image", sessionManager.getUser().getImage());
                getSocket().emit(Const.EVENT_ADD_PARTICIPATED, jsonObject);
                hostPosition = i;

            }

            @Override
            public void onGiveMic() {
                if (jsonArray != null && jsonArray.length() > 0) {
                    Log.e(TAG, "onGiveMic: Click>>>>>>>>>>>>>>>>>>>>>> if condition");
                    Log.e(TAG, "onGiveMic: Click>>>>>>>>>>>>>>>>>>>>>> if condition          " + jsonArray);

                    new BottomSheetViewersUsers(HostLiveAudioActivity.this, jsonArray, userDummy -> {
                        try {
                            Log.e(TAG, "onGiveMic: >>>>>>>>>>>>>>>try catch");
                            getUser(userDummy.get("userId").toString(), i, seatItem);
                            userPosition = i;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Toast.makeText(HostLiveAudioActivity.this, "You haven't ny user to give mic", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLockMic() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("lock", !seatItem.isLock());

                getSocket().emit(Const.EVENT_LOCK_SEAT, jsonObject);
            }

            @Override
            public void onMuteMic() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("mute", !seatItem.isMute());

                getSocket().emit(Const.EVENT_MUTESEAT, jsonObject);
            }

            @Override
            public void onCancelClick() {

            }

            @Override
            public void onClickRemove() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", i);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());

                getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

            }
        });
    }

    public void onclickGiftIcon(View view) {
        emojiBottomsheetFragment.show(getSupportFragmentManager(), "emojifragfmetn");
    }

    private void getUser(String userId, int postion, SeatItem seatItem) {
        getSocket().on(Const.EVENT_GET_USER, args1 -> {
            runOnUiThread(() -> {
                if (args1[0] != null) {

                    String data = args1[0].toString();
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(data);
                    Gson gson = new Gson();

                    GuestProfileRoot.User userData = gson.fromJson(mJson, GuestProfileRoot.User.class);

                    if (userData != null) {
                        doUserTask(userData, postion, seatItem);
                    }
                }
            });

            getSocket().off(Const.EVENT_GET_USER);
        });

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromUserId", sessionManager.getUser().getId());
            jsonObject.put("toUserId", userId);
            Log.d(TAG, "getUser:request  " + jsonObject);
            getSocket().emit(Const.EVENT_GET_USER, jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doUserTask(GuestProfileRoot.User userData, int postion, SeatItem seatItem) {

        new BottomSheetViewersUserProfile(this, seatItem, userData, new BottomSheetViewersUserProfile.OnClickListner() {
            @Override
            public void onUnMute(BottomSheetOnlineProfileBinding sheetDilogBinding) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", postion);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("mute", !seatItem.isMute());
                Log.e(TAG, "onUnMute: >>>>>>>>>>>>>>>>  " + !seatItem.isMute());
                getSocket().emit(Const.EVENT_MUTESEAT, jsonObject);


                if (seatItem.isMute()) {
                    sheetDilogBinding.txtMic.setText("Unmute mic");
                    Glide.with(HostLiveAudioActivity.this).load(R.drawable.speaker_off).into(sheetDilogBinding.mute);
                } else {
                    sheetDilogBinding.txtMic.setText("Mute mic");
                    Glide.with(HostLiveAudioActivity.this).load(R.drawable.speaker).into(sheetDilogBinding.mute);
                }
            }

            @Override
            public void onRemoveSeat() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", postion);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                jsonObject.addProperty("name", sessionManager.getUser().getName());
                jsonObject.addProperty("country", sessionManager.getUser().getCountry());
                jsonObject.addProperty("agoraUid", liveUser.getAgoraUID());
                jsonObject.addProperty("image", sessionManager.getUser().getImage());
                getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);

            }

            @Override
            public void onkickOut() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", postion);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("userId", sessionManager.getUser().getId());
                jsonObject.addProperty("name", sessionManager.getUser().getName());
                jsonObject.addProperty("country", sessionManager.getUser().getCountry());
                jsonObject.addProperty("agoraUid", liveUser.getAgoraUID());
                jsonObject.addProperty("image", sessionManager.getUser().getImage());

                getSocket().emit(Const.EVENT_LESS_PARTICIPATED, jsonObject);


                blockedUsersList.put(seatItem.getUserId());

                Log.d(TAG, "initLister: blocked " + blockedUsersList.toString());

                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("blocked", blockedUsersList);
                    getSocket().emit(Const.EVENT_BLOCK, jsonObject1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void inviteUser() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("position", postion);
                jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                jsonObject.addProperty("userId", userData.getUserId());
                jsonObject.addProperty("name", userData.getName());
                jsonObject.addProperty("country", userData.getCountry());
                jsonObject.addProperty("agoraUid", -1);
                jsonObject.addProperty("image", userData.getImage());

                for (int i = 0; i < bookedSeatItemList.size(); i++) {
                    if (!bookedSeatItemList.get(i).equals(userData.getUserId())) {
                        getSocket().emit(Const.EVENT_ADD_REQUESTED, jsonObject);
                    }
                }

                Log.e(TAG, "inviteUser: add participate........................");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        statsManager().clearAllData();
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        Log.d(TAG, "onLeaveChannel: stts " + stats);
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess: chanel " + channel + " uid" + uid + "  elapsed " + elapsed);
        this.uuid = uid;
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline: " + uid + " reason" + reason);

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
        Log.d(TAG, "onLocalVideoStats: ");
        if (!statsManager().isEnabled()) return;


    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
        runOnUiThread(() -> {
            if (rtcStatsView != null && rtcStatsView.getVisibility() == View.VISIBLE) {
                rtcStatsView.setLocalStats(stats.rxKBitRate,
                        stats.rxPacketLossRate, stats.txKBitRate,
                        stats.txPacketLossRate);
            }
        });


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

        RemoteStatsData data = (RemoteStatsData) statsManager().getStatsData(stats.uid);
        if (data == null) return;

        data.setWidth(stats.width);
        data.setHeight(stats.height);
        data.setFramerate(stats.rendererOutputFrameRate);
        data.setVideoDelay(stats.delay);
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

                if (speakers[i].volume > 10) {

                    Log.e("onAudioVolumeIndication", "onAudioVolumeIndication: >>>>>>>  " + speakers[i].uid + "   " + speakers[i]);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                    jsonObject.addProperty("agoraUID", speakers[i].uid);
                    jsonObject.addProperty("isSpeaking", true);

                    getSocket().emit(Const.Speaking, jsonObject);
                    Log.e("onAudioVolumeIndication", "onAudioVolumeIndication: >>>>>>>>>>>>  emit " + jsonObject.toString());
                    String uid = String.valueOf(speakers[i].uid);
                    List<String> uidlist = new ArrayList<>();
                    uidlist.add(uid);


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: falseemit ");
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("liveUserMongoId", liveUser.getId());
                            jsonObject.addProperty("agoraUid", uid);
                            jsonObject.addProperty("isspeaking", false);

                            getSocket().emit(Const.Speaking, jsonObject);
                            if (uidlist != null && !uidlist.isEmpty() && uidlist.size() > 0) {
                                uidlist.remove(uid);
                            }
                        }
                    }, 3000);

                }
            }

        });
    }

    @Override
    public void onActiveSpeaker(int uid) {
        Log.d(TAG, "onActiveSpeaker: " + uid);
    }

    @Override
    public void onAudioMixingStateChanged(int state, int reason) {

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
        Log.d(TAG, "onFirstLocalAudioFramePublished: " + elapsed);
    }

    @Override
    public void onFirstRemoteAudioFrame(int uid, int elapsed) {
        Log.d(TAG, "onFirstRemoteAudioFrame: " + uid);
    }

    @Override
    public void onUserMuteAudio(int uid, boolean muted) {
        Log.d(TAG, "onUserMuteAudio: " + uid);
    }

    @Override
    public void finish() {
        super.finish();
        statsManager().clearAllData();

    }


}