package com.example.rayzi.audioLive;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetViewersOnlineBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

public class BottomSheetViewersUsers {

    public GiveMicUserAdapter liveViewUserAdapter = new GiveMicUserAdapter();
    BottomSheetDialog bottomSheetDialog;
    BakgroundAdapter bakgroundAdapter;
    Context context;

    public BottomSheetViewersUsers(Context context, JSONArray list, OnClickListner onClickListner) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme);
        this.context = context;

        Log.e("TAG", "BottomSheetViewersUsers: bottomSheet" + list.toString() );

        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet)
                    .setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        BottomSheetViewersOnlineBinding sheetDilogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bottom_sheet_viewers_online, null, false);

        bottomSheetDialog.setContentView(sheetDilogBinding.getRoot());
        bottomSheetDialog.show();

        sheetDilogBinding.rvViewUsers.setAdapter(liveViewUserAdapter);

        liveViewUserAdapter.setOnLiveUserAdapterClickLisnter(userDummy -> {
            onClickListner.OnItemClick(userDummy);
        });

        liveViewUserAdapter.addData(list);

    }

    public interface OnClickListner {
        void OnItemClick(JSONObject userDummy);
    }

}
