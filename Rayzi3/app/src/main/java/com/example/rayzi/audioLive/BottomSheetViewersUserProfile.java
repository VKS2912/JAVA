package com.example.rayzi.audioLive;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetOnlineProfileBinding;
import com.example.rayzi.modelclass.GuestProfileRoot;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetViewersUserProfile {

    BottomSheetDialog bottomSheetDialog;
    Context context;

    public BottomSheetViewersUserProfile(Context context, SeatItem seatItem, GuestProfileRoot.User userData, OnClickListner onClickListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;

        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetOnlineProfileBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_online_profile, null, false);
        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        Glide.with(context).load(userData.getImage()).circleCrop().into(sheetDilogBinding.userImg);

        sheetDilogBinding.userName.setText(userData.getName());
        sheetDilogBinding.userId.setText(userData.getUserId());
        sheetDilogBinding.gender.setText(userData.getGender());
        sheetDilogBinding.location.setText(userData.getCountry());

        sheetDilogBinding.unMuteMic.setOnClickListener(v -> {
            onClickListner.onUnMute(sheetDilogBinding);
            bottomSheetDialog.dismiss();
        });

        sheetDilogBinding.removeSit.setOnClickListener(v -> {
            if (seatItem.isReserved()) {
                onClickListner.onRemoveSeat();
            }else {
                onClickListner.inviteUser();
            }
            bottomSheetDialog.dismiss();
        });


        sheetDilogBinding.kickOut.setOnClickListener(v -> {
            onClickListner.onkickOut();
            bottomSheetDialog.dismiss();
        });

        if (seatItem.isMute()) {
            sheetDilogBinding.txtMic.setText("Unmute mic");
            Glide.with(context).load(R.drawable.speaker_off).into(sheetDilogBinding.mute);
        } else {
            sheetDilogBinding.txtMic.setText("Mute mic");
            Glide.with(context).load(R.drawable.speaker).into(sheetDilogBinding.mute);
        }

        if (seatItem.isReserved()) {
            sheetDilogBinding.txtSeat.setText("Remove seat");
            Glide.with(context).load(R.drawable.remove_sit).into(sheetDilogBinding.seat);
        } else {
            sheetDilogBinding.txtSeat.setText("Invite seat");
            Glide.with(context).load(R.drawable.take_sit).into(sheetDilogBinding.seat);
        }


    }

    public interface OnClickListner {
        void onUnMute(BottomSheetOnlineProfileBinding sheetDilogBinding);

        void onRemoveSeat();

        void onkickOut();void inviteUser();
    }


}
