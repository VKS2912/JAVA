package com.example.rayzi.user;

import static android.provider.MediaStore.MediaColumns.DATA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.rayzi.BuildConfig;
import com.example.rayzi.MainApplication;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.activity.MainActivity;
import com.example.rayzi.databinding.ActivityEditProfileBinding;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {
    private static final int GALLERY_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 111;
    private static final String TAG = "Editprofileact";
    ActivityEditProfileBinding binding;
    boolean isValidUserName = false;
    String nameS, usernameS;
    private String gender = "";
    private String picturePath = "";
    private UserRoot.User userDummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        binding.pd1.setVisibility(View.GONE);
        userDummy = sessionManager.getUser();
        Glide.with(this).load(userDummy.getImage())
                .apply(MainApplication.requestOptions)
                .circleCrop().into(binding.imgUser);
        binding.etName.setText(userDummy.getName());
        binding.etBio.setText(userDummy.getBio());
        binding.etAge.setText(String.valueOf(userDummy.getAge()));
        if (String.valueOf(userDummy.getAge()).equals("0")) {
            binding.etAge.setText("18");
        }
        binding.etUserName.setText(userDummy.getUsername());

        binding.lytMale.setOnClickListener(v -> onMaleClick());
        binding.lytFemale.setOnClickListener(v -> onFeMaleClick());
        binding.radioFemale.setOnClickListener(v -> onFeMaleClick());
        binding.radioMale.setOnClickListener(v -> onMaleClick());

        if (userDummy.getGender().equalsIgnoreCase(Const.MALE)) {
            binding.lytMale.performClick();
        } else if (userDummy.getGender().equalsIgnoreCase(Const.FEMALE)) {
            binding.tvFemale.performClick();
        } else {
            binding.lytMale.performClick();
        }
        if (isRTL(this)) {
            binding.back.setScaleX(isRTL(this) ? -1 : 1);
        }

        gender = Const.MALE;

        isValidUserName = !userDummy.getUsername().isEmpty();
        Log.d(TAG, "checkDetails: " + isValidUserName + "  " + gender);
        if (userDummy != null && userDummy.getUsername() != null && !userDummy.getUsername().isEmpty()) {
            binding.etUserName.setText(userDummy.getUsername());
            isValidUserName = true;
            // binding.etUserName.setEnabled(false);
        }

        if (userDummy.getUsername() != null && !userDummy.getUsername().isEmpty()) {

            binding.etUserName.setText(userDummy.getUsername());
            isValidUserName = true;
            //  binding.etUserName.setEnabled(false);
        }
        binding.imgUser.setOnClickListener(v -> choosePhoto());
        binding.btnPencil.setOnClickListener(v -> choosePhoto());

        binding.etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidation(s.toString());
                usernameS = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!usernameS.isEmpty()) {
//                  checkDetails();
//                }

            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameS = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    binding.etAge.setError("Enter Correct Age");
                    return;
                }
                int age = Integer.parseInt(s.toString());
                if (age < 18 || age > 105) {
                    binding.etAge.setError("Minimum age must be 18 years.");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.tvSubmit.setOnClickListener(v -> {

            String name = binding.etName.getText().toString();
            String userName = binding.etUserName.getText().toString();
            String bio = binding.etBio.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter your name first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userName.isEmpty()) {
                Toast.makeText(this, "Enter Username first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (gender.isEmpty()) {
                Toast.makeText(this, "Select your gender", Toast.LENGTH_SHORT).show();
                return;
            }

            int age = Integer.parseInt(binding.etAge.getText().toString());

            if (age < 18 || age > 105) {
                Toast.makeText(this, "Minimum age must be 18 years.", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, RequestBody> map = new HashMap<>();

            MultipartBody.Part body = null;
            if (picturePath != null && !picturePath.isEmpty()) {
                File file = new File(picturePath);
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            }


            RequestBody bodyUserid = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
            RequestBody bodyName = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody bodyGender = RequestBody.create(MediaType.parse("text/plain"), gender);
            // RequestBody bodyEmail = RequestBody.create(MediaType.parse("text/plain"), userDummy.getEmail());
            RequestBody bodyUserName = RequestBody.create(MediaType.parse("text/plain"), userName);
            RequestBody bodyBio = RequestBody.create(MediaType.parse("text/plain"), bio);

            RequestBody bodyAge = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(age));


            map.put("name", bodyName);
            map.put("username", bodyUserName);
            map.put("bio", bodyBio);
            map.put("userId", bodyUserid);
            map.put("gender", bodyGender);
            map.put("age", bodyAge);

            binding.loder.setVisibility(View.VISIBLE);
            Call<UserRoot> call = RetrofitBuilder.create().updateUser(map, body);
            call.enqueue(new Callback<UserRoot>() {
                @Override
                public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                    if (response.code() == 200) {
                        if (response.body().isStatus()) {
                            sessionManager.saveUser(response.body().getUser());
                            sessionManager.saveBooleanValue(Const.ISLOGIN, true);
                            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                    binding.loder.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<UserRoot> call, Throwable t) {

                }
            });


        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private String getProfileUrl(String imageUrl, String gender) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        if (gender.equalsIgnoreCase(Const.FEMALE)) {
            imageUrl = BuildConfig.BASE_URL + "storage/female.png";
        } else if (gender.equalsIgnoreCase(Const.MALE)) {
            imageUrl = BuildConfig.BASE_URL + "storage/male.png";

        } else return "";
        return imageUrl;
    }



    private void checkValidation(String toString) {

        binding.pd1.setVisibility(View.VISIBLE);

        Call<RestResponse> call = RetrofitBuilder.create().checkUserName(toString, sessionManager.getUser().getId());
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (!response.body().isStatus()) {
                        binding.etUserName.setError("Username already taken");
                        isValidUserName = false;
                    } else {

                        isValidUserName = true;
                    }
                    Log.d(TAG, "checkDetails: " + isValidUserName + "  " + gender);


                }
                binding.pd1.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {

            }
        });
    }


    private void choosePhoto() {
        requestPermission();
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_CODE);

    }


    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        1);
            }
        } else {
            if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.per_deny), Toast.LENGTH_SHORT).show();
            }
            // Do something for lollipop and above versions
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getResources().getString(R.string.per_deny), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();

            Glide.with(this)
                    .load(selectedImage)
                    .circleCrop()
                    .into(binding.imgUser);
            String[] filePathColumn = {DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

        }
    }

    private void onFeMaleClick() {
        gender = Const.FEMALE;
        binding.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.pink));
        binding.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.radioMale.setChecked(false);
        binding.radioFemale.setChecked(true);


    }

    private void onMaleClick() {
        gender = Const.MALE;
        binding.tvMale.setTextColor(ContextCompat.getColor(this, R.color.pink));
        binding.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white));
        binding.radioMale.setChecked(true);
        binding.radioFemale.setChecked(false);

    }
}