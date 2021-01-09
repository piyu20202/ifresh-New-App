package com.ifresh.customer.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifresh.customer.R;
import com.ifresh.customer.activity.PromoCodeList;
import com.ifresh.customer.activity.ReferEarnActivity;
import com.ifresh.customer.helper.Constant;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;


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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_promo_code, null);
        PromoCodeItemHolder promoCodeItemHolder = new PromoCodeItemHolder(v);
        return promoCodeItemHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PromoCodeItemHolder holder, int position)
    {
        try{
            final PromoCode promoCode = promoCodeArrayList.get(position);
            holder.txt_1_cpname.setText(promoCode.getC_title());
            holder.txt_3_enddate.setText(promoCode.getEnd_date());
            holder.grab.setTag(promoCode.getC_title());

            if(promoCode.getC_disc_in()==1)
            {
                //discount in percentage
                holder.txt_discountType.setText(promoCode.getC_disc_value() + " %" + " off");
                String sourceString = "<b>"+"<font color='#09B150'>" + promoCode.getC_disc_value() + " %" + " off" +"</font>"+"</b>"+ " on vegetable order by applying coupon code "+ "\n" + "<b>"+  "<font color='#09B150'> "  + promoCode.getC_title() + "</font>" +"</b> ";
                holder.txt_1_cpname.setText(Html.fromHtml(sourceString));


            }
            else if(promoCode.getC_disc_in()==2)
            {
                holder.txt_discountType.setText(promoCode.getC_disc_value() +" "+ Constant.SETTING_CURRENCY_SYMBOL  +  " off");
                String sourceString = "<b>"+ "<font color='#09B150'>" + promoCode.getC_disc_value() +" " + Constant.SETTING_CURRENCY_SYMBOL +"</font>"+"</b>" + " off" + " on vegetable order by applying coupon code" + "\n" + "<b>"+  "<font color='#09B150'> "  + promoCode.getC_title() + "</font>" +"</b> ";


                holder.txt_1_cpname.setText(Html.fromHtml(sourceString));
            }

            holder.grab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("code", promoCode.getC_title());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(activity, R.string.promo_code_copied, Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
       return promoCodeArrayList.size();
    }

    public class PromoCodeItemHolder extends RecyclerView.ViewHolder {
        TextView txt_1_cpname,txt_3_enddate,txt_discountType;
        Button grab;

        public PromoCodeItemHolder(@NonNull View view)
        {
            super(view);
            txt_1_cpname = (TextView)view.findViewById(R.id.txt_1);
            txt_discountType = (TextView)view.findViewById(R.id.txt_discountType);
            txt_3_enddate = (TextView)view.findViewById(R.id.txt_3);
            grab = (Button)view.findViewById(R.id.grab);
        }
    }

}
