package com.example.wayout_ver_01.Activity;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_board;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;



import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeBoard_write extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private TextView freeWrite_btn;
    private EditText freeWrite_title, freeWrite_content;
    private ImageView freeWrite_img;
    private RecyclerView freeWrite_rv;
    private ProgressDialog progressDialog;
    private ArrayList<Uri> uriList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_board_write);

        freeWrite_btn = findViewById(R.id.freeWrite_btn);
        freeWrite_title = findViewById(R.id.freeWrite_title);
        freeWrite_content = findViewById(R.id.freeWrite_content);
        freeWrite_img = findViewById(R.id.freeWrite_img);

        freeWrite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(FreeBoard_write.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

            // Retrofit 전송
                String title = freeWrite_title.getText().toString().trim();
                String content = freeWrite_content.getText().toString().trim();
                String date = getTime();
                String writer = PreferenceManager.getString(FreeBoard_write.this,"autoNick");
                Log.e(TAG, "내용 : writer : "+ writer);

                RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                Call<DTO_board> call = retrofitInterface.writeFreeBoard(writer,title,content,date);
                call.enqueue(new Callback<DTO_board>() {
                    @Override
                    public void onResponse(@NonNull Call<DTO_board> call,@NonNull Response<DTO_board> response) {
                        if(response.isSuccessful() && response.body() != null )
                            progressDialog.dismiss();
                        {
                            if(response.body().isSuccess()) {
//                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "내용 :  request 성공 , response :" + response.body() );
                                finish();
                            }
                        }
                    }


                    @Override
                    public void onFailure(@NonNull Call<DTO_board> call, @NonNull Throwable t) {
                        Toast.makeText(getApplicationContext(), "전성 오류", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "내용 : 에러메세지 : " + t.getLocalizedMessage());
                        progressDialog.dismiss();
                    }
                });
            }
        });



        // 이미지 가져오기
        freeWrite_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 2222);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) { // 이미지를 선택하지 않았을때
            Log.e(TAG, "내용 : 이미지를 선택하지 않았을때");
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else {// 이미지를 하나라도 선택한 경우
            if (data.getClipData() == null) { // 이미지를 하나만 선택한 경우
                Log.e(TAG, "내용 : 이미지 하나만 선택 : " + data.getData());
                Uri imageUri = data.getData();
                uriList.add(imageUri);


            }
            else
            { //  이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e(TAG, "내용 : ClipData : " + String.valueOf(clipData.getItemCount()) + clipData );

                if(clipData.getItemCount() > 3 ){ // 3장 이상 선택시
                    Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                }
                else { // 1~3 장 선택한 경우
                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri(); // 선택한 이미지의 uri를 가져온다.
                        try{
                            uriList.add(imageUri); // uri 를 list 에 담는다.

                        } catch (Exception e) {
                            Log.e(TAG,"에러메세지 : " + e.getLocalizedMessage());
                        }
                    }

                }
            }

        }
    }

    public String getTime() {
        // 현재 시간 가져오기
        long now = System.currentTimeMillis();
        // Date 생성하기
        Date date = new Date(now);
        // 가져오고 싶은 형식으로 가져오기
        /* yy : 년도 , MM : 날짜, dd : 날짜 ,a : 오전, 오후,
         hh : 12시간제 , HH : 24 시간제, mm : 분, ss : 초 */
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일 a HH시 mm분", Locale.KOREA);
        String nowDate = dateFormat.format(date);
        Log.e(TAG, "내용 : 작성 시간 : " + nowDate );

        return nowDate;
    }
}