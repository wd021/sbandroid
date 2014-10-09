package com.shipbob.adapters;

/**
 * Created by waldemar on 12.06.14.
 */

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ShipBob.R;
import com.facebook.ShipBob.activities.MainActivity;
import com.shipbob.models.Option;
import com.shipbob.models.Order;


public class OptionsAdapter extends ArrayAdapter<String> {

    private Activity activity;

    private ArrayList data;

    public Resources res;

    Option tempValues = null;

    LayoutInflater inflater;


    public OptionsAdapter(Context context, int textViewResourceId, ArrayList objects, Resources resLocal) {
        super(context, textViewResourceId, objects);


        activity = (MainActivity) context;
        data = objects;
        res = resLocal;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.row_option_item, parent, false);

        tempValues = null;
        tempValues = (Option) data.get(position);

        TextView label = (TextView) row.findViewById(R.id.textView);


//        if (position == 0) {
//
//            // Default selected Spinner item
//            label.setText("SHIPPING OPTIONS");
//        } else {
            // Set values for spinner each row
            label.setText(tempValues.getName());


//        }

        return row;
    }


}
