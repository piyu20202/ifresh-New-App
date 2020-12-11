package com.ifresh.customer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ifresh.customer.R
import com.ifresh.customer.model.State
import com.ifresh.customer.helper.TextDrawable
import java.util.*


class StateAdapter(var mContext: Context, var stateArrList: ArrayList<State>) : BaseAdapter()
{
    private val inflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return stateArrList.size
    }

    override fun getItem(p0: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val view: View
        val holder: ViewHolder
        if (convertView == null)
        {
            view = inflater.inflate(R.layout.simple_spinner_item, parent, false)
            holder = ViewHolder(view)
            view?.tag = holder
        }
        else {
            view = convertView
            holder = view.tag as ViewHolder
        }
        holder.statename.text = stateArrList.get(position).state_name
        holder.statename.tag = stateArrList.get(position).state_id
        val d = TextDrawable(mContext)
        d.text = stateArrList.get(position).state_name?.substring(0, 1)
        d.textAlign = Layout.Alignment.ALIGN_CENTER
        //d.setTextColor(R.color.colorAccent)
        d.colorFilter = PorterDuffColorFilter(R.color.colorPrimary, PorterDuff.Mode.SRC_IN)
        holder.base.setImageDrawable(d)
        return view
    }

    private class ViewHolder(row: View?) {
        val statename:TextView = row?.findViewById(R.id.text_itemlist_spinner) as TextView
        val base: ImageView = row?.findViewById(R.id.imageView4) as ImageView
   }






}