package com.ifresh.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ifresh.customer.R;
import com.ifresh.customer.adapter.CategoryAdapter;
import com.ifresh.customer.adapter.OfferAdapter;
import com.ifresh.customer.adapter.SectionAdapter;
import com.ifresh.customer.adapter.SliderAdapter;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.AppController;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;

import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.kotlin.LocationSelection_K;
import com.ifresh.customer.model.Category;
import com.ifresh.customer.model.Mesurrment;
import com.ifresh.customer.model.OfferImage;
import com.ifresh.customer.model.Slider;
import com.squareup.picasso.Picasso;

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

import static com.ifresh.customer.helper.Constant.AREA_N;
import static com.ifresh.customer.helper.Constant.AREA_NAME;
import static com.ifresh.customer.helper.Constant.AUTHTOKEN;
import static com.ifresh.customer.helper.Constant.BANNERIMAGE;
import static com.ifresh.customer.helper.Constant.BANNERIMAGEPATH;
import static com.ifresh.customer.helper.Constant.BASEPATH;
import static com.ifresh.customer.helper.Constant.CATEGORYIMAGEPATH;
import static com.ifresh.customer.helper.Constant.CITY_N;
import static com.ifresh.customer.helper.Constant.FEATUREPRODUCT;
import static com.ifresh.customer.helper.Constant.GETCATEGORY;
import static com.ifresh.customer.helper.Constant.GETFRENCHISE;
import static com.ifresh.customer.helper.Constant.GET_CONFIGSETTING;
import static com.ifresh.customer.helper.Constant.OFFER_IMAGE;
import static com.ifresh.customer.helper.Constant.SECTIONPRODUCT;
import static com.ifresh.customer.helper.Constant.SUBTITLE_1;


public class MainActivity extends DrawerActivity {

    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

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
    TextView tvlater, tvnever, tvrate,txt_delivery_loc;
    private Boolean firstTime = null;
    ImageView imgloc,img_src;
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
        measurement_list = new ArrayList<>();

        Log.d("token",  session.getData(AUTHTOKEN));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        activity = MainActivity.this;
        progressBar = findViewById(R.id.progressBar);
        progress_bar_banner = findViewById(R.id.progress_bar_banner);
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
        txt_delivery_loc = findViewById(R.id.txt_delivery_loc);
        img_src = findViewById(R.id.img_src);




        Picasso.with(mContext)
                .load(Constant.SETTINGIMAGEPATH +"app_video.jpg")
                .placeholder(R.drawable.placeholder)// optional
                .error(R.drawable.placeholder)
                .into(img_src);


