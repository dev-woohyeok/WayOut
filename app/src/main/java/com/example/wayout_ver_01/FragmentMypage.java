package com.example.wayout_ver_01;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMypage extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private TextView myPage_logout, myPage_Nick, myPage_friend, myPage_theme, myPage_cafe, myPage_delete;
    private ImageView myPage_reset;
    private CircleImageView myPage_profile;
    private ArrayList<Uri> imageSaveList;
    private static final int REQUEST_CODE = 0;
    private static final int GALLEY_CODE = 10;
    private String imageUri = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        String ID = PreferenceManager.getString(view.getContext(), "autoId");
        String PW = PreferenceManager.getString(view.getContext(), "autoPw");

        myPage_logout = view.findViewById(R.id.myPage_logout);
        myPage_reset = view.findViewById(R.id.myPage_reset);
        myPage_friend = view.findViewById(R.id.myPage_friend);
        myPage_theme = view.findViewById(R.id.myPage_theme);
        myPage_cafe = view.findViewById(R.id.myPage_cafe);
        myPage_delete = view.findViewById(R.id.myPage_delete);
        myPage_reset = view.findViewById(R.id.myPage_reset);
        myPage_profile = view.findViewById(R.id.myPage_profile);
        myPage_Nick = view.findViewById(R.id.myPage_Nick);

//        Integer userIndex = PreferenceManager.getInt(getContext(),"autoIndex");
//        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
//        Call<User> call = retrofitInterface.getUserProfile(userIndex);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if(response.isSuccessful() && response.body() != null)
//                {
//                    if(response.body().getUserProfile().length() > 3) {
//                        Glide.with(getContext())
//                                .load(response.body().getUserProfile())
//                                .into(myPage_profile);
//                    }
//
//
//                    Log.e(TAG, "내용 : 프로필 경로 : "+response.body().getUserProfile() );
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//
//            }
//        });


        myPage_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PreferenceManager.clear(view.getContext());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        myPage_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), UserReset.class);
                startActivity(intent);

            }
        });

        myPage_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> ListItems = new ArrayList<>();
                ListItems.add("앨범에서 선택");
                ListItems.add("프로필 사진 삭제");

                // String 배열 Items 에 ListItems 를 string[ListItems.size()] 형태로 생성한다.
                final String[] items = ListItems.toArray(new String[ListItems.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(items, (dialog, pos) -> {
                    String selectedText = items[pos];
                    switch (selectedText) {
                        case "앨범에서 선택":
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            Log.e(TAG, "내용 : 앨범 선택시 intent :" + intent);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            Log.e(TAG, "내용 : 앨범 선택시 Intent2 : " + intent);
                            startActivityForResult(intent, GALLEY_CODE);


                            break;
                        case "프로필 사진 삭제":
                            deleteUserProfile(PreferenceManager.getInt(getContext(),"autoIndex"));
                            break;
                    }
                });
                builder.show();
            }
        });


        myPage_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer userIndex = PreferenceManager.getInt(view.getContext(), "autoIndex");

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("회원 탈퇴");
                builder.setMessage("\n정말로 탈퇴하시겠습니까?\n");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("Test", "Activity : myPage // 회원탈퇴 확인");
                                userDelete(userIndex);
                            }
                        });
                builder.setNegativeButton("아니요",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("Test", "Activity : myPage // 회원탈퇴 거절");
                            }
                        });
                builder.show();

            }
        });

        return view;
    }

    // 절대 경로 가져오기 !!!!!
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Log.e(TAG, "내용 : String[] proj : " + proj);
        CursorLoader cursorLoader = new CursorLoader(getContext(), uri, proj, null, null, null);
        Log.e(TAG, "내용 : CursorLoader : " + cursorLoader);
        Cursor cursor = cursorLoader.loadInBackground();
        Log.e(TAG, "내용 :  Cursor : " + cursor);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        Log.e(TAG, "내용 : columnIndex : " + columnIndex);
        cursor.moveToFirst();
        Log.e(TAG, "내용 :  Cursor.moveToFirst : " + cursor.moveToFirst());
        String url = cursor.getString(columnIndex);
        Log.e(TAG, "내용 : url : " + url);
        cursor.close();
        return url;
    }

