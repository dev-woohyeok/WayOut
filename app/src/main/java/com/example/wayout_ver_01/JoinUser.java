package com.example.wayout_ver_01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinUser extends AppCompatActivity {

    private EditText Join_Id;
    private EditText Join_Pw;
    private EditText Join_Nick;
    private EditText Join_PwCk;
    private Button Join_Btn;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_user);

        Join_Id = findViewById(R.id.Join_Id);
        Join_Pw = findViewById(R.id.Join_Pw);
        Join_Nick = findViewById(R.id.Join_Nick);
        Join_Btn = findViewById(R.id.Join_Btn);
        Join_PwCk = findViewById(R.id.Join_Pw_ck);
        mContext = this;


        Join_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String PhoneNum = intent.getStringExtra("phone");
                String ID = Join_Id.getText().toString().trim();
                String PW = Join_Pw.getText().toString().trim();
                String Nick = Join_Nick.getText().toString().trim();
                Log.e("Test", "서버쪽으로 보내는 정보 " +
                        "ID : " + ID + " PW : " + PW +" Nick : " + Nick +" Phone : " + PhoneNum);

                if(Join_Id.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    Join_Id.requestFocus();
                    return;
                }
                if(Join_Nick.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show();
                    Join_Nick.requestFocus();
                    return;
                }

                if(Join_Pw.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    Join_Pw.requestFocus();
                    return;
                }
                if(!Join_Pw.getText().toString().equals(Join_PwCk.getText().toString()))
                {
                    Join_Pw.setText("");
                    Join_PwCk.setText("");
                    Join_Pw.requestFocus();
                    return;
                }

                insertJoin(ID,PW,Nick,PhoneNum);
            }
        });
    }

    private void insertJoin(String userId, String userPw, String userNick, String userPhone)
    {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<JoinRequest> call = retrofitInterface.insertJoin(userId, userPw, userNick ,userPhone);
        call.enqueue(new Callback<JoinRequest>() {
            @Override
            public void onResponse(Call<JoinRequest> call, Response<JoinRequest> response) {
                // 서버로 부터 통신이 성공적으로 받아졌는지 확인 , Json body 에 데이터가 들어있는지 확인
                if(response.isSuccessful() && response.body() != null)
                {
                    Boolean status = response.body().getStatus();
                    if(status)
                    {
                        Toast.makeText(JoinUser.this, "회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(JoinUser.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(JoinUser.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<JoinRequest> call, Throwable t) {
            }
        });
    }

}