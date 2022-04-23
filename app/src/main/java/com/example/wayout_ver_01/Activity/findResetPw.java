package com.example.wayout_ver_01.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.Retrofit.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class findResetPw extends AppCompatActivity {

    TextView findReset_do;
    EditText findReset_new_pw, findReset_new_pwCk;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_reset_pw);

        findReset_do = findViewById(R.id.findReset_do);
        findReset_new_pw = findViewById(R.id.findReset_new_pw);
        findReset_new_pwCk = findViewById(R.id.findReset_new_pwCk);


        findReset_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String userPw = findReset_new_pw.getText().toString();
                String userPhone = intent.getStringExtra("phone");


                if(findReset_new_pw.getText().toString().length() == 0 )
                {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    findReset_new_pw.requestFocus();
                    return;
                }


                RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                Call<User> call = retrofitInterface.findResetPw(userPw, userPhone);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(response.isSuccessful() && response.body() != null)
                        {
                            boolean status = response.body().getStatus();

                            if(status)
                            {
                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(findResetPw.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

            }
        });

    }
}