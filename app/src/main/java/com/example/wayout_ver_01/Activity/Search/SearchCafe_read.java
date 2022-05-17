package com.example.wayout_ver_01.Activity.Search;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchCafe_review_adapter;
import com.example.wayout_ver_01.RecyclerView.Theme.SearchTheme_adpater;
import com.example.wayout_ver_01.Retrofit.DTO_address;
import com.example.wayout_ver_01.Retrofit.DTO_review;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.DTO_theme;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivitySearchCafeReadBinding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchCafe_read extends AppCompatActivity implements OnMapReadyCallback {
    private ActivitySearchCafeReadBinding bind;
    private ActivityResultLauncher<Intent> launcher;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    // 게시글 인덱스 가져오기
    private String index;
    private double x = -1, y = -1, myX, myY;
    private ProgressDialog progressDialog;
    private SearchTheme_adpater cafeRead_theme_adapter;
    private SearchCafe_review_adapter cafeRead_review_adapter;
    // 출발지, 도착지 (경도, 위도)
    private String start, goal;
    private double distance;
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;
    private Dialog dialog, menu_dialog;
    private ScaleRatingBar scaleRatingBar;
    private float dialog_rating, review_rating;
    private int page = 1, size = 8;
    /* true = 0, false = 1 */
    private int isChecked = 1;
    private int theme_number;

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("cafe_Read,89", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        theme_number = cafeRead_theme_adapter.getItemCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("cafe_Read,91", "onResume");
        setTheme();

    }

    private void setTheme() {
        String user_id = PreferenceManager.getString(getBaseContext(), "userIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_shop> call = retrofitInterface.getCafeRead(index, page, size, user_id);
        call.enqueue(new Callback<DTO_shop>() {
            @Override
            public void onResponse(Call<DTO_shop> call, Response<DTO_shop> response) {
                if(response.isSuccessful() && response.body() != null) {
                    if(response.body().getThemes().size() != theme_number) {
                        /* 테마 세팅 */
                        cafeRead_theme_adapter.clearItem();
                        for (int i = 0; i < response.body().getThemes().size(); i++) {
                            cafeRead_theme_adapter.addItem(new DTO_theme(
                                    response.body().getThemes().get(i).getIndex(),
                                    response.body().getThemes().get(i).getName(),
                                    response.body().getThemes().get(i).getDiff(),
                                    response.body().getThemes().get(i).getLimit(),
                                    response.body().getThemes().get(i).getGenre(),
                                    response.body().getThemes().get(i).getCafe(),
                                    response.body().getThemes().get(i).getImage(),
                                    response.body().getThemes().get(i).getRate()
                            ));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_shop> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySearchCafeReadBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        // 게시글 인덱스 세팅
        Intent intent = getIntent();
        index = intent.getStringExtra("인덱스");
        Log.e("CafeRead, 85", "get Index : " + index);

        /* 매장과 현재 내 사이의 거리를 계산하기 위해서 현재의 latLng 필요 */
        // 내 위치 가져 오기
        // 사용자의 위치 수신을 위한 세팅
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //사용자의 현재 위치
        Location userLocation = getMyLocation();
        if (userLocation != null) {
            double latitude = userLocation.getLatitude();
            double longitude = userLocation.getLongitude();
            start = longitude + ", " + latitude;
        }

        /* Activity Result Launcher Callback Setting~!~~!~!~!~!~!~! */
        /* type : 1, 카페 정보 수정 , type : 2, 테마 추가 */
        setLauncher();

        /* 매장에 등록 되어있는 테마 정보 불러오기 */
        // 테마 어뎁터 세팅
        setAdapter();

        // 기본 데이터 세팅
        setData();

        /* 리뷰 쓰기  */
        bind.CafeReadWriteReview.setOnClickListener((v -> {
            // 다이얼로그 등록
            dialog = new Dialog(SearchCafe_read.this); // 객체 초기화
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
            dialog.setContentView(R.layout.item_dialog); // 레이아웃과 Dialog
            DialogShow();
        }));
        /* 메뉴 선택 */
        /* 0. 테마 등록, 1. 카페 수정, 2. 카페 삭제 */
        bind.CafeReadMenu.setOnClickListener((v -> {
            menu_dialog = new Dialog(SearchCafe_read.this);
            menu_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            menu_dialog.setContentView(R.layout.item_dialog_menu_cafe);
            MenuDialogShow();
        }));
        /* 매장 찜하기 */
        bind.CafeReadLike.setOnClickListener((v -> {
            // 체크됨 -> 검은색 -> 흰색으로 , 등록
            // 등록시 ( 유저 고유값, 카페 번호, 현재 isChecked)
            // true = 0, false = 1;
            if (isChecked == 0) {
                bind.CafeReadLike.setImageResource(R.drawable.heartwhite);
                setLike();
            } else {
                // 체크 풀림 -> 흰색에서 검은색으로, 등록 삭제
                bind.CafeReadLike.setImageResource(R.drawable.heartblack);
                setLike();
            }
        }));
        /* 스크롤 페이징 */
        bind.CafeReadScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    getScroll();
                    Log.e("cafe_read, 135", "스크롤 OK");
                }
            }
        });
        /*=========================================================================================*/
    }

    private void setLauncher() {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Intent i = result.getData();
                            /* 1번 => 테마 추가, 2번 => 카페 수정 */
                            String callback = i.getStringExtra("callback");
                            switch (callback) {
                                case "add_theme":
                                case "cafe_update":
                                    setData();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
        );
    }

    private void MenuDialogShow() {
        menu_dialog.show();

        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */
        // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
        TextView menu_add_theme, menu_update, menu_delete;
        menu_add_theme = menu_dialog.findViewById(R.id.item_dialog_add_theme);
        menu_update = menu_dialog.findViewById(R.id.item_dialog_update_cafe);
        menu_delete = menu_dialog.findViewById(R.id.item_dialog_delete_cafe);
        /* 주의할점 : call -> 어느 엑티비티에서 요청했는지
        *           callback -> 어느 액티비티에서 결과를 받았는지 */
        /* 테마 추가 -> 테마 추가 Activity 로 이동 */
        menu_add_theme.setOnClickListener(v -> {
            Intent intent = new Intent(SearchCafe_read.this, SearchCafe_add_theme.class);
            intent.putExtra("cafe_index",index);
            intent.putExtra("cafe_name",bind.CafeReadName.getText().toString());
            intent.putExtra("call","cafe_read");
            launcher.launch(intent);
            menu_dialog.dismiss();
        });
        /* 주의할점 : call -> 어느 엑티비티에서 요청했는지
         *           callback -> 어느 액티비티에서 결과를 받았는지 */
        /* 매장 수정 -> 테마 수정 Activity 로 이동 */
        menu_update.setOnClickListener(v -> {
            Intent intent = new Intent(SearchCafe_read.this, SearchCafe_update.class);
            intent.putExtra("cafe_index",index);
            intent.putExtra("call","cafe_read");
            launcher.launch(intent);
            menu_dialog.dismiss();
        });
        /* 매장 삭제 -> 삭제 다이얼로그로 재 확인 후 삭제 */
        menu_delete.setOnClickListener(v -> {
            Dialog dialog = new Dialog(SearchCafe_read.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_dialog_check);
            showDeleteDialog(dialog);
            menu_dialog.dismiss();
        });
    }

    private void showDeleteDialog(Dialog dialog) {
        dialog.show();

        /* 리뷰 뷰 세팅 -> 제목, 내용 수정 */
        /* 주의사항! 제목과 내용 다시 설정해주기 */

        TextView dialog_title, dialog_content, dialog_yes, dialog_no;
        dialog_title = dialog.findViewById(R.id.item_dialog_yn_title);
        dialog_content = dialog.findViewById(R.id.item_dialog_yn_content);
        dialog_yes = dialog.findViewById(R.id.item_dialog_yn_yes);
        dialog_no = dialog.findViewById(R.id.item_dialog_yn_no);
        dialog_title.setText("매장 삭제");
        dialog_content.setText("매장을 삭제하시겠습니까?");
        /* 삭제 레트로핏 호출 */
        dialog_yes.setOnClickListener(v -> {
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_shop> call = retrofitInterface.deleteCafe(index);
            call.enqueue(new Callback<DTO_shop>() {
                @Override
                public void onResponse(Call<DTO_shop> call, Response<DTO_shop> response) {
                    if(response.isSuccessful() && response.body() != null){
                    }
                }

                @Override
                public void onFailure(Call<DTO_shop> call, Throwable t) {
                    Log.e("cafe_read, 280", "카페 삭제 실패 : " + t);
                }
            });

            dialog.dismiss();
            finish();
        });

        dialog_no.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void setLike() {
        String user_index = PreferenceManager.getString(SearchCafe_read.this, "userIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_shop> call = retrofitInterface.setLike(index, user_index, isChecked);
        call.enqueue(new Callback<DTO_shop>() {
            @Override
            public void onResponse(Call<DTO_shop> call, Response<DTO_shop> response) {
                if (response.body() != null && response.isSuccessful()) {
                    isChecked = response.body().getIsChecked();
                }
            }

            @Override
            public void onFailure(Call<DTO_shop> call, Throwable t) {
                Log.e("cafeRead, 183", "set_like 에러 : " + t);
            }
        });
    }

    private void getScroll() {
        if (page == 1) {
            page = 2;
        }
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_review>> call = retrofitInterface.getCafeReadReviewScroll(index, page, size);
        call.enqueue(new Callback<ArrayList<DTO_review>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_review>> call, Response<ArrayList<DTO_review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().size() > 0) {
                        page++;
                    }

                    /* 리뷰 초기화하면서 다시 세팅  */
                    for (int i = 0; i < response.body().size(); i++) {
                        cafeRead_review_adapter.scrollItem(new DTO_review(
                                response.body().get(i).getWriter(),
                                response.body().get(i).getContent(),
                                response.body().get(i).getRate(),
                                response.body().get(i).getIndex(),
                                response.body().get(i).getDate()
                        ));
                        cafeRead_review_adapter.notifyItemInserted((page - 1) * size + i);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_review>> call, Throwable t) {
                Log.e("cafe_read, 158", "스크롤 리뷰 에러 : " + t);
            }
        });
    }

    private void setAdapter() {
        /* 테마 세팅 어뎁터 */
        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchCafe_read.this, RecyclerView.HORIZONTAL, false);
        bind.CafeReadThemeRv.setLayoutManager(layoutManager);
        cafeRead_theme_adapter = new SearchTheme_adpater(SearchCafe_read.this);
        bind.CafeReadThemeRv.setAdapter(cafeRead_theme_adapter);

        /* 리뷰 세팅 어뎁터 */
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(SearchCafe_read.this, RecyclerView.VERTICAL, false);
        bind.CafeReadReviewRv.setLayoutManager(layoutManager2);
        cafeRead_review_adapter = new SearchCafe_review_adapter(SearchCafe_read.this, bind.CafeReadGrade, bind.CafeReadGrade2, bind.CafeReadScore, index);
        bind.CafeReadReviewRv.setAdapter(cafeRead_review_adapter);

    }

    private void DialogShow() {
        dialog.show();

        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

        // 위젯 연결 방식은 각자 취향대로
        // '아래 아니오 버튼' 처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
        // '아래 네 버튼' 처럼 바로 연결하면 일회성으로 사용하기 편함.
        // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

        scaleRatingBar = dialog.findViewById(R.id.item_dialog_grade);
        EditText dialog_content = dialog.findViewById(R.id.item_dialog_content);

        //  아니오
        TextView noBtn = dialog.findViewById(R.id.item_dialog_no);
        noBtn.setOnClickListener((v -> {
            dialog.dismiss();
//            Log.e("아니오 버튼", "OK");
        }));
        // 예
        TextView yesBtn = dialog.findViewById(R.id.item_dialog_yes);
        yesBtn.setOnClickListener((v -> {
            // 작성값 평점 가져오기
            float rate = scaleRatingBar.getRating();
            String content = dialog_content.getText().toString();
            progressDialog = new ProgressDialog(SearchCafe_read.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            // 리뷰 내용 없을시 예외 처리 -> 작성해달라고 토스트 요청
            if (content.isEmpty()) {
                Toast.makeText(SearchCafe_read.this, "리뷰 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            /* 리뷰작성 서버에 등록 후 서버에서 리뷰 데이터 가져와서 다시 세팅 */
            // 리뷰 작성하기 ( 작성 내용 , 평점 가져오기)
            writeReview(content, rate);

            progressDialog.dismiss();
            // Dialog 종료
            dialog.dismiss();
        }));
    }

    private void writeReview(String content, float rate) {
        /* 페이징 맨 처음로 초기화 어뎁터네 리뷰 지우기 -> */
        page = 1;

        String writer = PreferenceManager.getString(getBaseContext(), "autoId");
        // 서버에 저장 하고 adapter 에 저장장
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<ArrayList<DTO_review>> call = retrofitInterface.writeCafeReview(index, writer, content, rate, page, size);
        call.enqueue(new Callback<ArrayList<DTO_review>>() {
            @Override
            public void onResponse(Call<ArrayList<DTO_review>> call, Response<ArrayList<DTO_review>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    /* 바뀐 평점 세팅 */
                    bind.CafeReadGrade.setRating(response.body().get(0).getTotal());
                    bind.CafeReadGrade2.setRating(response.body().get(0).getTotal());
                    bind.CafeReadScore.setText("평점  " + response.body().get(0).getTotal());

                    /* 리뷰 초기화하면서 다시 세팅  */
                    cafeRead_review_adapter.clearItems();
                    for (int i = 0; i < response.body().size(); i++) {
                        cafeRead_review_adapter.addItem(new DTO_review(
                                response.body().get(i).getWriter(),
                                response.body().get(i).getContent(),
                                response.body().get(i).getRate(),
                                response.body().get(i).getIndex(),
                                response.body().get(i).getDate()
                        ));


                    }

                }
            }

            @Override
            public void onFailure(Call<ArrayList<DTO_review>> call, Throwable t) {
                Log.e("Cafe_Read, 205", "리뷰 작성 에러 : " + t);
            }
        });


    }


    /* 사용자 위치를 가져온다. */
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("////////////사용자에게 권한을 요청해야함");
            ActivityCompat.requestPermissions((Activity) getBaseContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            getMyLocation(); //이건 써도되고 안써도 되지만, 전 권한 승인하면 즉시 위치값 받아오려고 썼습니다!
        } else {
            System.out.println("////////////권한요청 안해도됨");
            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lat = currentLocation.getLatitude();
                double lng = currentLocation.getLongitude();
                Log.e("CafeRead,127", "위도 : " + lat);
                Log.e("CafeRead,126", "경도 : " + lng);
                // 시작점 위치
                start = lat + ", " + lng;
            }
        }
        return currentLocation;
    }

    private void setData() {
        // 로딩바 세팅
        progressDialog = new ProgressDialog(SearchCafe_read.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        /* 2 초 초과시 작동 */
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

        /* 카페 고유값, 페이지, 사이즈 , 유저 고유값 */
        String user_id = PreferenceManager.getString(getBaseContext(), "userIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_shop> call = retrofitInterface.getCafeRead(index, page, size, user_id);
        call.enqueue(new Callback<DTO_shop>() {
            @Override
            public void onResponse(Call<DTO_shop> call, Response<DTO_shop> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 좋아요 설정
                    isChecked = response.body().getIsChecked();
                    // 체크됨 -> 검은색
                    // 등록시 ( 유저 고유값, 카페 번호, 현재 isChecked)
                    // true = 0, false = 1;
                    if (isChecked == 0) {
                        bind.CafeReadLike.setImageResource(R.drawable.heartblack);
                    } else {
                        // 체크 풀림 -> 흰색
                        bind.CafeReadLike.setImageResource(R.drawable.heartwhite);
                    }
                    String user_index = PreferenceManager.getString(SearchCafe_read.this, "userIndex");
                    /* 본인일시 권한 부여 */
                    if (response.body().getWriter().equals(user_index)) {
                        bind.CafeReadMenu.setVisibility(View.VISIBLE);
                    }
                    // 게시글 설정 , 카페 이름,
                    bind.CafeReadToolbar.setText(response.body().getName());
                    bind.CafeReadName.setText(response.body().getName());
                    bind.CafeReadOpen.setText(response.body().getOpen());
                    bind.CafeReadClose.setText(response.body().getClose());
                    bind.CafeReadHoliday.setText(response.body().getHoliday());
                    bind.CafeReadAddress.setText(response.body().getAddress() + " " + response.body().getMore_address());
                    bind.CafeReadInfo.setText(response.body().getInfo());
                    Glide.with(SearchCafe_read.this)
                            .load(response.body().getImage())
                            .error(R.drawable.basic)
                            .fitCenter()
                            .into(bind.CafeReadImage);

                    /* 테마 세팅 */
                    cafeRead_theme_adapter.clearItem();
                    for (int i = 0; i < response.body().getThemes().size(); i++) {
                        cafeRead_theme_adapter.addItem(new DTO_theme(
                                response.body().getThemes().get(i).getIndex(),
                                response.body().getThemes().get(i).getName(),
                                response.body().getThemes().get(i).getDiff(),
                                response.body().getThemes().get(i).getLimit(),
                                response.body().getThemes().get(i).getGenre(),
                                response.body().getThemes().get(i).getCafe(),
                                response.body().getThemes().get(i).getImage(),
                                response.body().getThemes().get(i).getRate()
                        ));
                    }

                    /* 평점 세팅 */
                    if (response.body().getReviews().size() > 0) {
                        bind.CafeReadGrade.setRating(response.body().getReviews().get(0).getTotal());
                        bind.CafeReadGrade2.setRating(response.body().getReviews().get(0).getTotal());
                        bind.CafeReadScore.setText("평점  " + response.body().getReviews().get(0).getTotal());


                        /* 리뷰 세팅 */
                        cafeRead_review_adapter.clearItems();
                        for (int j = 0; j < response.body().getReviews().size(); j++) {
                            cafeRead_review_adapter.addItem(new DTO_review(
                                    response.body().getReviews().get(j).getWriter(),
                                    response.body().getReviews().get(j).getContent(),
                                    response.body().getReviews().get(j).getRate(),
                                    response.body().getReviews().get(j).getIndex(),
                                    response.body().getReviews().get(j).getDate()
                            ));
                        }
                    }

                    Log.e("Cafe,Read,177", response.body().getThemes().get(0).getDiff());

                    // 주소로 지도 정보 GeoCoding 하기 -> 주소 정보 -> 위도, 경도로 바꿈
                    getAddress(response.body().getAddress());

                    Log.e("CafeRead, 94", "게시글 가져오기 : OK");
                }
            }

            @Override
            public void onFailure(Call<DTO_shop> call, Throwable t) {

            }
        });
    }

    private void getAddress(String address) {
        RetrofitInterface retrofitInterface = RetrofitClient.getNaverApiClient().create(RetrofitInterface.class);
        Call<DTO_address> call = retrofitInterface.searchAddress(address, start);
        call.enqueue(new Callback<DTO_address>() {
            @Override
            public void onResponse(Call<DTO_address> call, Response<DTO_address> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // x = log(경도),  y = lat(위도)
                    x = response.body().getAddresses().get(0).getX();
                    y = response.body().getAddresses().get(0).getY();
                    distance = response.body().getAddresses().get(0).getDistance();

                    if (distance >= 1000) {
                        int km = (int) (distance / 1000);
                        bind.cafeReadDistance.setText(km + " KM");
                    } else {
                        int m = (int) distance;
                        bind.cafeReadDistance.setText(m + " M");
                    }

                    goal = x + ", " + y;
                    // 맵 정보 세팅
                    Map();

//                    Log.e("위도, 경도 받아오기", "status : " + response.body().getStatus());
//                    Log.e("위도, 경도 받아오기", "log : " + x);
//                    Log.e("위도, 경도 받아오기", "lat : " + y);
//                    Log.e("도착지", "goal : " + goal);
                }
            }

            @Override
            public void onFailure(Call<DTO_address> call, Throwable t) {
                Log.e("CafeRead, 332", "위도,경도 geocoding 에러 : " + t);
            }
        });

//        Log.e("위도, 경도 받아오기", "x : " + x + " , y : " + y);
    }

    private void Map() {
        // 지도 객체 생성
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.Cafe_read_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.Cafe_read_map, mapFragment).commit();
        }

        // getMapAsync 를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady 에서 NaverMap 객체를 받아옴 -> onMapReady 실행
        mapFragment.getMapAsync(this);

        // 위치를 반환하는 구현체인 FusedLocationSource 생성
        locationSource =
                new FusedLocationSource(this, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정정
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

        // 마커 세팅
        Marker marker = new Marker();
        marker.setPosition(new LatLng(y, x));
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(Color.RED);
        marker.setWidth(60);
        marker.setHeight(70);
        marker.setMap(naverMap);

        // 카메라 세팅 ( 위도 경도, 줌 정도) -> 숫자가 커질수록 줌이 가까워짐
        LatLng mLatLng = new LatLng(y, x);
        CameraPosition cameraPosition = new CameraPosition(mLatLng, 14);
        naverMap.setCameraPosition(cameraPosition);

        // 줌범위 설정
        naverMap.setMaxZoom(18.0);
        naverMap.setMinZoom(14.0);
        // 내 위치 가져오기
//        naverMap.addOnLocationChangeListener(location1 -> {
//            double myX = location1.getLatitude();
//            double myY = location1.getLongitude();
//            start = myX + ", " + myY;
////            Log.e("시작점", "start : " + start);
//
//            if(!distance){
//                distance = true;
//                RetrofitInterface retrofitInterface = RetrofitClient.getNaverApiClient().create(RetrofitInterface.class);
//                Call<DTO_Direction> call = retrofitInterface.searchDirection(start,goal);
//                call.enqueue(new Callback<DTO_Direction>() {
//                    @Override
//                    public void onResponse(Call<DTO_Direction> call, Response<DTO_Direction> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<DTO_Direction> call, Throwable t) {
//
//                    }
//                });
//            }
//
////            Location location = locationSource.getLastLocation();
////            if(location != null) {
////                double myX = location.getLatitude();
////                double myY = location.getLongitude();
////                start = myX + ", " + myY;
////                Log.e("시작점", "start : " + start);
////            }
//        });


        // 권한확인. 결과는 onRequestPermissionsResult 콜백 매서드 호출
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        // 다이얼로그 종료
        progressDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        // request code와 권한획득 여부 확인
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }

    }
}