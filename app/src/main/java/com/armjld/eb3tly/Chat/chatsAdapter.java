package com.armjld.eb3tly.Chat;

import android.content.Context;
import android.content.Intent;
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
import com.armjld.eb3tly.DatabaseClasses.caculateTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;
import Model.Chat;
import Model.ChatsData;

public class chatsAdapter extends RecyclerView.Adapter<chatsAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChatsData> chatData;

    private DatabaseReference messageDatabase, uDatabase;
    String TAG = "Chat Adapter";
    public static caculateTime _cacu = new caculateTime();

    public void addItem(int position , ChatsData data) {
        int size = chatData.size();
        if (size > position && size != 0) {
            chatData.set(position, data);
            notifyItemChanged(position);

        }
    }

    public chatsAdapter(Context context,  ArrayList<ChatsData> chatData) {
        this.context = context;
        this.chatData = chatData;
        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    }

    @NonNull
    @Override
    public chatsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.card_chat,parent,false);
        return new chatsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull chatsAdapter.MyViewHolder holder, final int position) {
        ChatsData chat = chatData.get(position);
        String talkerID = chat.getUserId();
        Log.i(TAG, talkerID);

        holder.myview.setOnClickListener(v -> {
            Intent intent = new Intent(context, Messages.class);
            intent.putExtra("roomid", chat.getRoomid());
            intent.putExtra("rid", chat.getUserId());
            context.startActivity(intent);
            Messages.cameFrom = "Chats";
            ChatFragmet.cameFrom = "Chat";
        });

        uDatabase.child(talkerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imgURL = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                holder.txtName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                Picasso.get().load(Uri.parse(imgURL)).into(holder.imgEditPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        messageDatabase.child(chat.getRoomid()).orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chatS = snapshot.getValue(Chat.class);
                assert chatS != null;
                for(DataSnapshot ds : snapshot.getChildren()) {

                    holder.txtBody.setText(Objects.requireNonNull(ds.child("msg").getValue()).toString());
                        String startDate = Objects.requireNonNull(ds.child("timestamp").getValue()).toString();
                        holder.txtNotidate.setText(_cacu.setPostDate(startDate));
                }
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
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