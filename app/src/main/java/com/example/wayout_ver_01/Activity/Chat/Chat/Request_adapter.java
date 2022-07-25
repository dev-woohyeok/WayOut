package com.example.wayout_ver_01.Activity.Chat.Chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Request_adapter extends RecyclerView.Adapter<Request_adapter.viewHolder> {
    ArrayList<DTO_follow> items = new ArrayList<>();
    Context context;

    public Request_adapter(Context context) {
        this.context = context;
    }

    public void addItem(DTO_follow item) {
        items.add(item);
        notifyItemInserted(getItemCount());
    }


    public void itemsClear(){
        int size = getItemCount();
        items.clear();
        notifyItemRangeRemoved(0,size);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow, parent, false);
        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_follow item = items.get(position);
        Glide.with(context).load(item.getUser_image()).circleCrop().into(holder.item_image);
        holder.item_name.setText(item.getUser_id());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        ImageView item_image;
        TextView item_name, item_yes, item_no;

        public viewHolder(@NonNull View itemView, Request_adapter adapter) {
            super(itemView);

            item_image = itemView.findViewById(R.id.item_follow_image);
            item_name = itemView.findViewById(R.id.item_follow_name);
            item_yes = itemView.findViewById(R.id.item_follow_ok);
            item_no = itemView.findViewById(R.id.item_follow_delete);

            /* 승인시 */
            item_yes.setOnClickListener(v -> {
                DTO_follow item = adapter.items.get(getBindingAdapterPosition());
                String user_id = PreferenceManager.getString(adapter.context, "userId");
                String follower_id = item.getUser_id();

                RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                Call<DTO_follow> call = retrofitInterface.updateFollow(user_id, follower_id);
                call.enqueue(new Callback<DTO_follow>() {
                    @Override
                    public void onResponse(Call<DTO_follow> call, Response<DTO_follow> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            item_yes.setVisibility(View.GONE);
                            if(response.body().getFollow_state() == 0) {
                                item_no.setText("맞팔로우");
                                item_no.setBackgroundResource(R.drawable.category_check);
                            }else{
                                item_no.setText("팔로잉");
                                item_no.setBackgroundResource(R.drawable.category_check_end);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DTO_follow> call, Throwable t) {
                        Log.e("//===========//", "================================================");
                        Log.e("", "\n" + "[ Request_adpater , follower 팔로우 승인 에러 : " + t + "  ]");
                        Log.e("//===========//", "================================================");
                    }
                });
            });

            /* 거절시 */
            item_no.setOnClickListener(v -> {
                String str = item_no.getText().toString();
                /* 아이템 삭제시 */
                switch (str) {
                    case "삭제":
                        /* 삭제 */
                        deleteFollow(adapter);
                        adapter.items.remove(getBindingAdapterPosition());
                        adapter.notifyItemRemoved(getBindingAdapterPosition());
                        break;
                    case "맞팔로우":
                        item_no.setBackgroundResource(R.drawable.category_check_end);
                        item_no.setText("팔로잉");
                        toFollow(adapter);
                        break;
                    case "팔로잉":
                        item_no.setBackgroundResource(R.drawable.category_check);
                        item_no.setText("맞팔로우");
                        deleteFollow2(adapter);
                        break;
                }
            });
        }

        private void deleteFollow2(Request_adapter adapter) {
            DTO_follow item = adapter.items.get(getBindingAdapterPosition());
            String user_id = PreferenceManager.getString(adapter.context, "userId");
            String follower = item.getUser_id();

            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_message> call = retrofitInterface.deleteFollow(user_id, follower);
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

        private void toFollow(Request_adapter adapter) {
            DTO_follow item = adapter.items.get(getBindingAdapterPosition());
            String user_id = PreferenceManager.getString(adapter.context, "userId");
            String follower = item.getUser_id();

            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_message> call = retrofitInterface.writeFollow(user_id, follower);
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

        private void deleteFollow(Request_adapter adapter) {
            DTO_follow item = adapter.items.get(getBindingAdapterPosition());
            String user_id = PreferenceManager.getString(adapter.context, "userId");
            String follower = item.getUser_id();

            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_message> call = retrofitInterface.deleteFollow(follower, user_id);
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
