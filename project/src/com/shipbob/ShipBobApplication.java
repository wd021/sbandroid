/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shipbob;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ArrayAdapter;

import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.shipbob.models.Option;
import com.shipbob.models.ReturnAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Use a custom Application class to pass state data between Activities.
 */
public class ShipBobApplication extends Application {

    public static final String SERVER_URL = "http://shipbobapi.azurewebsites.net";

    public static final String LOGIN_URL = SERVER_URL + "/api/Profile/ValidateUser";

    public static final String REGISTER_USER = SERVER_URL + "/api/Profile/InsertNewUser";

    public static final String GET_USER_PROFILE = SERVER_URL + "/api/Profile/GetUserProfile/?email=";

    public static final String IS_USER_EXIST = SERVER_URL + "/api/Profile/UserExists/?identifier=";

    public static final String GET_USER_CONTACTS = SERVER_URL + "/api/UserContacts/GetUserContacts?email=";

    public static final String UPDATE_PHONE_NUMBER = SERVER_URL + "/api/Profile/UpdateUserPhoneNumber";

    public static final String MAKE_ORDER = SERVER_URL + "/api/Shipping/SubmitShipments/";

    public static final String ALL_ORDERS = SERVER_URL + "/api/Orders/GetOrdersForUser/?emailAddress=";

    public static final String SHIP_OPTIONS = SERVER_URL + "/api/Shipping/GetShippingOptions";

    public static final String IMAGE_HOST = " https://snailmailblob.blob.core.windows.net/";

    public static final String UPDATE_ADDRESS = SERVER_URL + "/api/PickUpLocation/SetPickUpLocation";

    public static final String INSERT_CONTACT = SERVER_URL + "/api/UserContacts/InsertUserContact";

    public static final String INSERT_CREDIT_CARD = SERVER_URL + "/api/Profile/InsertCreditCard";

    public static final String INSERT_PROMO_CODE = SERVER_URL + "/api/PromoCode/InsertPromoCode";

    public static ArrayList<Option> options = new ArrayList<Option>();

    public static ReturnAddress currentAddress;

    private List<GraphUser> selectedUsers;
    private GraphPlace selectedPlace;

    public List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<GraphUser> users) {
        selectedUsers = users;
    }

    public GraphPlace getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(GraphPlace place) {
        this.selectedPlace = place;
    }

    public static boolean isOnline(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
