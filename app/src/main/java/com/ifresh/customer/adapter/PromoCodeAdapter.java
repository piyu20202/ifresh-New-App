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
import com.ifresh.customer.activity.PromoCodeList;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.model.WalletBalance;

import java.util.ArrayList;


public class PromoCodeAdapter extends RecyclerView.Adapter<PromoCodeAdapter.PromoCodeItemHolder> {
    Activity activity;
    ArrayList<PromoCode> promoCodeArrayList;

    public PromoCodeAdapter(Activity activity, ArrayList<PromoCode> promoCodeArrayList) {
        this.activity = activity;
        this.promoCodeArrayList = promoCodeArrayList;
    }




    @NonNull
    @Override
    public PromoCodeItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_coupan, null);
        PromoCodeItemHolder promoCodeItemHolder = new PromoCodeItemHolder(v);
        return promoCodeItemHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PromoCodeItemHolder holder, int position)
    {
        if(position % 2 == 0)
        {
            holder.linear_view.setBackgroundResource(R.drawable.ic_coupon_1);
            holder.linear_view_2.setBackgroundResource(R.drawable.bg_transparent);
        }
        else{
            holder.linear_view.setBackgroundResource(R.drawable.ic_coupon_2);
            holder.linear_view_2.setBackgroundResource(R.drawable.bg_transparent_2);
        }

        try{
            PromoCode promoCode = promoCodeArrayList.get(position);
            holder.txt_1_cpname.setText(promoCode.getC_title());

            //holder.txt_2_shopename.setText(coupan.getStr_shope_name());

            String str1=  "Valid Till: "+ promoCode.getEnd_date();
            holder.txt_3_descr.setText(str1);

            if(promoCode.getC_disc_in()==1)
            {
                //discount in percentage
                holder.txt_discountType.setText(promoCode.getC_disc_value() + "%");
                //holder.txt_discount.setText("upto"+ " "+coupan.getStr_disupto() );
            }
            else if(promoCode.getC_disc_in()==2)
            {
                holder.txt_discountType.setText(promoCode.getC_disc_value() + Constant.SETTING_CURRENCY_SYMBOL  + " " + "off");
                //holder.txt_discount.setText(coupan.getStr_discount() + "%" + " "+ "off");
            }
            //holder.txt_price.setText("Price: "+coupan.getStr_price());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
       return promoCodeArrayList.size();
        //return 10;
    }

    public class PromoCodeItemHolder extends RecyclerView.ViewHolder {
        LinearLayout linear_view,linear_view_2;
        TextView txt_1_cpname, txt_2_shopename, txt_3_descr,txt_discount,txt_price,txt_discountType;

        public PromoCodeItemHolder(@NonNull View view)
        {
            super(view);
            txt_1_cpname = (TextView)view.findViewById(R.id.txt_1);
            txt_2_shopename = (TextView)view.findViewById(R.id.txt_2);
            txt_3_descr = (TextView)view.findViewById(R.id.txt_3);
            txt_discountType = (TextView)view.findViewById(R.id.txt_discountType);
            txt_price = (TextView)view.findViewById(R.id.txt_4);

            txt_discount = (TextView)view.findViewById(R.id.txt_discount);
            linear_view = (LinearLayout)view.findViewById(R.id.linear_view);
            linear_view_2 = (LinearLayout)view.findViewById(R.id.linear_view_2);


        }
    }

}
