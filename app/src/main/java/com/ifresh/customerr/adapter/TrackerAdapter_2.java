package com.ifresh.customerr.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.ifresh.customerr.R;
import com.ifresh.customerr.activity.TrackerDetailActivity;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.model.OrderTracker;
import com.ifresh.customerr.model.OrderTracker_2;

import java.util.ArrayList;

;

public class TrackerAdapter_2 extends RecyclerView.Adapter<TrackerAdapter_2.CartItemHolder> {

    Activity activity;
    ArrayList<OrderTracker_2> orderTrackerArrayList;

    public TrackerAdapter_2(Activity activity, ArrayList<OrderTracker_2> orderTrackerArrayList) {
        this.activity = activity;
        this.orderTrackerArrayList = orderTrackerArrayList;
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_trackorder, null);
        CartItemHolder cartItemHolder = new CartItemHolder(v);
        return cartItemHolder;
    }

    @Override
    public void onBindViewHolder(final CartItemHolder holder, final int position) {
        final OrderTracker_2 order = orderTrackerArrayList.get(position);
        holder.txtorderid.setText(order.getOrder_id());

        holder.txtorderdate.setText(order.getDate_added());

        holder.carddetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, TrackerDetailActivity.class).putExtra("model", order));
            }
        });
        if (order.getStatus().equalsIgnoreCase("cancelled")) {
            holder.lyttracker.setVisibility(View.GONE);
        } else {
            if (order.getStatus().equals("returned")) {
                holder.l4.setVisibility(View.VISIBLE);
                holder.returnLyt.setVisibility(View.VISIBLE);
            }
            holder.lyttracker.setVisibility(View.VISIBLE);
            ApiConfig.setOrderTrackerLayout_2(activity, order, holder);
        }


        holder.recyclerView.setAdapter(new ItemsAdapter_2(activity, orderTrackerArrayList.get(position).itemsList));
        holder.recyclerView.setNestedScrollingEnabled(false);

    }

    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView txtorderid, txtorderdate;
        NetworkImageView imgorder;
        LinearLayout lyttracker, returnLyt;
        CardView carddetail;
        RecyclerView recyclerView;
        View l4;

        public CartItemHolder(View itemView) {
            super(itemView);
            txtorderid = itemView.findViewById(R.id.txtorderid);
            txtorderdate = itemView.findViewById(R.id.txtorderdate);
            imgorder = itemView.findViewById(R.id.imgorder);
            lyttracker = itemView.findViewById(R.id.lyttracker);
            l4 = itemView.findViewById(R.id.l4);
            returnLyt = itemView.findViewById(R.id.returnLyt);
            carddetail = itemView.findViewById(R.id.carddetail);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        }
    }


    @Override
    public int getItemCount() {

        return orderTrackerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
