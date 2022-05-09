package com.example.wayout_ver_01.RecyclerView.Gallery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wayout_ver_01.Activity.Gallery.GalleryBoard_read;
import com.example.wayout_ver_01.Activity.Gallery.GalleryBoard_reply;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_comment;
import com.example.wayout_ver_01.Retrofit.DTO_gallery;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryRead_comment_adapter extends RecyclerView.Adapter<GalleryRead_comment_adapter.viewHolder> {
    Context context;
    ArrayList<DTO_comment> items = new ArrayList<>();
    String index;
    int total_comment;
    boolean mode;
    TextView total_comment_tv;
    EditText update_content;
    int update_pos;
    InputMethodManager imm;

    public GalleryRead_comment_adapter(Context context, TextView total_comment_tv) {
        this.context = context;
        this.total_comment_tv = total_comment_tv;
    }
    public void setImm (InputMethodManager imm) {
        this.imm = imm;
    }

    public void setUpdate_content(EditText update_content){
        this.update_content = update_content;
    }

    public void total_comment(int total_comment){
        this.total_comment = total_comment;
    }
    public boolean getMode() {
        return mode;
    }

    public String getIndex(){
        return index;
    }
    public void endUpdate(){
        this.mode = false;
    }

    public void clearItems(){
        items.clear();
    }

    public void submit_item(DTO_comment item){
        items.add(0, item);
    }

    public int getPos () {
      return update_pos;
    };

    public void add_item(DTO_comment item){
        items.add(item);
    }

    public void change_item(int pos, DTO_comment item){
        items.set(pos, item);
        notifyItemChanged(pos);
    }

    public void updateItem(int pos, String content){
        items.get(pos).setContent(content);
        notifyItemChanged(pos);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View item = layoutInflater.inflate(R.layout.item_gallery_comment,parent,false);

        return new viewHolder(item,this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_comment item = items.get(position);
        index = item.getComment_index();
        holder.item_writer.setText("작성자 : "+ item.getWriter());
        holder.item_content.setText("내용 : " +item.getContent());
        if(item.getTotal_reply() > 0){
            holder.item_write.setText("답글 " + item.getTotal_reply());
        }else{
            holder.item_write.setText("답글 달기");
        }
        try {
            holder.item_date.setText(DateConverter.resultDateToString(item.getDate(),"M월 d일 a h:mm"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!(item.getWriter().equals(PreferenceManager.getString(context,"autoNick")))){
            holder.item_menu.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private TextView item_writer, item_content, item_date, item_reply_btn, item_more_tv, item_write;
        private ImageView item_menu, item_more_iv;
        private RecyclerView item_rv;
        private ConstraintLayout item_more;
        private GalleryRead_reply_adapter reply_adapter;
        private InputMethodManager imm;

        public viewHolder(@NonNull View itemView, GalleryRead_comment_adapter adapter) {
            super(itemView);
            item_write = itemView.findViewById(R.id.galleryComment_write_reply);
            reply_adapter = new GalleryRead_reply_adapter(itemView.getContext());
            item_writer = itemView.findViewById(R.id.galleryComment_writer);
            item_content = itemView.findViewById(R.id.galleryComment_content);
            item_date = itemView.findViewById(R.id.galleryComment_date);
//            item_reply_btn = itemView.findViewById(R.id.galleryComment_write_reply);
//            item_more_tv = itemView.findViewById(R.id.galleryComment_more_tv);
//            item_more_iv = itemView.findViewById(R.id.galleryComment_more_iv);
            item_menu = itemView.findViewById(R.id.galleryComment_menu);
//            item_rv = itemView.findViewById(R.id.galleryComment_reply_rv);
//            item_more = itemView.findViewById(R.id.galleryComment_more);



            item_write.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(itemView.getContext(), GalleryBoard_reply.class);
                        intent.putExtra("index", adapter.items.get(pos).getComment_index());
                        intent.putExtra("board_number", adapter.items.get(pos).getBoard_number());
                        intent.putExtra("writer", adapter.items.get(pos).getWriter());
                        intent.putExtra("content", adapter.items.get(pos).getContent());
                        intent.putExtra("date", adapter.items.get(pos).getDate());
                        v.getContext().startActivity(intent);

                        Log.e("대댓글 전송 " , "index : " + adapter.items.get(pos).getComment_index());
                    }
                }
            });

            item_menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position = getAdapterPosition();
                            if(position != RecyclerView.NO_POSITION){
                                final List<String> ListItems = new ArrayList<>();
                                ListItems.add("댓글 삭제");
                                ListItems.add("댓글 수정");

                                adapter.index = adapter.items.get(getAdapterPosition()).getComment_index();

                                // String 배열 Items 에 ListItems 를 string[ListItems.size()] 형태로 생성한다.
                                final String[] items = ListItems.toArray(new String[ListItems.size()]);
                                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                                builder.setItems(items, (dialog, pos) -> {
                                    String selectedText = items[pos];
                                    switch (selectedText) {
                                        case "댓글 삭제":
                                            delete_reply(adapter);
                                            break;
                                        case "댓글 수정":
                                            update_reply(adapter);
                                            break;
                                    }
                                });
                                builder.show();
                            }
                        }
                    });
        }

        private void delete_reply(GalleryRead_comment_adapter adapter){


            // 알림창
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(adapter.context);
            builder.setTitle("게시물 삭제");
            builder.setMessage("\n정말로 삭제하시겠습니까?\n");
            builder.setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 삭제 실행하기
                            DTO_comment item = adapter.items.get(getAdapterPosition());
                            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                            Call<DTO_comment> call = retrofitInterface.deleteGalleryComment(item.getBoard_number(), item.getComment_index());
                            call.enqueue(new Callback<DTO_comment>() {
                                @Override
                                public void onResponse(Call<DTO_comment> call, Response<DTO_comment> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        adapter.items.remove(getAdapterPosition());
                                        adapter.notifyItemRemoved(getAdapterPosition());
                                        adapter.total_comment = response.body().getTotal_comment();
                                        adapter.total_comment_tv.setText("댓글 " + adapter.total_comment);
                                    } else {
                                        Toast.makeText(itemView.getContext(), "답글 삭제 실패", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<DTO_comment> call, Throwable t) {
                                    Toast.makeText(itemView.getContext(), "답글 삭제 오류 : " + t, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
            // =====
            builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();



        }

        private void update_reply(GalleryRead_comment_adapter adapter){
            // 액티비티에서 수정할 수 있도록 boolean 체크
            // 수정하기 전 내용 가져와서 Et 에 뿌려주기
            // 수정할 아이탬 위치 알려주기
            // 수정할 댓글의 고유값 넣어주기
            // 수정 표시해주기 위해서 et 키보드 올리기
            // 커서를 제일 마지막으로 옮기기
            adapter.mode = true;
            adapter.update_content.setText(adapter.items.get(getAdapterPosition()).getContent());
            adapter.update_pos = getAdapterPosition();
            adapter.index = adapter.items.get(getAdapterPosition()).getComment_index();
            adapter.imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            adapter.update_content.requestFocus();
            adapter.update_content.setSelection(adapter.items.get(getAdapterPosition()).getContent().length());
        }
    }
}
