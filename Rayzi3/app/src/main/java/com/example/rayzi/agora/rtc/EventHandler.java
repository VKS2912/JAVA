package com.example.rayzi.agora.rtc;

import io.agora.rtc.IRtcEngineEventHandler;

public interface EventHandler {
    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

    void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats);

    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserOffline(int uid, int reason);

    void onUserJoined(int uid, int elapsed);

    void onLastmileQuality(int quality);

    void onErr(int err);

    void onConnectionLost();

    void onVideoStopped();

    void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result);

    void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats);

    void onRtcStats(IRtcEngineEventHandler.RtcStats stats);

    void onNetworkQuality(int uid, int txQuality, int rxQuality);

    void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats);

    void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats);

    void onFirstLocalAudioFramePublished(int elapsed);

    void onFirstRemoteAudioFrame(int uid, int elapsed);

    void onUserMuteAudio(int uid, boolean muted);

    void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume);

    void onActiveSpeaker(int uid);

    void onAudioMixingStateChanged(int state, int reason);

}
