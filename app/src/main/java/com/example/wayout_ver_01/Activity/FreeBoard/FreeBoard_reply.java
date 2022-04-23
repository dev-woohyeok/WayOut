package com.example.wayout_ver_01.Activity.FreeBoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.FreeComment_adapter;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.FreeRead_reply;
import com.example.wayout_ver_01.Retrofit.DTO_free_reply;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeBoard_reply extends AppCompatActivity {
    TextView freeComment_writer, freeComment_content, freeComment_date, freeComment_commit, freeComment_number;
    EditText freeComment_et_content;
    RecyclerView freeComment_rv;
    String reply_index, board_number, writer, content, date;
    FreeComment_adapter freeComment_adapter;
    ProgressDialog progressDialog;
    InputMethodManager imm;
    NestedScrollView freeReply_scroll;
    int page = 1;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard_reply);
        // View 세팅
        setView();

        // 인텐트로 받아온 값 세팅
        setIntent();

        //키보드 세팅
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // 어뎁터 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(FreeBoard_reply.this, LinearLayoutManager.VERTICAL, false);
        freeComment_rv.setLayoutManager(layoutManager);
        freeComment_adapter = new FreeComment_adapter(FreeBoard_reply.this,freeComment_et_content, imm);
        freeComment_rv.setAdapter(freeComment_adapter);

        // 서버에서 답글 가져오기
        setComment(reply_index);

        // 답글 작성 하기
        freeComment_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean setMode = freeComment_adapter.getMode();
                try {
                    if(!setMode) {
                        uploadComment(reply_index);
                    } else {
                        updateComment(reply_index);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        freeReply_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    page++;
                    getScroll(reply_index,page);
                }
            }
        });


    }

    private void getScroll(String reply_index, int page) {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_free_reply>> call = retrofitInterface.getFreeCommentScroll(reply_index,page);
        call.enqueue(new Callback<ArrayList<DTO_free_reply>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DTO_free_reply>> call, @NonNull Response<ArrayList<DTO_free_reply>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        freeComment_adapter.addItem(new FreeRead_reply(
                                response.body().get(i).getReply_writer(),
                                response.body().get(i).getReply_content(),
                                response.body().get(i).getReply_date(),
                                response.body().get(i).getReply_index(),
                                response.body().get(i).getBoard_number()
                        ));
                        freeComment_adapter.notifyItemInserted(i + (page - 1) * 6);
                    }
                }
            }
            @Override
            public void onFailure
                    (@NonNull Call<ArrayList<DTO_free_reply>> call, @NonNull Throwable t) {
//                Toast.makeText(getApplicationContext(), "답글 불러오기 오류 : " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setIntent() {
        Intent intent = getIntent();
        reply_index = intent.getStringExtra("reply_index");
        board_number = intent.getStringExtra("board_number");
        writer = intent.getStringExtra("reply_writer");
        content = intent.getStringExtra("reply_content");

        try {
            date = DateConverter.resultDateToString(intent.getStringExtra("reply_date"), "M월 d일 a h:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        freeComment_writer.setText(writer);
        freeComment_content.setText(content);
        freeComment_date.setText(date);
    }

    private void updateComment(String reply_index) throws ParseException {
        String writerDate = DateConverter.setDate();
        String index = freeComment_adapter.getIndex();

        if (freeComment_et_content.getText().toString().isEmpty()) {
            freeComment_et_content.setError("내용을 입력해주세요");
            freeComment_et_content.requestFocus();
            return;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_free_reply> call = retrofitInterface.updateFreeComment(index,freeComment_et_content.getText().toString(),writerDate);
        call.enqueue(new Callback<DTO_free_reply>() {
            @Override
            public void onResponse(Call<DTO_free_reply> call, Response<DTO_free_reply> response) {
                if(response.isSuccessful() && response.body() != null ) {
                    freeComment_adapter.setItem(freeComment_adapter.getItemPos(), freeComment_et_content.getText().toString());
                    freeComment_et_content.setText("");
                    freeComment_adapter.endMode();
                    Log.e(TAG, "내용 : 답글 인덱스 : " + index);
                    Log.e(TAG, "내용 : 답글 업데이트 : " + freeComment_adapter.getMode());
                }
            }

            @Override
            public void onFailure(Call<DTO_free_reply> call, Throwable t) {

            }
        });

        imm.hideSoftInputFromWindow(freeComment_et_content.getWindowToken(), 0);
    }


    private void uploadComment(String reply_index) throws ParseException {
        String writeDate = DateConverter.setDate();

        if (freeComment_et_content.getText().toString().isEmpty()) {
            freeComment_et_content.setError("내용을 입력해주세요");
            freeComment_et_content.requestFocus();
            return;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_free_reply> call = retrofitInterface.writeFreeComment(reply_index, PreferenceManager.getString(getApplicationContext(),"autoNick"), freeComment_et_content.getText().toString(), writeDate);
        call.enqueue(new Callback<DTO_free_reply>() {
            @Override
            public void onResponse(Call<DTO_free_reply> call, Response<DTO_free_reply> response) {
                if (response.isSuccessful() && response.body() != null) {
                    freeComment_adapter.addItem(new FreeRead_reply(PreferenceManager.getString(getApplicationContext(),"autoNick"), freeComment_et_content.getText().toString(), writeDate, reply_index));
                    freeComment_adapter.notifyItemInserted(freeComment_adapter.getItemCount());
                }
                freeComment_et_content.setText("");
                freeComment_rv.scrollToPosition(freeComment_adapter.getItemCount() -1);
            }

            @Override
            public void onFailure(Call<DTO_free_reply> call, Throwable t) {
            }

        });
        imm.hideSoftInputFromWindow(freeComment_et_content.getWindowToken(), 0);
        // 마지막 작성 아이탬으로 이동
        freeReply_scroll.scrollTo(0,freeComment_rv.getBottom());
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mvIntent = new Intent(FreeBoard_reply.this,FreeBoard_read.class);
        mvIntent.putExtra("board_number", board_number);
        mvIntent.putExtra("writer", writer);
        startActivity(mvIntent);
        Log.e(TAG, "내용 : 보내는 보드 게시판 번호 : " + board_number);
        finish();
    }

    private void setComment(String reply_index) {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_free_reply>> call = retrofitInterface.getFreeComment(reply_index);
        call.enqueue(new Callback<ArrayList<DTO_free_reply>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<DTO_free_reply>> call, @NonNull Response<ArrayList<DTO_free_reply>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        freeComment_adapter.addItem(new FreeRead_reply(
                                response.body().get(i).getReply_writer(),
                                response.body().get(i).getReply_content(),
                                response.body().get(i).getReply_date(),
                                response.body().get(i).getReply_index(),
                                response.body().get(i).getBoard_number()
                        ));
                        freeComment_adapter.notifyItemInserted(i);
                    }
                }
            }

            @Override
            public void onFailure
                    (@NonNull Call<ArrayList<DTO_free_reply>> call, @NonNull Throwable t) {
//                Toast.makeText(getApplicationContext(), "답글 불러오기 오류 : " + t, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showProgress() {
        progressDialog = new ProgressDialog(FreeBoard_reply.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    private void hideProgress() {
        progressDialog.dismiss();
    }

    private void setView() {
        freeComment_writer = findViewById(R.id.freeRead_comment_writer);
        freeComment_content = findViewById(R.id.freeRead_comment_content);
        freeComment_date = findViewById(R.id.freeRead_comment_date);
        freeComment_commit = findViewById(R.id.freeRead_comment_commit);
        freeComment_et_content = findViewById(R.id.freeRead_etComment_content);
        freeComment_rv = findViewById(R.id.freeRead_comment_rv);
        freeComment_number = findViewById(R.id.freeRead_comment_number);
        freeReply_scroll = findViewById(R.id.freeReply_scroll);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }
}