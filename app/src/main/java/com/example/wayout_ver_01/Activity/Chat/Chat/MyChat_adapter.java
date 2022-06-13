package com.example.wayout_ver_01.Activity.Chat.Chat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.Chat.ChatRoom;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.text.ParseException;
import java.util.ArrayList;

public class MyChat_adapter extends RecyclerView.Adapter<MyChat_adapter.viewHolder> {
    Context context;
    ArrayList<DTO_room> items = new ArrayList<>();
    ArrayList<DTO_room> temp = new ArrayList<>();
    private ActivityResultLauncher<Intent> resultLauncher;

    public MyChat_adapter(Context context) {
        this.context = context;
    }

    /* onClickListener Custom  */
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }
    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }
    /* onClickListener Custom  */

    public DTO_room getItem(int pos){
        return items.get(pos);
    }

    public void addItem(DTO_room item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void setList(ArrayList<DTO_room> list){
        this.items = list;
        notifyItemRangeChanged(0,list.size());
    }

    public void itemsClear() {
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

    public ArrayList<DTO_room> getItems() {
        return items;
    }

    public void removeItem(int pos){
        this.items.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addToFirst(DTO_room item){
        items.add(0,item);
        notifyItemInserted(0);
    }

    public void setCount(int pos, int count){
        this.items.get(pos).setRoom_count(count);
        notifyItemChanged(pos);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mychat, parent, false);
        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_room item = items.get(position);

        holder.item_title.setText(item.getRoom_name());
        holder.item_join.setText(item.getJoin_number() + "");
        holder.item_message.setText(item.getRoom_message());
        if(item.getType().equals("img")){
            holder.item_message.setText("이미지가 전송 되었습니다.");
        }

        if (item.getRoom_count() > 0) {
            holder.item_message_count.setVisibility(View.VISIBLE);
            holder.item_message_count.setText("" + item.getRoom_count());
        }else{
            holder.item_message_count.setVisibility(View.INVISIBLE);
        }
        try {
            holder.item_date.setText(DateConverter.resultDateToString(item.getRoom_date(), "M월 dd일"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Glide.with(context).load(item.getRoom_image()).circleCrop().into(holder.item_image);

        Log.e("//===========//","================================================");
        Log.e("","\n"+"[ myChat count : "+item.getRoom_count()+"  ]");
        Log.e("","\n"+"[ myChat last_id : "+item.getLast_id()+"  ]");
        Log.e("","\n"+"[ myChat position : "+position+"  ]");
        Log.e("//===========//","================================================");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView item_title, item_date, item_message, item_join, item_message_count;
        ImageView item_image;

        public viewHolder(@NonNull View itemView, MyChat_adapter myChat_adapter) {
            super(itemView);

            item_title = itemView.findViewById(R.id.item_MyChat_title);
            item_date = itemView.findViewById(R.id.item_MyChat_date);
            item_message = itemView.findViewById(R.id.item_MyChat_messasge);
            item_join = itemView.findViewById(R.id.item_MyChat_join);
            item_message_count = itemView.findViewById(R.id.item_MyChat_message_number);
            item_image = itemView.findViewById(R.id.item_MyChat_image);

            itemView.setOnClickListener(v ->  {
                    /* 채팅방 들어갈때 */
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        DTO_room item = myChat_adapter.getItems().get(pos);
                        Intent intent = new Intent(itemView.getContext(), ChatRoom.class);
                        intent.putExtra("room_id", item.getRoom_id());
                        myChat_adapter.getItems().get(pos).setRoom_count(0);
                        myChat_adapter.notifyItemChanged(pos);
                        itemView.getContext().startActivity(intent);
                    }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        /* 선택 메뉴 만들기 */
                        Dialog dialog = new Dialog(itemView.getContext());
                        dialog.setContentView(R.layout.item_dialog_menu_cafe);
                        DialogShow(dialog);
                    }
                    /* 다음 이벤트 계속 진행 false, 이벤트 완료 true */
                    return true;
                }
            });

        }

        private void DialogShow(Dialog dialog) {
            dialog.show();
            Context context = itemView.getContext();
            TextView menu_add_theme, menu_update, menu_delete;
            menu_add_theme = dialog.findViewById(R.id.item_dialog_add_theme);
            menu_update = dialog.findViewById(R.id.item_dialog_update_cafe);
            menu_delete = dialog.findViewById(R.id.item_dialog_delete_cafe);

            menu_add_theme.setVisibility(View.GONE);
            menu_delete.setText("채팅방 나가기");
            menu_update.setVisibility(View.GONE);

            menu_delete.setOnClickListener(v -> {
                Dialog dialog2 = new Dialog(context);
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.item_dialog_check);
                showDeleteDialog(dialog2);
                dialog.dismiss();
            });

        }

        private void showDeleteDialog(Dialog dialog) {
            dialog.show();

            TextView title = dialog.findViewById(R.id.item_dialog_yn_title);
            TextView content = dialog.findViewById(R.id.item_dialog_yn_content);
            TextView yes = dialog.findViewById(R.id.item_dialog_yn_yes);
            TextView no = dialog.findViewById(R.id.item_dialog_yn_no);
            title.setText("채팅방 나가기");
            content.setText(" 채팅방을 나가시겠습니까??\n\n채팅방을 나갈시 대화내용이 모두 삭제되고 \n 채팅목록에서도 삭제됩니다. ");
            yes.setText("나가기");

            // 테마 삭제
            yes.setOnClickListener(v -> {
                dialog.dismiss();
                RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
//                Call<DTO_room> call = retrofitInterface.deleteCafe();

            });

            // 아무것도 아니죠
            no.setOnClickListener(v -> {
                dialog.dismiss();
            });
        }
    }
}
