package com.example.rayzi.activity;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ActivityFakeWatchLiveBinding;
import com.example.rayzi.emojifake.FakeEmojiBottomsheetFragment;
import com.example.rayzi.modelclass.LiveStramComment;
import com.example.rayzi.modelclass.LiveUserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.example.rayzi.viewModel.WatchLiveViewModel;
import com.example.rayzi.z_demo.Demo_contents;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.gson.Gson;

import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class FakeWatchLiveActivity extends AppCompatActivity {
    private static final String TAG = "fakewatch";
    ActivityFakeWatchLiveBinding binding;
    LiveUserRoot.UsersItem fakeHost;
    String videoURL;
    SessionManager sessionManager;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewModel.liveStramCommentAdapter.addSingleComment(Demo_contents.getLiveStreamComment().get(0));
            binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);
            handler.postDelayed(this, 2000);
        }
    };
    Handler handler = new Handler();
    private EmojiSheetViewModel giftViewModel;
    private SimpleExoPlayer player;
    private WatchLiveViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fake_watch_live);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new WatchLiveViewModel()).createFor()).get(WatchLiveViewModel.class);
        giftViewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        sessionManager = new SessionManager(this);
        binding.setViewModel(viewModel);
        viewModel.initLister();


        Intent intent = getIntent();
        String userStr = intent.getStringExtra(Const.DATA);
        sessionManager = new SessionManager(this);
        Log.e(TAG, "onCreate: >>>>>>>>>>>> " + sessionManager.getUser().isFake());
        if (userStr != null && !userStr.isEmpty()) {

            Toast.makeText(this, "open", Toast.LENGTH_SHORT).show();
            fakeHost = new Gson().fromJson(userStr, LiveUserRoot.UsersItem.class);
            Log.d(TAG, "onCreate: fakeho==========" + fakeHost.toString());
            videoURL = fakeHost.getLink();
            Log.d(TAG, "onCreate: link==================== " + fakeHost.getLink());
            Log.e(TAG, "onCreate: " + fakeHost.getLiveStreamingId());
            initView();
        }
        viewModel.liveStramCommentAdapter.addSingleComment(new LiveStramComment("", "", Demo_contents.getUsers(true).get(0), true));
        handler.postDelayed(runnable, 2000);
        initlistener();
    }

    private void initlistener() {
        binding.imggift2.setOnClickListener(v -> {
            new FakeEmojiBottomsheetFragment((binding1, giftRoot, giftCount) -> {
                Log.e(TAG, "initView: >>>>>>>>>>>>>>>>> ");
                Glide.with(this).load(giftRoot.getUrl()).into(binding.imgGift);
                new Handler(Looper.getMainLooper()).postDelayed(() -> binding.imgGift.setImageDrawable(null), 2000);
            }).show(getSupportFragmentManager(), "emojifragfmetn");
        });
    }

    private void initView() {
        Log.e(TAG, "onCreate: " + fakeHost.toString());
        setPlayer();
        binding.tvName.setText(fakeHost.getName());
        binding.tvCoins.setText(String.valueOf(fakeHost.getRCoin()));
        binding.tvUserId.setText("ID: " + fakeHost.getId());
        binding.tvGifts.setText(String.valueOf(Demo_contents.getRandomPostCoint()));
        Glide.with(this).load(fakeHost.getImage()).circleCrop().placeholder(R.drawable.ic_user_place).into(binding.imgProfile);

        Log.i(TAG, "initView: " + fakeHost.getImage());
        LiveStramComment liveStramComment = new LiveStramComment("", "", sessionManager.getUser(), true);
        viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
        binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);

    }

    private void setPlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        binding.playerview.setPlayer(player);
        //  binding.playerview.setShowBuffering(true);
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
                        Toast.makeText(FakeWatchLiveActivity.this, "Live Ended!", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> finish(), 2000);
                        Log.d(TAG, "end: " + uri);
                        break;
                    case STATE_IDLE:
                        Log.d(TAG, "idle: " + uri);

                        break;

                    case STATE_READY:


                        Log.d(TAG, "ready: " + uri);

                        break;
                    default:
                        break;
                }
            }
        });
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }


    public void onClickBack(View view) {
        onBackPressed();
    }

    public void onClickSendComment(View view) {
        String comment = binding.etComment.getText().toString();
        if (!comment.isEmpty()) {
            LiveStramComment liveStramComment = new LiveStramComment(fakeHost.getLiveStreamingId(), comment, sessionManager.getUser(), false);
            viewModel.liveStramCommentAdapter.addSingleComment(liveStramComment);
            binding.rvComments.scrollToPosition(viewModel.liveStramCommentAdapter.getItemCount() - 1);
            binding.etComment.setText("");
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (player != null) {
//            player.setPlayWhenReady(false);
//        } else {
//
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (player != null) {
//            player.setPlayWhenReady(true);
//        } else {
//
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (player != null) {
            player.release();
        }

    }

    public void onclickShare(View view) {

        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("Watch Live Video")
                .setContentDescription("By : " + fakeHost.getName())
                .setContentImageUrl(fakeHost.getImage())
                .setContentMetadata(new ContentMetadata().addCustomMetadata("type", "LIVE").addCustomMetadata(Const.DATA, new Gson().toJson(fakeHost)));

        LinkProperties lp = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")
                .addControlParameter("", "")
                .addControlParameter("", Long.toString(Calendar.getInstance().getTimeInMillis()));

        buo.generateShortUrl(this, lp, (url, error) -> {

            try {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareMessage = url;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));

            } catch (Exception e) {

            }

        });
    }


}