package com.example.wayout_ver_01;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMypage extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private TextView myPage_logout, myPage_Nick, myPage_friend, myPage_theme, myPage_cafe, myPage_delete;
    private ImageView myPage_reset;
    private CircleImageView myPage_profile;
    private  ArrayList<Uri> imageSaveList;
    private static final int REQUEST_CODE = 0;


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
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            launcher.launch(intent);
//                            Intent intent = new Intent(Intent.ACTION_PICK);
//                            intent.setType("image/*");
//                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_CODE);

                            break;
                        case "프로필 사진 삭제":
                            Toast.makeText(getContext(), "이미지 버튼 삭제", Toast.LENGTH_SHORT).show();
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

    public void CreateListDialog() {
//        final List<String> ListItems = new ArrayList<>();
//        ListItems.add("앨범에서 선택");
//        ListItems.add("프로필 사진 삭제");
//
//        // String 배열 Items 에 ListItems 를 string[ListItems.size()] 형태로 생성한다.
//        final String[] items = ListItems.toArray(new String[ListItems.size()]);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setItems(items, (dialog, pos) -> {
//            String selectedText = items[pos];
//            switch (selectedText) {
//                case "앨범에서 선택":
//                    Intent intent = new Intent();
//                    intent.setType("image/*");
//                    intent.setAction(Intent.ACTION_GET_CONTENT);
//                    launcher.launch(intent);
//            }
//        });

    }

//    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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
        if(requestCode == REQUEST_CODE)
        {
            if (data != null)
            {
                Uri fildPath = data.getData();
                Log.e(TAG, "내용 : " + fildPath );
                imageSaveList.add(fildPath);
                myPage_profile.setImageURI(fildPath);
                Glide.with(FragmentMypage.this)
                        .load(fildPath)
                        .into(myPage_profile);

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String Nick = PreferenceManager.getString(getContext(), "autoNick");
        myPage_Nick.setText(Nick);
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