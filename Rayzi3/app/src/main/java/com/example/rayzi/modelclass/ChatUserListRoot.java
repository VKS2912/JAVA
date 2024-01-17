package com.example.rayzi.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatUserListRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    @SerializedName("chatList")
    private List<ChatUserItem> chatList;

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public List<ChatUserItem> getChatList() {
        return chatList;
    }

    public static class ChatUserItem {

        @SerializedName("chatDate")
        private String chatDate;

        @SerializedName("isVIP")
        private boolean isVIP;

        public boolean isVIP() {
            return isVIP;
        }

        @SerializedName("country")
        private String country;
        @SerializedName("name")
        private String name;
        @SerializedName("image")
        private String image;
        @SerializedName("topic")
        private String topic;
        @SerializedName("time")
        private String time;
        @SerializedName("message")
        private String message;
        @SerializedName("userId")
        private String userId;
        @SerializedName("isFake")
        private boolean isFake;
        @SerializedName("link")
        private String link;

        @SerializedName("username")
        private String username;


        public boolean isFake() {
            return isFake;
        }

        public String getLink() {
            return link;
        }

        public String getCountry() {
            return country;
        }

        public String getImage() {
            return image;
        }

        public String getChatDate() {
            return chatDate;
        }

        public String getName() {
            return name;
        }

        public String getTopic() {
            return topic;
        }

        public String getTime() {
            if (time.trim().equals("0 minutes ago")) {
                return "Just Now";
            }
            return time;
        }

        public String getMessage() {
            return message;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "ChatUserItem{" +
                    "chatDate='" + chatDate + '\'' +
                    ", isVIP=" + isVIP +
                    ", country='" + country + '\'' +
                    ", name='" + name + '\'' +
                    ", image='" + image + '\'' +
                    ", topic='" + topic + '\'' +
                    ", time='" + time + '\'' +
                    ", message='" + message + '\'' +
                    ", userId='" + userId + '\'' +
                    ", isFake=" + isFake +
                    ", username='" + username + '\'' +
                    '}';
        }
    }

}