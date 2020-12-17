package com.ifresh.customer.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.ifresh.customer.R;
import com.ifresh.customer.activity.ProductDetailActivity_2;
import com.ifresh.customer.activity.ProductListActivity_2;
import com.ifresh.customer.activity.SetDefaultAddress_2;
import com.ifresh.customer.activity.UploadMedicine;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.kotlin.FillAddress;
import com.ifresh.customer.kotlin.SignInActivity_K;
import com.ifresh.customer.model.ModelProduct;
import com.ifresh.customer.model.ModelProductVariation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class ProductListAdapter_2 extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    //private static final int FOOTER_VIEW = 1;
    private static final int HEADER_VIEW = 1;
    private Context ctx;
    private final ArrayList<ModelProduct> arrayList_vertical;
    DatabaseHelper databaseHelper;
    Activity activity;
    Session session;
    // for load more
    public final int VIEW_TYPE_ITEM = 0;
    public final int VIEW_TYPE_LOADING = 1;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    public boolean isLoading;

    public int resource;
    SpannableString spannableString;

    public  Boolean is_deafultAddExist=false;
    public  Boolean is_address_save=false;
    public  Boolean is_default_address_save=false;


    public void add(int position, ModelProduct item) {
        arrayList_vertical.add(position, item);
        notifyItemInserted(position);
    }


    public ProductListAdapter_2(Context ctx, ArrayList<ModelProduct> arrayList_vertical, Activity activity, Session session) {
        this.ctx = ctx;
        this.arrayList_vertical = arrayList_vertical;
        databaseHelper = new DatabaseHelper(ctx);
        this.activity = activity;
        this.session = session;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        if (viewType == HEADER_VIEW)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_footer, parent, false);
            HeaderViewHolder vh = new HeaderViewHolder(view);
            return vh;
        }

        /*else if (viewType == FOOTER_VIEW)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_footer, parent, false);
            FooterViewHolder vh = new FooterViewHolder(view);
            return vh;
        }*/

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_item_list_12, parent, false);
        ProductViewHolder vh = new ProductViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder instanceof ProductViewHolder)
            {
                int newposition = position;
                if(ProductListActivity_2.is_footer_show != null)
                {
                    if(newposition > 0 && ProductListActivity_2.is_footer_show)
                    {
                        //Header is Added
                        newposition = newposition-1;
                    }
                }

                final ProductViewHolder vh = (ProductViewHolder) holder;
                final ModelProduct product = arrayList_vertical.get(newposition);
                final ArrayList<ModelProductVariation> product_variations = product.getPriceVariations();
                product.setGlobalStock(Double.parseDouble(product_variations.get(0).getStock()));

                if (product_variations.size() == 1)
                {
                    vh.imgarrow.setVisibility(View.GONE);
                }

                /*if (!product.getIndicator().equals("0"))
                {
                    holder.imgIndicator.setVisibility(View.VISIBLE);
                    if (product.getIndicator().equals("1"))
                        holder.imgIndicator.setImageResource(R.drawable.veg_icon);
                    else if (product.getIndicator().equals("2"))
                        holder.imgIndicator.setImageResource(R.drawable.non_veg_icon);
                }*/

                String cap_title = product.getName().substring(0, 1).toUpperCase() + product.getName().substring(1);
                vh.productName.setText(Html.fromHtml("<font color='#000000'><b>" + cap_title + "</b></font> - <small>" +  "</small>"));
                vh.imgThumb.setDefaultImageResId(R.drawable.placeholder);
                vh.imgThumb.setErrorImageResId(R.drawable.placeholder);
                vh.imgThumb.setImageUrl(product.getProduct_img(), Constant.imageLoader);

                CustomAdapter_2 customAdapter = new CustomAdapter_2(ctx, product_variations, vh, product);
                vh.spinner.setAdapter(customAdapter);

                vh.imgThumb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Constant.SELECTEDPRODUCT_POS = position + "=" + product.getId();
                        activity.startActivity(new Intent(activity, ProductDetailActivity_2.class).
                                putExtra("vpos", product_variations.size() == 1 ? 0 :
                                        vh.spinner.getSelectedItemPosition()).putExtra("model", product));
                    }
                });


                vh.imgarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vh.spinner.performClick();
                }
            });


                vh.productName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Constant.SELECTEDPRODUCT_POS = position + "=" + product.getId();
                        activity.startActivity(new Intent(activity, ProductDetailActivity_2.class).
                                putExtra("vpos", product_variations.size() == 1 ? 0 :
                                        vh.spinner.getSelectedItemPosition()).putExtra("model", product));
                    }
                });

                ApiConfig.SetFavOnImg(databaseHelper, vh.imgFav, product_variations.get(0).getProductId(), product_variations.get(0).getFrproductId(), product_variations.get(0).getId());

                vh.imgFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Log.d("Prodid", product_variations.get(0).getProductId());
                       // Log.d("getFranchiseId", product_variations.get(0).getFrproductId());
                       // Log.d("getFrproductId", product_variations.get(0).getId());

                        ApiConfig.AddRemoveFav(databaseHelper, vh.imgFav,product_variations.get(0).getProductId(), product_variations.get(0).getFrproductId(), product_variations.get(0).getId());
                    }
                });

                SetSelectedData(product, vh, product_variations.get(0));

            }

            else if (holder instanceof HeaderViewHolder)
            {
                HeaderViewHolder vh = (HeaderViewHolder) holder;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        // make one
        if(ProductListActivity_2.is_footer_show != null)
        {
            if(ProductListActivity_2.is_footer_show)
                return arrayList_vertical.size()+1;
            else
                return arrayList_vertical.size();
        }
        else{
            return arrayList_vertical.size();
        }

    }

    @Override
    public int getItemViewType(int position)
    {
        if(ProductListActivity_2.is_footer_show != null)
        {
            if (position == 0 && ProductListActivity_2.is_footer_show)
                return HEADER_VIEW;

        }
        return super.getItemViewType(position);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder
    {
        private TextView productName, productPrice ,txtqty, Measurement, showDiscount, originalPrice, txtstatus;
        private NetworkImageView imgThumb;
        private ImageView imgFav, imgIndicator;
        private TextView imgarrow;
        private CardView lytmain;
        private AppCompatSpinner spinner;
        private Button imgAdd, imgMinus;
        private LinearLayout qtyLyt;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.txtprice);
            showDiscount = itemView.findViewById(R.id.showDiscount);
            originalPrice = itemView.findViewById(R.id.txtoriginalprice);
            Measurement = itemView.findViewById(R.id.txtmeasurement);
            txtstatus = itemView.findViewById(R.id.txtstatus);
            imgThumb = itemView.findViewById(R.id.imgThumb);
            imgIndicator = itemView.findViewById(R.id.imgIndicator);
            imgarrow = itemView.findViewById(R.id.imgarrow);
            imgAdd = itemView.findViewById(R.id.btnaddqty);
            imgMinus = itemView.findViewById(R.id.btnminusqty);
            txtqty = itemView.findViewById(R.id.txtqty);
            qtyLyt = itemView.findViewById(R.id.qtyLyt);
            imgFav = itemView.findViewById(R.id.imgFav);
            lytmain = itemView.findViewById(R.id.lytmain);
            spinner = itemView.findViewById(R.id.spinner);
        }
    }


    // Define a ViewHolder for Header view
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Do whatever you want on clicking the item
                    call_chekuser();
                }
            });
        }
    }

    private void call_chekuser() {
        if (session.isUserLoggedIn())
        {
            callApi_fillAdd();
        }
        else{
            Toast.makeText(activity, "Please Login or Register", Toast.LENGTH_SHORT).show();
            Intent intent  = new Intent(activity, SignInActivity_K.class);
            activity.startActivity(intent);
        }
    }


    public void call_view()
    {
        Intent intent;
        /*if(is_address_save)
        {
            //address save
            if(is_deafultAddExist)
            {
                session.setBoolean("is_upload", true);
                intent = new Intent(activity, UploadMedicine.class);
            }
            else{
                //default address not exist
                intent = new Intent(activity, SetDefaultAddress_2.class);
            }
        }
        else{
            //address not save
            session.setBoolean("is_upload", true);
            intent = new Intent(activity, FillAddress.class);
            intent.putExtra("userId", session.getData(session.KEY_id));
        }*/
        if(is_deafultAddExist)
        {
            session.setBoolean("is_upload", true);
            intent = new Intent(activity, UploadMedicine.class);
        }
        else{
            //default address not exist
            intent = new Intent(activity, SetDefaultAddress_2.class);
        }
        activity.startActivity(intent);
    }



    public void callApi_fillAdd()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", session.getData(Session.KEY_id));
        params.put("areaId", session.getData(Constant.AREA_ID));

        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====>" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.has(Constant.SUCESS))
                        {
                            if(jsonObject.getInt(Constant.SUCESS) == 200)
                            {
                                if(jsonObject.getBoolean("noAddress_flag"))
                                {
                                    //no address save
                                    is_address_save=false;
                                }
                                else{
                                    //address is save
                                    is_address_save=true;
                                }
                                if(jsonObject.getBoolean("defaultAddress_flag"))
                                {
                                    // no default address
                                    is_default_address_save=false;
                                }
                                else{
                                    //default address save
                                    is_default_address_save=true;
                                }

                            }
                            else{
                                is_address_save=false;
                                is_default_address_save=false;
                            }

                            callApidefaultAdd();
                        }

                    } catch (JSONException e) {
                        is_address_save=false;
                        is_default_address_save=false;
                        callApidefaultAdd();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_CHECKADDRESS, params, true);

    }


    public Boolean callApidefaultAdd()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", session.getData(Session.KEY_id));
        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area=>" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.has(Constant.SUCESS))
                        {
                            if (jsonObject.getInt(Constant.SUCESS) == 200)
                            {
                                JSONObject data_obj = jsonObject.getJSONObject("data");
                                JSONObject address_obj = data_obj.getJSONObject("address");
                                Boolean default_address = address_obj.getBoolean("default_address");

                                if(default_address)
                                {
                                    Log.d("val", "true");
                                    is_deafultAddExist = true;
                                }
                                else{
                                    Log.d("val", "false");
                                    is_deafultAddExist = false;
                                }
                            }
                            else{
                                is_deafultAddExist = false;
                                Toast.makeText(activity, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            is_deafultAddExist = false;
                            Toast.makeText(activity, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();

                        }
                        call_view();

                    } catch (JSONException e) {
                        is_deafultAddExist = false;
                        Toast.makeText(activity, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH+Constant.GET_USERDEFULTADD, params, false);
        return is_deafultAddExist;
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        // Define elements of a row here
        public ViewHolder(View itemView) {
            super(itemView);
            // Find view by ID and initialize here
        }

        public void bindView(int position) {
            // bindView() method to implement actions
        }
    }

    public class CustomAdapter_2 extends BaseAdapter
    {
        Context context;
        ArrayList<ModelProductVariation> extra;
        LayoutInflater inflter;
        ProductViewHolder holder;
        ModelProduct product;

        public CustomAdapter_2(Context context, ArrayList<ModelProductVariation> extra, ProductViewHolder holder, ModelProduct product) {
            this.context = context;
            this.extra = extra;
            this.holder = holder;
            this.product = product;
            inflter = (LayoutInflater.from(ctx));
        }

        @Override
        public int getCount() {
            return extra.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.lyt_spinner_item, null);

            TextView measurement = view.findViewById(R.id.txtmeasurement);
            TextView price = view.findViewById(R.id.txtprice);
            TextView txttitle = view.findViewById(R.id.txttitle);

            final ModelProductVariation productVariation = extra.get(i);
            measurement.setText(productVariation.getMeasurement_unit_name() +" "+ productVariation.getMeasurement() );

            Log.d("price", productVariation.getPrice());
            Log.d("mesurment", productVariation.getMeasurement_unit_name() +" "+ productVariation.getMeasurement());


            price.setText(ctx.getResources().getString(R.string.rupee) + productVariation.getPrice());


            if (i == 0) {
                txttitle.setVisibility(View.VISIBLE);
            } else {
                txttitle.setVisibility(View.GONE);
            }


            if (productVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT))
            {
                measurement.setTextColor(context.getResources().getColor(R.color.red));
                price.setTextColor(context.getResources().getColor(R.color.red));
            } else {
                measurement.setTextColor(context.getResources().getColor(R.color.black));
                price.setTextColor(context.getResources().getColor(R.color.black));
            }

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ModelProductVariation productVariation= extra.get(i);
                    SetSelectedData(product, holder, productVariation);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            return view;


        }
    }


    public void SetSelectedData(final ModelProduct product, final ProductViewHolder holder, final ModelProductVariation extra)
    {
        holder.Measurement.setText(extra.getMeasurement_unit_name()+" "+extra.getMeasurement());
        holder.productPrice.setText(ctx.getResources().getString(R.string.rupee) + extra.getPrice());
        holder.txtstatus.setText(extra.getServe_for());

        if (extra.getDiscounted_price().equals("0") || extra.getDiscounted_price().equals(""))
        {
            holder.originalPrice.setText("");
            holder.showDiscount.setText("");
            holder.originalPrice.setVisibility(View.GONE);
            holder.showDiscount.setVisibility(View.GONE);

        }
        else{
            spannableString = new SpannableString(ctx.getResources().getString(R.string.mrp) + ctx.getResources().getString(R.string.rupee) + extra.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.originalPrice.setText(spannableString);
            double diff = Double.parseDouble(extra.getPrice()) - Double.parseDouble(extra.getPrice());
            holder.showDiscount.setText(ctx.getResources().getString(R.string.you_save) + ctx.getResources().getString(R.string.rupee) + diff + extra.getDiscountpercent());

            holder.showDiscount.setVisibility(View.VISIBLE);
            holder.originalPrice.setVisibility(View.VISIBLE);
        }

        if (extra.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT))
        {
            // product is sold out or not available
            holder.txtstatus.setVisibility(View.VISIBLE);
            holder.txtstatus.setTextColor(Color.RED);
            holder.qtyLyt.setVisibility(View.GONE);
        } else {
            // product is available for sale
            holder.txtstatus.setVisibility(View.GONE);
            holder.qtyLyt.setVisibility(View.VISIBLE);
        }

        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extra.getType().equals("loose"))
                {
                    String measurement = extra.getMeasurement_unit_name();
                    if (measurement.equals("kg") || measurement.equals("ltr") || measurement.equals("gm") || measurement.equals("ml")) {
                        double totalKg;
                        if (measurement.equals("kg") || measurement.equals("ltr"))
                            totalKg = (Integer.parseInt(extra.getMeasurement()) * 1000);
                        else
                            totalKg = (Integer.parseInt(extra.getMeasurement()));
                        double cartKg = ((databaseHelper.getTotalKG(product.getId()) + totalKg) / 1000);

                        if (cartKg <= product.getGlobalStock()) {
                            holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), product.getId(), extra.getProductId(),extra.getFranchiseId(), extra.getFrproductId(), extra.getId(),false, activity, false, Double.parseDouble(extra.getPrice()), extra.getMeasurement()+ "@" + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getPrice(),product.getProduct_img()).split("=")[0]);
                        } else {
                            Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        RegularCartAdd(product, holder, extra);
                    }

                } else {
                    RegularCartAdd(product, holder, extra);
                }
                activity.invalidateOptionsMenu();
            }
        });

        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.txtqty.setText(databaseHelper.AddUpdateOrder(extra.getId(), extra.getProductId(), extra.getProductId(), extra.getFranchiseId(), extra.getFrproductId(),extra.getId(),false, activity, false, Double.parseDouble(extra.getPrice()), extra.getMeasurement() + "@" + extra.getMeasurement_unit_name() + "==" + product.getName() + "==" + extra.getPrice(), product.getProduct_img()).split("=")[0]);
                activity.invalidateOptionsMenu();
            }
        });

        holder.txtqty.setText(databaseHelper.CheckOrderExists(extra.getId(), product.getId()));
    }

    public void RegularCartAdd(final ModelProduct product, final ProductViewHolder holder, final ModelProductVariation pricevariation)
    {
       // Log.d("productvar",""+pricevariation.getMeasurement()+ "@" + pricevariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + pricevariation.getPrice() );

        if (Double.parseDouble(databaseHelper.CheckOrderExists(pricevariation.getId(), pricevariation.getProductId())) < Double.parseDouble(String.valueOf(pricevariation.getStock())))

            holder.txtqty.setText(databaseHelper.AddUpdateOrder(pricevariation.getId(), pricevariation.getProductId(), pricevariation.getProductId(),pricevariation.getFranchiseId(), pricevariation.getFrproductId(), pricevariation.getCatId(),true,activity, false, Double.parseDouble(pricevariation.getPrice()), pricevariation.getMeasurement()+ "@" + pricevariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + pricevariation.getPrice(),product.getProduct_img()).split("=")[0]);

        else

            Toast.makeText(ctx, ctx.getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();

    }


    public void setLoaded() {
        isLoading = false;
    }


}
