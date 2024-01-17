package com.example.rayzi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.audioLive.SeatItem;
import com.example.rayzi.databinding.ItemCohostBinding;

import java.util.ArrayList;

public class CoHostListAdapter extends RecyclerView.Adapter<CoHostListAdapter.CoHostListHolder> {

    Context context;
    ArrayList<SeatItem> coHostList = new ArrayList<>();
    CoHostClickListener coHostClickListener;

    @NonNull
    @Override
    public CoHostListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cohost, parent, false);
        context = parent.getContext();
        return new CoHostListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoHostListHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context)
                .load(coHostList.get(position).getImage())
                .into(holder.binding.ivProfile);

        holder.binding.tvUserName.setText(coHostList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            coHostClickListener.onCoHostSelect(position,coHostList.get(position));

            }
        });

    }

    public void addData(ArrayList<SeatItem> coHostList) {
        this.coHostList = coHostList;
    }

    public void setCoHostClickListener(CoHostClickListener coHostClickListener) {
        this.coHostClickListener = coHostClickListener;
    }

    @Override
    public int getItemCount() {
        return coHostList.size();
    }

    class CoHostListHolder extends RecyclerView.ViewHolder {
        ItemCohostBinding binding;

        public CoHostListHolder(@NonNull View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView.getRootView());

        }
    }

    public interface CoHostClickListener {

        void onCoHostSelect(int position,SeatItem seatItem);

    }

}
