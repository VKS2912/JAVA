package com.example.rayzi.emoji;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.databinding.FragmentEmojiBinding;
import com.example.rayzi.modelclass.GiftCategoryRoot;
import com.example.rayzi.modelclass.GiftRoot;
import com.example.rayzi.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmojiFragment extends BaseFragment {


    FragmentEmojiBinding binding;


    EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter();
    private OnEmojiSelectLister onEmojiSelectLister;
    private GiftCategoryRoot.CategoryItem categoryRoot;


    public EmojiFragment(GiftCategoryRoot.CategoryItem categoryRoot) {
        // Required empty public constructor

        this.categoryRoot = categoryRoot;
    }

    public OnEmojiSelectLister getOnEmojiSelectLister() {
        return onEmojiSelectLister;
    }

    public void setOnEmojiSelectLister(OnEmojiSelectLister onEmojiSelectLister) {
        this.onEmojiSelectLister = onEmojiSelectLister;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_emoji, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMain();
        getData();

    }

    private void getData() {
        binding.noData.setVisibility(View.GONE);
        Log.d("TAG", "getData: gifts  size " + emojiGridAdapter.getItemCount());
        if (emojiGridAdapter.getItemCount() <= 0) {  //todo shimmer issue
            binding.shimmerTab.setVisibility(View.VISIBLE);
        }
        Call<GiftRoot> call = RetrofitBuilder.create().getGiftsByCategory(categoryRoot.getId());
        call.enqueue(new Callback<GiftRoot>() {
            @Override
            public void onResponse(Call<GiftRoot> call, Response<GiftRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getGift().isEmpty()) {
                        emojiGridAdapter.addData(response.body().getGift());
                    } else {
                        binding.noData.setVisibility(View.VISIBLE);
                    }
                }
                binding.shimmerTab.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<GiftRoot> call, Throwable t) {

            }
        });
    }

    private void initMain() {
//        if (isRTL(getActivity())) {
//            binding.rvEmoji.setRotation(180);
//        }
        binding.rvEmoji.setAdapter(emojiGridAdapter);
        emojiGridAdapter.setOnEmojiSelectLister((binding1, giftRoot) -> onEmojiSelectLister.onEmojiSelect(binding1, giftRoot));

    }
}