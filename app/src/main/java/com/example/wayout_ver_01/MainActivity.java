package com.example.wayout_ver_01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText Login_Id;
    private EditText Login_Pw;
    private Button Login_Btn;
    private CheckBox Login_Auto;
    private TextView Login_Join;
    private TextView Login_FindId;
    private TextView Login_FindPw;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Login_Id = findViewById(R.id.Login_Id);
        Login_Pw = findViewById(R.id.Login_Pw);
        Login_Btn = findViewById(R.id.Login_Btn);
        Login_Join = findViewById(R.id.Login_Join);
        Login_FindId = findViewById(R.id.Login_FindId);
        Login_FindPw = findViewById(R.id.Login_FindPw);
        Login_Auto = findViewById(R.id.Login_Auto);


        //동적퍼미션 작업
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            int permissionResult= checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if(permissionResult== PackageManager.PERMISSION_DENIED){
                String[] permissions= new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,10);
            }
        }else{
            //cv.setVisibility(View.VISIBLE);
        }

        // permission확인 절차
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Log.d("test", "=== sms전송을 위한 퍼미션 확인 ===" );
            // For device above MarshMallow
            boolean permission = getWritePermission();
            if(permission) {
                // If permission Already Granted
                // Send You SMS here
                Log.d("test", "=== 퍼미션 허용 ===" );
            }
        }
        else{
            // Send Your SMS. You don't need Run time permission
            Log.d("test", "=== 퍼미션 필요 없는 버전임 ===" );
        }

        if(PreferenceManager.getBoolean(mContext,"autoLogin"))
        {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }

        Login_Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JoinPhone.class);
                startActivity(intent);
                Log.e("Test", "회원 가입으로 이동");
            }
        });
        Login_FindPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, findPw.class);
                startActivity(intent);
            }
        });

        Login_FindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, findId.class);
                startActivity(intent);
                Log.e("Test", "Activity : 로그인 Activity // 현재 상황 : 아이디 찾기로 이동 // 로그 값 : ");
            }
        });

        // 로그인 버튼 클릭시
        Login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String ID = Login_Id.getText().toString().trim();
               String PW = Login_Pw.getText().toString().trim();

                if(Login_Auto.isChecked())
                {
                    // 오토 로그인 성공시 로그인 저장
                    PreferenceManager.setBoolean(mContext,"autoLogin",true);
                }
                else
                {
                    PreferenceManager.setBoolean(mContext, "autoLogin", false);
                }

                if(Login_Id.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                    Login_Id.requestFocus();
                    return;
                }
                if(Login_Pw.getText().toString().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    Login_Pw.requestFocus();
                    return;
                }


                selectJoin(ID,PW);
            }
        });



    }

    private void selectJoin (String userId, String userPw){
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<JoinRequest> call = retrofitInterface.selectJoin(userId,userPw);
        call.enqueue(new Callback<JoinRequest>() {
            @Override
            public void onResponse(Call<JoinRequest> call, Response<JoinRequest> response) {
                //통신 성공시
                if(response.isSuccessful() && response.body() != null)
                {
                    // 로그인 성공시
                    Boolean status = response.body().getStatus();
                    if(status)
                    {
                        Intent intent = new Intent(MainActivity.this,Home.class);
                        startActivity(intent);
                        //회원정보 저장
                        PreferenceManager.setString(mContext, "autoId", response.body().getUserId());
                        PreferenceManager.setInt(mContext,"autoIndex", response.body().getUserIndex());
                        PreferenceManager.setString(mContext,"autoNick",""+ response.body().getUserNick());
                        PreferenceManager.setString(mContext,"autoPhone",""+ response.body().getUserPhone());

                        Log.e("Test", "Activity :MainActivity // 로그인 성공 후 // 저장된 ID : " + response.body().getUserId());
                        Log.e("Test", "Activity :MainActivity // 로그인 성공 후 // 저장된 인덱스 : " + response.body().getUserIndex());
                        Log.e("Test", "Activity :MainActivity // 로그인 성공 후 // 저장된 닉네임 : " + response.body().getUserNick());
                        Log.e("Test", "Activity :MainActivity // 로그인 성공 후 // 저장된 폰번호 : " + response.body().getUserPhone());
                        finish();
                        Toast.makeText(MainActivity.this,  response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<JoinRequest> call, Throwable t) {
                Toast.makeText(MainActivity.this, "예기치 못한 오류가 발생하였습니다.\\n 고객센터에 문의바랍니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public boolean getWritePermission(){
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 10);
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 10: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permission is Granted
                    // Send Your SMS here
                }
            }
        }
    }


}