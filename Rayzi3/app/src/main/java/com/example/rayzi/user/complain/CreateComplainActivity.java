package com.example.rayzi.user.complain;

import static android.provider.MediaStore.MediaColumns.DATA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.rayzi.R;
import com.example.rayzi.activity.BaseActivity;
import com.example.rayzi.databinding.ActivityCreateComplainBinding;
import com.example.rayzi.modelclass.RestResponse;
import com.example.rayzi.retrofit.RetrofitBuilder;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateComplainActivity extends BaseActivity {
    private static final int GALLERY_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 101;
    ActivityCreateComplainBinding binding;
    Uri selectedImage;
    String picturePath;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQ_ID = 22;
    private static final int REQ_ID = 1;
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_complain);


        initView();
        initListner();
    }

    private void initListner() {
        binding.btnOpenGallery.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.e("TAG", "onCreate: >>>>>>>>>>>>>  11 ");

                if (checkSelfPermission(storge_permissions_33[0], PERMISSION_REQ_ID) && checkSelfPermission(storge_permissions_33[1], PERMISSION_REQ_ID) && checkSelfPermission(storge_permissions_33[2], PERMISSION_REQ_ID) && checkSelfPermission(storge_permissions_33[3], PERMISSION_REQ_ID)) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, storge_permissions_33, REQ_ID);
                }

            } else {
                Log.e("TAG", "onCreate: >>>>>>>>>>>>>  22 ");
                if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                        checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID) && checkSelfPermission(REQUESTED_PERMISSIONS[3], PERMISSION_REQ_ID)) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);
                } else {

                    ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);

                }
            }

        });

    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, storge_permissions_33, requestCode);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: " + requestCode);
        if (requestCode == PERMISSION_REQ_ID) {
            Log.e(TAG, "onRequestPermissionsResult: " + PackageManager.PERMISSION_GRANTED + "  " + grantResults[0] + " " + grantResults[1]);
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: 11 ");

                showLongToast("Need permissions "
                        + Manifest.permission.READ_EXTERNAL_STORAGE + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.

        } else {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: 22 ");
                showLongToast("Need permissions "
                        + Manifest.permission.READ_EXTERNAL_STORAGE + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);

                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.

        }
    }

    private void showLongToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            selectedImage = data.getData();
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));

            Glide.with(this)
                    .load(selectedImage)
                    .apply(requestOptions)
                    .into(binding.image);
            String[] filePathColumn = {DATA};

            Cursor cursor = this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();


        }

    }

    public void onClickSubmit(View view) {
        String message = binding.etMessage.getText().toString().trim();
        String contact = binding.etContact.getText().toString().trim();
        if (message.equals("")) {
            Toast.makeText(this, "please Enter Message", Toast.LENGTH_SHORT).show();
//            binding.etMessage.setError("Required!");
            return;
        }
        RequestBody messagebody = RequestBody.create(MediaType.parse("text/plain"), message);
        RequestBody contactbody = RequestBody.create(MediaType.parse("text/plain"), contact);
        RequestBody userIdbody = RequestBody.create(MediaType.parse("text/plain"), sessionManager.getUser().getId());
        HashMap<String, RequestBody> map = new HashMap<>();
        MultipartBody.Part body = null;
        Call<RestResponse> call = null;
        if (picturePath != null) {
            File file = new File(picturePath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


        } else {
            // ll
        }
        call = RetrofitBuilder.create().addSupport(map, body);

        map.put("contact", contactbody);
        map.put("message", messagebody);
        map.put("userId", userIdbody);
        binding.animationView.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus()) {
                        Toast.makeText(CreateComplainActivity.this, "Complain Send Successfully", Toast.LENGTH_SHORT).show();
                        try {
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(CreateComplainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateComplainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                binding.animationView.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
            }
        });
    }

    private void initView() {
        if (isRTL(this)) {
            binding.back.setScaleX(isRTL(this) ? -1 : 1);
        }
    }
}