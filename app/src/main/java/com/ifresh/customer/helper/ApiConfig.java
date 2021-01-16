package com.ifresh.customer.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import com.ifresh.customer.R;
import com.ifresh.customer.activity.DrawerActivity;
import com.ifresh.customer.kotlin.LocationSelection_K;
import com.ifresh.customer.kotlin.SignInActivity_K;
import com.ifresh.customer.kotlin.SignUpActivity_K;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.ModelProductVariation;
import com.ifresh.customer.model.ModelProduct;
import com.ifresh.customer.model.OrderTracker;
import com.ifresh.customer.model.OrderTracker_2;
import com.ifresh.customer.model.PriceVariation;
import com.ifresh.customer.model.Product;
import com.ifresh.customer.model.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.Key;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import static com.ifresh.customer.helper.Constant.AUTHORIZATION;
import static com.ifresh.customer.helper.Constant.AUTHTOKEN;
import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.GET_CONFIGSETTING;
import static com.ifresh.customer.helper.Constant.GUEST;
import static com.ifresh.customer.helper.Constant.MEASUREMENT;
import static com.ifresh.customer.helper.Constant.SETTINGS_PAGE;

public class ApiConfig {

    public static String user_location = "";
    public static double latitude1 = 0, longitude1 = 0;
    public static GPSTracker gps;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    static String[] permissionsRequired = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA
    };

    /*=================================================Volley Call Method Start=======================================*/

    public static void RequestToVolley_GET(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress) {

        final Session session = new Session(activity);
        //Log.d("token==>", session.getData(AUTHTOKEN));

        if (AppController.isConnected(activity))
        {
               StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        callback.onSuccess(true, response);
                    }
                    catch (JSONException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.d("error", ""+error);
                            callback.onSuccess(false, "");
                            String message = VolleyErrorMessage(error);
                            Log.d("Error message", message);
                            /*if (!message.equals(""))
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();*/
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded";
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params1 = new HashMap<String, String>();
                    params1.put("x-api-key", "b9381c63b051c9906bf6e01075ca0b5af6084eeda6092b5b9e79dec5");
                    Log.d("token", session.getData(AUTHTOKEN));
                    params1.put(AUTHORIZATION, "Bearer " + session.getData(AUTHTOKEN));
                    return params1;
                }

                   @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(stringRequest);
            //progressDisplay.hideProgress();
        }

    }


    public static void RequestToVolley_POST(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress) {
        final Session session = new Session(activity);


        if (AppController.isConnected(activity))
        {
            System.out.println("================= " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    //System.out.println("================= " + url + " == " + response);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        callback.onSuccess(true, response);
                      }
                    catch (JSONException ex)
                    {
                        ex.printStackTrace();

                    }

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            callback.onSuccess(false, "");
                            String message = VolleyErrorMessage(error);
                            /*if (!message.equals(""))
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();*/
                        }
                    }) {


                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded";
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params1 = new HashMap<String, String>();
                    params1.put("x-api-key", "b9381c63b051c9906bf6e01075ca0b5af6084eeda6092b5b9e79dec5");
                    params1.put(AUTHORIZATION, "Bearer " + session.getData(AUTHTOKEN));
                    return params1;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

    }


    public static void RequestToVolley_GET_SETING(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress)
    {
        if (AppController.isConnected(activity))
        {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    callback.onSuccess(true, response);
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.d("error", ""+error);
                            callback.onSuccess(false, "");
                            String message = VolleyErrorMessage(error);
                            /*if (!message.equals(""))
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();*/
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded";
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params1 = new HashMap<String, String>();
                    params1.put("x-api-key", "b9381c63b051c9906bf6e01075ca0b5af6084eeda6092b5b9e79dec5");
                    //params1.put(AUTHORIZATION, "Bearer " + AUTHTOKEN);
                    return params1;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

    }


    public static void RequestToVolley_POST_GUEST(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress)
    {

        if (AppController.isConnected(activity))
        {
            Log.d("url", url);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    Log.d("response", response);
                    callback.onSuccess(true, response);
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.d("error", ""+error);
                            callback.onSuccess(false, "");
                            String message = VolleyErrorMessage(error);
                            /*if (!message.equals(""))
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();*/
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded";
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params1 = new HashMap<String, String>();
                    params1.put("x-api-key", "b9381c63b051c9906bf6e01075ca0b5af6084eeda6092b5b9e79dec5");
                    return params1;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }

            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

    }

    /*================================================= Volley Call Method End ======================================*/



    /*================================================== My Method Start ======================================================= */
    public static void GetSettings_Api(final Activity activity, final Context ctx)
    {
        final StorePrefrence storeinfo = new StorePrefrence(ctx);
        final Session session = new Session(ctx);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET_SETING(new VolleyCallback()
        {
            @Override
            public void onSuccess(boolean result, String response)
            {
                if (result)
                {
                    try {
                        System.out.println("====res area" + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt(Constant.SUCESS) == 200)
                        {
                            try{
                                JSONArray mjsonarr = jsonObject.getJSONArray("data");
                                JSONObject objectbject = mjsonarr.getJSONObject(0);

                                Constant.free_delivery_message = objectbject.getString("free_delivery_message");
                                //Log.d("free_delivery_message", Constant.free_delivery_message);

                                int server_versionCode =  objectbject.getInt("version_code");
                                int versionCode=0;
                                try {
                                    PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
                                    versionCode = pInfo.versionCode;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(versionCode == server_versionCode)
                                {
                                    //app is updated
                                    storeinfo.setBoolean("is_app_updated", true);
                                }
                                else if(versionCode < server_versionCode)
                                {
                                    //app is not updated
                                    storeinfo.setBoolean("is_app_updated", false);
                                }

                                Log.d("bool1", ""+storeinfo.getBoolean("is_app_updated"));

                                storeinfo.setInt("version_code",objectbject.getInt("version_code"));
                                storeinfo.setString("delivery_chrge",objectbject.getString("delivery_chrge"));
                                storeinfo.setInt("min_order",objectbject.getInt("min_order"));



                                Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY =  objectbject.getDouble("min_order");
                                Log.d("value", ""+ Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY);

                                Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble(objectbject.getString("delivery_chrge"));
                                Constant.SETTING_TAX = Double.parseDouble(objectbject.getString("tax"));
                                Constant.ISACCEPTMINORDER = objectbject.getBoolean("accept_minimum_order");

                                //Constant.SETTING_TAX = 0.0;
                                if(objectbject.getBoolean("force_update"))
                                {
                                    storeinfo.setInt("force_update",1);
                                }
                                else{
                                    storeinfo.setInt("force_update",0);
                                }

                                String checkout_deliveryChargeMessage = objectbject.getString("checkout_deliveryChargeMessage");
                                String[] str_msg = checkout_deliveryChargeMessage.split("#");
                                String msg_below_300 = str_msg[0];
                                String msg_above_300 = str_msg[1];

                                storeinfo.setString("msg_below_300",msg_below_300);
                                storeinfo.setString("msg_above_300",msg_above_300);
                                storeinfo.setString(Constant.SHORT_LINK,objectbject.getString(Constant.SHORT_LINK));
                                storeinfo.setString(Constant.SHARE_MSG, objectbject.getString(Constant.SHARE_MSG));
                                String[] parts = objectbject.getString(Constant.EARN_MSG).split("#");
                                String msg1 = parts[0];
                                storeinfo.setString(Constant.MSG_1, msg1);

                                String [] new_parts = parts[1].split("@");
                                storeinfo.setString(Constant.MSG_2,new_parts[0]);
                                storeinfo.setString(Constant.MSG_3,new_parts[1]);

                                storeinfo.setString(Constant.USER_REFER_AMT,objectbject.getString(Constant.USER_REFER_AMT));
                                storeinfo.setString(Constant.FRIEND_ONE,objectbject.getString(Constant.FRIEND_ONE));
                                storeinfo.setString(Constant.FRIEND_SECOND,objectbject.getString(Constant.FRIEND_SECOND));
                                storeinfo.setString(Constant.EXPIRY_DAY,objectbject.getString(Constant.EXPIRY_DAY));

                                Constant.ORDER_DAY_LIMIT = Integer.parseInt(objectbject.getString("max_product_return_days"));
                                Constant.SETTING_MAIL_ID = objectbject.getString("reply_email");
                                Constant.MINIMUM_WITHDRAW_AMOUNT = Double.parseDouble(objectbject.getString("minimum_withdrawal_amount"));
                                Constant.MAX_EARN_AMOUNT = objectbject.getString("max_refer_earn_amount");
                                Constant.REFER_EARN_ORDER_AMOUNT = objectbject.getString("min_refer_earn_order_amount");
                                Constant.REFER_EARN_METHOD = objectbject.getString("refer_earn_method");
                                Constant.REFER_EARN_BONUS = objectbject.getString("refer_earn_bonus");
                                Constant.KEY_FCM_ID =  objectbject.getString("key_fcm_id");
                                Constant.YOUTUBECODE =  objectbject.getString("video_code");
                                Constant.DEVICE_REG_MSG = objectbject.getString("device_reg_msg");
                                Constant.FREE_DELIVERY_MSG = objectbject.getString("free_msg");
                                Constant.REDIRECT_URL = objectbject.getString("web_url");


                                Constant.RAZOR_PAY_KEY_VALUE = objectbject.getString("razor_key_id");
                                session.setData(Constant.KEY_FCM_ID, objectbject.getString("key_fcm_id"));

                                if(objectbject.getBoolean("is_refer"))
                                {
                                    Constant.REFER_EARN_ACTIVE = "1";
                                }
                                else{
                                    Constant.REFER_EARN_ACTIVE = "0";
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, activity, BASEPATH + SETTINGS_PAGE, params, true);

    }


    public static ArrayList<ModelProduct>GetProductList_2(JSONArray jsonArray_products,ArrayList<Mesurrment> mesurrment)
    {
        ArrayList<ModelProduct> arrayList_vertical  = new ArrayList<>();;
        try{
            for(int i =0; i < jsonArray_products.length();i++)
            {
                ModelProduct vertical_productList =  new ModelProduct();
                JSONObject mjson_obj = jsonArray_products.getJSONObject(i);
                if(mjson_obj.length() > 0)
                {

                    vertical_productList.setId(mjson_obj.getString("productId"));
                    vertical_productList.setName(mjson_obj.getString("title"));
                    vertical_productList.setDescription(mjson_obj.getString("description").substring(0, 1).toUpperCase() + mjson_obj.getString("description").substring(1));
                    vertical_productList.setFrProductId(mjson_obj.getString("frProductId"));

                    vertical_productList.setCatId(mjson_obj.getString("catId"));
                    vertical_productList.setFranchiseId(mjson_obj.getString("franchiseId"));
                    vertical_productList.setPacket(mjson_obj.getBoolean("isPacket"));

                    //product image
                    JSONArray mjsonarr_prodimg = mjson_obj.getJSONArray("productImg");
                    for(int j = 0; j< mjsonarr_prodimg.length(); j++)
                    {
                        JSONObject mjson_prodimg = mjsonarr_prodimg.getJSONObject(j);
                        if(mjson_prodimg.getBoolean("isMain"))
                        {
                            //Log.d("image===>", Constant.PRODUCTIMAGEPATH+mjson_prodimg.getString("title"));
                            vertical_productList.setProduct_img(Constant.PRODUCTIMAGEPATH+mjson_prodimg.getString("title"));
                            vertical_productList.setProduct_img_id(mjson_prodimg.getString("productId"));
                        }

                    }

                    //product variants
                    if(mjson_obj.getJSONArray("productvar").length()>0)
                    {
                        JSONArray mjsonarr_prodvar = mjson_obj.getJSONArray("productvar");
                        ArrayList<ModelProductVariation> arr_productVariations  = new ArrayList<>();;
                        for(int k =0;  k< mjsonarr_prodvar.length() ; k++)
                        {
                            ModelProductVariation productVariation = new ModelProductVariation();
                            JSONObject mjson_prodvar = mjsonarr_prodvar.getJSONObject(k);
                            productVariation.setIs_active(mjson_prodvar.getString("is_active"));

                            if(mjson_prodvar.getString("is_active").equalsIgnoreCase("1"))
                            {
                                productVariation.setServe_for("Available");
                            }
                            else{
                                productVariation.setServe_for(Constant.SOLDOUT_TEXT);
                            }

                            productVariation.setId(mjson_prodvar.getString("_id"));


                            String measurment_str="" ;
                            for(int p = 0; p<mesurrment.size(); p++)
                            {
                                Mesurrment mesurrment1 = mesurrment.get(p);

                                Log.d("mesurrment1", mesurrment1.getId());
                                if(mesurrment1.getId().equalsIgnoreCase(mjson_prodvar.getString("unit")))
                                {
                                    measurment_str = mesurrment1.getAbv().toLowerCase();
                                    break;
                                }
                            }



                            productVariation.setMeasurement(measurment_str);

                            productVariation.setMeasurement_unit_name(mjson_prodvar.getString("measurment"));
                            String discountpercent = "0", productPrice = " ";
                            if (mjson_prodvar.getString("disc_price").equals("0"))
                                productPrice = mjson_prodvar.getString("price");
                            else {
                                discountpercent = ApiConfig.GetDiscount(mjson_prodvar.getString("price"), mjson_prodvar.getString("disc_price"));
                                productPrice = mjson_prodvar.getString("price");
                            }
                            String prod_type;
                            if(mjson_obj.getBoolean("isPacket"))
                            {
                                prod_type = "Packet";

                            }
                            else{
                                prod_type = "loose";
                            }
                            productVariation.setType(prod_type);
                            productVariation.setPrice(productPrice);
                            productVariation.setDiscountpercent(discountpercent);
                            productVariation.setDiscounted_price(mjson_prodvar.getString("disc_price"));

                             /*if(mjson_prodvar.getString("qty").equalsIgnoreCase("null")){
                                 productVariation.setStock("100");
                             }
                             else{
                                 productVariation.setStock(String.valueOf(mjson_prodvar.getInt("qty")));
                             }*/
                            productVariation.setStock(String.valueOf(mjson_prodvar.getInt("qty")));
                            productVariation.setDescription(mjson_prodvar.getString("description").substring(0, 1).toUpperCase()+ mjson_prodvar.getString("description").substring(1) );
                            productVariation.setCatId(mjson_prodvar.getString("catId"));
                            productVariation.setFrproductId(mjson_prodvar.getString("frproductId"));
                            productVariation.setProductId(mjson_prodvar.getString("productId"));
                            productVariation.setFranchiseId(mjson_prodvar.getString("franchiseId"));
                            arr_productVariations.add(productVariation);
                        }

                         /*System.out.println("\nUsing for-each loop\n");
                          for (ProductVariation str : arr_productVariations)
                         {
                          System.out.println("value array list==>"+str.getPrice());
                         }*/

                        vertical_productList.setPriceVariations(arr_productVariations);
                    }
                    arrayList_vertical.add(vertical_productList);

                }
                else{
                    //no data in object
                }



            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //Log.d("list", arrayList_vertical.toString());

        return  arrayList_vertical;

    }


    public static ArrayList<ModelProduct>GetFeatureProduct_2(JSONArray jsonArray_products,ArrayList<Mesurrment> mesurrment)
    {
        ArrayList<ModelProduct> arrayList_vertical  = new ArrayList<>();;
        try{
            for(int i =0; i < jsonArray_products.length();i++)
            {
                ModelProduct vertical_productList =  new ModelProduct();
                JSONObject mjson_obj = jsonArray_products.getJSONObject(i);
                if(mjson_obj.length() > 0)
                {
                    vertical_productList.setId(mjson_obj.getString("productId"));

                    if(mjson_obj.getJSONArray("product").length() > 0)
                    {
                        vertical_productList.setName(mjson_obj.getJSONArray("product").getJSONObject(0).getString("title").substring(0, 1).toUpperCase() + mjson_obj.getJSONArray("product").getJSONObject(0).getString("title").substring(1));
                        vertical_productList.setDescription(mjson_obj.getJSONArray("product").getJSONObject(0).getString("description").substring(0, 1).toUpperCase() + mjson_obj.getJSONArray("product").getJSONObject(0).getString("description").substring(1));
                        vertical_productList.setFrProductId(mjson_obj.getJSONArray("product").getJSONObject(0).getString("_id"));
                    }
                    else{
                        vertical_productList.setName("");
                        vertical_productList.setDescription("");
                        vertical_productList.setFrProductId("");
                    }

                    vertical_productList.setCatId(mjson_obj.getString("catId"));
                    vertical_productList.setFranchiseId(mjson_obj.getString("franchiseId"));
                    vertical_productList.setPacket(mjson_obj.getBoolean("isPacket"));

                    //product image
                    JSONArray mjsonarr_prodimg = mjson_obj.getJSONArray("productImg");
                    if(mjsonarr_prodimg.length() > 0)
                    {
                        for(int j = 0; j< mjsonarr_prodimg.length(); j++)
                        {
                            JSONObject mjson_prodimg = mjsonarr_prodimg.getJSONObject(j);
                            if(mjson_prodimg.getBoolean("isMain"))
                            {
                                vertical_productList.setProduct_img(Constant.PRODUCTIMAGEPATH+mjson_prodimg.getString("title"));
                                vertical_productList.setProduct_img_id(mjson_prodimg.getString("productId"));
                            }

                        }
                    }
                    else{
                        vertical_productList.setProduct_img("");
                        vertical_productList.setProduct_img_id("");
                    }

                    //product variants
                    if(mjson_obj.getJSONArray("productvar").length()>0)
                    {
                        JSONArray mjsonarr_prodvar = mjson_obj.getJSONArray("productvar");
                        ArrayList<ModelProductVariation> arr_productVariations  = new ArrayList<>();;
                        for(int k =0;  k< mjsonarr_prodvar.length() ; k++)
                        {
                            ModelProductVariation productVariation = new ModelProductVariation();
                            JSONObject mjson_prodvar = mjsonarr_prodvar.getJSONObject(k);
                            productVariation.setIs_active(mjson_prodvar.getString("is_active"));

                            if(mjson_prodvar.getString("is_active").equalsIgnoreCase("1"))
                            {
                                productVariation.setServe_for("Available");
                            }
                            else{
                                productVariation.setServe_for(Constant.SOLDOUT_TEXT);
                            }

                            productVariation.setId(mjson_prodvar.getString("_id"));

                            String measurment_str="" ;
                            for(int p = 0; p<mesurrment.size(); p++)
                            {
                                Mesurrment mesurrment1 = mesurrment.get(p);
                                if(mesurrment1.getId().equalsIgnoreCase(mjson_prodvar.getString("unit")))
                                {
                                    measurment_str = mesurrment1.getAbv().toLowerCase();
                                    break;
                                }
                            }

                            /*for(int l=0; l<mesurrment.size(); l++)
                            {
                                if(mjson_prodvar.getString("unit").equalsIgnoreCase("1"))
                                {
                                    measurment_str =  "kg";
                                    break;
                                }
                                else if(mjson_prodvar.getString("unit").equalsIgnoreCase("2"))
                                {
                                    measurment_str =  "gm";
                                    break;
                                }
                                else if(mjson_prodvar.getString("unit").equalsIgnoreCase("3"))
                                {
                                    measurment_str =  "ltr";
                                    break;
                                }
                                else if(mjson_prodvar.getString("unit").equalsIgnoreCase("4"))
                                {
                                    measurment_str =  "ml";
                                    break;
                                }
                                else if(mjson_prodvar.getString("unit").equalsIgnoreCase("5"))
                                {
                                    measurment_str =  "pack";
                                    break;
                                }
                                else if(mjson_prodvar.getString("unit").equalsIgnoreCase("6"))
                                {
                                    measurment_str =  "pcs";
                                    break;
                                }
                                else if(mjson_prodvar.getString("unit").equalsIgnoreCase("7"))
                                {
                                    measurment_str =  "m";
                                    break;
                                }
                            }*/
                            productVariation.setMeasurement(measurment_str);

                            productVariation.setMeasurement_unit_name(mjson_prodvar.getString("measurment"));
                            String discountpercent = "0", productPrice = " ";
                            if (mjson_prodvar.getString("disc_price").equals("0"))
                                productPrice = mjson_prodvar.getString("price");
                            else {
                                discountpercent = ApiConfig.GetDiscount(mjson_prodvar.getString("price"), mjson_prodvar.getString("disc_price"));
                                productPrice = mjson_prodvar.getString("price");
                            }
                            String prod_type;
                            if(mjson_obj.getBoolean("isPacket"))
                            {
                                prod_type = "Packet";

                            }
                            else{
                                prod_type = "loose";
                            }
                            productVariation.setType(prod_type);
                            productVariation.setPrice(productPrice);
                            productVariation.setDiscountpercent(discountpercent);
                            productVariation.setDiscounted_price(mjson_prodvar.getString("disc_price"));

                             /*if(mjson_prodvar.getString("qty").equalsIgnoreCase("null")){
                                 productVariation.setStock("100");
                             }
                             else{
                                 productVariation.setStock(String.valueOf(mjson_prodvar.getInt("qty")));
                             }*/
                            productVariation.setStock(String.valueOf(mjson_prodvar.getInt("qty")));
                            productVariation.setDescription(mjson_prodvar.getString("description").substring(0, 1).toUpperCase() + mjson_prodvar.getString("description").substring(1));
                            productVariation.setCatId(mjson_prodvar.getString("catId"));
                            productVariation.setFrproductId(mjson_prodvar.getString("frproductId"));
                            productVariation.setProductId(mjson_prodvar.getString("productId"));
                            productVariation.setFranchiseId(mjson_prodvar.getString("franchiseId"));
                            arr_productVariations.add(productVariation);
                        }

                         /*System.out.println("\nUsing for-each loop\n");
                          for (ProductVariation str : arr_productVariations)
                         {
                          System.out.println("value array list==>"+str.getPrice());
                         }*/

                        vertical_productList.setPriceVariations(arr_productVariations);
                    }

                    arrayList_vertical.add(vertical_productList);

                }
                else{
                    //no data in object
                }

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Log.d("list", arrayList_vertical.toString());

        return  arrayList_vertical;

    }


    public static ModelProduct GetCartList2(JSONArray jsonArray, String vid, String qty, DatabaseHelper databaseHelper,ArrayList<Mesurrment> measurement_list)
    {
        ModelProduct modelProduct = null;
        try {
            Log.d("array", jsonArray.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONArray productvar = jsonObject.getJSONArray("productvar");
                    ArrayList<ModelProductVariation> priceVariations = new ArrayList<>();

                    for (int j = 0; j < productvar.length(); j++)
                    {
                        JSONObject obj = productvar.getJSONObject(j);
                        String discountpercent = "0", productPrice = "";
                        if (obj.getString("disc_price").equals("0"))
                            productPrice = obj.getString("price");
                        else {
                            discountpercent = GetDiscount( obj.getString("price"), obj.getString("disc_price") );
                            productPrice = obj.getString("price");
                        }

                        String prod_type;
                        if(jsonObject.getBoolean("isPacket"))
                        {
                            prod_type = "Packet";

                        }
                        else{
                            prod_type = "loose";
                        }

                        priceVariations.clear();

                        if (obj.getString("_id").equals(vid))
                        {
                            if(obj.getString("is_active").equalsIgnoreCase("0"))
                            {
                                //SOLD OUT
                                databaseHelper.DeleteOrderData(obj.getString("_id"), obj.getString("productId"));
                            }
                            else {
                                int quantity = Integer.parseInt(qty);
                                Log.d("qty", ""+quantity);
                                Log.d("productPrice", ""+productPrice);
                                double totalprice = Double.parseDouble(decimalFormat.format(quantity * Double.parseDouble(productPrice)));

                                JSONArray image_arr = jsonObject.getJSONArray("productImg");
                                String image_url="";
                                for(int k = 0; k< image_arr.length(); k++)
                                {
                                    JSONObject mjson_prodimg = image_arr.getJSONObject(k);
                                    if(mjson_prodimg.getBoolean("isMain"))
                                    {
                                        image_url = Constant.PRODUCTIMAGEPATH + mjson_prodimg.getString("title");
                                        break;
                                    }
                                }

                                String measurment_str="" ;
                                for(int p = 0; p<measurement_list.size(); p++)
                                {
                                    Mesurrment mesurrment1 = measurement_list.get(p);
                                    if(mesurrment1.getId().equalsIgnoreCase(obj.getString("unit")))
                                    {
                                        measurment_str = mesurrment1.getAbv().toLowerCase();
                                        break;
                                    }
                                }


                                /*for(int l=0; i<measurement_list.size(); l++)
                                {
                                    if(obj.getString("unit").equalsIgnoreCase("1"))
                                    {
                                        measurment_str =  "kg";
                                        break;
                                    }
                                    else if(obj.getString("unit").equalsIgnoreCase("2"))
                                    {
                                        measurment_str =  "gm";
                                        break;
                                    }
                                    else if(obj.getString("unit").equalsIgnoreCase("3"))
                                    {
                                        measurment_str =  "ltr";
                                        break;
                                    }
                                    else if(obj.getString("unit").equalsIgnoreCase("4"))
                                    {
                                        measurment_str =  "ml";
                                        break;
                                    }
                                    else if(obj.getString("unit").equalsIgnoreCase("5"))
                                    {
                                        measurment_str =  "pack";
                                        break;
                                    }
                                    else if(obj.getString("unit").equalsIgnoreCase("6"))
                                    {
                                        measurment_str =  "pcs";
                                        break;
                                    }
                                    else if(obj.getString("unit").equalsIgnoreCase("7"))
                                    {
                                        measurment_str =  "m";
                                        break;
                                    }
                                }*/

                                databaseHelper.UpdateOrderData(obj.getString("_id"), obj.getString("productId"), obj.getString("productId") , obj.getString("franchiseId"), obj.getString("frproductId"),obj.getString("catId") ,qty, totalprice, obj.getString("price"),measurment_str +"@"+  obj.getString("measurment") + "==" + jsonObject.getString("title") + "==" + productPrice.split("=")[0],image_url);

                                ModelProductVariation modelPriceVariation = new ModelProductVariation();
                                modelPriceVariation.setId(obj.getString("_id"));
                                modelPriceVariation.setMeasurement(measurment_str);
                                modelPriceVariation.setMeasurement_unit_name(obj.getString("measurment"));
                                modelPriceVariation.setPrice(obj.getString("price"));
                                modelPriceVariation.setDiscounted_price(obj.getString("disc_price"));
                                modelPriceVariation.setDiscountpercent(discountpercent);
                                modelPriceVariation.setStock(obj.getString("qty"));


                                modelPriceVariation.setDescription(obj.getString("description").substring(0, 1).toUpperCase() + obj.getString("description").substring(1));
                                modelPriceVariation.setCatId(obj.getString("catId"));
                                modelPriceVariation.setFrproductId(obj.getString("frproductId"));
                                modelPriceVariation.setProductId(obj.getString("productId"));
                                modelPriceVariation.setFranchiseId(obj.getString("franchiseId"));
                                modelPriceVariation.setTotalprice(totalprice);

                                modelPriceVariation.setType(prod_type);

                                modelPriceVariation.setQty(quantity);
                                priceVariations.add(modelPriceVariation);

                            }
                            break;
                        }
                    }

                    if (priceVariations.size() != 0)
                    {
                        modelProduct=new ModelProduct();
                        modelProduct.setId(jsonObject.getString("productId"));
                        modelProduct.setName(jsonObject.getString("title").substring(0, 1).toUpperCase() + jsonObject.getString("title").substring(1));
                        modelProduct.setFrProductId(jsonObject.getString("frProductId"));
                        modelProduct.setCatId(jsonObject.getString("catId"));
                        modelProduct.setFranchiseId(jsonObject.getString("franchiseId"));
                        modelProduct.setPacket(jsonObject.getBoolean("isPacket"));
                        modelProduct.setDescription(jsonObject.getString("description").substring(0, 1).toUpperCase() + jsonObject.getString("description").substring(1));

                        JSONArray mjsonarr_prodimg = jsonObject.getJSONArray("productImg");
                        for(int j = 0; j< mjsonarr_prodimg.length(); j++)
                        {
                            JSONObject mjson_prodimg = mjsonarr_prodimg.getJSONObject(j);
                            if(mjson_prodimg.getBoolean("isMain"))
                            {
                                modelProduct.setProduct_img(Constant.PRODUCTIMAGEPATH+mjson_prodimg.getString("title"));
                                modelProduct.setProduct_img_id(mjson_prodimg.getString("productId"));
                                break;
                            }
                            else{
                                modelProduct.setProduct_img("noimage");
                                modelProduct.setProduct_img_id("0");
                            }

                        }

                        modelProduct.setPriceVariations(priceVariations);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelProduct;
    }


    public static void setOrderTrackerLayout_2(Activity activity, OrderTracker_2 order, RecyclerView.ViewHolder holder) {
       try {

           for (int i = 0; i < order.getOrderStatusArrayList().size(); i++)
           {
               int img = activity.getResources().getIdentifier("img" + i, "id", activity.getPackageName());
               int view = activity.getResources().getIdentifier("l" + i, "id", activity.getPackageName());
               int txt = activity.getResources().getIdentifier("txt" + i, "id", activity.getPackageName());
               int textview = activity.getResources().getIdentifier("txt" + i + "" + i, "id", activity.getPackageName());


               // System.out.println("===============" + img + " == " + view);
               View v = holder.itemView;
               if (img != 0 && v.findViewById(img) != null) {
                   ImageView imageView = v.findViewById(img);
                   imageView.setColorFilter(activity.getResources().getColor(R.color.colorAccent));
               }
               if (view != 0 && v.findViewById(view) != null) {
                   View view1 = v.findViewById(view);
                   view1.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
               }
               if (txt != 0 && v.findViewById(txt) != null) {
                   TextView view1 = v.findViewById(txt);
                   view1.setTextColor(activity.getResources().getColor(R.color.black));
               }
               if (textview != 0 && v.findViewById(textview) != null) {
                   TextView view1 = v.findViewById(textview);
                   String str_date = order.getOrderStatusArrayList().get(i).getStatusdate();
                   view1.setText(str_date);
               }
           }
       }
       catch (Exception ex)
       {
           ex.printStackTrace();
       }
    }



    public static void GetPaymentConfig_2(Activity activity,Session session)
    {
       //Constant.RAZOR_PAY_KEY_VALUE = objectbject.getString("razor_key_id");
       Log.d("razor_key_id", Constant.RAZOR_PAY_KEY_VALUE);

        /*Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback()
        {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("res======" + response);
                if (result) {
                    try {


                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray_1 = object.getJSONArray("data");
                        JSONArray jsonArray_payment_type = jsonArray_1.getJSONArray(5);//payment type
                        JSONObject mobj_payment = jsonArray_payment_type.getJSONObject(0);
                        JSONObject mobj_payment_methods = mobj_payment.getJSONObject("payment_methods");

                        Constant.RAZOR_PAY_KEY_VALUE = mobj_payment_methods.getString("razorpay_key");
                        Constant.RAZORPAY = mobj_payment_methods.getString("razorpay_payment_method");




                        Log.d("razor pay key", Constant.RAZOR_PAY_KEY_VALUE);

                        //Constant.MERCHANT_KEY = mobj_payment_methods.getString("payumoney_merchant_key");
                        //Constant.MERCHANT_ID = mobj_payment_methods.getString("payumoney_merchant_id");
                        //Constant.MERCHANT_SALT = mobj_payment_methods.getString("payumoney_salt");
                        //Constant.PAYPAL = mobj_payment_methods.getString("paypal_payment_method");
                        //Constant.PAYUMONEY = mobj_payment_methods.getString("payumoney_payment_method");
                       //Constant.RAZOR_PAY_KEY_VALUE = "rzp_test_fav4Dtczn6dmMT";

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, BASEPATH + GET_CONFIGSETTING , params, false);

         */

    }


    public static void Call_GuestToken(final Activity activity, final Session session, final StorePrefrence storeinfo)
    {
        //Session and Store Preference Clear
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_POST_GUEST(new VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("res======" + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data_jsonobj = object.getJSONObject("data");
                        session.setData(AUTHTOKEN, data_jsonobj.getString("authtoken"));
                        session.setData("role", data_jsonobj.getJSONObject("user").getString("role_type"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, BASEPATH + GUEST, params, false);

        /*if(session.getData("role").equalsIgnoreCase(""))
        {
            Map<String, String> params = new HashMap<String, String>();
            ApiConfig.RequestToVolley_POST_GUEST(new VolleyCallback() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSuccess(boolean result, String response) {
                    System.out.println("res======" + response);
                    if (result) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject data_jsonobj = object.getJSONObject("data");
                            session.setData(AUTHTOKEN, data_jsonobj.getString("authtoken"));
                            session.setData("role", data_jsonobj.getJSONObject("user").getString("role_type"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, activity, BASEPATH + GUEST, params, false);
        }
        else{
             // session is already created

        }*/



    }



    public static void GetSettingConfigApi(Activity activity, final Session session)
    {
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(boolean result, String response)
            {
                System.out.println("res======" + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray_1 = object.getJSONArray("data");

                        JSONArray jsonArray_address = jsonArray_1.getJSONArray(0);//Address

                        JSONArray jsonArray_measurement = jsonArray_1.getJSONArray(1);//MEASUREMENT UNIT

                        JSONArray jsonArray_timeslot = jsonArray_1.getJSONArray(2);//Time  slot
                        JSONArray jsonArray_dayslot = jsonArray_1.getJSONArray(3);//Day  slot
                        JSONArray jsonArray_payment_type = jsonArray_1.getJSONArray(4);//payment type

                        //JSONArray jsonArray = jsonArray_1.getJSONArray(5);//PaymentConfig

                        JSONArray jsonArray_status = jsonArray_1.getJSONArray(6);//status config

                        //JSONObject jsonObject = jsonArray.getJSONObject(0);
                        //JSONObject payment_obj =  jsonObject.getJSONObject("payment_methods");

                        session.setData(Constant.KEY_MEASUREMENT, jsonArray_measurement.toString());
                        session.setData(Constant.KEY_ADDRESS, jsonArray_address.toString());
                        session.setData(Constant.KEY_TIMESLOT, jsonArray_timeslot.toString());
                        session.setData(Constant.KEY_DAYSLOT, jsonArray_dayslot.toString());

                        session.setData(Constant.KEY_PAYMENT_TYPE, jsonArray_payment_type.toString());
                        //session.setData(Constant.KEY_PAYMENT_METHOD, payment_obj.toString());

                        String status = "";
                        if(jsonArray_status.length() == 1)
                        {
                            if(jsonArray_status.getJSONObject(0).getString("status").equalsIgnoreCase("1"))
                                status = "received";
                            else if(jsonArray_status.getJSONObject(0).getString("status").equalsIgnoreCase("2"))
                                status = "processed";
                            else if(jsonArray_status.getJSONObject(0).getString("status").equalsIgnoreCase("3"))
                                status = "shipped";
                            else if(jsonArray_status.getJSONObject(0).getString("status").equalsIgnoreCase("4"))
                                status = "delivered";
                            else if(jsonArray_status.getJSONObject(0).getString("status").equalsIgnoreCase("5"))
                                status = "returned";
                        }
                        else{
                            List<String> list = new ArrayList<>();
                            for(int i= 0; i<jsonArray_status.length();i++)
                            {
                                if(jsonArray_status.getJSONObject(i).getString("status").equalsIgnoreCase("1"))
                                    status = "received";
                                else if(jsonArray_status.getJSONObject(i).getString("status").equalsIgnoreCase("2"))
                                    status = "processed";
                                else if(jsonArray_status.getJSONObject(i).getString("status").equalsIgnoreCase("3"))
                                    status = "shipped";
                                else if(jsonArray_status.getJSONObject(i).getString("status").equalsIgnoreCase("4"))
                                    status = "delivered";
                                else if(jsonArray_status.getJSONObject(i).getString("status").equalsIgnoreCase("5"))
                                    status = "returned";

                                list.add(status);
                            }
                            status = String.join(",", list);
                        }

                        //Log.d("status", status);
                        session.setData(Constant.KEY_STATUS, status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, BASEPATH + GET_CONFIGSETTING , params, false);
    }



    public static void GetMessurmentApi(Activity activity, final Session session)
    {
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(boolean result, String response)
            {
                //System.out.println("res======" + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        session.setData(Constant.KEY_MEASUREMENT, object.getJSONArray("data").getJSONArray(1).toString());//MEASUREMENT UNIT
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, BASEPATH + GET_CONFIGSETTING , params, false);
    }

    /*================================================== My Method End ======================================================= */

    /*
    * Below Method Are Old IFresh App Method Please do not delete or Alter Method
    *
    *
    *
    *
    *
    *
    *
    *
    * */

    /*==================================================== Old Method Start ======================================================= */

    public static String VolleyErrorMessage(VolleyError error) {
        String message = "";
        try {
            if (error instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!!";
            } else if (error instanceof AuthFailureError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ParseError) {
                message = "Parsing error! Please try again after some time!!";
            } else if (error instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            } else
                message = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static void RequestToVolley(final VolleyCallback callback, final Activity activity, final String url, final Map<String, String> params, final boolean isprogress) {
        final ProgressDisplay progressDisplay = new ProgressDisplay(activity);

        if (AppController.isConnected(activity))
        {
            if (isprogress)
                if(url.equalsIgnoreCase(Constant.SETTINGHOME))
                {
                    progressDisplay.hideProgress();
                }
                else{
                    progressDisplay.showProgress();
                }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    System.out.println("================= " + url + " == " + response);
                    callback.onSuccess(true, response);
                    if (isprogress)
                        progressDisplay.hideProgress();
                }

            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (isprogress)
                                progressDisplay.hideProgress();

                            callback.onSuccess(false, "");
                            String message = VolleyErrorMessage(error);
                            if (!message.equals(""))
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params1 = new HashMap<String, String>();
                    params1.put(AUTHORIZATION, "Bearer " + createJWT("eKart", "eKart Authentication"));
                    return params1;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    params.put(Constant.AccessKey, Constant.AccessKeyVal);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().getRequestQueue().getCache().clear();
            AppController.getInstance().addToRequestQueue(stringRequest);
        }

    }


    public static void GetPayment_Api(Activity activity, final Context ctx)
    {
        final StorePrefrence storeinfo = new StorePrefrence(ctx);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET_SETING(new VolleyCallback()
        {
            @Override
            public void onSuccess(boolean result, String response)
            {
                if (result)
                {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray data = jsonObject.getJSONArray("data");
                        JSONArray payment_type = data.getJSONArray(5);
                        JSONObject object = payment_type.getJSONObject(0);

                        Constant.MERCHANT_KEY = object.getString(Constant.PAY_M_KEY);
                        Constant.MERCHANT_ID = object.getString(Constant.PAYU_M_ID);
                        Constant.MERCHANT_SALT = object.getString(Constant.PAYU_SALT);

                        Constant.RAZOR_PAY_KEY_VALUE = object.getString(Constant.RAZOR_PAY_KEY);

                        Constant.PAYPAL = object.getString(Constant.paypal_method);
                        Constant.PAYUMONEY = object.getString(Constant.payu_method);
                        Constant.RAZORPAY = object.getString(Constant.razor_pay_method);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }






        }, activity, BASEPATH + GET_CONFIGSETTING, params, false);

    }

    public static String createJWT(String issuer, String subject) {
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            byte[] apiKeySecretBytes = Constant.JWT_KEY.getBytes();
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            JwtBuilder builder = Jwts.builder()
                    .setIssuedAt(now)
                    .setSubject(subject)
                    .setIssuer(issuer)
                    .signWith(signatureAlgorithm, signingKey);

            return builder.compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static boolean CheckValidattion(String item, boolean isemailvalidation, boolean ismobvalidation) {
        if (item.length() == 0)
            return true;
        else if (isemailvalidation && (!android.util.Patterns.EMAIL_ADDRESS.matcher(item).matches()))
            return true;
        else return ismobvalidation && (item.length() < 10 || item.length() > 12);
    }


    public static String GetDiscount(String oldprice, String newprice) {
        double dold = Double.parseDouble(oldprice);
        double dnew = Double.parseDouble(newprice);

        //return String.valueOf(((dnew / dold) - 1) * 100);
        return " (" + String.format("%.2f", (((dnew / dold) - 1) * 100)) + "%)";
    }


    public static ArrayList<Product> GetProductList(JSONArray jsonArray)
    {
        ArrayList<Product> productArrayList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                    JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);
                    for (int j = 0; j < pricearray.length(); j++)
                    {
                        JSONObject obj = pricearray.getJSONObject(j);
                        String discountpercent = "0", productPrice = " ";
                        if (obj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                            productPrice = obj.getString(Constant.PRICE);
                        else {
                            discountpercent = ApiConfig.GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                            productPrice = obj.getString(Constant.DISCOUNTED_PRICE);
                        }
                        priceVariations.add(new PriceVariation(obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), productPrice, obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent));
                    }

                    productArrayList.add(new Product(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES).toString(), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString("indicator")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productArrayList;
    }

    public static Product GetCartList(JSONArray jsonArray, String vid, String qty, DatabaseHelper databaseHelper) {
        Product product = null;
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ArrayList<PriceVariation> priceVariations = new ArrayList<>();
                    JSONArray pricearray = jsonObject.getJSONArray(Constant.VARIANT);
                    for (int j = 0; j < pricearray.length(); j++)
                    {
                        JSONObject obj = pricearray.getJSONObject(j);
                        String discountpercent = "0", productPrice = "";

                        if (obj.getString(Constant.DISCOUNTED_PRICE).equals("0"))
                            productPrice = obj.getString(Constant.PRICE);
                        else {
                            discountpercent = GetDiscount(obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE));
                            productPrice = obj.getString(Constant.DISCOUNTED_PRICE);
                        }
                        priceVariations.clear();

                        if (obj.getString(Constant.ID).equals(vid))
                        {
                            if (obj.getString(Constant.SERVE_FOR).equalsIgnoreCase(Constant.SOLDOUT_TEXT)) {
                                databaseHelper.DeleteOrderData(vid, obj.getString(Constant.PRODUCT_ID));
                            } else {
                                int quantity = Integer.parseInt(qty);
                                double totalprice = Double.parseDouble(decimalFormat.format(quantity * Double.parseDouble(productPrice)));
                                databaseHelper.UpdateOrderData(obj.getString(Constant.ID), obj.getString(Constant.PRODUCT_ID), obj.getString("productId") ,obj.getString(Constant.FRANCHISCID), obj.getString(Constant.FRANCHISEPID),"" ,qty, totalprice, "",obj.getString(Constant.MEASUREMENT) +  obj.getString(Constant.MEASUREMENT_UNIT_ID) + "==" + jsonObject.getString(Constant.NAME) + "==" + productPrice.split("=")[0],"");
                                priceVariations.add(new PriceVariation(obj.getString(Constant.ID),  obj.getString("productId") ,obj.getString(Constant.TYPE), obj.getString(Constant.MEASUREMENT), obj.getString(Constant.MEASUREMENT_UNIT_ID), productPrice, obj.getString(Constant.PRICE), obj.getString(Constant.DISCOUNTED_PRICE), obj.getString(Constant.SERVE_FOR), obj.getString(Constant.STOCK), obj.getString(Constant.STOCK_UNIT_ID), obj.getString(Constant.MEASUREMENT_UNIT_NAME), obj.getString(Constant.STOCK_UNIT_NAME), discountpercent, quantity, totalprice));
                            }
                            break;
                        }
                    }
                    if (priceVariations.size() != 0) {
                        product = new Product(jsonObject.getString(Constant.ID), jsonObject.getString(Constant.NAME), jsonObject.getString(Constant.SLUG), jsonObject.getString(Constant.SUC_CATE_ID), jsonObject.getString(Constant.IMAGE), jsonObject.getJSONArray(Constant.OTHER_IMAGES).toString(), jsonObject.getString(Constant.DESCRIPTION), jsonObject.getString(Constant.STATUS), jsonObject.getString(Constant.DATE_ADDED), jsonObject.getString(Constant.CATEGORY_ID), priceVariations, jsonObject.getString("indicator"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }


    public static void addMarkers(int currentPage, ArrayList<Slider> imglist, LinearLayout mMarkersLayout, Activity activity) {

       try{
            if (activity != null)
            {
            TextView[] markers = new TextView[imglist.size()];

            mMarkersLayout.removeAllViews();

            for (int i = 0; i < markers.length; i++) {
                markers[i] = new TextView(activity);
                markers[i].setText(Html.fromHtml("&#8226;"));
                markers[i].setTextSize(35);
                markers[i].setTextColor(activity.getResources().getColor(R.color.overlay_white));
                mMarkersLayout.addView(markers[i]);
            }
            if (markers.length > 0)
                markers[currentPage].setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            }
       }
       catch (Exception ex)
       {
           ex.printStackTrace();
       }
    }


    public static void setOrderTrackerLayout(Activity activity, OrderTracker order, RecyclerView.ViewHolder holder)
    {
        try {
            for (int i = 0; i < order.getOrderStatusArrayList().size(); i++) {
                int img = activity.getResources().getIdentifier("img" + i, "id", activity.getPackageName());
                int view = activity.getResources().getIdentifier("l" + i, "id", activity.getPackageName());
                int txt = activity.getResources().getIdentifier("txt" + i, "id", activity.getPackageName());
                int textview = activity.getResources().getIdentifier("txt" + i + "" + i, "id", activity.getPackageName());
                // System.out.println("===============" + img + " == " + view);
                View v = holder.itemView;
                if (img != 0 && v.findViewById(img) != null) {
                    ImageView imageView = v.findViewById(img);
                    imageView.setColorFilter(activity.getResources().getColor(R.color.colorAccent));
                }
                if (view != 0 && v.findViewById(view) != null) {
                    View view1 = v.findViewById(view);
                    view1.setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                }
                if (txt != 0 && v.findViewById(txt) != null) {
                    TextView view1 = v.findViewById(txt);
                    view1.setTextColor(activity.getResources().getColor(R.color.black));
                }
                if (textview != 0 && v.findViewById(textview) != null) {
                    TextView view1 = v.findViewById(textview);
                    String str = order.getOrderStatusArrayList().get(i).getStatusdate();
                    String[] splited = str.split("\\s+");
                    view1.setText(splited[0] + "\n" + splited[1]);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void SetFavOnImg(DatabaseHelper databaseHelper, ImageView imgFav, String id, String getFranchiseId, String getFrproductId) {
        if (databaseHelper.getFavouriteById(id)) {
            imgFav.setImageResource(R.drawable.ic_favorite);
            imgFav.setTag("y");
        } else {
            imgFav.setImageResource(R.drawable.ic_favorite_not);
            imgFav.setTag("n");
        }

    }


    public static void AddRemoveFav(DatabaseHelper databaseHelper, ImageView imgFav, String id, String FranchiseId, String FrproductId) {
        if (imgFav.getTag().equals("y"))
        {
            databaseHelper.removeFavouriteById(id);
            imgFav.setImageResource(R.drawable.ic_favorite_not);
            imgFav.setTag("n");
        } else {
            databaseHelper.addFavourite(id, FranchiseId, FrproductId);
            imgFav.setImageResource(R.drawable.ic_favorite);
            imgFav.setTag("y");
        }
    }

    public static void setSnackBar(String message, String action, Activity activity) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();

            }
        });
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }


    public static Drawable buildCounterDrawable(int count, int backgroundImageId, Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);

        // System.out.println("=============count " + count);
        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setVisibility(View.VISIBLE);
            try {
                textView.setText("" + count);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(activity.getResources(), bitmap);
    }

    public static void GetSettings(final Activity activity)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.GET_SETTINGS, Constant.GetVal);
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                  System.out.println("============" + response);
                if (result) {
                    try {
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR)) {
                            JSONObject object = objectbject.getJSONObject(Constant.SETTINGS);
                            Constant.VERSION_CODE = object.getString(Constant.KEY_VERSION_CODE);
                            Constant.REQUIRED_VERSION = object.getString(Constant.KEY_VERSION_CODE);
                            Constant.VERSION_STATUS = object.getString(Constant.KEY_UPDATE_STATUS);
                            Constant.REFER_EARN_BONUS = object.getString(Constant.KEY_REFER_EARN_BONUS);
                            Constant.REFER_EARN_ACTIVE = object.getString(Constant.KEY_REFER_EARN_STATUS);
                            Constant.REFER_EARN_METHOD = object.getString(Constant.KEY_REFER_EARN_METHOD);
                            Constant.REFER_EARN_ORDER_AMOUNT = object.getString(Constant.KEY_MIN_REFER_ORDER_AMOUNT);
                            Constant.MAX_EARN_AMOUNT = object.getString(Constant.KEY_MAX_EARN_AMOUNT);
                            Constant.MINIMUM_WITHDRAW_AMOUNT = Double.parseDouble(object.getString(Constant.KEY_MIN_WIDRAWAL));
                            Constant.SETTING_CURRENCY_SYMBOL = object.getString(Constant.CURRENCY);
                            Constant.SETTING_TAX = Double.parseDouble(object.getString(Constant.TAX));
                            Constant.SETTING_DELIVERY_CHARGE = Double.parseDouble(object.getString(Constant.DELIEVERY_CHARGE));
                            Constant.SETTING_MAIL_ID = object.getString(Constant.REPLY_TO);
                            Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY = Double.parseDouble(object.getString(Constant.MINIMUM_AMOUNT));

                            Constant.ORDER_DAY_LIMIT = Integer.parseInt(object.getString(Constant.KEY_ORDER_RETURN_DAY_LIMIT));

                            if (DrawerActivity.tvWallet != null) {
                               try {
                                   DrawerActivity.tvWallet.setText(activity.getResources().getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                               }
                               catch (Exception ex){
                                   ex.printStackTrace();
                               }
                            }

                            String versionName = "";
                            try {
                                PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                                versionName = packageInfo.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            if (ApiConfig.compareVersion(versionName, Constant.VERSION_CODE) < 0) {
                                //OpenBottomDialog(activity);
                            } else if (ApiConfig.compareVersion(versionName, Constant.REQUIRED_VERSION) < 0) {
                                //OpenBottomDialog(activity);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.ORDERPROCESS_URL, params, false);
    }

    public static void GetPaymentConfig(final Activity activity) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SETTINGS, Constant.GetVal);
        params.put(Constant.GET_PAYMENT_METHOD, Constant.GetVal);
        //  System.out.println("=====params " + params.toString());
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {

                if (result) {
                    try {
                            System.out.println("=====pay config " + response);
                        JSONObject objectbject = new JSONObject(response);
                        if (!objectbject.getBoolean(Constant.ERROR))
                        {
                            JSONObject object = objectbject.getJSONObject(Constant.PAYMENT_METHODS);
                            Constant.MERCHANT_KEY = object.getString(Constant.PAY_M_KEY);
                            Constant.MERCHANT_ID = object.getString(Constant.PAYU_M_ID);
                            Constant.MERCHANT_SALT = object.getString(Constant.PAYU_SALT);

                            Constant.RAZOR_PAY_KEY_VALUE = object.getString(Constant.RAZOR_PAY_KEY);

                            Constant.PAYPAL = object.getString(Constant.paypal_method);
                            Constant.PAYUMONEY = object.getString(Constant.payu_method);
                            Constant.RAZORPAY = object.getString(Constant.razor_pay_method);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.SETTING_URL, params, false);
    }


    public static void OpenBottomDialog(final Activity activity) {
        View sheetView = activity.getLayoutInflater().inflate(R.layout.lyt_terms_privacy, null);
        ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeAllViews();
        }

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        FrameLayout bottomSheet = (FrameLayout) mBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);

        ImageView imgclose = sheetView.findViewById(R.id.imgclose);
        TextView txttitle = sheetView.findViewById(R.id.tvTitle);
        Button btnNotNow = sheetView.findViewById(R.id.btnNotNow);
        Button btnUpadateNow = sheetView.findViewById(R.id.btnUpdateNow);
        if (Constant.VERSION_STATUS.equals("0")) {
            btnNotNow.setVisibility(View.VISIBLE);
            imgclose.setVisibility(View.VISIBLE);
            mBottomSheetDialog.setCancelable(true);
        } else
            mBottomSheetDialog.setCancelable(false);


        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
            }
        });
        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
            }
        });

        btnUpadateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_LINK + activity.getPackageName())));
            }
        });
    }

    public static void displayLocationSettingsRequest(final Activity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;

                }
            }
        });
    }



    public static double getWalletBalance(final Activity activity, Session session)
    {
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("=================*wallet " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getInt(Constant.SUCESS) == 200)
                        {
                            DecimalFormat df = new DecimalFormat("#.##");
                            Constant.WALLET_BALANCE = Double.parseDouble(object.getString("wallet_balance"));
                            DrawerActivity.tvWallet.setText(activity.getResources().getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + df.format(Constant.WALLET_BALANCE));
                        }
                        } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, BASEPATH + Constant.GET_WALLETBAL+session.getData(Session.KEY_id), params, false);
        return Constant.WALLET_BALANCE;
    }

    public static String getDeviceId(Context mContext)
    {
        String Deviceid = "";
        String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        try {
            String ts = Context.TELEPHONY_SERVICE;
            TelephonyManager mTelephonyMgr = (TelephonyManager) mContext.getSystemService(ts);
            String mimsi = mTelephonyMgr.getSubscriberId();
            Deviceid = "IM" + mimsi;

            if (Deviceid == "IM") {
                Deviceid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception ex) {
            Deviceid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return Deviceid + "," + myVersion + "," + sdkVersion;

    }
    public static void getLocation(final Activity activity)
    {
        try {
            if (ContextCompat.checkSelfPermission(activity, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || (ContextCompat.checkSelfPermission(activity, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(activity, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(activity, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED)
                    || (ContextCompat.checkSelfPermission(activity, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED)
                     )
            {

                if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[2])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[3])
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity,permissionsRequired[4])
                )
                {
                    // Show an explanation to the user asynchronously -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    new AlertDialog.Builder(activity)
                            .setTitle(activity.getResources().getString(R.string.location_permission))
                            .setMessage(activity.getResources().getString(R.string.location_permission_message))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE }, 0);
                                     Constant.is_permission_grant=1;

                                }
                            })
                            .create()
                            .show();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }
            } else {
                gps = new GPSTracker(activity);
                if (gps.canGetLocation()) {
                    user_location = gps.getAddressLine(activity);
                }
                if (gps.getIsGPSTrackingEnabled())
                {
                    latitude1 = gps.latitude;
                    longitude1 = gps.longitude;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isGPSEnable(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;
    }

    public static String getAddress(double lat, double lng, Activity activity) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        String address = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                address = add;
            }
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    public static int compareVersion(String version1, String version2) {
        String[] arr1 = version1.split("\\.");
        String[] arr2 = version2.split("\\.");

        int i = 0;
        while (i < arr1.length || i < arr2.length) {
            if (i < arr1.length && i < arr2.length) {
                if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])) {
                    return -1;
                } else if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])) {
                    return 1;
                }
            } else if (i < arr1.length) {
                if (Integer.parseInt(arr1[i]) != 0) {
                    return 1;
                }
            } else {
                if (Integer.parseInt(arr2[i]) != 0) {
                    return -1;
                }
            }

            i++;
        }

        return 0;
    }

    /*================================================== Old Method End ======================================================= */


}
