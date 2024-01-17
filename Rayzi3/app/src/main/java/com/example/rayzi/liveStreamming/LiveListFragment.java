package com.example.rayzi.liveStreamming;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.MyLoader;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.databinding.FragmentLiveListBinding;
import com.example.rayzi.modelclass.LiveUserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LiveListFragment extends BaseFragment {

    LiveListAdapter liveListAdapter = new LiveListAdapter();
    FragmentLiveListBinding binding;
    public static final String TAG = "LiveListFragment";
    private int start = 0;
    MyLoader myLoader = new MyLoader();
    private String type;
    boolean isLoadMoreLoading = true;
    boolean isFirstTimeLoading = true;
    MyLoader loader;

    public LiveListFragment() {
    }

    public LiveListFragment(String type) {
        // Required empty public constructor
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_live_list, container, false);
        binding.setLoader(myLoader);
        initView();
//        getData(false);
        initLister();
        return binding.getRoot();

    }

    private void initLister() {
        Log.e("TAG", "initLister: >>>>>>>>>>>>>>>>>>>>>> 00 ");
        binding.swipeRefresh.setOnRefreshListener((refreshLayout) -> {
            getData(false);
        });
        binding.swipeRefresh.setOnLoadMoreListener(refreshLayout -> {
            getData(true);
        });
    }

    private void getData(boolean isLoadMore) {


        if (isLoadMore) {
            start = start + Const.LIMIT;
            myLoader.isLoadingmore.set(true);
        } else {
            myLoader.isFristTimeLoading.set(true);
            start = 0;
            liveListAdapter.clear();
        }

        myLoader.noData.set(false);
        Call<LiveUserRoot> call = RetrofitBuilder.create().getLiveUsersList(sessionManager.getUser().getId(), type, start, Const.LIMIT,"false");
        call.enqueue(new Callback<LiveUserRoot>() {
            @Override
            public void onResponse(Call<LiveUserRoot> call, Response<LiveUserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getUsers().isEmpty()) {
                        liveListAdapter.addData(response.body().getUsers());
                        Log.d(TAG, "onResponse:  users ==================================================" + response.body().getUsers());
                    } else if (start == 0) {
                        myLoader.noData.set(true);
                    }
                }
                myLoader.isLoadCompleted.postValue(true);
                myLoader.isFristTimeLoading.set(false);
                myLoader.isLoadingmore.set(false);
                binding.swipeRefresh.finishRefresh();
                binding.swipeRefresh.finishLoadMore();
            }

            @Override
            public void onFailure(Call<LiveUserRoot> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void initView() {
      /*  ((GridLayoutManager)binding.rvVideos.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position%3==0){
                    return 2;
                }
                return 1;
            }
        });*/
        binding.rvVideos.setAdapter(liveListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLister();
        getData(false);
    }
}