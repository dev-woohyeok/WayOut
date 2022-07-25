package com.example.wayout_ver_01.Activity.Chat.Chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.Chat.Friend_profile;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Follow_adapter extends RecyclerView.Adapter<Follow_adapter.VH> {
    ArrayList<DTO_follow> items = new ArrayList<>();
    Context context;
    TextView textView;
    boolean friend;

    public Follow_adapter(Context context) {
        this.context = context;
    }

    public Follow_adapter(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    public void addItem(DTO_follow item){
        items.add(item);
        notifyItemInserted(getItemCount());
    }

    public void setfriend(boolean friend){
        this.friend = friend;
    }

    public void itemsClear(){
        int size = getItemCount();
        items.clear();
        notifyItemRangeRemoved(0,size);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow,parent,false);
        return new VH(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if(items.size() > 0){
            textView.setVisibility(View.GONE);
        }else {
            textView.setVisibility(View.VISIBLE);
        }

        DTO_follow item = items.get(position);
        holder.item_yes.setVisibility(View.GONE);
        Glide.with(context).load(item.getUser_image()).circleCrop().into(holder.item_image);
        holder.item_name.setText(item.getUser_id());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView item_image;
        TextView item_name, item_yes, item_no;

        public VH(@NonNull View itemView, Follow_adapter adapter) {
            super(itemView);

            item_image = itemView.findViewById(R.id.item_follow_image);
            item_name = itemView.findViewById(R.id.item_follow_name);
            item_yes = itemView.findViewById(R.id.item_follow_ok);
            item_no = itemView.findViewById(R.id.item_follow_delete);

            if(adapter.friend){
                item_no.setVisibility(View.GONE);
            }else{
                item_no.setVisibility(View.VISIBLE);
            }

            item_no.setOnClickListener(v -> {
                deleteFollow(adapter);
                adapter.items.remove(getBindingAdapterPosition());
                adapter.notifyItemRemoved(getBindingAdapterPosition());
            });

            itemView.setOnClickListener(v -> {
                DTO_follow item = adapter.items.get(getBindingAdapterPosition());
                Intent intent = new Intent(itemView.getContext(), Friend_profile.class);
                intent.putExtra("user_id",item.getUser_id());
                intent.putExtra("user_image", item.getUser_image());
                intent.putExtra("user_index", item.getUser_index());
                itemView.getContext().startActivity(intent);
            });

        }

        private void deleteFollow(Follow_adapter adapter) {
            DTO_follow item = adapter.items.get(getBindingAdapterPosition());
            String user_id = PreferenceManager.getString(adapter.context, "userId");
            String follower = item.getUser_id();

            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_message> call = retrofitInterface.deleteFollow(follower,user_id);
            call.enqueue(new Callback<DTO_message>() {
                @Override
                public void onResponse(Call<DTO_message> call, Response<DTO_message> response) {
                    if (response.isSuccessful() && response.body() != null) {
                    }
                }

                @Override
                public void onFailure(Call<DTO_message> call, Throwable t) {
                    Log.e("//===========//", "================================================");
                    Log.e("", "\n" + "[ Drawer_adapter _ update 에러 : " + t + "  ]");
                    Log.e("//===========//", "================================================");
                }
            });

        }
    }
}
