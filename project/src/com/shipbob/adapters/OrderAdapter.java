package com.shipbob.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.helpers.Log;
import com.shipbob.vendors.lazylist.ImageLoader;
import com.shipbob.models.Order;

import java.util.ArrayList;

/**
 * Created by waldemar on 12.06.14.
 */
public class OrderAdapter extends ArrayAdapter<Order> {

    private final Context context;
    private final int layoutResourceId;
    ArrayList<Order> data;

    ImageLoader imageLoader;


    public OrderAdapter(Context context, int layoutResourceId, ArrayList<Order> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

        imageLoader = new ImageLoader(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final OrderHolder orderHolder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            orderHolder = new OrderHolder();

            orderHolder.name = (TextView) convertView.findViewById(R.id.nameTextView);
            orderHolder.status = (TextView) convertView.findViewById(R.id.statusTextView);
            orderHolder.image = (ImageView) convertView.findViewById(R.id.imageImageView);
            orderHolder.cancel = (ImageButton) convertView.findViewById(R.id.cancelImageButton);

            convertView.setTag(orderHolder);

        } else {
            orderHolder = (OrderHolder) convertView.getTag();
        }

        final Order order = (Order) data.get(position);

        if (order != null) {
//            orderHolder.name.setText(order.getContactName());
            orderHolder.status.setText(order.getStatus());
            imageLoader.DisplayImage(ShipBobApplication.IMAGE_HOST + order.getImageUrl(), orderHolder.image, ImageLoader.NORMAL);
            orderHolder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelItem(order);
                }
            });
        }

        return convertView;
    }

    private void cancelItem(Order order) {
        Log.d("Cancel");
    }

    static class OrderHolder {
        ImageView image;
        TextView name;
        TextView status;
        ImageButton cancel;

    }




}