        img_src.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OfferImageDetail.class);
                intent.putExtra("youtube_code", Constant.YOUTUBECODE);
                intent.putExtra("image_url", Constant.SETTINGIMAGEPATH+"app_video.jpg");
                startActivity(intent);
            }
        });



        txt_delivery_loc.setOnClickListener(new View.OnClickListener() {
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
                    Log.d("token", token);
                    Log.d("KEY_FCM_ID", session.getData(Constant.KEY_FCM_ID));
                    session.setData("token", token);
                }

            }
        });


        chekUpdateAuto();


        if (AppController.isConnected(MainActivity.this))
        {
            callSettingApi_messurment();// to call measurement data
            GetFrenchise_id();
            GetSlider();
            GetCategory();
            SectionProductRequest();
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
        //Log.d("url", BASEPATH + SECTIONPRODUCT +  session.getData(Constant.AREA_ID));

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

                            //remove unnecessary product only save top 3 product for section
                            /*for(int i = 3; i<jsonArray_products.length(); i++)
                            {
                                jsonArray_products.remove(i);
                            }*/

                            if(measurement_list.size() == 0)
                            {
                                callSettingApi_messurment();
                            }

                            section.setProductList(ApiConfig.GetFeatureProduct_2(jsonArray_products,measurement_list) );
                            sectionList.add(section);

                            sectionView.setVisibility(View.VISIBLE);
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
            Log.d("data", session.getData(Constant.KEY_MEASUREMENT));
            Log.d("data len", ""+session.getData(Constant.KEY_MEASUREMENT).length());
            JSONArray jsonArray = new JSONArray(session.getData(Constant.KEY_MEASUREMENT));
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






                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        lytCategory.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, MainActivity.this, CategoryUrl, params, true);

    }



    private void GetFrenchise_id() {
        progressBar.setVisibility(View.GONE);
        String FrenchiseUrl = BASEPATH + GETFRENCHISE + session.getData(Constant.AREA_ID);
        Map<String, String> params = new HashMap<String, String>();
        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                System.out.println("frenchise==>" + response);
                if (result) {
                    try {
                        JSONObject object = new JSONObject(response);

                        if (object.getInt(Constant.SUCESS) == 200)
                        {
                            JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                            if(jsonArray.length() > 0)
                            {
                                JSONObject jsonObject =  jsonArray.getJSONObject(0);
                                String franchiseId = jsonObject.getString("franchiseId");
                                Log.d("save_franchiseId", franchiseId);
                                session.setData("franchiseId", franchiseId);
                                storeinfo.setString("franch", franchiseId);
                            }
                        }
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }
        }, MainActivity.this, FrenchiseUrl, params, true);

    }



    @Override
    public void onResume() {
        super.onResume();
        chekUpdateAuto();
        checkNewAppVersionState();


        if (session.isUserLoggedIn())
        {
            tvName.setText(session.getData(session.KEY_FIRSTNAME)+" "+ session.getData(session.KEY_LASTNAME));
        }
        else{
            tvName.setText(getResources().getString(R.string.is_login));
        }
        txt_delivery_loc.setText("Deliver to : "+session.getData(AREA_N) + " / "+ session.getData(CITY_N));

        try{
                //execute if franchise is different from current franchise
                if(session.getBoolean("area_change"))
                {
                    showAlertView_LocChange();
                    session.setBoolean("area_change",false);
                    if(storeinfo.getBoolean("is_locchange"))
                    {
                        storeinfo.setBoolean("is_locchange",false);
                        if (AppController.isConnected(MainActivity.this))
                        {
                            /*if(measurement_list.size() == 0)
                            {
                                callSettingApi_messurment();
                            }*/
                            callSettingApi_messurment();
                            GetFrenchise_id();
                            GetSlider();
                            GetCategory();
                            SectionProductRequest();
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
            //showAlertView_2();
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
                        //Log.d("offer", Constant.OFFER_IMAGE+Constant.GET_OFFER+session.getData(Constant.AREA_ID));
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

                                Log.d("offerimage", OFFER_IMAGE + object.getString("offer_img"));
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

    private void chekUpdateAuto()
    {

        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Create a listener to track request state updates.
        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                // Show module progress, log state, or install the update.
                if (installState.installStatus() == InstallStatus.DOWNLOADED)
                    // After the update is downloaded, show a notification
                    // and request user confirmation to restart the app.
                    popupSnackbarForCompleteUpdateAndUnregister();
            }
        };



        appUpdateInfoTask.addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // Request the update.
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                        // Before starting an update, register a listener for updates.
                        appUpdateManager.registerListener(installStateUpdatedListener);
                        // Start an update.
                        startAppUpdateFlexible(appUpdateInfo);
                    } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // Start an update.
                        startAppUpdateImmediate(appUpdateInfo);
                    }
                }
            }
        });




    }

    private void popupSnackbarForCompleteUpdateAndUnregister()
    {
        Snackbar snackbar = Snackbar
                .make(categoryRecyclerView, "New app is ready!", Snackbar.LENGTH_LONG)
                .setAction("Install", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (appUpdateManager != null){
                            appUpdateManager.completeUpdate();
                        }
                    }
                });
        snackbar.setActionTextColor(getResources().getColor(R.color.yellow));
        snackbar.show();

        unregisterInstallStateUpdListener();
    }

    private void unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null)
            appUpdateManager.unregisterListener(installStateUpdatedListener);
    }

    private void startAppUpdateFlexible(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MainActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            unregisterInstallStateUpdListener();
        }
    }

    private void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MainActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void checkNewAppVersionState()
    {

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdateAndUnregister();
                }

                //IMMEDIATE:
                if (appUpdateInfo.updateAvailability()
                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    startAppUpdateImmediate(appUpdateInfo);
                }
                //FLEXIBLE:
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
            }
        });





    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {

            case REQ_CODE_VERSION_UPDATE:
                if (resultCode != RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                    //Log.d("Update flow failed! Result code: ",resultCode);
                    // If the update is cancelled or fails,
                    // you can request to start the update again.
                    unregisterInstallStateUpdListener();
                }

                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterInstallStateUpdListener();
    }
}
