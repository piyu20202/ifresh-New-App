package com.ifresh.customerr.kotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifresh.customerr.R
import com.ifresh.customerr.activity.CartActivity_2
import com.ifresh.customerr.activity.MainActivity
import com.ifresh.customerr.adapter.*
import com.ifresh.customerr.helper.*
import com.ifresh.customerr.helper.Constant.*
import com.ifresh.customerr.model.*
import kotlinx.android.synthetic.main.activity_location_selection.*
import org.json.JSONObject
import java.util.*

class LocationSelection_K : AppCompatActivity() {
    private var activity = this
    private val mContext: Context = this@LocationSelection_K

    //private lateinit var storePrefrence: StorePrefrence
    private lateinit var session: Session
    private lateinit var storeinfo: StorePrefrence

    private lateinit var gps: GPSTracker

    private val arrayListCity = arrayListOf<CityName>()
    private val arrayListArea = arrayListOf<Area>()
    private val arrayListSubArea = arrayListOf<SubArea>()
    private val arrayListCountry = arrayListOf<Country>()
    private val arrayListState = arrayListOf<State>()

    private var areaAdapter: AreaAdapter? = null
    private var subareaAdapter: SubAreaAdapter? = null
    private var cityAdapter: CityAdapter? = null
    private var countryAdapter: CountryAdapter? = null
    private var stateAdapter: StateAdapter? = null
    private var countryid: String = ""
    private var stateid: String = ""
    private var cityid: String = ""
    private var areaid: String = ""
    private var subareaid: String = ""
    private var str_country = ""
    private var str_state = ""
    private var str_city = ""
    private var str_area = ""
    private var str_subarea = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_selection)
        //storePrefrence = StorePrefrence(mContext)
        session = Session(mContext)
        storeinfo = StorePrefrence(mContext)

        activity = this@LocationSelection_K
        gps = GPSTracker(this@LocationSelection_K)

        ApiConfig.displayLocationSettingsRequest(this@LocationSelection_K)
        ApiConfig.getLocation(this@LocationSelection_K)

        val saveLatitude = session.getCoordinates(Session.KEY_LATITUDE).toDouble()
        val saveLongitude = session.getCoordinates(Session.KEY_LONGITUDE).toDouble()

        if (saveLatitude.equals(0.0) || saveLongitude.equals(0.0)) {
            SaveLocation(gps.latitude.toString(), gps.longitude.toString())
        } else {
            SaveLocation(session.getCoordinates(Session.KEY_LATITUDE), session.getCoordinates(Session.KEY_LONGITUDE))
        }

        init_country()
        init_state()
        init_city()
        init_area()
        init_subarea()
        callApi_country(activity)

        btnsubmit.setOnClickListener(View.OnClickListener {
            when {
                countryid.isEmpty() || countryid == "-1" ->
                {
                    Toast.makeText(mContext, getString(R.string.selectcountry), Toast.LENGTH_SHORT).show()
                }
                stateid.isEmpty() || stateid == "-1" -> {
                    Toast.makeText(mContext, getString(R.string.selectstate), Toast.LENGTH_SHORT).show()
                }
                cityid.isEmpty() || cityid == "-1"  -> {
                    Toast.makeText(mContext, getString(R.string.selectcitym), Toast.LENGTH_SHORT).show()
                }
                areaid.isEmpty() || areaid == "-1" -> {
                    Toast.makeText(mContext, getString(R.string.selectaream), Toast.LENGTH_SHORT).show()
                }
                subareaid.isEmpty() || subareaid == "-1"  -> {
                    Toast.makeText(mContext, getString(R.string.selectsubarea), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    session.setData(COUNTRY_ID, countryid)
                    session.setData(COUNTRY_N, str_country)

                    session.setData(STATE_ID, stateid)
                    session.setData(STATE_N, str_state)

                    storeinfo.setString("state_id", stateid)
                    storeinfo.setString("state_name", str_state)



                    session.setData(CITY_ID, cityid)
                    session.setData(CITY_N, str_city)

                    session.setData(AREA_ID, areaid)
                    session.setData(AREA_N, str_area)

                    session.setData(SUBAREA_ID, subareaid)
                    session.setData(SUBAREA_N, str_subarea)

                    if (session.isUserLoggedIn) {
                        //user already login
                        val mainIntent = Intent(mContext, MainActivity::class.java)

                        startActivity(mainIntent);
                        finish()
                    } else {
                        // user is type of guest
                        callGuestUserApi();
                    }

                }
            }
        })

        // spinner country
        spin_country.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos > 0) {
                    val country: Country = arrayListCountry[pos]
                    //Log.d("id==>", "" + country.country_id)
                    str_country = country.country_name.toString()
                    countryid = country.country_id.toString()

                    callApi_state(activity, countryid)
                }
                else{
                    if(countryid != "-1")
                    {
                        callApi_state(activity, countryid)
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                countryid = "-1"
                str_country = ""
            }
        }

        // spinner state
        spin_state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {

                if (pos > 0)
                {
                    val state: State = arrayListState[pos]
                    //Log.d("id==>", "" + state.state_id)
                    str_state = state.state_name.toString()
                    stateid = state.state_id.toString()


                    last_city.text = ""
                    last_area.visibility = View.GONE

                    last_area.text = ""
                    last_area.visibility = View.GONE

                    last_subarea.text = ""
                    last_subarea.visibility = View.GONE

                    callApi_city(activity, stateid)
                }
                else{
                    if(stateid != "-1")
                    {
                        callApi_city(activity, stateid)
                    }

                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                stateid = "-1"
                str_state = ""
            }
        }

        //spinner city
        spin_city.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                Log.d("postion", pos.toString());
                if (pos > 0)
                {
                    val city: CityName = arrayListCity[pos]
                    //Log.d("id==>", "" + city.city_id)
                    cityid = city.city_id.toString()
                    str_city = city.city_name.toString()

                    last_city.text = ""
                    last_area.visibility = View.GONE

                    last_area.text = ""
                    last_area.visibility = View.GONE

                    last_subarea.text = ""
                    last_subarea.visibility = View.GONE


                    callApi_area(activity, cityid)
                }
                else{
                    if(cityid != "-1")
                    {
                        callApi_area(activity, cityid)
                    }
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                cityid = "-1"
                str_city = ""
            }

        }

        //spinner area
        spin_area.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos > 0) {
                    val area: Area = arrayListArea[pos]
                    //Log.d("id==>", "" + area.area_id)
                    areaid = area.area_id.toString()
                    str_area = area.area_name.toString()

                    last_area.text = ""
                    last_area.visibility = View.GONE

                    last_subarea.text = ""
                    last_subarea.visibility = View.GONE

                    callApi_subarea(activity, areaid)
                }
                else{
                    if(areaid != "-1")
                    {
                        callApi_subarea(activity, areaid)
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                areaid = "-1"
                str_area = ""
            }
        }

        spin_area_sub.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos > 0) {
                    val subArea: SubArea = arrayListSubArea[pos]
                    //Log.d("id==>", "" + subArea.subarea_id)
                    subareaid = subArea.subarea_id.toString()
                    str_subarea = subArea.subarea_name.toString()
                }
                else{
                    if(subareaid != "-1")
                    {
                        callApi_subarea(activity, areaid)
                    }


                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                subareaid = "-1"
                str_subarea = ""
            }

        }
    }

    private fun  callGuestUserApi() {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_POST_GUEST({ result, response ->
            if (result)
            {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    val data_jsonobj = jsonObject.getJSONObject("data");

                    session.setData(AUTHTOKEN, data_jsonobj.getString("authtoken"))
                    session.setData("role", data_jsonobj.getJSONObject("user").getString("role_type"))

                    val mainIntent = Intent(mContext, MainActivity::class.java)
                    startActivity(mainIntent);
                    finish()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, BASEPATH + GUEST, params, false)
    }

    override fun onResume() {
        super.onResume()

    }

    private fun init_country() {
        val country = Country()
        if (session.getData(COUNTRY_ID).isNotEmpty())
        {
            country.country_id = session.getData(COUNTRY_ID)
            country.country_name = session.getData(COUNTRY_N)

            //spin_country.isEnabled=false
            //spin_country.isFocusable=false


        } else {
            country.country_id = "-1"
            country.country_name = "Select Country"
        }


        countryid = country.country_id.toString()
        str_country = country.country_name.toString()

        arrayListCountry.add(country)
        countryAdapter = CountryAdapter(mContext, arrayListCountry)
        spin_country.adapter = countryAdapter

        session.setData(COUNTRY_ID,countryid)
        session.setData(COUNTRY_N, str_country)



    }

    private fun init_state() {
        val state = State()
        if (session.getData(STATE_ID).isNotEmpty())
        {
            state.state_id = storeinfo.getString("state_id")
            state.state_name = storeinfo.getString("state_name")
            //state.state_name = session.getData(STATE_N)
            spin_state.isEnabled=false
            spin_state.isFocusable=false
        } else {
            state.state_id = "-1"
            state.state_name = "Select State"
        }

        stateid = state.state_id.toString()
        str_state = state.state_name.toString()

        callApi_state(activity, countryid.toString())

        arrayListState.add(state)
        stateAdapter = StateAdapter(mContext, arrayListState)
        spin_state.adapter = stateAdapter
    }

    private fun init_city() {
        val city = CityName()
        if (session.getData(CITY_ID).isNotEmpty()) {
            city.city_id = session.getData(CITY_ID)
            city.city_name = session.getData(CITY_N)

            last_city.text = city.city_name
            last_city.visibility = View.VISIBLE

        } else {
            city.city_id = "-1"
            city.city_name = "Select City"

            last_city.text = ""
            last_city.visibility = View.GONE
        }

        cityid = city.city_id.toString()
        str_city = city.city_name.toString()

        arrayListCity.add(city)
        cityAdapter = CityAdapter(mContext, arrayListCity)
        spin_city.adapter = cityAdapter



    }

    private fun init_area() {
        val area = Area()
        if (session.getData(AREA_ID).isNotEmpty()) {
            area.area_id = session.getData(AREA_ID)
            area.area_name = session.getData(AREA_N)

            last_area.text = area.area_name
            last_area.visibility = View.VISIBLE
        } else {
            area.area_id = "-1"
            area.area_name = "Select Area"

            last_area.text = ""
            last_area.visibility = View.GONE
        }

        areaid = area.area_id.toString()
        str_area = area.area_name.toString()

        arrayListArea.add(area)
        areaAdapter = AreaAdapter(mContext, arrayListArea)
        spin_area.adapter = areaAdapter

    }

    private fun init_subarea() {
        val subArea = SubArea()
        if (session.getData(SUBAREA_ID).isNotEmpty())
        {
            subArea.subarea_id = session.getData(SUBAREA_ID)
            subArea.subarea_name = session.getData(SUBAREA_N)

            last_subarea.text = subArea.subarea_name
            last_subarea.visibility = View.VISIBLE

        } else {
            subArea.subarea_id = "-1"
            subArea.subarea_name = "Select Sub Area"
            last_subarea.text = ""
            last_subarea.visibility = View.GONE

        }

        subareaid = subArea.subarea_id.toString()
        str_subarea = subArea.subarea_name.toString()


        arrayListSubArea.add(subArea)
        subareaAdapter = SubAreaAdapter(mContext, arrayListSubArea)
        spin_area_sub.adapter = subareaAdapter

    }

    private fun callApi_area(activity: Activity, cityId: String) {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200)
                    {
                        val jsonArray = jsonObject.optJSONArray("data")

                        arrayListArea.clear()
                        val area = Area()
                        area.area_id = "-1"
                        area.area_name = "Select Area"
                        arrayListArea.add(area)

                        arrayListSubArea.clear()
                        val subArea = SubArea()
                        subArea.subarea_id = "-1"
                        subArea.subarea_name = "Select Sub Area"
                        arrayListSubArea.add(subArea)

                        areaAdapter?.notifyDataSetChanged()
                        subareaAdapter?.notifyDataSetChanged()
                        spin_area.setSelection(0)
                        spin_area_sub.setSelection(0)


                        if(session.getData(CITY_ID)!= cityId)
                        {
                            areaid="-1"
                            subareaid="-1"
                        }

                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val area = Area()
                            area.area_id = jsonObject.getString("_id")
                            area.area_name = jsonObject.getString("title")
                            arrayListArea.add(area)

                        }
                        areaAdapter?.notifyDataSetChanged()
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

    private fun callApi_subarea(activity: Activity, areaId: String) {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200)
                    {
                        arrayListSubArea.clear()
                        val subArea = SubArea()
                        subArea.subarea_id = "-1"
                        subArea.subarea_name = "Select Sub Area"
                        arrayListSubArea.add(subArea)

                        subareaAdapter?.notifyDataSetChanged()
                        spin_area_sub.setSelection(0)

                        val jsonArray = jsonObject.optJSONArray("data")
                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val subarea = SubArea()
                            subarea.subarea_id = jsonObject.getString("_id")
                            subarea.subarea_name = jsonObject.getString("title")
                            arrayListSubArea.add(subarea)
                        }
                        subareaAdapter?.notifyDataSetChanged()

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_SUBAREA + areaId, params, true)
    }

    private fun callApi_city(activity: Activity, state_id: String) {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(SUCESS) == 200) {
                        val jsonArray = jsonObject.optJSONArray("data")

                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val city = CityName()
                            if(session.getData(CITY_ID) == jsonObject.getString("_id"))
                            {
                                //do not add
                            }
                            else{
                                city.city_id = jsonObject.getString("_id")
                                city.city_name = jsonObject.getString("title")
                                arrayListCity.add(city)
                            }
                        }
                        cityAdapter?.notifyDataSetChanged()


                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }


                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_CITY + state_id, params, true)
    }

    private fun callApi_country(activity: Activity) {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {

                        val jsonArray = jsonObject.optJSONArray("data")
                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val country = Country()
                            if(session.getData(Constant.COUNTRY_ID) == jsonObject.getString("_id"))
                            {
                                //do not add
                            }
                            else{
                                country.country_id = jsonObject.getString("_id")
                                country.country_name = jsonObject.getString("title")
                                arrayListCountry.add(country)
                            }
                        }
                        countryAdapter?.notifyDataSetChanged()

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_COUNTRY, params, true)
    }

    private fun callApi_state(activity: Activity, country_id: String) {
        val params: MutableMap<String, String> = HashMap()
        ApiConfig.RequestToVolley_GET({ result, response ->
            if (result) {
                try {
                    println("===n response $response")
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getInt(Constant.SUCESS) == 200) {
                        val jsonArray = jsonObject.optJSONArray("data")

                        for (i in 0 until jsonArray.length())
                        {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val state = State()
                            if(session.getData(Constant.STATE_ID) == jsonObject.getString("_id"))
                            {
                                //do not add
                            }
                            else{
                                state.state_id = jsonObject.getString("_id")
                                state.state_name = jsonObject.getString("title")
                                arrayListState.add(state)
                            }
                        }
                        stateAdapter?.notifyDataSetChanged()

                    } else {
                        Toast.makeText(mContext, jsonObject.getString("msg"), Toast.LENGTH_SHORT)
                                .show()
                    }
                } catch (e: java.lang.Exception) {

                    e.printStackTrace()
                }
            }
        }, activity, Constant.BASEPATH + Constant.GET_STATE + country_id, params, true)
    }



    fun SaveLocation(latitude: String?, longitude: String?) {
        //Log.d("lat", "" + latitude)
        //Log.d("long", "" + longitude)
        session.setData(Session.KEY_LATITUDE, latitude.toString())
        session.setData(Session.KEY_LONGITUDE, longitude.toString())

        storeinfo.setString("latitude", latitude.toString())
        storeinfo.setString("longitude", longitude.toString())


        //Log.d("valll", session.getData(Session.KEY_LATITUDE))
    }


    override fun onBackPressed() {
        Toast.makeText(applicationContext, "Disabled Back Press", Toast.LENGTH_SHORT).show()
    }

}





