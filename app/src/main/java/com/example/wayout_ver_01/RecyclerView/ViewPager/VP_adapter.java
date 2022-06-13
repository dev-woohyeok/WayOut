package com.example.wayout_ver_01.RecyclerView.ViewPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class VP_adapter extends FragmentStateAdapter {
    // 객체 타입의 final => 객체의 필드값은 변경할 수 있지만 다른객체로 바꿀수는 없다.
    private final ArrayList<Fragment> items = new ArrayList<>();

    public void addItem(Fragment item){
        this.items.add(item);
        notifyItemInserted(items.size());
    }

    public void changeItem(Fragment item,int pos){
        this.items.set(pos,item);
        notifyItemChanged(pos);
    }

    public void clearItem(){
        int size = items.size();
        items.clear();
        notifyItemRangeRemoved(0,size);
    }

    public VP_adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public VP_adapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public VP_adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
