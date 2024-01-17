package com.example.rayzi.emojifake;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.rayzi.R;
import com.example.rayzi.databinding.FakeFragmentEmojiBinding;
import com.example.rayzi.modelclass.FakeGiftRoot;

import java.util.ArrayList;
import java.util.List;


public class FakemojiFragment extends Fragment {


    FakeFragmentEmojiBinding binding;
    FakeEmojiGridAdapter emojiGridAdapter;
    int Count;
    private FakeOnEmojiSelectLister onEmojiSelectLister;
    private List<FakeGiftRoot> giftRoot = new ArrayList<>();

    public FakemojiFragment(List<FakeGiftRoot> giftRoot, int count) {
        // Required empty public constructor
        this.giftRoot = giftRoot;
        this.Count = count;
    }

    public FakeOnEmojiSelectLister getOnEmojiSelectLister() {
        return onEmojiSelectLister;
    }

    public void setOnEmojiSelectLister(FakeOnEmojiSelectLister onEmojiSelectLister) {
        this.onEmojiSelectLister = onEmojiSelectLister;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fake_fragment_emoji, container, false);
        emojiGridAdapter = new FakeEmojiGridAdapter(Count);
        initMain();
        return binding.getRoot();
    }

    private void initMain() {
        emojiGridAdapter.addData(giftRoot);
        binding.rvEmoji.setAdapter(emojiGridAdapter);
        emojiGridAdapter.setOnEmojiSelectLister((binding1, giftRoot, giftCount) -> {
            onEmojiSelectLister.onEmojiSelect(binding1, giftRoot, giftCount);
        });
    }
}