package com.shipbob.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.databasehandler.ShippingInformationDatabaseHandler;
import com.shipbob.models.ShippingInformationTable;
import com.shipbob.vendors.lazylist.ImageLoader;
import com.shipbob.models.Option;
import com.shipbob.vendors.bitmaphandling.BitmapScaler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by waldemar on 10.06.14.
 */
public class ItemListAdapter extends ArrayAdapter<ShippingInformationTable> {


    private final Context context;
    private final int layoutResourceId;
    ArrayList<ShippingInformationTable> data;

    ImageLoader imageLoader;

    ArrayList<Option> options;

    OptionsAdapter adapter;


    public ItemListAdapter(Context context, int layoutResourceId, ArrayList<ShippingInformationTable> data) {
        super(context, layoutResourceId, data);

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

        this.options = ShipBobApplication.options;

        imageLoader = new ImageLoader(context);


        Resources resources = context.getResources();

        adapter = new OptionsAdapter(context, R.layout.row_option_item, ShipBobApplication.options, resources);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ShipItemHolder shipItemHolder;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutResourceId, parent, false);

            shipItemHolder = new ShipItemHolder();

            shipItemHolder.name = (TextView) convertView.findViewById(R.id.nameTextView);
            shipItemHolder.address = (TextView) convertView.findViewById(R.id.addressTextView);
            shipItemHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            shipItemHolder.deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);
            shipItemHolder.optionsButton = (Spinner) convertView.findViewById(R.id.optionsButton);
            shipItemHolder.optionsButton.setAdapter(adapter);

            convertView.setTag(shipItemHolder);

        } else {
            shipItemHolder = (ShipItemHolder) convertView.getTag();
        }

        final ShippingInformationTable shipItem = (ShippingInformationTable) data.get(position);

        if (shipItem != null) {
            shipItemHolder.name.setText(shipItem.getContactName());
            shipItemHolder.address.setText(constructDestinationAddress(shipItem));
            showThumbnail(shipItem.getImageFileName(), shipItemHolder.imageView);
            shipItemHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(shipItem);
                }
            });

            Option o = getById(Integer.valueOf(shipItem.getShipOption() != null ? shipItem.getShipOption() : "0"));
//            shipItemHolder.optionsButton.setSelection(o != null ? options.indexOf(0) : 0);

            ArrayAdapter myAdap = (ArrayAdapter) shipItemHolder.optionsButton.getAdapter(); //cast to an ArrayAdapter

            int spinnerPosition = myAdap.getPosition(o);

            shipItemHolder.optionsButton.setSelection(spinnerPosition);

            shipItemHolder.optionsButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                    if (p > 0) {
                        Option option = (Option) shipItemHolder.optionsButton.getItemAtPosition(p);
                        data.get(position).setShipOption("" + option.getId());
                        ShippingInformationDatabaseHandler databaseHandler = new ShippingInformationDatabaseHandler(context);

                        databaseHandler.updateItem(shipItem.getId(), option.getId());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        return convertView;
    }

    private void removeItem(final ShippingInformationTable shipItem) {

        new AlertDialog.Builder(context)
                .setTitle("Warning")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure want to delete it?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ShippingInformationDatabaseHandler shipDbHandler = new ShippingInformationDatabaseHandler(context);
                        if (shipDbHandler.remove(shipItem.getId())) {
                            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                            data.remove(shipItem);
                            reload();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();


    }

    private void reload() {
        this.notifyDataSetChanged();
    }

    static class ShipItemHolder {
        TextView name;
        TextView address;
        ImageView imageView;
        ImageButton deleteButton;
        Spinner optionsButton;
    }

    private String constructDestinationAddress(ShippingInformationTable shipItem) {
        String constructedAddress = shipItem.getDestinationAddress();
        constructedAddress = constructedAddress.replaceAll("null,", " ");

        return constructedAddress;
    }

    private void showThumbnail(String file, ImageView imageView) {
        try {
            File fileName = new File(file);
            final int THUMBNAIL_SIZE = 512;
            BitmapScaler scaler = new BitmapScaler(fileName, THUMBNAIL_SIZE);
            imageView.setImageBitmap(scaler.getScaled());

        } catch (Exception ex) {

        }

    }

    private Option getById(int id) {
        for (Option option : options) {
            if (option.getId() == id)
                return option;
        }

        return null;
    }

}
