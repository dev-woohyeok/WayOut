package com.example.wayout_ver_01;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FreeBoard_adapter extends RecyclerView.Adapter<FreeBoard_adapter.CustumViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    ArrayList<FreeBoard> List_items = new ArrayList<FreeBoard>();
    Context context;

    @NonNull
    @Override
    public CustumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_freeboard, parent, false);

        return new CustumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustumViewHolder holder, int position) {
        // Item 초기 설정 세팅 하는 곳
        Log.e(TAG, "내용 : onBindViewHolder");
        // ItemView 재활용한 ViewHolder 에 데이터 넣는 곳
        FreeBoard item = List_items.get(position);
    }

    @Override
    public int getItemCount() {
        // 아이탬 갯수 리턴
        return List_items.size();
    }

    public class CustumViewHolder extends RecyclerView.ViewHolder {
        // item 의 Find View ID 하면됨

        public CustumViewHolder (@NonNull View itemView) {
            super(itemView);
            // View 의 Click event 같은건 여기서 처리

        }

        public void setItem(FreeBoard itemView){

        }
    }
}
