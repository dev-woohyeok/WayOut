package com.example.wayout_ver_01.Activity.Gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryRead_comment_adapter;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryRead_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_comment;
import com.example.wayout_ver_01.Retrofit.DTO_free_reply;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.DTO_image;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryBoard_read extends AppCompatActivity {

    private TextView galleryRead_cafe, galleryRead_theme,galleryRead_point, galleryRead_writer, galleryRead_date, galleryRead_content, galleryRead_comment_num, galleryRead_like_num, galleryRead_comment_submit;
    private ImageView galleryRead_menu, galleryRead_like_btn;
    private EditText galleryRead_comment_et;
    private RecyclerView galleryRead_rv, galleryRead_comment_rv;
    private GalleryRead_adpater galleryRead_adpater;
    private GalleryRead_comment_adapter galleryRead_comment_adapter;
    private ProgressDialog progressDialog;
    private NestedScrollView galleryRead_scroll;
    private LinearLayoutManager layoutManager, layoutManager_comment;
    private SwipeRefreshLayout galleryRead_swipe;
    private InputMethodManager imm;
    private String cafe, theme, content;

    // 초기화 신경 쓸 것
    // 페이지, 한번에 몇개씩 할지, 총 댓글 수 , 총 좋아요 수, 총 답글 수
    private int page = 1, limit = 5, total_comment, total_like, total_reply;
    // 스크롤 True 시 스크롤 페이징 적용 , 스와이프 true 시 새로고침 적용
    private boolean scroll, swipe;
    // like 버튼이 클릭되어있는지 아닌지 확인
    // type_mode : 수정시와 일반 게시글 등록시를
    private boolean is_click, type_mode;
    // 보드판 번호, 작성자 정보
    private String board_number, writer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_board_read);

        // View 들을 연결함
        setFindView();

        // 이미지 리사이클러뷰 세팅
        // 레이아웃 매니저 설정, 수직 방향으로 설정
        // 이미지 어뎁터 설정, 파라미터 : Context
        layoutManager = new LinearLayoutManager(GalleryBoard_read.this, LinearLayoutManager.VERTICAL, false);
        galleryRead_rv.setLayoutManager(layoutManager);
        galleryRead_adpater = new GalleryRead_adpater(GalleryBoard_read.this);
        galleryRead_rv.setAdapter(galleryRead_adpater);

        // 댓글 리사이클러뷰 세팅
        // 레이아웃 매니저 설정, 수직 방향으로 설정
        // 댓글 어뎁터 설정, 파라미터 : Context
        layoutManager_comment = new LinearLayoutManager(GalleryBoard_read.this, LinearLayoutManager.VERTICAL, false);
        galleryRead_comment_rv.setLayoutManager(layoutManager_comment);
        galleryRead_comment_adapter = new GalleryRead_comment_adapter(GalleryBoard_read.this, galleryRead_comment_num);
        galleryRead_comment_rv.setAdapter(galleryRead_comment_adapter);
        galleryRead_comment_adapter.setUpdate_content(galleryRead_comment_et);
        galleryRead_comment_adapter.setImm(imm);

        // 게시판 번호 : DB 에서 데이터 요청
        // 작성자 : 수정, 삭제 권한 설정
        Intent intent = getIntent();
        board_number = intent.getStringExtra("board_number");
        writer = intent.getStringExtra("writer");
