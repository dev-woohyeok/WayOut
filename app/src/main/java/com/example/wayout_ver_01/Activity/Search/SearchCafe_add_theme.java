package com.example.wayout_ver_01.Activity.Search;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.wayout_ver_01.Activity.CreateShop.CreateTheme_write;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.ItemTouchHelperCallback;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryWrite_adapter;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.DTO_image;
import com.example.wayout_ver_01.Retrofit.DTO_review;
import com.example.wayout_ver_01.Retrofit.DTO_theme;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivitySearchCafeAddThemeBinding;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lib.kingja.switchbutton.SwitchMultiButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchCafe_add_theme extends AppCompatActivity {
    private ActivitySearchCafeAddThemeBinding bind;
    /* 이미지 처리 어뎁터 */
    private GalleryWrite_adapter addTheme_img_adapter;
    /* 난이도 */
    private SwitchMultiButton switchMultiButton;
    private String theme_diff = "Easy";
    /* 테마 이미지 */
    private ArrayList<String> theme_images = new ArrayList<>();
    /* 장르, 제한시간, 소개, 이름 */
    private String theme_limit = "", theme_genre = "", theme_info = "", theme_name = "", cafe_name = "";
    /* 키보드 설정 -> 이외 레이아웃 클릭시 키보드 내리기 */
    private InputMethodManager imm;
    /* Call : cafe_read -> 매장 등록, Call : update_theme -> 매장 수정 */
    private String call;
    /* 1번, 카페에 등록하기, 2번, 테마 수정시키기 */
    private String cafe_index, theme_index;
    private String user_index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySearchCafeAddThemeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        user_index = PreferenceManager.getString(getApplicationContext(),"userIndex");
        Log.e("user_index", "user_index : " + user_index);
        // 테마 데이터 세팅
        setData();

        /* 키보드 세팅 */
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 키보드 내림 처리
        bind.addThemeLayout.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getCurrentFocus().clearFocus();
            }
        });

        /* 어뎁터 세팅 */
        setAdapter();

        /* Submit 버튼 클릭스 */
        bind.addThemeSubmit.setOnClickListener(v -> {
            theme_name = bind.addThemeName.getText().toString();

            // 키보드 설정
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getCurrentFocus().clearFocus();
            }

            /* 유효성 체크 */
            if (theme_name.isEmpty()) {
                bind.addThemeName.requestFocus();
                bind.addThemeName.setError("테마명을 입력해주세요");
                return;
            }
            if (addTheme_img_adapter.getItemCount() < 1) {
                bind.addThemeImageRv.requestFocus();
                Toast.makeText(getBaseContext(), "이미지를 한 장 이상 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (theme_genre.isEmpty()) {
                bind.addThemeGenre.requestFocus();
                Toast.makeText(getBaseContext(), "장르를 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            if (theme_limit.isEmpty()) {
                bind.addThemeLimit.requestFocus();
                Toast.makeText(getBaseContext(), "제한 시간을 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }


            /* 주의점 : 필요한 Index 변수 잘 확인합시다. */
            if (call.equals("cafe_read")) {/* 테마 추가 ->  */
                /* cafe_index 로 테마를 카페에 추가 */
                addTheme();
            } else {/* 테마 수정 ->  */
                /* theme_index 로 특정 테마정보 다 불러오기 */
                updateTheme();

            }

        });
        // 난이도 선택
        bind.addThemeDifficult.setOnSwitchListener(((position, tabText) -> {
            theme_diff = tabText;
        }));

        // 장르 선택 Spinner 에서 선택한 아이탬 콜백
        bind.addThemeGenre.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                theme_genre = newItem;
//                Log.e("테마 장르 선택", "장르 :" + newItem);
            }
        });

        // 제한시간 선택 Spinner 에서 선택한 아이탬 콜백
        bind.addThemeLimit.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                theme_limit = newItem;
