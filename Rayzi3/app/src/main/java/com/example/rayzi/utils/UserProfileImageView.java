package com.example.rayzi.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.databinding.ItemUserprofileImageviewBinding;

public class UserProfileImageView extends RelativeLayout {

    ItemUserprofileImageviewBinding binding;


    public UserProfileImageView(Context context) {
        super(context);
        init();
    }

    public UserProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public UserProfileImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public UserProfileImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BackColor);
        int mColor = a.getColor(R.styleable.BackColor_backColor, getContext().getColor(R.color.transparent));
        ColorStateList color = a.getColorStateList(R.styleable.BackColor_backColor);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_userprofile_imageview, null, false);

        binding.imguser.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_round_pink));
        binding.imguser.setBackgroundTintList(color);
        addView(binding.getRoot());
    }

    private void init() {

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_userprofile_imageview, null, false);
        addView(binding.getRoot());

    }

    public void setUserImage(String imageUrl) {
        if (binding != null) {
            Glide.with(this).load(imageUrl)
                    .apply(MainApplication.requestOptions).circleCrop().into(binding.imguser);
            binding.imgvip.setVisibility(GONE);
        }

    }

    public void setUserImage(String imageUrl, boolean isVip, Context context) {
        if (binding != null) {
            Glide.with(context).load(imageUrl)
                    .apply(MainApplication.requestOptions)
                    .circleCrop().into(binding.imguser);
            if (isVip) {
                binding.imgvip.setVisibility(VISIBLE);
            } else {
                binding.imgvip.setVisibility(GONE);
            }
        }
    }
}
