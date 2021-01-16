package com.ifresh.customer.kotlin


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.ifresh.customer.R
import com.ifresh.customer.activity.SplashActivity
import com.ifresh.customer.helper.ApiConfig
import com.ifresh.customer.helper.Constant
import com.ifresh.customer.helper.Constant.*
import com.ifresh.customer.helper.Session
import kotlinx.android.synthetic.main.activity_view_signup.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.regex.Pattern


class SignUpActivity_K : AppCompatActivity()
{
    private val mContext:Context = this@SignUpActivity_K
    private val activity = this
    private lateinit var session: Session
    private lateinit var referrerClient: InstallReferrerClient
    private lateinit var ip_address:String
    private lateinit var friend_code:String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_signup)
        session = Session(mContext)


        //Log.d("Ipdaddress",getIPAddress(true));
        call_SendDeviceId(activity)
        GetPublicIP().execute()


        //initTracking()
        btnsignup.setOnClickListener(){
            val phoneNo = edtsignupMobile.text.toString()
            if(edtLoginMobile.text.isEmpty())
            {
                ApiConfig.setSnackBar(getString(R.string.empty_phone), "RETRY", activity)
            }
            else{
                if(isValidMobile(edtsignupMobile.text.toString()))
                {
                    if(edtfirstname.text.isEmpty())
                    {
                      ApiConfig.setSnackBar(getString(R.string.invalid_firstname), "RETRY", activity)
                    }
                    else{
                        /*if(edtlastname.text.isEmpty())
                        {
                            ApiConfig.setSnackBar(getString(R.string.invalid_lastname), "RETRY", activity)
                        }
                        else{
                            // all field have data
                            callaSighup(activity, phoneNo)
                        }*/
                        // all field have data
                        callaSighup(activity, phoneNo)
                    }

                }
                else{
                    ApiConfig.setSnackBar(getString(R.string.invalid_phone), "RETRY", activity)
                }
            }
        }

        tvlogin.setOnClickListener(){
            val mainIntent = Intent(mContext, SignInActivity_K::class.java)
            startActivity(mainIntent);
        }

    }

    private fun initTracking() {
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {

            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // Connection established.
                        obtainReferrerDetails()
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        // API not available on the current Play Store app.
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        // Connection couldn't be established.
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun obtainReferrerDetails(){
        val response: ReferrerDetails = referrerClient.installReferrer
        Log.d("response - ", response.toString())
        val referrerUrl: String = response.installReferrer
        Log.d("referrerUrl - ", referrerUrl)
        Toast.makeText(mContext, "url"+response.getInstallReferrer(), Toast.LENGTH_LONG).show()

        //tvlogin.text = referrerUrl

        val referrerClickTime: Long = response.referrerClickTimestampSeconds
        Log.d("referrerClickTime - ", referrerClickTime.toString())
        val appInstallTime: Long = response.installBeginTimestampSeconds
        Log.d("appInstallTime - ", appInstallTime.toString())
        val instantExperienceLaunched: Boolean = response.googlePlayInstantParam
        Log.d("instantExperienceLaunch", instantExperienceLaunched.toString())
    }


    private fun callaSighup(activity: SignUpActivity_K, phone_no: String) {
        val params: MutableMap<String, String> = HashMap()
        if(edtlastname.text.isEmpty())
            params["lname"] = ""
        else
            params["lname"] = edtlastname.text.toString()

        params["fname"] = edtfirstname.text.toString()
        params["phone"] = phone_no
        params["reqForm"] = "signup"
        params["device_id"]= ApiConfig.getDeviceId(mContext)
        params["token"]= session.getData("token")
        params["uip"]= ip_address

        params[FRIEND_CODE]= edtRefer.text.toString().trim()

        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200)
                    {

                        val mainIntent = Intent(mContext, OtpActivity_K::class.java)

                        mainIntent.putExtra("reqForm", "signup")
                        mainIntent.putExtra("phone", phone_no)
                        //session.setData("refer_code",jsonObject.getString("referCode"))
                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainIntent);
                        finish()

                    } else if (jsonObject.getInt(SUCESS) == 400) {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                        val mainIntent = Intent(mContext, SignInActivity_K::class.java)
                        startActivity(mainIntent);

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }

                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, BASEPATH + LOGIN, params, true)
    }

    private fun call_SendDeviceId(activity: SignUpActivity_K)
    {
        val params: MutableMap<String, String> = HashMap()
        params["deviceId"]= ApiConfig.getDeviceId(mContext)
        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200)
                    {
                         Log.d("SUCESS",Constant.SUCESS)
                        if(jsonObject.getBoolean("data"))
                        {
                            //true show text view
                            linear_layout.visibility = VISIBLE
                            txt_usermsg.setText(Constant.DEVICE_REG_MSG)
                        }
                        else{
                            //false do not show text view
                            linear_layout.visibility = GONE
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, BASEPATH + SENDDEVICEID, params, true)
    }



    @SuppressLint("SetTextI18n")
    private fun call_SendDeviceiP(activity: SignUpActivity_K)
    {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200)
                    {
                        if(jsonObject.getJSONArray("data").length() > 0)
                        {
                            friend_code=(jsonObject.getJSONArray("data").getJSONObject(0).getString("friend_code"))
                            refer_code_val.text = "Your Refer Code is $friend_code"
                            edtRefer.setText(friend_code)
                        }
                        else{
                            //no data value in json array
                            friend_code=""
                            refer_code_val.text=""
                        }

                    }
                    else{
                        friend_code=""
                        refer_code_val.text=""
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, "$BASEPATH$GETFRIENDCODE$ip_address", params, true)
    }

    private fun isValidMobile(phone: String): Boolean
    {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length > 6 && phone.length == 10;
        }
        return false;
    }


    inner class GetPublicIP : AsyncTask<String?, String?, String>()
    {
        override fun doInBackground(vararg params: String?): String? {
            var publicIP = ""
            try {
                val s = Scanner(
                        URL(
                                "https://api.ipify.org")
                                .openStream(), "UTF-8")
                        .useDelimiter("\\A")
                publicIP = s.next()
                ip_address = publicIP
                println("My current IP address is $publicIP")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return publicIP
        }

        override fun onPostExecute(publicIp: String)
        {
            super.onPostExecute(publicIp)
            Log.e("PublicIP", publicIp + "")
            ip_address = publicIp
            Toast.makeText(mContext, "ip$publicIp", Toast.LENGTH_LONG).show()
            //Here 'publicIp' is your desire public IP
            call_SendDeviceiP(activity)
        }
    }








}
