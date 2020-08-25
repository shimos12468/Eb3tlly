package com.armjld.eb3tly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.Chat;
import Model.ChatsData;

public class chatsAdapter extends RecyclerView.Adapter<com.armjld.eb3tly.Adapters.chatsAdapter.MyViewHolder> {

    Context context;
    List<ChatsData> chatData;
    String uId = UserInFormation.getId();

    private DatabaseReference nDatabase, messageDatabase, uDatabase;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    public chatsAdapter(Context context, List<ChatsData> chatData) {
        this.context = context;
        this.chatData = chatData;

        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        String TAG = "Chat Adapter";
    }

    @NonNull
    @Override
    public com.armjld.eb3tly.Adapters.chatsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.card_chat, parent, false);
            return new chatsAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull chatsAdapter.MyViewHolder holder, final int position) {
        ChatsData chat = chatData.get(position);

        String talkerID = "";
        if(chat.getUser1().equals(uId)) {
            talkerID = chat.getUser2();
        } else {
            talkerID = chat.getUser1();
        }

        holder.myview.setOnClickListener(v -> {

        });

        uDatabase.child(talkerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imgURL = snapshot.child("ppURL").getValue().toString();
                holder.txtName.setText(snapshot.child("name").getValue().toString());
                Picasso.get().load(Uri.parse(imgURL)).into(holder.imgEditPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        messageDatabase.child(chat.getGroupid()).orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chat = snapshot.getValue(Chat.class);
                assert chat != null;
                holder.txtBody.setText(chat.getMsg());
                holder.txtNotidate.setText(chat.getTimestamp());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName,txtBody,txtNotidate;
        ImageView imgEditPhoto;
        View myview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            txtName = itemView.findViewById(R.id.txtName);
            txtBody = itemView.findViewById(R.id.txtBody);
            txtNotidate = itemView.findViewById(R.id.txtNotidate);
            imgEditPhoto = itemView.findViewById(R.id.imgEditPhoto);
        }
    }
}