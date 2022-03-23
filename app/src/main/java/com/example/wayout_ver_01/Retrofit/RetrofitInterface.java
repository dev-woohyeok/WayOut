package com.example.wayout_ver_01.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitInterface {
    // 서버에 보내고 받을 인터페이스 설정
    // 인터페이스 설정시 retrofit 이 자동으로 처리해줌
    // @POST : HTTP 통신 방식 // test.php 서버측 URL 경로
    // @FormUrlEncoded 무조건해야함 -> URL 형식으로 인코디애서 작성됨
    @FormUrlEncoded
    @POST("joinInsert.php")
    // Call 함수 -> DTO 클래스  // @Field 에는 보낼 Key&value 입력
    Call<JoinRequest> insertJoin(
            @Field("userId") String userId,
            @Field("userPw") String userPw,
            @Field("userNick") String userNick,
            @Field("userPhone") String userPhone
    );

    @FormUrlEncoded
    @POST("joinSelect.php")
    Call<JoinRequest> selectJoin(
            @Field("userId") String userId,
            @Field("userPw") String userPw
    );

    @FormUrlEncoded
    @POST("userUpdate.php")
    Call<User> userReset(
            @Field("userId") String userId,
            @Field("userPw") String userPw,
            @Field("userNick") String userNick,
            @Field("userIndex") Integer userIndex
    );

    @FormUrlEncoded
    @POST("phoneInsert.php")
    Call<User> userPhoneCk(
            @Field("userPhone") String userPhone,
            @Field("code") String Code
    );

    @FormUrlEncoded
    @POST("userPwReset.php")
    Call<User> userPwReset(
            @Field("userPw") String userPw,
            @Field("userNewPw") String userNewPw,
            @Field("userIndex") Integer userIndex
    );

    @FormUrlEncoded
    @POST("joinDelete.php")
    Call<User> userDelete(
            @Field("userIndex") Integer userIndex
    );

    @FormUrlEncoded
    @POST("findResetPw.php")
    Call<User> findResetPw(
            @Field("userPw") String userPw,
            @Field("userPhone") String userPhone
    );

    @Multipart
    @POST("updateUserProfile.php")
    Call<User> userProfile(
            @Part MultipartBody.Part file,
            @Part("userIndex") Integer userIndex
    );

    @FormUrlEncoded
    @POST("getUserProfile.php")
    Call<User> getUserProfile(
            @Field("userIndex") Integer userIndex
    );

    @FormUrlEncoded
    @POST("deleteUserProfile.php")
    Call<User> deleteUserProfile(
            @Field("userIndex") Integer userIndex
    );

    @Multipart
    @POST("updateFreeBoard.php")
    Call<DTO_board> updateFreeBoard(
            @Part("imgNum") int imgNum,
            @Part("board_number") RequestBody board_number,
            @Part("writer") RequestBody writer,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("date") RequestBody date,
            @Part ArrayList<MultipartBody.Part> files
    );

    @Multipart
    @POST("writeFreeboard.php")
    Call<DTO_board> writeFreeBoard(
            @Part("writer") RequestBody writer,
            @Part("title") RequestBody title,
            @Part("content")RequestBody content,
            @Part("date") RequestBody date,
            @Part("imgNum") int imgNum,
            @Part ArrayList<MultipartBody.Part> files
    );
    @FormUrlEncoded
    @POST("writeFreeReply.php")
    Call<DTO_free_reply> writeFreeReply(
            @Field("reply_writer") String reply_writer,
            @Field("reply_content") String reply_content,
            @Field("reply_date") String reply_date,
            @Field("board_number") String board_number
    );

    @GET("getFreeBoard.php")
    Call<ArrayList<DTO_board>> getFreeBoard();

    @FormUrlEncoded
    @POST("getFreeRead.php")
    Call<DTO_board> getFreeRead(
            @Field("board_num") String board_num
    );

    @FormUrlEncoded
    @POST("deleteFreeRead.php")
    Call<DTO_board> deleteFreeBoard(
            @Field("board_number") String board_number,
            @Field("writer") String writer
    );

    @FormUrlEncoded
    @POST("deleteFreeReply.php")
    Call<DTO_free_reply> deleteFreeReply(
            @Field("reply_index") String reply_index
    );

    @FormUrlEncoded
    @POST("getFreeReply.php")
    Call<DTO_free_reply> getFreeReply(
            @Field("reply_index") String reply_index
    );

    @FormUrlEncoded
    @POST("updateFreeReply.php")
    Call<DTO_free_reply> updateFreeReply(
            @Field("reply_index") String reply_index,
            @Field("reply_content") String reply_content,
            @Field("reply_date") String reply_date
    );

    @FormUrlEncoded
    @POST("getFreeComment.php")
    Call<ArrayList<DTO_free_reply>> getFreeComment(
            @Field("reply_index") String reply_index
    );

    @FormUrlEncoded
    @POST("updateFreeComment.php")
    Call<DTO_free_reply> updateFreeComment(
            @Field("reply_index") String reply_index,
            @Field("reply_writer") String reply_writer,
            @Field("reply_content") String reply_content,
            @Field("reply_date") String reply_date
    );
}