//                Log.e("테마 장르 선택", "장르 :" + newItem);
            }
        });

        bind.addThemeImg.setOnClickListener(v -> {
            theme_images.clear();
            selectImage();
        });

        // 테마 Info 이벤트 -> 글자수 반환함
        bind.addThemeInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // info 입력 키보드 up && 입력 값 있을때
                if (bind.addThemeInfo.isFocusable() && !s.toString().isEmpty()) {
                    int number = bind.addThemeInfo.getText().toString().length();
                    bind.addThemeInfoNumber.setText("" + number);
                    theme_info = bind.addThemeInfo.getText().toString();
                    if (number >= 200) {
                        number = 200;
                        Toast.makeText(getApplicationContext(), "200자까지 입력이 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void checkEffect() {

    }

    private void selectImage() {
        // 선택한 이미지는 3개 까지 가능하다
        if (addTheme_img_adapter.getItemCount() < 3) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, 2222);
        } else {
            Toast.makeText(getApplicationContext(), "사진은 3장까지 선택이 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) { // 이미지 선택을 안했을때
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else {// 이미지를 한개 이상 선택한 경우 -> 1개 or 2개 이상
            if (data.getClipData() == null) { // 이미지를 1개 선택한 경우
                String image_uri = getReadPathFromUri(data.getData());
                theme_images.add(image_uri);
            } else { // 이미지를 여러장 선택한 경우 2장 이상
                ClipData clipData = data.getClipData();

                if (clipData.getItemCount() > 3) {// 3장 이상 선택시
                    Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < clipData.getItemCount(); i++) { // 가져온 사진 갯수 만큼 반복
                        String image_uri = getReadPathFromUri(clipData.getItemAt(i).getUri());
                        theme_images.add(image_uri);
                    }
                }
            }

        }
        // 저장한 이미지 경로를 Adapter 에 추가해서 이제 선택이 가능하도록 변경
        // 선택한 이미지와 기존의 이미지가 3개를 넘어가면 예외 처리
        if (addTheme_img_adapter.getItemCount() + theme_images.size() < 4) {
            for (int i = 0; i < theme_images.size(); i++) {
                addTheme_img_adapter.setUri(theme_images.get(i));
                addTheme_img_adapter.notifyItemInserted(addTheme_img_adapter.getItemCount());
                // 아이템 숫자 변경
                bind.addThemeImgNum.setText(addTheme_img_adapter.getItemCount() + "/3");
            }
        }
    }

    private String getReadPathFromUri(Uri uri) {
        String[] strArray = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), uri, strArray, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String image_uri = cursor.getString(columnIndex);
        cursor.close();
        return image_uri;
    }

    private void updateTheme() {
        Log.e("여기 실행??", "실행되냐?3");
        RequestBody name = RequestBody.create(theme_name, MediaType.parse("text/plain"));
        RequestBody diff = RequestBody.create(theme_diff, MediaType.parse("text/plain"));
        RequestBody limit = RequestBody.create(theme_limit, MediaType.parse("text/plain"));
        RequestBody genre = RequestBody.create(theme_genre, MediaType.parse("text/plain"));
        RequestBody info = RequestBody.create(theme_info, MediaType.parse("text/plain"));
        RequestBody t_index = RequestBody.create(theme_index, MediaType.parse("text/plain"));

        // 피드맵을 모두 파일로 만들어서 서버에 올린다.
        ArrayList<DTO_gallery> items = new ArrayList<>();
        ArrayList<MultipartBody.Part> files = new ArrayList<>();
        items = addTheme_img_adapter.getItems();

        // 비트맵 을 file 로 바꿔서 multipartBody.Part 에 저장
        if(items != null) {
            for (int i = 0; i < items.size(); i++){
                File file = bitmapToFile(items.get(i).getBitmap(), "bitmaps" + i);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-date"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + i, i + "", requestFile);
                files.add(body);
            }
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_theme> call = retrofitInterface.updateTheme(name,diff,limit,genre,info,t_index,files);
        call.enqueue(new Callback<DTO_theme>() {
            @Override
            public void onResponse(Call<DTO_theme> call, Response<DTO_theme> response) {
                if(response.body() != null & response.isSuccessful()){
                    Log.e("여기 실행??", "실행되냐?");
                    Intent i = new Intent(SearchCafe_add_theme.this,SearchTheme_read.class);
                    i.putExtra("callback","add_theme");
                    setResult(RESULT_OK,i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DTO_theme> call, Throwable t) {
                Log.e("여기 실행??", "실행되냐?2");
            }
        });
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

    private void addTheme() {
        // 보낼 데이터 RequestBody 로 변환하기
        // 데이터 세팅
        RequestBody name = RequestBody.create(theme_name, MediaType.parse("text/plain"));
        RequestBody diff = RequestBody.create(theme_diff, MediaType.parse("text/plain"));
        RequestBody limit = RequestBody.create(theme_limit, MediaType.parse("text/plain"));
        RequestBody genre = RequestBody.create(theme_genre, MediaType.parse("text/plain"));
        RequestBody info = RequestBody.create(theme_info, MediaType.parse("text/plain"));
        RequestBody cafe = RequestBody.create(cafe_name, MediaType.parse("text/plain"));
        RequestBody index = RequestBody.create(cafe_index, MediaType.parse("text/plain"));
        RequestBody user = RequestBody.create(user_index, MediaType.parse("text/plain"));

        Log.e("테마 정보 맞음?", "이름" + cafe_name);

        // 절대경로를 파일로 만들어서 서버에 올린다. -> DTO 테마에 어떤 이미지가 있는지 선택된거 다 들어있음
        ArrayList<String> items = addTheme_img_adapter.getImages();
        ArrayList<MultipartBody.Part> files = new ArrayList<>();
        for (int j = 0; j < items.size(); j++) {
            // 절대주소를 파일 객체로 만듬
            File file = new File(items.get(j));
            Log.e("테마 이미지", "파일 변환 전 : " + items.get(j));
            RequestBody RequestFile = RequestBody.create(file, MediaType.parse("multipart/form-date"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + j, j + "", RequestFile);
            files.add(body);
        }

        // 레트로핏 - 이름, 난이도, 제한시간, 소개, 매장이름, 매장 번호
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_theme> call = retrofitInterface.writeTheme(files, name, diff, limit, genre, info, cafe, index,user);
        call.enqueue(new Callback<DTO_theme>() {
            @Override
            public void onResponse(Call<DTO_theme> call, Response<DTO_theme> response) {
                if (response.isSuccessful() && response.body() != null) {
                    /* 추가 후 카페 게시글로 돌아가기 */
                    Intent i = new Intent(getBaseContext(), SearchCafe_read.class);
                    i.putExtra("callback", "add_theme");
                    setResult(RESULT_OK, i);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DTO_theme> call, Throwable t) {
                Log.e("add_theme, 295 ", "테마 추가 에러 : " + t);
            }
        });
}

    private void setAdapter() {
        /* 이미지 어뎁터 세팅 */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false);
        bind.addThemeImageRv.setLayoutManager(linearLayoutManager);
        addTheme_img_adapter = new GalleryWrite_adapter(getBaseContext(), bind.addThemeImgNum);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(addTheme_img_adapter));
        itemTouchHelper.attachToRecyclerView(bind.addThemeImageRv);
        bind.addThemeImageRv.setAdapter(addTheme_img_adapter);
    }

    private void setData() {
        /* 인텐트, call 값에 해당하는 세팅 */
        /* Call : 1 테마 추가 , call :2 테마 수정 */
        Intent i = getIntent();
        call = i.getStringExtra("call");
        switch (call) {
            case "cafe_read":
                /* 테마 추가 툴바, 등록 버튼  */
                bind.addThemeToolbar.setText("테마 추가");
                bind.addThemeSubmit.setText("등록");
                cafe_index = i.getStringExtra("cafe_index");
                cafe_name = i.getStringExtra("cafe_name");
                break;
            case "theme_read":
                /* 테마 index 로 해당 테마 값 가져오기 */
                /* 테마 수정 툴바, 수정 버튼, 수정할 데이터 세팅  */
                bind.addThemeToolbar.setText("테마 수정");
                bind.addThemeSubmit.setText("수정");
                theme_index = i.getStringExtra("theme_index");
                /* 수정할 데이터 불러오기 */
                setTheme();
                break;
            default:
                /* 잘못된 접근인거지  */
                Toast.makeText(getBaseContext(), "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                finish();
        }
    }

    private void setTheme() {
        /* 수정할 데이터 불러오기 */
        String user_index = PreferenceManager.getString(getApplicationContext(),"userIndex");
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_theme> call = retrofitInterface.getThemeRead(theme_index,1,8,user_index);
        call.enqueue(new Callback<DTO_theme>() {
            @Override
            public void onResponse(Call<DTO_theme> call, Response<DTO_theme> response) {
                if(response.isSuccessful() && response.body() != null) {
                    bind.addThemeName.setText(response.body().getName());
                    theme_name = response.body().getName();
                    ArrayList<DTO_image> image_url = response.body().getImage_uri();
                    for(int i = 0; i< image_url.size(); i++){
                        addTheme_img_adapter.addItems(image_url.get(i).getImage_uri());
                    }
                    bind.addThemeGenre.setText(response.body().getGenre());
                    theme_genre = response.body().getGenre();
                    bind.addThemeLimit.setText(response.body().getLimit());
                    theme_limit = response.body().getLimit();
                    bind.addThemeInfo.setText(response.body().getInfo());
                    theme_info = response.body().getInfo();
                    bind.addThemeInfoNumber.setText(response.body().getInfo().length() + "");
                    /* 수정할 내용 세팅 */
                    String[] difficult = {"Easy","Normal","Hard","Hell"};
                    String dif = response.body().getDiff();
                    theme_diff = response.body().getDiff();
                    switch (dif){
                        case "Easy":
                            bind.addThemeDifficult.setSelectedTab(0);
                            break;
                        case "Normal":
                            bind.addThemeDifficult.setSelectedTab(1);
                            break;
                        case "Hard":
                            bind.addThemeDifficult.setSelectedTab(2);
                            break;
                        default:
                            bind.addThemeDifficult.setSelectedTab(3);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<DTO_theme> call, Throwable t) {
                Log.e("add_theme,362", "테마 데이터 세팅 에러 : " + t );
            }
        });

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