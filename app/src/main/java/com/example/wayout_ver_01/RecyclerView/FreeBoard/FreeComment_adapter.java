package com.example.wayout_ver_01.RecyclerView.FreeBoard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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

public class FreeComment_adapter extends RecyclerView.Adapter<FreeComment_adapter.viewHolder> {
    Context context;
    ArrayList<FreeRead_reply> items = new ArrayList<>();
    EditText Content;
    int itemPos;
    String comment_index;
    InputMethodManager imm;


    boolean setMode;
    public FreeComment_adapter(Context context, EditText Content, InputMethodManager imm) {
        this.context = context;
        this.Content = Content;
        this.imm = imm;
    }

    public void addItem(FreeRead_reply item) {
        items.add(item);
    }

    public void setItem(int pos , String content) {
        items.get(pos).setContent_reply(content);
        notifyItemChanged(pos);
    }

    public String getIndex(){
        return comment_index;
    }

    public int getItemPos (){
        return itemPos;
    }

    public void setMode(){
        setMode = true;
    }

    public void endMode(){
        setMode = false;
    }

    public boolean getMode(){
        return setMode;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.item_free_comment_reply, parent, false);
        return new viewHolder(item, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FreeRead_reply item = items.get(position);
        holder.freeComment_writer.setText(item.getWriter_reply());
        holder.freeComment_content.setText(item.getContent_reply());

        if(!holder.freeComment_writer.getText().toString().equals(PreferenceManager.getString(context,"autoId"))){
            holder.freeComment_menu.setVisibility(View.INVISIBLE);
        }

        try {
            holder.freeComment_date.setText(DateConverter.resultDateToString(item.getDate_reply(), "yyyy-MM-dd a h시 mm분"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView freeComment_writer, freeComment_content, freeComment_date;
        ImageView freeComment_menu;

        public viewHolder(@NonNull View itemView, FreeComment_adapter adapter) {
            super(itemView);

            freeComment_writer = itemView.findViewById(R.id.freeComment_writer);
            freeComment_content = itemView.findViewById(R.id.freeComment_content);
            freeComment_date = itemView.findViewById(R.id.freeComment_date);
            freeComment_menu = itemView.findViewById(R.id.freeComment_menu);

            freeComment_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 메뉴 클릭시
                    String index = adapter.items.get(getAdapterPosition()).getReply_index();
                    setMenu(index, adapter);
                }
            });
        }

        public void setMenu(String index, FreeComment_adapter adapter) {
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

        public void update_reply(String index, FreeComment_adapter adapter) {
            adapter.setMode();
            adapter.Content.setText(adapter.items.get(getAdapterPosition()).getContent_reply());
            adapter.itemPos = getAdapterPosition();
            adapter.comment_index = adapter.items.get(getAdapterPosition()).getReply_index();
            adapter.imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }

        public void delete_reply(String index, FreeComment_adapter adapter){
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_free_reply> call = retrofitInterface.deleteFreeComment(index);
            call.enqueue(new Callback<DTO_free_reply>() {
                @Override
                public void onResponse(Call<DTO_free_reply> call, Response<DTO_free_reply> response) {
                    if(response.isSuccessful() && response.body() != null ) {
                        adapter.items.remove(getAdapterPosition());
                        adapter.notifyItemRemoved(getAdapterPosition());
                    }
                    else
                    {
                        Toast.makeText(itemView.getContext(), "답글 삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DTO_free_reply> call, Throwable t) {
                     Toast.makeText(itemView.getContext(), "답글 삭제 오류 : " + t, Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

}
