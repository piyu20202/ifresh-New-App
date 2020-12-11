package com.ifresh.customerr.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ifresh.customerr.R;
import com.ifresh.customerr.adapter.CategoryAdapter;
import com.ifresh.customerr.adapter.OfferAdapter;
import com.ifresh.customerr.adapter.SectionAdapter;
import com.ifresh.customerr.adapter.SliderAdapter;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.AppController;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.DatabaseHelper;
import com.ifresh.customerr.helper.Session;

import com.ifresh.customerr.helper.StorePrefrence;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.kotlin.LocationSelection_K;
import com.ifresh.customerr.kotlin.SignInActivity_K;
import com.ifresh.customerr.model.Category;
import com.ifresh.customerr.model.Mesurrment;
import com.ifresh.customerr.model.OfferImage;
import com.ifresh.customerr.model.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import hotchemi.android.rate.StoreType;

import static com.ifresh.customerr.helper.Constant.AUTHTOKEN;
import static com.ifresh.customerr.helper.Constant.BANNERIMAGE;
import static com.ifresh.customerr.helper.Constant.BANNERIMAGEPATH;
import static com.ifresh.customerr.helper.Constant.BASEPATH;
import static com.ifresh.customerr.helper.Constant.CATEGORYIMAGEPATH;
import static com.ifresh.customerr.helper.Constant.CITY_N;
import static com.ifresh.customerr.helper.Constant.FEATUREPRODUCT;
import static com.ifresh.customerr.helper.Constant.GETCATEGORY;
import static com.ifresh.customerr.helper.Constant.ISAREACHAGE;
import static com.ifresh.customerr.helper.Constant.OFFER_IMAGE;
import static com.ifresh.customerr.helper.Constant.SECTIONPRODUCT;
import static com.ifresh.customerr.helper.Constant.SUBTITLE_1;


public class MainActivity extends DrawerActivity {

    boolean doubleBackToExitPressedOnce = false;
    DatabaseHelper databaseHelper;
    public static Session session;
    Toolbar toolbar;
    public RelativeLayout layoutSearch;
    Activity activity;
    public LinearLayout lytBottom;
    Menu menu;
    String from;
    private RecyclerView categoryRecyclerView, sectionView, offerView;
    private ArrayList<Slider> sliderArrayList;
    private ArrayList<OfferImage> offerImgArrayList;

    public static ArrayList<Category> categoryArrayList, sectionList;
    private ViewPager mPager;
    private LinearLayout mMarkersLayout;
    private int size;
    private Timer swipeTimer;
    private Handler handler;
    private Runnable Update;
    private int currentPage = 0;

    private LinearLayout lytCategory;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar,progress_bar_banner;
    TextView tvlater, tvnever, tvrate,txt_currentloc;
    private Boolean firstTime = null;
    ImageView imgloc;
    String str_cat_id;
    ArrayList<Mesurrment> measurement_list;
    //public static Boolean is_deafultAddExist=false;
    //public static Boolean is_address_save=false,is_default_address_save=false;




    //ReviewManager manager ;
    //ReviewInfo reviewInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        databaseHelper = new DatabaseHelper(MainActivity.this);
        session = new Session(MainActivity.this);
        storeinfo = new StorePrefrence(MainActivity.this);
        Log.d("token",  session.getData(AUTHTOKEN));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        activity = MainActivity.this;
        //from = getIntent().getStringExtra("from");
        progressBar = findViewById(R.id.progressBar);
        txt_currentloc = findViewById(R.id.txt_currentloc);
        progress_bar_banner = findViewById(R.id.progress_bar_banner);
        imgloc = findViewById(R.id.imgloc);
        lytBottom = findViewById(R.id.lytBottom);
        layoutSearch = findViewById(R.id.layoutSearch);
        layoutSearch.setVisibility(View.VISIBLE);


        categoryRecyclerView = findViewById(R.id.categoryrecycleview);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        //categoryRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

        sectionView = findViewById(R.id.sectionView);
        sectionView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        sectionView.setNestedScrollingEnabled(false);

        offerView = findViewById(R.id.offerView);
        offerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        offerView.setNestedScrollingEnabled(false);

        nestedScrollView = findViewById(R.id.nestedScrollView);
        mMarkersLayout = findViewById(R.id.layout_markers);
        lytCategory = findViewById(R.id.lytCategory);
        mPager = findViewById(R.id.pager);

        imgloc.setVisibility(View.VISIBLE);
        txt_currentloc.setVisibility(View.VISIBLE);

