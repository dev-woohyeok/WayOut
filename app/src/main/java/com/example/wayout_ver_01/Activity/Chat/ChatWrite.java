package com.example.wayout_ver_01.Activity.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wayout_ver_01.Class.JsonMaker;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_room;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityChatWriteBinding;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatWrite extends AppCompatActivity {
    private ActivityChatWriteBinding bind;
    private InputMethodManager imm;
    private Bitmap bitmap;
    private String user_max = null;
    private String chat_info = null;
    // 오픈
    private final int ROOM_TYPE_OPEN = 0;
    private final int ROOM_TYPE_CLOSE = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityChatWriteBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 이미지 선택
        bind.chatWriteImage.setOnClickListener(v -> {
            Matisse.from(ChatWrite.this)
                    .choose(MimeType.ofAll())
                    .countable(false)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showPreview(false)
                    .forResult(1111);
        });

        /* Info 글자수 */
        bind.chatWriteInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (bind.chatWriteInfo.isFocusable() && !s.toString().isEmpty()) {
                    int number = bind.chatWriteInfo.getText().toString().length();
                    bind.chatWriteInfooNumber.setText("" + number);
                    chat_info = bind.chatWriteInfo.getText().toString();
                    if (number >= 100) {
                        number = 100;
                        Toast.makeText(getApplicationContext(), "100자까지 작성이 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 인원 제한 선택
        bind.chatWriteMax.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                user_max = newItem;
            }
        });

        // 작성 버튼시 => 서버에 채팅방 만들기
        bind.chatWriteSubmit.setOnClickListener(v -> {

            if (bind.chatWriteTitle.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "모임 이름을 작성해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user_max.isEmpty()) {
                Toast.makeText(getApplicationContext(), "모집 인원을 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (bitmap == null) {
                Toast.makeText(getApplicationContext(), "대표 이미지를 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = bind.chatWriteTitle.getText().toString();
            String max = user_max;
            String info = chat_info;
            String writer = PreferenceManager.getString(ChatWrite.this, "userId");

            Log.e("//===========//","================================================");
            Log.e("","\n"+"[ ChatWrite : name "+name+" ]");
            Log.e("","\n"+"[ ChatWrite : max "+max+" ]");
            Log.e("","\n"+"[ ChatWrite : info "+info+" ]");
            Log.e("","\n"+"[ ChatWrite : writer "+writer+" ]");
            Log.e("//===========//","================================================");

            RequestBody room_name = RequestBody.create(name, MediaType.parse("text/plain"));
            RequestBody room_max = RequestBody.create(max, MediaType.parse("text/plain"));
            RequestBody room_info = RequestBody.create(info, MediaType.parse("text/plain"));
            RequestBody room_writer = RequestBody.create(writer, MediaType.parse("text/plain"));

            /* 유효성 체크 */
            File file = bitmapToFile(bitmap, "file");
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-date"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("room_image", "room_image", requestFile);

            /* 서버에 올릴 값 생성 */
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_room> call = retrofitInterface.writeChatRoom(room_name, room_writer, room_max, room_info, body);
            call.enqueue(new Callback<DTO_room>() {
                @Override
                public void onResponse(Call<DTO_room> call, Response<DTO_room> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Socket socket = Service_chat.getSocket();
                        String user_id = PreferenceManager.getString(getApplicationContext(),"userId");
                        String room_id = response.body().getRoom_id();
                        String msg = JsonMaker.makeJson("join",room_id, user_id,user_id + "님이 입장하셧습니다.","","","io",name);
                        /* 방 생성 => join, in */
                        Send send = new Send(socket,msg);
                        send.start();

                        Log.e("//===========//","================================================");
                        Log.e("","\n"+"[ ChatWrite // user_id " + user_id + " ]");
                        Log.e("","\n"+"[ ChatWrite // room_id " + room_id + " ]");
                        Log.e("","\n"+"[ ChatWrite // msg " + msg + " ]");
                        Log.e("//===========//","================================================");

                    }
                    finish();
                }

                @Override
                public void onFailure(Call<DTO_room> call, Throwable t) {

                }
            });

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1111 && resultCode == RESULT_OK) {
            Uri image_uri = Matisse.obtainResult(data).get(0);
            Glide.with(ChatWrite.this)
                    .asBitmap()
                    .load(image_uri)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            /* 서버에 올릴 파일 저장 */
                            bind.chatWriteImage.setImageBitmap(resource);
                            bitmap = resource;
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }
    }

    // 비트맵 -> File로 변환
    private File bitmapToFile(Bitmap getBitmap, String filename) {
        // bitmap 을 담을 File 을 생성한다
        File f = new File(getCacheDir(), filename);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // bitmap 을 byte array 바꾼다
        Bitmap bitmap = getBitmap;
        // bitmap 을 byte 배열로 담는다.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 비트맵을 png 형식으로 byte[] 으로 변환하여 bos 에 담는다.
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        // byte [] 을 File 에 담는다.
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    /* 키보드 자동 내리기 세팅 */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            // 사각형을 만드는 클래스 (Rectangle) 직사각형
            Rect rect = new Rect();
            // focus 된 View 의 전체 면적을 가져옴
            focusView.getGlobalVisibleRect(rect);
            // 현재 이벤트가 일어난 x, y 좌표를 가져옴
            int x = (int) ev.getX(), y = (int) ev.getY();
            // 클릭이벤트가 focusView 범위 안에 일어났는지 확인
            if (!rect.contains(x, y)) {
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}