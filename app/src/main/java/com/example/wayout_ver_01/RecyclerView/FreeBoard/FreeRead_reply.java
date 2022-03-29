package com.example.wayout_ver_01.RecyclerView.FreeBoard;

public class FreeRead_reply {

    private String writer_reply;
    private String content_reply;
    private String date_reply;
    private String number_reply;
    private String reply_index;
    private int answer_reply;
    private String board_number;

    public FreeRead_reply(String writer_reply, String content_reply, String date_reply, String number_reply,  String reply_index, int answer_reply) {
        this.writer_reply = writer_reply;
        this.content_reply = content_reply;
        this.date_reply = date_reply;
        this.number_reply = number_reply;
        this.answer_reply = answer_reply;
        this.reply_index = reply_index;
    }

    public FreeRead_reply(String writer_reply, String content_reply, String date_reply, String reply_index, String board_number) {
        this.writer_reply = writer_reply;
        this.content_reply = content_reply;
        this.date_reply = date_reply;
        this.board_number = board_number;
        this.reply_index = reply_index;
    }

    public FreeRead_reply(String writer_reply, String content_reply, String date_reply, String reply_index){
        this.writer_reply = writer_reply;
        this.content_reply = content_reply;
        this.date_reply = date_reply;
        this.reply_index = reply_index;
    }

    public String getBoard_number() {
        return board_number;
    }

    public void setBoard_number(String board_number) {
        this.board_number = board_number;
    }

    public String getReply_index() {
        return reply_index;
    }

    public void setReply_index(String reply_index) {
        this.reply_index = reply_index;
    }

    public int getAnswer_reply() {
        return answer_reply;
    }

    public void setAnswer_reply(int answer_reply) {
        this.answer_reply = answer_reply;
    }

    public String getWriter_reply() {
        return writer_reply;
    }

    public void setWriter_reply(String writer_reply) {
        this.writer_reply = writer_reply;
    }

    public String getContent_reply() {
        return content_reply;
    }

    public void setContent_reply(String content_reply) {
        this.content_reply = content_reply;
    }

    public String getDate_reply() {
        return date_reply;
    }

    public void setDate_reply(String date_reply) {
        this.date_reply = date_reply;
    }

    public String getNumber_reply() {
        return number_reply;
    }

    public void setNumber_reply(String number_reply) {
        this.number_reply = number_reply;
    }


}
