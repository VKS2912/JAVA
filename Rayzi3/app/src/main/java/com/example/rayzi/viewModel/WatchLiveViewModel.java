package com.example.rayzi.viewModel;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rayzi.adapter.LiveViewAdapter;
import com.example.rayzi.liveStreamming.LiveStramCommentAdapter;
import com.example.rayzi.liveStreamming.LiveViewUserAdapter;
import com.example.rayzi.modelclass.UserRoot;

import org.json.JSONObject;

public class WatchLiveViewModel extends ViewModel {
    public CameraX.LensFacing lensFacing = CameraX.LensFacing.FRONT;
    public PreviewConfig.Builder builder;
    public PreviewConfig previewConfig;
    public Preview preview;
    public VideoCaptureConfig.Builder builder1;
    public VideoCaptureConfig videoCaptureConfig;
    public VideoCapture videoCapture;

    public boolean isMuted = false;

    public LiveViewUserAdapter liveViewUserAdapter = new LiveViewUserAdapter();
    public LiveStramCommentAdapter liveStramCommentAdapter = new LiveStramCommentAdapter();
    public MutableLiveData<UserRoot.User> clickedComment = new MutableLiveData<>();
    public MutableLiveData<JSONObject> clickedUser = new MutableLiveData<>();

    public LiveViewAdapter liveViewAdapter = new LiveViewAdapter();


    public void initLister() {
        liveStramCommentAdapter.setOnCommentClickListner((UserRoot.User userDummy) -> {
            clickedComment.setValue(userDummy);
        });
        liveViewUserAdapter.setOnLiveUserAdapterClickLisnter((JSONObject userDummy) -> clickedUser.setValue(userDummy));

        liveViewAdapter.setOnLiveUserAdapterClickLisnter(userDummy -> {
            clickedUser.setValue(userDummy);
        });

    }

}
