package com.example.wayout_ver_01.Activity.Search;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchCafe_review_adapter;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchTheme_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_review;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.DTO_theme;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivitySearchThemeReadBinding;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import lib.kingja.switchbutton.SwitchMultiButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTheme_read extends AppCompatActivity {
    private ActivitySearchThemeReadBinding bind;
    private String theme_index = "", dialog_exit = "", dialog_diff = "", dialog_content;
    private ActivityResultLauncher<Intent> launcher;
    private ProgressDialog progressDialog;
    private SearchCafe_review_adapter theme_read_adapter;
    /* 리뷰 작성 , 메뉴 다이얼로그 */
    private Dialog dialog, menu_dialog;
    private float dialog_rate, review_rate;
    private int page = 1, size = 8;
    /* true = 0 , false = 1 */
    private int isChecked = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySearchThemeReadBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /* Activity Result Launcher Callback Setting~!~~!~!~!~!~!~! */
        /* type : 1, 카페 정보 수정 , type : 2, 테마 추가 */
        setLauncher();

        /* 매장에 등록 되어있는 테마 정보 불러오기 */
        setAdapter();

        /* 기본 데이터 세팅 */
        setData();
        Log.e("theme_read", "theme_index : " + theme_index);

        /* 리뷰 쓰기 */
        bind.themeReadWriteReview.setOnClickListener(v -> {
            // 다이얼로그 등록
            dialog = new Dialog(SearchTheme_read.this); // 객체 초기화
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
            dialog.setContentView(R.layout.item_dialog_theme); // 레이아웃과 Dialog
            DialogShow();
        });

        /* 메뉴 선텍 */
        bind.themeReadMenu.setOnClickListener(v -> {
            menu_dialog = new Dialog(SearchTheme_read.this);
            menu_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            menu_dialog.setContentView(R.layout.item_dialog_menu_cafe);
            MenuDialogShow();
        });

        /* 테마 찜하기 */
        bind.themeReadLike.setOnClickListener(v -> {
            // 체크됨 -> 검은색 -> 흰색으로 , 등록
            // 등록시 ( 유저 고유값, 카페 번호, 현재 isChecked)
            // true = 0, false = 1;
            if (isChecked == 0) {
                bind.themeReadLike.setImageResource(R.drawable.heartwhite);
                setLike();
            } else {
                // 체크 풀림 -> 흰색에서 검은색으로, 등록 삭제
                bind.themeReadLike.setImageResource(R.drawable.heartblack);
                setLike();
            }
        });

        /* 스크롤 페이징 */
        bind.themeReadScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    getScroll();
                }
            }
        });

        /* ============================================================ */
    }

    private void getScroll() {
        if (page == 1) {
            page = 2;
        }
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_review>> call = retrofitInterface.getThemeReadReviewScroll(theme_index, page, size);
        call.enqueue(new Callback<ArrayList<DTO_review>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_review>> call, Response<ArrayList<DTO_review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().size() > 0) {
                        page++;
                    }

                    /* 리뷰 세팅 */
                    for (int j = 0; j < response.body().size(); j++) {
                        theme_read_adapter.addItem(new DTO_review(
                                response.body().get(j).getWriter(),
                                response.body().get(j).getContent(),
                                response.body().get(j).getRate(),
                                response.body().get(j).getIndex(),
                                response.body().get(j).getDate(),
                                response.body().get(j).getDiff(),
                                response.body().get(j).getSuccess(),
                                response.body().get(j).getUser_index(),
                                response.body().get(j).getTotal()
                        ));
                        theme_read_adapter.notifyItemInserted((page - 1) * size + j);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_review>> call, Throwable t) {
                Log.e("cafe_read, 158", "스크롤 리뷰 에러 : " + t);
            }
        });
    }

    private void setLike() {
        String user_index = PreferenceManager.getString(SearchTheme_read.this, "userIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_shop> call = retrofitInterface.setThemeLike(theme_index, user_index, isChecked);
        call.enqueue(new Callback<DTO_shop>() {
            @Override
            public void onResponse(Call<DTO_shop> call, Response<DTO_shop> response) {
                isChecked = response.body().getIsChecked();
            }

            @Override
            public void onFailure(Call<DTO_shop> call, Throwable t) {
                Log.e("theme_read, 132", "like_에러 : " + t);
            }
        });
    }

    private void MenuDialogShow() {
        Context context = SearchTheme_read.this;
        menu_dialog.show();
        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */
        // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        TextView menu_add_theme, menu_update, menu_delete;
        menu_add_theme = menu_dialog.findViewById(R.id.item_dialog_add_theme);
        menu_update = menu_dialog.findViewById(R.id.item_dialog_update_cafe);
        menu_delete = menu_dialog.findViewById(R.id.item_dialog_delete_cafe);

        menu_add_theme.setText("테마 수정");
        menu_delete.setText("테마 삭제");
        menu_update.setVisibility(View.GONE);

        /* 주의할점 : call -> 어느 엑티비티에서 요청했는지
         *           callback -> 어느 액티비티에서 결과를 받았는지 */
        /* 테마 수정 -> 테마 추가 Activity 로 이동 */
        menu_add_theme.setOnClickListener(v -> {
            Intent intent = new Intent(context, SearchCafe_add_theme.class);
            intent.putExtra("theme_index", theme_index);
            intent.putExtra("call", "theme_read");
            launcher.launch(intent);
            menu_dialog.dismiss();
        });

        /* 매장 삭제 -> 삭제 다이얼로그로 재 확인 후 삭제 */
        menu_delete.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_dialog_check);
            showDeleteDialog(dialog);
            menu_dialog.dismiss();
        });
    }

    private void showDeleteDialog(Dialog dialog) {
        dialog.show();
        TextView title = dialog.findViewById(R.id.item_dialog_yn_title);
        TextView content = dialog.findViewById(R.id.item_dialog_yn_content);
        TextView yes = dialog.findViewById(R.id.item_dialog_yn_yes);
        TextView no = dialog.findViewById(R.id.item_dialog_yn_no);
        title.setText("테마 삭제");
        content.setText(" 테마를 정말 삭제하시겠습니까? ");

        // 테마 삭제
        yes.setOnClickListener(v -> {
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_theme> call = retrofitInterface.deleteTheme(theme_index);
            call.enqueue(new Callback<DTO_theme>() {
                @Override
                public void onResponse(Call<DTO_theme> call, Response<DTO_theme> response) {
                    Log.e("theme_read, 190", "삭제 성공");
                }

                @Override
                public void onFailure(Call<DTO_theme> call, Throwable t) {
                    Log.e("theme_read. 195", "삭제 실패 : " + t);
                }
            });
            dialog.dismiss();
            finish();
        });

        // 아무것도 아니죠
        no.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void DialogShow() {
        dialog.show();

        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */
        // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        ScaleRatingBar scaleRatingBar = dialog.findViewById(R.id.item_dialog_theme_grade);
        SwitchMultiButton diff_btn = dialog.findViewById(R.id.item_dialog_theme_diff);
        SwitchMultiButton success_btn = dialog.findViewById(R.id.item_dialog_theme_success);
        EditText ed_content = dialog.findViewById(R.id.item_dialog_theme_content);
        TextView no_btn = dialog.findViewById(R.id.item_dialog_theme_no);
        TextView yes_btn = dialog.findViewById(R.id.item_dialog_theme_yes);

        String[] success = {"성공", "실패"};
        String[] difficult = {"Easy", "Normal", "Hard", "Hell"};

        /* 예 */
        yes_btn.setOnClickListener(v -> {
            dialog_diff = difficult[diff_btn.getSelectedTab()];
            dialog_exit = success[success_btn.getSelectedTab()];
            dialog_rate = scaleRatingBar.getRating();
            dialog_content = ed_content.getText().toString();
            Log.e("확인", "Diff + : " + dialog_diff);
            Log.e("확인", "Diff + : " + dialog_exit);
            Log.e("확인", "Diff + : " + dialog_rate);
            if (dialog_content.isEmpty()) {
                ed_content.requestFocus();
                Toast.makeText(getApplicationContext(), "리뷰 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            /* 리뷰 등록 */
            writeReview();

            dialog.dismiss();
        });

        /* 아니오 */
        no_btn.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }

    private void writeReview() {
        page = 1;
        String writer = PreferenceManager.getString(getApplicationContext(), "autoId");
        String user_index = PreferenceManager.getString(getApplicationContext(), "userIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_review>> call = retrofitInterface.writeThemeReview(theme_index, writer, user_index, dialog_content, dialog_diff, dialog_exit, dialog_rate, page, size);
        call.enqueue(new Callback<ArrayList<DTO_review>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_review>> call, Response<ArrayList<DTO_review>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    /* 평점 세팅 */
                    if (response.body().size() > 0) {
                        bind.themeReadGrade.setRating(response.body().get(0).getTotal());
                        bind.themeReadGrade2.setRating(response.body().get(0).getTotal());
                        bind.themeReadScore.setText("평점  " + response.body().get(0).getTotal());

                        /* 리뷰 세팅 */
                        theme_read_adapter.clearItems();
                        for (int j = 0; j < response.body().size(); j++) {
                            theme_read_adapter.addItem(new DTO_review(
                                    response.body().get(j).getWriter(),
                                    response.body().get(j).getContent(),
                                    response.body().get(j).getRate(),
                                    response.body().get(j).getIndex(),
                                    response.body().get(j).getDate(),
                                    response.body().get(j).getDiff(),
                                    response.body().get(j).getSuccess(),
                                    response.body().get(j).getUser_index(),
                                    response.body().get(j).getTotal()
                            ));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_review>> call, Throwable t) {

            }
        });
    }

    private void setAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchTheme_read.this, RecyclerView.VERTICAL, false);
        bind.themeReadReviewRv.setLayoutManager(linearLayoutManager);
        theme_read_adapter = new SearchCafe_review_adapter(getApplicationContext(), bind.themeReadGrade, bind.themeReadGrade2, bind.themeReadScore, "0");
        bind.themeReadReviewRv.setAdapter(theme_read_adapter);
        theme_read_adapter.setMode();
    }

    private void setData() {
        /* 테마 인덱스 세팅 */
        Intent i = getIntent();
        theme_index = i.getStringExtra("theme_index");
        theme_read_adapter.setThemeIndex(theme_index);

        // 로딩바 세팅
        progressDialog = new ProgressDialog(SearchTheme_read.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        /* 2 초 초과시 작동 로딩바 종료 */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 3초가 지나면 다이얼로그 닫기
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
            }
        });
        thread.start();

        /* User 고유값 => Like 데이터 불러올때 사용됨 */
        /* 카페 고유값, 페이지, 사이즈 , 유저 고유값값 */
        String user_id = PreferenceManager.getString(getApplicationContext(), "userIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_theme> call = retrofitInterface.getThemeRead(theme_index, page, size, user_id);
        call.enqueue(new Callback<DTO_theme>() {
            @Override
            public void onResponse(Call<DTO_theme> call, Response<DTO_theme> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 좋아요
                    isChecked = response.body().getIsChecked();
                    // 체크됨 -> 검은색
                    // 등록시 ( 유저 고유값, 카페 번호, 현재 isChecked)
                    // true = 0, false = 1;
                    if (isChecked == 0) {
                        bind.themeReadLike.setImageResource(R.drawable.heartblack);
                    } else {
                        // 체크 풀림 -> 흰색
                        bind.themeReadLike.setImageResource(R.drawable.heartwhite);
                    }
                    String user_index = PreferenceManager.getString(getApplicationContext(), "userIndex");

                    /* 본인일시 권한 부여 */
                    if (response.body().getUser_index().equals(user_index)) {
                        bind.themeReadMenu.setVisibility(View.VISIBLE);
                    }
                    /* 게시글 설정 */
                    bind.themeReadToolbar.setText(response.body().getName());
                    bind.themeReadName.setText(response.body().getName());
                    bind.themeReadDiff.setText(response.body().getDiff());
                    bind.themeReadLimit.setText(response.body().getLimit());
                    bind.themeReadGenre.setText(response.body().getGenre());
                    bind.themeReadCafeName.setText(response.body().getCafe());
                    bind.themeReadInfo.setText(response.body().getInfo());

                    Glide.with(SearchTheme_read.this)
                            .load(response.body().getImage())
                            .fitCenter()
                            .into(bind.themeReadImage);

                    /* 평점 세팅 */
                    if (response.body().getReviews().size() > 0) {
                        bind.themeReadGrade.setRating(response.body().getReviews().get(0).getTotal());
                        bind.themeReadGrade2.setRating(response.body().getReviews().get(0).getTotal());
                        bind.themeReadScore.setText("평점  " + response.body().getReviews().get(0).getTotal());

                        /* 리뷰 세팅 */
                        theme_read_adapter.clearItems();
                        for (int j = 0; j < response.body().getReviews().size(); j++) {
                            theme_read_adapter.addItem(new DTO_review(
                                    response.body().getReviews().get(j).getWriter(),
                                    response.body().getReviews().get(j).getContent(),
                                    response.body().getReviews().get(j).getRate(),
                                    response.body().getReviews().get(j).getIndex(),
                                    response.body().getReviews().get(j).getDate(),
                                    response.body().getReviews().get(j).getDiff(),
                                    response.body().getReviews().get(j).getSuccess(),
                                    response.body().getReviews().get(j).getUser_index(),
                                    response.body().getReviews().get(j).getTotal()
                            ));
                        }
                    }
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<DTO_theme> call, Throwable t) {
                Log.e("theme_read, 94", "테마 호출 오류 : " + t);
            }
        });
    }

    private void setLauncher() {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent i = result.getData();
                            /* 1번 => 테마 추가, 2번 => 카페 수정 */
                            String callback = i.getStringExtra("callback");
                            if ("add_theme".equals(callback)) {
                                setData();
                                Log.e("theme_read,401", "테마 데이터 다시 세팅하는가?");
                            }
                        }
                    }
                }
        );
    }
}