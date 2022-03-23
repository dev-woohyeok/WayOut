package com.example.wayout_ver_01.RecyclerView;

public class FreeBoard {

    private String title;
    private String profile;
    private String writer;
    private String content;
    private String date;
    private int reply;

    public String getBoard_num() {
        return board_num;
    }

    public void setBoard_num(String board_num) {
        this.board_num = board_num;
    }

    private String board_num;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getReply() {
        return reply;
    }

    public void setReply(int reply) {
        this.reply = reply;
    }

    public FreeBoard(String title, String writer, String date, String board_num, int reply) { //String reply,
        this.title = title;
        this.writer = writer;
        this.date = date;
        this.board_num = board_num;
        this.reply = reply;
    }


}
