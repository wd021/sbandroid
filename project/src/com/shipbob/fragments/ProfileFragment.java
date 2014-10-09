package com.shipbob.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.shipbob.GlobalMethods;
import com.facebook.ShipBob.R;
import com.facebook.ShipBob.activities.MainActivity;
import com.facebook.ShipBob.activities.ReturnAddress;
import com.facebook.ShipBob.activities.UpdateCreditCardActivity;
import com.facebook.ShipBob.activities.UpdatePhoneNumberActivity;
import com.facebook.ShipBob.activities.InsertCouponCodeActivity;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.databasehandler.ReturnAddressClass;
import com.shipbob.models.UserProfileTable;

/**
 * Created by waldemar on 03.06.14.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";

    private ProfilePictureView profilePictureView;
    private TextView userNameView;
    private TextView emailAddress;
    private TextView creditCard;
    private TextView address;
    private TextView phoneNumber;
    private TextView coupon;
    private UserProfileTable userProfile;
    private String userEmail;
    private static int FirstActivityRequestCode = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.fragment_profile, container, false);

        ImageButton nameButton = (ImageButton) view.findViewById(R.id.nameImageButton);
        nameButton.setOnClickListener(this);

        ImageButton emailButton = (ImageButton) view.findViewById(R.id.emailImageButton);
        emailButton.setOnClickListener(this);

        ImageButton phoneButton = (ImageButton) view.findViewById(R.id.phoneImageButton);
        phoneButton.setOnClickListener(this);

        ImageButton cardButton = (ImageButton) view.findViewById(R.id.cardImageButton);
        cardButton.setOnClickListener(this);

        ImageButton couponButton = (ImageButton) view.findViewById(R.id.couponImageButton);
        couponButton.setOnClickListener(this);
        
/*        ImageButton addressButton = (ImageButton) view.findViewById(R.id.addressImageButton);
        addressButton.setOnClickListener(this);*/


        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        userNameView = (TextView) view.findViewById(R.id.userNameTextView);

        emailAddress = (TextView) view.findViewById(R.id.emailTextView);
        phoneNumber = (TextView) view.findViewById(R.id.phoneTextView);
        userEmail = GlobalMethods.getDefaultsForPreferences("email", getActivity());
        creditCard = (TextView) view.findViewById(R.id.cardTextView);
        coupon = (TextView) view.findViewById(R.id.couponTextView);
/*        address = (TextView) view.findViewById(R.id.addressTextView);
*/

        UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(getActivity(), userEmail);
        if (existingUserProfile != null) {
        	
        	userProfile = existingUserProfile;
            setDefaultValues();

        }
        //Setting Values


        return view;
    }

    private void setDefaultValues() {
        // Phone
        String profilephoneNumber = userProfile.getPhoneNumber();
        if(profilephoneNumber!=null){
        	if (!profilephoneNumber.equals("null")) {
                phoneNumber.setText(profilephoneNumber);
            }
        }
        
        // Credit card
        String creditCardNumber = userProfile.getLastFourCreditCard();
        if(creditCardNumber!=null){
        if (!creditCardNumber.equals("null")) {
            creditCard.setText("**** " + creditCardNumber);
        }
        }

        // Coupon
        String couponCode = userProfile.getCouponCode();
        if(couponCode!=null){
        if (!couponCode.equals("null")) {
        	coupon.setText("20% off next purchase!");
        }
        }
        
        // Return address
     /*   String returnAddress = constructReturnAddress(userProfile);
        if (returnAddress == null || returnAddress == "" || returnAddress.isEmpty() || returnAddress.equals("null")) {
            address.setText(returnAddress);
        }*/

        // Email
        String email = userProfile.getEmail();
        emailAddress.setText(email);

/*        address.setText(constructReturnAddress(userProfile));
*/
        // UserName
        userNameView.setText(userProfile.getFirstName().toString() + " " + userProfile.getLastName().toString());


        Session session = Session.getActiveSession();
        if (session == null) {
            // try to restore from cache
            session = Session.openActiveSessionFromCache(getActivity());
        }

        if (session != null && session.isOpened()) {
            makeMeRequest(session);
        } else {
            // TO DO:: Show ERROR Page
        }


    }

    private String constructReturnAddress(UserProfileTable uProfile) {
        String houseNumber = uProfile.getHouseNumber();
        String streetAddress = uProfile.getStreetAddress1();
        String city = uProfile.getCity();
        String country = uProfile.getCountry();
        String zipCode = uProfile.getZipCode();

        String[] arr = new String[5];
        arr[0] = (houseNumber != null && !houseNumber.equals("null") ? houseNumber : null);
        arr[1] = (streetAddress != null && !streetAddress.equals("null") ? streetAddress : null);
        arr[2] = (city != null && !city.equals("null") ? city : null);
        arr[3] = (country != null && !country.equals("null") ? country : null);
        arr[4] = (zipCode != null && !zipCode.equals("null") ? zipCode : null);

        String output = "";

        for (String str : arr)
            if (str != null && !str.equals("null"))
            output = output + str + ", ";

        output = output.trim().replaceAll(" ,$", "");

        return output;
    }

    private void makeMeRequest(final Session session) {

        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {

                        profilePictureView.setProfileId(user.getId());

                    }
                }
                if (response.getError() != null) {
                    // Handle errors, will do so later.
                }
            }
        });
        request.executeAsync();
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.nameImageButton:

                break;

            case R.id.emailImageButton:

                break;

            case R.id.cardImageButton:
                intent = new Intent(getActivity(), UpdateCreditCardActivity.class);
                intent.putExtra(EXTRA_TITLE, "PaymentActivity");
                intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
                intent.putExtra(EXTRA_MODE, 0);
                intent.putExtra("page", MainActivity.PROFILE_PAGE);
                startActivityForResult(intent, FirstActivityRequestCode);

                break;

         /*   case R.id.addressImageButton:
                intent = new Intent(getActivity(), ReturnAddress.class);
                intent.putExtra(EXTRA_TITLE, "Return Address");
                intent.putExtra("page", MainActivity.PROFILE_PAGE);
                intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
                intent.putExtra(EXTRA_MODE, 0);
                ReturnAddressClass currentAddressClass = new ReturnAddressClass();
                startActivityForResult(intent, FirstActivityRequestCode);

                break;*/

            case R.id.phoneImageButton:
                intent = new Intent(getActivity(), UpdatePhoneNumberActivity.class);
                intent.putExtra(EXTRA_TITLE, "Phone Number");
                intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
                intent.putExtra(EXTRA_MODE, 0);
                intent.putExtra("page", MainActivity.PROFILE_PAGE);
                startActivityForResult(intent, FirstActivityRequestCode);

                break;
            
            case R.id.couponImageButton:
            	
                String couponCode = userProfile.getCouponCode();
                
                if (couponCode == null)
                {
                	intent = new Intent(getActivity(), InsertCouponCodeActivity.class);
                	intent.putExtra(EXTRA_TITLE, "Coupon Code");
                	intent.putExtra(EXTRA_RESOURCE_ID, R.drawable.icon);
              	  	intent.putExtra(EXTRA_MODE, 0);
              	  	intent.putExtra("page", MainActivity.PROFILE_PAGE);
                	startActivityForResult(intent, FirstActivityRequestCode);
                }
                
                break;
        }
    }
}
