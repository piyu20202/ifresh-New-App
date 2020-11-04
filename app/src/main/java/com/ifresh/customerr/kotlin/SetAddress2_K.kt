package com.ifresh.customerr.kotlin

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.TextView
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
import com.ifresh.customerr.activity.MainActivity
import com.ifresh.customerr.activity.MapsActivity
import com.ifresh.customerr.activity.SetDefaultAddress_2
import com.ifresh.customerr.adapter.*
import com.ifresh.customerr.helper.*
import com.ifresh.customerr.helper.Constant.*
import com.ifresh.customerr.model.*
import kotlinx.android.synthetic.main.activity_location_selection.*
import kotlinx.android.synthetic.main.activity_location_selection.spin_area
import kotlinx.android.synthetic.main.activity_location_selection.spin_area_sub
import kotlinx.android.synthetic.main.activity_location_selection.spin_city
import kotlinx.android.synthetic.main.activity_location_selection.spin_state
import kotlinx.android.synthetic.main.activity_view_setaddress.*
import kotlinx.android.synthetic.main.activity_view_setaddress.btnsave
import kotlinx.android.synthetic.main.activity_view_setaddress.chHome
import kotlinx.android.synthetic.main.activity_view_setaddress.chOther
import kotlinx.android.synthetic.main.activity_view_setaddress.chWork
import kotlinx.android.synthetic.main.activity_view_setaddress.edthno
import kotlinx.android.synthetic.main.activity_view_setaddress.edtlandmark
import kotlinx.android.synthetic.main.activity_view_setaddress.edtpincode
import kotlinx.android.synthetic.main.activity_view_setaddress.spin_addresstype
import kotlinx.android.synthetic.main.activity_view_setaddress_2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SetAddress2_K : AppCompatActivity(), OnMapReadyCallback
{
    private var activity = this
    private val  mContext: Context = this@SetAddress2_K
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

    private val arrayListCity = arrayListOf<CityName>()
    private val arrayListArea = arrayListOf<Area>()
    private val arrayListSubArea = arrayListOf<SubArea>()
    private val arrayListCountry = arrayListOf<Country>()
    private val arrayListState = arrayListOf<State>()

    private var areaAdapter: AreaAdapter?=null
    private var subareaAdapter: SubAreaAdapter?=null
    private var cityAdapter: CityAdapter?=null
    private var countryAdapter: CountryAdapter?=null
    private var stateAdapter: StateAdapter?=null
    private var countryid:String=""
    private var stateid:String=""
    private var cityid:String=""
    private var areaid:String=""
    private var subareaid:String=""

    lateinit var databaseHelper: DatabaseHelper
    var isbackto_home:Boolean = false
    var userIsInteracting:Boolean=false
    var clickcount=0
    var prepos=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_setaddress_2)
        session = Session(mContext)
        databaseHelper =  DatabaseHelper(mContext);
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

        //spinner area
        spin_area.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long)
            {
                      if(databaseHelper.getTotalCartAmt(session) > 0)
                      {
                          if(pos > 0)
                          {
                              showAlertView(pos)
                          }
                      }
                      else{
                          val area: Area = arrayListArea[pos]
                          session.setData(AREA_ID,area.area_id)
                          session.setData(AREA_N, area.area_name)

                          spin_area_sub.isEnabled = true
                          spin_area_sub.isClickable=true

                          callApi_subarea(activity, areaid, true)

                      }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                areaid=""

            }
        }

        //spinner sub area
        spin_area_sub.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if(pos > 0)
                {
                    val subArea: SubArea = arrayListSubArea[pos]
                    Log.d("id==>", "" + subArea.subarea_id)
                    subareaid = subArea.subarea_id.toString()
                    session.setData(SUBAREA_ID, subareaid)
                    session.setData(SUBAREA_N, subArea.subarea_name)
                    session.setData(SUBAREA_N, subArea.subarea_name.toString())
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                subareaid=""
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

    private fun call_saveaddress(activity: SetAddress2_K) {
        pdialog.visibility=View.VISIBLE
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
        params["areaId"] = session.getData(AREA_ID)
        Log.d("areaId", session.getData(AREA_ID))
        params["cityId"] = session.getData(CITY_ID)
        Log.d("cityId", session.getData(CITY_ID))
        params["sub_areaId"] = session.getData(SUBAREA_ID)
        Log.d("sub_areaId", session.getData(SUBAREA_ID))
        params["stateId"] = session.getData(STATE_ID)
        Log.d("stateId", session.getData(STATE_ID))
        params["countryId"] = session.getData(COUNTRY_ID)
        Log.d("countryId", session.getData(COUNTRY_ID))
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
                    if (jsonObject.getInt(SUCESS) == 200)
                    {
                        Toast.makeText(mContext, ADDRESS_SAVEMSG, Toast.LENGTH_SHORT).show()

                        edthno.setText("");
                        edtlandmark.setText("");
                        edtpincode.setText("");

                        addresstype_id = "0";
                        chOther.isChecked = false
                        chHome.isChecked = false
                        chWork.isChecked = false
                        pdialog.visibility=View.GONE

                        onBackPressed()
                    }
                    else {
                        pdialog.visibility=View.GONE
                        Toast.makeText(mContext, jsonObject.getString("Error Occur Please Try Again"), Toast.LENGTH_SHORT)
                                .show()
                    }


                } catch (e: java.lang.Exception) {
                    pdialog.visibility=View.GONE
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
        Handler().postDelayed({ mapFragment!!.getMapAsync(this@SetAddress2_K) }, 1000)
        userId = intent.getStringExtra("userId").toString();

        callApidefaultAdd(BASEPATH + GET_USERDEFULTADD)
    }


    private fun call_addresstype(activity: SetAddress2_K)
    {
        val str_addresstype = session.getData(KEY_ADDRESS)
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
        if (ContextCompat.checkSelfPermission(this@SetAddress2_K, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.location_permission))
                        .setMessage(getString(R.string.location_permission_message))
                        .setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
                            ActivityCompat.requestPermissions(this@SetAddress2_K, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
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
        if (ApiConfig.isGPSEnable(this@SetAddress2_K)) //startActivity(new Intent(ProfileActivity.this, MapActivity.class));
            startActivity(Intent(this@SetAddress2_K, MapsActivity::class.java)) else ApiConfig.displayLocationSettingsRequest(this@SetAddress2_K)
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
        pdialog.visibility=View.VISIBLE
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
                            Toast.makeText(mContext, "No Default Address", Toast.LENGTH_SHORT).show()

                        }
                    }
                    else{
                        Toast.makeText(mContext, "No Default Address", Toast.LENGTH_SHORT).show()
                    }
                    pdialog.visibility=View.GONE
                    init_state();

                } catch (e: JSONException) {
                    pdialog.visibility=View.GONE
                    e.printStackTrace()
                }
            }
        }, activity, url, params, false)
    }

    private fun init_state() {
        val state = State()

        state.state_id = session.getData(STATE_ID)
        state.state_name = session.getData(STATE_N)


        arrayListState.add(state)
        stateAdapter = StateAdapter(mContext, arrayListState)
        spin_state.adapter = stateAdapter

        session.setData(STATE_N,state.state_name.toString())

        init_city()

        spin_state.isEnabled=false
        spin_state.isClickable=false

    }

    private fun init_city() {
        val city = CityName()

        city.city_id = session.getData(COUNTRY_ID)
        city.city_name = session.getData(COUNTRY_N)

        arrayListCity.add(city)
        cityAdapter = CityAdapter(mContext, arrayListCity)
        spin_city.adapter = cityAdapter

        spin_city.isEnabled=false
        spin_city.isClickable=false

        init_area()
        init_subarea();
    }


    private fun init_area() {
        val area = Area()
        area.area_id = session.getData(AREA_ID)
        area.area_name = session.getData(AREA_NAME)
        arrayListArea.add(area)
        areaAdapter = AreaAdapter(mContext, arrayListArea)
        spin_area.adapter = areaAdapter

        callApi_area(activity, cityid)
    }

    private fun init_subarea()
    {
        val subArea=SubArea()
        subArea.subarea_id=session.getData(SUBAREA_ID)
        subArea.subarea_name=session.getData(SUBAREA_N)
        arrayListSubArea.add(subArea)

        subareaAdapter = SubAreaAdapter(mContext, arrayListSubArea)
        spin_area_sub.adapter=subareaAdapter

        spin_area_sub.isEnabled = false;
        spin_area_sub.isClickable=false

    }


    private fun callApi_area(activity: Activity, cityId: String)
    {
        pdialog.visibility=View.VISIBLE
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)

                    if (jsonObject.getInt(SUCESS) == 200)
                    {
                        val jsonArray = jsonObject.optJSONArray("data")

                        /*arrayListArea.clear()
                        val area = Area()
                        area.area_id = "0"
                        area.area_name = "Select Area"
                        arrayListArea.add(area)*/
                        //arrayListArea.removeAt(0)

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val area = Area()
                            if(session.getData(Constant.AREA_ID) == jsonObject.getString("_id"))
                            {
                                //do not add
                            }
                            else{
                                area.area_id = jsonObject.getString("_id")
                                area.area_name = jsonObject.getString("title")
                                arrayListArea.add(area)
                            }
                        }

                        pdialog.visibility=View.GONE
                        areaAdapter?.notifyDataSetChanged()

                        //spin_area.setSelection(0)

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, BASEPATH + GET_AREA + cityId, params, true)
    }


    private fun callApi_subarea(activity: Activity, areaId: String, isspinclick:Boolean)
    {
        pdialog.visibility=View.VISIBLE
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(SUCESS) == 200)
                    {
                        val jsonArray = jsonObject.optJSONArray("data")
                        val subarea = SubArea()
                        if(isspinclick)
                        {
                          arrayListSubArea.clear()
                          subarea.subarea_id = "0"
                          subarea.subarea_name = "Select Sub Area"
                          arrayListSubArea.add(subarea)
                          spin_area_sub.setSelection(0)
                        }
                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val subarea = SubArea()
                            subarea.subarea_id = jsonObject.getString("_id")
                            subarea.subarea_name = jsonObject.getString("title")
                            arrayListSubArea.add(subarea)
                        }
                        pdialog.visibility=View.GONE
                        subareaAdapter?.notifyDataSetChanged()
                    }

                    else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {
                    pdialog.visibility=View.GONE
                    e.printStackTrace()
                }
            }
        }, activity, BASEPATH + GET_SUBAREA + areaId, params, true)
    }


    private fun showAlertView(pos: Int)
    {

        val alertDialog = AlertDialog.Builder(mContext)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.msg_view_6, null)
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)
        val dialog = alertDialog.create()
        Objects.requireNonNull(dialog.window)?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvyes: TextView
        val tvclose: TextView
        tvyes = dialogView.findViewById(R.id.tvyes)
        tvclose = dialogView.findViewById(R.id.tvclose)

        tvyes.setOnClickListener {
            dialog.dismiss()
            //delete your cart and call change area api
            databaseHelper?.DeleteAllOrderData()
            isbackto_home=true

            //fill data by api
            val area: Area = arrayListArea[pos]
            Log.d("AREAID", ""+area.area_id)

            session.setData(AREA_ID,area.area_id)
            session.setData(AREA_N, area.area_name)

            subareaAdapter?.notifyDataSetChanged()

            spin_area_sub.isEnabled = true
            spin_area_sub.isClickable=true


            callApi_subarea(activity, areaid,true)

        }
        tvclose.setOnClickListener {
            dialog.dismiss()
            arrayListArea.clear()
            val area = Area()
            area.area_id = session.getData(AREA_ID)
            area.area_name = session.getData(AREA_N)
            arrayListArea.add(area)
            areaAdapter = AreaAdapter(mContext, arrayListArea)
            spin_area.adapter = areaAdapter

            spin_area_sub.isEnabled = false;
            spin_area_sub.isClickable=false


            callApi_area(activity, cityid)


            //onBackPressed();
        }
        dialog.show()
    }


    override fun onBackPressed()
    {
        super.onBackPressed()
        if(isbackto_home)
        {
            val mainIntent = Intent(mContext, MainActivity::class.java)
            startActivity(mainIntent);
            finish()
        }
        else{
            val mainIntent = Intent(mContext, SetDefaultAddress_2::class.java)
            startActivity(mainIntent);
            finish()
        }



    }



}


