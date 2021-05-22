package com.ifresh.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.ifresh.customer.BuildConfig;
import com.ifresh.customer.R;
import com.ifresh.customer.adapter.SliderAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.model.ModelProductVariation;
import com.ifresh.customer.model.ModelProduct;
import com.ifresh.customer.model.Slider;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ProductDetailActivity_2 extends AppCompatActivity {
    Context ctx =  ProductDetailActivity_2.this;
    int vpos;
    String from;

    ModelProduct product;
    ModelProductVariation productVariation;
    ArrayList<ModelProductVariation> prodcutVariationslist;

    Toolbar toolbar;
    ImageView imgIndicator;
    public TextView txtProductName, txtqty, txtPrice, txtOriginalPrice, txtDiscountedPrice, txtMeasurement, imgarrow;
    public static ArrayList<Slider> sliderArrayList;
    public WebView webDescription;
    public TextView btncart;
    public ImageButton imgAdd, imgMinus;
    SpannableString spannableString;
    public int size;
    public ViewPager viewPager;
    public AppCompatSpinner spinner;
    TextView txtstatus;
    public ImageView imgFav;
    LinearLayout mMarkersLayout, lytqty;
    RelativeLayout lytmainprice;

    FrameLayout fragment_container;

    public static FragmentManager fragmentManager;
    DatabaseHelper databaseHelper;
    Menu menu;
    StorePrefrence storeinfo;
    public double total_cart=0;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        activity = ProductDetailActivity_2.this;
        vpos = getIntent().getIntExtra("vpos", 0);
        from = getIntent().getStringExtra("from");
        storeinfo = new StorePrefrence(ProductDetailActivity_2.this);
        InitializeViews();

       /*if (from != null && from.equals("share"))
       {
            GetProductDetail(getIntent().getStringExtra("id"));

        } else {
            product = (ModelProduct) getIntent().getSerializableExtra("model");
            priceVariationslist = product.getPriceVariations();
            SetProductDetails();
        }*/

        product = (ModelProduct) getIntent().getSerializableExtra("model");
        prodcutVariationslist = product.getPriceVariations();
        SetProductDetails();

    }



   /* private void GetProductDetail(String productid)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.PRODUCT_ID, productid);

        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                         System.out.println("======product " + response);
                         JSONObject objectbject = new JSONObject(response);
                         if (!objectbject.getBoolean(Constant.ERROR))
                         {

                            JSONObject object = new JSONObject(response);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);

                            product = ApiConfig.GetProductList_2(jsonArray).get(0);
                            priceVariationslist = product.getPriceVariations();
                            product.setGlobalStock(Double.parseDouble(priceVariationslist.get(0).getStock()));
                            SetProductDetails();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ProductDetailActivity_2.this, Constant.GET_PRODUCT_DETAIL_URL, params, false);
    }*/


    private void InitializeViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Detail");
        fragmentManager = getSupportFragmentManager();
        databaseHelper = new DatabaseHelper(ProductDetailActivity_2.this);
        lytqty = findViewById(R.id.lytqty);
        mMarkersLayout = findViewById(R.id.layout_markers);
        fragment_container = findViewById(R.id.fragment_container);

        viewPager = findViewById(R.id.viewPager);
        btncart = findViewById(R.id.btncart);
        txtProductName = findViewById(R.id.txtproductname);
        txtOriginalPrice = findViewById(R.id.txtoriginalprice);
        txtDiscountedPrice = findViewById(R.id.txtdiscountPrice);
        webDescription = findViewById(R.id.txtDescription);
        txtPrice = findViewById(R.id.txtprice);
        txtMeasurement = findViewById(R.id.txtmeasurement);
        imgarrow = findViewById(R.id.imgarrow);
        imgFav = findViewById(R.id.imgFav);
        lytmainprice = findViewById(R.id.lytmainprice);
        txtqty = findViewById(R.id.txtqty);
        txtstatus = findViewById(R.id.txtstatus);
        imgAdd = findViewById(R.id.btnaddqty);
        imgMinus = findViewById(R.id.btnminusqty);
        spinner = findViewById(R.id.spinner);
        imgIndicator = findViewById(R.id.imgIndicator);
        imgarrow.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dropdown, 0);
        //setBtncart();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void SetProductDetails()
    {
        try {
                sliderArrayList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray("["+ "\"" + product.getProduct_img() + "\"" + "]");
                size = jsonArray.length();
                for (int i = 0; i < jsonArray.length(); i++) {
                    sliderArrayList.add(new Slider(jsonArray.getString(i)));
                }
                //sliderArrayList.add(new Slider(product.getProduct_img()));
                viewPager.setAdapter(new SliderAdapter(sliderArrayList, ProductDetailActivity_2.this, R.layout.lyt_detail_slider, "detail"));
                ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, ProductDetailActivity_2.this);

                if (prodcutVariationslist.size() == 1)
                {
                    spinner.setVisibility(View.GONE);
                    lytmainprice.setEnabled(false);
                    imgarrow.setVisibility(View.GONE);
                    productVariation = prodcutVariationslist.get(0);
                    SetSelectedData(productVariation);
                }

                /*if (!product.getIndicator().equals("0"))
                {
                    imgIndicator.setVisibility(View.VISIBLE);
                    if (product.getIndicator().equals("1"))
                        imgIndicator.setImageResource(R.drawable.veg_icon);
                    else if (product.getIndicator().equals("2"))
                        imgIndicator.setImageResource(R.drawable.non_veg_icon);
                }
                imgIndicator.setVisibility(View.VISIBLE);
                imgIndicator.setImageResource(R.drawable.veg_icon);
              */

                CustomAdapter customAdapter = new CustomAdapter();
                spinner.setAdapter(customAdapter);
                /*String text;
                text = "<html><body  style=\"text-align:justify;\">";
                text += product.getDescription() ;
                text += "</body></html>";


                webDescription.setVerticalScrollBarEnabled(true);
                webDescription.loadDataWithBaseURL("", text, "text/html", "UTF-8", "");
                webDescription.setBackgroundColor(getResources().getColor(R.color.white));*/
                txtProductName.setText(product.getName());

                spinner.setSelection(vpos);

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, ProductDetailActivity_2.this);
                    }
                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
                });

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        productVariation = product.getPriceVariations().get(i);
                        SetSelectedData(productVariation);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                productVariation = product.getPriceVariations().get(0);

                ApiConfig.SetFavOnImg(databaseHelper, imgFav, productVariation.getProductId(), productVariation.getFrproductId(), productVariation.getId() );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void OnBtnClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.lytshare:
                //ShareProduct();
                new ShareProduct().execute();
                break;
            case R.id.lytsave:
                //Log.d("Prodid", product.getId());
                ApiConfig.AddRemoveFav(databaseHelper, imgFav, productVariation.getProductId(), productVariation.getFrproductId(), productVariation.getId());
                break;
            case R.id.btncart:
                OpenCart();
                break;
            case R.id.lytsimilarproducts:
                ViewMoreProduct();
                break;
            case R.id.lytmainprice:
                spinner.performClick();
                break;
            case R.id.btnminusqty:
                txtqty.setText(databaseHelper.AddUpdateOrder(productVariation.getId(), productVariation.getProductId(), productVariation.getProductId() , productVariation.getFranchiseId(), productVariation.getFrproductId(), productVariation.getCatId(),false, ProductDetailActivity_2.this, false, Double.parseDouble(productVariation.getPrice()), productVariation.getMeasurement()+ "@" + productVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + productVariation.getPrice(),product.getProduct_img()).split("=")[0]);
                invalidateOptionsMenu();
                break;
            case R.id.btnaddqty:
                if (productVariation.getType().equals("loose"))
                {
                    String measurement = productVariation.getMeasurement();
                    if (measurement.contains("kg") || measurement.contains("ltr") || measurement.contains("gm") || measurement.contains("ml"))
                    {
                        //kg,ltr,gm ml
                        double totalKg;
                        if (measurement.contains("kg") || measurement.contains("ltr"))
                            totalKg = (Integer.parseInt(productVariation.getMeasurement_unit_name()) * 1000);
                        else
                            totalKg = (Integer.parseInt(productVariation.getMeasurement_unit_name()));

                        double cartKg = ((databaseHelper.getTotalKG_2( productVariation.getFrproductId() ) + totalKg));
                        Log.d("cartKg",""+cartKg);

                        if(Double.parseDouble(product.getMax_order()) == 0)
                        {
                            //normal add value
                            RegularCartAdd();
                        }
                        else{
                            if (cartKg <= Double.parseDouble(product.getMax_order()))
                            {
                              txtqty.setText(databaseHelper.AddUpdateOrder(productVariation.getId(), productVariation.getProductId(), productVariation.getProductId(),productVariation.getFranchiseId(), productVariation.getFrproductId(), productVariation.getCatId(),true,activity, false, Double.parseDouble(productVariation.getPrice()), productVariation.getMeasurement()+ "@" + productVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + productVariation.getPrice(),product.getProduct_img()).split("=")[0]);
                            } else {
                                Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                    else{
                        //consider if loose but no unit match with kg,ltr,gm,ml so apply piece unit logic
                        int otherunit;
                        otherunit = (Integer.parseInt(productVariation.getMeasurement_unit_name()));
                        //Log.d("pcs",""+papm);
                        double cartotherunit = ((databaseHelper.getTotalKG_2(productVariation.getFrproductId())) + otherunit);
                        //Log.d("cartPcs",""+cartPcs);
                        if(Double.parseDouble(product.getMax_order()) == 0)
                        {
                            //normal add value
                            RegularCartAdd();
                        }
                        else{
                            if(cartotherunit <= Double.parseDouble(product.getMax_order()))
                            {
                                RegularCartAdd();
                            }
                            else{
                                Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }
                else {
                    //Pack,Piece,M unit
                    int papm;
                    papm = (Integer.parseInt(productVariation.getMeasurement_unit_name()));
                    //Log.d("pcs",""+papm);
                    double cartPcs = ((databaseHelper.getTotalKG_2(productVariation.getFrproductId())) + papm);
                    Log.d("getMax_order",""+product.getMax_order());
                    if(Double.parseDouble(product.getMax_order()) == 0)
                    {
                        //normal add value
                        RegularCartAdd();
                    }
                    else{
                        if(cartPcs <= Double.parseDouble(product.getMax_order()))
                        {
                            RegularCartAdd();
                        }
                        else{
                            Toast.makeText(activity, activity.getResources().getString(R.string.kg_limit), Toast.LENGTH_LONG).show();
                        }
                    }
                 }
                invalidateOptionsMenu();
                break;
        }

    }

    public void RegularCartAdd()
    {
        if (Double.parseDouble(databaseHelper.CheckOrderExists(productVariation.getId(), productVariation.getProductId())) < Double.parseDouble(String.valueOf(productVariation.getStock())))
        {
            txtqty.setText(databaseHelper.AddUpdateOrder(productVariation.getId(), productVariation.getProductId(), productVariation.getProductId() , productVariation.getFranchiseId(), productVariation.getFrproductId(),productVariation.getCatId(),true, ProductDetailActivity_2.this, false, Double.parseDouble(productVariation.getPrice()), productVariation.getMeasurement() + "@" + productVariation.getMeasurement_unit_name() + "==" + product.getName() + "==" + productVariation.getPrice(),product.getProduct_img()).split("=")[0]);
        }
        else
            Toast.makeText(ProductDetailActivity_2.this, getResources().getString(R.string.stock_limit), Toast.LENGTH_SHORT).show();
    }

    private class ShareProduct extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Bitmap bitmap = null;
                URL url = null;
                url = new URL(product.getProduct_img());
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
                Date now = new Date();
                File file = new File(getExternalCacheDir(), formatter.format(now) + ".png");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);

                //String message = product.getName() + "\n";
                //message = message + Constant.share_url + "itemdetail/" + product.getId() /*+ "/" + product.getSlug()*/;
                String message = storeinfo.getString(Constant.SHARE_MSG) + "\n" +  storeinfo.getString(Constant.SHORT_LINK);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                intent.setDataAndType(uri, getContentResolver().getType(uri));
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

    }


    private void ViewMoreProduct() {
        Intent intent = new Intent(ProductDetailActivity_2.this, ProductListActivity_2.class);
        intent.putExtra("from", "regular");
        intent.putExtra("id", product.getCatId());
        Log.d("id", product.getCatId());
        Log.d("from", "regular");
        //intent.putExtra("name", product.getName());
        startActivity(intent);
    }

    public class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return product.getPriceVariations().size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.lyt_spinner_item, null);
            TextView measurement = view.findViewById(R.id.txtmeasurement);
            TextView price = view.findViewById(R.id.txtprice);
            TextView txttitle = view.findViewById(R.id.txttitle);
            ModelProductVariation extra = product.getPriceVariations().get(i);

            measurement.setText(extra.getMeasurement_unit_name()+" "+ extra.getMeasurement());

            price.setText(ctx.getResources().getString(R.string.rupee) + extra.getPrice());

            if (i == 0) {
                txttitle.setVisibility(View.VISIBLE);
            } else {
                txttitle.setVisibility(View.GONE);
            }

            if (extra.getIs_active().equalsIgnoreCase("0")) {
                //sold out
                measurement.setTextColor(getResources().getColor(R.color.red));
                price.setTextColor(getResources().getColor(R.color.red));
            } else {
                // available
                measurement.setTextColor(getResources().getColor(R.color.black));
                price.setTextColor(getResources().getColor(R.color.black));
            }

            return view;
        }
    }


    public void SetSelectedData(ModelProductVariation priceVariation) {
        String text;
        text = "<html><body  style=\"text-align:justify;\">";
        text += product.getDescription();
        text += "</body></html>";

        webDescription.setVerticalScrollBarEnabled(true);
        webDescription.loadDataWithBaseURL("", text, "text/html", "UTF-8", "");
        webDescription.setBackgroundColor(getResources().getColor(R.color.white));

        txtMeasurement.setText(" ( " + priceVariation.getMeasurement_unit_name() + " " +priceVariation.getMeasurement()+" ) ");
        txtPrice.setText(getString(R.string.offer_price) + ctx.getResources().getString(R.string.rupee) + priceVariation.getPrice());
        txtstatus.setText(priceVariation.getServe_for());

        if (priceVariation.getDiscounted_price().equals("0") || priceVariation.getDiscounted_price().equals("")) {
            txtOriginalPrice.setText("");
            txtDiscountedPrice.setText("");
        } else {
            spannableString = new SpannableString(getString(R.string.mrp) + ctx.getResources().getString(R.string.rupee) + priceVariation.getPrice());
            spannableString.setSpan(new StrikethroughSpan(), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtOriginalPrice.setText(spannableString);
            txtOriginalPrice.setVisibility(View.GONE);
            double diff = Double.parseDouble(priceVariation.getPrice()) - Double.parseDouble(priceVariation.getPrice());
            txtDiscountedPrice.setText(getString(R.string.you_save) + ctx.getResources().getString(R.string.rupee) + diff + priceVariation.getDiscounted_price());
            txtDiscountedPrice.setVisibility(View.GONE);
        }

        if (priceVariation.getServe_for().equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
            txtstatus.setVisibility(View.VISIBLE);
            lytqty.setVisibility(View.GONE);
            btncart.setBackgroundColor(getResources().getColor(R.color.gray));
            btncart.setEnabled(false);
        } else {
            txtstatus.setVisibility(View.INVISIBLE);
            lytqty.setVisibility(View.VISIBLE);
            btncart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btncart.setEnabled(true);
        }

        txtqty.setText(databaseHelper.CheckOrderExists(priceVariation.getId(), priceVariation.getProductId()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_sort).setVisible(false);
        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart, ProductDetailActivity_2.this));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_cart:
                OpenCart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void OpenCart() {
        Intent intent  = new Intent(getApplicationContext(), CartActivity_2.class);
        intent.putExtra("id", productVariation.getProductId());
        startActivity(intent);
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }


}
