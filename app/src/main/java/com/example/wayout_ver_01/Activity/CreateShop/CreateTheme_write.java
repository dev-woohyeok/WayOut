package com.example.wayout_ver_01.Activity.CreateShop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.FreeBoard.FreeBoard_write;
import com.example.wayout_ver_01.Activity.Home;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.ItemTouchHelperCallback;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryWrite_adapter;
import com.example.wayout_ver_01.RecyclerView.Theme.Theme_adapter;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.DTO_shop;
import com.example.wayout_ver_01.Retrofit.DTO_theme;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.example.wayout_ver_01.databinding.ActivityCreateThemeWriteBinding;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import lib.kingja.switchbutton.SwitchMultiButton;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTheme_write extends AppCompatActivity {
    private ActivityCreateThemeWriteBinding binding;
    public static Activity CreateShop;
    // 매장 작성에서 가져온 데이터들, shop_index = 매장 index
    private String shop_name, shop_address, shop_more, shop_info, shop_open, shop_close, shop_holiday, shop_index, theme_writer;
    private ArrayList<String> shop_images = new ArrayList<>();
    // 테마 정보 데이터들
    private ArrayList<DTO_theme> themes = new ArrayList<>();
    // 사진 임시 저장 array
    private ArrayList<String> images = new ArrayList<>();
    // 테마 추가 Layout Open, Close 처리
    private boolean writeTheme;
    // 이미지 처리 어뎁터
    private GalleryWrite_adapter createTheme_img_adapter;
    // 테마 처리 어뎁터
    private Theme_adapter theme_adapter;
    // 난이도 텝 Switch view
    private SwitchMultiButton switchMultiButton;
    // 난이도 값 저장
    private String theme_difficult = "Easy";
    // 테마 이미지
    private ArrayList<String> theme_images = new ArrayList<>();
    // 장르, 제한시간, 테마 인포, 테마 이름
    private String theme_limit, theme_genre, theme_info, theme_name;
    // 키보드 설정
    private InputMethodManager imm;
    private CreateShop_write my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateThemeWriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // 종료 시킬 엑티비티
        my = (CreateShop_write) CreateShop_write.my;

        // 매장데이터
        setIntent();
        // adapter 설정
        setAdapter();

        // 테마 작성 버튼 레이아웃 up & down
        binding.createThemeAddTheme.setVisibility(View.GONE);
        binding.createThemeWriteTheme.setOnClickListener(v -> {
            AddTheme();
        });

        // 키보드 내림 처리
        binding.createThemeAddTheme.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getCurrentFocus().clearFocus();
            }
        });

        // 매장과 테마를 서버에 등록
        binding.createThemeSubmit.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            // 서버 DB 에 매장 정보를 저장 ->  저장된 매장정보 index 를 받아옴
            // createCafe 보낸 후 -> CreateTheme 로 데이터 다시 보냄
            createCafe();
        });

        // 테마를 테마 어뎁터에 추가
        binding.createThemeMakeTheme.setOnClickListener(v -> {
            // theme_image 에 이미지를 넣어주고
            // 데이터를 adapter 에 넣어줌
            write_theme();
        });

        // 테마 info 이벤트 textWatcher 로 입력 이벤트 받음
        binding.createThemeInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.createThemeInfo.isFocusable() && !s.toString().isEmpty()) {
                    int number = binding.createThemeInfo.getText().toString().length();
                    binding.createThemeInfoNumber.setText(number + "");
                    theme_info = binding.createThemeInfo.getText().toString();
//                    Log.e("테마 인포 작성", "theme_info : " + theme_info);
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

        // 난이도 선택
        binding.createThemeDifficult.setOnSwitchListener((position, tabText) -> {
            theme_difficult = tabText;

//            Log.e("테마 난이도 선택", "난이도 : " + tabText);
        });


        // 장르 선택 Spinner 에서 선택한 아이탬 콜백
        binding.createThemeGenre.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                theme_genre = newItem;
//                Log.e("테마 장르 선택", "장르 :" + newItem);
            }
        });

        // 제한시간 선택 Spinner 에서 선택한 아이템 콜백
        binding.createThemeLimit.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                theme_limit = newItem;