        imgloc.setBackgroundResource(R.drawable.ic_editloc);


        callSettingApi_messurment();


        imgloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storeinfo.setBoolean("is_locchange", true);
                Intent intent = new Intent(mContext, LocationSelection_K.class);
                startActivity(intent);
            }
        });

        txt_currentloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeinfo.setBoolean("is_locchange", true);
                Intent intent = new Intent(mContext, LocationSelection_K.class);
                startActivity(intent);
            }
        });


        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                ApiConfig.addMarkers(position, sliderArrayList, mMarkersLayout, MainActivity.this);
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mPager.setPageMargin(5);
        if (session.isUserLoggedIn())
        {
            //tvName.setText(session.getData(session.getData(session.KEY_FIRSTNAME)+" "+ session.getData(session.KEY_LASTNAME)));
            tvName.setText("Login");
            tvMobile.setText(session.getData(session.KEY_mobile));

        } else {
            tvName.setText(getResources().getString(R.string.is_login));
        }
        drawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        drawer, toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close
                ) {
        };

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult)
            {
                String token = instanceIdResult.getToken();
                if (!token.equals(session.getData(Constant.KEY_FCM_ID)))
                {
                    //UpdateToken(token, MainActivity.this);
                    session.setData("token", token);
                }

            }
        });


        if (AppController.isConnected(MainActivity.this))
        {
            ApiConfig.GetSettingConfigApi(activity,session);
            GetSlider();
            GetCategory();
            GetOfferImage();
            if (Constant.REFER_EARN_ACTIVE.equals("0")) {
                Menu nav_Menu = navigationView.getMenu();
                nav_Menu.findItem(R.id.refer).setVisible(false);
            }
        }

    }

    public void askForReview() {

        if(reviewInfo!= null)
        {
            manager.launchReviewFlow(this,reviewInfo)
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(Exception e) {
                        }
                     })
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

        }
   }

    public void initReview() {
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    reviewInfo = task.getResult();
                } else {
                    // There was some problem, continue regardless of the result.
                }
            }
        });


    }

    /*public void setAppLocal(String languageCode) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(languageCode.toLowerCase()));
        resources.updateConfiguration(configuration, dm);
    }*/
    public void SectionProductRequest() {  //json request for product search
        Map<String, String> params = new HashMap<>();
        Log.d("url", BASEPATH + SECTIONPRODUCT +  session.getData(Constant.AREA_ID) +"/" + str_cat_id);

        ApiConfig.RequestToVolley_GET(new VolleyCallback()
        {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                         System.out.println("====res section " + response);
                         //Log.d("url", BASEPATH + SECTIONPRODUCT +  session.getData(Constant.AREA_ID) +"/" + str_cat_id);
                        JSONObject object1 = new JSONObject(response);
                        if (object1.getInt(Constant.SUCESS) == 200)
                        {
                            sectionList = new ArrayList<>();
                            Category section = new Category();
                            //JSONObject jsonObject = jsonArray.getJSONObject(j);
                            section.setName(FEATUREPRODUCT);
                            section.setStyle("style_2");
                            section.setSubtitle(SUBTITLE_1);
                            JSONArray jsonArray_products = object1.getJSONArray(Constant.DATA);
                            section.setProductList(ApiConfig.GetFeatureProduct_2(jsonArray_products,measurement_list) );
                            sectionList.add(section);

                            sectionView.setVisibility(View.VISIBLE);
                            for (int i = 0; i < sectionList.size();i++)
                            {
                                System.out.println("value==>"+sectionList.get(i));
                            }
                            SectionAdapter sectionAdapter = new SectionAdapter(MainActivity.this, sectionList);
                            sectionView.setAdapter(sectionAdapter);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, MainActivity.this, BASEPATH + SECTIONPRODUCT +  session.getData(Constant.AREA_ID) , params, false);
    }

    private void callSettingApi_messurment()
    {
        try{
            String str_measurment = session.getData(Constant.KEY_MEASUREMENT);
            JSONArray jsonArray = new JSONArray(str_measurment);
            measurement_list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object1 = jsonArray.getJSONObject(i);
                measurement_list.add(new Mesurrment(object1.getString("id"), object1.getString("title"), object1.getString("abv")));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }



    private void GetSlider() {
        progress_bar_banner.setVisibility(View.VISIBLE);
        String SliderUrl = BASEPATH + BANNERIMAGE +  session.getData(Constant.AREA_ID);
        //Log.d("SliderUrl===",SliderUrl);
        Map<String, String> params = new HashMap<>();

        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    sliderArrayList = new ArrayList<>();
                    try {
                         //Log.d("response", response);
                         JSONObject object = new JSONObject(response);
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            progress_bar_banner.setVisibility(View.GONE);
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            size = jsonArray.length();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                sliderArrayList.add(new Slider("", "", jsonObject.getString("title"),  BANNERIMAGEPATH + jsonObject.getString("img")) );
                            }
                            mPager.setAdapter(new SliderAdapter(sliderArrayList, MainActivity.this, R.layout.lyt_slider, "home"));
                            ApiConfig.addMarkers(0, sliderArrayList, mMarkersLayout, MainActivity.this);
                            handler = new Handler();
                            Update = new Runnable() {
                                public void run() {
                                    if (currentPage == size) {
                                        currentPage = 0;
                                    }
                                    try {
                                        mPager.setCurrentItem(currentPage++, true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            swipeTimer = new Timer();
                            swipeTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    handler.post(Update);
                                }
                            }, 3000, 2000);
                        }
                       else{
                            progress_bar_banner.setVisibility(View.GONE);
                            Toast.makeText(mContext, object.getString("msg"),Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        progress_bar_banner.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, MainActivity.this, SliderUrl, params, true);

    }


    private void GetCategory() {
        progressBar.setVisibility(View.GONE);
        String CategoryUrl = BASEPATH + GETCATEGORY + session.getData(Constant.AREA_ID);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("======cate " + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        categoryArrayList = new ArrayList<>();
                        categoryArrayList.clear();
                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if(i == 0)
                                    {
                                        str_cat_id =  jsonObject.getString("_id");
                                    }

                                    if(jsonObject.getString("catagory_id").equalsIgnoreCase("null") )
                                    {
                                       Boolean allow_upload ;
                                       if(jsonObject.has("allow_upload"))
                                       {
                                           allow_upload =  jsonObject.getBoolean("allow_upload");
                                       }
                                       else{
                                           allow_upload=false;
                                       }

                                        categoryArrayList.add(new Category(
                                                jsonObject.getString("_id"),
                                                jsonObject.getString("title"),
                                                "",
                                                CATEGORYIMAGEPATH + jsonObject.getString("catagory_img"),
                                                allow_upload
                                                ));

                                    }
                                }
                            }
                            else{
                                categoryArrayList.add(new Category("0","No Category","","",false) );
                            }
                            progressBar.setVisibility(View.GONE);
                            categoryRecyclerView.setAdapter(new CategoryAdapter(MainActivity.this, categoryArrayList, R.layout.lyt_category, "cate", session));
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            lytCategory.setVisibility(View.GONE);
                            Toast.makeText(mContext, object.getString("msg"),Toast.LENGTH_SHORT).show();
                        }
                        SectionProductRequest();

                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        lytCategory.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, MainActivity.this, CategoryUrl, params, true);

    }



    @Override
    public void onResume() {
        super.onResume();
        txt_currentloc.setText(session.getData(CITY_N));
        if (session.isUserLoggedIn())
        {
            tvName.setText(session.getData(session.KEY_FIRSTNAME)+" "+ session.getData(session.KEY_LASTNAME));
        }
        else{
            tvName.setText(getResources().getString(R.string.is_login));
        }

        try{
            if(session.getBoolean("area_change"))
            {
                //Toast.makeText(mContext, "Dear User Your Area is Changed", Toast.LENGTH_SHORT).show();
                showAlertView_LocChange();
                session.setBoolean("area_change",false);
                if(storeinfo.getBoolean("is_locchange"))
                {
                    storeinfo.setBoolean("is_locchange",false);
                    if (AppController.isConnected(MainActivity.this)) {
                        //callApi_fillAdd();
                        //callApidefaultAdd();
                        GetSlider();
                        GetCategory();
                        GetOfferImage();
                        session.setBoolean("area_change",false);
                    }

                }

            }


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }



       if(storeinfo.getBoolean("is_app_updated"))
        {
            //app is updated nothing do
        }
        else {
            //app is not updated
            showAlertView_2();
        }

        invalidateOptionsMenu();
    }

    private void GetOfferImage() {
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        ArrayList<String> offerList = new ArrayList<>();
                        JSONObject objectbject = new JSONObject(response);
                        Log.d("offer", Constant.BASEPATH+Constant.GET_OFFER+session.getData(Constant.AREA_ID));
                        System.out.println("=====>"+response);
                        offerImgArrayList = new ArrayList<>();
                        if (objectbject.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = objectbject.getJSONArray(Constant.DATA);
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                JSONObject object = jsonArray.getJSONObject(i);
                                OfferImage offerImage = new OfferImage();
                                offerImage.setId(object.getString("_id"));
                                //offerImage.setIs_imgscroll(object.getInt("click"));
                                offerImage.setImage(OFFER_IMAGE + object.getString("offer_img"));
                                offerImage.setOffer_title(object.getString("title"));
                                //offerImage.setYoutube_str(object.getString("youtube"));
                                offerImgArrayList.add(offerImage);
                                offerList.add(OFFER_IMAGE + object.getString("offer_img"));
                            }
                            offerView.setAdapter(new OfferAdapter(offerList, offerImgArrayList, R.layout.offer_lyt, MainActivity.this));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, MainActivity.this, Constant.BASEPATH+Constant.GET_OFFER+session.getData(Constant.AREA_ID), params, false);


    }

    public void OnClickBtn(View view)
    {
        int id = view.getId();
        if (id == R.id.lythome) {
            finish();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else if (id == R.id.lytcategory) {
            OnViewAllClick(view);
        } else if (id == R.id.lytfav) {
               if(session.getData(Constant.CAT_ID).length() > 0)
               {
                 startActivity(new Intent(MainActivity.this, FavouriteActivity.class).putExtra("cat_id", session.getData(Constant.CAT_ID)));
               }
               else{
                   Toast.makeText(mContext, "Please Select Category First", Toast.LENGTH_SHORT).show();
               }

        } else if (id == R.id.layoutSearch) {
            //startActivity(new Intent(MainActivity.this, SearchActivity.class).putExtra("from", Constant.FROMSEARCH));
        } else if (id == R.id.lytcart) {
            OpenCart();
        }
    }


    public void OnViewAllClick(View view) {
        startActivity(new Intent(MainActivity.this, ProductCategory.class));
    }


    public static void UpdateToken(final String token, Activity activity)
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.TYPE, Constant.REGISTER_DEVICE);
        params.put(Constant.TOKEN, token);
        params.put(Constant.USER_ID, session.getData(Session.KEY_ID));
        ApiConfig.RequestToVolley(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (!object.getBoolean(Constant.ERROR))
                        {
                            session.setData(Constant.KEY_FCM_ID, token);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.RegisterUrl, params, false);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(navigationView))
            drawer.closeDrawers();
        else
            doubleBack();
    }

    public void doubleBack() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
        menu.findItem(R.id.menu_add).setVisible(false);
        Log.d("item", ""+databaseHelper.getTotalItemOfCart());

        menu.findItem(R.id.menu_cart).setIcon(ApiConfig.buildCounterDrawable(databaseHelper.getTotalItemOfCart(), R.drawable.ic_cart, MainActivity.this));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_cart:
                OpenCart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void OpenCart() {
        Intent intent  = new Intent(getApplicationContext(), CartActivity_2.class);
        startActivity(intent);

    }


    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }

    private void showReview_Custom()
    {
         AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY) //default is Google, other option is Amazon
                .setInstallDays(3) // default 10, 0 means install day.
                .setLaunchTimes(10) // default 10 times.
                .setRemindInterval(2) // default 1 day.
                //.setShowLaterButton(true) // default true.
                .setDebug(true) // default false.
                .setCancelable(false) // default false.
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                        if(which == -3)
                        {
                              storeinfo.setBoolean("view_alert_rating", true);
                        }
                        else if(which == -2)
                        {
                            storeinfo.setBoolean("view_alert_rating", false);
                        }
                        else if(which == -1)
                        {
                            storeinfo.setBoolean("view_alert_rating", false);
                        }
                    }
                })
                .setMessage(R.string.rate_dialog_message)
                .setTitle(R.string.new_rate_dialog_title)
                .setTextLater(R.string.new_rate_dialog_later)
                .setTextNever(R.string.new_rate_dialog_never)
                .setTextRateNow(R.string.new_rate_dialog_ok)
                .monitor();


        if(isFirstTime()){
            AppRate.showRateDialogIfMeetsConditions(MainActivity.this);
        }
        else if(storeinfo.getBoolean("view_alert_rating"))
        {
            AppRate.showRateDialogIfMeetsConditions(MainActivity.this);
        }
    }

/*
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
                        }

                    } catch (JSONException e) {
                        is_address_save=false;
                        is_default_address_save=false;
                        e.printStackTrace();
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_CHECKADDRESS, params, true);

    }

*/





}
