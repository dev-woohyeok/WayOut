package com.example.wayout_ver_01.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class UserResetPw extends AppCompatActivity {

    TextView resetPw_do;
    EditText resetPw_pw, resetPw_new_pw, resetPw_new_pwCk;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reset_pw);

        resetPw_do = findViewById(R.id.resetPw_do);
        resetPw_pw = findViewById(R.id.resetPw_pw);
        resetPw_new_pw = findViewById(R.id.resetPw_new_pw);
        resetPw_new_pwCk = findViewById(R.id.resetPw_new_pwCk);

        Integer Index = PreferenceManager.getInt(mContext, "autoIndex");

        resetPw_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Pw = resetPw_pw.getText().toString().trim();
                String newPw = resetPw_new_pw.getText().toString().trim();
                String newPwCk = resetPw_new_pwCk.getText().toString().trim();
                Integer userIndex = PreferenceManager.getInt(mContext,"autoIndex");
                Log.e("Test", "Activity : userResetPw // userPwReset userIndex 값 : " + userIndex);

               if ( resetPw_pw.getText().length() == 0 )
               {
                   Toast.makeText(getApplicationContext(), "현재 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                   resetPw_pw.requestFocus();
                   return;
               }
                if ( resetPw_new_pw.getText().length() == 0 )
                {
                    Toast.makeText(getApplicationContext(), "새 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    resetPw_new_pw.requestFocus();
                    return;
                }
                if ( resetPw_new_pwCk.getText().length() == 0 )
                {
                    Toast.makeText(getApplicationContext(), "비밀번호 확인을 입력하세요", Toast.LENGTH_SHORT).show();
                    resetPw_new_pwCk.requestFocus();
                    return;
                }
                if ( !resetPw_new_pwCk.getText().toString().equals(resetPw_new_pwCk.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    resetPw_new_pw.setText("");
                    resetPw_new_pwCk.setText("");
                    resetPw_new_pw.requestFocus();
                    return;
                }
                userPwReset(Pw,newPw,userIndex);
            }
        });

    }
    private void userPwReset(String userPw, String userNewPw , Integer userIndex)
    {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.userPwReset(userPw, userNewPw, userIndex);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Boolean status = response.body().getStatus();
                    Log.e("Test", "Activity : userResetPw // userPwReset 레트로핏 통신 성공 // ");

                    if (status)
                    {
                        Log.e("Test", "Activity : userResetPw // userPwReset 레트로핏 Status : true // ");
                        Toast.makeText(mContext, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        Log.e("Test", "Activity : userResetPw // userPwReset 레트로핏 Status : false // ");
                        Toast.makeText(mContext, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(mContext, "예기치 못한 오류가 발생하였습니다.\n       고객센터에 문의바랍니다.", Toast.LENGTH_SHORT).show();
                Log.e("Test", "Activity : userResetPw // userPwReset 레트로핏 통신 실패 // 오류 데이터 : " + t);
            }
        });
    }

}