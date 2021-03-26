package com.ifresh.customer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.loader.content.CursorLoader;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ifresh.customer.R;
import com.ifresh.customer.helper.ApiConfig;
import com.ifresh.customer.helper.Constant;
import com.ifresh.customer.helper.FileOperation;
import com.ifresh.customer.helper.Session;
import com.ifresh.customer.helper.StorePrefrence;
import com.ifresh.customer.helper.VolleyCallback;
import com.ifresh.customer.helper.VolleyMultipartRequest;
import com.ifresh.customer.kotlin.FillAddress;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.graphics.Color.GRAY;
import static com.ifresh.customer.helper.Constant.GET_ORDERCANCEL;

public class UploadMedicine extends AppCompatActivity  {
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequiGRAY = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA
    };

    private boolean sentToSettings = false;
    Activity activity = UploadMedicine.this;
    Session session;
    StorePrefrence storeinfo;
    Toolbar toolbar;
    LinearLayout linear_top_picmore,linear2,linear3,linear4;
    TextView txt_picmore,txtaddress,type1,tvnoAddress;
    ImageView user_pic,user_pic_2,user_pic_3,user_pic_4,imgedit;
    Button btn_pic,btn_pic_2,btn_pic_3,btn_pic_4,btn_save;
    Button btn_del,btn_del_2,btn_del_3,btn_del_4;
    EditText txt;
    Context mContext = UploadMedicine.this;
    private Bitmap bitmap;
    private Uri selectedImage;
    private String type, filename,doc;
    String mCurrentPhotoPath;
    String imageFileName="";

    ArrayList<String>arrayList = new ArrayList<>();
    private ProgressBar progressBar;
    private Boolean is_img1=false, is_img2=false, is_img3=false, is_img4 = false;

    private Boolean is_img_up1=false, is_img_up2=false, is_img_up3=false, is_img_up4 = false;

    Boolean is_address_save=false, is_default_address_save=false;
    LinearLayout linear_home;






    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploadpres);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        storeinfo = new StorePrefrence(UploadMedicine.this);
        session = new Session(UploadMedicine.this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.upload_medicine));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        imgedit = findViewById(R.id.imgedit);
        linear_home = findViewById(R.id.linear_home);
        txtaddress = findViewById(R.id.txtaddress);
        tvnoAddress = findViewById(R.id.tvnoAddress);
        type1 = findViewById(R.id.type1);

        linear_top_picmore = findViewById(R.id.linear_top_picmore);
        linear2 = findViewById(R.id.linear2);
        linear3 = findViewById(R.id.linear3);
        linear4 = findViewById(R.id.linear4);

        user_pic = findViewById(R.id.user_pic);
        user_pic_2 = findViewById(R.id.user_pic_2);
        user_pic_3 = findViewById(R.id.user_pic_3);
        user_pic_4 = findViewById(R.id.user_pic_4);



        btn_del = findViewById(R.id.btn_del);
        btn_del_2 = findViewById(R.id.btn_del_2);
        btn_del_3 = findViewById(R.id.btn_del_3);
        btn_del_4 = findViewById(R.id.btn_del_4);


        btn_pic = findViewById(R.id.btn_pic);
        btn_pic_2 = findViewById(R.id.btn_pic_2);
        btn_pic_3 = findViewById(R.id.btn_pic_3);
        btn_pic_4 = findViewById(R.id.btn_pic_4);
        btn_save = findViewById(R.id.btn_save);

        txt = findViewById(R.id.txt);
        txt_picmore = findViewById(R.id.txt_picmore);

        checkpermission();



        linear_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadMedicine.this, FillAddress.class);
                intent.putExtra("userId", session.getData(session.KEY_id));
                startActivity(intent);
                finish();
            }
        });

        imgedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadMedicine.this, FillAddress.class);
                intent.putExtra("userId", session.getData(session.KEY_id));
                startActivity(intent);
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if(is_img_up1 || is_img_up2 || is_img_up3 || is_img_up4)
                  call_placingimageorder_api();
                else
                   Toast.makeText(mContext, "Please Upload At Least One Image Order", Toast.LENGTH_SHORT).show();
            }
        });

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertView(btn_del.getTag().toString(),  btn_del, btn_pic, user_pic, 1);
            }
        });

        btn_del_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertView(btn_del_2.getTag().toString(),  btn_del_2, btn_pic_2, user_pic_2,2);
                //call_cancel_order_api(btn_del_2.getTag().toString(),  btn_del_2, btn_pic_2, user_pic_2,2);
            }
        });

        btn_del_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertView(btn_del_3.getTag().toString(),  btn_del_3, btn_pic_3, user_pic_3,3);
                //call_cancel_order_api(btn_del_3.getTag().toString(),  btn_del_3, btn_pic_3, user_pic_3,3);
            }
        });

        btn_del_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertView(btn_del_4.getTag().toString(),  btn_del_4, btn_pic_4, user_pic_4,4);
                //call_cancel_order_api(btn_del_4.getTag().toString(),  btn_del_4, btn_pic_4, user_pic_4,4);
            }
        });



        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_img1)
                {
                    linear_top_picmore.setVisibility(View.VISIBLE);
                    uploadFile(selectedImage,btn_pic, btn_del , user_pic, 1);
                }
                else{
                    Toast.makeText(mContext,"Please Upload Image First", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btn_pic_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_img2)
                {
                    uploadFile(selectedImage, btn_pic_2, btn_del_2 ,user_pic_2,2);
                }
                else{
                    Toast.makeText(mContext,"Please Upload Image First", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_pic_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_img3)
                {
                    uploadFile(selectedImage, btn_pic_3, btn_del_3 ,user_pic_3,3);
                }
                else{
                    Toast.makeText(mContext,"Please Upload Image First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_pic_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_img4)
                {
                    uploadFile(selectedImage, btn_pic_4, btn_del_4,user_pic_4,4);
                }
                else{
                    Toast.makeText(mContext,"Please Upload Image First", Toast.LENGTH_SHORT).show();
                }

            }
        });

        linear_top_picmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(linear2.getVisibility() == View.GONE)
                {
                    linear2.setVisibility(View.VISIBLE);
                }
                else if(linear3.getVisibility() == View.GONE)
                {
                    linear3.setVisibility(View.VISIBLE);
                }
                else if(linear4.getVisibility() == View.GONE)
                {
                    linear4.setVisibility(View.VISIBLE);
                }
            }
        });

        txt_picmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linear2.getVisibility() == View.GONE)
                {
                    linear2.setVisibility(View.VISIBLE);
                }
                else if(linear3.getVisibility() == View.GONE)
                {
                    linear3.setVisibility(View.VISIBLE);
                }
                else if(linear4.getVisibility() == View.GONE)
                {
                    linear4.setVisibility(View.VISIBLE);
                }
            }
        });


        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_pic.isEnabled())
                {
                    selectImage("userpic_1");
                }
                else{
                    //show image view
                    String url_img_pic =  user_pic.getTag().toString();
                    Intent intent = new Intent(mContext, ImageFullView.class);
                    intent.putExtra("image",url_img_pic);
                    startActivity(intent);
                }


            }
        });


        user_pic_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_pic_2.isEnabled())
                {
                    selectImage("userpic_2");
                }
                else{
                    //show image view
                    String url_img_pic = Constant.UPLOAD_IMAGE_SHOW + user_pic_2.getTag();
                    Intent intent = new Intent(mContext, ImageFullView.class);
                    intent.putExtra("image",url_img_pic);
                    startActivity(intent);
                }


            }
        });

        user_pic_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_pic_3.isEnabled())
                {
                    selectImage("userpic_3");
                }
                else{
                    //show image view
                    String url_img_pic = Constant.UPLOAD_IMAGE_SHOW + user_pic_3.getTag();
                    Intent intent = new Intent(mContext, ImageFullView.class);
                    intent.putExtra("image",url_img_pic);
                    startActivity(intent);
                }
            }
        });

        user_pic_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_pic_4.isEnabled())
                {
                    selectImage("userpic_4");
                }
                else{
                    //show image view
                    String url_img_pic = Constant.UPLOAD_IMAGE_SHOW + user_pic_4.getTag();
                    Intent intent = new Intent(mContext, ImageFullView.class);
                    intent.putExtra("image",url_img_pic);
                    startActivity(intent);
                }
            }
        });

    }



    private File createImageFile() throws IOException
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



    private void selectImage(final String picvalue)
    {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));


                    if(picvalue.equalsIgnoreCase("userpic_1"))
                    {
                        startActivityForResult(intent, 1);
                    }
                    else if(picvalue.equalsIgnoreCase("userpic_2"))
                    {
                        startActivityForResult(intent, 3);
                    }
                    else if(picvalue.equalsIgnoreCase("userpic_3"))
                    {
                        startActivityForResult(intent, 5);
                    }
                    else if(picvalue.equalsIgnoreCase("userpic_4"))
                    {
                        startActivityForResult(intent, 7);
                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if(picvalue.equalsIgnoreCase("userpic_1"))
                    {
                        startActivityForResult(intent, 2);
                    }
                    if(picvalue.equalsIgnoreCase("userpic_2"))
                    {
                        startActivityForResult(intent, 4);
                    }
                    if(picvalue.equalsIgnoreCase("userpic_3"))
                    {
                        startActivityForResult(intent, 6);
                    }
                    if(picvalue.equalsIgnoreCase("userpic_4"))
                    {
                        startActivityForResult(intent, 8);
                    }
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            //selectedImage = getIntent().getData();
            if (requestCode == 1 || requestCode == 3 || requestCode == 5 || requestCode == 7)
            {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles())
                {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    selectedImage = getImageUri(mContext, bitmap);
                    if(requestCode == 1) {
                        user_pic.setImageBitmap(bitmap);
                        type="Photo1";
                        filename="Photo1"+session.getData(session.KEY_id);
                        is_img1=true;

                    }
                    else if(requestCode == 3){
                        user_pic_2.setImageBitmap(bitmap);
                        type="Photo2";
                        filename="Photo2"+session.getData(session.KEY_id);
                        is_img2=true;
                    }
                    else if(requestCode == 5){
                        user_pic_3.setImageBitmap(bitmap);
                        type="Photo3";
                        filename="Photo3"+session.getData(session.KEY_id);
                        is_img3=true;

                    }
                    else if(requestCode == 7){
                        user_pic_4.setImageBitmap(bitmap);
                        type="Photo4";
                        filename="Photo4"+session.getData(session.KEY_id);
                        is_img4=true;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == 2 || requestCode == 4 || requestCode == 6 || requestCode == 8)
            {
                selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                bitmap = (BitmapFactory.decodeFile(picturePath));

                if(requestCode == 2){
                    user_pic.setImageBitmap(bitmap);
                    type="Photo1";
                    filename="Photo1"+session.getData(session.KEY_id);
                    is_img1=true;
                }
                else if(requestCode == 4){
                    user_pic_2.setImageBitmap(bitmap);
                    type="Photo2";
                    filename="Photo2"+session.getData(session.KEY_id);
                    is_img2=true;
                }
                else if(requestCode == 6){
                    user_pic_3.setImageBitmap(bitmap);
                    type="Photo3";
                    filename="Photo3"+session.getData(session.KEY_id);
                    is_img3=true;

                }
                else if(requestCode == 8){
                    user_pic_4.setImageBitmap(bitmap);
                    type="Photo4";
                    filename="Photo4"+session.getData(session.KEY_id);
                    is_img4=true;
                }
            }
        }
    }

    private void showPic_1Image(final Button btn, final Button btn_del_n , final ImageView img, final String image_name, final int btn_var) {
        progressBar.setVisibility(View.VISIBLE);
        try{
            final String url_img_pic = Constant.UPLOAD_IMAGE_SHOW + image_name;
            Log.d("url", url_img_pic);
            if (!url_img_pic.contentEquals(""))
            {
                Picasso.with(mContext)
                        .load(url_img_pic)
                        .placeholder( R.drawable.progress_animationn)
                        .error(R.drawable.ic_image_upload)
                        .into(img, new Callback() {
                            @Override
                            public void onSuccess() {
                                btn.setEnabled(false);
                                btn.setBackgroundColor(GRAY);

                                btn_del_n.setEnabled(true);
                                btn_del_n.setBackgroundColor(Color.parseColor("#09B150"));
                                btn_del_n.setTag(image_name);

                                img.setEnabled(true);
                                img.setTag(url_img_pic);

                                if(btn_var == 1)
                                {
                                    is_img_up1=true;
                                }
                                else if(btn_var == 2)
                                {
                                    is_img_up2=true;
                                }
                                else if(btn_var == 3)
                                {
                                    is_img_up3 = true;
                                }
                                else if(btn_var == 4)
                                {
                                    is_img_up4 = true;
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                            @Override
                            public void onError() {

                                if(btn_var == 1)
                                {
                                    is_img_up1=false;
                                }
                                else if(btn_var == 2)
                                {
                                    is_img_up2=false;
                                }
                                else if(btn_var == 3)
                                {
                                    is_img_up3=false;
                                }
                                else if(btn_var == 4)
                                {
                                    is_img_up4=false;
                                }

                                btn.setEnabled(true);
                                btn.setBackgroundColor(Color.parseColor("#09B150"));

                                btn_del_n.setEnabled(false);
                                btn_del_n.setBackgroundColor(GRAY);

                                img.setEnabled(true);

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }




        }
        catch (Exception ex)
        {
            progressBar.setVisibility(View.GONE);
            ex.printStackTrace();
        }
    }


    private void uploadFile(Uri fileUri,  final Button btn_pic,  final Button btn_del, final ImageView user_pic, final int btn_var) {
        final ProgressDialog pDialog  = new ProgressDialog(mContext);
        try {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Uploading...");
            pDialog.setIndeterminate(true);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();

            final File file = new File(getRealPathFromURI(fileUri));
            final FileOperation f = new FileOperation();
            f.readFile(file);

            String url= Constant.BASEPATH+Constant.UPLOAD_IMAGE;
            Log.d("Url", ""+url);
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    Log.d("Response", resultResponse);
                    try{
                        JSONObject jsonObject = new JSONObject(resultResponse);
                        if(jsonObject.getString(Constant.SUCESS).equalsIgnoreCase("200"))
                        {
                            arrayList.add(jsonObject.getString("name"));
                            pDialog.dismiss();
                            showPic_1Image(btn_pic, btn_del ,user_pic, jsonObject.getString("name"), btn_var);
                        }
                    }
                    catch (Exception ex)
                    {
                        pDialog.dismiss();
                        ex.printStackTrace();
                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "Unknown error";
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = "Request timeout";
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect server";
                        }
                    } else {
                        String result = new String(networkResponse.data);
                        try {
                            JSONObject response = new JSONObject(result);
                            String status = response.getString("status");
                            String message = response.getString("message");

                            Log.e("Error Status", status);
                            Log.e("Error Message", message);

                            if (networkResponse.statusCode == 404) {
                                errorMessage = "Resource not found";
                            } else if (networkResponse.statusCode == 401) {
                                errorMessage = message+" Please login again";
                            } else if (networkResponse.statusCode == 400) {
                                errorMessage = message+ " Check your inputs";
                            } else if (networkResponse.statusCode == 500) {
                                errorMessage = message+" Something is getting wrong";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("Error", errorMessage);
                    pDialog.dismiss();
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real local_path
                    // for now just get bitmap data from ImageView
                    try {
                        params.put("user_img", new DataPart("user_image.jpg",f.readFile(file), "image/jpeg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(500000,0,0));

            //Adding request to the queue
            requestQueue.add(multipartRequest);

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            pDialog.dismiss();
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void call_placingimageorder_api()
    {
        final Vibrator vibe = (Vibrator) UploadMedicine.this.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(80);

        progressBar.setVisibility(View.VISIBLE);
        String order_imgs="";
        if(arrayList.size() == 1)
        {
            order_imgs = arrayList.get(0);
        }
        else{
             order_imgs = String.join(",", arrayList);
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", session.getData(Session.KEY_id));
        params.put("phone_no", session.getData(Session.KEY_mobile));
        params.put("order_imgs", order_imgs);
        params.put("decsription", txt.getText().toString());
        params.put("order_type", "2");


        ApiConfig.RequestToVolley_POST(new VolleyCallback()
        {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        //System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(Constant.SUCESS).equalsIgnoreCase("200"))
                        {
                            //Toast.makeText(mContext, "Order Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                            user_pic.setEnabled(false);
                            btn_pic.setEnabled(false);
                            btn_del.setEnabled(false);
                            btn_pic.setBackgroundColor(GRAY);
                            btn_del.setBackgroundColor(GRAY);


                            user_pic_2.setEnabled(false);
                            btn_pic_2.setEnabled(false);
                            btn_del_2.setEnabled(false);
                            btn_pic_2.setBackgroundColor(GRAY);
                            btn_del_2.setBackgroundColor(GRAY);

                            user_pic_3.setEnabled(false);
                            btn_pic_3.setEnabled(false);
                            btn_del_3.setEnabled(false);
                            btn_pic_3.setBackgroundColor(GRAY);
                            btn_del_3.setBackgroundColor(GRAY);


                            user_pic_4.setEnabled(false);
                            btn_pic_4.setEnabled(false);
                            btn_del_4.setEnabled(false);
                            btn_pic_4.setBackgroundColor(GRAY);
                            btn_del_4.setBackgroundColor(GRAY);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 2s = 2000ms
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(UploadMedicine.this, MedicalOrderPlacedActivity.class));
                                    finish();
                                }
                            }, 2000);
                        }
                        else{
                             Toast.makeText(mContext, "Error has occurred please try again later", Toast.LENGTH_SHORT).show();
                             progressBar.setVisibility(View.GONE);
                          }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Error Has Occur Please Try Again Later", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.PLACING_IMAGEORDER , params, false);
   }


    private void call_cancel_order_api(final String imageFileName, final Button btn_del, final Button btn_pic, final ImageView user_pic, final Integer is_imgdel)
    {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<String, String>();

        ApiConfig.RequestToVolley_GET(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(Constant.SUCESS).equalsIgnoreCase("200"))
                        {
                            Toast.makeText(mContext, "Order Image Deleted", Toast.LENGTH_SHORT).show();
                            btn_del.setEnabled(false);
                            btn_del.setBackgroundColor(GRAY);


                            if(is_imgdel == 1)
                                 is_img1=false;
                            else if(is_imgdel == 2)
                                 is_img2=false;
                            else if(is_imgdel == 3)
                                 is_img3=false;
                            else if(is_imgdel == 4)
                                 is_img4=false;

                            if(arrayList.contains(imageFileName))
                            {
                                int pos = arrayList.indexOf(imageFileName);
                                arrayList.remove(pos);
                            }

                            showPic_1Image(btn_pic, btn_del ,user_pic, "",is_imgdel);


                        }
                        else{
                            Toast.makeText(mContext, "Error Has Occur Please Try Again Later", Toast.LENGTH_SHORT).show();

                        }
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.ORDER_DELETE+imageFileName , params, false);
    }


    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callApidefaultAdd(Constant.BASEPATH+Constant.GET_USERDEFULTADD);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() ==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void checkpermission()
    {
        if(ActivityCompat.checkSelfPermission(UploadMedicine.this, permissionsRequiGRAY[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(UploadMedicine.this, permissionsRequiGRAY[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(UploadMedicine.this, permissionsRequiGRAY[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(UploadMedicine.this, permissionsRequiGRAY[3]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(UploadMedicine.this, permissionsRequiGRAY[4]) != PackageManager.PERMISSION_GRANTED
        )
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(UploadMedicine.this,permissionsRequiGRAY[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(UploadMedicine.this,permissionsRequiGRAY[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(UploadMedicine.this,permissionsRequiGRAY[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(UploadMedicine.this,permissionsRequiGRAY[3])
                    || ActivityCompat.shouldShowRequestPermissionRationale(UploadMedicine.this,permissionsRequiGRAY[4])
            )
            {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(UploadMedicine.this);
                builder.setTitle(activity.getResources().getString(R.string.location_permission));
                builder.setMessage(activity.getResources().getString(R.string.location_permission_message));
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(UploadMedicine.this,permissionsRequiGRAY,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
            else {
                //just request the permission
                ActivityCompat.requestPermissions(UploadMedicine.this,permissionsRequiGRAY,PERMISSION_CALLBACK_CONSTANT);
            }


            storeinfo.setBoolean(permissionsRequiGRAY[0],true);

        } else {
            //You already have the permission, just go ahead.
            //proceedAfterPermission();

        }
    }



    private void showAlertView(final String imageFileName, final Button btn_del, final Button btn_pic, final ImageView user_pic, final Integer is_imgdel)
    {
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(UploadMedicine.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.msg_view_4, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(true);
        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView tvcancel, tvclose;

        tvcancel = dialogView.findViewById(R.id.tvcancel);
        tvclose = dialogView.findViewById(R.id.tvclose);

        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Map<String, String> params = new HashMap<String, String>();
                ApiConfig.RequestToVolley_GET(new VolleyCallback() {
                    @Override
                    public void onSuccess(boolean result, String response) {
                        System.out.println("=================*cancelorder- " + response);
                        if (result) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getString(Constant.SUCESS).equalsIgnoreCase("200"))
                                {
                                    Toast.makeText(mContext, "Order Image Deleted", Toast.LENGTH_SHORT).show();
                                    btn_del.setEnabled(false);
                                    btn_del.setBackgroundColor(GRAY);


                                    if(is_imgdel == 1)
                                        is_img1=false;
                                    else if(is_imgdel == 2)
                                        is_img2=false;
                                    else if(is_imgdel == 3)
                                        is_img3=false;
                                    else if(is_imgdel == 4)
                                        is_img4=false;

                                    if(arrayList.contains(imageFileName))
                                    {
                                        int pos = arrayList.indexOf(imageFileName);
                                        arrayList.remove(pos);
                                    }

                                    showPic_1Image(btn_pic, btn_del ,user_pic, "",is_imgdel);

                                }
                                else{
                                    Toast.makeText(mContext, "Error has occur please try again.", Toast.LENGTH_SHORT).show();

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, UploadMedicine.this, Constant.BASEPATH + Constant.ORDER_DELETE+imageFileName, params, false);
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



    public void callApi_fillAdd(String url)
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

                        callApidefaultAdd(Constant.BASEPATH+Constant.GET_USERDEFULTADD);

                    } catch (JSONException e) {
                        is_address_save=false;
                        is_default_address_save=false;
                        e.printStackTrace();
                    }
                }
            }
        }, activity, url, params, true);

    }



    private void callApidefaultAdd(String url) {
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
                                //fill  Default address
                                JSONObject jsonObject_data = jsonObject.getJSONObject("data");
                                if(jsonObject_data.length() > 0)
                                {
                                    //fill Default Address
                                    JSONObject jsonObject_address = jsonObject_data.getJSONObject("address");
                                    imgedit.setVisibility(View.VISIBLE);

                                    /*String name = session.getData(Session.KEY_FIRSTNAME) + " " +session.getData(Session.KEY_LASTNAME) + "<br>";
                                    String add =  jsonObject_data.getString("complete_address")+ "<br>"
                                                  + session.getData(Constant.AREA_N)
                                                  + ", " + session.getData(Constant.CITY_N)
                                                  + "<br><b>" + getString(R.string.mobile_) + session.getData(Session.KEY_mobile);


                                    String complete_add =  name +  add;*/

                                    String  send_address_param = "Address:"+" "+jsonObject_data.getString("complete_address")+"<br>"+
                                            "State:"+" "+session.getData(Constant.STATE_N)+"<br>"+
                                            "City:"+" "+session.getData(Constant.CITY_N)+"<br>"+
                                            "Area:"+" "+session.getData(Constant.AREA_N)+"<br>"+
                                            "Mobile:"+" " + session.getData(Session.KEY_mobile)+"<br>"+
                                            "Deliver to :"+" "+session.getData(Session.KEY_FIRSTNAME) + " " +session.getData(Session.KEY_LASTNAME);

                                   txtaddress.setText(Html.fromHtml(send_address_param));

                                    int address_type = jsonObject_address.getInt("address_type");
                                    if(address_type == 1)
                                    {
                                        type1.setText("Home Default Address");
                                    }
                                    else if(address_type == 2)
                                    {
                                        type1.setText("Work Default Address");
                                    }
                                    else if(address_type == 3)
                                    {
                                        type1.setText("Other Default Address");
                                    }
                                }

                            }

                        }
                    } catch (JSONException e) {
                        imgedit.setVisibility(View.GONE);
                        Toast.makeText(activity, Constant.NODEFAULT_ADD, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        }, activity, url, params, false);

    }






}
