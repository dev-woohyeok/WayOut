package com.example.wayout_ver_01.Activity.Gallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.Gallery.GalleryWrite_adapter;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.ItemTouchHelperCallback;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.DTO_image;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryBoard_write extends AppCompatActivity {
    private TextView galleryWrite_submit, galleryWrite_img_num;
    private EditText galleryWrite_cafe, galleryWrite_theme, galleryWrite_content;
    private ImageView galleryWrite_img;
    private RecyclerView galleryWrite_rv;
    private ProgressDialog progressDialog;
    private ArrayList<String> uri_list = new ArrayList<>();
    private GalleryWrite_adapter galleryWrite_adapter;
    private ItemTouchHelper itemTouchHelper;
    private boolean mode_update;
    private String board_number;
    private ArrayList<String> items = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_board_write);

        Intent intent = getIntent();
        mode_update = intent.getBooleanExtra("mode", false);


        // 뷰들 생성 모아놓은 클래스
        setView();
        // 리사이클러뷰에 어뎁터 및 레이아웃매니저, 아이탬 터치 헬퍼를 달아준다.
        setAdapter();

        if(mode_update){
            // 수정시 데이터 가져오기
            galleryWrite_cafe.setText(intent.getStringExtra("cafe"));
            galleryWrite_theme.setText(intent.getStringExtra("theme"));
            galleryWrite_content.setText(intent.getStringExtra("content"));
            board_number = intent.getStringExtra("board_number");
            items = intent.getStringArrayListExtra("items");
            for(int i = 0;  i< items.size(); i++) {
                galleryWrite_adapter.addItems(items.get(i));
                galleryWrite_adapter.notifyItemInserted(i);
            }
        }

        // 전송 버튼 눌렀을때
        galleryWrite_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode_update) {
                    update_board();
                }else{
                    submitBoard();
                }

            }
        });

        // 이미지 가져오기
        galleryWrite_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로딩창 실행
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                // 절대 경로를 담아 놓은 리스트를 초기화한다.
                // 재선택시 기존의 이미지와 중복되지 않게 하기 위해서
                uri_list.clear();

                // 선택한 이미지는 3개를 넘을 수 없다.
                if (galleryWrite_adapter.getItemCount() < 3) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, 2222);
                } else {
                    Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) { // 이미지를 선택하지 않았을때
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else {// 이미지를 하나라도 선택한 경우
            if (data.getClipData() == null) { // 이미지를 하나만 선택한 경우
                String imageUri = getRealPathFromUri(data.getData());
                uri_list.add(imageUri);

            } else { //  이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();

                if (clipData.getItemCount() > 3) { // 3장 이상 선택시
                    Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                } else { // 1~3 장 선택한 경우
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        String imageUri = getRealPathFromUri(clipData.getItemAt(i).getUri()); // 선택한 이미지의 uri 를 가져온다.
                        uri_list.add(imageUri); // uri 를 list 에 담는다.
                    }
                }
            }
        }

        // 선택한 절대 경로를 어뎁터에 넣어서 리사이클러뷰에 표시해준다.
        if (galleryWrite_adapter.getItemCount() + uri_list.size() < 4) {
            for (int i = 0; i < uri_list.size(); i++) {
                // 저장해놓은 절대 경로를 리사이클러뷰 리스트 안에 넣는다.
                galleryWrite_adapter.setUri(uri_list.get(i));
                galleryWrite_adapter.notifyItemInserted(galleryWrite_adapter.getItemCount());
                // 아이탬 갯수 표현
                galleryWrite_img_num.setText(galleryWrite_adapter.getItemCount() + "/3");
            }
        } else {
            Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.4", Toast.LENGTH_SHORT).show();
        }
        // 로딩창 종료
        progressDialog.dismiss();
    }

    // 상대경로 -> 절대경로로 변환
    private String getRealPathFromUri(Uri uri) {
        String[] strArray = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), uri, strArray, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);
        cursor.close();
        return url;
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

    private void update_board(){
        // 로딩창 보이기
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // 서버에 등록할 갤러리 게시판 데이터 정의
        String cafe = galleryWrite_cafe.getText().toString();
        String theme = galleryWrite_theme.getText().toString();
        String writer = PreferenceManager.getString(getApplicationContext(), "autoNick");
        String content = galleryWrite_content.getText().toString();
        String number = board_number;

        // RequestBody 정리
        RequestBody body_cafe = RequestBody.create(MediaType.parse("text/plain"), cafe);
        RequestBody body_theme = RequestBody.create(MediaType.parse("text/plain"), theme);
        RequestBody body_writer = RequestBody.create(MediaType.parse("text/plain"), writer);
        RequestBody body_content = RequestBody.create(MediaType.parse("text/plain"), content);
        RequestBody body_number = RequestBody.create(MediaType.parse("text/plain"), number);

        // 유효성 검사
        if (cafe.isEmpty()) {
            galleryWrite_cafe.setError("카페명을 입력해주세요");
            galleryWrite_cafe.requestFocus();
            return;
        }
        if (theme.isEmpty()) {
            galleryWrite_theme.setError("테마명을 입력해주세요");
            galleryWrite_theme.requestFocus();
            return;
        }

        // 이미지를 담을 ArrayList : files
        // bitmap 이 담긴 arrayList : items
        ArrayList<DTO_gallery> items = new ArrayList<>();
        ArrayList<MultipartBody.Part> files = new ArrayList<>();
        items = galleryWrite_adapter.getItems();

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
        Call<DTO_gallery> call = retrofitInterface.updateGalleryBoard(body_number,body_cafe,body_theme,body_content,body_writer,files);
        call.enqueue(new Callback<DTO_gallery>() {
            @Override
            public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {
                if(response.isSuccessful() && response.body() != null) {
                    finish();
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(), "업데이트 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DTO_gallery> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "업데이트 실페", Toast.LENGTH_SHORT).show();
                Log.e("갤러리 게시판 업데이트 실패", "내용 : " + t);
            }
        });


    }

    private void submitBoard(){
        // 로딩창 보이기
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // 서버에 등록할 갤러리 게시판 데이터 정의
        String cafe = galleryWrite_cafe.getText().toString();
        String theme = galleryWrite_theme.getText().toString();
        String writer = PreferenceManager.getString(getApplicationContext(), "autoNick");
        String content = galleryWrite_content.getText().toString();

        // RequestBody 정리
        RequestBody body_cafe = RequestBody.create(MediaType.parse("text/plain"), cafe);
        RequestBody body_theme = RequestBody.create(MediaType.parse("text/plain"), theme);
        RequestBody body_writer = RequestBody.create(MediaType.parse("text/plain"), writer);
        RequestBody body_content = RequestBody.create(MediaType.parse("text/plain"), content);

        // 유효성 검사
        if (cafe.isEmpty()) {
            galleryWrite_cafe.setError("카페명을 입력해주세요");
            galleryWrite_cafe.requestFocus();
            return;
        }
        if (theme.isEmpty()) {
            galleryWrite_theme.setError("테마명을 입력해주세요");
            galleryWrite_theme.requestFocus();
            return;
        }

        // 이미지를 담을 ArrayList : files
        // bitmap 이 담긴 arrayList : items
        ArrayList<DTO_gallery> items = new ArrayList<>();
        ArrayList<MultipartBody.Part> files = new ArrayList<>();
        items = galleryWrite_adapter.getItems();

        // 비트맵 을 file 로 바꿔서 multipartBody.Part 에 저장
        if(items != null) {
            for (int i = 0; i < items.size(); i++){
                File file = bitmapToFile(items.get(i).getBitmap(), "bitmaps" + i);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-date"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + i, i + "", requestFile);
                files.add(body);
            }
        }
        // 레트로핏으로 업로드
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_gallery> call = retrofitInterface.writeGalleryBoard(body_cafe,body_theme,body_writer,body_content,files);
        call.enqueue(new Callback<DTO_gallery>() {
            @Override
            public void onResponse(Call<DTO_gallery> call, Response<DTO_gallery> response) {
                // 로딩창 종료
                progressDialog.dismiss();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(galleryWrite_cafe.getWindowToken(), 0);
                finish();
            }

            @Override
            public void onFailure(Call<DTO_gallery> call, Throwable t) {
                // 로딩창 종료
                progressDialog.dismiss();
                finish();
            }
        });
    }

    private void setAdapter() {
        // view 홀더 측정 막기, 속도가 조금 올라감
        galleryWrite_rv.setHasFixedSize(true);
        // 어떤 형식으로 item 을 배치할지 결정하는 manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(GalleryBoard_write.this, LinearLayoutManager.HORIZONTAL, false);
        // layout manager 설정
        galleryWrite_rv.setLayoutManager(linearLayoutManager);
        // 어뎁터 생성
        galleryWrite_adapter = new GalleryWrite_adapter(GalleryBoard_write.this, galleryWrite_img_num);
        // 리사이클러뷰의 drag & drop 을 도와주는 클래스 (리스너 클래스가 필요함, 리스너를 implement 시킴)
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(galleryWrite_adapter));
        // 해당 클래스를 리사이클러뷰에 설정해준다.
        itemTouchHelper.attachToRecyclerView(galleryWrite_rv);
        // 어뎁터 설정
        galleryWrite_rv.setAdapter(galleryWrite_adapter);
    }

    private void setView() {
        galleryWrite_submit = findViewById(R.id.galleryWrite_submit);
        galleryWrite_img_num = findViewById(R.id.galleryWrite_img_num);
        galleryWrite_cafe = findViewById(R.id.galleryWrite_cafe);
        galleryWrite_theme = findViewById(R.id.galleryWrite_theme);
        galleryWrite_content = findViewById(R.id.galleryWrite_content);
        galleryWrite_img = findViewById(R.id.galleryWrite_img);
        galleryWrite_rv = findViewById(R.id.galleryWrite_rv);
        progressDialog = new ProgressDialog(GalleryBoard_write.this);
    }
}