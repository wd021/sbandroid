package com.shipbob.databasehandler;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import com.shipbob.helpers.Log;
import com.shipbob.models.ShippingInformationTable;
import com.shipbob.models.UserAddress;

public class ShippingInformationDatabaseHandler  extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION =4;

	// Database Name
	private static final String DATABASE_NAME = "ShipBobDb_ShipDB";

	// Contacts table name
	private static final String TABLE_CONTACTS = "ShippingInformationTable";

	// Contacts Table Columns names
	private static final String KEY_ShipInformationTableId = "ShipInformationTableId";
	
	private static final String KEY_ImageFileName = "ImageFileName";

	private static final String KEY_ShipOption = "ShipOption";

	private static final String KEY_DestinationAddress = "DestinationAddress";

	private static final String KEY_ContactName = "ContactName";

	private static final String KEY_InsertDate = "InsertDate";

    private static final String KEY_AddressId = "AddressId";


	public ShippingInformationDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		// String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
		// + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT)";

		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "("
				+ KEY_ShipInformationTableId + " INTEGER PRIMARY KEY,"
				+ KEY_ImageFileName + " TEXT,"
				+  KEY_ShipOption + " TEXT,"
				+ KEY_DestinationAddress + " TEXT,"
				+ KEY_ContactName + " TEXT,"
                + KEY_AddressId + " TEXT,"
				+ KEY_InsertDate + " TEXT)";

		db.execSQL(CREATE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		// Create tables again
		onCreate(db);
	}

	public long addShipment(ShippingInformationTable shipInformation, Context c) {
		deleteContact(shipInformation);
		SQLiteDatabase db = this.getWritableDatabase();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		java.util.Date utilDate = cal.getTime();

		ContentValues values = new ContentValues();
//		values.put(KEY_ShipInformationTableId, shipInformation.getId());
		values.put(KEY_ImageFileName, shipInformation.getImageFileName());
		values.put(KEY_ShipOption, shipInformation.getShipOption());
		values.put(KEY_DestinationAddress, shipInformation.getDestinationAddress());
		values.put(KEY_ContactName, shipInformation.getContactName());
		values.put(KEY_InsertDate, shipInformation.getInsertDate());
		values.put(KEY_AddressId, shipInformation.getAddressId());

		// Inserting Row
		long insertedId = db.insert(TABLE_CONTACTS, null, values);
		// db.close(); // Closing database connection
		// copyDataBase(c);
		return insertedId;

	}

    public boolean update(long id, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();

        return db.update(TABLE_CONTACTS, setContentValue(image), "ShipInformationTableId = ?", new String[] { id + "" }) > 0;
    }

    public boolean remove(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();

        return db.delete(TABLE_CONTACTS, "ShipInformationTableId = ?", new String[] { id + ""}) > 0;
    }

    private ContentValues setContentValue(String image) {
        ContentValues cv = new ContentValues();

        cv.put(KEY_ImageFileName, image);

        return cv;
    }


    public long addShipment(UserAddress userAddress, Context c) {
        SQLiteDatabase db = this.getWritableDatabase();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();

        ContentValues values = new ContentValues();
//		values.put(KEY_ShipInformationTableId, shipInformation.getId());
//        values.put(KEY_ImageFileName, shipInformation.getImageFileName());
//        values.put(KEY_ShipOption, shipInformation.getShipOption());
        values.put(KEY_DestinationAddress, constructDestinationAddress(userAddress));
        values.put(KEY_ContactName, userAddress.getName());
        values.put(KEY_AddressId, userAddress.getUserContactId());
//        values.put(KEY_InsertDate, userAddress.getInsertDate());

        // Inserting Row
        long insertedId = db.insert(TABLE_CONTACTS, null, values);
        // db.close(); // Closing database connection
        // copyDataBase(c);
        return insertedId;

    }

    private String constructDestinationAddress(UserAddress userContact) {
        String constructedAddress = userContact.getHouseNumber() + "," + userContact.getStreetAddress1() + "," +
                userContact.getCity() + "," + userContact.getState() + "," + userContact.getZipCode();

        return constructedAddress;
    }

	public long deleteContact(ShippingInformationTable shipInformation) {
		
		try{
			SQLiteDatabase db = this.getWritableDatabase();
		
			db.delete(TABLE_CONTACTS, KEY_ShipInformationTableId + "=" + shipInformation.getId(), null);
		return 1;
		}
		catch(Exception e){
			return -1;
		}
	}

	// Getting single contact
	public ShippingInformationTable getShippingInformationById(String shipInformationTableId) {

		ShippingInformationTable shipInformationTable = getShippingInformationFromDbById(shipInformationTableId);


		return shipInformationTable;
	}

    public ArrayList<ShippingInformationTable> getShippingInformation() {

        ArrayList<ShippingInformationTable> shipInformationTable = getShippingInformationFromDb();


        return shipInformationTable;
    }




	public ShippingInformationTable getShippingInformationFromDbById(String queryString) {
		ShippingInformationTable shipInformation = new ShippingInformationTable();
		Cursor cursor;

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			// Cursor cursor = db.rawQuery(selectQuery, null);
			
				cursor = db.rawQuery(
						"SELECT * FROM ShippingInformationTable WHERE ShipInformationTableId=?;",
						new String[] { queryString });
			
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					ShippingInformationTable tempcontact = new ShippingInformationTable();
					tempcontact.setShipInformationTableId(Integer.parseInt(cursor.getString(0)));
					tempcontact.setImageFileName(cursor.getString(1));
					tempcontact.setShipOption(cursor.getString(2));
					tempcontact.setDestinationAddress(cursor.getString(3));
					tempcontact.setContactName(cursor.getString(4));
					tempcontact.setInsertDate(cursor.getString(5));
                    tempcontact.setAddressId(cursor.getInt(6));
					shipInformation=tempcontact;
					// Adding contact to list
				} while (cursor.moveToNext());
			}

			// return contact list

			return shipInformation;
		}
		
	
		catch (Exception e) {

			Log.e(e.getLocalizedMessage());
			return null;
		}
	}

    public ArrayList<ShippingInformationTable> getShippingInformationFromDb() {
        ArrayList<ShippingInformationTable> items = new ArrayList<ShippingInformationTable>();

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor;
            // Cursor cursor = db.rawQuery(selectQuery, null);

            cursor = db.rawQuery(
                    "SELECT * FROM ShippingInformationTable;",
                    null);

            // looping through all rows and adding to list

            if (cursor.moveToFirst()) {
                do {
                    ShippingInformationTable tempcontact = new ShippingInformationTable();
                    tempcontact.setShipInformationTableId(Integer.parseInt(cursor.getString(0)));
                    tempcontact.setImageFileName(cursor.getString(1));
                    tempcontact.setShipOption(cursor.getString(2));
                    tempcontact.setDestinationAddress(cursor.getString(3));
                    tempcontact.setContactName(cursor.getString(4));
                    tempcontact.setAddressId(cursor.getInt(5));
                    tempcontact.setInsertDate(cursor.getString(6));


                    // Adding contact to list
                    items.add(tempcontact);
                } while (cursor.moveToNext());
            }

            // return contact list

            return items;
        } catch (Exception e) {

            Log.i(e.getMessage());
            return null;
        }
    }

	public int updateShipInformationOption(int shipInformationTableId, String shipOption) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ShipInformationTableId, shipInformationTableId);
		values.put(KEY_ShipOption, shipOption);
	
		// updating row
		int id = db.update(TABLE_CONTACTS, values, KEY_ShipInformationTableId + " = ?",
				new String[] { String.valueOf(shipInformationTableId) });

		return id;
	}

	public int updateShipInformationDestinationAddress(int shipInformationTableId, String destinationAddress, String contactName) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ShipInformationTableId, shipInformationTableId);
		values.put(KEY_DestinationAddress, destinationAddress);
		values.put(KEY_ContactName, contactName);

		// updating row
		int id = db.update(TABLE_CONTACTS, values, KEY_ShipInformationTableId + " = ?",
				new String[] { String.valueOf(shipInformationTableId) });

		return id;
	}
	


	
	public void copyDataBase(Context context) {
		Log.i("in copy data base at finally");
		try {

			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();
			if (sd.canWrite()) {

				File currentDB = context.getDatabasePath(DATABASE_NAME);

				Log.i("Can Write");

				// File currentDB=new File(Path);

				String backupDBPath = TABLE_CONTACTS;

				File backupDB = new File(sd, backupDBPath);
				if (currentDB.exists()) {
					Log.i("Writing into DB");
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
		} catch (Exception e) {
			Log.i("in copy of bata base 10 ");

		}
	}


    public int clearShipItems() {

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CONTACTS, null, null);
    }

    public void updateItem(int itemId, int optionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ShipOption, optionId);

        // updating row
        int id = db.update(TABLE_CONTACTS, values, KEY_ShipInformationTableId + " = ?",
                new String[] { String.valueOf(itemId) });
    }
}
