package com.shipbob.databasehandler;

import android.os.Parcel;
import android.os.Parcelable;

public class ReturnAddressClass implements Parcelable {

	public String StreetAddress1;
	public String StreetAddress2;
	public String HouseNumber;
	public String City;
	public String State;
	public String Country;
	public String ZipCode;
	public int UserId;
	
	   public String getStreetAddress1() {  
		   return StreetAddress1;  
		      }  
		      public void setStreetAddress1(String StreetAddress1) {  
		   this.StreetAddress1 = StreetAddress1;  
		      }  
		      public String getStreetAddress2() {  
		    	  	return StreetAddress2;  
		      }  
		      public void setStreetAddress2(String StreetAddress2) {  
		   this.StreetAddress2 = StreetAddress2;  
		      }  
		 
		      
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		 parcel.writeString(StreetAddress1);  
    	 parcel.writeString(StreetAddress2);  
	}
	
	public static final Parcelable.Creator<ReturnAddressClass> CREATOR = new Creator<ReturnAddressClass>() {  
		   public ReturnAddressClass createFromParcel(Parcel source) {  
			   ReturnAddressClass mBook = new ReturnAddressClass();  
		       mBook.StreetAddress1 = source.readString();  
		       mBook.StreetAddress2 = source.readString();  
		       return mBook;  
		   }

		@Override
		public ReturnAddressClass[] newArray(int arg0) {
			// TODO Auto-generated method stub
			
			return new ReturnAddressClass[arg0];
			}
		

		   
	   };
	   
	   
	   
}

