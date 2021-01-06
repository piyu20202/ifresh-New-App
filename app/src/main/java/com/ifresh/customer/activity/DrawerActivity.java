package com.ifresh.customer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.Task;
import com.ifresh.customer.R;;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.DatabaseHelper;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.kotlin.ChangePassword;
import com.ifresh.customer.kotlin.EditProfile_K;
import com.ifresh.customer.kotlin.ForgetPassword_K;

import com.ifresh.customer.kotlin.FillAddress;
import com.ifresh.customer.kotlin.LocationSelection_K;
import com.ifresh.customer.kotlin.SignInActivity_K;

import static com.ifresh.customer.helper.Constant.AREA_N;
import static com.ifresh.customer.helper.Constant.CITY_N;

public class DrawerActivity extends AppCompatActivity {
    Context mContext = DrawerActivity.this;
    public NavigationView navigationView;
    public DrawerLayout drawer;
    public ActionBarDrawerToggle drawerToggle;
    protected FrameLayout frameLayout;
    public TextView tvMobile,txt;
    public static TextView tvName, tvWallet;
    Session session;

    StorePrefrence  storeinfo;
    LinearLayout lytProfile;
    LinearLayout lytWallet;
    LinearLayout lyt_update_app;
    int versionCode;
    String version;
    boolean is_appupdate=false;
    android.app.AlertDialog.Builder builder;
    ReviewManager manager ;
    ReviewInfo reviewInfo = null;
    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // ApiConfig.transparentStatusAndNavigation(DrawerActivity.this);
        setContentView(R.layout.activity_drawer);
        storeinfo = new StorePrefrence(DrawerActivity.this);
        session = new Session(DrawerActivity.this);
        databaseHelper = new DatabaseHelper(DrawerActivity.this);
        builder = new android.app.AlertDialog.Builder(DrawerActivity.this);

