package com.example.rayzi.emoji;

import com.example.rayzi.databinding.ItemEmojiGridBinding;
import com.example.rayzi.modelclass.GiftRoot;

public interface OnEmojiSelectLister {
    void onEmojiSelect(ItemEmojiGridBinding binding, GiftRoot.GiftItem giftRootDummy);
}
