package com.example.wayout_ver_01.Activity.Gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryRead_reply_adapter;
import com.example.wayout_ver_01.Retrofit.DTO_comment;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.text.ParseException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GalleryBoard_reply extends AppCompatActivity {
    private TextView galleryReply_writer, galleryReply_date, galleryReply_number, galleryReply_content, galleryReply_commit;
    private NestedScrollView galleryReply_scroll;
    private RecyclerView galleryReply_rv;
    private EditText galleryReply_et_content;
    private GalleryRead_reply_adapter galleryRead_reply_adapter;
    private SwipeRefreshLayout galleryReply_swipe;
    InputMethodManager imm;
    ProgressDialog progressDialog;
    String index, board_number, writer, content, date;
    int page = 1;
    String setDate;
    boolean mode, swipe;
    int total_comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_board_reply);

        // View  연결하기
        setView();
        // 인텐트로 받아온 값 세팅하기
        setIntent();

        // 키보드 세팅
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // 어뎁터 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(GalleryBoard_reply.this, LinearLayoutManager.VERTICAL, false);
        galleryReply_rv.setLayoutManager(layoutManager);
        galleryRead_reply_adapter = new GalleryRead_reply_adapter(GalleryBoard_reply.this);
        galleryReply_rv.setAdapter(galleryRead_reply_adapter);
        galleryRead_reply_adapter.setImm(imm);
        galleryRead_reply_adapter.setEdit(galleryReply_et_content);

        // 서버에서 답글을 가져오기
        // 댓글 번호를 기준으로 해서 답글 불러옴
        setReply(index);

        // 답글 작성 하기
        galleryReply_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = galleryRead_reply_adapter.getMode();
                if (mode) {
                    updateReply();
                } else {
                    submitReply();
                }
            }
        });

        // 스크롤 페이징
        galleryReply_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
                    Log.e("갤러리 답글 페이징 ","reply_page : " +page );
                    getScroll();
                }
            }
        });

        // 스와이프시
        galleryReply_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe = true;
                page = 1;
                mode = false;
                setReply(index);
                galleryReply_et_content.setText("");
                galleryReply_swipe.setRefreshing(false);
            }
        });
    }


    private void getScroll() {
        // 해당 댓글 관련해서 답글들 모두 불러오기
        RetrofitInterface retrofit = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_comment>> call = retrofit.getGalleryReplyScroll(page, index);
        call.enqueue(new Callback<ArrayList<DTO_comment>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_comment>> call, Response<ArrayList<DTO_comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 가져오는 데이터가 없을때는 페이징을 넘기지 않음
                    if (response.body().size() == 0) {
                        page--;
                    }
                    Log.e("갤러리 답글 페이징 ","reply_page : " +page );

                    for (int i = 0; i < response.body().size(); i++) {
                        galleryRead_reply_adapter.addItem(new DTO_comment(
                                response.body().get(i).getWriter(),
                                response.body().get(i).getContent(),
                                response.body().get(i).getDate(),
                                response.body().get(i).getBoard_number(),
                                response.body().get(i).getComment_index()
                        ));
                        galleryRead_reply_adapter.notifyItemInserted(i + (page - 1) * 6);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_comment>> call, Throwable t) {

            }
        });

        progressDialog.dismiss();
    }

    private void updateReply() {
        progressDialog = new ProgressDialog(GalleryBoard_reply.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // 유효성 검사
        if (galleryReply_et_content.getText().toString().isEmpty()) {
            galleryReply_et_content.setError("내용을 입력해주세요");
            galleryReply_et_content.requestFocus();
            progressDialog.dismiss();
            return;
        }

        // 레트로핏 으로 업로드할 정보
        String setContent = galleryReply_et_content.getText().toString();
        String index = galleryRead_reply_adapter.getIndex();

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_comment> call = retrofitInterface.updateGalleryReply(setContent, index);
        call.enqueue(new Callback<DTO_comment>() {
            @Override
            public void onResponse(Call<DTO_comment> call, Response<DTO_comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    galleryRead_reply_adapter.updateItem(galleryRead_reply_adapter.getPos(), setContent);
                }
                progressDialog.dismiss();
                galleryReply_et_content.setText("");
                imm.hideSoftInputFromWindow(galleryReply_et_content.getWindowToken(), 0);
            }

            @Override
            public void onFailure(Call<DTO_comment> call, Throwable t) {

            }
        });

        progressDialog.dismiss();

    }

    private void submitReply() {
        String content = galleryReply_et_content.getText().toString();

//        Log.e("대댓글 작성", "내용 : " + index);
//        Log.e("대댓글 작성", "내용 : " + writer);
//        Log.e("대댓글 작성", "내용 : " + content);
//        Log.e("대댓글 작성", "내용 : " + board_number);
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_comment> call = retrofitInterface.writeGalleryReply(index, writer, content, board_number);
        call.enqueue(new Callback<DTO_comment>() {
            @Override
            public void onResponse(Call<DTO_comment> call, Response<DTO_comment> response) {
                String date = null;
                String setDate = null;
                try {
                    date = DateConverter.setDate();
                    setDate = DateConverter.resultDateToString(date, "M월 d일 a h:mm");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (response.isSuccessful() && response.body() != null) {
                    if (galleryRead_reply_adapter.getItemCount() % 6 != 0 || galleryRead_reply_adapter.getItemCount() == 0) {
                        galleryRead_reply_adapter.addItem(new DTO_comment(writer, content, setDate, board_number, index
                        ));
                        galleryRead_reply_adapter.notifyItemInserted(galleryRead_reply_adapter.getItemCount());
                    }
                    galleryReply_et_content.setText("");
                    imm.hideSoftInputFromWindow(galleryReply_et_content.getWindowToken(), 0);
                }
            }

            @Override
            public void onFailure(Call<DTO_comment> call, Throwable t) {

            }
        });
    }

    private void setReply(String index) {
        // 로딩창
        progressDialog = new ProgressDialog(GalleryBoard_reply.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //스와이프시 새로고침은 기존의 리사이클러뷰의 데이터를 지우고 다시 뿌려준다.
        if(swipe) {
            galleryRead_reply_adapter.itemsClear();
            swipe = false;
        }


        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_comment>> call = retrofitInterface.getGalleryReply(index);
        call.enqueue(new Callback<ArrayList<DTO_comment>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_comment>> call, Response<ArrayList<DTO_comment>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    for (int i = 0; i < response.body().size(); i++) {
                        galleryRead_reply_adapter.addItem(new DTO_comment(
                                response.body().get(i).getWriter(),
                                response.body().get(i).getContent(),
                                response.body().get(i).getDate(),
                                response.body().get(i).getBoard_number(),
                                response.body().get(i).getComment_index()
                        ));
                        galleryRead_reply_adapter.notifyItemInserted(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_comment>> call, Throwable t) {

            }
        });

        progressDialog.dismiss();
    }

    private void setIntent() {
        Intent intent = getIntent();
        index = intent.getStringExtra("index");
        board_number = intent.getStringExtra("board_number");
        writer = intent.getStringExtra("writer");
        content = intent.getStringExtra("content");
        date = intent.getStringExtra("date");

        try {
            setDate = DateConverter.resultDateToString(date, "M월 d일 a h:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        galleryReply_writer.setText(writer);
        galleryReply_content.setText(content);
        galleryReply_date.setText(setDate);

    }

    private void setView() {
        galleryReply_writer = findViewById(R.id.galleryReply_writer);
        galleryReply_date = findViewById(R.id.galleryReply_date);
        galleryReply_content = findViewById(R.id.galleryReply_content);
        galleryReply_commit = findViewById(R.id.galleryReply_commit);
        galleryReply_et_content = findViewById(R.id.galleryReply_et_content);
        galleryReply_rv = findViewById(R.id.galleryReply_rv);
        galleryReply_scroll = findViewById(R.id.galleryReply_scroll);
        galleryReply_swipe = findViewById(R.id.galleryReply_swipe);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

    }
}