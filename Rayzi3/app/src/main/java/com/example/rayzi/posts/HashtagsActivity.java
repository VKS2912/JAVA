package com.example.rayzi.posts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.DataBindingUtil;

import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityHashtagsBinding;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.z_demo.Demo_contents;

import java.util.ArrayList;
import java.util.List;

public class HashtagsActivity extends BaseActivity {
    ActivityHashtagsBinding binding;
    List<String> selectedHashtags = new ArrayList<>();
    HashtagsAdapter selectedHashtagsAdapter = new HashtagsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hashtags);
        Intent intent = getIntent();
        String hashtag = intent.getStringExtra(Const.DATA);
        if (hashtag!=null && !hashtag.isEmpty()) {
            binding.etHashtags.setText(hashtag);
        }
        initView();
        initLister();
    }

    private void initLister() {
        binding.btnDone.setOnClickListener(v -> {
            Intent intent = getIntent();
            intent.putExtra(Const.DATA, binding.etHashtags.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        });
        selectedHashtagsAdapter.setOnHashtagsClickLisnter(hashtag -> {

            addHashtags();
        });
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedHashtagsAdapter.clear();
                if (s.toString().isEmpty()) {

                }
                List<String> searchedHashtag = new ArrayList<>();
                for (String h : Demo_contents.getHashtags()) {
                    if (h.toLowerCase().contains(s.toString().toLowerCase())) {
                        searchedHashtag.add(h);
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initView() {


        binding.rvSelectedHashtags.setAdapter(selectedHashtagsAdapter);


    }

    private void addHashtags() {
if (binding.etHashtags.getText().toString().contains("Add Hashtags")) {

    binding.etHashtags.setText("");
}
        for (int i = 0; i < selectedHashtags.size(); i++) {
            binding.etHashtags.setText(binding.etHashtags.getText().toString() + " " + selectedHashtags.get(i));
        }
    }
}