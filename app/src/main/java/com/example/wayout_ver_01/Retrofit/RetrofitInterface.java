package com.example.wayout_ver_01.Retrofit;

import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_chat;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_follow;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_message;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_room;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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

    @Headers({
            "X-NCP-APIGW-API-KEY-ID:ztfhsqjefk",
            "X-NCP-APIGW-API-KEY:MYqVC77uYSv1PNI1pXMutqy27FsmqdbiClMItnqa"
    })

    @GET("/map-geocode/v2/geocode")
    public Call<DTO_address> searchAddress(
            @Query("query") String query,
            @Query("coordinate") String start
    );

    @Headers({
            "X-NCP-APIGW-API-KEY-ID:ztfhsqjefk",
            "X-NCP-APIGW-API-KEY:MYqVC77uYSv1PNI1pXMutqy27FsmqdbiClMItnqa"
    })
    @GET("/map-direction/v1/driving")
    public Call<DTO_Direction> searchDirection(
            @Query("start") String start,
            @Query("goal") String goal);

    @FormUrlEncoded
    @POST("joinSelect.php")
    Call<JoinRequest> selectJoin(
            @Field("userId") String userId,
            @Field("userPw") String userPw
    );

    @FormUrlEncoded
    @POST("writerFollow.php")
    Call<DTO_message> writeFollow(
            @Field("user_id") String user_id,
            @Field("follower") String follower
    );

    @FormUrlEncoded
    @POST("deleteFollow.php")
    Call<DTO_message> deleteFollow(
            @Field("user_id") String user_id,
            @Field("follower") String follower
    );

    @FormUrlEncoded
    @POST("writeCafeReview.php")
    Call<ArrayList<DTO_review>> writeCafeReview(
            @Field("index") String index,
            @Field("writer") String writer,
            @Field("content") String content,
            @Field("rate") float grade,
            @Field("page") int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("writeThemeReview.php")
    Call<ArrayList<DTO_review>> writeThemeReview(
            @Field("theme_index") String theme_index,
            @Field("writer") String writer,
            @Field("user_index") String user_index,
            @Field("content") String content,
            @Field("diff") String diff,
            @Field("success") String success,
            @Field("rate") float rate,
            @Field("page") int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("getCafeRead.php")
    Call<DTO_shop> getCafeRead(
            @Field("index") String index,
            @Field("page") int page,
            @Field("size") int size,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("getThemeRead.php")
    Call<DTO_theme> getThemeRead(
            @Field("index") String index,
            @Field("page") int page,
            @Field("size") int size,
            @Field("user_index") String user_id
    );

    @FormUrlEncoded
    @POST("getCafeReadReview.php")
    Call<DTO_review> getCafeReadReview(
            @Field("index") String index,
            @Field("page") int page,
            @Field("size") int size
    );


    @FormUrlEncoded
    @POST("getCafeReadScrollReview.php")
    Call<ArrayList<DTO_review>> getCafeReadReviewScroll(
            @Field("index") String index,
            @Field("page") int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("getThemeReadScrollReview.php")
    Call<ArrayList<DTO_review>> getThemeReadReviewScroll(
            @Field("theme_index") String theme_index,
            @Field("page") int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("updateCafeReview.php")
    Call<DTO_review> updateCafeReview(
            @Field("cafe_index") String cafe_index,
            @Field("index") String index,
            @Field("rate") float rate,
            @Field("content") String content
    );

    @FormUrlEncoded
    @POST("updateThemeReview.php")
    Call<DTO_review> updateThemeReview(
            @Field("theme_index") String theme_index,
            @Field("review_index") String review_index,
            @Field("rate") float rate,
            @Field("content") String content,
            @Field("success") String success,
            @Field("diff") String diff
    );

    @FormUrlEncoded
    @POST("deleteCafeReview.php")
    Call<DTO_review> deleteCafeReview(
            @Field("cafe_index") String cafe_index,
            @Field("index") String index
    );

    @FormUrlEncoded
    @POST("deleteThemeReview.php")
    Call<DTO_review> deleteThemeReview(
            @Field("theme_index") String theme_index,
            @Field("review_index") String review_index
    );

    @FormUrlEncoded
    @POST("deleteCafe.php")
    Call<DTO_shop> deleteCafe(
            @Field("cafe_index") String cafe_index
    );

    @FormUrlEncoded
    @POST("deleteRoomOut.php")
    Call<DTO_room> deleteRoomOut(
            @Field("room_id") String room_id,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("deleteKick.php")
    Call<DTO_message> deleteKick(
            @Field("room_id") String room_id,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("deleteTheme.php")
    Call<DTO_theme> deleteTheme(
            @Field("theme_index") String theme_index
    );

    @FormUrlEncoded
    @POST("writeCafeLike.php")
    Call<DTO_shop> setLike(
            @Field("cafe_index") String cafe_index,
            @Field("user_index") String user_index,
            @Field("isChecked") int isChecked
    );

    @FormUrlEncoded
    @POST("writeThemeLike.php")
    Call<DTO_shop> setThemeLike(
            @Field("theme_index") String theme_index,
            @Field("user_index") String user_index,
            @Field("isChecked") int isChecked
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

    @Multipart
    @POST("writeImageMessage.php")
    Call<ArrayList<DTO_message>> writerImageMessage(
      @Part ArrayList<MultipartBody.Part> files
    );

    @FormUrlEncoded
    @POST("getUserProfile.php")
    Call<User> getUserProfile(
            @Field("userIndex") Integer userIndex,
            @Field("user_id") String user_id
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
    @POST("updateGalleryBoard.php")
    Call<DTO_gallery> updateGalleryBoard(
            @Part("board_number") RequestBody board_number,
            @Part("cafe") RequestBody cafe,
            @Part("theme") RequestBody theme,
            @Part("content") RequestBody content,
            @Part("writer") RequestBody writer,
            @Part ArrayList<MultipartBody.Part> files
    );

    @FormUrlEncoded
    @POST("updateGalleryComment.php")
    Call<DTO_comment> updateGalleryComment(
            @Field("writer") String writer,
            @Field("content") String content,
            @Field("index") String index
    );

    @FormUrlEncoded
    @POST("updateGalleryReply.php")
    Call<DTO_comment> updateGalleryReply(
            @Field("content") String content,
            @Field("index") String index
    );

    @Multipart
    @POST("writeFreeboard.php")
    Call<DTO_board> writeFreeBoard(
            @Part("writer") RequestBody writer,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("date") RequestBody date,
            @Part("imgNum") int imgNum,
            @Part ArrayList<MultipartBody.Part> files
    );

    @Multipart
    @POST("writeChatBoard.php")
    Call<DTO_room> writeChatRoom(
            @Part("name") RequestBody name,
            @Part("writer") RequestBody writer,
            @Part("max") RequestBody max,
            @Part("info") RequestBody info,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("writerTheme.php")
    Call<DTO_theme> writeTheme(
            @Part ArrayList<MultipartBody.Part> files,
            @Part("name") RequestBody name,
            @Part("diff") RequestBody diff,
            @Part("limit") RequestBody limit,
            @Part("genre") RequestBody genre,
            @Part("info") RequestBody info,
            @Part("cafe") RequestBody cafe,
            @Part("index") RequestBody index,
            @Part("user_index") RequestBody user_index
    );

    @Multipart
    @POST("writeCafe.php")
    Call<DTO_shop> writeCafe(
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("more") RequestBody address_more,
            @Part("info") RequestBody info,
            @Part("open") RequestBody open,
            @Part("close") RequestBody close,
            @Part("holiday") RequestBody holiday,
            @Part("writer") RequestBody writer,
            @Part ArrayList<MultipartBody.Part> files
    );

    @Multipart
    @POST("updateCafe.php")
    Call<DTO_shop> updateCafe(
            @Part("name") RequestBody name,
            @Part("address") RequestBody address,
            @Part("more") RequestBody address_more,
            @Part("info") RequestBody info,
            @Part("open") RequestBody open,
            @Part("close") RequestBody close,
            @Part("holiday") RequestBody holiday,
            @Part("cafe_index") RequestBody cafe_index,
            @Part ArrayList<MultipartBody.Part> files
    );

    @Multipart
    @POST("updateTheme.php")
    Call<DTO_theme> updateTheme(
            @Part("name") RequestBody name,
            @Part("diff") RequestBody diff,
            @Part("limit") RequestBody limit,
            @Part("genre") RequestBody genre,
            @Part("info") RequestBody info,
            @Part("index") RequestBody index,
            @Part ArrayList<MultipartBody.Part> files
    );

    @Multipart
    @POST("writeGalleryBoard.php")
    Call<DTO_gallery> writeGalleryBoard(
            @Part("cafe") RequestBody cafe,
            @Part("theme") RequestBody theme,
            @Part("writer") RequestBody writer,
            @Part("content") RequestBody content,
            @Part ArrayList<MultipartBody.Part> files
    );

    @FormUrlEncoded
    @POST("writeHistory.php")
    Call<DTO_history> writeHistory(
            @Field("index") String index,
            @Field("contents") String contents
    );


    @FormUrlEncoded
    @POST("writeFreeReply.php")
    Call<DTO_free_reply> writeFreeReply(
            @Field("reply_writer") String reply_writer,
            @Field("reply_content") String reply_content,
            @Field("reply_date") String reply_date,
            @Field("board_number") String board_number
    );

    @FormUrlEncoded
    @POST("writerGalleryComment.php")
    Call<DTO_comment> writerGalleryComment(
            @Field("writer") String writer,
            @Field("content") String content,
            @Field("board_number") String board_number
    );

    @FormUrlEncoded
    @POST("writerGalleryReply.php")
    Call<DTO_comment> writeGalleryReply(
            @Field("index") String index,
            @Field("writer") String writer,
            @Field("content") String content,
            @Field("board_number") String board_number
    );

    @GET("getFreeBoard.php")
    Call<ArrayList<DTO_board>> getFreeBoard(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @FormUrlEncoded
    @POST("getGalleryBoard.php")
    Call<ArrayList<DTO_gallery>> getGalleryBoard(
            @Field("page") int page,
            @Field("limit") int limit,
            @Field("writer") String writer
    );


    @GET("getFreeBoard.php")
    Call<ArrayList<DTO_board>> getFreeSearch(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("category") String category,
            @Query("search_con") String search_con
    );

    @FormUrlEncoded
    @POST("getGalleryBoard.php")
    Call<ArrayList<DTO_gallery>> getGallerySearch(
            @Field("page") int page,
            @Field("limit") int limit,
            @Field("category") String category,
            @Field("search_con") String search_con,
            @Field("writer") String user_id
    );

    @FormUrlEncoded
    @POST("getGalleryRead.php")
    Call<DTO_gallery> getGalleryRead(
            @Field("page") int page,
            @Field("user_id") String user_id,
            @Field("board_number") String board_number
    );

    @FormUrlEncoded
    @POST("getGalleryScroll.php")
    Call<DTO_gallery> getGalleryScroll(
            @Field("page") int page,
            @Field("board_number") String board_number,
            @Field("user_id") String user_id
    );


    @FormUrlEncoded
    @POST("getFreeRead.php")
    Call<DTO_board> getFreeScroll(
            @Field("page") int page,
            @Field("board_num") String board_num
    );

    @FormUrlEncoded
    @POST("writeGalleryLike.php")
    Call<DTO_gallery> writeGalleryLike(
            @Field("user_id") String user_id,
            @Field("board_number") String board_number
    );

    @FormUrlEncoded
    @POST("getFreeRead.php")
    Call<DTO_board> getFreeRead(
            @Field("board_num") String board_num,
            @Field("writer") String writer
    );

    @FormUrlEncoded
    @POST("deleteFreeRead.php")
    Call<DTO_board> deleteFreeBoard(
            @Field("board_number") String board_number,
            @Field("writer") String writer
    );

    @FormUrlEncoded
    @POST("deleteGalleryRead.php")
    Call<DTO_gallery> deleteGallery(
            @Field("board_number") String board_number,
            @Field("writer") String writer
    );

    @FormUrlEncoded
    @POST("deleteGalleryComment.php")
    Call<DTO_comment> deleteGalleryComment(
            @Field("board_number") String board_number,
            @Field("index") String index
    );

    @FormUrlEncoded
    @POST("deleteGalleryReply.php")
    Call<DTO_comment> deleteGalleryReply(
            @Field("board_number") String board_number,
            @Field("index") String index
    );

    @FormUrlEncoded
    @POST("deleteFreeReply.php")
    Call<DTO_free_reply> deleteFreeReply(
            @Field("reply_index") String reply_index
    );

    @FormUrlEncoded
    @POST("deleteGalleryLike.php")
    Call<DTO_gallery> deleteGalleryLike(
            @Field("user_id") String user_id,
            @Field("board_number") String board_number
    );

    @FormUrlEncoded
    @POST("deleteFreeComment.php")
    Call<DTO_free_reply> deleteFreeComment(
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
    @POST("updateFreeComment.php")
    Call<DTO_free_reply> updateFreeComment(
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
    @POST("getFreeComment.php")
    Call<ArrayList<DTO_free_reply>> getFreeCommentScroll(
            @Field("reply_index") String reply_index,
            @Field("page") int page
    );

    @FormUrlEncoded
    @POST("getGalleryReply.php")
    Call<ArrayList<DTO_comment>> getGalleryReplyScroll(
            @Field("page") int page,
            @Field("index") String index
    );


    @FormUrlEncoded
    @POST("writeFreeComment.php")
    Call<DTO_free_reply> writeFreeComment(
            @Field("reply_index") String reply_index,
            @Field("reply_writer") String reply_writer,
            @Field("reply_content") String reply_content,
            @Field("reply_date") String reply_date
    );

    @FormUrlEncoded
    @POST("getGalleryReply.php")
    Call<ArrayList<DTO_comment>> getGalleryReply(
            @Field("index") String index
    );

    @FormUrlEncoded
    @POST("getHistory.php")
    Call<ArrayList<DTO_history>> getHistory(
            @Field("index") String index
    );

    @FormUrlEncoded
    @POST("getSearchCafe.php")
    Call<ArrayList<DTO_shop>> getSearchCafe(
            @Field("page") int page,
            @Field("size") int size,
            @Field("search") String search
    );

    @FormUrlEncoded
    @POST("getMyCafeLike.php")
    Call<ArrayList<DTO_shop>> getMyLikeCafe(
            @Field("page") int page,
            @Field("size") int size,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("getMyThemeLike.php")
    Call<ArrayList<DTO_theme>> getMyLikeTheme(
            @Field("page") int page,
            @Field("size") int size,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("getSearchTheme.php")
    Call<ArrayList<DTO_theme>> getSearchTheme(
            @Field("page") int page,
            @Field("size") int size,
            @Field("search") String search
    );

    @FormUrlEncoded
    @POST("getMyTheme.php")
    Call<ArrayList<DTO_theme>> getMyTheme(
            @Field("page") int page,
            @Field("size") int size,
            @Field("user_index") String user_index
    );

    @FormUrlEncoded
    @POST("getMyCafe.php")
    Call<ArrayList<DTO_shop>> getMyCafe(
            @Field("page") int page,
            @Field("size") int size,
            @Field("user_index") String user_index
    );

    @FormUrlEncoded
    @POST("getChatRoom.php")
    Call<ArrayList<DTO_room>> getChatRoom(
            @Field("page") int page,
            @Field("size") int size,
            @Field("category") String category,
            @Field("search") String search
    );

    @FormUrlEncoded
    @POST("getRoomJoin.php")
    Call<DTO_room> getRoomJoin(
            @Field("room_id") String room_id
    );

    @FormUrlEncoded
    @POST("getRefreshJoin.php")
    Call<ArrayList<DTO_message>> getRefreshJoin(
            @Field("room_id") String room_id,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("getMyChat.php")
    Call<ArrayList<DTO_room>> getMyChat(
            @Field("page") int page,
            @Field("size") int size,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("writeSingleMsg.php")
    Call<DTO_room> writeSingleMsg(
            @Field("user_id") String user_id,
            @Field("follow_id") String follow_id
    );

    @FormUrlEncoded
    @POST("getMy.php")
    Call<DTO_message> getMy(
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("writerJoin.php")
    Call<DTO_room> writerJoin(
            @Field("user_id") String user_id,
            @Field("room_id") String room_id
    );

    @FormUrlEncoded
    @POST("getMessage.php")
    Call<ArrayList<DTO_message>> getMessage(
            @Field("room_id") String room_id,
            @Field("page") int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("getRoomData.php")
    Call<DTO_chat> getRoomData(
      @Field("room_id") String room_id,
      @Field("page") int page,
      @Field("size") int size,
      @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("getRoomScroll.php")
    Call<ArrayList<DTO_message>> getRoomScroll(
            @Field("room_id") String room_id,
            @Field("page") int page,
            @Field("size") int size,
            @Field("last") String last,
            @Field("last_msg") int last_msg
    );

    @FormUrlEncoded
    @POST("getMember.php")
    Call<ArrayList<DTO_message>> getMember(
            @Field("room_id") String room_id,
            @Field("size") int size
    );


    @FormUrlEncoded
    @POST("deleteRoomAll.php")
    Call<DTO_room> deleteAll(
            @Field("room_id") String room_id
    );

    @FormUrlEncoded
    @POST("getInvite.php")
    Call<DTO_follow> getInvite(
            @Field("room_id") String room_id,
            @Field("user_id") String user_Id,
            @Field("page") int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("getFollowRequest.php")
    Call<DTO_follow> getFollowRequest(
            @Field("user_id") String user_id,
            @Field("page") int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("updateFollower.php")
    Call<DTO_follow> updateFollow(
            @Field("user_id") String user_id,
            @Field("follow_id") String follow_id
    );

    @FormUrlEncoded
    @POST("getFollower.php")
    Call<DTO_follow> getFollower(
            @Field("user_id") String user_id,
            @Field("page")  int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("getFollowing.php")
    Call<DTO_follow> getFollowing(
            @Field("user_id") String user_id,
            @Field("page") int page,
            @Field("size") int size
    );

    @FormUrlEncoded
    @POST("getFriendProfile.php")
    Call<DTO_follow> getFriendProfile(
        @Field("user_index") String user_index,
        @Field("user_id") String user_id,
        @Field("reader") String reader
    );

    @FormUrlEncoded
    @POST("getFriendData.php")
    Call<DTO_room> getfriendData(
            @Field("user_id") String user_id,
            @Field("follow_id") String follow_id
    );
}
