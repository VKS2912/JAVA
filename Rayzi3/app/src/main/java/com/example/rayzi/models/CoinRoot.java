package com.example.rayzi.models;

import com.google.gson.annotations.SerializedName;

public class CoinRoot {

    @SerializedName("senderUser")
    private SenderUser senderUser;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public SenderUser getSenderUser() {
        return senderUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class SenderUser {

        @SerializedName("isReferral")
        private boolean isReferral;

        @SerializedName("referralCount")
        private int referralCount;

        @SerializedName("country")
        private String country;

        @SerializedName("lastLogin")
        private String lastLogin;

        @SerializedName("isBlock")
        private boolean isBlock;

        @SerializedName("gender")
        private String gender;

        @SerializedName("rCoin")
        private int rCoin;

        @SerializedName("loginType")
        private int loginType;

        @SerializedName("link")
        private Object link;

        @SerializedName("channel")
        private String channel;

        @SerializedName("bio")
        private String bio;

        @SerializedName("isOnline")
        private boolean isOnline;

        @SerializedName("video")
        private int video;

        @SerializedName("withdrawalRcoin")
        private int withdrawalRcoin;

        @SerializedName("notification")
        private Notification notification;

        @SerializedName("spentCoin")
        private int spentCoin;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("analyticDate")
        private String analyticDate;

        @SerializedName("post")
        private int post;

        @SerializedName("identity")
        private String identity;

        @SerializedName("referralCode")
        private String referralCode;

        @SerializedName("plan")
        private Plan plan;

        @SerializedName("imageType")
        private int imageType;

        @SerializedName("fcmToken")
        private String fcmToken;

        @SerializedName("email")
        private String email;

        @SerializedName("updatedAt")
        private String updatedAt;

        @SerializedName("image")
        private String image;

        @SerializedName("isBusy")
        private boolean isBusy;

        @SerializedName("ad")
        private Ad ad;

        @SerializedName("level")
        private Level level;

        @SerializedName("ip")
        private String ip;

        @SerializedName("isVIP")
        private boolean isVIP;

        @SerializedName("token")
        private String token;

        @SerializedName("diamond")
        private int diamond;

        @SerializedName("followers")
        private int followers;

        @SerializedName("following")
        private int following;

        @SerializedName("name")
        private String name;

        @SerializedName("linkType")
        private int linkType;

        @SerializedName("_id")
        private String id;

        @SerializedName("isFake")
        private boolean isFake;

        @SerializedName("age")
        private int age;

        @SerializedName("username")
        private String username;

        public boolean isIsReferral() {
            return isReferral;
        }

        public int getReferralCount() {
            return referralCount;
        }

        public String getCountry() {
            return country;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public boolean isIsBlock() {
            return isBlock;
        }

        public String getGender() {
            return gender;
        }

        public int getRCoin() {
            return rCoin;
        }

        public int getLoginType() {
            return loginType;
        }

        public Object getLink() {
            return link;
        }

        public String getChannel() {
            return channel;
        }

        public String getBio() {
            return bio;
        }

        public boolean isIsOnline() {
            return isOnline;
        }

        public int getVideo() {
            return video;
        }

        public int getWithdrawalRcoin() {
            return withdrawalRcoin;
        }

        public Notification getNotification() {
            return notification;
        }

        public int getSpentCoin() {
            return spentCoin;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getAnalyticDate() {
            return analyticDate;
        }

        public int getPost() {
            return post;
        }

        public String getIdentity() {
            return identity;
        }

        public String getReferralCode() {
            return referralCode;
        }

        public Plan getPlan() {
            return plan;
        }

        public int getImageType() {
            return imageType;
        }

        public String getFcmToken() {
            return fcmToken;
        }

        public String getEmail() {
            return email;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getImage() {
            return image;
        }

        public boolean isIsBusy() {
            return isBusy;
        }

        public Ad getAd() {
            return ad;
        }

        public Level getLevel() {
            return level;
        }

        public String getIp() {
            return ip;
        }

        public boolean isIsVIP() {
            return isVIP;
        }

        public String getToken() {
            return token;
        }

        public int getDiamond() {
            return diamond;
        }

        public int getFollowers() {
            return followers;
        }

        public int getFollowing() {
            return following;
        }

        public String getName() {
            return name;
        }

        public int getLinkType() {
            return linkType;
        }

        public String getId() {
            return id;
        }

        public boolean isIsFake() {
            return isFake;
        }

        public int getAge() {
            return age;
        }

        public String getUsername() {
            return username;
        }
    }

    public static class Plan {

        @SerializedName("planId")
        private Object planId;

        @SerializedName("planStartDate")
        private Object planStartDate;

        public Object getPlanId() {
            return planId;
        }

        public Object getPlanStartDate() {
            return planStartDate;
        }
    }

    public static class Notification {

        @SerializedName("likeCommentShare")
        private boolean likeCommentShare;

        @SerializedName("newFollow")
        private boolean newFollow;

        @SerializedName("favoriteLive")
        private boolean favoriteLive;

        @SerializedName("message")
        private boolean message;

        public boolean isLikeCommentShare() {
            return likeCommentShare;
        }

        public boolean isNewFollow() {
            return newFollow;
        }

        public boolean isFavoriteLive() {
            return favoriteLive;
        }

        public boolean isMessage() {
            return message;
        }
    }

    public static class Level {

        @SerializedName("image")
        private String image;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("accessibleFunction")
        private AccessibleFunction accessibleFunction;

        @SerializedName("name")
        private String name;

        @SerializedName("_id")
        private String id;

        @SerializedName("coin")
        private int coin;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public AccessibleFunction getAccessibleFunction() {
            return accessibleFunction;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public int getCoin() {
            return coin;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }

    public static class Ad {

        @SerializedName("date")
        private Object date;

        @SerializedName("count")
        private int count;

        public Object getDate() {
            return date;
        }

        public int getCount() {
            return count;
        }
    }

    public static class AccessibleFunction {

        @SerializedName("uploadPost")
        private boolean uploadPost;

        @SerializedName("freeCall")
        private boolean freeCall;

        @SerializedName("uploadVideo")
        private boolean uploadVideo;

        @SerializedName("cashOut")
        private boolean cashOut;

        @SerializedName("liveStreaming")
        private boolean liveStreaming;

        public boolean isUploadPost() {
            return uploadPost;
        }

        public boolean isFreeCall() {
            return freeCall;
        }

        public boolean isUploadVideo() {
            return uploadVideo;
        }

        public boolean isCashOut() {
            return cashOut;
        }

        public boolean isLiveStreaming() {
            return liveStreaming;
        }
    }
}