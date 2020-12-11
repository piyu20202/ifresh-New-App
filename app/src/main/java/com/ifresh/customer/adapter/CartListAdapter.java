package com.ifresh.customer.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.ifresh.customer.R;

import com.android.volley.toolbox.NetworkImageView;


import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.activity.CartActivity;
import com.ifresh.customer.model.PriceVariation;
import com.ifresh.customer.model.Product;

import java.util.ArrayList;
import java.util.Objects;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartItemHolder> {
    public ArrayList<Product> productList;
    public Activity activity;
    SpannableString spannableString;
    DatabaseHelper databaseHelper;


    public CartListAdapter(ArrayList<Product> cartDataList, Activity activity) {
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
        final Product order = productList.get(position);
        final PriceVariation priceVariation = order.getPriceVariations().get(0);

        order.setGlobalStock(Double.parseDouble(priceVariation.getStock()));
        holder.txtMenuName.setText(order.getName());
        holder.txtQuantity.setText(priceVariation.getQty() + "");
        holder.txtMeasurement.setText(priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name());
        holder.txttotalprice.setText(Constant.SETTING_CURRENCY_SYMBOL+ DatabaseHelper.decimalformatData.format(priceVariation.getTotalprice()));

        holder.thumb.setDefaultImageResId(R.drawable.placeholder);
        holder.thumb.setErrorImageResId(R.drawable.placeholder);
        holder.thumb.setImageUrl(order.getImage(), Constant.imageLoader);


        holder.txtprice.setText(Constant.SETTING_CURRENCY_SYMBOL +""+ priceVariation.getProductPrice());

        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
            holder.originalPrice.setText("");
            holder.showDiscount.setText("");
        } else {
            spannableString = new SpannableString(Constant.SETTING_CURRENCY_SYMBOL + priceVariation.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.originalPrice.setText(spannableString);
            holder.showDiscount.setText(priceVariation.getDiscountpercent());
        }

           }

    public void RegularCartAdd(final Product order, final CartItemHolder holder, final PriceVariation priceVariation) {
        if (Double.parseDouble(databaseHelper.CheckOrderExists(priceVariation.getId(), order.getId())) < Double.parseDouble(priceVariation.getStock()))
            SetData(true, holder, priceVariation, order);
        else
            Toast.makeText(activity, activity.getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void SetData(boolean isadd, CartItemHolder holder, PriceVariation priceVariation, Product order) {
        String[] qty_total = databaseHelper.AddUpdateOrder(priceVariation.getId(), order.getId(),"","","","", isadd, activity, true, Double.parseDouble(priceVariation.getProductPrice()), priceVariation.getMeasurement() + priceVariation.getMeasurement_unit_name() + "==" + order.getName() + "==" + priceVariation.getProductPrice(),"").split("=");
        holder.txtQuantity.setText(qty_total[0]);
        holder.txttotalprice.setText(Constant.SETTING_CURRENCY_SYMBOL + qty_total[1]);
        CartActivity.SetDataTotal();
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
                     Product order = productList.get(position);
                     PriceVariation priceVariation = order.getPriceVariations().get(0);
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
                    Product order = productList.get(position);
                    PriceVariation priceVariation = order.getPriceVariations().get(0);
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
                    final Product order = productList.get(position);
                    final PriceVariation priceVariation = order.getPriceVariations().get(0);

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
                            databaseHelper.DeleteOrderData(priceVariation.getId(), order.getId());
                            productList.remove(position);
                            CartActivity.SetDataTotal();
                            notifyItemRemoved(position);
                            activity.invalidateOptionsMenu();
                            if (getItemCount() == 0) {
                                CartActivity.lytempty.setVisibility(View.VISIBLE);
                                CartActivity.lyttotal.setVisibility(View.GONE);
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
