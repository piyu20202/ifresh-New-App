package com.ifresh.customerr.kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ifresh.customerr.R
import com.ifresh.customerr.activity.MapsActivity
import com.ifresh.customerr.adapter.AreaTypeAdapter
import com.ifresh.customerr.helper.ApiConfig
import com.ifresh.customerr.helper.Constant
import com.ifresh.customerr.helper.Constant.*
import com.ifresh.customerr.helper.Session
import com.ifresh.customerr.model.AddressType
import kotlinx.android.synthetic.main.activity_view_setaddress.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SetAddress_K : AppCompatActivity(), OnMapReadyCallback
{
    private var activity = this
    private val  mContext: Context = this@SetAddress_K
    private val arrayListAreaType = arrayListOf<AddressType>()
    private var areaTypeAdapter: AreaTypeAdapter?=null
    private lateinit var session:Session
    var latitude = 0.0
    var longitude:Double = 0.0
    var addresstype_id:String="0"
    var mapFragment: SupportMapFragment? = null
    var toolbar: Toolbar? = null
    var from_str:String=""
    var addresstype_id_get:String=""
    var userId:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_setaddress)
        session = Session(mContext)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = mContext.getString(R.string.address_save)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        // spinner country
        spin_addresstype.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if(pos > 0)
                {
                    val addressType: AddressType = arrayListAreaType[pos]
                    Log.d("id==>", "" + addressType.adress_id)
                    addresstype_id = addressType.adress_id.toString()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                addresstype_id="-1"
            }
        }

        chHome.setOnClickListener {
            chHome.isChecked = true
            chWork.isChecked = false
            chOther.isChecked = false
            addresstype_id = "1"
        }
        chWork.setOnClickListener {
            chWork.isChecked = true
            chHome.isChecked = false
            chOther.isChecked = false
            addresstype_id = "2"
        }
        chOther.setOnClickListener {
            chOther.isChecked = true
            chHome.isChecked = false
            chWork.isChecked = false
            addresstype_id = "3"
        }

        btnsave.setOnClickListener(View.OnClickListener {

            if (edthno.text.isEmpty())
            {
                ApiConfig.setSnackBar(getString(R.string.empty_hno), "RETRY", activity)
            }
            else if (edtlandmark.text.isEmpty())
            {
                ApiConfig.setSnackBar(getString(R.string.empty_landmark), "RETRY", activity)
            } else if (addresstype_id == "0")
            {
                ApiConfig.setSnackBar(getString(R.string.empty_addtype), "RETRY", activity)
            }
            else {
                call_saveaddress(activity)
            }

        })
    }

    private fun call_saveaddress(activity: SetAddress_K) {
        val params: MutableMap<String, String> = HashMap()
        //params["address1"] = edthno.text.toString() + " " +edtcolony.text.toString()
        params["address1"] = edthno.text.toString()
        Log.d("address1", edthno.text.toString())
        params["address2"] = edtlandmark.text.toString()
        Log.d("address2", edtlandmark.text.toString())
        params["pincode"] = edtpincode.text.toString()
        session.setData("pincode",edtpincode.text.toString())
        Log.d("pincode", edtpincode.text.toString())
        params["phone_no"] = session.getData(Session.KEY_mobile)
        Log.d("phone_no", session.getData(Session.KEY_mobile))
        params["areaId"] = session.getData(Session.KEY_AREA_ID)
        Log.d("areaId", session.getData(Session.KEY_AREA_ID))
        params["cityId"] = session.getData(Session.KEY_CITY_ID)
        Log.d("cityId", session.getData(Session.KEY_CITY_ID))
        params["sub_areaId"] = session.getData(Session.KEY_SUBAREA_ID)
        Log.d("sub_areaId", session.getData(Session.KEY_SUBAREA_ID))
        params["stateId"] = session.getData(Session.KEY_STATE_ID)
        Log.d("stateId", session.getData(Session.KEY_STATE_ID))
        params["countryId"] = session.getData(Session.KEY_COUNTRY_ID)
        Log.d("countryId", session.getData(Session.KEY_COUNTRY_ID))
        params["lat"] = latitude.toString()
        Log.d("lat", latitude.toString())
        params["long"] = longitude.toString()
        Log.d("long", longitude.toString())
        params["address_type"] = addresstype_id
        Log.d("address_type", addresstype_id)
        params["userId"] = session.getData(Session.KEY_id)
        Log.d("userId", session.getData(Session.KEY_id))

        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        Toast.makeText(mContext, ADDRESS_SAVEMSG, Toast.LENGTH_SHORT).show()
                        edthno.setText("");
                        //edtcolony.setText("");
                        edtlandmark.setText("");
                        edtpincode.setText("");
                        edtcity.setText("");
                        edtstate.setText("");

                        addresstype_id = "0";
                        chOther.isChecked = false
                        chHome.isChecked = false
                        chWork.isChecked = false

                        /*val default_add: String = edthno.text.toString() + " " + edtcolony.text.toString() +
                                edtlandmark.text.toString() + " " + "\n" + "PinCode:" + edtpincode.text.toString()

                        val default_add_2: String = edthno.text.toString() + " " + edtcolony.text.toString() + "$" +
                                edtlandmark.text.toString() + "$" + "PinCode" + "$" + edtpincode.text.toString()


                        if (from_str == "checkout") {
                            session.setData("pas_address", default_add_2)
                            session.setData(Session.KEY_ADDRESS, default_add)
                            session.setData("addresstype_id", addresstype_id)
                        }*/


                    } else {
                        Toast.makeText(mContext, jsonObject.getString("Address Save Successfully"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.SAVE_DELIVERYADDRESS, params, true)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        latitude = session.getCoordinates(Session.KEY_LATITUDE).toDouble()
        longitude = session.getCoordinates(Session.KEY_LONGITUDE).toDouble()
        Log.d("lat", "" + latitude);
        Log.d("long", "" + longitude);
        //tvCurrent.text = getString(R.string.location_1) + ApiConfig.getAddress(latitude, longitude, this@SetAddress_K)
        Handler().postDelayed({ mapFragment!!.getMapAsync(this@SetAddress_K) }, 1000)



        //edtcity.setText("Jodhpur")
        //edtstate.setText("Rajasthan")
        edtcity.setText(session.getData(Session.KEY_CITY_N))
        edtstate.setText(session.getData(Session.KEY_STATENAME_N))
        userId = intent.getStringExtra("userId").toString();
        callApidefaultAdd(Constant.BASEPATH + Constant.GET_USERDEFULTADD)

        //init_addressstype()

    }

    private fun call_addresstype(activity: SetAddress_K) {

        val str_addresstype = session.getData(Constant.KEY_ADDRESS)
        val jsonArray = JSONArray(str_addresstype)
        for (i in 0 until jsonArray.length())
        {
            val jsonObject = jsonArray.getJSONObject(i)
            val addressType = AddressType()
            addressType.adress_id = jsonObject.getString("id")
            addressType.address_type = jsonObject.getString("title")

            arrayListAreaType.add(addressType)
        }
        areaTypeAdapter?.notifyDataSetChanged()

    }

    private fun init_addressstype() {
        val addressType = AddressType()
        addressType.adress_id ="-1"
        addressType.address_type = "Select Address Type"
        arrayListAreaType.add(addressType)
        areaTypeAdapter = AreaTypeAdapter(mContext, arrayListAreaType)
        spin_addresstype.adapter = areaTypeAdapter
        call_addresstype(activity)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(this@SetAddress_K, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.location_permission))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
                            ActivityCompat.requestPermissions(this@SetAddress_K, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    0)
                        }
                        .create()
                        .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        0)
            }
        }

        val mMap: GoogleMap = googleMap
        val saveLatitude: Double = session.getCoordinates(Session.KEY_LATITUDE).toDouble()
        val saveLongitude: Double = session.getCoordinates(Session.KEY_LONGITUDE).toDouble()
        mMap.clear()

        val latLng = LatLng(saveLatitude, saveLongitude)
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        val icon = BitmapDescriptorFactory.fromResource(R.drawable.location)
        val marker = MarkerOptions().position(LatLng(latitude, longitude)).draggable(true).title(getString(R.string.current_location))
        marker.icon(icon)
        mMap.addMarker(marker)
        val cameraPosition = CameraPosition.Builder()
                .target(latLng).zoom(15f).tilt(60f).build()
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition))
    }

    fun UpdateLocation_pro(view: View?)
    {
        if (ApiConfig.isGPSEnable(this@SetAddress_K)) //startActivity(new Intent(ProfileActivity.this, MapActivity.class));
            startActivity(Intent(this@SetAddress_K, MapsActivity::class.java)) else ApiConfig.displayLocationSettingsRequest(this@SetAddress_K)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun callApidefaultAdd(url: String) {
        val params: MutableMap<String, String> = HashMap()
        Log.d("userId", session.getData(Session.KEY_id))
        params["userId"] = session.getData(Session.KEY_id)
        ApiConfig.RequestToVolley_POST({ result, response ->
            if (result) {
                try {
                    println("====res area=>$response")
                    val jsonObject = JSONObject(response)

                    if(jsonObject.has(SUCESS))
                    {
                        if (jsonObject.getInt(Constant.SUCESS) == 200)
                        {
                            //fill address
                            val objectData = jsonObject.getJSONObject("data")
                            if (objectData.length() > 0)
                            {
                                val jsonObject_address = objectData.getJSONObject("address")
                                edthno.setText(jsonObject_address.getString("address1"))
                                edtlandmark.setText(jsonObject_address.getString("address2"))
                                edtpincode.setText(jsonObject_address.getString("pincode"))

                                if (jsonObject_address.getString("address_type") == "1") {
                                    chHome.isChecked = true;
                                    chWork.isChecked = false;
                                    chOther.isChecked = false;
                                } else if (jsonObject_address.getString("address_type") == "2") {
                                    chWork.isChecked = true;
                                    chHome.isChecked = false;
                                    chOther.isChecked = false;
                                } else if (jsonObject_address.getString("address_type") == "3") {
                                    chOther.isChecked = true;
                                    chHome.isChecked = false;
                                    chWork.isChecked = false;
                                }

                            }
                        } else {
                            Toast.makeText(mContext, "No Address", Toast.LENGTH_SHORT).show()

                        }

                    }
                    else{
                        Toast.makeText(mContext, "No Address", Toast.LENGTH_SHORT).show()
                    }




                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, url, params, false)
    }


}


