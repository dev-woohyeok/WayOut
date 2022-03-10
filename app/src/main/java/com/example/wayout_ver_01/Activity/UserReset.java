package com.example.wayout_ver_01.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.Retrofit.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReset extends AppCompatActivity {

    private TextView reset_id, reset_nick, reset_pw, reset_btn, reset_do;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reset);

        reset_id = findViewById(R.id.reset_id);
        reset_nick = findViewById(R.id.reset_nick);
        reset_pw = findViewById(R.id.reset_pw);
        reset_btn = findViewById(R.id.reset_btn);
        reset_do = findViewById(R.id.reset_do);

        reset_id.setText(PreferenceManager.getString(mContext, "autoId"));
        reset_nick.setText(PreferenceManager.getString(mContext, "autoNick"));

        Log.e("Test", "Activity : UserReset // 아이디 저장값 : " + reset_id.getText().toString().trim());

        // 비밀번호 재설정으로 이동
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserReset.this, UserResetPw.class);
                startActivity(intent);
                finish();
            }
        });

        // 수정 버튼
        reset_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = reset_id.getText().toString().trim();
                String PW = reset_pw.getText().toString().trim();
                String Nick = reset_nick.getText().toString().trim();
                Integer Index = PreferenceManager.getInt(mContext, "autoIndex");


                Log.e("Test", "Activity => userReset // reset_do 버튼 클릭시 // 수정 인덱스 : " + Index);
                Log.e("Test", "Activity => userReset // reset_do 버튼 클릭시 // 수정 닉네임 : " + Nick);


                //유효성 검사
                if(reset_id.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    reset_id.requestFocus();
                    return;
                }
                if(reset_nick.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                    reset_nick.requestFocus();
                    return;
                }
                if(reset_pw.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    reset_pw.requestFocus();
                    return;
                }

                //(아이디, Pw, 인덱스,닉네임)
                userReset(ID, PW,Nick,Index);
            }
        });

    }

    private void userReset(String userId, String userPw, String userNick, Integer userIndex) {
        // 레트로핏 인터페이스에 레트로핏 클라이언트로 객체를 생성해줌
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        // 서버로 보낼 데이터 형식과 경로 파일 내용을 정해준다.
        Call<User> call = retrofitInterface.userReset(userId, userPw, userNick, userIndex);
        // 서버로부터 결과값을 받아온다.
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("Test", "회원 정보 수정");
                    Boolean status = response.body().getStatus();
                    if (status) {
                        PreferenceManager.setString(mContext, "autoId", userId);
                        PreferenceManager.setString(mContext, "autoNick", userNick);

                        Log.e("Test", "Activity => userReset // 레트로핏 통신 성공 후 // 수정 아이디 :" + response.body().getUserId());
                        Log.e("Test", "Activity => userReset // 레트로핏 통신 성공 후 // 수정 닉네임 :" + response.body().getUserNick());
                        Log.e("Test", "Activity => userReset // 레트로핏 통신 성공 후 // 수정 인덱스 :" + response.body().getUserIndex());

                        // 회원정보 수정 하고
                        finish();
                        Toast.makeText(UserReset.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserReset.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Test", "통신 실패 // 오류 메세지 : " + t);
                Toast.makeText(UserReset.this, "예기치 못한 오류가 발생하였습니다.\n       고객센터에 문의바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}