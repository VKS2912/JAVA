package com.example.rayzi.audioLive;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.databinding.ItemSeatBinding;

import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatHolder> {

    private static final String TAG = "seatad====";
    Context context;
    List<SeatItem> seatList = new ArrayList<>();
    SessionManager sessionManager;
    onSeatClick onSeatClick;

    public SeatAdapter.onSeatClick getOnSeatClick() {
        return onSeatClick;
    }

    public void setOnSeatClick(SeatAdapter.onSeatClick onSeatClick) {
        this.onSeatClick = onSeatClick;
    }

    public void addData(List<SeatItem> seat) {
        seatList.addAll(seat);
//        this.seatList = seat;
        notifyItemRangeInserted(seatList.size(), seat.size());
    }

    public void updateData(List<SeatItem> seat) {
        Log.e(TAG, "updateData: >>>>>>>>>>>>>>   ");
        seatList.clear();
        seatList.addAll(seat);
        notifyDataSetChanged();
    }

    public void clear() {
        seatList.remove(seatList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        sessionManager = new SessionManager(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false);
        return new SeatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatHolder holder, int position) {
        setImage(seatList.get(position), holder.binding.image, holder.binding.image1);
        holder.binding.nameCount.setText(getNameText(seatList.get(position)));

        holder.itemView.setOnClickListener(view -> {
            onSeatClick.OnClickSeat(seatList.get(position), position);
        });

        if (seatList.get(position).getUserId() != null && seatList.get(position).getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
            if (seatList.get(position).isSpeaking()) {
                holder.binding.animationView1.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.binding.animationView1.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        }

        if (seatList.get(position).isMute()) {
            holder.binding.ivMute.setImageResource(R.drawable.mute);
        } else {
            holder.binding.ivMute.setImageResource(R.drawable.unmute);
        }

        if (seatList.get(position).getUserId() != null && seatList.get(position).getUserId().equalsIgnoreCase(sessionManager.getUser().getId())) {
            if (seatList.get(position).isMute()) {
                Log.d(TAG, "onBindViewHolder: " + seatList);
                onSeatClick.onMuteClick(seatList.get(position), position);
            } else if (!seatList.get(position).isMute()) {
                onSeatClick.onUnmuteClick(seatList.get(position), position);
            }

            if (seatList.get(position).isReserved()) {
                if (!seatList.get(position).isMute()) {
                    onSeatClick.onReserved(seatList.get(position), position);
                }
            } else if (seatList.get(position).isReserved()) {
                onSeatClick.onRedervedFalse(seatList.get(position), position);
            }

//            Log.e("onAudioVolumeIndication", "onBindViewHolder: >>>  isspeak "  +seatList.get(position).isSpeaking() );

            if (seatList.get(position).isSpeaking()) {
                onSeatClick.onSpeakingTrue(seatList.get(position), position, holder.binding);
            }


        }
    }

    public String getNameText(SeatItem seatItem) {
        if (seatItem.isReserved()) {
            return seatItem.getName();
        } else {
            return String.valueOf(seatItem.getPosition() + 1);
        }

    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    private void setImage(SeatItem seatItem, ImageView image, ImageView image1) {
        Log.d("seatAd", "setImage: " + seatItem.toString());

        if (seatItem.isReserved()) {
            Glide.with(context).load(seatItem.getImage()).circleCrop().into(image);

        } else if (seatItem.isReserved() && seatItem.isMute()) {
            Glide.with(context).load(R.drawable.host_mic_mute).into(image1);
            Glide.with(context).load(seatItem.getImage()).circleCrop().into(image);
        } else if (!seatItem.isReserved() && !seatItem.isLock())
            Glide.with(context).load(R.drawable.audio_sit).into(image);
        else if (seatItem.isLock())
            Glide.with(context).load(R.drawable.audio_lock).into(image);


      /*  if (seatItem.isMute())
            Glide.with(context).load(R.drawable.audio_mute_true).into(image);
        else
            Glide.with(context).load(R.drawable.audio_mute_false).into(image);*/
        // todo mute image ,only mic ni hoy
    }

    public interface onSeatClick {
        void OnClickSeat(SeatItem seatItem, int position);

        void onMuteClick(SeatItem seatItem, int positon);

        void onUnmuteClick(SeatItem seatItem, int position);

        void onReserved(SeatItem seatItem, int pos);

        void onRedervedFalse(SeatItem seatItemm, int pos);

        void onSpeakingTrue(SeatItem seatItem, int pos, ItemSeatBinding binding);

//        void  onSpeakingFalse(SeatItem seatItem,int pos,ItemSeatBinding binding);


    }

    public class SeatHolder extends RecyclerView.ViewHolder {
        ItemSeatBinding binding;

        public SeatHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSeatBinding.bind(itemView);

        }
    }

}
