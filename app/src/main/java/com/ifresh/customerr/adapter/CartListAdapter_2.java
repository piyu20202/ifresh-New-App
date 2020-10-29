package com.ifresh.customerr.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.ifresh.customerr.R;
import com.ifresh.customerr.activity.CartActivity_2;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.DatabaseHelper;
import com.ifresh.customerr.model.ModelProductVariation;
import com.ifresh.customerr.model.ModelProduct;

import java.util.ArrayList;
import java.util.Objects;

public class CartListAdapter_2 extends RecyclerView.Adapter<CartListAdapter_2.CartItemHolder> {
    public ArrayList<ModelProduct> productList;
    public Activity activity;
    SpannableString spannableString;
    DatabaseHelper databaseHelper;

    public CartListAdapter_2(ArrayList<ModelProduct> cartDataList, Activity activity) {
        this.productList = cartDataList;
        this.activity = activity;
        databaseHelper = new DatabaseHelper(activity);
    }

    @Override
    public CartItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_cartlist, null);
        CartItemHolder cartItemHolder = new CartItemHolder(v);
        return cartItemHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CartItemHolder holder, final int position) {
        final ModelProduct order = productList.get(position);
        final ModelProductVariation priceVariation = order.getPriceVariations().get(0);
        order.setGlobalStock(Double.parseDouble(priceVariation.getStock()));

        holder.txtMenuName.setText(order.getName());
        holder.txtQuantity.setText(priceVariation.getQty() + "");
        holder.txtMeasurement.setText(priceVariation.getMeasurement_unit_name() + priceVariation.getMeasurement());
        holder.txttotalprice.setText(Constant.SETTING_CURRENCY_SYMBOL+ DatabaseHelper.decimalformatData.format(priceVariation.getTotalprice()));


        //Log.d("price",priceVariation.getPrice());
        //Double total_price = priceVariation.getQty() * Double.parseDouble( priceVariation.getPrice());
        holder.thumb.setDefaultImageResId(R.drawable.placeholder);
        holder.thumb.setErrorImageResId(R.drawable.placeholder);
        holder.thumb.setImageUrl(order.getProduct_img(), Constant.imageLoader);

        holder.txtprice.setText(Constant.SETTING_CURRENCY_SYMBOL +""+ priceVariation.getPrice());

        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
            holder.originalPrice.setText("");
            holder.showDiscount.setText("");
        } else {
            spannableString = new SpannableString(Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.originalPrice.setText(spannableString);
            holder.showDiscount.setText(priceVariation.getDiscountpercent());

            holder.originalPrice.setVisibility(View.GONE);
            holder.showDiscount.setVisibility(View.GONE);

        }

      }

    public void RegularCartAdd(final ModelProduct order, final CartItemHolder holder, final ModelProductVariation priceVariation) {
        //Log.d("value 1", ""+Double.parseDouble(databaseHelper.CheckOrderExists(priceVariation.getId(), order.getId())));
        //Log.d("value 2", ""+ Double.parseDouble(String.valueOf(priceVariation.getQty())));
        if (Double.parseDouble(databaseHelper.CheckOrderExists(priceVariation.getId(), order.getId())) < Double.parseDouble(String.valueOf(priceVariation.getStock())))
            SetData(true, holder, priceVariation, order);
        else
            Toast.makeText(activity, activity.getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("SetTextI18n")
    private void SetData(boolean isadd, CartItemHolder holder, ModelProductVariation priceVariation, ModelProduct order) {
        Log.d("productvar",priceVariation.getMeasurement() + "@" +priceVariation.getMeasurement_unit_name() + "==" + order.getName() + "==" + priceVariation.getPrice());
        String[] qty_total = databaseHelper.AddUpdateOrder( priceVariation.getId(), priceVariation.getProductId(), priceVariation.getProductId(), priceVariation.getFranchiseId(), priceVariation.getFrproductId() ,priceVariation.getCatId(),isadd, activity, true, Double.parseDouble(priceVariation.getPrice()), priceVariation.getMeasurement() + "@" +priceVariation.getMeasurement_unit_name() + "==" + order.getName() + "==" + priceVariation.getPrice(), order.getProduct_img()).split("=");
        holder.txtQuantity.setText(qty_total[0]);
        holder.txttotalprice.setText(Constant.SETTING_CURRENCY_SYMBOL + qty_total[1]);
        CartActivity_2.SetDataTotal();
    }


    public class CartItemHolder extends RecyclerView.ViewHolder {
        TextView txtMenuName, txtQuantity, txttotalprice, txtMeasurement, txtprice, count;
        ImageView imgdelete, imgAdd, imgMinus;
        NetworkImageView thumb;
        TextView showDiscount, originalPrice;
        RelativeLayout lytmain;

        public CartItemHolder(View itemView) {
            super(itemView);
            txtMenuName = (TextView) itemView.findViewById(R.id.txtproductname);
            txtQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
            txttotalprice = (TextView) itemView.findViewById(R.id.txttotalprice);
            txtMeasurement = (TextView) itemView.findViewById(R.id.txtmeasurement);
            txtprice = (TextView) itemView.findViewById(R.id.txtprice);
            thumb = (NetworkImageView) itemView.findViewById(R.id.imgproduct);
            imgAdd = itemView.findViewById(R.id.btnaddqty);
            imgMinus = itemView.findViewById(R.id.btnminusqty);
            imgdelete = (ImageView) itemView.findViewById(R.id.imgdelete);
            showDiscount = itemView.findViewById(R.id.showDiscount);
            originalPrice = itemView.findViewById(R.id.txtoriginalprice);
            lytmain = itemView.findViewById(R.id.lytmain);

            imgAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     int position = getAdapterPosition();
                     ModelProduct order = productList.get(position);
                     ModelProductVariation priceVariation = order.getPriceVariations().get(0);
                     priceVariation.setQty(priceVariation.getQty()+1);

                    if (priceVariation.getType().equals("loose"))
                    {
                        String measurement = priceVariation.getMeasurement_unit_name();
                        if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                            double totalKg;
                            if (measurement.equals("kg") || measurement.equals("ltr"))
                                totalKg = (Integer.parseInt(priceVariation.getMeasurement()) * 1000);
                            else
                                totalKg = (Integer.parseInt(priceVariation.getMeasurement()));
                            double cartKg = ((databaseHelper.getTotalKG(order.getId()) + totalKg) / 1000);

                            if (cartKg <= order.getGlobalStock()) {
                                SetData(true, CartItemHolder.this, priceVariation, order);
                            } else {
                                Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            RegularCartAdd(order, CartItemHolder.this, priceVariation);
                        }
                    } else {
                        RegularCartAdd(order, CartItemHolder.this, priceVariation);
                    }
                }
            });

            imgMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    ModelProduct order = productList.get(position);
                    ModelProductVariation priceVariation = order.getPriceVariations().get(0);
                    priceVariation.setQty(priceVariation.getQty()-1);
                    SetData(false, CartItemHolder.this, priceVariation, order);
                }
            });

            lytmain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //activity.startActivity(new Intent(activity, ProductDetailActivity.class).putExtra("vpos",0).putExtra("model",order));
                }
            });


            imgdelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    final int position = getAdapterPosition();
                    final ModelProduct order = productList.get(position);
                    final ModelProductVariation priceVariation = order.getPriceVariations().get(0);

                    final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity);
                    LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialogView = inflater.inflate(R.layout.msg_view_4, null);
                    alertDialog.setView(dialogView);
                    alertDialog.setCancelable(true);
                    final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();

                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    TextView tvremove,tvclose,txt_msg;

                    tvremove = dialogView.findViewById(R.id.tvcancel);
                    tvclose = dialogView.findViewById(R.id.tvclose);
                    txt_msg = dialogView.findViewById(R.id.txt_msg);


                    tvclose.setText("CANCEL");
                    tvremove.setText("REMOVE");
                    txt_msg.setText(activity.getResources().getString(R.string.deleteproductmsg));

                    tvremove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            databaseHelper.DeleteOrderData(priceVariation.getId(), priceVariation.getProductId());
                            productList.remove(position);
                            CartActivity_2.SetDataTotal();
                            notifyItemRemoved(position);
                            activity.invalidateOptionsMenu();
                            if (getItemCount() == 0)
                            {
                                //databaseHelper.DeleteAllOrderData();
                                CartActivity_2.lytempty.setVisibility(View.VISIBLE);
                                CartActivity_2.lyttotal.setVisibility(View.GONE);
                            }

                        }
                    });
                    tvclose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            //onBackPressed();
                        }
                    });
                    dialog.show();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
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