//                Log.e("테마 제한시간 선택", "제한 시간 : " + newItem);
            }
        });

        // 이미지 선택 메서드 -> 갤러리에서 선택한 url 가져와서 이미지 Adapter 에 저장
        binding.createThemeImg.setOnClickListener(v -> {
            // 절대 경로로 담아 놓은 임시 리스트를 초기화
            // 재선택시 기존의 이미지와 중복되지 않기 위해서
            images.clear();
            selectImage();
        });
    }


    private void click_spinner() {
        binding.createThemeLimit.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });

        binding.createThemeGenre.setOnClickListener(v -> {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    private void createTheme() {
        themes = theme_adapter.getItems();
        String user_index = PreferenceManager.getString(getApplicationContext(),"userIndex");
        // 등록된 테마수 만큼 서버에 테마를 하나씩 업로드
        for (int i = 0; i < themes.size(); i++) {
            RequestBody name = RequestBody.create(themes.get(i).getName(), MediaType.parse("text/plain"));
            RequestBody diff = RequestBody.create(themes.get(i).getDifficult(), MediaType.parse("text/plain"));
            RequestBody limit = RequestBody.create(themes.get(i).getLimit(), MediaType.parse("text/plain"));
            RequestBody genre = RequestBody.create(themes.get(i).getGenre(), MediaType.parse("text/plain"));
            RequestBody info = RequestBody.create(themes.get(i).getInfo(), MediaType.parse("text/plain"));
            RequestBody cafe = RequestBody.create(shop_name, MediaType.parse("text/plain"));
            RequestBody index = RequestBody.create(shop_index, MediaType.parse("text/plain"));
            RequestBody user = RequestBody.create(user_index, MediaType.parse("text/plain"));

            // 절대경로를 파일로 만들어서 서버에 올린다. -> DTO 테마에 어떤 이미지가 있는지 선택된거 다 들어있음
            ArrayList<String> items = themes.get(i).getImages();
            ArrayList<MultipartBody.Part> files = new ArrayList<>();
            for (int j = 0; j < items.size(); j++) {
                // 절대주소를 파일 객체로 만듬
                File file = new File(items.get(j));
                Log.e("테마 이미지", "파일 변환 전 : " + items.get(j));
                RequestBody RequestFile = RequestBody.create(file, MediaType.parse("multipart/form-date"));
                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + i, i + "", RequestFile);
                files.add(body);
            }

            // 레트로핏 - 이름, 난이도, 제한시간, 소개, 매장이름, 매장 번호
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_theme> call = retrofitInterface.writeTheme(files, name, diff, limit, genre, info, cafe, index, user);
            call.enqueue(new Callback<DTO_theme>() {
                @Override
                public void onResponse(Call<DTO_theme> call, Response<DTO_theme> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("레트로핏 서버 반복 ", "데이터 반복확인 : " + name);
                    }
                }

                @Override
                public void onFailure(Call<DTO_theme> call, Throwable t) {

                }
            });
        }

        Intent intent = new Intent(CreateTheme_write.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    private void createCafe() {
        // 보낼 데이터 RequestBody 로 변환하기
        // 데이터 세팅
        // shop_name, 매장 사진 (이미지 uri), shop_address, shop_more (주소, 상세 정보)
        // shop_info , shop_open,close (open, close) ,shop_holiday 휴무일, shop_writer 매장 주인
        String owner = PreferenceManager.getString(getApplicationContext(), "userIndex");
        RequestBody body_name = RequestBody.create(shop_name, MediaType.parse("text/plain"));
        RequestBody body_address = RequestBody.create(shop_address, MediaType.parse("text/plain"));
        RequestBody body_more = RequestBody.create(shop_more, MediaType.parse("text/plain"));
        RequestBody body_info = RequestBody.create(shop_info, MediaType.parse("text/plain"));
        RequestBody body_open = RequestBody.create(shop_open, MediaType.parse("text/plain"));
        RequestBody body_close = RequestBody.create(shop_close, MediaType.parse("text/plain"));
        RequestBody body_holiday = RequestBody.create(shop_holiday, MediaType.parse("text/plain"));
        RequestBody body_writer = RequestBody.create(owner, MediaType.parse("text/plain"));

        ArrayList<String> items = shop_images;
        ArrayList<MultipartBody.Part> theme_files = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            // 절대주소를 파일 객체로 만듬
            File file = new File(items.get(i));
            RequestBody RequestFile = RequestBody.create(file, MediaType.parse("multipart/form-date"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + i, i + "", RequestFile);
            theme_files.add(body);
        }

        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_shop> call = retrofitInterface.writeCafe(body_name, body_address, body_more, body_info, body_open, body_close, body_holiday, body_writer, theme_files);
        call.enqueue(new Callback<DTO_shop>() {
            @Override
            public void onResponse(Call<DTO_shop> call, Response<DTO_shop> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 매장 값 가져옴
                    shop_index = response.body().getCafe_index();
                    Log.e("매장 고유번호 서버에서 받아오기", "shop_index : " + shop_index);
                    // 받아온 index 로 Theme 해당 index 에 종속되게 데이터를 저장 한다.
                    createTheme();
                }
            }

            @Override
            public void onFailure(Call<DTO_shop> call, Throwable t) {

            }
        });
    }

    private void write_theme() {
        // 테마 이름, 사진, 난이도, 장르, 제한시간, 테마 소개 순으로 저장
        // 테마 이미지를 이미지 어뎁터에서 받아온다. -> 그리고 초기화함 저거 받아오는거 해야댐
        theme_name = binding.createThemeName.getText().toString();
        theme_images = createTheme_img_adapter.getImages();

        if(theme_name.isEmpty()){
            binding.createThemeName.setError("테마명을 입력해주세요");
            binding.createThemeName.requestFocus();
            return;
        }

        /* 주의점 : 테마 어뎁터는 맨위에, 테마 이미지 어뎁터가 유효성 확인하는 어뎁터 */
        if(createTheme_img_adapter.getItemCount() < 1){
            Toast.makeText(getApplicationContext(), "사진을 추가해주세요", Toast.LENGTH_SHORT).show();
            binding.createThemeImg.requestFocus();
            return;
        }

        DTO_theme item = new DTO_theme(theme_name, theme_difficult, theme_limit, theme_genre, shop_name, theme_info, theme_images);
        theme_adapter.addItem(item);


        // 테마 등록 닫기
        writeTheme = false;
        binding.createThemeAddTheme.setVisibility(View.GONE);
        binding.createThemeArrow.setImageResource(R.drawable.down);

        // 초기화
        binding.createThemeName.setText("");
        createTheme_img_adapter.clearImages();
        binding.createThemeImgNum.setText("0/3");
        binding.createThemeDifficult.setSelectedTab(0);
        binding.createThemeGenre.setText("장르");
        binding.createThemeLimit.setText("제한시간");
        binding.createThemeInfo.setText("");

        imm.hideSoftInputFromWindow(binding.createThemeMakeTheme.getWindowToken(), 0);

        // theme 추가시 예외 처리
        if (theme_adapter.getItemCount() > 0) {
            binding.createThemeNoItem.setVisibility(View.GONE);
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
                images.add(image_uri);
            } else { // 이미지를 여러장 선택한 경우 2장 이상
                ClipData clipData = data.getClipData();

                if (clipData.getItemCount() > 3) {// 3장 이상 선택시
                    Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < clipData.getItemCount(); i++) { // 가져온 사진 갯수 만큼 반복
                        String image_uri = getReadPathFromUri(clipData.getItemAt(i).getUri());
                        images.add(image_uri);
                    }
                }
            }

        }
        // 저장한 이미지 경로를 Adapter 에 추가해서 이제 선택이 가능하도록 변경
        // 선택한 이미지와 기존의 이미지가 3개를 넘어가면 예외 처리
        if (createTheme_img_adapter.getItemCount() + images.size() < 4) {
            for (int i = 0; i < images.size(); i++) {
                createTheme_img_adapter.setUri(images.get(i));
                createTheme_img_adapter.notifyItemInserted(createTheme_img_adapter.getItemCount());
                // 아이템 숫자 변경
                binding.createThemeImgNum.setText(createTheme_img_adapter.getItemCount() + "/3");
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

    private void selectImage() {
        // 선택한 이미지는 3개 까지 가능하다
        if (createTheme_img_adapter.getItemCount() < 3) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, 2222);
        } else {
            Toast.makeText(getApplicationContext(), "사진은 3장까지 선택이 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddTheme() {
        if (writeTheme) {
            binding.createThemeAddTheme.setVisibility(View.GONE);
            binding.createThemeArrow.setImageResource(R.drawable.down);
            writeTheme = false;
        } else {
            binding.createThemeAddTheme.setVisibility(View.VISIBLE);
            binding.createThemeArrow.setImageResource(R.drawable.up);
            writeTheme = true;
        }
    }

    private void setAdapter() {

        // 키보드 설정
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // 이미지 리사이클러뷰 설정
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CreateTheme_write.this, LinearLayoutManager.HORIZONTAL, false);
        binding.createThemeImageRv.setLayoutManager(linearLayoutManager);
        createTheme_img_adapter = new GalleryWrite_adapter(CreateTheme_write.this, binding.createThemeImgNum);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(createTheme_img_adapter));
        itemTouchHelper.attachToRecyclerView(binding.createThemeImageRv);
        binding.createThemeImageRv.setAdapter(createTheme_img_adapter);

        // 테마 리사이클러뷰 설정
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(CreateTheme_write.this, LinearLayoutManager.HORIZONTAL, false);
        binding.createThemeThemeRv.setLayoutManager(linearLayoutManager1);
        theme_adapter = new Theme_adapter(CreateTheme_write.this);
        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(new ItemTouchHelperCallback(theme_adapter));
        itemTouchHelper1.attachToRecyclerView(binding.createThemeThemeRv);
        binding.createThemeThemeRv.setAdapter(theme_adapter);
        theme_adapter.onDelete();
    }

    private void setIntent() {
        Intent i = getIntent();
        shop_name = i.getStringExtra("shop_name");
        shop_address = i.getStringExtra("shop_address");
        shop_more = i.getStringExtra("shop_address_more");
        shop_info = i.getStringExtra("shop_info");
        shop_open = i.getStringExtra("shop_open");
        shop_close = i.getStringExtra("shop_close");
        shop_holiday = i.getStringExtra("shop_holiday");
        shop_images = i.getStringArrayListExtra("shop_image");
        Log.e("인텐트 확인", "  Shop_name : " + shop_name);

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