package com.example.wayout_ver_01.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.Activity.FreeBoard_read;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.R;

import java.text.ParseException;
import java.util.ArrayList;

public class FreeBoard_adapter extends RecyclerView.Adapter<FreeBoard_adapter.CustomViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    ArrayList<FreeBoard> List_items = new ArrayList<FreeBoard>();
    Context context;


    public  FreeBoard_adapter (Context context) {
        this.context = context;
    }

    // item CRUD
    public void add_item(FreeBoard item ){
        List_items.add(item);
    }

    public FreeBoard get_item(int pos) {
        return List_items.get(pos);
    }

    public void edit_item(int pos, FreeBoard item){
        List_items.set(pos, item);
    }

    public void remove_item(int pos){
        List_items.remove(pos);
    }

    public ArrayList<FreeBoard> get_list() {
        return this.List_items;
    }

    public void clear_list() {
        this.List_items.clear();
    }




    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_freeboard, parent, false);
//        Log.e(TAG, "내용 : onCreateViewHolder ");
        return new CustomViewHolder(this, view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        // Item 초기 설정 세팅 하는 곳
//        Log.e(TAG, "내용 : onBindViewHolder");
        // ItemView 재활용한 ViewHolder 에 데이터 넣는 곳
        FreeBoard item = List_items.get(position);
            holder.free_title.setText(item.getTitle());
            holder.free_writer.setText(item.getWriter());

            holder.free_boardNum.setText(item.getBoard_num());
            if(item.getReply() > 0)
            {
                holder.free_reply.setText("댓글 " + item.getReply());
            }
            else
            {
                holder.freeBoard_reply.setVisibility(View.INVISIBLE);
                holder.free_reply.setVisibility(View.INVISIBLE);
            }

        try {
            String date = DateConverter.resultDateToString(item.getDate(),"MM월 dd일 a h:mm");
            holder.free_date.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        // 아이탬 갯수 리턴
//        Log.e(TAG, "내용 : getItemCount");
        return List_items.size();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        // item 의 Find View ID 하면됨
        TextView free_title, free_content, free_writer, free_date ,free_reply, free_boardNum ;
        ImageView free_profile,freeBoard_reply;

        public CustomViewHolder (FreeBoard_adapter freeBoard_adapter, @NonNull View itemView , Context context) {
            super(itemView);
            // View 의 Click event 같은건 여기서 처리
            free_title = itemView.findViewById(R.id.free_title);
            free_date = itemView.findViewById(R.id.free_date);
            free_writer = itemView.findViewById(R.id.free_writer);
            free_reply = itemView.findViewById(R.id.free_reply);
            free_boardNum = itemView.findViewById(R.id.free_boardNum);
            freeBoard_reply = itemView.findViewById(R.id.freeBoard_reply);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    FreeBoard item = freeBoard_adapter.List_items.get(pos);
                    String boardNum = item.getBoard_num();

                    // board 번호에 해당하는 게시글, 이미지, 댓글 불러오기
                    Intent intent = new Intent(v.getContext() , FreeBoard_read.class);
                    intent.putExtra("board_num", boardNum);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