        frameLayout = findViewById(R.id.content_frame);
        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        View header = navigationView.getHeaderView(0);
        lytWallet = header.findViewById(R.id.lytWallet);
        lyt_update_app = header.findViewById(R.id.lyt_update_app);
        tvWallet = header.findViewById(R.id.tvWallet);
        tvName = header.findViewById(R.id.header_name);
        tvMobile = header.findViewById(R.id.tvMobile);
        lytProfile = header.findViewById(R.id.lytProfile);
        txt = header.findViewById(R.id.txt);
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
            version = pInfo.versionName;
          } catch (Exception e) {
            e.printStackTrace();
          }

        int store_info_version_code = storeinfo.getInt("version_code");
        if(versionCode < store_info_version_code)
        {
            is_appupdate=true;
        }
        else{
            is_appupdate=false;
        }


        if (session.isUserLoggedIn())
        {
            //Log.d("mob",""+session.getData(session.KEY_mobile) );
            //Log.d("name",""+session.getData(session.KEY_FIRSTNAME)+" "+ session.getData(session.KEY_LASTNAME) );
            tvMobile.setText(session.getData(session.KEY_mobile));
            tvName.setText(session.getData(session.KEY_FIRSTNAME)+" "+ session.getData(session.KEY_LASTNAME));
            lytWallet.setVisibility(View.VISIBLE);
            tvWallet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet_white, 0, 0, 0);
            DrawerActivity.tvWallet.setText(getString(R.string.wallet_balance) + "\t:\t" + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);

            ApiConfig.getWalletBalance(DrawerActivity.this, session);

             tvWallet.setText(getString(R.string.wallet_balance)+"\t:\t"+ApiConfig.getWalletBalance(DrawerActivity.this, session));;

            tvWallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), WalletBalanceList.class));
                }
            });

        }
        else {
            lytWallet.setVisibility(View.GONE);
            tvName.setText(getResources().getString(R.string.is_login));
        }

        lytProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                if (session.isUserLoggedIn())
                    startActivity(new Intent(getApplicationContext(), EditProfile_K.class));
                else
                    startActivity(new Intent(getApplicationContext(), SignInActivity_K.class));
            }
        });
        setupNavigationDrawer();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void setupNavigationDrawer() {
        Menu nav_Menu = navigationView.getMenu();
        if(is_appupdate)
        {
            nav_Menu.findItem(R.id.menu_update).setVisible(true);
        }
        else{
            nav_Menu.findItem(R.id.menu_update).setVisible(false);
        }

        if (session.isUserLoggedIn())
        {
            nav_Menu.findItem(R.id.menu_editprofile).setVisible(true);
            nav_Menu.findItem(R.id.menu_logout).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.menu_logout).setVisible(true);
            nav_Menu.findItem(R.id.menu_editprofile).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawer.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.menu_update:
                        if(is_appupdate) {
                            showAlertView();
                        }
                        else{
                            Toast.makeText(mContext, "Latest Version of iFresh App Already Installed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.notifications:
                        if (session.isUserLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), NotificationList.class));
                        }
                        else
                            startActivity(new Intent(getApplicationContext(), SignInActivity_K.class));

                        break;
                    case R.id.walletbalance:
                        if (session.isUserLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), WalletBalanceList.class));
                        } else
                            startActivity(new Intent(getApplicationContext(), SignInActivity_K.class));
                        break;
                    case R.id.faq:
                        Intent faq = new Intent(getApplicationContext(), WebViewActivity.class);
                        faq.putExtra("type", "3");
                        startActivity(faq);
                        break;
                    case R.id.menu_terms:
                        Intent terms = new Intent(getApplicationContext(), WebViewActivity.class);
                        terms.putExtra("type", "4");
                        startActivity(terms);
                        // ApiConfig.OpenBottomDialog("terms", getApplicationContext());
                        break;
                    case R.id.contact:
                        Intent contact = new Intent(getApplicationContext(), WebViewActivity.class);
                        contact.putExtra("type", "2");
                        startActivity(contact);
                        break;
                    case R.id.about_us:
                        Intent about = new Intent(getApplicationContext(), WebViewActivity.class);
                        about.putExtra("type", "1");
                        startActivity(about);
                        break;
                    case R.id.menu_privacy:
                        Intent privacy = new Intent(getApplicationContext(), WebViewActivity.class);
                        privacy.putExtra("type", "5");
                        startActivity(privacy);
                        break;
                    case R.id.menu_home:
                        finish();
                         startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        break;
                    case R.id.menu_editprofile:
                        if (session.isUserLoggedIn())
                            startActivity(new Intent(getApplicationContext(), EditProfile_K.class));
                        else
                            startActivity(new Intent(getApplicationContext(), SignInActivity_K.class));
                        break;

                    case R.id.friend_code_list:
                        if (session.isUserLoggedIn())
                            startActivity(new Intent(getApplicationContext(), FriendCodeList.class));
                        else
                            startActivity(new Intent(getApplicationContext(), SignInActivity_K.class));
                        break;
                    case R.id.refer:
                        if (session.isUserLoggedIn())
                            startActivity(new Intent(getApplicationContext(), ReferEarnActivity.class));
                        else
                            startActivity(new Intent(getApplicationContext(), SignInActivity_K.class));
                        break;


                    case R.id.cart:
                        startActivity(new Intent(getApplicationContext(), CartActivity_2.class));
                        break;
                    case R.id.changePass:
                        Intent intent1 = new Intent(getApplicationContext(), ChangePassword.class);
                        if (session.isUserLoggedIn())
                            intent1.putExtra("from", "changepsw");
                        startActivity(intent1);
                        break;
                    case R.id.menu_tracker:
                        if (session.isUserLoggedIn()) {
                            startActivity(new Intent(getApplicationContext(), OrderListActivity_2.class));
                        } else
                            startActivity(new Intent(getApplicationContext(), SignInActivity_K.class));
                        break;
                    case R.id.menu_share:
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        shareIntent.putExtra(Intent.EXTRA_TEXT, storeinfo.getString(Constant.SHARE_MSG) + "\n" +  storeinfo.getString(Constant.SHORT_LINK));
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
                        break;
                    case R.id.menu_rate:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ifresh.customer&hl=en")));
                        initReview();
                        askForReview();
                        break;
                    case R.id.menu_logout:
                        showAlertView_3_1();
                        break;
                    case R.id.menu_changepass:
                        startActivity(new Intent(getApplicationContext(), ChangePassword.class));
                        break;
                    case R.id.menu_forget:
                        startActivity(new Intent(getApplicationContext(), ForgetPassword_K.class));
                        break;

                    case R.id.menu_setaddresstyp: {
                        if (session.isUserLoggedIn())
                        {
                            Intent intent = new Intent(getApplicationContext(), FillAddress.class);
                            intent.putExtra("pas_address","");
                            intent.putExtra("from","");
                            intent.putExtra("addresstype_id","");
                            startActivity(intent);
                        } else
                            startActivity(new Intent(getApplicationContext(), SignInActivity_K.class) );
                        break;
                    }

                    case R.id.menu_setdefultaddress:
                        if (session.isUserLoggedIn())
                        {
                            startActivity(new Intent(getApplicationContext(), SetDefaultAddress_2.class));
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(), SignInActivity_K.class));
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void showAlertView_3_1() {
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(DrawerActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_4, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvlogout,tvclose,txt_msg;

        tvlogout = dialogView.findViewById(R.id.tvcancel);
        tvclose = dialogView.findViewById(R.id.tvclose);
        txt_msg = dialogView.findViewById(R.id.txt_msg);

        tvclose.setText("No");
        tvlogout.setText("LogOut");
        txt_msg.setText(R.string.logout_cnf);

        tvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                session.logoutUser(DrawerActivity.this);
                session.deletePref();
                storeinfo.clear();
                finish();

                Intent intent = new Intent(DrawerActivity.this, LocationSelection_K.class);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

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



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    public void showAlertView()
    {
        String final_msg = "Please Download Latest Version Of iFresh From Play Store.";
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_2, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_msg,tvcancel,tvupdate;

        txt_msg = dialogView.findViewById(R.id.txt_msg);
        tvcancel = dialogView.findViewById(R.id.tvcancel);
        tvupdate = dialogView.findViewById(R.id.tvupdate);
        final String pakage_name = getApplicationContext().getPackageName();
        final String str_google_play_url = "https://play.google.com/store/apps/details?id=";
        final String str_google_play_end = "&hl=en";


        tvupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String url = str_google_play_url+pakage_name+str_google_play_end;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                finish();
            }
        });

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        txt_msg.setText(final_msg);
        dialog.show();


    }

    public void showAlertView_2()
    {
        String final_msg = "Please Download Latest Version Of iFresh From Play Store.";
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_3, null);
        alertDialog.setView(dialogView);

        if(storeinfo.getInt("force_update") == 0)
        {
            alertDialog.setCancelable(true);
        }
        else if(storeinfo.getInt("force_update") == 1)
        {
            alertDialog.setCancelable(false);
        }

        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_msg,tvcancel,tvupdate;
        txt_msg = dialogView.findViewById(R.id.txt_msg);
        tvupdate = dialogView.findViewById(R.id.tvupdate);
        final String pakage_name = getApplicationContext().getPackageName();
        final String str_google_play_url = "https://play.google.com/store/apps/details?id=";
        final String str_google_play_end = "&hl=en";


        tvupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //clear prefrence when app is updated
                storeinfo.clear();
                session.clear();

                dialog.dismiss();
                String url = str_google_play_url+pakage_name+str_google_play_end;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                finish();
            }
        });
        txt_msg.setText(final_msg);
        dialog.show();
    }



    public void showAlertView_LocChange()
    {
        String final_msg = "Dear User You Have Changed Your Location Now You City Is" + " " + session.getData(CITY_N) + " "+
                            "And Area Is"+ " "+ session.getData(AREA_N) + "." ;
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_8, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView txt_msg;
        txt_msg = dialogView.findViewById(R.id.txt_msg);
        txt_msg.setText(final_msg);
        dialog.show();

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










}
