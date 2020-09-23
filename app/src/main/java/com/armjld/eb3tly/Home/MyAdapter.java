package com.armjld.eb3tly.Home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.eb3tly.Block.BlockManeger;
import com.armjld.eb3tly.Orders.OrderInfo;
import com.armjld.eb3tly.DatabaseClasses.rquests;

import com.armjld.eb3tly.Settings.Wallet.wallet;
import com.armjld.eb3tly.DatabaseClasses.caculateTime;
import com.armjld.eb3tly.Login.MainActivity;
import com.armjld.eb3tly.Orders.EditOrders;
import com.armjld.eb3tly.R;
import Model.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import Model.Data;
import Model.notiData;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

     static Context context;
    Context context1;
     long count;
     ArrayList<Data>filtersData;
     private FirebaseAuth mAuth = FirebaseAuth.getInstance();
     private DatabaseReference mDatabase;
     private DatabaseReference uDatabase;
     private DatabaseReference nDatabase;
     private DatabaseReference Database;
     private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
     private String TAG = "My Adapter";
     String uType = UserInFormation.getAccountType();
     String uId = UserInFormation.getId();
     private BlockManeger block = new BlockManeger();

     public static caculateTime _cacu = new caculateTime();


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
     String datee = sdf.format(new Date());
    int howMany = 0;

    public void addItem(int position , Data data){
        int size = filtersData.size();
        if(size > position && size != 0) {

            filtersData.set(position,data);
            notifyItemChanged(position);
            Log.i(TAG, "Filter Data Statue : " + data.getStatue());
        }
    }

    public MyAdapter(Context context, ArrayList<Data> filtersData, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.filtersData =filtersData;
        this.context1 = context1;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.item_data,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Vibrator vibe = (Vibrator) (context).getSystemService(Context.VIBRATOR_SERVICE);
        //holder.btnBid.setVisibility(View.GONE);
        // Get Post Date
        holder.lin1.setVisibility(View.GONE);
        holder.txtWarning.setVisibility(View.GONE);

        String orderID = filtersData.get(position).getId().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        String owner = filtersData.get(position).getuId().replaceAll("(^\\h*)|(\\h*$)", "").trim();
        String type = filtersData.get(position).getType();

        holder.setDate(filtersData.get(position).getDDate().replaceAll("(^\\h*)|(\\h*$)", "").trim(), filtersData.get(position).getpDate().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setOrdercash(filtersData.get(position).getGMoney().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setOrderFrom(filtersData.get(position).reStateP().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setOrderto(filtersData.get(position).reStateD().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setFee(filtersData.get(position).getGGet().replaceAll("(^\\h*)|(\\h*$)", "").trim());
        holder.setPostDate(filtersData.get(position).getDate());
        holder.setType(filtersData.get(position).getIsCar(), filtersData.get(position).getIsMotor(), filtersData.get(position).getIsMetro(), filtersData.get(position).getIsTrans());
        holder.setBid(type);

        switch (uType) {
            case "Supplier":
                holder.lin1.setVisibility(View.GONE);
                holder.txtWarning.setVisibility(View.GONE);
                holder.linAdmin.setVisibility(View.GONE);
                break;
            case "Admin":
                holder.lin1.setVisibility(View.GONE);
                holder.txtWarning.setVisibility(View.GONE);
                holder.linAdmin.setVisibility(View.VISIBLE);
                break;
            case "Delivery Worker":

                holder.lin1.setVisibility(View.VISIBLE);
                holder.txtWarning.setVisibility(View.GONE);
                holder.linAdmin.setVisibility(View.GONE);

                // ----------- Listener to Hie Buttons when order deleted or became accepted ------------ //
                mDatabase.child(orderID).child("statue").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!Objects.requireNonNull(snapshot.getValue()).toString().equals("placed")) {
                            holder.lin1.setVisibility(View.GONE);
                            holder.txtWarning.setVisibility(View.VISIBLE);

                        } else {
                            holder.lin1.setVisibility(View.VISIBLE);
                            holder.txtWarning.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
                break;
        }

        holder.linerDate.setOnClickListener(v -> {
            Toast.makeText(context,"معاد تسليم الاوردر يوم : " + holder.txtDate.getText().toString(), Toast.LENGTH_SHORT).show();
            assert vibe != null;
            vibe.vibrate(20);
        });

        holder.txtgGet.setOnClickListener(v -> {
            Toast.makeText(context, "مصاريف شحن الاوردر : "+ holder.txtgGet.getText().toString(), Toast.LENGTH_SHORT).show();
            assert vibe != null;
            vibe.vibrate(20);
        });
        holder.txtgMoney.setOnClickListener(v -> {
            Toast.makeText(context, "مقدم الاوردر : "+ holder.txtgMoney.getText().toString(), Toast.LENGTH_SHORT).show();
            assert vibe != null;
            vibe.vibrate(20);
        });


        mDatabase.child(orderID).child("requests").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    holder.setBid("true");
                } else {
                    holder.setBid("false");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        holder.btnBid.setOnClickListener(v1 -> {
            mDatabase.child(orderID).child("requests").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:

                                    // ------------------- Send Request -------------------- //
                                    rquests _rquests = new rquests();
                                    _rquests.deleteReq(uId, orderID);

                                    holder.setBid("false");

                                    Toast.makeText(context, "تم الغاء التقديم", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("هل انت متأكد من انك تريد التقديم علي هذه الشحنه ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();

                    } else {
                         wallet w = new wallet();
                         if(!w.workerbid()){
                             Toast.makeText(context1, "يجب دفع المبلغ السمتحق اولا", Toast.LENGTH_LONG).show();
                             return;
                         }

                        if(HomeActivity.requests) {
                            Toast.makeText(context1, "لا يمكنك ارسال اكثر من 10 طلبات في اليوم", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(HomeActivity.orders) {
                            Toast.makeText(context1, "لديك 20 اوردر معلق حتي الان, قم بتوصيلهم اولا", Toast.LENGTH_LONG).show();
                            return;
                        }

                        DialogInterface.OnClickListener dialogClickListener = (confirmDailog, which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:

                                    // ------------------- Send Request -------------------- //
                                    rquests _rquests = new rquests();
                                    _rquests.addrequest(orderID, datee);

                                    // ------------------ Notificatiom ------------------ //
                                    String message = "قام " + UserInFormation.getUserName() + " بالتقديم علي اوردر " + filtersData.get(position).getDName();
                                    notiData Noti = new notiData(uId, owner,orderID,message,datee,"false","order", UserInFormation.getUserName(), UserInFormation.getUserURL());
                                    nDatabase.child(owner).push().setValue(Noti);

                                    holder.setBid("true");
                                    Toast.makeText(context, "تم التقديم علي الشحنه", Toast.LENGTH_SHORT).show();

                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("هل انت متأكد من انك تريد التقديم علي هذه الشحنه ؟").setPositiveButton("نعم", dialogClickListener).setNegativeButton("لا", dialogClickListener).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        });


        //More Info Button
        holder.btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderInfo.class);
            intent.putExtra("orderID", orderID);
            intent.putExtra("owner", owner);
            ((Activity) context).startActivityForResult(intent, 1);
            OrderInfo.cameFrom = "Home Activity";
        });

        holder.icnCar.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "يمكن توصيل الاوردر بالسيارة", Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        holder.icnMetro.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "يمكن توصيل الاوردر بالمترو", Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        holder.icnMotor.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "يمكن توصيل الاوردر بالموتسكل", Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });
        holder.icnTrans.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "يمكن توصيل الاوردر بالمواصلات", Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        holder.linerDate.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context,"معاد تسليم الاوردر يوم : " + holder.txtDate.getText().toString(), Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });
        holder.txtgGet.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "مصاريف شحن الاوردر : "+ holder.txtgGet.getText().toString(), Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });

        holder.txtgMoney.setOnClickListener(v -> {
            assert vibe != null;
            Toast.makeText(context, "مقدم الاوردر : "+ holder.txtgMoney.getText().toString(), Toast.LENGTH_SHORT).show();
            vibe.vibrate(20);
        });




        holder.btnEdit.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            Intent editInt = new Intent(context, EditOrders.class);
            editInt.putExtra("orderid", orderID);
            context.startActivity(editInt);
        });

        holder.btnDelete.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            mDatabase.child(orderID).child("statue").setValue("deleted");
            Toast.makeText(context, "Order is Deleted", Toast.LENGTH_SHORT).show();
        });

        holder.btnOpen.setOnClickListener(v -> {
            uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String uPass = Objects.requireNonNull(snapshot.child("mpass").getValue()).toString();
                    String uMail = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                    mAuth.signOut();
                    Intent editInt = new Intent(context, MainActivity.class);
                    editInt.putExtra("umail", uMail);
                    editInt.putExtra("upass", uPass);
                    context.startActivity(editInt);
                    Log.i(TAG , "User Info : " + uMail + " : " + uPass);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        });

    }

    @Override
    public int getItemCount() {
        return (int) count;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public View myview;
        public Button btnMore, btnEdit,btnDelete,btnOpen, btnBid;
        public TextView txtWarning,txtgGet, txtgMoney,txtDate, txtOrderFrom,txtOrderTo,txtPostDate, txtDate2;
        public LinearLayout lin1,linerDate,linAdmin;
        public ImageView icnCar,icnMotor,icnMetro,icnTrans;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
            btnMore = myview.findViewById(R.id.btnMore);
            btnOpen = myview.findViewById(R.id.btnOpen);
            lin1 = myview.findViewById(R.id.lin1);
            linAdmin = myview.findViewById(R.id.linAdmin);
            txtWarning = myview.findViewById(R.id.txtWarning);
            linerDate = myview.findViewById(R.id.linerDate);
            txtgGet = myview.findViewById(R.id.fees);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtDate = myview.findViewById(R.id.date);
            txtDate2 = myview.findViewById(R.id.date3);
            btnDelete = myview.findViewById(R.id.btnDelete);
            btnEdit = myview.findViewById(R.id.btnEdit);
            txtOrderFrom = myview.findViewById(R.id.OrderFrom);
            txtOrderTo = myview.findViewById(R.id.orderto);
            icnCar = myview.findViewById(R.id.icnCar);
            icnMotor = myview.findViewById(R.id.icnMotor);
            icnMetro = myview.findViewById(R.id.icnMetro);
            icnTrans = myview.findViewById(R.id.icnTrans);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
            btnBid = myview.findViewById(R.id.btnBid);
        }

        public void setBid(String type) {
            if(type.equals("true")) {
                btnBid.setText("الغاء قبول الاوردر");
                btnBid.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_bad));
            } else {
                btnBid.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_defult));
                btnBid.setText("قبول الاوردر");
            }
        }

        public void setOrderFrom(String orderFrom){
            txtOrderFrom.setText(orderFrom);
        }

        public void setOrderto(String orderto){
            txtOrderTo.setText(orderto);
        }

        public void setDate (String dDate, String pDate){
            txtDate.setText(dDate);
            txtDate2.setText(pDate);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash){
            txtgMoney.setText("ثمن الرسالة : " + ordercash + " ج");
        }

        @SuppressLint("SetTextI18n")
        public void setFee(String fees) {
            txtgGet.setText("مصاريف الشحن : " + fees + " ج");
        }


        public void setType(String car, String motor, String metro, String trans) {
            if (car.equals("سياره")) {
                icnCar.setVisibility(View.VISIBLE);
            } else {
                icnCar.setVisibility(View.GONE);
            }

            if(motor.equals("موتسكل")) {
                icnMotor.setVisibility(View.VISIBLE);
            } else {
                icnMotor.setVisibility(View.GONE);
            }
            if(metro.equals("مترو")) {
                icnMetro.setVisibility(View.VISIBLE);
            } else {
                icnMetro.setVisibility(View.GONE);
            }
            if (trans.equals("مواصلات")) {
                icnTrans.setVisibility(View.VISIBLE);
            } else {
                icnTrans.setVisibility(View.GONE);
            }
        }

        public void setPostDate(String startDate) {
            txtPostDate.setText(_cacu.setPostDate(startDate));
        }
    }

}