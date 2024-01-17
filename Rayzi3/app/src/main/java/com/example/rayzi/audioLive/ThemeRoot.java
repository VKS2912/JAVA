package com.example.rayzi.audioLive;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThemeRoot {

    @SerializedName("theme")
    private List<ThemeItem> theme;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private boolean status;

    public List<ThemeItem> getTheme() {
        return theme;
    }

    public String getMessage() {
        return message;
    }

    public boolean isStatus() {
        return status;
    }

    public static class ThemeItem {

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("theme")
        private String theme;

        @SerializedName("_id")
        private String id;

        @SerializedName("type")
        private int type;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public String getTheme() {
            return theme;
        }

        public String getId() {
            return id;
        }

        public int getType() {
            return type;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}