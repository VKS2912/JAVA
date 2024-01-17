package com.example.rayzi.emojifake;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.rayzi.modelclass.GiftCategory;

import java.util.List;

public class FakeEmojiViewPagerAdapter extends FragmentPagerAdapter {


    private List<GiftCategory> giftCategories;
    private FakeOnEmojiSelectLister onEmojiSelectLister;

    public FakeEmojiViewPagerAdapter(FragmentManager fm, List<GiftCategory> giftCategories) {
        super(fm);
        this.giftCategories = giftCategories;
    }

    public FakeOnEmojiSelectLister getOnEmojiSelectLister() {
        return onEmojiSelectLister;
    }

    public void setOnEmojiSelectLister(FakeOnEmojiSelectLister onEmojiSelectLister) {
        this.onEmojiSelectLister = onEmojiSelectLister;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("TAG", "getItem: >>>>>>>>>>>>>>>>>>>> " + giftCategories.get(position).getGiftRoot());
        FakemojiFragment emojiFragment = new FakemojiFragment(giftCategories.get(position).getGiftRoot(), getCount());
        emojiFragment.setOnEmojiSelectLister((binding, giftRoot, giftCount) -> {
            Log.e("TAG", "getItem: >>>>>>>>>>>>>>>>>>>> " + giftRoot);
            onEmojiSelectLister.onEmojiSelect(binding, giftRoot, giftCount);
        });
        return emojiFragment;
    }

    @Override
    public int getCount() {
        return giftCategories.size();
    }
}
