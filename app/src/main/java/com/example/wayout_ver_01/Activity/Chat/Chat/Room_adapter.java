package com.example.wayout_ver_01.Activity.Chat.Chat;

import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wayout_ver_01.Class.DateConverter;
import com.example.wayout_ver_01.Class.PreferenceManager;
import com.example.wayout_ver_01.R;

import java.text.ParseException;
import java.util.ArrayList;

public class Room_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<DTO_message> items = new ArrayList<>();
    int join_number;
    int member;

    public Room_adapter(Context context) {
        this.context = context;
    }

    public void setJoin_number(int join) {
        this.join_number = join;
    }

    @Override
    public int getItemViewType(int position) {
        DTO_message item = items.get(position);
        String type = item.getType();
        String writer = item.getName();
        String me = PreferenceManager.getString(context, "userId");
//        Log.e("room_adapter", "writer : " + writer);
//        Log.e("room_adapter", "me : " + me);
        /* 어떤 view 로 문자를 주고 받을지 설정 */
        switch (type) {
            case "io":
                Log.w("//===========//", "================================================");
                Log.i("", "\n" + "[ChatRoom_adapter, return Type : 0 (IO) :: ]");
                Log.w("//===========//", "================================================");
                return 0;
            case "msg":
                /* 내가 보낸건지 아닌지 확인 */
                if (writer.equals(me)) {
                    Log.w("//===========//", "================================================");
                    Log.i("", "\n" + "[ChatRoom_adapter, return Type : 1(내가 보낸거) (msg) :: ]");
                    Log.w("//===========//", "================================================");
                    return 1;
                } else {
                    Log.w("//===========//", "================================================");
                    Log.i("", "\n" + "[ChatRoom_adapter, return Type : 2(내가 받은거) (msg) :: ]");
                    Log.w("//===========//", "================================================");
                    return 2;
                }
            case "img":
                /* 내가 보낸건지 아닌지 확인 */
                if (writer.equals(me)) {
                    Log.w("//===========//", "================================================");
                    Log.i("", "\n" + "[ChatRoom_adapter, return Type : 3(내가 보낸거) (img) :: ]");
                    Log.w("//===========//", "================================================");
                    return 3;
                } else {
                    Log.w("//===========//", "================================================");
                    Log.i("", "\n" + "[ChatRoom_adapter, return Type : 4(내가 받은거) (img) :: ]");
                    Log.w("//===========//", "================================================");
                    return 4;
                }
            default:
                Log.e("Room_adapter,57", "view Type 에러, Type 값을 확인하세요 Type : " + type);
                return 5;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        /* 결정된 ViewType 에 맞게 viewHolder 와 inflater setting */
        switch (viewType) {
            case 0:
                /* 방출입 */
                Log.e("room_adapter,58", "viewType : " + viewType + " [방출입시]");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_io, parent, false);
                return new IO_VH(view, this);
            case 1:
                /*  메세지 오른쪽 */
                Log.e("room_adapter,62", "viewType : " + viewType + " [내가 보낸 메세지]");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_send, parent, false);
                return new Msg_Right_VH(view, this);
            case 2:
                /* 메세지 왼쪽 */
                Log.e("room_adapter,69", "viewType : " + viewType + " [내가 받은 메세지]");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_receive, parent, false);
                return new Msg_Left_VH(view, this);
            case 3:
                /* 이미지 오른쪽 */
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ Room_adapter viewType : " + viewType + " [내가 보낸 이미지]  ]");
                Log.e("//===========//", "================================================");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_img_send, parent, false);
                return new img_Right_VH(view, this);
            case 4:
                /* 이미지 왼쪽 */
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ Room_adapter viewType : " + viewType + " [내가 받은 이미지]  ]");
                Log.e("//===========//", "================================================");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_img_receive, parent, false);
                return new img_Left_VH(view, this);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_io, parent, false);
                Log.e("//===========//", "================================================");
                Log.e("", "\n" + "[ RoomAdapter_ViewHolder type 오류, 122 ]");
                Log.e("//===========//", "================================================");
                return new IO_VH(view, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DTO_message item = items.get(position);
        String user_id = PreferenceManager.getString(context, "userId");
        int IO = join_number - item.getMember();
        item.setIO(IO);


        if (holder instanceof IO_VH) {
            /* 방출입 ,[ 메세지 ] */
            IO_VH VH = (IO_VH) holder;
            /* 내 출입메세지는 안보기 */
            if (!user_id.equals(item.getName())) {
                VH.item_message.setText(item.getMessage());
                VH.item_message.setVisibility(View.VISIBLE);
            } else {
                VH.item_message.setVisibility(View.GONE);
            }
        } else if (holder instanceof Msg_Right_VH) {
            /* 보낸 msg [ 메세지, 작성시간 ]*/
            Msg_Right_VH VH = (Msg_Right_VH) holder;
            VH.item_message.setText(item.getMessage());
            VH.item_memeber.setText("" + item.getIO());
            if (item.getIO() <= 0) {
                VH.item_memeber.setVisibility(View.INVISIBLE);
            } else {
                VH.item_memeber.setVisibility(View.VISIBLE);
            }
            System.out.println(item.getMessage());
            System.out.println("ChatRoomAdapter,101");
            try {
                Log.w("//===========//", "================================================");
                Log.i("", "\n" + "[ChatRoom_adapter, item.getDate() : " + item.getDate() + " :: ]");
                Log.w("//===========//", "================================================");
                VH.item_date.setText(DateConverter.resultDateToString(item.getDate(), "a h시 m분"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (holder instanceof Msg_Left_VH) {
            /* 받은 msg [ 보낸사람, 프로필사진, 내용, 작성시간 ]*/
            Msg_Left_VH VH = (Msg_Left_VH) holder;
            VH.item_name.setText(item.getName());
            VH.item_message.setText(item.getMessage());
            VH.item_member.setText("" + item.getIO());
            if (item.getIO() <= 0) {
                VH.item_member.setVisibility(View.INVISIBLE);
            } else {
                VH.item_member.setVisibility(View.VISIBLE);
            }
            Glide.with(context).load(item.getImage()).circleCrop().into(VH.item_image);
            try {
                VH.item_date.setText(DateConverter.resultDateToString(item.getDate(), "a h시 m분"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (holder instanceof img_Right_VH) {
            /* 보낸 img [사진, 작성시간] */
            img_Right_VH VH = (img_Right_VH) holder;
            VH.item_member.setText("" + item.getIO());
            if (item.getIO() <= 0) {
                VH.item_member.setVisibility(View.INVISIBLE);
            } else {
                VH.item_member.setVisibility(View.VISIBLE);
            }
            /* 보낸 이미지 */
            Glide.with(context).load(item.getMessage()).centerCrop().into(VH.item_image);
            try {
                VH.item_date.setText(DateConverter.resultDateToString(item.getDate(), "a h시 m분"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (holder instanceof img_Left_VH) {
            /* 받은 img [보낸사람, 프로필사진, 사진, 작성시간] */
            img_Left_VH VH = (img_Left_VH) holder;
            VH.item_name.setText(item.getName());
            VH.item_member.setText("" + item.getIO());
            if (item.getIO() <= 0) {
                VH.item_member.setVisibility(View.INVISIBLE);
            } else {
                VH.item_member.setVisibility(View.VISIBLE);
            }

            /* 유저 프로필 */
            Glide.with(context).load(item.getImage()).circleCrop().into(VH.item_profile);
            /* 받은 이미지 */
            Glide.with(context).load(item.getMessage()).centerCrop().into(VH.item_image);
            try {
                VH.item_date.setText(DateConverter.resultDateToString(item.getDate(), "a h시 m분"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    /* IO */
    public static class IO_VH extends RecyclerView.ViewHolder {
        TextView item_message;

        public IO_VH(@NonNull View itemView, Room_adapter room_adapter) {
            super(itemView);
            item_message = itemView.findViewById(R.id.item_IO_message);
        }
    }

    /* 보낸 메세지 */
    public static class Msg_Right_VH extends RecyclerView.ViewHolder {
        TextView item_message, item_date, item_memeber;

        public Msg_Right_VH(@NonNull View itemView, Room_adapter room_adapter) {
            super(itemView);
            item_message = itemView.findViewById(R.id.item_send_message);
            item_date = itemView.findViewById(R.id.item_send_date);
            item_memeber = itemView.findViewById(R.id.item_send_member);
        }
    }

    /* 받은 메세지 */
    public static class Msg_Left_VH extends RecyclerView.ViewHolder {
        TextView item_name, item_message, item_date, item_member;
        ImageView item_image;

        public Msg_Left_VH(@NonNull View itemView, Room_adapter room_adapter) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_receive_name);
            item_message = itemView.findViewById(R.id.item_receive_message);
            item_date = itemView.findViewById(R.id.item_receive_date);
            item_image = itemView.findViewById(R.id.item_receive_image);
            item_member = itemView.findViewById(R.id.item_receive_member);
        }
    }

    /* 보낸 이미지 */
    public static class img_Right_VH extends RecyclerView.ViewHolder {
        TextView item_member, item_date;
        ImageView item_image;

        public img_Right_VH(@NonNull View itemView, Room_adapter room_adapter) {
            super(itemView);
            item_member = itemView.findViewById(R.id.item_send_image_member);
            item_date = itemView.findViewById(R.id.item_send_image_date);
            item_image = itemView.findViewById(R.id.item_send_image);
        }
    }

    /* 받은 이미지 */
    public static class img_Left_VH extends RecyclerView.ViewHolder {
        TextView item_name, item_date, item_member;
        ImageView item_image, item_profile;

        public img_Left_VH(@NonNull View itemView, Room_adapter room_adapter) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_receive_image_name);
            item_member = itemView.findViewById(R.id.item_receive_image_member);
            item_date = itemView.findViewById(R.id.item_receive_image_date);
            item_image = itemView.findViewById(R.id.item_receive_image_message);
            item_profile = itemView.findViewById(R.id.item_receive_image_profile);
        }
    }

    public void addItem(DTO_message item) {
        items.add(item);
        notifyItemInserted(items.size());
    }

    public void sendItem(DTO_message item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    public ArrayList<DTO_message> getItems() {
        return items;
    }

    public void setItems(ArrayList<DTO_message> list) {
        this.items = list;
        notifyItemRangeChanged(0, list.size());
    }

    public void setMember(ArrayList<DTO_message> list) {
        for (int i = 0; i < items.size(); i++) {
            int member = list.get(i).getMember();
            items.get((items.size() - 1) - i).setMember(member);
        }
        notifyItemRangeChanged(0, items.size());
    }

    public void itemsClear() {
        int size = getItemCount();
        items.clear();
        notifyItemRangeRemoved(0, size);
    }

}
