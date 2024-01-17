package com.example.rayzi.audioLive;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rayzi.R;
import com.example.rayzi.databinding.BottomSheetOnlineBinding;

public class OnlineViwersAdpter extends RecyclerView.Adapter<OnlineViwersAdpter.OnlineViewerHolder> {


    @NonNull
    @Override
    public OnlineViewerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_sheet_online, parent, false);
        return new OnlineViewerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlineViewerHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class OnlineViewerHolder extends RecyclerView.ViewHolder {

        BottomSheetOnlineBinding binding;

        public OnlineViewerHolder(@NonNull View itemView) {
            super(itemView);
            binding = BottomSheetOnlineBinding.bind(itemView);
        }
    }
}
