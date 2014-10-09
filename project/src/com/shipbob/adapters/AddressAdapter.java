package com.shipbob.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.ShipBob.R;
import com.shipbob.models.UserAddress;

import java.util.ArrayList;

/**
 * Created by waldemar on 06.06.14.
 */
public class AddressAdapter extends ArrayAdapter<UserAddress> {

    private final Context context;
    private final int layoutResourceId;
    ArrayList<UserAddress> data;


    public AddressAdapter(Context context, int layoutResourceId, ArrayList<UserAddress> data) {
        super(context, layoutResourceId, data);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final AddressHolder addressHolder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            addressHolder = new AddressHolder();

            addressHolder.name = (TextView) convertView.findViewById(R.id.nameTextView);
            addressHolder.address = (TextView) convertView.findViewById(R.id.addressTextView);

            convertView.setTag(addressHolder);

        } else {
            addressHolder = (AddressHolder) convertView.getTag();
        }

        final UserAddress userAddress = (UserAddress) data.get(position);

        if (userAddress != null) {
            addressHolder.name.setText(userAddress.getName());
            addressHolder.address.setText(constructDestinationAddress(userAddress));
        }

        return convertView;
    }

    static class AddressHolder {
        TextView name;
        TextView address;
    }

    private String constructDestinationAddress(UserAddress userContact) {
    	
    	if(!userContact.getHouseNumber().equals("null")){
    		 return userContact.getHouseNumber() + "," + userContact.getStreetAddress1() + "," +
    	                userContact.getCity() + "," + userContact.getZipCode();
    	}
    	else{
    		
    		return userContact.getStreetAddress1() + "," +
    	                userContact.getCity() + "," + userContact.getZipCode();
    	}
       

    }
}
