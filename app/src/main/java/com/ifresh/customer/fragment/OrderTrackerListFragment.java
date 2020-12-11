package com.ifresh.customer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.ifresh.customer.activity.OrderListActivity_2;
import com.ifresh.customer.adapter.TrackerAdapter_2;
import com.ifresh.customer.helper.Session;

import com.ifresh.customer.R;
import com.ifresh.customer.model.OrderTracker_2;

import java.util.ArrayList;


public class OrderTrackerListFragment extends Fragment {

    RecyclerView recyclerView;
    TextView nodata;
    ProgressBar progressbar;
    Session session;

    private ArrayList<OrderTracker_2> orderTrackerArrayList;
    int pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ordertrackerlist, container, false);

        pos = getArguments().getInt("pos");
        session = new Session(getActivity());

        progressbar = v.findViewById(R.id.progressbar);
        recyclerView = v.findViewById(R.id.recycleview);
        nodata = (TextView) v.findViewById(R.id.nodata);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        switch (pos) {
            case 0:
                orderTrackerArrayList = OrderListActivity_2.orderTrackerslist;
                break;
            case 1:
                orderTrackerArrayList = OrderListActivity_2.processedlist;
                break;
            case 2:
                orderTrackerArrayList = OrderListActivity_2.shippedlist;
                break;
            case 3:
                orderTrackerArrayList = OrderListActivity_2.deliveredlist;
                break;
            case 4:
                orderTrackerArrayList = OrderListActivity_2.cancelledlist;
                break;
            case 5:
                orderTrackerArrayList = OrderListActivity_2.returnedList;
                break;
        }

        if (orderTrackerArrayList.size() == 0)
            nodata.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new TrackerAdapter_2(getActivity(), orderTrackerArrayList));
        progressbar.setVisibility(View.GONE);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
