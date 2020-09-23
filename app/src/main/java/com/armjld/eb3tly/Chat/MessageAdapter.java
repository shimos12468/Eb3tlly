package com.armjld.eb3tly.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import java.util.List;
import Model.Chat;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    public static int MSG_RIGHT = 1;
    public static int MSG_LEFT = 0;
    public static String TAG = "Message Adapter";

    Context context;
    List<Chat> chatData;
    String uId = UserInFormation.getId();


    public MessageAdapter(Context context, List<Model.Chat> chatData) {
        this.context = context;
        this.chatData = chatData;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_RIGHT) {
            // My Message
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
            return new MyViewHolder(view);
        } else {
            // Recive Message
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, final int position) {
        Chat chat = chatData.get(position);
        holder.txtMsg.setText(chat.getMsg());
    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatData.get(position).getSenderid().equals(uId)) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMsg);
        }
    }
}