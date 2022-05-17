package com.example.wayout_ver_01.RecyclerView.Theme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Activity.Search.SearchCafe_read;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;
import com.example.wayout_ver_01.Retrofit.DTO_review;
import com.example.wayout_ver_01.Retrofit.RetrofitClient;
import com.example.wayout_ver_01.Retrofit.RetrofitInterface;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;

import lib.kingja.switchbutton.SwitchMultiButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchCafe_review_adapter extends RecyclerView.Adapter<SearchCafe_review_adapter.viewHolder> {
    ArrayList<DTO_review> items = new ArrayList<>();
    Context context;
    BaseRatingBar rate1;
    BaseRatingBar rate2;
    TextView rate3;
    String cafe_index, theme_index;
    boolean mode = false;

    public SearchCafe_review_adapter(Context context, BaseRatingBar rate1, BaseRatingBar rate2, TextView rate3, String cafe_index) {
        this.context = context;
        this.rate1 = rate1;
        this.rate2 = rate2;
        this.rate3 = rate3;
        this.cafe_index = cafe_index;
    }

    public SearchCafe_review_adapter(Context context) {
        this.context = context;
    }

    public void addItem(DTO_review item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void setThemeIndex(String theme_index){
        this.theme_index = theme_index;
    }

    public void scrollItem(DTO_review item) {
        items.add(item);
    }

    public void deleteItem(int pos) {
        items.remove(pos);
        notifyItemRemoved(pos);
    }

    public void clearItems(){
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0,size);
    }

    public void setMode(){
        this.mode = true;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_cafe, parent, false);
        return new viewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        DTO_review item = items.get(position);
        String my_ID = PreferenceManager.getString(context,"autoId");

        if(mode) {
            holder.item_exit.setVisibility(View.VISIBLE);
            holder.item_diff.setVisibility(View.VISIBLE);
            holder.item_diff.setText(item.getDiff());
            if ("실패".equals(item.getSuccess())) {
                Glide.with(context).load(R.drawable.fall_down).fitCenter().into(holder.item_exit);
            }else{
                Glide.with(context).load(R.drawable.exit_out).fitCenter().into(holder.item_exit);
            }
        }

        if(!item.getWriter().equals(my_ID)){
            holder.item_delete.setVisibility(View.GONE);
            holder.item_update.setVisibility(View.GONE);
        }

        holder.item_writer.setText(item.getWriter());
        holder.item_content.setText(item.getContent());
        holder.item_rate.setRating(item.getRate());
        try {
            holder.item_date.setText(DateConverter.resultDateToString(item.getDate(), "yyyy-MM-dd"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView item_writer, item_date, item_content, item_update, item_delete, item_diff;
        BaseRatingBar item_rate;
        ImageView item_exit;

        public viewHolder(@NonNull View itemView, SearchCafe_review_adapter adapter) {
            super(itemView);

            item_writer = itemView.findViewById(R.id.item_cafe_review_writer);
            item_content = itemView.findViewById(R.id.item_cafe_review_content);
            item_date = itemView.findViewById(R.id.item_cafe_review_date);
            item_rate = itemView.findViewById(R.id.item_cafe_review_grade);
            item_update = itemView.findViewById(R.id.item_cafe_review_update);
            item_delete = itemView.findViewById(R.id.item_cafe_review_delete);
            item_diff = itemView.findViewById(R.id.item_cafe_review_diff);
            item_exit = itemView.findViewById(R.id.item_cafe_review_exit);

            /* 리뷰 삭제 후 데이터 다시 뿌림 */
            item_delete.setOnClickListener((v -> {
                if(adapter.mode){
                    deleteThemeReview(adapter);
                }else {
                    deleteReview(adapter);
                }
            }));

            /* 리뷰 수정 후 데이터 다시 뿌림 */
            item_update.setOnClickListener((v ->{
                if(adapter.mode){
                    updateThemeReview(adapter);
                }else {
                    updateReview(adapter);
                }
            }));

        }

        private void updateThemeReview(SearchCafe_review_adapter adapter) {
            Dialog dialog = new Dialog(itemView.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_dialog_theme);
            dialog.show();

            /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */
            // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.
            ScaleRatingBar scaleRatingBar = dialog.findViewById(R.id.item_dialog_theme_grade);
            SwitchMultiButton diff_btn = dialog.findViewById(R.id.item_dialog_theme_diff);
            SwitchMultiButton success_btn = dialog.findViewById(R.id.item_dialog_theme_success);
            EditText ed_content = dialog.findViewById(R.id.item_dialog_theme_content);
            TextView no_btn = dialog.findViewById(R.id.item_dialog_theme_no);
            TextView yes_btn = dialog.findViewById(R.id.item_dialog_theme_yes);
            int position = getBindingAdapterPosition();
            Log.e("posion","position + "+ position);

            String[] success = {"성공", "실패"};
            String[] difficult = {"Easy","Normal","Hard","Hell"};

            /* 수정할 내용 세팅 */
            DTO_review item = adapter.items.get(getBindingAdapterPosition());
            scaleRatingBar.setRating(adapter.items.get(getBindingAdapterPosition()).getRate());
            String suc = item.getSuccess();
            String dif = item.getDiff();
            ed_content.setText(item.getContent());
             if("실패".equals(suc)) {
                 success_btn.setSelectedTab(1);
             }
             switch (dif){
                 case "Easy":
                     diff_btn.setSelectedTab(0);
                     break;
                 case "Normal":
                     diff_btn.setSelectedTab(1);
                     break;
                 case "Hard":
                     diff_btn.setSelectedTab(2);
                     break;
                 default:
                     diff_btn.setSelectedTab(3);
                     break;
             }

            /* 예 */
            yes_btn.setOnClickListener(v -> {
               String dialog_diff = difficult[diff_btn.getSelectedTab()];
               String dialog_exit = success[success_btn.getSelectedTab()];
               float dialog_rate = scaleRatingBar.getRating();
               String dialog_content = ed_content.getText().toString();
                Log.e("확인","Diff + : " + dialog_diff);
                Log.e("확인","Diff + : " + dialog_exit);
                Log.e("확인","Diff + : " + dialog_rate);
                if(dialog_content.isEmpty()){
                    ed_content.requestFocus();
                    Toast.makeText(itemView.getContext(),"리뷰 내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                /* 리뷰 수정 */
                String review_index = adapter.items.get(getBindingAdapterPosition()).getIndex();
                RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                Call<DTO_review> call = retrofitInterface.updateThemeReview(adapter.theme_index,review_index,dialog_rate,dialog_content,dialog_exit,dialog_diff);
                call.enqueue(new Callback<DTO_review>() {
                    @Override
                    public void onResponse(Call<DTO_review> call, Response<DTO_review> response) {
                        if(response.body() != null && response.isSuccessful()){
                            float total_rate = response.body().getTotal();
                            adapter.rate1.setRating(total_rate);
                            adapter.rate2.setRating(total_rate);
                            adapter.rate3.setText(String.format("평점  %s", total_rate));

                            Log.e("position", "position" + position);
                            item.setDiff(dialog_diff);
                            item.setSuccess(dialog_exit);
                            item.setRate(dialog_rate);
                            item.setContent(dialog_content);
                            adapter.items.set(position,item);
                            adapter.notifyItemChanged(position);
                        }
                    }

                    @Override
                    public void onFailure(Call<DTO_review> call, Throwable t) {
                        Log.e("review_adapter,213","테마 리뷰 수정 실패 : " + t);
                    }
                });

                dialog.dismiss();
            });

            /* 아니오 */
            no_btn.setOnClickListener(v -> {
                dialog.dismiss();
            });


        }



        private void deleteThemeReview(SearchCafe_review_adapter adapter) {
            // 다이얼로그 등록
            Dialog dialog2 = new Dialog(itemView.getContext()); // 객체 초기화
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
            dialog2.setContentView(R.layout.item_dialog_check); // 레이아웃과 Dialog
            dialog2.show();

            /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */
            // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

            //  아니오
            TextView noBtn = dialog2.findViewById(R.id.item_dialog_yn_no);
            noBtn.setOnClickListener((v -> {
                dialog2.dismiss();
//            Log.e("아니오 버튼", "OK");
            }));

            // 예
            TextView yesBtn = dialog2.findViewById(R.id.item_dialog_yn_yes);
            yesBtn.setOnClickListener((v -> {

                /* 리뷰작성 서버에 등록 후 서버에서 리뷰 데이터 가져와서 다시 세팅 */
                // 리뷰 삭제
                RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                Call<DTO_review> call = retrofitInterface.deleteThemeReview(adapter.theme_index, adapter.items.get(getBindingAdapterPosition()).getIndex());
                call.enqueue(new Callback<DTO_review>() {
                    @Override
                    public void onResponse(Call<DTO_review> call, Response<DTO_review> response) {
                        if(response.isSuccessful() && response.body() != null) {

                            float total_rate = response.body().getTotal();
                            Log.e("토탈","토탈 : " + total_rate);
                            adapter.deleteItem(getBindingAdapterPosition());
                            adapter.rate1.setRating(total_rate);
                            adapter.rate2.setRating(total_rate);
                            adapter.rate3.setText("평점  " + total_rate);
                        }
                    }

                    @Override
                    public void onFailure(Call<DTO_review> call, Throwable t) {
                        Log.e("review_adapter, 227", "cafe_review 삭제 실패 : " + t);
                    }
                });

                // Dialog 종료
                dialog2.dismiss();
            }));
        }

        private void updateReview(SearchCafe_review_adapter adapter) {
            // 다이얼로그 등록
            Dialog dialog = new Dialog(itemView.getContext()); // 객체 초기화
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
            if(adapter.mode){
                dialog.setContentView(R.layout.item_dialog_theme);
            }else {
                dialog.setContentView(R.layout.item_dialog); // 레이아웃과 Dialog
            }
            DialogShow(dialog, adapter);
        }

        private void DialogShow(Dialog dialog, SearchCafe_review_adapter adapter) {
            dialog.show();
            /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

            // 위젯 연결 방식은 각자 취향대로
            // '아래 아니오 버튼' 처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
            // '아래 네 버튼' 처럼 바로 연결하면 일회성으로 사용하기 편함.
            // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

            ScaleRatingBar scaleRatingBar = dialog.findViewById(R.id.item_dialog_grade);
            EditText dialog_content = dialog.findViewById(R.id.item_dialog_content);
            TextView dialog_title = dialog.findViewById(R.id.item_dialog_title);
            dialog_title.setText("리뷰 수정");

            scaleRatingBar.setRating(adapter.items.get(getBindingAdapterPosition()).getRate());
            dialog_content.setText(adapter.items.get(getBindingAdapterPosition()).getContent());

            //  아니오
            TextView noBtn = dialog.findViewById(R.id.item_dialog_no);
            noBtn.setOnClickListener((v -> {
                dialog.dismiss();
//            Log.e("아니오 버튼", "OK");
            }));

            // 예
            TextView yesBtn = dialog.findViewById(R.id.item_dialog_yes);
            yesBtn.setOnClickListener((v -> {
                // 작성값 평점 가져오기
                float rate = scaleRatingBar.getRating();
                String content = dialog_content.getText().toString();
                ProgressDialog progressDialog = new ProgressDialog(itemView.getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();



                // 리뷰 내용 없을시 예외 처리 -> 작성해달라고 토스트 요청
                if (content.isEmpty()) {
                    Toast.makeText(itemView.getContext(), "리뷰 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                /* 리뷰작성 서버에 등록 후 서버에서 리뷰 데이터 가져와서 다시 세팅 */
                // 리뷰 수정하기 ( 작성 내용 , 평점 가져오기 )
                // 수정내용 , 수정 평점, 수정할 리뷰의 고유값
                writeReview(adapter.cafe_index, content, rate, adapter.items.get(getBindingAdapterPosition()).getIndex(), adapter);

                progressDialog.dismiss();
                // Dialog 종료
                dialog.dismiss();
            }));
        }

        private void writeReview(String cafe_index,String content, float rate, String index, SearchCafe_review_adapter adapter) {
            RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
            Call<DTO_review> call = retrofitInterface.updateCafeReview(cafe_index,index,rate,content);
            call.enqueue(new Callback<DTO_review>() {
                @Override
                public void onResponse(Call<DTO_review> call, Response<DTO_review> response) {
                    if(response.isSuccessful() && response.body() != null) {
                        float total_rate = response.body().getTotal();
                        adapter.rate1.setRating(total_rate);
                        adapter.rate2.setRating(total_rate);
                        adapter.rate3.setText("평점  " + total_rate);
                        adapter.items.get(getBindingAdapterPosition()).setContent(content);
                        adapter.items.get(getBindingAdapterPosition()).setRate(rate);
                        adapter.notifyItemChanged(getBindingAdapterPosition());
                    }

                }

                @Override
                public void onFailure(Call<DTO_review> call, Throwable t) {
                    Log.e("review_adapter, 195", "리뷰  수정 실패 : " + t);
                }
            });

        }

        private void deleteReview(SearchCafe_review_adapter adapter) {

            // 다이얼로그 등록
            Dialog dialog2 = new Dialog(itemView.getContext()); // 객체 초기화
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
            dialog2.setContentView(R.layout.item_dialog_check); // 레이아웃과 Dialog
            dialog2.show();

            /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

            // 위젯 연결 방식은 각자 취향대로
            // '아래 아니오 버튼' 처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
            // '아래 네 버튼' 처럼 바로 연결하면 일회성으로 사용하기 편함.
            // * 주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

            //  아니오
            TextView noBtn = dialog2.findViewById(R.id.item_dialog_yn_no);
            noBtn.setOnClickListener((v -> {
                dialog2.dismiss();
//            Log.e("아니오 버튼", "OK");
            }));

            // 예
            TextView yesBtn = dialog2.findViewById(R.id.item_dialog_yn_yes);
            yesBtn.setOnClickListener((v -> {

                /* 리뷰작성 서버에 등록 후 서버에서 리뷰 데이터 가져와서 다시 세팅 */
                // 리뷰 삭제
                RetrofitInterface retrofitInterface = RetrofitClient.getApiClint().create(RetrofitInterface.class);
                Call<DTO_review> call = retrofitInterface.deleteCafeReview(adapter.cafe_index, adapter.items.get(getBindingAdapterPosition()).getIndex());
                call.enqueue(new Callback<DTO_review>() {
                    @Override
                    public void onResponse(Call<DTO_review> call, Response<DTO_review> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            adapter.deleteItem(getBindingAdapterPosition());
                            adapter.rate1.setRating(response.body().getTotal());
                            adapter.rate2.setRating(response.body().getTotal());
                            adapter.rate3.setText("평점  " + response.body().getTotal());
                        }
                    }

                    @Override
                    public void onFailure(Call<DTO_review> call, Throwable t) {
                        Log.e("review_adapter, 227", "cafe_review 삭제 실패 : " + t);
                    }
                });

                // Dialog 종료
                dialog2.dismiss();
            }));
        }
    }
}
