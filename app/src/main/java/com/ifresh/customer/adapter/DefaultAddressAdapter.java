package com.ifresh.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customer.R;
import com.ifresh.customer.activity.SetDefaultAddress_2;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.model.Default_Add_model;

import java.util.ArrayList;


public class DefaultAddressAdapter extends RecyclerView.Adapter<DefaultAddressAdapter.DefaultAddItemHolder> {
    Activity activity;
    ArrayList<Default_Add_model> default_add_models_list;
    Session session;
    Context ctx;

    public DefaultAddressAdapter(Activity activity, Context ctx ,ArrayList<Default_Add_model> default_add_models_list, Session session) {
        this.activity = activity;
        this.default_add_models_list = default_add_models_list;
        this.session = session;
        this.ctx = ctx;
    }
    @NonNull
    @Override
    public DefaultAddItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_cell_default_add, null);
        DefaultAddItemHolder notificationItemHolder = new DefaultAddItemHolder(v);
        return notificationItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DefaultAddItemHolder holder, int position) {
       Default_Add_model default_add_model = default_add_models_list.get(position);
        if(default_add_model.getAddress_type().equals("0"))
        {
            holder.type.setText("ADDRESS");
        }

       if(default_add_model.getAddress_type().equals("1"))
       {
           holder.type.setText("HOME ADDRESS");
       }
       else if(default_add_model.getAddress_type().equals("2"))
       {
           holder.type.setText("OFFICE ADDRESS");
       }
       else if(default_add_model.getAddress_type().equals("3"))
       {
           holder.type.setText("OTHER ADDRESS");
       }

       String default_add = default_add_model.getAddress1() +  " " + default_add_model.getAddress2() + " "+"\n"+ "PinCode:"+" "+ default_add_model.getPincode();
       holder.txtaddress.setText(default_add);
       holder.chkbox.setChecked(default_add_model.getDefault_address());
    }

    @Override
    public int getItemCount(){
       return default_add_models_list.size();
    }

    public class DefaultAddItemHolder extends RecyclerView.ViewHolder {
        TextView type,txtaddress;
        CheckBox chkbox;
        ImageView imgdelete;
        public DefaultAddItemHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type1);
            txtaddress = itemView.findViewById(R.id.txtaddress);
            chkbox = itemView.findViewById(R.id.chHome);
            imgdelete = itemView.findViewById(R.id.imgdelete_1);


            imgdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Default_Add_model default_add_model = default_add_models_list.get(position);
                    ((SetDefaultAddress_2) ctx).ConformationView(default_add_model.getAddress_id(),default_add_model.getArea_id());
                }
            });


           chkbox.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int position = getAdapterPosition();
                   String chkstatus;
                   if(chkbox.isChecked()){
                       chkstatus="true";
                   }
                   else{
                       chkstatus="false";
                   }
                   Default_Add_model default_add_model = default_add_models_list.get(position);

                   ((SetDefaultAddress_2) ctx).callApi_updatedefultAdd(default_add_model.getAddress_id(),chkstatus, default_add_model.getArea_id());
               }
           });

        }

    }

}