//        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == RESULT_OK) {
//                        Log.e(TAG, "result : " + result);
//                        Intent intent = result.getData();
//                        Log.e("test", "intent : " + intent);
//                        Uri uri = intent.getData();
//                        Log.e("test", "uri : " + uri);
////                        imageview.setImageURI(uri);
//                        Glide.with(FragmentMypage.this)
//                                .load(uri)
//                                .into(myPage_profile);
//                    }
//                }
//            });

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_CODE && data != null) {

            imageUri = getRealPathFromUri(data.getData());
            Log.e(TAG, "내용 : 이미지 절대 경로 : " + imageUri);
            // 이미지 둥글게 처리하기 : Glide 에서 설정
            RequestOptions cropOptions = new RequestOptions();
            Log.e(TAG, "내용 : cropOptions : " + cropOptions);



            File file = new File(imageUri);
            Log.e(TAG, "내용 : file : " + file);
            ArrayList<MultipartBody.Part> files = new ArrayList<>();
            Log.e(TAG, "내용 : ArrayList<MultipartBody.Part> : " + files);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            Log.e(TAG, "내용 : requestFile : " + requestFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "1234", requestFile);
            Log.e(TAG, "내용 : MultipartBody.Part : " + body);
//            files.add(body);
//            Log.e(TAG, "내용 : files : " + files);
            Integer Index = PreferenceManager.getInt(getContext(), "autoIndex");
            Log.e(TAG, "내용 보낼 유저 인덱스 : " + Index);


            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Log.e(TAG, "내용 retrofitInterface : " + retrofitInterface);
            Call<User> call = retrofitInterface.userProfile(body, Index);
            Log.e(TAG, "내용 : call : " + call);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if(response.isSuccessful() && response.body() != null)
                    {
                        Log.e(TAG, "내용 : === 프로필 파일 서버 업로드 : 성공 ===");
                        Glide.with(FragmentMypage.this)
                                .load(response.body().getUserProfile())
                                .into(myPage_profile);
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "내용 error message : " + t);

                }
            });
        }
    }

    private void deleteUserProfile(Integer userIndex){
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.deleteUserProfile(userIndex);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    Log.e(TAG, "내용 : === 프로필 파일 삭제 : 성공 ===");
                    Glide.with(getContext())
                            .load(response.body().getUserProfile())
                            .into(myPage_profile);
                    Log.e(TAG, "내용 : userProfile : " + response.body().getUserProfile());

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "내용 : === 프로필 파일 삭제 : 실패 // error message : " + t);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "내용 : ===========onSTOP================================");
    }

    @Override
    public void onResume() {
        super.onResume();
        String Nick = PreferenceManager.getString(getContext(), "autoNick");
        myPage_Nick.setText(Nick);

        Log.e(TAG, "내용 : ===== onResume ======================");

        Integer userIndex = PreferenceManager.getInt(getContext(),"autoIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.getUserProfile(userIndex);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful() && response.body() != null)
                {

                    Log.e(TAG, "내용 : onResume 프로필 경로 : "+response.body().getUserProfile() );
                    Log.e(TAG, "내용 : 이미지 가져오기 성공 ================");
                    Log.e(TAG, "내용 : 이미지 경로 : " + response.body().getUserProfile());
                        Glide.with(FragmentMypage.this)
                                .load(response.body().getUserProfile())
                                .into(myPage_profile);

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "내용 : onResume 유저 프로필 에러 : "+t );

            }
        });

    }


    private void userDelete(Integer userIndex) {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.userDelete(userIndex);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Test", "Activity : myPage // 현재상황 : 레트로핏 통신 성공 // 로그 값 :");
                    boolean status = response.body().getStatus();

                    if (status) {
                        Log.e("Test", "Activity : myPage // 현재상황 : PHP 실행 완료 // 로그 값 :" + response.body().getMessage());
                        Toast.makeText(getContext(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        PreferenceManager.clear(getContext());

                    } else {
                        Log.e("Test", "Activity : myPage // 현재상황 : PHP 처리중 문제발생 // 로그 값 :" + response.body().getMessage());
                        Toast.makeText(getContext(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Test", "Activity : myPage // 현재상황 : 레트로핏 통신 실패 // 로그 값 : " + t);
            }
        });
    }
}