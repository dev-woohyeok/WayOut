package com.example.wayout_ver_01.Activity.FreeBoard;


import androidx.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wayout_ver_01.Class.DateConverter;

import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.FreeWrite_Adapter;
import com.example.wayout_ver_01.RecyclerView.FreeBoard.ItemTouchHelperCallback;

import com.example.wayout_ver_01.Retrofit.DTO_board;
import com.example.wayout_ver_01.Retrofit.DTO_img;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FreeBoard_update extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private TextView freeUpdate_commit, freeUpdate_img_num;
    private ImageView freeUpdate_img;
    private EditText freeUpdate_title, freeUpdate_content;
    private ProgressDialog progressDialog;
    private String board_number, writer;
    ArrayList<String> arrayList = new ArrayList<>();
    private RecyclerView freeUpdate_rv;
    private ArrayList<String> uriList = new ArrayList<>();
    private FreeWrite_Adapter freeUpdate_adapter;
    public ItemTouchHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeboard_update);

        showProgress();
        // 뷰 세팅
        setFindView();
        // 게시글 내용 불러오기
        setIntent();
        // Adapter 세팅
        setAdapter();

        // 전송 버튼
        freeUpdate_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버로 수정 게시판 전송
                showProgress();
                submitServer();
                hideProgress();
            }
        });

        // 이미지 가져오기
        freeUpdate_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //갤러리에서 이미지 가져오기
                showProgress();
                getImage();
                hideProgress();
            }
        });

        hideProgress();
    }

    // 이미지 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) { // 이미지를 선택하지 않았을때
            Log.e(TAG, "내용 : 이미지를 선택하지 않았을때");
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else {// 이미지를 하나라도 선택한 경우
            if (data.getClipData() == null) { // 이미지를 하나만 선택한 경우
                String imageUri = getRealPathFromUri(data.getData());
                uriList.add(imageUri);

            } else { //  이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();
                Log.e(TAG, "내용 : ClipData : " + clipData.getItemCount() + clipData);

                if (clipData.getItemCount() > 3) { // 3장 이상 선택시
                    Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                } else { // 1~3 장 선택한 경우
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        String imageUri = getRealPathFromUri(clipData.getItemAt(i).getUri()); // 선택한 이미지의 uri 를 가져온다.
                        try {
                            uriList.add(imageUri); // uri 를 list 에 담는다.

//                            Log.e(TAG, "내용 : imageUri : " + imageUri);
//                            Log.e(TAG, "내용 : uriList : " + uriList);
                        } catch (Exception e) {
//                            Log.e(TAG, "에러메세지 : " + e.getLocalizedMessage());
                        }
                    }
                }
            }
        }

        if (freeUpdate_adapter.getItemCount() + uriList.size() < 4) {
            for (int i = 0; i < uriList.size(); i++) {
                freeUpdate_adapter.addItem(new DTO_img(uriList.get(i)), freeUpdate_adapter.getItemCount());
                freeUpdate_adapter.notifyItemInserted(freeUpdate_adapter.getItemCount());
                // 아이탬 갯수 표현
                freeUpdate_img_num.setText(freeUpdate_adapter.getItemCount() + "/3");
            }
        } else {
            Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setIntent() {
        Intent intent = getIntent();
        freeUpdate_title.setText(intent.getStringExtra("title"));
        freeUpdate_content.setText(intent.getStringExtra("content"));
        board_number = intent.getStringExtra("board_number");
        writer = intent.getStringExtra("writer");
        // 이미지 Uri 경로 받아 오기
        arrayList = intent.getStringArrayListExtra("imgList");

        Log.e(TAG, "내용 : 보드 번호" + board_number);
    }

    private String setDate() {
        String data = null;
        try {
           data = DateConverter.setDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return data;
    }

    private void setAdapter(){
        if (freeUpdate_adapter == null) {
            freeUpdate_rv.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FreeBoard_update.this, LinearLayoutManager.HORIZONTAL, false);
            freeUpdate_rv.setLayoutManager(linearLayoutManager);
            freeUpdate_adapter = new FreeWrite_Adapter(FreeBoard_update.this, freeUpdate_img_num);
            helper = new ItemTouchHelper(new ItemTouchHelperCallback(freeUpdate_adapter));
            helper.attachToRecyclerView(freeUpdate_rv);
            freeUpdate_rv.setAdapter(freeUpdate_adapter);

            for (int i = 0; i < arrayList.size(); i++){
                freeUpdate_adapter.setItem(arrayList.get(i));
                freeUpdate_adapter.notifyItemInserted(i);
            }
            freeUpdate_img_num.setText(arrayList.size()+"/3");
        }
    }

    private void showProgress(){
        progressDialog = new ProgressDialog(FreeBoard_update.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }
    private void hideProgress(){
        progressDialog.dismiss();
    }

    private void submitServer() {
        // 업로드할 데이터 정리

        // Retrofit 전송
        String title = freeUpdate_title.getText().toString();
        String content = freeUpdate_content.getText().toString();
        String date = setDate();


        RequestBody body_title = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody body_content = RequestBody.create(MediaType.parse("text/plain"), content);
        RequestBody body_date = RequestBody.create(MediaType.parse("text/plain"), date);
        RequestBody body_writer = RequestBody.create(MediaType.parse("text/plain"), writer);
        RequestBody body_Number = RequestBody.create(MediaType.parse("text/plain"), board_number);

        // 유효성 검사
        if (title.isEmpty()) {
            freeUpdate_title.setError("제목을 입력해주세요");
            freeUpdate_title.requestFocus();
            return;
        }

        ArrayList<DTO_img> items = new ArrayList<>();
        items = freeUpdate_adapter.getItems();
        ArrayList<MultipartBody.Part> files = new ArrayList<>();

        if(items != null) {
            for (int i = 0; i < items.size(); i++){
                Log.e(TAG, "내용 : bitmapToFile : " + items.get(i).getBitmap());
                File file = bitmapToFile(items.get(i).getBitmap() , "update_bitmaps" + i);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-date"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file" + i, i + "", requestFile);
                files.add(body);
                Log.e(TAG, "내용 : getFIles : " + files.get(i));
            }
        }

        // 이미지 갯수, 게시판 숫자 , 내용, 제목, 작성일자, 작성자 , 업로드할 파일,
        RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
        Call<DTO_board> call = retrofitInterface.updateFreeBoard(items.size(), body_Number, body_writer,body_title,body_content,body_date,files);
        call.enqueue(new Callback<DTO_board>() {
            @Override
            public void onResponse(@NonNull Call<DTO_board> call, @NonNull Response<DTO_board> response) {
                if(response.isSuccessful() && response.body() != null )
                {
                    freeUpdate_adapter.clearBitmaps();
                    Intent intent = new Intent(FreeBoard_update.this,FreeBoard_read.class);
                    intent.putExtra("board_number", board_number);
                    intent.putExtra("writer", writer);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DTO_board> call, @NonNull Throwable t) {
                Log.e(TAG, "내용 : 에러 : " + t);

            }
        });

    }

    private void getImage() {
        uriList.clear();
        if(freeUpdate_adapter.getItems().size() < 4) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, 2222);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "사진은 3장까지 선택이 가능합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFindView() {
        freeUpdate_commit = findViewById(R.id.freeUpdate_btn);
        freeUpdate_img_num = findViewById(R.id.freeUpdate_img_num);
        freeUpdate_img = findViewById(R.id.freeUpdate_img);
        freeUpdate_title = findViewById(R.id.freeUpdate_title);
        freeUpdate_content = findViewById(R.id.freeUpdate_content);
        freeUpdate_rv = findViewById(R.id.freeUpdate_rv);
    }

    // 비트맵 -> File로 전환환
    public File bitmapToFile (Bitmap getBitmap,String filename)
    {
        //create a file to write bitmap data
        File f = new File(getCacheDir(), filename);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    //Convert bitmap to byte array
        Bitmap bitmap = getBitmap;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

    //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    // 절대 경로
    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(FreeBoard_update.this, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(columnIndex);
        cursor.close();
        return url;
    }
}