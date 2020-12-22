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
import com.ifresh.customer.model.FriendCodeUser;
import com.ifresh.customer.model.WalletBalance;

import java.util.ArrayList;


public class FriendListReferAdapter extends RecyclerView.Adapter<FriendListReferAdapter.FriendCodeUserItemHolder> {
    Activity activity;
    ArrayList<FriendCodeUser> friendCodeUsers_list;

    public FriendListReferAdapter(Activity activity, ArrayList<FriendCodeUser> friendCodeUsers_list) {
        this.activity = activity;
        this.friendCodeUsers_list = friendCodeUsers_list;
    }

    public FriendListReferAdapter(Activity activity) {
        this.activity = activity;
    }


    @NonNull
    @Override
    public FriendCodeUserItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_friendcodelist_list, null);
        FriendCodeUserItemHolder friendCodeUserItemHolder = new FriendCodeUserItemHolder(v);
        return friendCodeUserItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendCodeUserItemHolder holder, int position) {
        FriendCodeUser friendCodeUser = friendCodeUsers_list.get(position);
        holder.tvDate_st.setText(friendCodeUser.getDate());
        holder.tv_name.setText(friendCodeUser.getName());
        holder.tv_mob.setText(friendCodeUser.getMobile_no());
    }

    @Override
    public int getItemCount(){
       return friendCodeUsers_list.size();
        //return 10;
    }

    public class FriendCodeUserItemHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tvDate_st, tv_mob;
        public FriendCodeUserItemHolder(@NonNull View itemView) {
            super(itemView);
            tvDate_st = itemView.findViewById(R.id.tvDate_st);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mob = itemView.findViewById(R.id.tv_mob);
        }
    }

}
