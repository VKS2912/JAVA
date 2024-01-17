package com.example.rayzi.audioLive;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetAudioBgBinding;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetOtions {

    BottomSheetDialog bottomSheetDialog;
    BakgroundAdapter bakgroundAdapter;
    Context context;



    public BottomSheetOtions(Context context, OnClickListner onClickListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;

        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetAudioBgBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_audio_bg, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        bakgroundAdapter = new BakgroundAdapter();

        getTheme(sheetDilogBinding);


        bakgroundAdapter.setOnImageClick(themeItem -> {
            onClickListner.OnImage(themeItem);
            bottomSheetDialog.dismiss();
        });

    }

    private void getTheme(BottomSheetAudioBgBinding sheetDilogBinding) {
        Call<ThemeRoot> call = RetrofitBuilder.create().getTheme();

        call.enqueue(new Callback<ThemeRoot>() {
            @Override
            public void onResponse(Call<ThemeRoot> call, Response<ThemeRoot> response) {
                if (response.code() == 200) {
                    if (response.isSuccessful() && response.body() != null) {
                        bakgroundAdapter.addData(response.body().getTheme());
                        sheetDilogBinding.rvAudioBg.setAdapter(bakgroundAdapter);

                    }
                }
            }

            @Override
            public void onFailure(Call<ThemeRoot> call, Throwable t) {

            }
        });
    }


    public interface OnClickListner {
        void OnImage(String image);
    }


}
