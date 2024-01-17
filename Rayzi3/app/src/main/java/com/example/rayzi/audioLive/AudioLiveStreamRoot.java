package com.example.rayzi.audioLive;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AudioLiveStreamRoot {

    @SerializedName("liveUser")
    private LiveUser liveUser;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public LiveUser getLiveUser() {
        return liveUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class LiveUser {

        @SerializedName("country")
        private String country;

        @SerializedName("image")
        private String image;

        @SerializedName("rCoin")
        private int rCoin;

        @SerializedName("channel")
        private String channel;

        @SerializedName("agoraUID")
        private int agoraUID;

        @SerializedName("liveStreamingId")
        private String liveStreamingId;

        @SerializedName("isVIP")
        private boolean isVIP;

        @SerializedName("token")
        private String token;

        @SerializedName("liveUserId")
        private String liveUserId;

        @SerializedName("seat")
        private List<SeatItem> seat;

        @SerializedName("createdAt")
        private String createdAt;


        @SerializedName("view")
        private int view;

        @SerializedName("diamond")
        private int diamond;

        @SerializedName("name")
        private String name;

        @SerializedName("isPublic")
        private boolean isPublic;
        @SerializedName("_id")
        private String id;

        @SerializedName("uniqueId")
        private String uniqueId;

        public String getUniqueId() {
            return uniqueId;
        }
        @SerializedName("background")
        private String background;

        @SerializedName("audio")
        private boolean audio;
        @SerializedName("age")
        private int age;
        @SerializedName("username")
        private String username;
        @SerializedName("updatedAt")
        private String updatedAt;

        @Override
        public String toString() {
            return "LiveUser{" +
                    "country='" + country + '\'' +
                    ", image='" + image + '\'' +
                    ", rCoin=" + rCoin +
                    ", channel='" + channel + '\'' +
                    ", agoraUID=" + agoraUID +
                    ", liveStreamingId='" + liveStreamingId + '\'' +
                    ", isVIP=" + isVIP +
                    ", token='" + token + '\'' +
                    ", liveUserId='" + liveUserId + '\'' +
                    ", seat=" + seat +
                    ", createdAt='" + createdAt + '\'' +
                    ", view=" + view +
                    ", diamond=" + diamond +
                    ", name='" + name + '\'' +
                    ", isPublic=" + isPublic +
                    ", id='" + id + '\'' +
                    ", background='" + background + '\'' +
                    ", audio=" + audio +
                    ", age=" + age +
                    ", username='" + username + '\'' +
                    ", updatedAt='" + updatedAt + '\'' +
                    '}';
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public String getCountry() {
            return country;
        }

        public String getImage() {
            return image;
        }

        public int getRCoin() {
            return rCoin;
        }

        public String getChannel() {
            return channel;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setAgoraUID(int agoraUID) {
            this.agoraUID = agoraUID;
        }

        public int getAgoraUID() {
            return agoraUID;
        }

        public String getLiveStreamingId() {
            return liveStreamingId;
        }

        public boolean isIsVIP() {
            return isVIP;
        }

        public String getToken() {
            return token;
        }

        public String getLiveUserId() {
            return liveUserId;
        }

        public List<SeatItem> getSeat() {
            return seat;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getView() {
            return view;
        }

        public int getDiamond() {
            return diamond;
        }

        public String getName() {
            return name;
        }

        public boolean isIsPublic() {
            return isPublic;
        }

        public String getId() {
            return id;
        }

        public boolean isAudio() {
            return audio;
        }

        public int getAge() {
            return age;
        }

        public String getUsername() {
            return username;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }


    }


}