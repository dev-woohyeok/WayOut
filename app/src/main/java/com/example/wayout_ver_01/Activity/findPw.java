package com.example.wayout_ver_01.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.Class.GenerateCertNumber;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.Retrofit.User;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class findPw extends AppCompatActivity {

    private EditText findPW_number, findPW_numberCk;
    private TextView findPW_submit, findPW_ck, findPW_reset, findPW_timer;
    private Button findPW_next;
    private String smsContents;
    private CountDownTimer countDownTimer;
    private static final long START_TIME_MILLTS = 10000;
    private long mTimeLeftInMillis = START_TIME_MILLTS;
    private boolean mTimerRunning;
    private Boolean certCk = false;
    private Boolean submitCk = false;
    private boolean changeActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        findPW_number = findViewById(R.id.findPW_number);
        findPW_numberCk = findViewById(R.id.findPW_numberCk);
        findPW_submit = findViewById(R.id.findPW_submit);
        findPW_next = findViewById(R.id.findPW_next);
        findPW_ck = findViewById(R.id.findPW_ck);
        findPW_reset = findViewById(R.id.findPW_reset);
        findPW_timer = findViewById(R.id.findPW_timer);


        // 휴대폰 인증 후
        findPW_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = findPW_number.getText().toString().trim();

                // 유효성 검사
                if (certCk == false) {
                    Toast.makeText(getApplicationContext(), "휴대폰 인증을 진행해주세요", Toast.LENGTH_SHORT).show();
                    findPW_number.requestFocus();
                } else {
                    // 인증 완료 후 실행
                    String PhoneNumber = findPW_number.getText().toString();

                    Intent intent = new Intent(findPw.this, findResetPw.class);
                    intent.putExtra("phone", phoneNumber);
                    PreferenceManager.setBoolean(getApplicationContext(), "phoneCk", false);
                    startActivity(intent);
                    finish();

                    Log.e("Test", "폰 인증 후 , 넘어가는 폰 번호 : " + phoneNumber);
                }
            }
        });

        // 인증번호 확인 절차
        // 1. 핸드폰 번호 중복 확인
        // 2. 중복 아닐시 인증 번호 생성 => code table 에 인증번호, 휴대폰 번호 저장
        // 3. 인증번호 문자 발송 => 카운트 다운 30초(시연) , 다시 보내기 => 보내기, 카운트 다운 초기화
        // 4. 인증번호 확인 버튼 => 인증번호 확인 통과시 boolean cert = true //
        // 5. 다음 버튼 클릭 => boolean cert = true 일때 진행 => 진행 완료 후 cert false 로 전환

        // 인증 번호 발송 =====================================
        findPW_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 유효성 체크
                if (findPW_number.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "핸드폰 번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    findPW_number.requestFocus();
                    return;
                }
                // 핸드폰 번호 11자리 입력시
                if (findPW_number.getText().toString().length() < 11 || findPW_number.getText().toString().length() > 11) {
                    Toast.makeText(getApplicationContext(), "휴대폰 번호를 정확히 입력해주세요", Toast.LENGTH_SHORT).show();
                    findPW_number.requestFocus();
                    return;
                }
                if(!mTimerRunning) {
                    sendSMS();
//                  // 인증번호 발송 위치 액티비티 알려주기
//                  // 회원가입 = 0, 아이디 찾기 = 1, 비밀번호 찾기 = 2
                    PreferenceManager.setInt(getApplicationContext(),"submitAct", 2);
                }
            }
        });



        // 인증 번호 입력 후 다음 인증 확인 버튼
        findPW_ck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 입력한 인증번호랑 같으면
                if (findPW_numberCk.getText().toString().equals(PreferenceManager.getString(getApplicationContext(), "phoneNum"))) {
                    // 버튼 이용 불가 설정
                    findPW_numberCk.setEnabled(false);
                    findPW_number.setEnabled(false);
                    findPW_submit.setEnabled(false);
                    findPW_reset.setClickable(false);
                    findPW_reset.setFocusable(false);
                    findPW_reset.setTextColor(Color.parseColor("#000000"));
                    findPW_reset.setText("인증이 완료 되었습니다.");


//                    // 가입아이디 다시 보내기
//                    try {
//                        Log.d("test", "=== 문자 전송 시작 ===");
//                        //전송
//                        String phoneNumStr = "5555215554";
//
//                        // 인증 번호랑 입력한 번호가 같으면 확인 후 통과
//                        String SMSContents = PreferenceManager.getString(getApplicationContext(),"findId");
//
//                        SmsManager smsManager = SmsManager.getDefault();
//                        smsManager.sendTextMessage(phoneNumStr, null, "[WayOut] 아이디는 [" + SMSContents + "] 입니다.", null, null);
//                        //SMSContents 앞서 전역변수로 입력한, 번호 [랜덤숫자 생성] 포스팅의 메서드를 활용하여 넣으면, 랜덤으로 숫자가 보내진다.
//                        Log.d("test", "=== 문자 전송 완료 ===");
//
//                    } catch (Exception e) {
//                        Log.d("test", "=== 문자 전송 실패 === 에러코드 e : " + e);
//                        e.printStackTrace();
//                    }


                    stopTimer();
                    // 인증확인
                    certCk = true;
                    Log.e("Test", "Activity : JoinPhone // 인증번호 확인 // 결과 값 : " + findPW_numberCk.getText().toString().equals(PreferenceManager.getString(getApplicationContext(), "phoneNum")));
                }

            }
        });

        // 리셋 버튼 클릭스 -> 인증 번호 update 하고 재확인
        findPW_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
                resetTimer();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String smsNum = intent.getStringExtra("smsNum");
        findPW_numberCk.setText(smsNum);
    }

    private void startTimer() {
        // 카운트 다운 시간 설정 ( 총 진행 시간 , 줄이는 단위 1초 )
        countDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateCountDownText();
                // 1초 지날때 마다 할 일
                mTimeLeftInMillis = millisUntilFinished;
                // 분,초 갱신
                Log.e("Test", "Activity : JoinPhone // 현재 상황 : 1초마다 진행되는 일들 // 로그 값 : ");
            }

            @Override
            public void onFinish() {
                // CountDown 종료시 할 일
                findPW_submit.setEnabled(true);
                findPW_number.setEnabled(true);

                mTimerRunning = false;
                findPW_reset.setVisibility(View.INVISIBLE);
                findPW_timer.setVisibility(View.INVISIBLE);
                findPW_numberCk.setEnabled(false);
                findPW_numberCk.setText("");
                findPW_number.requestFocus();

                Log.e("Test", "Activity : JoinPhone // 현재 상황 : CountDown 쓰레드 종료시 실행 // 로그 값 : ");

                mTimeLeftInMillis = START_TIME_MILLTS;

                // 인증번호 유효성 제거
                PreferenceManager.removeKey(getApplicationContext(),"phoneNum");

                // 인증시간 초과시 확인
                if(!changeActivity) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(findPw.this);
                    builder.setTitle("입력 시간 초과");
                    builder.setMessage("입력시간이 초과하였습니다.\n번호 인증을 다시 요청해 주십시요");
                    builder.setPositiveButton("확인", null);
                    builder.create().show();
                }
            }
        }.start();

        mTimerRunning = true;
    }

    private void resetTimer() {
        countDownTimer.cancel();
        Log.e("Test", "Activity : JoinPhone // 현재 상황 : 리셋 타이머 종료 // 로그 값 : ");
        mTimeLeftInMillis = START_TIME_MILLTS;
        updateCountDownText();
        Log.e("Test", "Activity : JoinPhone // 현재 상황 : 분초 업데이트 // 로그 값 : ");
        startTimer();
        Log.e("Test", "Activity : JoinPhone // 현재 상황 : 리셋버튼 클릭햇을때 // 로그 값 : ");
    }

    private void updateCountDownText() {
        // 분, 초 표현
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        // ??분 ??초 로 포멧
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d분 %02d초", minutes, seconds);
        findPW_timer.setText(timeLeftFormatted);
    }

    private void stopTimer() {
        countDownTimer.cancel();
        mTimerRunning = false;
        findPW_timer.setVisibility(View.INVISIBLE);
    }

    // 문자 메세지 전송 부분
    private void sendSMS() {

        String phone = findPW_number.getText().toString();
        //전송
        String phoneNumStr = "5555215554";
        GenerateCertNumber generateCertNumber = new GenerateCertNumber();
        // 인증 번호랑 입력한 번호가 같으면 확인 후 통과
        String SMSContents = generateCertNumber.excuteGenerate();
        // 인증 번호 임시 저장
        PreferenceManager.setString(getApplicationContext(), "phoneNum", SMSContents);

        // 레트로핏 통신 => 핸드폰 번호가 중복이 있는지 확인
        Log.e("Test", "Activity : joinPhone // 현재 상황 : 레트로핏 통신 시작 // 로그 값 : ");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<User> call = retrofitInterface.userPhoneCk(phone, SMSContents);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean status = response.body().getStatus();

                    // ======================= 인증번호 전송 완료 ======================================
                    if (!status) {
                        Log.e("Test", "Activity : joinPhone // 현재 상황 : 레트로핏 통신 성공// 휴대폰 인증번호 등록 // 로그 값 : " + response.body().getMessage());
                        Toast.makeText(getApplicationContext(), "문자가 발송되었습니다.", Toast.LENGTH_SHORT).show();

                        // ===================== 메세지 발송 부분 =========================================
                        try {
                            Log.d("test", "=== 문자 전송 시작 ===");

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumStr, null, "[WayOut] 인증번호는 [" + SMSContents + "] 입니다.", null, null);
                            //SMSContents 앞서 전역변수로 입력한, 번호 [랜덤숫자 생성] 포스팅의 메서드를 활용하여 넣으면, 랜덤으로 숫자가 보내진다.
                            Log.d("test", "=== 문자 전송 완료 ===");

                        } catch (Exception e) {
                            Log.d("test", "=== 문자 전송 실패 === 에러코드 e : " + e);
                            e.printStackTrace();
                        }

                        // ======================= 인증번호 전송 완료 ======================================
                        // 인증번호
                        String phoneNum = PreferenceManager.getString(getApplicationContext(), "phoneNum");

                        // 리셋 버튼 보이기, 카운트 다운 쓰레드 작동

                        findPW_reset.setVisibility(View.VISIBLE);
                        findPW_reset.setText("인증번호가 도착하지 않으셨나요?");
                        findPW_timer.setVisibility(View.VISIBLE);
                        findPW_numberCk.setEnabled(true);

                        // 인증번호 , 휴대폰 입력창 사용불가
                        findPW_submit.setEnabled(false);
                        findPW_number.setEnabled(false);
                        PreferenceManager.setString(getApplicationContext(),"findId",response.body().getUserId());
                        Log.e("Test", "Activity : findID // 현재 상황 : 인증번호 발급 // 로그 값 서버로 부터 받은 아이디 : " + response.body().getUserId());

                        startTimer();
                        // ========================== 인증번호 발송 실패 =========================================
                    } else {

                        Toast.makeText(getApplicationContext(), "등록되지 않은 핸드폰 번호입니다.", Toast.LENGTH_SHORT).show();
                        Log.e("Test", "Activity : joinPhone // 현재 상황 : 레트로핏 쿼리 데이터 오류 // 로그 값 : " + response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Test", "Activity : joinPhone // 현재 상황 : 레트 로핏 통신 실패// 로그 값 : " + t);
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        mTimerRunning = false;


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            changeActivity = true;
        }
        Log.e("Test", "Activity : findId // 현재 상황 // 로그 값 : ");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.removeKey(getApplicationContext(),"phoneNum");
    }
}