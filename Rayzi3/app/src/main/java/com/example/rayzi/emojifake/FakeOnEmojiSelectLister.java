package com.example.rayzi.emojifake;

import com.example.rayzi.databinding.ItemEmojiGridBinding;
import com.example.rayzi.modelclass.FakeGiftRoot;


public interface FakeOnEmojiSelectLister {
    void onEmojiSelect(ItemEmojiGridBinding binding, FakeGiftRoot giftRoot, String giftCount);
}
