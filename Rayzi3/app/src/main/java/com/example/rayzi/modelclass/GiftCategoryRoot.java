package com.example.rayzi.modelclass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiftCategoryRoot {

    @SerializedName("message")
    private String message;

    @SerializedName("category")
    private List<CategoryItem> category;

    @SerializedName("status")
    private boolean status;

    public GiftCategoryRoot(List<CategoryItem> category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public List<CategoryItem> getCategory() {
        return category;
    }

    public boolean isStatus() {
        return status;
    }

    public static class CategoryItem {

        @SerializedName("image")
        private String image;

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("name")
        private String name;

        @SerializedName("giftCount")
        private int giftCount;

        @SerializedName("_id")
        private String id;

        public String getImage() {
            return image;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getName() {
            return name;
        }

        public int getGiftCount() {
            return giftCount;
        }

        public String getId() {
            return id;
        }

        public CategoryItem(String image, String createdAt, String name, int giftCount, String id) {
            this.image = image;
            this.createdAt = createdAt;
            this.name = name;
            this.giftCount = giftCount;
            this.id = id;
        }
    }
}