package com.ifresh.customerr.activity;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ifresh.customerr.R;
import com.ifresh.customerr.helper.ApiConfig;
import com.ifresh.customerr.helper.Constant;
import com.ifresh.customerr.helper.FileOperation;
import com.ifresh.customerr.helper.Session;
import com.ifresh.customerr.helper.StorePrefrence;
import com.ifresh.customerr.helper.VolleyCallback;
import com.ifresh.customerr.helper.VolleyMultipartRequest;
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

import static android.graphics.Color.RED;

public class UploadMedicine extends AppCompatActivity  {


    private boolean sentToSettings = false;
    Activity activity = UploadMedicine.this;

    Session session;
    StorePrefrence storeinfo;
    Toolbar toolbar;
    LinearLayout linear_top_picmore,linear2,linear3,linear4;
    TextView txt_picmore;
    ImageView user_pic,user_pic_2,user_pic_3,user_pic_4;
    Button btn_pic,btn_pic_2,btn_pic_3,btn_pic_4,btn_save;
    EditText txt;
    int count=0;
    Context mContext = UploadMedicine.this;
    private Bitmap bitmap;
    private Uri selectedImage;
    private String type, filename,doc;
    String mCurrentPhotoPath;
    String imageFileName="";

    ArrayList<String>arrayList = new ArrayList<>();

    private ProgressBar progressBar;




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
        linear_top_picmore = findViewById(R.id.linear_top_picmore);
        linear2 = findViewById(R.id.linear2);
        linear3 = findViewById(R.id.linear3);
        linear4 = findViewById(R.id.linear4);

        user_pic = findViewById(R.id.user_pic);
        user_pic_2 = findViewById(R.id.user_pic_2);
        user_pic_3 = findViewById(R.id.user_pic_3);
        user_pic_4 = findViewById(R.id.user_pic_4);

        btn_pic = findViewById(R.id.btn_pic);
        btn_pic_2 = findViewById(R.id.btn_pic_2);
        btn_pic_3 = findViewById(R.id.btn_pic_3);
        btn_pic_4 = findViewById(R.id.btn_pic_4);
        btn_save = findViewById(R.id.btn_save);

        txt = findViewById(R.id.txt);
        txt_picmore = findViewById(R.id.txt_picmore);



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_placingimageorder_api();
            }
        });



        btn_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_top_picmore.setVisibility(View.VISIBLE);
                uploadFile(selectedImage,btn_pic, user_pic);
            }
        });


        btn_pic_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadFile(selectedImage, btn_pic_2, user_pic_2);
            }
        });

        btn_pic_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(selectedImage, btn_pic_3, user_pic_3);
            }
        });

        btn_pic_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(selectedImage, btn_pic_4, user_pic_4);
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
                    }
                    else if(requestCode == 3){
                        user_pic_2.setImageBitmap(bitmap);
                        type="Photo2";
                        filename="Photo2"+session.getData(session.KEY_id);
                    }
                    else if(requestCode == 5){
                        //aadhar_img.setImageBitmap(bitmap);
                        user_pic_3.setImageBitmap(bitmap);
                        type="Photo3";
                        filename="Photo3"+session.getData(session.KEY_id);

                    }
                    else if(requestCode == 7){
                        //id_img.setImageBitmap(bitmap);
                        user_pic_4.setImageBitmap(bitmap);
                        type="Photo4";
                        filename="Photo4"+session.getData(session.KEY_id);

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
                }
                else if(requestCode == 4){
                    user_pic_2.setImageBitmap(bitmap);
                    type="Photo2";
                    filename="Photo2"+session.getData(session.KEY_id);
                }
                else if(requestCode == 6){
                    user_pic_3.setImageBitmap(bitmap);
                    type="Photo3";
                    filename="Photo3"+session.getData(session.KEY_id);

                    //store_info.setImage("image_a",bitmap);
                }
                else if(requestCode == 8){
                    user_pic_4.setImageBitmap(bitmap);
                    type="Photo4";
                    filename="Photo4"+session.getData(session.KEY_id);
                }
            }
        }
    }





    private void showPic_1Image(final Button btn, final ImageView img, final String image_name) {

        try{
            final String url_img_pic = Constant.UPLOAD_IMAGE_SHOW+image_name;
            Log.d("url", url_img_pic);
            if (!url_img_pic.contentEquals("")) {
                Picasso.with(mContext)
                        .load(url_img_pic)
                        .placeholder( R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(img, new Callback() {
                            @Override
                            public void onSuccess() {
                                btn.setEnabled(false);
                                btn.setBackgroundColor(RED);
                                img.setEnabled(true);
                                img.setTag(url_img_pic);
                            }
                            @Override
                            public void onError() {
                                btn.setEnabled(true);
                                btn.setBackgroundColor(Color.parseColor("#09B150"));
                                img.setEnabled(true);
                            }
                        });
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }






    private void uploadFile(Uri fileUri,  final Button btn_pic, final ImageView user_pic) {
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
                            String name = jsonObject.getString("name");
                            arrayList.add(name);
                            showPic_1Image(btn_pic, user_pic, name);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    pDialog.dismiss();


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



    private void call_placingimageorder_api()
    {
        progressBar.setVisibility(View.VISIBLE);
        String order_imgs="";
        for(int i = 0; i<arrayList.size(); i++)
        {
            order_imgs = arrayList.get(i)+",";
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", session.getData(Session.KEY_id));
        params.put("phone_no", session.getData(Session.KEY_mobile));
        params.put("order_imgs", order_imgs);
        params.put("decsription", txt.getText().toString());
        params.put("order_type", "2");


        ApiConfig.RequestToVolley_POST(new VolleyCallback() {
            @Override
            public void onSuccess(boolean result, String response) {
                if (result) {
                    try {
                        System.out.println("====res area " + response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString(Constant.SUCESS).equalsIgnoreCase("200"))
                        {
                            Toast.makeText(mContext, "Your Order Was Successfully Placed", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else{
                            Toast.makeText(mContext, "Error Has Occurred Please Try Again Later", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }, activity, Constant.BASEPATH + Constant.PLACING_IMAGEORDER , params, false);
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

}
