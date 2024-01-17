package com.example.rayzi.audioLive;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetHostMicBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetHostMic {

    BottomSheetDialog bottomSheetDialog;
    Context context;


    public BottomSheetHostMic(Context context, SeatItem seatItem, OnClickListner onClickListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;

        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetHostMicBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_host_mic, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        if (seatItem.isMute()){
            sheetDilogBinding.tvMute.setText("UnMute");
        }
        if (seatItem.isLock()){
            sheetDilogBinding.tvLock.setText("UnLock");
        }
        if (seatItem.isReserved()){
            sheetDilogBinding.tvGiveRemove.setText("Remove");
        }
        sheetDilogBinding.takeMic.setOnClickListener(v -> {
            onClickListner.onTakeMic();
            bottomSheetDialog.dismiss();
        });
        sheetDilogBinding.giveMic.setOnClickListener(v -> {
            if (seatItem.isReserved()){
                onClickListner.onClickRemove();
            }else {
                onClickListner.onGiveMic();
            }
            bottomSheetDialog.dismiss();

        });
        sheetDilogBinding.lockMic.setOnClickListener(v -> {
            onClickListner.onLockMic();
            bottomSheetDialog.dismiss();
        });
        sheetDilogBinding.muteMic.setOnClickListener(v -> {
            onClickListner.onMuteMic();
            bottomSheetDialog.dismiss();
        });

        sheetDilogBinding.cancel.setOnClickListener(v -> {
            onClickListner.onCancelClick();
            bottomSheetDialog.dismiss();
        });


    }


    public interface OnClickListner {
        void onTakeMic();

        void onGiveMic();

        void onLockMic();

        void onMuteMic();

        void onCancelClick();

        void onClickRemove();
    }


}
