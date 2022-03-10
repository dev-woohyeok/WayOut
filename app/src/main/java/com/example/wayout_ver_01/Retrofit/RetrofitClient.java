package com.example.wayout_ver_01.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // 필요한 변수 선언

    // 서버 주소
    private static final String BASE_URL = "http://13.124.21.159/"; // 주소 뒤에 "/"로 마무리 하기
    private static Retrofit retrofit;

    //레트로핏 객체에 데이터를 담아서 보내는 클라이언트 메도스
    public static Retrofit getApiClint() {
        // Gson 객체 생성
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // 로그를 보기 위한 Interceptor 생성

        // 1. 인터셉터 객체 생성
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        // 2. 로그 어느 내용까지 표현할지 단계 설정
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // 3. Retrofit 클라이언트에 넣을 OkHttpClient 객체 생성
        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(240, TimeUnit.SECONDS)
                //4. 클라이언트 객체에 인터셉터 객체 넣기
                .addInterceptor(interceptor)
                //5. 생성
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    //서버 주소 등록
                    .baseUrl(BASE_URL)
                    // Gson 컨버터 생성 Gson 형태의 파일을 객체로 만들어줌
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    // 로그 찍는 인터셉터 추가
                    .client(client)
                    // build
                    .build();
        }

        return retrofit;
    }
}
