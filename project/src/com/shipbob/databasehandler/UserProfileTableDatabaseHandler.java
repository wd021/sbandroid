package com.shipbob.databasehandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.crashlytics.android.Crashlytics;
import com.shipbob.helpers.Log;
import com.shipbob.models.UserProfileTable;

public class UserProfileTableDatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 32;

    // Database Name
    private static final String DATABASE_NAME = "ShipBobDb_ProfileDb";

    // Contacts table name
    private static final String TABLE_CONTACTS = "UserProfileTable";

    // Contacts Table Columns names
    
    private static final String KEY_PrimaryKeyId = "UserKeyId";

    private static final String KEY_UserId = "UserId";
    private static final String KEY_FirstName = "FirstName";

    private static final String KEY_LastName = "LastName";

    private static final String KEY_Email = "Email";

    private static final String KEY_CouponCode = "CouponCode";

    private static final String KEY_PhoneNumber = "PhoneNumber";

    private static final String KEY_LastFour = "LastFourCreditCard";

    private static final String KEY_address = "Address";

    private static final String KEY_city = "City";

    private static final String KEY_state = "State";

    private static final String KEY_zip = "ZipCode";
    
    private static final String KEY_StreetAddress2 = "StreetAddress2";


    public UserProfileTableDatabaseHandler(Context context) {


        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
        // + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT)";

    	try{
    	    String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "("
    	    		+ KEY_PrimaryKeyId + " INTEGER PRIMARY KEY autoincrement,"
    	    		+ KEY_UserId + " INTEGER,"
                    + KEY_Email + " TEXT,"
                    + KEY_FirstName + " TEXT,"
                    + KEY_LastName + " TEXT,"
                    + KEY_LastFour + " TEXT,"
                    + KEY_address + " TEXT,"
                    + KEY_city + " TEXT,"
                    + KEY_state + " TEXT, "
                    + KEY_zip + " TEXT, "
                    + KEY_PhoneNumber + " TEXT,"
                    + KEY_CouponCode + " TEXT,"
                    + KEY_StreetAddress2 + " TEXT)"
                    ;

            db.execSQL(CREATE_CONTACTS_TABLE);
    	}
    	
    	catch(Exception e){
    		  Crashlytics.logException(e);

    	}
    

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // Create tables again
        onCreate(db);
    }

    public long addContact(UserProfileTable contact, Context c) {
        deleteContact(contact);
        SQLiteDatabase db = this.getWritableDatabase();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.util.Date utilDate = cal.getTime();

        ContentValues values = new ContentValues();
        values.put(KEY_UserId, contact.getUserId());
        values.put(KEY_Email, contact.getEmail());
        values.put(KEY_FirstName, contact.getFirstName());
        values.put(KEY_LastName, contact.getLastName());
        values.put(KEY_LastFour, contact.getLastFourCreditCard());
        values.put(KEY_PhoneNumber, contact.getPhoneNumber());
        values.put(KEY_CouponCode, contact.getCouponCode());
        values.put(KEY_address, contact.getStreetAddress1());
        values.put(KEY_zip, contact.getZipCode());
        values.put(KEY_state, contact.getState());
        values.put(KEY_city, contact.getCity());
        values.put(KEY_StreetAddress2, contact.getStreetAddress1());

        // Inserting Row
        long insertedId = db.insert(TABLE_CONTACTS, null, values);
        // db.close(); // Closing database connection
        // copyDataBase(c);
        return insertedId;

    }

    public boolean updateAddress(UserProfileTable contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.update(TABLE_CONTACTS, setContentValue(contact), "Email = ?", new String[]{contact.getEmail() + ""}) > 0;
    }

    public UserProfileTable getAddress(String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "SELECT " + KEY_address + ", " + KEY_state + ", " + KEY_city + ", " + KEY_zip + ","+ KEY_StreetAddress2+" FROM " + TABLE_CONTACTS +
                " WHERE Email = '" + email + "'";

        Cursor mCursor = db.rawQuery(sql, null);
        mCursor.moveToFirst();

        UserProfileTable contact = cursorToModel(mCursor);
        mCursor.close();

        return contact;
    }

    private ContentValues setContentValue(UserProfileTable contact) {
        ContentValues cv = new ContentValues();

        cv.put(KEY_address, contact.getStreetAddress1());
        cv.put(KEY_city, contact.getCity());
        cv.put(KEY_state, contact.getState());
        cv.put(KEY_zip, contact.getZipCode());
        cv.put(KEY_StreetAddress2, contact.getStreetAddress2());

        return cv;
    }

    public long deleteContact(UserProfileTable contact) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            db.delete(TABLE_CONTACTS, KEY_UserId + "=" + contact.getUserId(), null);
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

    // Getting single contact
    public UserProfileTable getContact(String email) {

        UserProfileTable userProfile = getContact(email, true);

/*		List<UserProfileTable> finalList = new ArrayList<UserProfileTable>();
        for (UserProfileTable contact : qrCodeUsers) {

			finalList.add(contact);
		}

		for (UserProfileTable contact : emailUsers) {

			finalList.add(contact);
		}*/

        return userProfile;
    }


    public UserProfileTable getContact(String queryString, Boolean checkEmail) {
        UserProfileTable contact = new UserProfileTable();

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor;
            // Cursor cursor = db.rawQuery(selectQuery, null);
            if (!checkEmail)
                cursor = db.rawQuery(
                        "SELECT * FROM UserProfileTable WHERE UserId=?;",
                        new String[]{queryString});
            else
                cursor = db.rawQuery("SELECT * FROM UserProfileTable WHERE Email=?;",
                        new String[]{queryString});
            // looping through all rows and adding to list
            if (cursor == null) return null;
            if (cursor.moveToFirst()) {
                do {
                    UserProfileTable tempcontact = new UserProfileTable();
                    tempcontact.setUserId(Integer.parseInt(cursor.getString(1)));
                    tempcontact.setEmail(cursor.getString(2));
                    tempcontact.setFirstName(cursor.getString(3));
                    tempcontact.setLastName(cursor.getString(4));
                    tempcontact.setLastFourCreditCard(cursor.getString(5));
                    tempcontact.setPhoneNumber(cursor.getString(10));
                    tempcontact.setCouponCode(cursor.getString(11));
                    tempcontact.setStreetAddress1(cursor.getString(7));
                    tempcontact.setCity(cursor.getString(8));
                    tempcontact.setZipCode(cursor.getString(9));
                    contact = tempcontact;
                    // Adding contact to list
                } while (cursor.moveToNext());
            }

            // return contact list

            return contact;
        } catch (Exception e) {

            Log.e(e.getLocalizedMessage());

            e.printStackTrace();
            return null;
        }
    }

    public UserProfileTable getUserDatas(String queryString, Boolean checkEmail) {
        UserProfileTable contact = new UserProfileTable();

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor;
            // Cursor cursor = db.rawQuery(selectQuery, null);
            if (!checkEmail)
                cursor = db.rawQuery(
                        "SELECT * FROM UserProfileTable WHERE UserId=?;",
                        new String[]{queryString});
            else
                cursor = db.rawQuery("SELECT * FROM UserProfileTable WHERE Email=?;",
                        new String[]{queryString});
            // looping through all rows and adding to list
            if (cursor == null) return null;
            if (cursor.moveToFirst()) {
                do {
                    UserProfileTable tempcontact = new UserProfileTable();
                    tempcontact.setUserId(Integer.parseInt(cursor.getString(1)));
                    tempcontact.setEmail(cursor.getString(2));
                    tempcontact.setFirstName(cursor.getString(3));
                    tempcontact.setLastName(cursor.getString(4));
                    tempcontact.setLastFourCreditCard(cursor.getString(5));
                    tempcontact.setPhoneNumber(cursor.getString(10));
                    tempcontact.setCouponCode(cursor.getString(11));
                    tempcontact.setStreetAddress1(cursor.getString(6));
                    tempcontact.setCity(cursor.getString(7));
                    tempcontact.setZipCode(cursor.getString(9));

                    tempcontact.setLastFourCreditCard(cursor.getString(4));
                    contact = tempcontact;
                    // Adding contact to list
                } while (cursor.moveToNext());
            }

            // return contact list

            return contact;
        } catch (Exception e) {

            Log.e(e.getLocalizedMessage());

            e.printStackTrace();
            return null;
        }
    }

    public int updateContact(String email, String firstName, String lastName,
                             int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UserId, userId);
        values.put(KEY_Email, email);
        values.put(KEY_FirstName, firstName);
        values.put(KEY_LastName, lastName);

        // updating row
        int id = db.update(TABLE_CONTACTS, values, KEY_UserId + " = ?",
                new String[]{String.valueOf(userId)});

        return id;
    }

    public int updateUserProfilePhoneNumber(String email, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Email, email);
        values.put(KEY_PhoneNumber, phoneNumber);
        // updating row
        int id = db.update(TABLE_CONTACTS, values, KEY_Email + " = ?",
                new String[]{String.valueOf(email)});

        return id;
    }
    
    public int addUserProfileCouponCode(String email, String couponCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(KEY_Email, email);
        values.put(KEY_CouponCode, couponCode);
        // updating row
        int id = db.update(TABLE_CONTACTS, values, KEY_Email + " = ?",
                           new String[]{String.valueOf(email)});
        
        return id;
    }
    
    public int updateUserProfileLastFourCreditCard(String email, String lastFourCreditCard) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Email, email);
        values.put(KEY_LastFour, lastFourCreditCard);
        // updating row
        int id = db.update(TABLE_CONTACTS, values, KEY_Email + " = ?",
                new String[]{String.valueOf(email)});

        return id;
    }

    public int updateUserLastFourCreditCard(String email, String lastFourCreditCard) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Email, email);
        values.put(KEY_LastFour, lastFourCreditCard);
        // updating row
        int id = db.update(TABLE_CONTACTS, values, KEY_Email + " = ?",
                new String[]{String.valueOf(email)});

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

    private UserProfileTable cursorToModel(Cursor cursor) {
        UserProfileTable user = new UserProfileTable();

        if (!cursor.isAfterLast()) {
            user.setStreetAddress1(cursor.getString(0));
            user.setState(cursor.getString(1));
            user.setCity(cursor.getString(2));
            user.setZipCode(cursor.getString(3));
            user.setStreetAddress2(cursor.getString(4));

        }

        return user;
    }


}
