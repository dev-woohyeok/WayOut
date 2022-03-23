package com.example.wayout_ver_01.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.RecyclerView.FreeRead_adapter;
import com.example.wayout_ver_01.RecyclerView.FreeRead_image;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.FreeRead_reply;
import com.example.wayout_ver_01.RecyclerView.FreeRead_reply_adapter;
import com.example.wayout_ver_01.Retrofit.DTO_board;
import com.example.wayout_ver_01.Retrofit.DTO_free_reply;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeBoard_read extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    TextView freeRead_title,freeRead_writer,freeRead_date,freeRead_content
            ,freeRead_reply_num,freeRead_reply_commit,sc_point,freeRead_toReply;
    ImageView freeRead_menu;
    EditText freeRead_reply_content;
    RecyclerView freeRead_rv, freeRead_reply_rv;
    FreeRead_adapter freeRead_adapter;
    ProgressDialog progressDialog;
    FreeRead_reply_adapter freeRead_reply_adapter;
    NestedScrollView nestedScrollView;
    LinearLayoutManager layoutManager_reply, layoutManager;
    boolean type_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_board_read);

        // View 세팅
        setFindView();

        // 로딩창
        progressDialog = new ProgressDialog(FreeBoard_read.this);
        progressDialog.setMessage("Loading");
        progressDialog.show();

        // 이미지 리사이클러뷰 세팅
        freeRead_rv = findViewById(R.id.freeRead_img_rv);
        layoutManager = new LinearLayoutManager(FreeBoard_read.this, LinearLayoutManager.VERTICAL, false);
        freeRead_rv.setLayoutManager(layoutManager);
        freeRead_adapter = new FreeRead_adapter(FreeBoard_read.this);
        freeRead_rv.setAdapter(freeRead_adapter);

        // 댓글 리사이클러뷰 세팅
        freeRead_reply_rv = findViewById(R.id.freeRead_reply_rv);
        layoutManager_reply = new LinearLayoutManager(FreeBoard_read.this, LinearLayoutManager.VERTICAL,false);
        freeRead_reply_rv.setLayoutManager(layoutManager_reply);
        freeRead_reply_adapter = new FreeRead_reply_adapter(FreeBoard_read.this, freeRead_reply_num, freeRead_reply_content);
        freeRead_reply_rv.setAdapter(freeRead_reply_adapter);

        // 선택한 게시판 정보 가져오기
        Intent intent = getIntent();
        String board_num = intent.getStringExtra("board_num");
        Log.e(TAG, "내용 : board_num : " + board_num);
        getFreeRead(board_num);

        // 댓글 작성
        freeRead_reply_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_mode = freeRead_reply_adapter.getMode();
                Log.e(TAG, "내용 : type_mode : " + type_mode);
                try {
                    // 작성  method
                    if(type_mode) {
                        // 수정시
                        update_reply(board_num);
                    }else {
                        // 작성시
                        upload_reply(board_num);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        // 메뉴 클릭시
        freeRead_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_menu(board_num);
            }
        });

        progressDialog.dismiss();
    }



    //==========================================
    private void update_reply(String board_num) throws ParseException {

        progressDialog = new ProgressDialog(FreeBoard_read.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // 레트로핏 통신
        String Date = DateConverter.setDate();
        String writer = PreferenceManager.getString(FreeBoard_read.this, "autoId");
        String content = freeRead_reply_content.getText().toString();
        String index = freeRead_reply_adapter.getReplyIndex();

        // 유효성검사
        if(freeRead_reply_content.getText().toString().isEmpty()){
            freeRead_reply_content.setError("수정할 내용을 입력해주세요");
            freeRead_reply_content.requestFocus();
            progressDialog.dismiss();
            return;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_free_reply> call = retrofitInterface.updateFreeReply(index,content,Date);
        call.enqueue(new Callback<DTO_free_reply>() {
            @Override
            public void onResponse(Call<DTO_free_reply> call, Response<DTO_free_reply> response) {
                if( response.isSuccessful() && response.body() != null ) {
                    Log.e(TAG, "내용 : 댓글 수정 성공 : " + " 댓글 수정" + index);
                    freeRead_reply_adapter.replyUpdate(freeRead_reply_adapter.getItemPos(), content);
                }
            }

            @Override
            public void onFailure(Call<DTO_free_reply> call, Throwable t) {
                Log.e(TAG, "내용 : 댓글 수정 에러 : " + t);
            }
        });

        // 작성 초기화
        freeRead_reply_content.setText("");
        freeRead_reply_adapter.endMode();
        Log.e(TAG, "내용 : type_mode : "  + type_mode);
        progressDialog.dismiss();
    }

    // 수정, 삭제 선택
    private void select_menu(String board_number){
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("게시물 삭제");
        ListItems.add("게시물 수정");

        // String 배열 Items 에 ListItems 를 string[ListItems.size()] 형태로 생성한다.
        final String[] items = ListItems.toArray(new String[ListItems.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(FreeBoard_read.this);
        builder.setItems(items, (dialog, pos) -> {
            String selectedText = items[pos];
            switch (selectedText) {
                case "게시물 삭제":
                    delete_board(board_number);
                    break;
                case "게시물 수정":
                    update_board(board_number);
                    break;
            }
        });
        builder.show();
    }



    // 게시물 삭제
    private void delete_board(String board_number) {
        Log.e(TAG, "내용 : 게시판 번호 : " + board_number);

        // 알림창
        AlertDialog.Builder builder = new AlertDialog.Builder(FreeBoard_read.this);
        builder.setTitle("게시물 삭제");
        builder.setMessage("\n정말로 삭제하시겠습니까?\n");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 삭제 실행
                        String writer = freeRead_writer.getText().toString();
                        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                        Call<DTO_board> call = retrofitInterface.deleteFreeBoard(board_number,writer);
                        call.enqueue(new Callback<DTO_board>() {
                            @Override
                            public void onResponse(Call<DTO_board> call, Response<DTO_board> response) {
                                if(response.isSuccessful() && response.body() != null) {
                                    if (response.body().isSuccess()) {
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<DTO_board> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "내용 : 삭제 실패 내용 " + t);
                            }
                        });
                    }
                    //========
                });
        builder.setNegativeButton("아니요",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
    // 게시물 수정
    private void update_board(String board_number){
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < freeRead_adapter.getItemCount(); i++){
            arrayList.add(freeRead_adapter.getUri(i));
        }
        Log.e(TAG, "내용 : arrayList  추가 " + arrayList);

        Intent intent = new Intent(FreeBoard_read.this, FreeBoard_update.class);
        intent.putExtra("title", freeRead_title.getText().toString());
        intent.putExtra("content", freeRead_content.getText().toString());
        intent.putExtra("writer", freeRead_writer.getText().toString());
        intent.putExtra("board_number", board_number);
        intent.putExtra("imgList", arrayList);
        startActivity(intent);
        finish();
    }

    // 댓글 업로드
    private void upload_reply(String board_num) throws ParseException {
        progressDialog = new ProgressDialog(FreeBoard_read.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // 유효성검사
        if(freeRead_reply_content.getText().toString().isEmpty()){
            freeRead_reply_content.setError("내용을 입력해주세요");
            freeRead_reply_content.requestFocus();
            progressDialog.dismiss();
            return;
        }

        // 레트로핏 통신
        String Date = DateConverter.setDate();
        String writer = PreferenceManager.getString(FreeBoard_read.this, "autoId");
        String content = freeRead_reply_content.getText().toString();
        // 작성 초기화
        freeRead_reply_content.setText("");

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_free_reply> call = retrofitInterface.writeFreeReply(writer,content,Date,board_num);
        call.enqueue(new Callback<DTO_free_reply>() {
            @Override
            public void onResponse(Call<DTO_free_reply> call, Response<DTO_free_reply> response) {
                if(response.isSuccessful() && response.body() != null) {
                        freeRead_reply_adapter.add_item(new FreeRead_reply(writer,content,Date,response.body().getReply_index()));
                        freeRead_reply_adapter.notifyItemInserted(freeRead_reply_adapter.getItemCount());
                }
                // 댓글 수 업로드
                freeRead_reply_num.setText("댓글 " +freeRead_reply_adapter.getItemCount()+"개");
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<DTO_free_reply> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
        // 마지막 작성 아이탬으로 이동
        nestedScrollView.scrollTo(0,sc_point.getBottom());
    }
    // View 세팅
    // ==========================================================================
    private void setFindView () {
        freeRead_title = findViewById(R.id.freeRead_title);
        freeRead_writer = findViewById(R.id.freeRead_writer);
        freeRead_date = findViewById(R.id.freeRead_date);
        freeRead_content = findViewById(R.id.freeRead_content);
        freeRead_reply_num = findViewById(R.id.freeRead_reply_num);
        freeRead_reply_commit = findViewById(R.id.freeRead_reply_commit);
        freeRead_reply_content = findViewById(R.id.freeRead_reply_comment);
        nestedScrollView = findViewById(R.id.sc_reply);

        sc_point = findViewById(R.id.sc_point);
        freeRead_menu = findViewById(R.id.freeRead_menu);
    }

    // DB 에서 게시판 정보 불러오기
    private void getFreeRead(String board_num) {

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_board> call = retrofitInterface.getFreeRead(board_num);
        call.enqueue(new Callback<DTO_board>() {
            @Override
            public void onResponse(@NonNull Call<DTO_board> call, @NonNull Response<DTO_board> response) {

                if(response.isSuccessful() && response.body() != null)
                {
                    String userName = PreferenceManager.getString(FreeBoard_read.this,"autoId");
                    String writer = response.body().getWriter();

                    // 메뉴 예외
                    if(!(userName.equals(writer)))
                    {
                        freeRead_menu.setVisibility(View.INVISIBLE);
                    }

                    // 게시글 설정
                    freeRead_title.setText(response.body().getTitle());
                    freeRead_writer.setText(response.body().getWriter());
                    try { freeRead_date.setText(DateConverter.resultDateToString(response.body().getDate(),"yyyy-MM-dd a h:mm"));}
                    catch (ParseException e) { e.printStackTrace();}
                    freeRead_content.setText(response.body().getContent());

                    // 이미지 리사이클러뷰 설정
                    for (int i = 0; i < response.body().getImages_uri().size(); i++){
                        String image_Uri = response.body().getImages_uri().get(i).getImage_uri();
                        freeRead_adapter.add_item(new FreeRead_image(image_Uri));
                        freeRead_adapter.notifyItemInserted(i);
                    }
                    // 댓글 리사이클러뷰 설정
                    if(response.body().getFree_reply() != null) {
                        freeRead_reply_num.setText("댓글 " + response.body().get_reply_size() + "개");
                        for (int i = 0; i < response.body().getFree_reply().size(); i++) {

                            freeRead_reply_adapter.add_item(new FreeRead_reply(
                                    response.body().getFree_reply().get(i).getReply_writer(),
                                    response.body().getFree_reply().get(i).getReply_content(),
                                    response.body().getFree_reply().get(i).getReply_date(),
                                    response.body().getFree_reply().get(i).getBoard_number(),
                                    response.body().getFree_reply().get(i).getReply_index(),
                                    response.body().getFree_reply().get(i).getReply_answer()

                                    ));
                            freeRead_reply_adapter.notifyItemInserted(i);
                        }
                    }
                }
                progressDialog.dismiss();
            }


            @Override
            public void onFailure(Call<DTO_board> call, Throwable t) {
                Log.e(TAG, "내용 : 에러 메세지 : " + t);
                progressDialog.dismiss();
            }
        });
    }
}