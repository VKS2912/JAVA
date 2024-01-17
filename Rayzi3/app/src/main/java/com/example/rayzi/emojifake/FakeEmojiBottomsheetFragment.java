package com.example.rayzi.emojifake;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.FakeFragmentEmojiBottomsheetBinding;
import com.example.rayzi.databinding.ItemEmojiGridBinding;
import com.example.rayzi.modelclass.FakeGiftRoot;
import com.example.rayzi.modelclass.GiftCategory;
import com.example.rayzi.models.CoinRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.example.rayzi.viewModel.EmojiSheetViewModel;
import com.example.rayzi.viewModel.ViewModelFactory;
import com.example.rayzi.z_demo.Demo_contents;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FakeEmojiBottomsheetFragment extends BottomSheetDialogFragment {


    FakeFragmentEmojiBottomsheetBinding binding;
    String[] country = {" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ", " 9 ", " 10"};
    String giftCount;
    List<GiftCategory> giftCategories = Demo_contents.getGiftCategory();
    SessionManager sessionManager;
    private FakeEmojiViewPagerAdapter emojiViewPagerAdapter;
    private ItemEmojiGridBinding lastBinding = null;
    private FakeOnEmojiSelectLister onEmojiSelectLister;
    private FakeGiftRoot selectedGift;
    private EmojiSheetViewModel viewModel;

    public FakeEmojiBottomsheetFragment() {
    }

    public FakeEmojiBottomsheetFragment(FakeOnEmojiSelectLister onEmojiSelectLister) {
        // Required empty public constructor
        this.onEmojiSelectLister = onEmojiSelectLister;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fake_fragment_emoji_bottomsheet, container, false);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new EmojiSheetViewModel()).createFor()).get(EmojiSheetViewModel.class);
        sessionManager = new SessionManager(this.getContext());
        initMain();
        initListner();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
        });
    }

    private void initListner() {
        binding.tvCoin.setText(String.valueOf(sessionManager.getUser().getDiamond()));
        emojiViewPagerAdapter.setOnEmojiSelectLister((binding1, giftRoot, giftCount) -> {
            selectedGift = giftRoot;
            if (lastBinding != null) {
                lastBinding.itememoji.setBackground(null);
                binding1.itememoji.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_selected_5dp));
            }
            lastBinding = binding1;
        });
        binding.btnSend.setOnClickListener(v -> {
            Log.e("TAG", "initListner: >>>>  " + selectedGift);
            if (selectedGift != null) {
                Log.e("TAG", "initListner: >>>> 11 " + selectedGift + "  " + giftCount);
                onEmojiSelectLister.onEmojiSelect(null, selectedGift, giftCount);
                getCoin(selectedGift);
            }
        });

        ArrayAdapter<String> aa = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, country);

        //Setting the ArrayAdapter data on the Spinner
        binding.spinner.setAdapter(aa);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.tvGiftCount.setText(String.valueOf(country[position]));
                giftCount = (String.valueOf(country[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.tvGiftCount.setText(String.valueOf(1));
            }
        });
        binding.lytGiftCount.setOnClickListener(v -> {

            binding.spinner.performClick();
        });
    }

    @Override
    public void setStyle(int style, int theme) {
        super.setStyle(style, theme);

    }


    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialogTheme;
    }

    private void initMain() {

        emojiViewPagerAdapter = new FakeEmojiViewPagerAdapter(getChildFragmentManager(), giftCategories);
        binding.viewPager.setAdapter(emojiViewPagerAdapter);
        binding.tablayout1.setupWithViewPager(binding.viewPager);
        binding.tablayout1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //ll

                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                    tv.setTextSize(16);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        Typeface typeface = getResources().getFont(R.font.abold);
                        tv.setTypeface(typeface);
                    }
                    View indicator = (View) v.findViewById(R.id.indicator);
                    indicator.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //ll
                View v = tab.getCustomView();
                if (v != null) {
                    TextView tv = (TextView) v.findViewById(R.id.tvTab);
                    tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.graylight));
                    tv.setTextSize(14);
                    View indicator = (View) v.findViewById(R.id.indicator);
                    indicator.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//ll
            }
        });
        settab(giftCategories);


    }

    private void settab(List<GiftCategory> contry) {
        binding.tablayout1.setTabGravity(TabLayout.GRAVITY_FILL);
        binding.tablayout1.removeAllTabs();
        for (int i = 0; i < contry.size(); i++) {
            binding.tablayout1.addTab(binding.tablayout1.newTab().setCustomView(createCustomView(i, contry.get(i).getName())));
        }

    }

    private View createCustomView(int i, String s) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.custom_tabhorizontol2, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(s);
        tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.graylight));
        tv.setTextSize(14);
        View indicator = (View) v.findViewById(R.id.indicator);
        if (i == 0) {
            indicator.setVisibility(View.VISIBLE);
        } else {
            indicator.setVisibility(View.GONE);
        }
        return v;

    }

    private void getCoin(FakeGiftRoot selectedGift) {
        Log.e("TAG", "getCoin: >>>>>>>>>>>>>>> " + selectedGift.getCoin());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("senderUserId", sessionManager.getUser().getId());
        jsonObject.addProperty("coin", selectedGift.getCoin());
        jsonObject.addProperty("receiverUserId", "");
        jsonObject.addProperty("type", Const.LIVE);
        Call<CoinRoot> call;
        call = RetrofitBuilder.create().getCoin(jsonObject);
        call.enqueue(new Callback<CoinRoot>() {
            @Override
            public void onResponse(Call<CoinRoot> call, Response<CoinRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && response.body().getSenderUser() != null) {
                        Log.e("TAG", "onResponse: >>>>>>>>>>>> " + response.body().getSenderUser());
                        binding.tvCoin.setText(String.valueOf(response.body().getSenderUser().getDiamond()));

                    }
                }
            }

            @Override
            public void onFailure(Call<CoinRoot> call, Throwable t) {

            }
        });
    }


}