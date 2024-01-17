package com.example.rayzi.audioLive;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetOptionsBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetTheme {

    BottomSheetDialog bottomSheetDialog;
    Context context;


    public BottomSheetTheme(Context context, OnClickListner onClickListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;

        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetOptionsBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_options, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        sheetDilogBinding.galleryLay.setOnClickListener(v -> {
            onClickListner.onGalleryClick();
            bottomSheetDialog.dismiss();
        });

        sheetDilogBinding.cancel.setOnClickListener(v -> {
            onClickListner.onCancelClick();
            bottomSheetDialog.dismiss();
        });


    }


    public interface OnClickListner {
        void onGalleryClick();

        void onCancelClick();
    }


}
