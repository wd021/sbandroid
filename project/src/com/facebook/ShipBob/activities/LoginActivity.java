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

package com.facebook.ShipBob.activities;

import java.security.MessageDigest;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.widget.LoginButton;
import com.shipbob.GlobalMethods;
import com.facebook.ShipBob.R;
import com.shipbob.ShipBobApplication;
import com.shipbob.asynctasks.*;
import com.shipbob.helpers.Log;
import com.shipbob.helpers.PreferencesHelpers;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.shipbob.databasehandler.GlobalDatabaseHandler;
import com.shipbob.models.UserProfileTable;

public class LoginActivity extends Activity {

    private boolean isResumed = false;

    private boolean isKeyboardShow = false;


    private Button loginBtn;

    // ------------ Flipp buttons operation -------------------
    private Button loginInvBtn;
    private TextView registrationInvLnk;
    Button registrFlipp;
    Button loginFlipp;

    // ------------ Flipp buttons operation -------------------


    // ------------ Registration  start -------------------
    PreferencesHelpers preferences;

    Button registrButton;
    EditText emailEditText;
    EditText passwordEditText;
    EditText repeatPasswordEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;

    String email;
    String password;
    String repeatPassword;
    String firstName;
    String lastName;

    EditText emailLogin;
    EditText passwordLogin;
    // ------------ Registration  end   -------------------


    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";
    private UiLifecycleHelper uiHelper;
    private GraphUser facebookGraphUser;

    private int position = 1;

    LoginButton logInButton;
    TextView or;

    //RefreshHandler refreshHandler = new RefreshHandler();
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        String lLogin = GlobalMethods.getDefaultsForPreferences("login", this);
        String lEmail = GlobalMethods.getDefaultsForPreferences("email", this);
        String lPassword = GlobalMethods.getDefaultsForPreferences("pass", this);

