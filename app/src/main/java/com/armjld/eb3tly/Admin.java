package com.armjld.eb3tly;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Model.Data;

public class Admin extends Activity {

    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase,mDatabase,rDatabase,vDatabase;
    private EditText txtChild,txtValue;
    private TextView txtAllOrdersCount,txtAllUsersCount,txtAllDevCount,txtAllSupCount,txtAllProfit;
    Button btnResetCounter,btnAddToUsers,btnAddToOrders,btnAccepting,btnAdding,btnAdminSignOut,btnReports,btnAddToComments,btnDeleteUser;
    Button btnMessages;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<String>();
    int supCount = 0;
    int devCount = 0;
    int profitCount = 0;
    int usedUsers = 0;

    int notCompleted = 0;
    String TAG = "Admin";

    public void onBackPressed() { }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        vDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("values");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");

        btnResetCounter = findViewById(R.id.btnResetCounter);
        btnAddToOrders = findViewById(R.id.btnAddToOrders);
        btnAddToUsers = findViewById(R.id.btnAddToUsers);
        txtChild = findViewById(R.id.txtChild);
        txtValue = findViewById(R.id.txtValue);
        txtAllOrdersCount  = findViewById(R.id.txtAllOrdersCount);
        txtAllUsersCount  = findViewById(R.id.txtAllUsersCount);
        txtAllDevCount  = findViewById(R.id.txtAllDevCount);
        txtAllSupCount  = findViewById(R.id.txtAllSupCount);
        txtAllProfit = findViewById(R.id.txtAllProfit);
        btnAccepting = findViewById(R.id.btnAccepting);
        btnAdding = findViewById(R.id.btnAdding);
        btnAdminSignOut = findViewById(R.id.btnAdminSignOut);
        btnReports = findViewById(R.id.btnReports);
        btnAddToComments = findViewById(R.id.btnAddToComments);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        btnMessages = findViewById(R.id.btnMessages);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("Admin Panel");

        // -------------------------- Check the Reports ------------------------------------//
        btnReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder theReports = new AlertDialog.Builder(Admin.this);
                LayoutInflater inflater = LayoutInflater.from(Admin.this);
                View reportsView = inflater.inflate(R.layout.admin_reports, null);
                theReports.setView(reportsView);
                AlertDialog dialog = theReports.create();
                dialog.show();

                ListView listReports = (ListView) reportsView.findViewById(R.id.listReports);
                final ArrayAdapter<String> arrayAdapterLessons = new ArrayAdapter<String>(Admin.this, R.layout.list_white_text, R.id.txtItem, mArraylistSectionLessons);
                listReports.setAdapter(arrayAdapterLessons);
                mArraylistSectionLessons.clear();

