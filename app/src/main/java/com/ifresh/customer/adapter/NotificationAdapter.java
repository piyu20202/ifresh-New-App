package com.ifresh.customer.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import com.ifresh.customer.R;
import com.ifresh.customer.model.Notification_2;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationItemHolder> {
    Activity activity;
    ArrayList<Notification_2> notifications;
    Integer val;

    public NotificationAdapter(Activity activity, ArrayList<Notification_2> notifications, int val) {
        this.activity = activity;
        this.notifications = notifications;
        this.val = val;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_notification_list, null);
        NotificationItemHolder notificationItemHolder = new NotificationItemHolder(v);
        return notificationItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationItemHolder holder, int position) {

        Notification_2 notification = notifications.get(position);
        if(val == 1)
        {
            //general_msg
            if(notification.getIs_general())
            {
                holder.tvTitle.setText(Html.fromHtml(notification.getMtitle()));
                holder.tvMessage.setText(Html.fromHtml(notification.getMbody()));
            }
        }
        else if(val == 2){
            //personal msg
            if(!notification.getIs_general())
            {
                holder.tvTitle.setText(Html.fromHtml(notification.getMtitle()));
                holder.tvMessage.setText(Html.fromHtml(notification.getMbody()));
            }
        }
        else if(val == 0)
        {
            holder.tvTitle.setText(Html.fromHtml(notification.getMtitle()));
            holder.tvMessage.setText(Html.fromHtml(notification.getMbody()));

        }




    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationItemHolder extends RecyclerView.ViewHolder {

        NetworkImageView image;
        TextView tvTitle, tvMessage;


        public NotificationItemHolder(@NonNull View itemView) {
            super(itemView);
            //image = itemView.findViewById(R.id.image);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }

}
