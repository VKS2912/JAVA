package com.example.rayzi.viewModel;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.lifecycle.ViewModel;

public class FakeVideoCallViewModel extends ViewModel {
    public CameraX.LensFacing lensFacing = CameraX.LensFacing.FRONT;
    public PreviewConfig.Builder builder;
    public PreviewConfig previewConfig;
    public Preview preview;
    public VideoCaptureConfig.Builder builder1;
    public VideoCaptureConfig videoCaptureConfig;
    public VideoCapture videoCapture;


}
