package com.armjld.eb3tly.Adapters;

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
import com.armjld.eb3tly.Utilites.UserInFormation;
import com.armjld.eb3tly.messeges.Messages;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.Chat;
import Model.ChatsData;
import Model.Data;

public class chatsAdapter extends RecyclerView.Adapter<com.armjld.eb3tly.Adapters.chatsAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChatsData> chatData;
    String uId = UserInFormation.getId();

    private DatabaseReference nDatabase, messageDatabase, uDatabase;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    String TAG = "Chat Adapter";
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

        messageDatabase.child(chat.getRoomid()).orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chatS = snapshot.getValue(Chat.class);
                assert chatS != null;
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Log.i(TAG, ds.getKey());
                    holder.txtBody.setText(ds.child("msg").getValue().toString());
                        String startDate = ds.child("timestamp").getValue().toString();
                        String stopDate = datee;
                        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
                        Date d1 = null;
                        Date d2 = null;
                        try {
                            d1 = format.parse(startDate);
                            d2 = format.parse(stopDate);
                        } catch (java.text.ParseException ex) {
                            ex.printStackTrace();
                        }
                        assert d2 != null;
                        assert d1 != null;
                        long diff = d2.getTime() - d1.getTime();
                        long diffSeconds = diff / 1000;
                        long diffMinutes = diff / (60 * 1000);
                        long diffHours = diff / (60 * 60 * 1000);
                        long diffDays = diff / (24 * 60 * 60 * 1000);

                        String finalDate = "";
                        if (diffSeconds < 60) {
                            finalDate = "منذ " + diffSeconds + " ثوان";
                        } else if (diffSeconds > 60 && diffSeconds < 3600) {
                            finalDate = "منذ " + diffMinutes + " دقيقة";
                        } else if (diffSeconds > 3600 && diffSeconds < 86400) {
                            finalDate = "منذ " + diffHours + " ساعات";
                        } else if (diffSeconds > 86400) {
                            finalDate = "منذ " +diffDays + " ايام";
                        }
                        holder.txtNotidate.setText(finalDate);
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