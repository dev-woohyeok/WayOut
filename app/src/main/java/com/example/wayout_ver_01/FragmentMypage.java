package com.example.wayout_ver_01;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentMypage extends Fragment {

    private TextView myPage_logout, myPage_Nick, myPage_friend, myPage_theme, myPage_cafe, myPage_delete;
    private ImageView myPage_reset;
    private CircleImageView myPage_profile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        String ID = PreferenceManager.getString(view.getContext(),"autoId");
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


    @Override
    public void onResume() {
        super.onResume();
        String Nick = PreferenceManager.getString(getContext(),"autoNick");
        myPage_Nick.setText(Nick);
    }

    private void userDelete(Integer userIndex)
    {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.userDelete(userIndex);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Log.e("Test", "Activity : myPage // 현재상황 : 레트로핏 통신 성공 // 로그 값 :");
                    boolean status = response.body().getStatus();

                    if(status)
                    {
                        Log.e("Test", "Activity : myPage // 현재상황 : PHP 실행 완료 // 로그 값 :"+ response.body().getMessage()) ;
                        Toast.makeText(getContext(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        PreferenceManager.clear(getContext());

                    }
                    else
                    {
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