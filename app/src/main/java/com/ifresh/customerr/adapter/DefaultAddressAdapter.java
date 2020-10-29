package com.ifresh.customerr.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customerr.R;
import com.ifresh.customerr.activity.SetDefaultAddress_2;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.model.Default_Add_model;
import com.ifresh.customerr.model.WalletBalance;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ifresh.customerr.helper.Constant.ADDRESS_DELETE_MSG;


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
                    ((SetDefaultAddress_2) ctx).ConformationView(default_add_model.getAddress_id());
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
                   ((SetDefaultAddress_2) ctx).callApi_updatedefultAdd(default_add_model.getAddress_id(),chkstatus);
               }
           });





        }

    }

}
