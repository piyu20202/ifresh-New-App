package com.ifresh.customer.kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customer.R
import com.ifresh.customer.activity.MainActivity
import com.ifresh.customer.helper.ApiConfig
import com.ifresh.customer.helper.Constant
import com.ifresh.customer.helper.StorePrefrence
import org.json.JSONObject
import java.util.HashMap

class SplashActivity_KT : AppCompatActivity()
{
    private lateinit var  storePrefrence: StorePrefrence
    private val  mContext: Context = this@SplashActivity_KT
    private var activity = this
    private val SPLASH_TIME_OUT = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        storePrefrence = StorePrefrence(mContext);
        //callaSetting(activity)
        GotonextScreeen();
    }

    fun GotonextScreeen()
    {
        Handler().postDelayed({
            if (storePrefrence.getString("area_id").length > 0)
            {
                if (storePrefrence.getBoolean("IS_USER_LOGIN")) {
                    val intent = Intent(this@SplashActivity_KT, MainActivity::class.java)
                    //val intent = Intent(this@SplashActivity_KT, SetAddress_K::class.java)

                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                else {
                    val intent = Intent(this@SplashActivity_KT, SignUpActivity_K::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this@SplashActivity_KT, LocationSelection_K::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }, SPLASH_TIME_OUT.toLong())

    }

    private fun callaSetting(activity: Activity)
    {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result)
            {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        GotonextScreeen();
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.SETTINGS, params, true)
    }

}