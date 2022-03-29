package com.example.wayout_ver_01.RecyclerView.FreeBoard;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.Activity.FreeBoard.FreeBoard_read;
import com.example.wayout_ver_01.Activity.FreeBoard.FreeBoard_reply;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_free_reply;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeRead_reply_adapter extends RecyclerView.Adapter<FreeRead_reply_adapter.viewHolder> {
    ArrayList<FreeRead_reply> items = new ArrayList<>();
    NestedScrollView sc;
    Context context;
    TextView reply_num;
    EditText reply_content;
    boolean updateMode;
    public final String TAG = this.getClass().getSimpleName();
    String replyIndex;
    int itemPos;
    int total;

    public FreeRead_reply_adapter(Context context, TextView reply_num, EditText reply_content) {
        this.context = context;
        this.reply_num = reply_num;
        this.reply_content = reply_content;
    }

    public String getReplyIndex () {
        return replyIndex;
    }

    public void updateItem () {
    }

    public void setTotal(int total){
        this.total = total;
    }

    public void replyUpdate(int pos, String content) {
        items.get(pos).setContent_reply(content);
        notifyItemChanged(pos);
    }

    public void add_item (FreeRead_reply item)
    {
        items.add(item);
    }

    public void upload_item (FreeRead_reply item){
        items.add(0,item);
    }

    public ArrayList<FreeRead_reply> getItems () {
        return items;
    }

    public boolean getMode() { return  updateMode;}
    public void setMode() {updateMode = true;}
    public void endMode() {updateMode = false;}


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.item_free_read_reply,parent,false);

        return new viewHolder(item, this, context);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FreeRead_reply item = items.get(position);
        holder.reply_writer.setText(item.getWriter_reply());
        holder.reply_content.setText(item.getContent_reply());
        try { holder.reply_date.setText(DateConverter.resultDateToString(item.getDate_reply(),"M월 d일 a h:mm"));}
        catch (ParseException e) { e.printStackTrace();}
        // 답글 갯수
        if(item.getAnswer_reply() > 0) {
            holder.reply_ToReply.setText("답글 " + item.getAnswer_reply()); }
        else {
            holder.reply_ToReply.setText("답글 달기"); }
        // 본인 수정 설정
        if(!item.getWriter_reply().equals(PreferenceManager.getString(context,"autoId"))) {
            holder.reply_menu.setVisibility(View.INVISIBLE); }

    }
    public int getItemPos() {
        return itemPos;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView reply_writer,reply_content,reply_date,reply_ToReply;
        ImageView reply_menu;

        public viewHolder(@NonNull View itemView , FreeRead_reply_adapter adapter, Context context) {
            super(itemView);
            reply_writer = itemView.findViewById(R.id.freeRead_reply_writer);
            reply_content = itemView.findViewById(R.id.freeRead_reply_content);
            reply_date = itemView.findViewById(R.id.freeRead_reply_date);
            reply_ToReply = itemView.findViewById(R.id.freeRead_ToReply);
            reply_menu = itemView.findViewById(R.id.freeRead_reply_menu);

            reply_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 댓글 수정 삭제 기능 ( 클릭한 댓글의 index )
                    setMenu(adapter.items.get(getAdapterPosition()).getReply_index(), adapter);
                }
            });

            // 답글 작성
            reply_ToReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 데이터 넘기기
                    Intent intent = new Intent(v.getContext(), FreeBoard_reply.class);
                    intent.putExtra("reply_index", adapter.items.get(getAdapterPosition()).getReply_index());
                    intent.putExtra("board_number", adapter.items.get(getAdapterPosition()).getNumber_reply());
                    intent.putExtra("reply_writer", adapter.items.get(getAdapterPosition()).getWriter_reply());
                    intent.putExtra("reply_content", adapter.items.get(getAdapterPosition()).getContent_reply());
                    intent.putExtra("reply_date", adapter.items.get(getAdapterPosition()).getDate_reply());
                    ((FreeBoard_read)context).finish();
                    v.getContext().startActivity(intent);

                }
            });
        }

        public void setMenu(String index, FreeRead_reply_adapter adapter) {
            final List<String> ListItems = new ArrayList<>();
            ListItems.add("댓글 삭제");
            ListItems.add("댓글 수정");

            // String 배열 Items 에 ListItems 를 string[ListItems.size()] 형태로 생성한다.
            final String[] items = ListItems.toArray(new String[ListItems.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setItems(items, (dialog, pos) -> {
                String selectedText = items[pos];
                switch (selectedText) {
                    case "댓글 삭제":
                        delete_reply(index, adapter);
                        break;
                    case "댓글 수정":
                        update_reply(index, adapter);
                        break;
                }
            });
            builder.show();
        }

        public void update_reply(String index, FreeRead_reply_adapter adapter) {
            adapter.setMode();

            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_free_reply> call = retrofitInterface.getFreeReply(index);
            call.enqueue(new Callback<DTO_free_reply>() {
                @Override
                public void onResponse(Call<DTO_free_reply> call, Response<DTO_free_reply> response) {
                    if ( response.isSuccessful() && response.body() != null ) {
                        adapter.reply_content.setText(response.body().getReply_content());
                        adapter.replyIndex = index;
                        adapter.itemPos = getAdapterPosition();
                    }
                }

                @Override
                public void onFailure(Call<DTO_free_reply> call, Throwable t) {

                }
            });


        }

        public void delete_reply(String index, FreeRead_reply_adapter adapter){
            adapter.items.remove(getAdapterPosition());
            adapter.notifyItemRemoved(getAdapterPosition());


            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_free_reply> call = retrofitInterface.deleteFreeReply(index);
            call.enqueue(new Callback<DTO_free_reply>() {
                @Override
                public void onResponse(Call<DTO_free_reply> call, Response<DTO_free_reply> response) {
                    if(response.isSuccessful()&& response.body() != null){
                        Log.e("댓글 삭제 성공", "내용 : " + response.body().getMessage());
                        adapter.reply_num.setText("댓글 " + response+ "개");
                    }
                }

                @Override
                public void onFailure(Call<DTO_free_reply> call, Throwable t) {
                        Log.e("댓글 삭제 실패", "내용 : " + t);
                }
            });

        }
    }
}
