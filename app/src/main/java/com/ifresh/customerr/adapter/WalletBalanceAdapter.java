package com.ifresh.customerr.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customerr.R;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.model.WalletBalance;

import java.util.ArrayList;


public class WalletBalanceAdapter extends RecyclerView.Adapter<WalletBalanceAdapter.WalletBalanceItemHolder> {
    Activity activity;
    ArrayList<WalletBalance> walletBalances_list;

    public WalletBalanceAdapter(Activity activity, ArrayList<WalletBalance> walletBalances_list) {
        this.activity = activity;
        this.walletBalances_list = walletBalances_list;
    }

    public WalletBalanceAdapter(Activity activity) {
        this.activity = activity;
    }


    @NonNull
    @Override
    public WalletBalanceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_walletbalance_list_2, null);
        WalletBalanceItemHolder notificationItemHolder = new WalletBalanceItemHolder(v);
        return notificationItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WalletBalanceItemHolder holder, int position) {

        WalletBalance walletBalance = walletBalances_list.get(position);
        if(walletBalance.getWallet_status() == 0)
        {
            //expiry
            holder.relative_view.setBackgroundResource(R.drawable.card_shadow_exp);
            holder.linear_exp.setVisibility(View.VISIBLE);
        }
        else if (walletBalance.getWallet_status() == 1)
        {
            //active
            holder.relative_view.setBackgroundResource(R.drawable.card_shadow);
            holder.linear_exp.setVisibility(View.GONE);

        }
        holder.tvDate_st.setText(walletBalance.getDate());
        holder.tvDate_end.setText(walletBalance.getExpdate());
        holder.tvMessage.setText(walletBalance.getMessage());
        if(walletBalance.getActype().equalsIgnoreCase("credit"))
        {
            holder.sign.setText("+");
            holder.relative.setBackgroundColor(this.activity.getResources().getColor(R.color.colorPrimary));
            holder.sign.setTextColor(this.activity.getResources().getColor(R.color.green));
            holder.tvAmt.setTextColor(this.activity.getResources().getColor(R.color.green));
            holder.tvAmt.setText(" "+Constant.SETTING_CURRENCY_SYMBOL + "" + walletBalance.getAmount() );
        }
        else{
            holder.sign.setText("-");
            holder.relative.setBackgroundColor(this.activity.getResources().getColor(R.color.red));
            holder.sign.setTextColor(this.activity.getResources().getColor(R.color.red));
            holder.tvAmt.setTextColor(this.activity.getResources().getColor(R.color.red));
            holder.tvAmt.setText(" "+Constant.SETTING_CURRENCY_SYMBOL + "" + walletBalance.getAmount() );
        }

    }

    @Override
    public int getItemCount(){
       return walletBalances_list.size();
        //return 10;
    }

    public class WalletBalanceItemHolder extends RecyclerView.ViewHolder {
        TextView tvDate_end, tvAmt, tvMessage, actype, sign,
                tvDate_st,tvexp;
        RelativeLayout relative_view,relative;
        LinearLayout linear_exp;
        public WalletBalanceItemHolder(@NonNull View itemView) {
            super(itemView);
            tvDate_st = itemView.findViewById(R.id.tvDate_st);
            tvDate_end = itemView.findViewById(R.id.tvDate_end);
            tvAmt = itemView.findViewById(R.id.tvAmt);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvexp = itemView.findViewById(R.id.tvexp);
            sign = itemView.findViewById(R.id.sign);
            relative_view = itemView.findViewById(R.id.relative_view);
            relative = itemView.findViewById(R.id.relative);
            linear_exp = itemView.findViewById(R.id.linear_exp);
            //actype = itemView.findViewById(R.id.actype);
        }
    }

}