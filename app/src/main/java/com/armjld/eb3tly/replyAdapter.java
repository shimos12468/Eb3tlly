package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import Model.notiData;
import Model.replyAdmin;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class replyAdapter extends RecyclerView.Adapter<replyAdapter.MyViewHolder> {

    Context context, context1;
    long count;
    ArrayList<replyAdmin>replyAdmin;
    private DatabaseReference nDatabase,cDatabase;
    String datee = DateFormat.getDateInstance().format(new Date());

    public replyAdapter(Context context, ArrayList<Model.replyAdmin> replyAdmin, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.replyAdmin = replyAdmin;
        this.context1 = context1;
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        cDatabase = getInstance().getReference().child("Pickly").child("messages");
        String TAG = "Notification Adapter";
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
        String Email = replyAdmin.get(position).getEmail();
        String Message = replyAdmin.get(position).getMessage();
        String Name = replyAdmin.get(position).getName();
        String TimeStamp = replyAdmin.get(position).getTimestamp();
        String Phone = replyAdmin.get(position).getPhone();
        String mID = replyAdmin.get(position).getId();
        String uID = replyAdmin.get(position).getuID();
        String version = replyAdmin.get(position).getCurrentVersion();



        holder.setBody(Name, Phone, Email, Message,TimeStamp,version);

        holder.btnClose.setOnClickListener(v -> {
            cDatabase.child(uID).child(mID).child("statue").setValue("closed");
            replyAdmin.remove(position);
            notifyItemRemoved(position);
        });

        holder.btnReply.setOnClickListener(v -> {
            String myReply = holder.txtReply.getText().toString().trim();

            notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1",uID, "-MAPQWoKEfmHIQG9xv-v", myReply, datee, "false");
            nDatabase.child(uID).push().setValue(Noti);

            cDatabase.child(uID).child(mID).child("statue").setValue("closed");
            Toast.makeText(context, "Replied Success", Toast.LENGTH_SHORT).show();
            replyAdmin.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return (int) count;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        TextView txtName, txtPhone, txtEmail, txtDate, txtMessage;
        EditText txtReply;
        Button btnReply;
        ImageButton btnClose;

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
            btnClose = myview.findViewById(R.id.btnClose);
            txtPhone.setText(phone);
            txtName.setText(name);
            txtMessage.setText(message);
            txtEmail.setText(email);
            txtDate.setText(date + "     App Version : " + version);
        }
    }
}