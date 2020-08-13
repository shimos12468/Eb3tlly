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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import Model.notiData;
import Model.reportData;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class reportsAdapter extends RecyclerView.Adapter<reportsAdapter.MyViewHolder> {

    Context context, context1;
    long count;
    ArrayList<Model.reportData> reportData;
    private DatabaseReference nDatabase,cDatabase,uDatabase,mDatabase,reportDatabase;
    String datee = DateFormat.getDateInstance().format(new Date());
    String whoPhone, whoName;
    String rWhoPhone, rWhoName;
    String delvMoney,money,from,to,orderDate,ddate;
    String TAG = "Reports Adapter";

    public reportsAdapter(Context context, ArrayList<Model.reportData> reportData, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.reportData = reportData;
        this.context1 = context1;
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        cDatabase = getInstance().getReference().child("Pickly").child("messages");
        uDatabase = getInstance().getReference().child("Pickly").child("users");
        mDatabase = getInstance().getReference().child("Pickly").child("orders");
        reportDatabase = getInstance().getReference().child("Pickly").child("reports");
        Log.i(TAG, " Reached Reports Adapter");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_report, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        reportData data = reportData.get(position);
        String whoID = data.getUserID();
        String reportWhoID = data.getReportedID();
        String orderID = data.getOrderID();
        String date = data.getDate();
        String type = data.getType();
        String id = data.getId();

        // ----------------------- Getting Users Info ---------------------- //
        uDatabase.child(whoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                whoName = snapshot.child("name").getValue().toString();
                whoPhone = snapshot.child("phone").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // ----------------------- Getting Users Info ---------------------- //
        uDatabase.child(reportWhoID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    rWhoName = snapshot.child("name").getValue().toString();
                    rWhoPhone = snapshot.child("phone").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        // ------------------------ Getting Order Data ------------------------//
        mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() >= 30) {
                    delvMoney = snapshot.child("gget").getValue().toString();
                    money = snapshot.child("gmoney").getValue().toString();
                    from = (snapshot.child("txtPState").getValue().toString() + snapshot.child("mPRegion").getValue().toString() + snapshot.child("mPAddress").getValue().toString());
                    to = (snapshot.child("txtDState").getValue().toString() + snapshot.child("mDRegion").getValue().toString() + snapshot.child("daddress").getValue().toString());
                    orderDate = snapshot.child("date").getValue().toString();
                    ddate = snapshot.child("ddate").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        String whoData = whoName + " | " + whoPhone;
        String rWhoData = rWhoName + " | " + rWhoPhone;
        String orderInfo = "Delv Money : " + delvMoney + " | Order Money : " + money + " | Order From : " + from + " | Order To : " + to + " | Placed order Date : " + orderDate + " | Delvery Data : " + ddate;

        holder.btnClose.setOnClickListener(v -> {
            reportDatabase.child(reportWhoID).child(id).removeValue();
            reportData.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeRemoved(position, reportData.size());
            Toast.makeText(context, "Report Removed", Toast.LENGTH_SHORT).show();
        });

        holder.btnDeactive.setOnClickListener(v -> {
            uDatabase.child(reportWhoID).child("active").setValue("false");
            Toast.makeText(context, "User Deactivated", Toast.LENGTH_SHORT).show();
        });

        holder.btnDelete.setOnClickListener(v -> {
            // ------------ Delete the Orders Notfications ------------------- //
            nDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(ds.exists()) {
                                for(DataSnapshot sn : ds.getChildren()) {
                                    if(sn.child("orderid").exists()) {
                                        String orderI = Objects.requireNonNull(Objects.requireNonNull(sn.child("orderid").getValue()).toString());
                                        if(orderI.equals(orderID)) {
                                            sn.getRef().removeValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

            mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().removeValue();
                    Toast.makeText(context, "Order Deleted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });

        holder.btnDelivered.setOnClickListener(v -> {
            mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("uAccepted").getValue().toString().equals(whoID) ||snapshot.child("uAccepted").getValue().toString().equals(reportWhoID)) {
                        mDatabase.child(orderID).child("statue").setValue("delivered");
                        Toast.makeText(context, "Order Set To delivered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });

        holder.btnPlaced.setOnClickListener(v -> {
            mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("uAccepted").getValue().toString().equals(whoID) ||snapshot.child("uAccepted").getValue().toString().equals(reportWhoID)) {
                        mDatabase.child(orderID).child("statue").setValue("placed");
                        mDatabase.child(orderID).child("uAccepted").setValue("");
                        mDatabase.child(orderID).child("acceptTime").setValue("");
                        Toast.makeText(context, "Order Set To Placed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });

        holder.btnReply.setOnClickListener(v -> {
            notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1",whoID, "-MAPQWoKEfmHIQG9xv-v", holder.txtReply.getText().toString(), datee, "false", "contact");
            nDatabase.child(whoID).push().setValue(Noti);
            Toast.makeText(context, "Reply Send Successful", Toast.LENGTH_SHORT).show();
        });

        holder.btnRecived.setOnClickListener(v -> {
            mDatabase.child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("uAccepted").getValue().toString().equals(whoID) ||snapshot.child("uAccepted").getValue().toString().equals(reportWhoID)) {
                        mDatabase.child(orderID).child("statue").setValue("recived");
                        Toast.makeText(context, "Order Set To Recived", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        });

        holder.setBody(date, whoData, rWhoData, type, orderID, orderInfo);
    }

    @Override
    public int getItemCount() {
        return this.reportData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        ImageButton btnClose;
        Button btnReply,btnRecived,btnDelete,btnPlaced,btnDelivered,btnDeactive;
        TextView txtDate,txtWho,txtReportedWho,ReportType,txtorderID,txtorderInfo;
        EditText txtReply;

        @SuppressLint("CutPasteId")
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;

            btnReply = myview.findViewById(R.id.btnReply);
            btnRecived = myview.findViewById(R.id.btnRecived);
            btnDelete = myview.findViewById(R.id.btnDelete);
            btnPlaced = myview.findViewById(R.id.btnPlaced);
            btnDelivered = myview.findViewById(R.id.btnDelivered);
            btnDeactive = myview.findViewById(R.id.btnDeactive);
            btnClose = myview.findViewById(R.id.btnClose);

            txtReply = myview.findViewById(R.id.txtReply);

            txtDate = myview.findViewById(R.id.txtDate);
            txtWho = myview.findViewById(R.id.txtWho);
            txtReportedWho = myview.findViewById(R.id.txtReportedWho);
            ReportType = myview.findViewById(R.id.ReportType);
            txtorderID = myview.findViewById(R.id.orderID);
            txtorderInfo = myview.findViewById(R.id.orderInfo);
        }

        @SuppressLint("SetTextI18n")
        public void setBody(String date, String whoData,String rWhoData,String type,String orderID,String orderInfo) {
            txtDate.setText(date);
            txtWho.setText(whoData);
            txtReportedWho.setText(rWhoData);
            ReportType.setText(type);
            txtorderID.setText(orderID);
            txtorderInfo.setText(orderInfo);
        }
    }
}
