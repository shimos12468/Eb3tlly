package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import Model.notiData;
import Model.replyAdmin;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class replyAdapter extends RecyclerView.Adapter<replyAdapter.MyViewHolder> {

    Context context, context1;
    long count;
    replyAdmin[] replyAdmin;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference uDatabase, nDatabase,cDatabase;
    private String TAG = "Notification Adapter";
    String datee = DateFormat.getDateInstance().format(new Date());

    public replyAdapter(Context context, replyAdmin[] replyAdmin, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.replyAdmin = replyAdmin;
        this.context1 = context1;
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        cDatabase = getInstance().getReference().child("Pickly").child("messages");
        Log.i(TAG, " Reached Reply Adapter");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_reply_admin, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String Email = replyAdmin[position].getEmail();
        String Message = replyAdmin[position].getMessage();
        String Name = replyAdmin[position].getName();
        String TimeStamp = replyAdmin[position].getTimestamp();
        String Statue = replyAdmin[position].getStatue();
        String Phone = replyAdmin[position].getPhone();
        String mID = replyAdmin[position].getId();
        String uID = replyAdmin[position].getuID();
        String version = replyAdmin[position].getCurrentVersion();



        holder.setBody(Name, Phone, Email, Message,TimeStamp,version);

        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myReply = holder.txtReply.getText().toString().trim();

                notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1",uID, "-M9z6ArQZAr9snFyM_mR", myReply, datee, "false");
                nDatabase.child(uID).push().setValue(Noti);
                cDatabase.child(uID).child(mID).child("statue").setValue("closed");
                Toast.makeText(context, "Replied Success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (int) count;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        TextView txtName, txtPhone, txtEmail, txtDate, txtMessage;
        EditText txtReply;
        Button btnReply;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            txtReply = myview.findViewById(R.id.txtReply);
            btnReply = myview.findViewById(R.id.btnReply);
        }

        @SuppressLint("SetTextI18n")
        public void setBody(String name, String phone, String email, String message, String date, String version) {
            txtDate = myview.findViewById(R.id.txtDate);
            txtEmail = myview.findViewById(R.id.txtEmail);
            txtMessage = myview.findViewById(R.id.txtMessage);
            txtName = myview.findViewById(R.id.txtName);
            txtPhone = myview.findViewById(R.id.txtPhone);
            txtPhone.setText(phone);
            txtName.setText(name);
            txtMessage.setText(message);
            txtEmail.setText(email);
            txtDate.setText(date + "     App Version : " + version);
        }
    }
}