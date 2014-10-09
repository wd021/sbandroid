package com.shipbob.databasehandler;

import android.content.Context;
import com.shipbob.models.ShippingInformationTable;
import com.shipbob.models.UserAddress;
import com.shipbob.models.UserProfileTable;

import java.util.ArrayList;

public class GlobalDatabaseHandler {

	
	public static UserProfileTable GetUserProfile(Context c, String email)
	{
		
	UserProfileTableDatabaseHandler userDbHandler=new UserProfileTableDatabaseHandler(c);
       UserProfileTable existingUserProfile= userDbHandler.getContact(email);
       return existingUserProfile;
	}   
	
	public static long insertUserProfileInSqlLite(Context c, UserProfileTable userProfile){
    	
    	//make JsonRequest to getUserProfile
    	//and then insert the record. 
		UserProfileTableDatabaseHandler userTableDatabaseHandler = new UserProfileTableDatabaseHandler(c);

		return userTableDatabaseHandler.addContact(userProfile,c);
	
     }
	
	public static ShippingInformationTable getShippingInformationTableById(Context c, int shippingInformationTableId)
	{
		
		ShippingInformationDatabaseHandler shipDbHandler=new ShippingInformationDatabaseHandler(c);
		ShippingInformationTable shipInformationTable= shipDbHandler.getShippingInformationById(String.valueOf(shippingInformationTableId));
       return shipInformationTable;
	}

    public static ArrayList<ShippingInformationTable> getShippingInformationTable(Context c)
    {

        ShippingInformationDatabaseHandler shipDbHandler=new ShippingInformationDatabaseHandler(c);
        ArrayList<ShippingInformationTable> shipInformationTable= shipDbHandler.getShippingInformation();
        return shipInformationTable;
    }

    public static long insertShippingInformation(Context c, ShippingInformationTable shipInformation){
    	
    	//make JsonRequest to getUserProfile
    	//and then insert the record. 
		ShippingInformationDatabaseHandler shipDbHandler = new ShippingInformationDatabaseHandler(c);
		ShippingInformationTable shipInfo = new ShippingInformationTable();
		
		shipInfo.setShipInformationTableId(shipInfo.getId());
		shipInfo.setImageFileName(shipInfo.getImageFileName());
		shipInfo.setDestinationAddress(shipInfo.getDestinationAddress());
		shipInfo.setContactName(shipInfo.getContactName());
		shipInfo.setInsertDate(shipInfo.getInsertDate());
		shipInfo.setShipOption(shipInfo.getShipOption());
		return shipDbHandler.addShipment(shipInformation, c);
	
     }

    public static long updateShippingInformation(Context c, ShippingInformationTable shipInformation){

        //make JsonRequest to getUserProfile
        //and then insert the record.
        ShippingInformationDatabaseHandler shipDbHandler = new ShippingInformationDatabaseHandler(c);
        ShippingInformationTable shipInfo = new ShippingInformationTable();

        shipInfo.setShipInformationTableId(shipInfo.getId());
        shipInfo.setImageFileName(shipInfo.getImageFileName());
        shipInfo.setDestinationAddress(shipInfo.getDestinationAddress());
        shipInfo.setContactName(shipInfo.getContactName());
        shipInfo.setInsertDate(shipInfo.getInsertDate());
        shipInfo.setShipOption(shipInfo.getShipOption());
        return shipDbHandler.addShipment(shipInformation, c);

    }

    public static long insertShippingInformation(Context c, UserAddress userAddress){

        //make JsonRequest to getUserProfile
        //and then insert the record.
        ShippingInformationDatabaseHandler shipDbHandler = new ShippingInformationDatabaseHandler(c);
        ShippingInformationTable shipInfo = new ShippingInformationTable();

        shipInfo.setShipInformationTableId(shipInfo.getId());
        shipInfo.setImageFileName(shipInfo.getImageFileName());
        shipInfo.setDestinationAddress(shipInfo.getDestinationAddress());
        shipInfo.setContactName(shipInfo.getContactName());
        shipInfo.setInsertDate(shipInfo.getInsertDate());
        shipInfo.setShipOption(shipInfo.getShipOption());

        return shipDbHandler.addShipment(userAddress, c);

    }
	
	
	
}