//        Log.e("갤러리 보드정보 가져오기" , "board_num : "  + board_number);
//        Log.e("갤러리 작성자 가져오기", "writer : " + writer);

        // 좋아요 클릭시
        // is_click : true -> 조회수 증가, is_click : false -> 조회수 감소 및 삭제
        galleryRead_like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_click) {
                    galleryRead_like_btn.setImageResource(R.drawable.heartwhite);
                    is_click = false;
                    click_unlike(board_number);
                } else {
                    galleryRead_like_btn.setImageResource(R.drawable.heartblack);
                    is_click = true;
                    click_like(board_number);
                }
            }
        });

        // 댓글 작성
        galleryRead_comment_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type_mode = galleryRead_comment_adapter.getMode();
                Log.e("갤러리 댓글 수정 ", "type_mode : " + type_mode);
                if (type_mode) {
                    // 댓글 수정
                    update_comment(board_number);
                    galleryRead_comment_adapter.endUpdate();
                } else {
                    // 댓글 작성
                    submit_comment(board_number);
                }
            }
        });

        // 스와이프시 새로고침 -> 보드 새로고침
        galleryRead_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe = true;
                page = 1;
                type_mode = false;
                getGalleryRead(board_number);
                galleryRead_comment_et.setText("");
                galleryRead_swipe.setRefreshing(false);
                progressDialog.dismiss();
            }
        });

        // 메뉴 클릭시
        galleryRead_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_menu(board_number);
            }
        });

        // 스크롤 페이징
        galleryRead_scroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    progressDialog.show();
                    progressDialog.setMessage("Loading...");
                    page++;
                    getScroll();
                    progressDialog.dismiss();
                }
            }
        });


    }

    // 화면 종료전에 초기화 하기 -> 다른화면 전환시 페이징 , 수정 에러 발생
    @Override
    protected void onStop() {
        super.onStop();

        swipe = true;
        page = 1;
        type_mode = false;
        getGalleryRead(board_number);
        progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 선택한 게시판 정보 가져오기
        // 게시판 인덱스에 해당하는 댓글, 게시물 내용 , 대댓글 불러와서 뿌려줌,
        getGalleryRead(board_number);

        // progressDialog 종료
        progressDialog.dismiss();
    }

    private void getScroll() {
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_gallery> call = retrofitInterface.getGalleryScroll(page, board_number, writer);
        call.enqueue(new Callback<DTO_gallery>() {
            @Override
            public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {

                // 불러온 댓글이 없을때에는 페이지 넘기지 않기
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().getGallery_comment().size() == 0){
                        page--;
                    }
                    Log.e("댓글 페이징 ", "page : " + page);

                    for (int i = 0; i < response.body().getGallery_comment().size(); i++) {
                        galleryRead_comment_adapter.add_item(new DTO_comment(
                                response.body().getGallery_comment().get(i).getWriter(),
                                response.body().getGallery_comment().get(i).getContent(),
                                response.body().getGallery_comment().get(i).getDate(),
                                response.body().getGallery_comment().get(i).getBoard_number(),
                                response.body().getGallery_comment().get(i).getComment_index(),
                                response.body().getGallery_comment().get(i).getTotal_comment(),
                                response.body().getGallery_comment().get(i).getTotal_reply()
                        ));
                        galleryRead_comment_adapter.notifyItemInserted(i + (page - 1) * 6);
                    }


                }
            }

            @Override
            public void onFailure(Call<DTO_gallery> call, Throwable t) {

            }
        });

    }

    private void select_menu(String board_number) {
        final ArrayList<String> Items = new ArrayList<>();
        Items.add("게시물 삭제");
        Items.add("게시물 수정");

        // Items 를 String[]로 전환한다.
        final String[] Strings = Items.toArray(new String[Items.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(GalleryBoard_read.this);
        builder.setItems(Strings, (dialog, pos) -> {
            String selectedText = Strings[pos];
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

    private void delete_board(String board_number) {

        // 알림창
        AlertDialog.Builder builder = new AlertDialog.Builder(GalleryBoard_read.this);
        builder.setTitle("게시물 삭제");
        builder.setMessage("\n정말로 삭제하시겠습니까?\n");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 삭제 실행하기
                        writer = PreferenceManager.getString(getApplicationContext(), "autoNick");
                        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create((RetrofitInterface.class));
                        Call<DTO_gallery> call = retrofitInterface.deleteGallery(board_number, writer);
                        call.enqueue(new Callback<DTO_gallery>() {
                            @Override
                            public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {
                                if (response.body().isSuccess() && response.body() != null) {
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<DTO_gallery> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                                Log.e("갤러리 보드 삭제 실패 ", "내용 : " + t);
                            }
                        });
                    }
                });
        // =====
        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void update_board(String board_number) {
        // 이미지 Uri 를 Intent 에 담아서 전달함
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < galleryRead_adpater.getItemCount(); i++) {
            arrayList.add(galleryRead_adpater.getUri(i));
            Log.e("제발 ...", "" + galleryRead_adpater.getUri(i));
        }
        Intent intent = new Intent(GalleryBoard_read.this, GalleryBoard_write.class);
        intent.putExtra("mode", true);
        intent.putExtra("items", arrayList);
        intent.putExtra("board_number", board_number);
        intent.putExtra("cafe", cafe);
        intent.putExtra("theme", theme);
        intent.putExtra("content", galleryRead_content.getText().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    private void update_comment(String board_number) {

        progressDialog = new ProgressDialog(GalleryBoard_read.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // 레트로핏 통신
        String setWriter = PreferenceManager.getString(getApplicationContext(), "autoNick");
        String setContent = galleryRead_comment_et.getText().toString();
        String setIndex = galleryRead_comment_adapter.getIndex();

        // 유효성 검사
        if (galleryRead_comment_et.getText().toString().isEmpty()) {
            galleryRead_comment_et.setError("내용을 입력해주세요");
            galleryRead_comment_et.requestFocus();
            progressDialog.dismiss();
            return;
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_comment> call = retrofitInterface.updateGalleryComment(setWriter, setContent, setIndex);
        call.enqueue(new Callback<DTO_comment>() {
            @Override
            public void onResponse(Call<DTO_comment> call, Response<DTO_comment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    galleryRead_comment_adapter.updateItem(galleryRead_comment_adapter.getPos(), setContent);

                }
            }

            @Override
            public void onFailure(Call<DTO_comment> call, Throwable t) {
                Log.e("댓글 수정 실패시", "내용 : 댓글 수정 에러 : " + t);
            }
        });

        // 작성 초기화
        galleryRead_comment_et.setText("");
        galleryRead_comment_adapter.endUpdate();
        progressDialog.dismiss();
        imm.hideSoftInputFromWindow(galleryRead_comment_et.getWindowToken(), 0);

    }

    private void submit_comment(String board_number) {
        // 유효성 검사
        if (galleryRead_comment_et.getText().toString().isEmpty()) {
            galleryRead_comment_et.setError("내용을 입력해주세요");
            galleryRead_comment_et.requestFocus();
            progressDialog.dismiss();
            return;
        }

        // 작성자, 내용, 작성일자
        String writer = PreferenceManager.getString(getApplicationContext(), "autoNick");
        String content = galleryRead_comment_et.getText().toString();

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_comment> call = retrofitInterface.writerGalleryComment(writer, content, board_number);
        call.enqueue(new Callback<DTO_comment>() {
            @Override
            public void onResponse(Call<DTO_comment> call, Response<DTO_comment> response) {
                if (response.isSuccessful() && response.body() != null) {

                    galleryRead_comment_adapter.total_comment(total_comment);
                    // 총댓글 갯수
                    galleryRead_comment_num.setText("댓글 " + response.body().getTotal_comment());
                    // 작성자, 내용 , 작성일, 작성 글 번호, 작성 댓글 번호
                    // 다음페이지로 넘어가는 경우 , 댓글을 처음 작성하는 경우를 제외하고 댓글을 리사이클러뷰에 추가한다.
                    if(galleryRead_comment_adapter.getItemCount() % 6 != 0 || galleryRead_comment_adapter.getItemCount() == 0) {
                        galleryRead_comment_adapter.add_item(new DTO_comment(
                                writer,
                                content,
                                response.body().getDate(),
                                board_number,
                                response.body().getComment_index()
                        ));
//                        galleryRead_scroll.fullScroll(View.FOCUS_DOWN);
                    }
                    galleryRead_comment_adapter.notifyItemInserted(galleryRead_comment_adapter.getItemCount());



                    // 작성 초기화
                    galleryRead_comment_et.setText("");
                    // 입력후 키보드 내리기
                    imm.hideSoftInputFromWindow(galleryRead_comment_et.getWindowToken(), 0);
                }
            }

            @Override
            public void onFailure(Call<DTO_comment> call, Throwable t) {

            }
        });


    }

    private void click_like(String board_number) {
        String user_id = PreferenceManager.getString(getApplicationContext(), "autoNick");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_gallery> call = retrofitInterface.writeGalleryLike(user_id, board_number);
        call.enqueue(new Callback<DTO_gallery>() {
            @Override
            public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int total = response.body().getTotal_like();
                    galleryRead_like_num.setText("좋아요 " + total);
                }
            }

            @Override
            public void onFailure(Call<DTO_gallery> call, Throwable t) {

            }
        });
    }

    private void click_unlike(String board_number) {
        String user_id = PreferenceManager.getString(getApplicationContext(), "autoNick");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_gallery> call = retrofitInterface.deleteGalleryLike(user_id, board_number);
        call.enqueue(new Callback<DTO_gallery>() {
            @Override
            public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int total = response.body().getTotal_like();
                    galleryRead_like_num.setText("좋아요 " + total);
                }
            }

            @Override
            public void onFailure(Call<DTO_gallery> call, Throwable t) {

            }
        });
    }


    // View 들을 연동해준다.
    private void setFindView() {
        galleryRead_cafe = findViewById(R.id.galleryRead_cafe);
        galleryRead_theme = findViewById(R.id.galleryRead_theme);
        galleryRead_writer = findViewById(R.id.galleryRead_writer);
        galleryRead_date = findViewById(R.id.galleryRead_date);
        galleryRead_content = findViewById(R.id.galleryRead_content);
        galleryRead_comment_num = findViewById(R.id.galleryRead_comment_num);
        galleryRead_like_num = findViewById(R.id.galleryRead_like_num);
        galleryRead_comment_submit = findViewById(R.id.galleryRead_comment_submit);
        galleryRead_menu = findViewById(R.id.galleryRead_menu);
        galleryRead_like_btn = findViewById(R.id.galleryRead_like_btn);
        galleryRead_comment_et = findViewById(R.id.galleryRead_comment_et);
        galleryRead_rv = findViewById(R.id.galleryRead_image_rv);
        galleryRead_comment_rv = findViewById(R.id.galleryRead_comment_rv);
        galleryRead_swipe = findViewById(R.id.galleryRead_swipe);
        galleryRead_scroll = findViewById(R.id.galleryRead_scroll);


        // 키보드 설정
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

    }

    private void getGalleryRead(String board_number) {
        // 로딩창 On
        progressDialog = new ProgressDialog(GalleryBoard_read.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // 스와이프 새로고침시 기존에 리사이클러뷰의 있는 데이터를 지우고 새로 불러온다.
        if (swipe) {
            galleryRead_adpater.clearItems();
            galleryRead_adpater.notifyDataSetChanged();
            galleryRead_comment_adapter.clearItems();
            galleryRead_comment_adapter.notifyDataSetChanged();
            swipe = false;
        }

        String user_id = PreferenceManager.getString(getApplicationContext(), "autoNick");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_gallery> call = retrofitInterface.getGalleryRead(page, user_id, board_number);
        call.enqueue(new Callback<DTO_gallery>() {
            @Override
            public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userName = PreferenceManager.getString(getApplicationContext(), "autoNick");
                    String writer = response.body().getWriter();
                    total_comment = response.body().getTotal_comment();
                    total_like = response.body().getTotal_like();

                    // 작성자일 경우 메뉴를 통해서 삭제, 수정 못하게 예외처리
                    if (!(userName.equals(writer))) {
                        galleryRead_menu.setVisibility(View.INVISIBLE);
                    }

                    // 게시글 설정
                    galleryRead_writer.setText("작성자 :  " + response.body().getWriter());
                    galleryRead_cafe.setText("카페명 : " + response.body().getCafe());
                    galleryRead_theme.setText("테마명 : " + response.body().getTheme());
                    galleryRead_content.setText(response.body().getContent());

                    cafe = response.body().getCafe();
                    theme = response.body().getTheme();
                    content = response.body().getContent();

                    // 날짜 세팅
                    try {
                        galleryRead_date.setText(DateConverter.resultDateToString(response.body().getDate(), "yyyy-MM-dd a h:mm"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // 좋아요 세팅
                    if (response.body().isClick()) {
                        galleryRead_like_btn.setImageResource(R.drawable.heartblack);
                        is_click = true;
                    } else {
                        galleryRead_like_btn.setImageResource(R.drawable.heartwhite);
                        is_click = false;
                    }
                    // 댓글과 좋아요 갯수 세팅
                    galleryRead_like_num.setText("좋아요 " + total_like);
                    galleryRead_comment_num.setText("댓글 " + total_comment);
                    galleryRead_comment_adapter.total_comment(total_comment);

                    // 이미지 리사이클러뷰 설정
                    if (galleryRead_adpater.getItemCount() == 0) {
                        for (int i = 0; i < response.body().getImages_uri().size(); i++) {
                            String image_uri = response.body().getImages_uri().get(i).getImage_uri();
                            galleryRead_adpater.addItem(new DTO_gallery(image_uri));
                            galleryRead_adpater.notifyItemInserted(i);
                        }
                    }

                    // 댓글 리사이클러뷰 설정
                    // 작성자, 내용, 작성일자, 게시판번호, 댓글 번호, 총 댓글수 , 총 답글 수

                    if (response.body().getGallery_comment() != null) {
                        ArrayList<DTO_comment> items = new ArrayList<>();
                        items = response.body().getGallery_comment();

//                      // 처음 댓글 작성시에는 무조건 추가
                        if (galleryRead_comment_adapter.getItemCount() == 0) {
                            for (int i = 0; i < response.body().getGallery_comment().size(); i++) {
                                galleryRead_comment_adapter.add_item(new DTO_comment(
                                        response.body().getGallery_comment().get(i).getWriter(),
                                        response.body().getGallery_comment().get(i).getContent(),
                                        response.body().getGallery_comment().get(i).getDate(),
                                        response.body().getGallery_comment().get(i).getBoard_number(),
                                        response.body().getGallery_comment().get(i).getComment_index(),
                                        response.body().getGallery_comment().get(i).getTotal_comment(),
                                        response.body().getGallery_comment().get(i).getTotal_reply()
                                ));
                                galleryRead_comment_adapter.notifyItemInserted(i);
                            }

//                            Log.e("댓글 리사이클러뷰 로그", "내용 댓글 데이터 확인: " + items.get(0).getWriter());
//                            Log.e("댓글 리사이클러뷰 로그", "내용 : " + response.body().getGallery_comment().get(i).getContent());
//                            Log.e("댓글 리사이클러뷰 로그", "내용 : " + response.body().getGallery_comment().get(i).getDate());
//                            Log.e("댓글 리사이클러뷰 로그", "내용 : " + response.body().getGallery_comment().get(i).getBoard_number());
//                            Log.e("댓글 리사이클러뷰 로그", "내용 : " + response.body().getGallery_comment().get(i).getComment_index());
//                            Log.e("댓글 리사이클러뷰 로그", "내용 : " + response.body().getGallery_comment().get(i).getTotal_reply());
//                            Log.e("댓글 리사이클러뷰 로그", "내용 : " + response.body().getGallery_comment().get(i).getTotal_comment());
                        }else{
                            // 댓글 초기화시 새로운 데이터 덮어씌우기
                            for (int i = 0; i < response.body().getGallery_comment().size(); i++) {
                                galleryRead_comment_adapter.change_item(i, new DTO_comment(
                                        response.body().getGallery_comment().get(i).getWriter(),
                                        response.body().getGallery_comment().get(i).getContent(),
                                        response.body().getGallery_comment().get(i).getDate(),
                                        response.body().getGallery_comment().get(i).getBoard_number(),
                                        response.body().getGallery_comment().get(i).getComment_index(),
                                        response.body().getGallery_comment().get(i).getTotal_comment(),
                                        response.body().getGallery_comment().get(i).getTotal_reply()
                                ));
                            }
                        }
                    }


                } else {
                    Log.e("Gallery_Read 레트로핏 실패 ", "담겨온 데이터가 없거나, 에러 발생");
                }
            }

            @Override
            public void onFailure(Call<DTO_gallery> call, Throwable t) {
                Log.e("갤러리Read 게시판 불러오기", "에러 메세지 : " + t);
            }
        });

//        Log.e("레트로핏 전송 데이터", "page : " + page );
//        Log.e("레트로핏 전송 데이터", "user_id : " + user_id );
//        Log.e("레트로핏 전송 데이터", "board_number : " + board_number );
    }
}