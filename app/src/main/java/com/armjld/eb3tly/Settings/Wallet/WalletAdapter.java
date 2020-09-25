package com.armjld.eb3tly.Settings.Wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.eb3tly.DatabaseClasses.caculateTime;
import com.armjld.eb3tly.R;

import java.util.ArrayList;

import Model.Data;

public class WalletAdapter extends  RecyclerView.Adapter<WalletAdapter.ViewHolder> {
    Context context;
    ArrayList<Data>orderData;
    public static String TAG = "Wallet Adapter";
    public static caculateTime _cacu = new caculateTime();



    public WalletAdapter(ArrayList<Data> orderData, Context context) {
        this.context = context;
        this.orderData = orderData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_wallet, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String orderFrom = orderData.get(position).getmPRegion();
        String orderTo = orderData.get(position).getmDRegion();
        String dDate =  _cacu.setPostDate(orderData.get(position).getDilverTime());

        holder.txtDate.setText(dDate);
        holder.txtOrderTo.setText(orderTo);
        holder.txtOrderFrom.setText(orderFrom);
    }


    private int caculateMoney(String gGet) {
        int orderMoney = Integer.parseInt(gGet);
        float precnt = (float) 0.2;
        return (int) (orderMoney * precnt);
    }

    @Override
    public int getItemCount() {
        return this.orderData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public TextView txtDate,txtOrderFrom,txtOrderTo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            txtDate = myview.findViewById(R.id.txtDate);
            txtOrderFrom = myview.findViewById(R.id.txtOrderFrom);
            txtOrderTo = myview.findViewById(R.id.txtOrderTo);
        }
    }
}
