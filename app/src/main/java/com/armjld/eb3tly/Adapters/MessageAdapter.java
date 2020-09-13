package com.armjld.eb3tly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.Chat;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MessageAdapter extends RecyclerView.Adapter<com.armjld.eb3tly.Adapters.MessageAdapter.MyViewHolder> {

    public static int MSG_RIGHT = 1;
    public static int MSG_LEFT = 0;

    Context context;
    List<Chat> chatData;
    String uId = UserInFormation.getId();

    private DatabaseReference nDatabase, confirmDatabase, uDatabase;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    public MessageAdapter(Context context, List<Model.Chat> chatData) {
        this.context = context;
        this.chatData = chatData;

        confirmDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("confirms");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");


        String TAG = "Message Adapter";
    }

    @NonNull
    @Override
    public com.armjld.eb3tly.Adapters.MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_RIGHT) {
            // My Message
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
            return new MessageAdapter.MyViewHolder(view);
        } else {
            // Recive Message
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
            return new MessageAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull com.armjld.eb3tly.Adapters.MessageAdapter.MyViewHolder holder, final int position) {
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMsg);
        }
    }
}