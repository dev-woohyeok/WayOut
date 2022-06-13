package com.example.wayout_ver_01.Class;

import android.util.Log;

import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_message;
import com.example.wayout_ver_01.Activity.Chat.Chat.DTO_room;
import com.google.gson.Gson;

import java.util.ArrayList;

public class JsonMaker {
    private String code;
    private String json;
    private DTO_message msg;
    private Gson gson;


    public JsonMaker(String code) {
        this.code = code;
    }

    public static String makeCode(String code,String user_id,String room){
        Gson gson = new Gson();
        DTO_message chat = new DTO_message(code,room,user_id,"0","0","0","0");
        String jsonStr = gson.toJson(chat);
        System.out.println("JsonMaker makeCode // 변환된 데이터 " + jsonStr);
        return jsonStr;
    }
    public static String makeArray(ArrayList<DTO_room> list){
        Gson gson = new Gson();
        ArrayList<DTO_room> array = new ArrayList<>();
        array = list;
        String jsonStr = gson.toJson(array);
        System.out.println("JsonMaker makeArray // 변환된 데이터 " + jsonStr);
        return jsonStr;
    };

    /* type 1.msg , 2.image, 3.IO */
    public static String makeJson(String code, String room, String name, String message, String image, String date, String type,String room_name) {
        /* 서버에 보낼 메세지 데이터 Json 생성 */
        Gson gson = new Gson();
        DTO_message chat = new DTO_message(code, room, name, message, image, date, type, room_name);
        String jsonStr = gson.toJson(chat);
        Log.e("//===========//","================================================");
        Log.e("","\n"+"[ JsonMaker makeJson // 변환된 데이터 " + jsonStr + " ]");
        Log.e("//===========//","================================================");
        return jsonStr;
    }

    /* DTO => JsonString 으로 어디든 보내기 쉽게 */
    public static String DtoToJson(DTO_message item) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(item);
        Log.e("//===========//","================================================");
        Log.e("","\n"+"[ JsonMaker makeJson // 변환된 데이터 " + jsonStr + " ]");
        Log.e("//===========//","================================================");
        return jsonStr;
    }


    // json String => DTO 로 변경해줌
    public static DTO_message makeDTO(String json) {
        Gson gson = new Gson();
        DTO_message message = gson.fromJson(json, DTO_message.class);
        Log.e("//===========//","================================================");
        Log.e("","\n"+"[ JsonMaker makeJson // MakeDTO ]");
        Log.e("//===========//","================================================");
        return message;
    }
}
