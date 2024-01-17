package com.example.rayzi.user;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.activity.SettingActivity;
import com.example.rayzi.databinding.FragmentProfileBinding;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.posts.FeedGridActivity;
import com.example.rayzi.reels.VideoListGridActivity;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.UserApiCall;
import com.example.rayzi.user.complain.ComplainListActivity;
import com.example.rayzi.user.complain.CreateComplainActivity;
import com.example.rayzi.user.freeCoins.FreeDimondsActivity;
import com.example.rayzi.user.vip.VipPlanActivity;
import com.example.rayzi.user.wallet.MyWalletActivity;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.google.gson.Gson;


public class ProfileFragment extends BaseFragment {


    FragmentProfileBinding binding;
    SessionManager sessionManager;
    private UserRoot.User user;
    private UserApiCall userApiCall;
    private ProfileViewModel viewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ProfileViewModel()).createFor()).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);

        return binding.getRoot();

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        sessionManager = new SessionManager(getActivity());
        userApiCall = new UserApiCall(getActivity());

        viewModel.isLoading.set(true);
        user = sessionManager.getUser();
        userApiCall.getUser(user -> {
            sessionManager.saveUser(user);
            ProfileFragment.this.user = user;
            initView();
            viewModel.isLoading.set(false);
        });
        initListner();
    }

    private void initListner() {
        binding.btnSetting.setOnClickListener(v -> startActivity(new Intent(getActivity(), SettingActivity.class)));
        binding.lytMyPost.setOnClickListener(v -> startActivity(new Intent(getActivity(), FeedGridActivity.class).putExtra(Const.DATA, new Gson().toJson(user))));
        binding.lytMyVideos.setOnClickListener(v -> startActivity(new Intent(getActivity(), VideoListGridActivity.class).putExtra(Const.DATA, new Gson().toJson(user))));
        binding.lytFollowing.setOnClickListener(v -> startActivity(new Intent(getActivity(), FollowrsListActivity.class).putExtra(Const.TYPE, 1).putExtra(Const.USERID, user.getId())));
        binding.lytFollowrs.setOnClickListener(v -> startActivity(new Intent(getActivity(), FollowrsListActivity.class).putExtra(Const.TYPE, 2).putExtra(Const.USERID, user.getId())));
        binding.btnEditProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        binding.tvLevel.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyLevelListActivity.class)));
        binding.lytVIP.setOnClickListener(v -> startActivity(new Intent(getActivity(), VipPlanActivity.class)));
        binding.lytWallet.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyWalletActivity.class)));
        binding.lytFreeDimonds.setOnClickListener(v -> startActivity(new Intent(getActivity(), FreeDimondsActivity.class)));
        binding.lytSupport.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreateComplainActivity.class)));
        binding.lytComplains.setOnClickListener(v -> startActivity(new Intent(getActivity(), ComplainListActivity.class)));

        binding.copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", user.getUniqueId());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getActivity(), getResources().getString(R.string.Copied_successfully), Toast.LENGTH_SHORT).show();
        });


    }

    private void initView() {
        if (getActivity() == null) return;

        Glide.with(this).load(user.getImage()).into(binding.imgUser1);
        binding.imgUser.setUserImage(user.getImage(), user.isIsVIP(), getContext());
        binding.tvName.setText(user.getName());
        binding.tvAge.setText(String.valueOf(user.getAge()));
        // binding.tvBio.setText(user.getBio());
        // binding.tvCountry.setText(user.getCountry());
        binding.tvFollowrs.setText(String.valueOf(user.getFollowers()));
        binding.tvLevel.setText(user.getLevel().getName());
        binding.tvFollowing.setText(String.valueOf(user.getFollowing()));

        if (user.getUniqueId() == null || user.getUniqueId().isEmpty()) {
            binding.tvUserName.setVisibility(View.GONE);
            binding.copy.setVisibility(View.GONE);
        } else {
            binding.tvUserName.setVisibility(View.VISIBLE);
            binding.copy.setVisibility(View.VISIBLE);
            binding.tvUserName.setText("ID:" + user.getUniqueId());

        }


        if (user.getGender().equalsIgnoreCase(Const.MALE)) {
            binding.imgGender.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.male));
        } else {
            binding.imgGender.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.female));
        }

        if (isRTL(getActivity())) {
            binding.tvWallet.setGravity(Gravity.END);
            binding.tvMyRelite.setGravity(Gravity.END);
            binding.tvMyPost.setGravity(Gravity.END);
            binding.tvFreeDiamond.setGravity(Gravity.END);
            binding.tvBecomeVip.setGravity(Gravity.END);
            binding.tvHaveIssue.setGravity(Gravity.END);
            binding.tvMyComplain.setGravity(Gravity.END);

            // folowing box
            binding.folowingbox.setGravity(Gravity.START);
            binding.imgWallet.setScaleX(isRTL(getActivity()) ? -1 : 1);
            binding.imgMyRelites.setScaleX(isRTL(getActivity()) ? -1 : 1);
            binding.imgMyPost.setScaleX(isRTL(getActivity()) ? -1 : 1);
            binding.imgFreeDiamond.setScaleX(isRTL(getActivity()) ? -1 : 1);
            binding.imgBecomeVip.setScaleX(isRTL(getActivity()) ? -1 : 1);
            binding.imgHaveIssue.setScaleX(isRTL(getActivity()) ? -1 : 1);
            binding.imgMyComplain.setScaleX(isRTL(getActivity()) ? -1 : 1);
        }

    }

    public class ProfileViewModel extends ViewModel {
        public ObservableBoolean isLoading = new ObservableBoolean(true);
    }
}