                listReports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                        String value = (String)adapter.getItemAtPosition(position);
                        Toast.makeText(Admin.this, value, Toast.LENGTH_SHORT).show();
                    }
                });

                rDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            for (DataSnapshot data : ds.getChildren()) {
                                String reportedUser = ds.getKey().toString();
                                String tempComment = data.child("comment").getValue().toString();
                                String isReported = data.child("isReported").getValue().toString();
                                if(!tempComment.equals("") && isReported.equals("true")) {
                                    mArraylistSectionLessons.add(reportedUser + "/n " + tempComment);
                                }
                                arrayAdapterLessons.notifyDataSetChanged();
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });

        // ------------------------- Delete Non Completed ---------------------------//
        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            String isCompleted = ds.child("completed").getValue().toString();
                            int deletedCount = 0;
                            if(isCompleted.equals("false")) {
                                ++deletedCount;
                                Log.i(TAG, " Users to Remove : " + ds.getValue());
                                ds.getRef().removeValue();
                            }
                            Toast.makeText(Admin.this, "Deleted Non Completed : " + deletedCount + " Users", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        // -------------------------- Signing Out of Admin Account -------------------------//
        btnAdminSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mAuth.signOut();
                                finish();
                                startActivity(new Intent(Admin.this, MainActivity.class));
                                Toast.makeText(getApplicationContext(), "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
                builder.setMessage("Are you sure you want to Sign Out ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        btnMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Admin.this, ReplyByAdmin.class));
            }
        });

        // -------------------------- Reset the cancelled orders counter for delivery workers -------------------------//
        btnResetCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int userCount = (int) dataSnapshot.getChildrenCount();
                                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String isCompleted = ds.child("completed").getValue().toString();
                                            if(isCompleted.equals("true")) {
                                                String userID = ds.child("id").getValue().toString();
                                                uDatabase.child(userID).child("canceled").setValue("0");
                                                Toast.makeText(Admin.this, "Counter Reseted for : " + userCount + " Users", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
                builder.setMessage("Are you sure you want to reset the Counter ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        // ----------------------------------------------- ADD NEW CHILD TO ALL USERS -------------------------//
        btnAddToUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txtChild.getText().toString())){
                    txtChild.setError("Can't Be EMPTY !!");
                    return;
                }
                if(TextUtils.isEmpty(txtValue.getText().toString())){
                    txtValue.setError("Can't Be EMPTY !!");
                    return;
                }
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int userCount = (int) dataSnapshot.getChildrenCount();
                                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                            if(ds.child("completed").getValue().equals("true")) {
                                                String userID = ds.child("id").getValue().toString();
                                                uDatabase.child(userID).child(txtChild.getText().toString()).setValue(txtValue.getText().toString());
                                                Toast.makeText(Admin.this, "Add Childs to : " + userCount + " Users", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
                builder.setMessage("Are you sure you want to add this child to all users ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        // -------------------------------------- ADD NEW CHILD TO ALL ORDERS ---------------------------------------//
        btnAddToOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txtChild.getText().toString())){
                    txtChild.setError("Can't Be EMPTY !!");
                    return;
                }
                if(TextUtils.isEmpty(txtValue.getText().toString())){
                    txtValue.setError("Can't Be EMPTY !!");
                    return;
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int userCount = (int) dataSnapshot.getChildrenCount();
                                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String orderID = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                            mDatabase.child(orderID).child(txtChild.getText().toString()).setValue(txtValue.getText().toString());
                                            Toast.makeText(Admin.this, "Add Childs to : " + userCount + " Orders", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
                builder.setMessage("Are you sure you want to add this child to all orders ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        // -------------------------------------- ADD NEW CHILD TO ALL COMMENTS ---------------------------------------//
        btnAddToComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(txtChild.getText().toString())){
                    txtChild.setError("Can't Be EMPTY !!");
                    return;
                }
                if(TextUtils.isEmpty(txtValue.getText().toString())){
                    txtValue.setError("Can't Be EMPTY !!");
                    return;
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                rDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot data : dataSnapshot.getChildren()) {
                                            for(DataSnapshot ds : data.getChildren()) {
                                                int ratings = (int) ds.getChildrenCount();
                                                String userRateID = Objects.requireNonNull(data.getKey().toString());
                                                Log.i(TAG, "User Id " + userRateID);
                                                String rateID = Objects.requireNonNull(ds.child("rId").getValue()).toString();
                                                Log.i(TAG, "Comment Id " + rateID);
                                                rDatabase.child(userRateID).child(rateID).child(txtChild.getText().toString()).setValue(txtValue.getText().toString());
                                                Toast.makeText(Admin.this, "Added Childs to : " + ratings + " Comments", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
                builder.setMessage("Are you sure you want to add this child to all comments ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        // ----------------------------------- Disable / Enable Adding Orders ---------------------------//
        btnAdding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child("adding").getValue().toString().equals("true")) {
                                            vDatabase.child("adding").setValue("false");
                                        } else if(dataSnapshot.child("adding").getValue().toString().equals("false")) {
                                            vDatabase.child("adding").setValue("true");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
                builder.setMessage("Are You Sure ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        // -------------------------------------- Disable / Enable Accepting Orders ---------------------------//
        btnAccepting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface confirmDailog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                vDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(Objects.requireNonNull(dataSnapshot.child("accepting").getValue()).toString().equals("true")) {
                                            vDatabase.child("accepting").setValue("false");
                                        } else if(Objects.requireNonNull(dataSnapshot.child("accepting").getValue()).toString().equals("false")) {
                                            vDatabase.child("accepting").setValue("true");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
                builder.setMessage("Are You Sure ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
            }
        });

        // ----------------------------

    }

    @Override
    protected void onStart () {
        super.onStart();

        // -------------------------------------- Get users Counts --------------------------//
        uDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int allUsers = (int) dataSnapshot.getChildrenCount();
                usedUsers = 0;
                supCount = 0;
                devCount = 0;
                notCompleted = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String strCompleted = ds.child("completed").getValue().toString();
                    if(strCompleted.equals("true")) {
                        String userType = ds.child("accountType").getValue().toString();
                        int intProfit = (int) Integer.parseInt(Objects.requireNonNull(ds.child("profit").getValue()).toString());
                        profitCount = profitCount + Integer.parseInt(Objects.requireNonNull(ds.child("profit").getValue()).toString());
                        if(intProfit > 0) {
                            ++usedUsers;
                        }
                        switch (userType) {
                            case "Supplier" : {
                                ++supCount;
                                break;
                            }
                            case "Delivery Worker" : {
                                ++devCount;
                                break;
                            }
                        }
                    } else {
                        ++notCompleted;
                    }
                }
                int forEach = 0;
                if(usedUsers != 0) {
                    forEach = profitCount / usedUsers;
                }
                txtAllUsersCount.setText("Users Count : " + allUsers + " | Completed " + (allUsers - notCompleted) + " | Not Completed : " + notCompleted);
                txtAllProfit.setText("Total Profit = " + profitCount + " EGP | " + forEach + " EGP For Each Active Delivery User");
                txtAllSupCount.setText("Suppliers Count : " + supCount);
                txtAllDevCount.setText("Delivery Workers Count : " + devCount + " | Active Count : " + usedUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // ----------------------------------------- Get orders Counts --------------------------------//
        mDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int allOrders = 0;
                int ordersWorth = 0;
                int acOrders = 0;
                int plOrders = 0;
                int deOrders = 0;
                int reOrders = 0;
                    if(dataSnapshot.exists()) {
                        allOrders = (int) dataSnapshot.getChildrenCount();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            if(ds.exists()) {
                                Data orderData = ds.getValue(Data.class);
                                assert orderData != null;
                                ordersWorth = ordersWorth + Integer.parseInt(orderData.getGMoney().toString());
                                switch (orderData.getStatue()) {
                                    case "placed":
                                        plOrders++;
                                        break;
                                    case "accepted":
                                        acOrders++;
                                        break;
                                    case "recived":
                                        reOrders++;
                                        break;
                                    case "delivered":
                                        deOrders++;
                                        break;
                                }
                            }
                        }
                    }
                txtAllOrdersCount.setText("We Have " + allOrders + " Orders in Our System | Worth : " + ordersWorth + " EGP | " + plOrders + " Placed | " + acOrders + " Accepted | " + reOrders + " Recived | " + deOrders + " Delivered." );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // --------------------------------------- Changing Button name Depending on Values ---------------------------//
        vDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String isAccepting = dataSnapshot.child("accepting").getValue().toString();
                String isAdding = dataSnapshot.child("adding").getValue().toString();

                if(isAccepting.equals("true")) {
                    btnAccepting.setText("Disable Accepting");
                } else if(isAccepting.equals("false")){
                    btnAccepting.setText("Enable Accepting");
                }
                if(isAdding.equals("true")) {
                    btnAdding.setText("Disable Adding");
                } else if(isAdding.equals("false")) {
                    btnAdding.setText("Enable Adding");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //-----------------------------

    }
}
