package com.example.rayzi.audioLive;

import com.google.gson.annotations.SerializedName;

public class SeatItem {
    @Override
    public String toString() {
        return "SeatItem{" +
                "image='" + image + '\'' +
                ", country='" + country + '\'' +
                ", reserved=" + reserved +
                ", name='" + name + '\'' +
                ", lock=" + lock +
                ", agoraUid=" + agoraUid +
                ", mute=" + mute +
                ", id='" + id + '\'' +
                ", position=" + position +
                ", invite=" + invite +
                ", userId='" + userId + '\'' +
                '}';
    }

    @SerializedName("image")
    private String image;

    @SerializedName("country")
    private String country;

    @SerializedName("reserved")
    private boolean reserved;

    @SerializedName("name")
    private String name;

    @SerializedName("lock")
    private boolean lock;

    @SerializedName("agoraUid")
    private int agoraUid;

    @SerializedName("mute")
    private boolean mute;

    @SerializedName("isSpeaking")
    private boolean isSpeaking;

    @SerializedName("_id")
    private String id;

    @SerializedName("position")
    private int position;

    @SerializedName("invite")
    private boolean invite;

    @SerializedName("userId")
    private String userId;

    public String getImage() {
        return image;
    }

    public String getCountry() {
        return country;
    }

    public boolean isReserved() {
        return reserved;
    }

    public String getName() {
        return name;
    }

    public boolean isLock() {
        return lock;
    }

    public int getAgoraUid() {
        return agoraUid;
    }

    public boolean isMute() {
        return mute;
    }

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public boolean isInvite() {
        return invite;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }


    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }
}
