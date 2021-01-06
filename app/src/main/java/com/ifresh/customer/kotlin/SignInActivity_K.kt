package com.ifresh.customer.kotlin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customer.R

import com.ifresh.customer.helper.ApiConfig
import com.ifresh.customer.helper.Constant
import com.ifresh.customer.helper.Session
import kotlinx.android.synthetic.main.activity_view_login.*
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class SignInActivity_K : AppCompatActivity() {
    private val mContext: Context = this@SignInActivity_K
    private val activity = this
    //private lateinit var storePrefrence: StorePrefrence
    private lateinit var session: Session
    lateinit var str_param:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_login)
        session = Session(mContext)

        btnlogin.setOnClickListener(View.OnClickListener
        {
            if(edtLoginMobile.text.isEmpty())
            {
                ApiConfig.setSnackBar(getString(R.string.empty_phone), "RETRY", activity)
            }
            else{
                if (isValidMobile(edtLoginMobile.text.toString()))
                {
                    callaLogin(activity, edtLoginMobile.text.toString())
                } else {
                    ApiConfig.setSnackBar(getString(R.string.invalid_phone), "RETRY", activity)
                }
            }
        })

        tvSignUp.setOnClickListener(View.OnClickListener {
            val mainIntent = Intent(mContext, SignUpActivity_K::class.java)
            startActivity(mainIntent);
            finish()
        })

    }

    private fun callaLogin(activity: SignInActivity_K, phoneNo: String)
    {
        val params: MutableMap<String, String> = HashMap()
        params["phone"] = phoneNo
        params["reqForm"] = "login"
        params["device_id"]= ApiConfig.getDeviceId(mContext)
        params["token"]= session.getData("token")


        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)

                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        //val otp = jsonObject.getString("data")
                        val mainIntent = Intent(mContext, OtpActivity_K::class.java)
                        //mainIntent.putExtra("otp", otp)
                        mainIntent.putExtra("reqForm", "signin")
                        mainIntent.putExtra("phone", phoneNo)

                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mainIntent)
                        finish()

                    }
                    else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.LOGIN, params, true)

    }

    private fun isValidMobile(phone: String): Boolean
    {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length > 6 && phone.length == 10;
        }
        return false;
    }


}