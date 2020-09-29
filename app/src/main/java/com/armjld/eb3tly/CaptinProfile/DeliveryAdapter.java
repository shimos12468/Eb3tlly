package com.armjld.eb3tly.CaptinProfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.armjld.eb3tly.Chat.chatListclass;
import com.armjld.eb3tly.Home.HomeActivity;
import com.armjld.eb3tly.Orders.AddOrders;
import com.armjld.eb3tly.Orders.OrderInfo;
import com.armjld.eb3tly.R;
import com.armjld.eb3tly.DatabaseClasses.Ratings;
import Model.UserInFormation;
import com.armjld.eb3tly.Settings.Wallet.wallet;
import com.armjld.eb3tly.DatabaseClasses.caculateTime;
import com.armjld.eb3tly.Chat.Messages;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import Model.Data;
import Model.notiData;
import Model.rateData;
import Model.reportData;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context;
    ArrayList<Data>filtersData;
    private DatabaseReference mDatabase,rDatabase,uDatabase,nDatabase,reportDatabase;
    public static String TAG = "Supplier Adapter";
    String uId = UserInFormation.getId();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    private static final int PHONE_CALL_CODE = 100;
    public static caculateTime _cacu = new caculateTime();
    ReviewInfo reviewInfo;

    public void addItem(int position , Data data){
        int size = filtersData.size();
        if(size > position && size != 0) {
            filtersData.set(position,data);
            notifyItemChanged(position);
            Log.i(TAG, "Filter Data Statue : " + data.getStatue());
        }
    }

    public DeliveryAdapter(Context context, ArrayList<Data> filtersData) {
        this.context = context;
        this.filtersData = filtersData;

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");
        reportDatabase = getInstance().getReference().child("Pickly").child("reports");

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.delveryitem,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {
        Vibrator vibe = (Vibrator) Objects.requireNonNull(context).getSystemService(Context.VIBRATOR_SERVICE);
        Data data = filtersData.get(position);

        String orderID = data.getId();
        String owner = data.getuId();

        // Get Post Date
        String startDate = Objects.requireNonNull(data.getDate());

        holder.setDate(data.getDDate());
        holder.setUsername(data.getuId());
        holder.setOrdercash(data.getGMoney());
        holder.setOrderFrom(data.reStateP());
        holder.setOrderto(data.reStateD());
        holder.setFee(data.getGGet());
        holder.setPostDate(startDate);
        holder.setDilveredButton(data.getStatue());
        holder.setRateButton(data.getSrated(), data.getStatue());
        holder.setType(data.getIsCar(), data.getIsMotor(), data.getIsMetro(), data.getIsTrans());
        holder.checkDeleted(data.getRemoved());


        // ------------------------------------   Order info
        holder.btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderInfo.class);
            intent.putExtra("orderID", orderID);
            intent.putExtra("owner", owner);
            ((Activity) context).startActivityForResult(intent, 1);
            OrderInfo.cameFrom = "Profile";
        });

        // ---------------- Set order to Recived
        holder.btnRecived.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context).setMessage("هل قمت باستلام الاوردر من التاجر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                mDatabase.child(orderID).child("statue").setValue("recived2");
                mDatabase.child(orderID).child("recived2Time").setValue(datee);

                String message = "قام " + UserInFormation.getUserName() + " بتأكد استلام الاوردر";
                notiData Noti = new notiData(uId, owner, orderID,message,datee,"false", "profile", UserInFormation.getUserName(), UserInFormation.getUserURL());
                nDatabase.child(owner).push().setValue(Noti);

                Toast.makeText(context, "تم تأكيد استلام الشحنة", Toast.LENGTH_SHORT).show();

                filtersData.get(position).setStatue("recived2");
                holder.setDilveredButton("recived2");

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();
        });

        // -----------------------   Set ORDER as Delivered
        holder.btnDelivered.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context).setMessage("هل قمت بتسليم الاوردر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // Changing the values in the orders db
                mDatabase.child(orderID).child("statue").setValue("delivered");
                mDatabase.child(orderID).child("dilverTime").setValue(datee);

                // ----- Add money to the Wallet
                wallet w = new wallet();
                w.SupsetDilivared(orderID, filtersData.get(position).getGGet());

                // --------------------------- Send Notifications ---------------------//
                String message =  "قام " + UserInFormation.getUserName() + " بتوصل الاردر";

                notiData Noti = new notiData(uId, owner,orderID,message,datee,"false","profile", UserInFormation.getUserName(), UserInFormation.getUserURL());
                nDatabase.child(owner).push().setValue(Noti);

                Toast.makeText(context, "تم توصيل الاوردر", Toast.LENGTH_SHORT).show();
                vibe.vibrate(20);
                //context.startActivity(new Intent(context, NewProfile.class));

                filtersData.get(position).setStatue("delivered");
                holder.setDilveredButton("delivered");

                chatListclass _ch = new chatListclass();
                _ch.dlevarychat(owner);

                ViewPager viewPager = ((HomeActivity) context).findViewById(R.id.view_pager);
                viewPager.setCurrentItem(1, true);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();
        });

        holder.btnOrderBack.setOnClickListener(v-> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context).setMessage("لم يستلم العميل الشحنة ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {

                mDatabase.child(orderID).child("statue").setValue("denied");
                mDatabase.child(orderID).child("dilverTime").setValue(datee);

                String message = "قام " + filtersData.get(position).getDName() + " برفض استلام اوردرك من الكابتن.";
                notiData Noti = new notiData(uId, owner, orderID, message, datee,"false", "profile", UserInFormation.getUserName(), UserInFormation.getUserURL());
                nDatabase.child(owner).push().setValue(Noti);

                // ----- Add money to the Wallet
                wallet w = new wallet();
                w.SupsetDilivared(orderID, filtersData.get(position).getGGet());

                filtersData.get(position).setStatue("denied");
                holder.setDilveredButton("denied");

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> {
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();
        });


        // -----------------------  Comment button
        holder.btnRate.setOnClickListener(v -> {
            AlertDialog.Builder myRate = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View dialogRate = inflater.inflate(R.layout.dialograte, null);
            myRate.setView(dialogRate);
            final AlertDialog dialog = myRate.create();
            dialog.show();

            TextView tbTitle = dialogRate.findViewById(R.id.toolbar_title);
            tbTitle.setText("تقييم التاجر");

            ImageView btnClose = dialogRate.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(v1 -> {
                dialog.dismiss();
                assert vibe != null;
                vibe.vibrate(20);
            });

            Button btnSaveRate = dialogRate.findViewById(R.id.btnSaveRate);
            final EditText txtRate = dialogRate.findViewById(R.id.drComment);
            final RatingBar drStar = dialogRate.findViewById(R.id.drStar);
            final TextView txtReport = dialogRate.findViewById(R.id.txtReport);

            // -------------- Make suer that the minmum rate is 1 star --------------------//
            drStar.setOnRatingBarChangeListener((drStar1, rating, fromUser) -> {
                if(rating<1.0f) {
                    drStar1.setRating(1.0f);
                } else if (rating == 1.0f) {
                    txtReport.setVisibility(View.VISIBLE);
                } else {
                    txtReport.setVisibility(View.GONE);
                }
            });

            btnSaveRate.setOnClickListener(v15 -> {
                assert vibe != null;
                vibe.vibrate(20);
                final String rRate = txtRate.getText().toString().trim();
                final String rId = rDatabase.push().getKey();
                final int intRating = (int) drStar.getRating();
                assert rId != null;

                rateData data1 = new rateData(rId, orderID, owner ,uId, intRating,rRate , datee);
                rDatabase.child(owner).child(rId).setValue(data1);

                mDatabase.child(orderID).child("srated").setValue("true");
                mDatabase.child(orderID).child("srateid").setValue(rId);

                Ratings _rate = new Ratings();
                _rate.setRating(owner, intRating);

                holder.setRateButton("true", filtersData.get(position).getStatue());

                Toast.makeText(context, "تم التقييم بالنجاح", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        // ----------------------- Toasts ------------------- //
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

        holder.btnChat.setOnClickListener(v-> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(uId,owner,context);
            Messages.cameFrom = "Profile";
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


        // --------- Report for Delvery
        holder.mImageButton.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            PopupMenu popup = new PopupMenu(context,v );
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_menu_delv, popup.getMenu());
            Menu popupMenu = popup.getMenu();

            switch (data.getStatue()) {
                case "accepted" : {
                    popupMenu.findItem(R.id.idelv).setVisible(true);
                    popupMenu.findItem(R.id.falsemoney).setVisible(false);
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(false);
                    break;
                }
                case "recived" : {
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(true);
                    popupMenu.findItem(R.id.falsemoney).setVisible(false);
                    popupMenu.findItem(R.id.idelv).setVisible(false);
                    break;
                }
                case "delivered" : {
                    popupMenu.findItem(R.id.falsemoney).setVisible(true);
                    popupMenu.findItem(R.id.idelv).setVisible(false);
                    popupMenu.findItem(R.id.didnt_reciv).setVisible(false);
                    break;
                }
            }

            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.falsemoney:
                        DialogInterface.OnClickListener dialogClickListener2 = (dialog, which) -> {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String id = reportDatabase.child(data.getuId()).push().getKey();
                                    reportData repo4 = new reportData(uId, data.getuId(),orderID,datee,"التاجر اخل بالاتفاق", id);
                                    assert id != null;
                                    reportDatabase.child(data.getuId()).child(id).setValue(repo4);
                                    Toast.makeText(context, "تم تقديم البلاغ", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setMessage("هل انت متاكد من انك تريد تقديم البلاغ ؟").setPositiveButton("نعم", dialogClickListener2).setNegativeButton("لا", dialogClickListener2).show();
                        break;
                    case R.id.idelv:
                        DialogInterface.OnClickListener dialogClickListener4 = (dialog, which) -> {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String id = reportDatabase.child(data.getuId()).push().getKey();
                                    reportData repo6 = new reportData(uId, data.getuId(),orderID,datee,"وصلت الاوردر و زر تم التوصيل غير موجود", id);
                                    assert id != null;
                                    reportDatabase.child(data.getuId()).child(id).setValue(repo6);
                                    Toast.makeText(context, "تم تقديم البلاغ", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder4 = new AlertDialog.Builder(context);
                        builder4.setMessage("هل انت متاكد من انك تريد تقديم البلاغ ؟").setPositiveButton("نعم", dialogClickListener4).setNegativeButton("لا", dialogClickListener4).show();
                        break;
                    case R.id.didnt_reciv:
                        DialogInterface.OnClickListener dialogClickListener5 = (dialog, which) -> {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    String id = reportDatabase.child(data.getuId()).push().getKey();
                                    reportData repo7 = new reportData(uId, data.getuId(),orderID,datee,"لم استلم الاوردر بعد", id);
                                    assert id != null;
                                    reportDatabase.child(data.getuId()).child(id).setValue(repo7);
                                    Toast.makeText(context, "تم تقديم البلاغ", Toast.LENGTH_SHORT).show();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                        AlertDialog.Builder builder5 = new AlertDialog.Builder(context);
                        builder5.setMessage("هل انت متاكد من انك تريد تقديم البلاغ ؟").setPositiveButton("نعم", dialogClickListener5).setNegativeButton("لا", dialogClickListener5).show();
                        break;
                }
                return false;
            });
            popup.show();
        });

        // -----------------------  Delete order for Delivery
        holder.btnDelete.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            Intent deleteAct = new Intent(context, Delete_Reaon_Delv.class);
            deleteAct.putExtra("orderid", orderID);
            deleteAct.putExtra("owner", data.getuId());
            deleteAct.putExtra("aTime", data.getAcceptedTime());
            deleteAct.putExtra("eTime", data.getLastedit());
            ((Activity)context).startActivityForResult(deleteAct, 1);
        });
    }

    @Override
    public int getItemCount() {
        return filtersData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ((HomeActivity)context).onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public Button btnDelete,btnInfo,btnDelivered,btnRate,btnChat,btnRecived,btnOrderBack;
        public TextView txtRate,txtGetStat,txtgGet, txtgMoney,txtDate, txtUsername, txtOrderFrom, txtOrderTo,txtPostDate;
        public LinearLayout linerDate, linerAll;
        public ImageView icnCar,icnMotor,icnMetro,icnTrans;
        public ImageButton mImageButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myview = itemView;

            btnDelivered = myview.findViewById(R.id.btnDelivered);
            btnInfo = myview.findViewById(R.id.btnInfo);
            btnDelete = myview.findViewById(R.id.btnDelete);
            btnRate = myview.findViewById(R.id.btnRate);
            txtRate = myview.findViewById(R.id.drComment);
            txtGetStat = myview.findViewById(R.id.txtStatue);
            linerAll = myview.findViewById(R.id.linerAll);
            btnChat = myview.findViewById(R.id.btnChat);
            btnRecived = myview.findViewById(R.id.btnRecived);
            linerDate = myview.findViewById(R.id.linerDate);
            txtgGet = myview.findViewById(R.id.fees);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtDate = myview.findViewById(R.id.date);
            mImageButton = myview.findViewById(R.id.imageButton);
            txtUsername = myview.findViewById(R.id.txtUsername);
            icnCar = myview.findViewById(R.id.icnCar);
            icnMotor = myview.findViewById(R.id.icnMotor);
            icnMetro = myview.findViewById(R.id.icnMetro);
            icnTrans = myview.findViewById(R.id.icnTrans);
            txtOrderFrom = myview.findViewById(R.id.OrderFrom);
            txtOrderTo = myview.findViewById(R.id.orderto);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
            btnOrderBack = myview.findViewById(R.id.btnOrderBack);
        }


        void setUsername(final String orderOwner){
           uDatabase.child(orderOwner).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        txtUsername.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }


        public void setOrderFrom(String orderFrom){
            txtOrderFrom.setText(orderFrom);
        }

        public void setRateButton(String rated, String statue) {
            switch (rated){
                case "true" : {
                    btnRate.setVisibility(View.GONE);
                    break;
                }
                case "false" : {
                    btnRate.setVisibility(View.GONE);
                    if (statue.equals("delivered") || statue.equals("deniedback")) {
                        btnRate.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
        }

        public void setDilveredButton(String state) {
            btnRate.setText("تقييم التاجر");
            switch (state) {
                case "accepted" : {
                    btnDelete.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnChat.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtGetStat.setText("تواصل مع التاجر لاستلام الاوردر");
                    txtGetStat.setBackgroundColor(Color.RED);
                    break;
                }
                case "recived" : {
                    btnDelete.setVisibility(View.GONE);
                    btnChat.setVisibility(View.VISIBLE);
                    btnDelivered.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.VISIBLE);
                    txtGetStat.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtGetStat.setText("تم استلام الاوردر من التاجر");
                    txtGetStat.setBackgroundColor(Color.parseColor("#ffc922"));
                    break;
                }

                case "recived2" : {
                    btnDelete.setVisibility(View.GONE);
                    btnChat.setVisibility(View.VISIBLE);
                    btnDelivered.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.VISIBLE);
                    txtGetStat.setText("تم استلام الاوردر من التاجر");
                    txtGetStat.setBackgroundColor(Color.parseColor("#ffc922"));
                    break;
                }

                case "delivered" :
                case "denied" :
                case "deniedback" : {
                    btnDelivered.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                    btnChat.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtGetStat.setText("تم توصيل الاوردر بنجاح");
                    txtGetStat.setBackgroundColor(Color.parseColor("#4CAF50"));
                    break;
                }
            }
        }

        public void setOrderto(String orderto){
            txtOrderTo.setText(orderto);
        }

        public void setDate (String date){
            txtDate.setText(date);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash){
            txtgMoney.setText(ordercash + " ج");
        }

        @SuppressLint("SetTextI18n")
        public void setFee(String fees) {
            txtgGet.setText(fees + " ج");
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

        public void checkDeleted(String removed) {
            if(removed.equals("true")) {
                linerAll.setVisibility(View.GONE);
            } else {
                linerAll.setVisibility(View.VISIBLE);
            }
        }
    }
}
