package com.ifresh.customer.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customer.R;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.model.WalletBalance;

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
    public void onBindViewHolder(@NonNull WalletBalanceItemHolder holder, int position)
    {

        WalletBalance walletBalance = walletBalances_list.get(position);
        if (walletBalance.getWallet_status() == 1)
        {
            //active
            holder.relative_view.setBackgroundResource(R.drawable.card_shadow);
            holder.linear_exp.setVisibility(View.VISIBLE);
            //holder.img_logo.setBackgroundResource(R.drawable.active);
        }
        else if(walletBalance.getWallet_status() == 2)
        {
            // used
            holder.relative_view.setBackgroundResource(R.drawable.card_shadow);
            holder.linear_exp.setVisibility(View.VISIBLE);
            //holder.img_logo.setBackgroundResource(R.drawable.ued);
        }
        else if(walletBalance.getWallet_status() == 3)
        {
            //expiry
            holder.relative_view.setBackgroundResource(R.drawable.card_shadow_exp);
            holder.linear_exp.setVisibility(View.VISIBLE);
            holder.img_logo.setBackgroundResource(R.drawable.explogo);
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
        }
        else{
            holder.sign.setText("-");
            holder.lbl_to.setVisibility(View.GONE);
            holder.tvDate_end.setVisibility(View.GONE);
            holder.txt_valid.setText("Date:");
            holder.relative.setBackgroundColor(this.activity.getResources().getColor(R.color.red));
            holder.sign.setTextColor(this.activity.getResources().getColor(R.color.red));
            holder.tvAmt.setTextColor(this.activity.getResources().getColor(R.color.red));
          }
         holder.tvAmt.setText(" "+Constant.SETTING_CURRENCY_SYMBOL + "" + walletBalance.getAmount() );

    }

    @Override
    public int getItemCount(){
       return walletBalances_list.size();
        //return 10;
    }

    public class WalletBalanceItemHolder extends RecyclerView.ViewHolder {
        TextView tvDate_end, tvAmt, tvMessage, actype, sign,
                tvDate_st,tvexp,lbl_to,txt_valid;
        RelativeLayout relative_view,relative;
        LinearLayout linear_exp;
        ImageView img_logo;
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
            img_logo = itemView.findViewById(R.id.img_logo);
            lbl_to = itemView.findViewById(R.id.lbl_to);
            txt_valid = itemView.findViewById(R.id.txt_valid);

            //actype = itemView.findViewById(R.id.actype);
        }
    }

}
