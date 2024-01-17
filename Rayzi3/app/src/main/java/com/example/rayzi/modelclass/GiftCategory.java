package com.example.rayzi.modelclass;

import java.util.List;

public class GiftCategory {
    String name;
    List<FakeGiftRoot> giftRoot;

    public GiftCategory(String name, List<FakeGiftRoot> giftRoot) {
        this.name = name;
        this.giftRoot = giftRoot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FakeGiftRoot> getGiftRoot() {
        return giftRoot;
    }

    public void setGiftRoot(List<FakeGiftRoot> giftRoot) {
        this.giftRoot = giftRoot;
    }
}