        if (lLogin != null && lEmail != null && lPassword != null)
            if (lLogin.equals("true") && !lEmail.equals("") && !lPassword.equals("")) {

                try {
                    JSONObject j = new JSONObject();
                    j.put("EmailAddress", lEmail);
                    j.put("Password", lPassword);

                    new LoginUserAsyncTask(this, new ProgressDialog(this), lEmail, lPassword).execute(j);

                } catch (Exception e) {

                    Log.e(e.getLocalizedMessage());


                }

//                finish();
            }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.facebook.ShipBob", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d(key);
            }
        } catch (Exception e) {


            Log.e(e.getLocalizedMessage());
        }
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        Session session = Session.getActiveSession();
        if (session == null) {
            // try to restore from cache
            session = Session
                    .openActiveSessionFromCache(getApplicationContext());
        }

        if (session != null && session.isOpened()) {
            invokeMainActivity();

        } else {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_flipp_start_page);

            final ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

            loginInvBtn = (Button) findViewById(R.id.loginbutton_invitation);
            registrationInvLnk = (TextView) findViewById(R.id.registration_invitation);

            loginInvBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewFlipper.getApplicationWindowToken(), 0);

                    viewFlipper.showNext();

                }
            });

            registrationInvLnk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewFlipper.getApplicationWindowToken(), 0);

                    viewFlipper.showPrevious();

                }
            });


            // ------------------ Registration -----------------
            registrFlipp = (Button) findViewById(R.id.registrFlipp);

            emailEditText = (EditText) findViewById(R.id.registrEmail);
            passwordEditText = (EditText) findViewById(R.id.registrPassword);
            repeatPasswordEditText = (EditText) findViewById(R.id.regConfirmPassword);
            firstNameEditText = (EditText) findViewById(R.id.regFirstName);
            lastNameEditText = (EditText) findViewById(R.id.regLastName);

            registrButton = (Button) findViewById(R.id.registration_button);
            registrButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!validateContact())
                        return;

                    firstName = firstNameEditText.getText().toString();
                    lastName = lastNameEditText.getText().toString();
                    password = passwordEditText.getText().toString();
                    repeatPassword = repeatPasswordEditText.getText().toString();
                    email = emailEditText.getText().toString();

                    if (password.equals(repeatPassword)) {

                        try {
                            JSONObject j = new JSONObject();
                            j.put("Email", email);
                            j.put("FirstName", firstName);
                            j.put("LastName", lastName);
                            j.put("Password", password);

                            ProgressDialog pd = new ProgressDialog(LoginActivity.this);

                            new RegisterAsyncTask(LoginActivity.this, pd, password).execute(j);

                        } catch (Exception e) {

                            Log.e(e.getLocalizedMessage());
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            registrFlipp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewFlipper.getApplicationWindowToken(), 0);

                    viewFlipper.showNext();

                }
            });


            // ------------------ Registration end -----------------

            // ------------------ Loginization -----------------

            logInButton = (LoginButton) findViewById(R.id.login_button);
            or = (TextView) findViewById(R.id.or);


            loginBtn = (Button) findViewById(R.id.login_button_process);
            emailLogin = (EditText) findViewById(R.id.regEmail);


            passwordLogin = (EditText) findViewById(R.id.regPassword);


            final View activityRootView = findViewById(R.id.loginLayout);
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    //r will be populated with the coordinates of your view that area still visible.
                    activityRootView.getWindowVisibleDisplayFrame(r);

                    int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                    if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                        logInButton.setVisibility(View.GONE);
                        or.setVisibility(View.GONE);
                    } else {
                        logInButton.setVisibility(View.VISIBLE);
                        or.setVisibility(View.VISIBLE);
                    }
                }
            });

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailLogin.getText().toString();
                    String password = passwordLogin.getText().toString();

                    try {
                        JSONObject j = new JSONObject();
                        j.put("EmailAddress", email);
                        j.put("Password", password);

                        ProgressDialog pd = new ProgressDialog(LoginActivity.this);

                        new LoginUserAsyncTask(LoginActivity.this, pd, email, password).execute(j);

                    } catch (Exception e) {

                        Log.e(e.getLocalizedMessage());
                    }
                }
            });

            loginFlipp = (Button) findViewById(R.id.loginFlipp);
            loginFlipp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewFlipper.getApplicationWindowToken(), 0);

                    viewFlipper.showPrevious();

                }
            });


        }

    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;

    }
    
    @Override
    public void onStop() {  
    	  super.onStop();
     
    }
    

    @Override
    public void onPause() {
       super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
         super.onDestroy();
        uiHelper.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

    }

    public boolean validateContact() {
        boolean validateContactResult = true;
        String errorNames = null;

        if (firstNameEditText.getText().toString().length() == 0) {
            firstNameEditText.setError("First Name is required!");
            validateContactResult = false;
            errorNames = "First Name";
        }

        if (lastNameEditText.getText().toString().length() == 0) {
            lastNameEditText.setError("Last Name is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", Street Name");
            } else errorNames = "Last Name";
        } else {
            lastNameEditText.setError(null);
        }


        if (emailEditText.getText().toString().length() == 0) {
            emailEditText.setError("Email address is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", email");
            } else errorNames = "Email";
        } else {
            emailEditText.setError(null);
        }


        if (passwordEditText.getText().toString().length() == 0) {
            passwordEditText.setError("Password is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", password");
            } else errorNames = "Password";
        } else {
            passwordEditText.setError(null);
        }

        if (repeatPasswordEditText.getText().toString().length() == 0) {
            repeatPasswordEditText.setError("Repeat password is required!");
            validateContactResult = false;
            if (errorNames != null) {
                errorNames = errorNames.concat(", repeat password");
            } else errorNames = "Repeat Password";
        } else {
            repeatPasswordEditText.setError(null);
        }


        if (!validateContactResult) {
            Toast.makeText(this, errorNames + " is required.",
                    Toast.LENGTH_SHORT).show();
        }
        return validateContactResult;


    }

    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {
        if (isResumed) {
            if (state.isOpened()) {
                makeMeRequest(session);

                // showFragment(SELECTION, false);
            } else if (state.isClosed()) {
                // If the session state is closed:
                // Show the login fragment
                // showFragment(SPLASH, false);
                setContentView(R.layout.activity_flipp_start_page);


            }
        }
    }

    // Retrieve Facebook Details
    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                logInButton.setVisibility(View.INVISIBLE);
                                facebookGraphUser = user;
                                checkIfUserExists(user);
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                        }
                    }
                }
        );
        request.executeAsync();
    }


    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //imageView.setImageResource(imgid[0]);

            // slideshow.this.updateUI();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }


    // Make JsonCall with Information
    private class JSONFeedTask extends AsyncTask<String, Void, String> {
        ProgressDialog progDailog = new ProgressDialog(LoginActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog.setMessage("Retrieving your Location...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                progDailog.dismiss();
                if (result.equals("true")) {
                    // Make sure User also Exists in SQLLite
                    String email = getEmailFromPreferencesOrFb();
                    if (email != null) {
                        UserProfileTable existingUserProfile = GlobalDatabaseHandler.GetUserProfile(getApplicationContext(), email);
                        if (existingUserProfile != null && (existingUserProfile != null && existingUserProfile.getEmail() != null && !existingUserProfile.getEmail().isEmpty())) {
                            invokeMainActivity();

                        } else {
                            new GetUserProfileAsyncTask(LoginActivity.this, progDailog)
                                    .execute(
                                            ShipBobApplication.GET_USER_PROFILE + email);
                        }
                    }

                } else {
                    insertNewUser();
                }

            } catch (Exception e) {
                Log.w(e.getLocalizedMessage());
                Log.e(e.getLocalizedMessage());

            }
        }
    }

    public String readJSONFeed(String URL) {
        return GlobalMethods.MakeandReceiveHTTPResponse(URL);
    }

    public void checkIfUserExists(GraphUser user) {
        if (user != null) {
            String email = user.getProperty("email").toString();
            setEmailSharedPrferences(user);
            new JSONFeedTask().execute(ShipBobApplication.IS_USER_EXIST + email);
        }
    }

    public String makeInsertNewUserUrlRequest(String URL, JSONObject jsonObject) {
        return GlobalMethods.makePostRequestWithJsonObject(URL, jsonObject);
    }

    private void insertNewUser() {

        String firstName = facebookGraphUser.getFirstName();
        String lastName = facebookGraphUser.getLastName();
        String email = facebookGraphUser.getProperty("email").toString();
        String facebookUid = facebookGraphUser.getId();
        try {
            JSONObject j = new JSONObject();
            j.put("Email", email);
            j.put("FirstName", firstName);
            j.put("LastName", lastName);
            j.put("FacebookUid", facebookUid);

            ProgressDialog pd = new ProgressDialog(LoginActivity.this);

            new InsertNewUserJsonTask(LoginActivity.this, pd)
                    .execute(ShipBobApplication.REGISTER_USER, j);

        } catch (Exception e) {

            Log.e(e.getLocalizedMessage());
        }
    }

    private void invokeMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        onDestroy();
        startActivity(intent);
        finish();
    }


    private void setEmailSharedPrferences(GraphUser user) {

        if (user != null) {
            GlobalMethods.setDefaultsForPreferences("email", user.getProperty("email").toString(), LoginActivity.this);
        	Crashlytics.log("facebook accessed");

            return;
        }
        if (facebookGraphUser != null) {
        	Crashlytics.log("facebook accessed");

            GlobalMethods.setDefaultsForPreferences("email", facebookGraphUser.getProperty("email").toString(), LoginActivity.this);
        }
    }

    private String getEmailFromPreferencesOrFb() {
        String email = GlobalMethods.getDefaultsForPreferences("email", getApplicationContext());
        if (email != null) return email;

        if (facebookGraphUser != null) return facebookGraphUser.getProperty("email").toString();
        else {
            return null;

        }

    }


}
