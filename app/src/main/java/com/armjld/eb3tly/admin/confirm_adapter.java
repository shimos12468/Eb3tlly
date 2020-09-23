package com.armjld.eb3tly.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Model.ConfirmationData;
import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class confirm_adapter extends RecyclerView.Adapter<confirm_adapter.MyViewHolder> {

    Context context, context1;
    long count;
    ArrayList<ConfirmationData>ConfirmationData;
    private DatabaseReference nDatabase,confirmDatabase,uDatabase;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    public confirm_adapter(Context context, ArrayList<Model.ConfirmationData> ConfirmationData, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.ConfirmationData = ConfirmationData;
        this.context1 = context1;
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
        confirmDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("confirms");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        uDatabase.keepSynced(true);
        confirmDatabase.keepSynced(true);

        String TAG = "Confirmation Adapter";
    }

    @NonNull
    @Override
    public confirm_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_confirmation, parent, false);
        return new confirm_adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull confirm_adapter.MyViewHolder holder, final int position) {
        // --------------------------- Get Data ------------------------------ //
        String ssnURL = ConfirmationData.get(position).getSsnURL();
        String isConfirmed = ConfirmationData.get(position).getIsConfirmed();
        String id = ConfirmationData.get(position).getId();
        String date = ConfirmationData.get(position).getDate();

        // --------------------------- Set the Data in the Adapter ----------------//
        if(isValidFormat("yyyy.MM.dd HH:mm:ss", date)) {
            String startDate = date;
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

            int idiffSeconds = (int) diffSeconds;
            int idiffMinutes = (int) diffMinutes;
            int idiffHours = (int) diffHours;
            int idiffDays = (int) diffDays;
            holder.setPostDate(idiffSeconds, idiffMinutes, idiffHours, idiffDays);
        } else {
            holder.setDate(date);
        }
        holder.getUserData(id);
        holder.setSSN(ssnURL);


        // ------------------ Buttons Functions ------------------ //
        holder.btnDelete.setOnClickListener(v -> {
            uDatabase.child(id).child("isConfirmed").setValue("false");
            confirmDatabase.child(id).child("isConfirmed").setValue("false");

            notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1",id, "-MAPQWoKEfmHIQG9xv-v", "لم يتم تأكيد حسابك بسبب عدم القدرة علي التأكد من بيانات بطاقتك يمكنك التواصل معنا اذا كنت ترغب في حل المشكلة, شكرا لتفهمكم", datee, "false", "contact", UserInFormation.getUserName(), UserInFormation.getUserURL());
            nDatabase.child(id).push().setValue(Noti);

            ConfirmationData.remove(position);
            notifyItemRemoved(position);
        });

        holder.btnConfirm.setOnClickListener(v -> {
            uDatabase.child(id).child("isConfirmed").setValue("true");
            uDatabase.child(id).child("ssnURL").setValue(ssnURL);
            confirmDatabase.child(id).child("isConfirmed").setValue("true");


            notiData Noti = new notiData("VjAuarDirNeLf0pwtHX94srBMBg1",id, "-MAPQWoKEfmHIQG9xv-v", "شكرا لك, تم تأكيد حسابك بصورة البطاقة و رقم الهاتف", datee, "false", "profile",UserInFormation.getUserName(), UserInFormation.getUserURL());
            nDatabase.child(id).push().setValue(Noti);

            Toast.makeText(context, "Activated Successfully", Toast.LENGTH_SHORT).show();
            ConfirmationData.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return this.ConfirmationData.size(); }

    @Override
    public int getItemViewType(int position) { return position; }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;
        TextView txtDate, txtName, txtPhone;
        Button btnConfirm,btnDelete;
        ImageView imgSSN,imgProfilePic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;

            txtDate = myview.findViewById(R.id.txtDate);
            txtName = myview.findViewById(R.id.txtName);
            txtPhone = myview.findViewById(R.id.txtPhone);
            btnConfirm = myview.findViewById(R.id.btnConfirm);
            btnDelete = myview.findViewById(R.id.btnDelete);
            imgSSN = myview.findViewById(R.id.imgSSN);
            imgProfilePic = myview.findViewById(R.id.imgProfilePic);
        }

        @SuppressLint("SetTextI18n")
        public void setBody(String name, String phone, String email, String message, String date, String version) {

        }

        public void getUserData(String id) {
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String Name = snapshot.child("name").getValue().toString();
                    String Phone = snapshot.child("phone").getValue().toString();
                    String ppURL = snapshot.child("ppURL").getValue().toString();

                    txtName.setText(Name);
                    txtPhone.setText(Phone);
                    Picasso.get().load(Uri.parse(ppURL)).into(imgProfilePic);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        public void setPostDate(int dS, int dM, int dH, int dD) {
            String finalDate = "";
            if (dS < 60) {
                finalDate = "منذ " + dS + " ثوان";
            } else if (dS > 60 && dS < 3600) {
                finalDate = "منذ " + dM + " دقيقة";
            } else if (dS > 3600 && dS < 86400) {
                finalDate = "منذ " + dH + " ساعات";
            } else if (dS > 86400) {
                finalDate = "منذ " +dD + " ايام";
            }
            txtDate.setText(finalDate);
        }

        public void setDate(String date) {
            txtDate.setText(date);
        }

        public void setSSN(String ssnURL) {
            Picasso.get().load(Uri.parse(ssnURL)).into(imgSSN);
        }
    }

    public boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            assert date != null;
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
}