package com.example.wayout_ver_01.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.FreeComment_adapter;
import com.example.wayout_ver_01.RecyclerView.FreeRead_adapter;
import com.example.wayout_ver_01.Retrofit.DTO_free_reply;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeBoard_reply extends AppCompatActivity {
    TextView freeComment_writer, freeComment_content, freeComment_date, freeComment_commit, freeComment_number;
    EditText freeComment_et_content;
    RecyclerView freeComment_rv;
    String reply_index;
    FreeComment_adapter freeComment_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard_reply);

        // 댓글 정보 가져오기
        Intent intent = getIntent();
        reply_index = intent.getStringExtra("reply_index");

        // 어뎁터 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(FreeBoard_reply.this, LinearLayoutManager.VERTICAL, false);
        freeComment_rv.setLayoutManager(layoutManager);
        freeComment_adapter = new FreeComment_adapter(FreeBoard_reply.this);
        freeComment_rv.setAdapter(freeComment_adapter);

        // 서버에서 답글 가져오기
        setComment(reply_index);
    }

    private void setComment(String reply_index) {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_free_reply>> call = retrofitInterface.getFreeComment(reply_index);
        call.enqueue(new Callback<ArrayList<DTO_free_reply>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DTO_free_reply>> call,@NonNull Response<ArrayList<DTO_free_reply>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    for(int i = 0; i < response.body().size(); i++){
                        freeComment_adapter.
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<DTO_free_reply>> call,@NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "답글 불러오기 오류 : " + t, Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void setView() {
        freeComment_writer = findViewById(R.id.freeRead_comment_writer);
        freeComment_content = findViewById(R.id.freeRead_comment_content);
        freeComment_date = findViewById(R.id.freeRead_comment_date);
        freeComment_commit = findViewById(R.id.freeRead_comment_commit);
        freeComment_et_content = findViewById(R.id.freeRead_etComment_content);
        freeComment_rv = findViewById(R.id.freeRead_comment_rv);
        freeComment_number = findViewById(R.id.freeRead_comment_number);
    }
}