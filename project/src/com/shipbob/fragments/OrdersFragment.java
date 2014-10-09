package com.shipbob.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.facebook.ShipBob.R;
import com.shipbob.adapters.OrderAdapter;
import com.shipbob.asynctasks.AllOrdersAsyncTask;
import com.shipbob.models.Order;

import java.util.ArrayList;

/**
 * Created by waldemar on 03.06.14.
 */
public class OrdersFragment extends Fragment {

    ArrayList<Order> orders = new ArrayList<Order>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_orders, container, false);

        ListView orderList = (ListView) view.findViewById(R.id.listView);

        OrderAdapter orderAdapter = new OrderAdapter(getActivity(), R.layout.row_orderitem, orders);
        orderList.setAdapter(orderAdapter);

        AllOrdersAsyncTask allOrdersAsyncTask = new AllOrdersAsyncTask(getActivity(), new ProgressDialog(getActivity()), orders, orderAdapter);

        allOrdersAsyncTask.execute();

        return view;
    }




}
