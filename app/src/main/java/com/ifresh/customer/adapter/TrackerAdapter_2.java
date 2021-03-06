package com.ifresh.customer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.ifresh.customer.R;
import com.ifresh.customer.activity.ReviewRatingActivity;
import com.ifresh.customer.activity.TrackerDetailActivity;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.model.OrderTracker_2;

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

        if(order.getShow_id().equalsIgnoreCase("1608568271857"))
        {
            Log.d("yes", "yes");
        }
        else {
            Log.d("no", "no");
        }

        holder.txtorderid.setText(order.getShow_id());

        holder.txtorderdate.setText(order.getDate_added());
        holder.txtdeliverydate.setText(order.getDate_delivery());


        holder.carddetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TrackerDetailActivity.class);
                intent.putExtra("model", order);
                intent.putExtra("product_rate",order.getOrderReviewArrayList().get(position).getProduct_rate());
                intent.putExtra("delivery_boy_rate",order.getOrderReviewArrayList().get(position).getDboy_rate());
                intent.putExtra("comment",order.getOrderReviewArrayList().get(position).getComment());
                activity.startActivity(intent);
            }
        });

        if (order.getStatus().equalsIgnoreCase("delivered")&&order.getOrderReviewArrayList().get(position).getProduct_rate()=="0")
        {
            holder.carddetailReview.setVisibility(View.VISIBLE);
            holder.carddetailReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, ReviewRatingActivity.class);
                    intent.putExtra("order_id",order.getOrder_id());
                    intent.putExtra("home","inside");
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_up_in);
                }
            });
        }
        else if (order.getStatus().equalsIgnoreCase("delivered") && !(order.getOrderReviewArrayList().get(position).getProduct_rate()=="0"))
        {
            holder.cardDetailReviewView.setVisibility(View.VISIBLE);
            holder.cardDetailReviewView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity,TrackerDetailActivity.class);
                    intent.putExtra("model", order);
                    intent.putExtra("product_rate",order.getOrderReviewArrayList().get(position).getProduct_rate());
                    intent.putExtra("delivery_boy_rate",order.getOrderReviewArrayList().get(position).getDboy_rate());
                    intent.putExtra("comment",order.getOrderReviewArrayList().get(position).getComment());
                    activity.startActivity(intent);
                }
            });
        }
        else
        {
            holder.carddetailReview.setVisibility(View.GONE);
        }

        if (order.getStatus().equalsIgnoreCase("cancelled")) {
            holder.lyttracker.setVisibility(View.GONE);
            //holder.txtorderid.setTextColor(activity.getResources().getColor(R.color.red));
            holder.lytost.setVisibility(View.VISIBLE);

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
        TextView txtorderid, txtorderdate, txtdeliverydate;
        NetworkImageView imgorder;
        LinearLayout lyttracker, returnLyt,lytost;
        CardView carddetail,carddetailReview,cardDetailReviewView;
        RecyclerView recyclerView;
        View l4;

        public CartItemHolder(View itemView) {
            super(itemView);
            txtorderid = itemView.findViewById(R.id.txtorderid);
            txtorderdate = itemView.findViewById(R.id.txtorderdate);
            txtdeliverydate = itemView.findViewById(R.id.txtdeliverydate);
            imgorder = itemView.findViewById(R.id.imgorder);
            lyttracker = itemView.findViewById(R.id.lyttracker);
            lytost = itemView.findViewById(R.id.lytost);
            l4 = itemView.findViewById(R.id.l4);
            returnLyt = itemView.findViewById(R.id.returnLyt);
            carddetail = itemView.findViewById(R.id.carddetail);
            carddetailReview = itemView.findViewById(R.id.carddetailReview);
            cardDetailReviewView = itemView.findViewById(R.id.carddetailReviewView);
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
