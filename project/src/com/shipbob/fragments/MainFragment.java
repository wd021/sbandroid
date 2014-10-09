package com.shipbob.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.shipbob.GlobalMethods;
import com.facebook.ShipBob.R;
import com.facebook.ShipBob.activities.AddressActivity;
import com.facebook.ShipBob.activities.CompleteProfile_CreditCard;
import com.facebook.ShipBob.activities.CompleteProfile_PhoneNumber;
import com.facebook.ShipBob.activities.MainActivity;
import com.facebook.ShipBob.activities.ReturnAddress;
import com.shipbob.ShipBobApplication;
import com.facebook.ShipBob.activities.*;
import com.shipbob.adapters.ItemListAdapter;
import com.shipbob.asynctasks.OrderAsyncTask;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.databasehandler.ShippingInformationDatabaseHandler;
import com.shipbob.models.ShippingInformationTable;
import com.shipbob.databasehandler.UserProfileTableDatabaseHandler;
import com.shipbob.helpers.Log;
import com.shipbob.models.UserProfileTable;
import com.shipbob.vendors.bitmaphandling.BitmapScaler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by waldemar on 03.06.14.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    ArrayList<ShippingInformationTable> shipInformation;

    ListView itemListView;
    TextView streetAddress;
    ImageView editImageView;

    RelativeLayout hiddenLayout;
    FrameLayout costEstimateLayout;

    FrameLayout shipItFrameLayout;
    TextView shipItTextView;
    TextView tapToAddItems;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_main, container, false);

        itemListView = (ListView) view.findViewById(R.id.listView);
        hiddenLayout = (RelativeLayout) view.findViewById(R.id.hiddenLayout);
        hiddenLayout = (RelativeLayout) view.findViewById(R.id.hiddenLayout);

        streetAddress = (TextView) view.findViewById(R.id.streetAddress);
        streetAddress.setText(getAddress());
        streetAddress.setOnClickListener(this);

        editImageView = (ImageView) view.findViewById(R.id.imageView);
        editImageView.setOnClickListener(this);

        shipItFrameLayout = (FrameLayout) view.findViewById(R.id.shipItFrameLayout);
        shipItFrameLayout.setOnClickListener(this);

        costEstimateLayout = (FrameLayout) view.findViewById(R.id.costEstimateLayout);
        costEstimateLayout.setOnClickListener(this);
        
        shipItTextView = (TextView) view.findViewById(R.id.shipItTextView);
        shipItTextView.setOnClickListener(this);

        tapToAddItems = (TextView) view.findViewById(R.id.textViewSupport);
        tapToAddItems.setOnClickListener(this);

        shipInformation = GlobalDatabaseHandler.getShippingInformationTable(getActivity());
        if(shipInformation==null){
        	shipInformation=new ArrayList<ShippingInformationTable>();
        }

        ItemListAdapter itemListAdapter = new ItemListAdapter(getActivity(), R.layout.row_shipitem, shipInformation);

        itemListView.setAdapter(itemListAdapter);

        Log.d(String.valueOf(shipInformation.size()));

        if (shipInformation.size() > 0) {
            hiddenLayout.setVisibility(View.GONE);
            costEstimateLayout.setVisibility(View.VISIBLE);

        } else {
            hiddenLayout.setVisibility(View.VISIBLE);
            costEstimateLayout.setVisibility(View.GONE);

        }

        return view;
    }


    @Override
    public void onClick(View v) {
          String costEstimateLine2 = "Our Ship Captains will provide you a real time estimate of the shipping cost at the time of pick up.!";
          String costEstimateLine3 = "You will not be charged till we actually ship your item!";


    	switch (v.getId()) {
      
    	case R.id.textViewSupport:
    		 Intent intent = new Intent(getActivity(), AddressActivity.class);
             startActivity(intent);
             break;
    	 case R.id.streetAddress:
         case R.id.imageView:
                Intent intenttoAddress = new Intent(getActivity(), ReturnAddress.class);
                intenttoAddress.putExtra("page", MainActivity.MAIN_PAGE);
                startActivity(intenttoAddress);
                break;
            case R.id.costEstimateLayout:
            case R.id.costEstimateTextView:
                new AlertDialog.Builder(getActivity())
                .setTitle("Almost Done!")
/*                .setIcon(android.R.drawable.ic_dialog_alert)
*/                .setMessage(costEstimateLine2+"\n"+"\n"+costEstimateLine3)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create()
                .show();
                break;

            case R.id.shipItFrameLayout:
            case R.id.shipItTextView:
                Log.i("Clicked");

                if (!validation())

                    return;

                try {
                    JSONArray jsonArray = new JSONArray();

                    for (ShippingInformationTable informationTable : shipInformation) {

                        try {
                            File fileName = new File(informationTable.getImageFileName());
                            final int THUMBNAIL_SIZE = 512;

                            BitmapScaler scaler = new BitmapScaler(fileName, THUMBNAIL_SIZE);

                            Bitmap bitmap = scaler.getScaled();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();
                            String strBase64 = Base64.encodeToString(data, 0);


                            JSONObject j = new JSONObject();
                            j.put("ImageString", strBase64);
                            j.put("UserContactsId", informationTable.getAddressId());
                            j.put("ShipOptions", "1");
                            j.put("EmailAddress", GlobalMethods.getDefaultsForPreferences("email", getActivity()));

                            jsonArray.put(j);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    ProgressDialog pr = new ProgressDialog(getActivity());
                    OrderAsyncTask orderAsyncTask = new OrderAsyncTask(getActivity(), pr);
                    orderAsyncTask.execute(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private boolean validation() {
        boolean valid = true;
        boolean credit = true;
        boolean phone = true;

        String message = "We would need a ";
        UserProfileTableDatabaseHandler handler = new UserProfileTableDatabaseHandler(getActivity());
        final UserProfileTable u = handler.getUserDatas(GlobalMethods.getDefaultsForPreferences("email", getActivity()), true);

        if (u == null) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Hey there!")
                    .setMessage("Please fill your data from the Profile tab.")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            ((MainActivity) getActivity()).showProfileFragment();
                        }
                    })
                    .create()
                    .show();
            return false;
        } else if (shipInformation.size() == 0) {
            new AlertDialog.Builder(getActivity())
            	.setTitle("Hey there!")
                    .setMessage("You should add items before shipping!")
                    .setCancelable(false)
                    .setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(getActivity(), AddressActivity.class);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();


            return false;

        }


        if (u.getLastFourCreditCard().equals("null")) {
            valid = false;
            message += "credit card, ";
            credit = false;
        }

        if (u.getPhoneNumber().equals("null")) {
            valid = false;
            message += "phone number, ";
            phone = false;
        }

        if (ShipBobApplication.currentAddress == null) {
            valid = false;
            message += "pick up location ";
        }


        message += " before shipping!";

        if (!allItemsHasOption()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Hey there!")
                    .setMessage("All items need to have a shipping option from 1 day to ground!")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();

            return false;
        }

        if (valid) {

            return true;
        }

        if (!credit || !phone) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Hey there!")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Add Information", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (u.getPhoneNumber() == null || u.getPhoneNumber() == "" || u.getPhoneNumber().isEmpty() || u.getPhoneNumber().equals("null")) {
                                moveToCompletePhoneNumberActivity(false);
                            } else if (u.getLastFourCreditCard() == null || u.getLastFourCreditCard().equals("null") || u.getLastFourCreditCard().isEmpty() || u.getLastFourCreditCard() == "") {
                                moveToCompleteCreditCardProfile(false);
                            }


                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();

            return false;

        }

        new AlertDialog.Builder(getActivity())
                .setTitle("Hey there!")
/*                .setIcon(android.R.drawable.ic_dialog_alert)
*/                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                })
                .create()
                .show();


        return false;

    }

    private void moveToCompletePhoneNumberActivity(boolean firstTimeLogin) {
        //setting the email in the shared preferences
        Intent intent = new Intent(getActivity(), CompleteProfile_PhoneNumber.class);
        intent.putExtra(MainActivity.EXTRA_TITLE, "PhoneNumber");
        intent.putExtra(MainActivity.EXTRA_RESOURCE_ID, R.drawable.icon);
        intent.putExtra(MainActivity.EXTRA_MODE, 0);
        if (firstTimeLogin) {
            intent.putExtra("FirstTimeLogin", "true");
        }
        getActivity().startActivity(intent);

        getActivity().overridePendingTransition(0, 0);
    }

    private void moveToCompleteCreditCardProfile(boolean firstTimeLogin) {

        Intent intent = new Intent(getActivity(), CompleteProfile_CreditCard.class);
        if (firstTimeLogin) {
            intent.putExtra("FirstTimeLogin", "true");
        }
        getActivity().startActivity(intent);
    }

    private String getAddress() {


        if (ShipBobApplication.currentAddress != null) {
        	
        	if(ShipBobApplication.currentAddress.getStreetAddress2()!=null){
     		   return  ShipBobApplication.currentAddress.getStreetAddress2()+","+ShipBobApplication.currentAddress.getAddress()+","+ ShipBobApplication.currentAddress.getCity();

        	}
        	else{
        		   return  ShipBobApplication.currentAddress.getAddress()+","+ShipBobApplication.currentAddress.getCity();
                         
        	}
         
        } else {
            return "TAP TO SET PICK UP LOCATION.";
        }

    }

    private boolean allItemsHasOption() {
        boolean result = true;

        for (ShippingInformationTable table : shipInformation) {
            Log.d("OPtion: " + table.getShipOption());
            if (table.getShipOption() == null) {
                result = false;
            }
        }

        return result;

    }
}
