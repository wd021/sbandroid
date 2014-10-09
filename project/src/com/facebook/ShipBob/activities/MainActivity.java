package com.facebook.ShipBob.activities;

import android.content.Intent;
import android.location.*;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.shipbob.GlobalMethods;
import com.facebook.ShipBob.R;
import com.shipbob.fragments.MainFragment;
import com.shipbob.fragments.OrdersFragment;
import com.shipbob.fragments.ProfileFragment;
import com.actionbarsherlock.app.SherlockActivity;

/**
 * Created by waldemar on 03.06.14.
 */
public class MainActivity extends SherlockFragmentActivity
        implements ISideNavigationCallback {

    public static final int MAIN_PAGE = 0;
    public static final int PROFILE_PAGE = 1;
    public static final int ORDER_PAGE = 2;

    public static final String EXTRA_TITLE = "com.devspark.sidenavigation.sample.extra.MTGOBJECT";
    public static final String EXTRA_RESOURCE_ID = "com.devspark.sidenavigation.sample.extra.RESOURCE_ID";
    public static final String EXTRA_MODE = "com.devspark.sidenavigation.sample.extra.MODE";
    public int currentItemId = R.id.home;

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static final int MENU_ADD_ITEM = 0;

    private SideNavigationView sideNavigationView;

    Fragment mapFragment;
    Fragment profileFragment;
    Fragment ordersFragment;

    Menu menu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sideNavigationView = (SideNavigationView) findViewById(R.id.side_navigation_view);
        sideNavigationView.setMenuItems(R.menu.side_navigation_menu);
        sideNavigationView.setMenuClickCallback(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        profileFragment = new ProfileFragment();
        mapFragment = new MainFragment();
        ordersFragment = new OrdersFragment();

        switch (getIntent().getIntExtra("page", 0)) {
            case 0:
                changeFragment(mapFragment, "main");
                currentItemId = R.id.home;
                break;
            case 1:
                changeFragment(profileFragment, "profile");
                currentItemId = R.id.side_navigation_menu_item1;
                
                break;

            case 2:
                changeFragment(ordersFragment, "Orders");
                currentItemId = R.id.side_navigation_menu_item2;

                break;
        }

    }


    @Override
    public void onSideNavigationItemClick(int itemId) {
        if (itemId == currentItemId) itemId = 10;
        switch (itemId) {

            case R.id.home:

                showMainFragment();
                currentItemId = itemId;
                break;

            case R.id.side_navigation_menu_item1:
                showProfileFragment();
                currentItemId = itemId;
                break;

            case R.id.side_navigation_menu_item2:

          /*      if (getFragmentManager().findFragmentByTag("Orders") != null)
                    return;

                changeFragment(ordersFragment, "Orders");
                hideOption(MENU_ADD_ITEM);
                setTitle("My Orders");*/
            	showOrdersFragment();
                currentItemId = itemId;
                break;

            case R.id.side_navigation_menu_item3:

                String lLogin = GlobalMethods.getDefaultsForPreferences("login", this);
                String lEmail = GlobalMethods.getDefaultsForPreferences("email", this);
                String lPassword = GlobalMethods.getDefaultsForPreferences("pass", this);

                if (lLogin != null && lEmail != null && lPassword != null) {
                    if (lLogin.equals("true") && !lEmail.equals("") && !lPassword.equals("")) {


                        GlobalMethods.setDefaultsForPreferences("login", "false", this);
                        GlobalMethods.setDefaultsForPreferences("email", "", this);
                        GlobalMethods.setDefaultsForPreferences("pass", "", this);

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        openLogoutScreen(itemId);
                    }
                } else {
                    openLogoutScreen(itemId);

                }

                break;

            default:

                return;
        }


    }

    private void openLogoutScreen(int itemId) {
        Intent intent = new Intent(MainActivity.this, LogOutActivity.class);
        startActivity(intent);
        currentItemId = itemId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                sideNavigationView.toggleMenu();
                break;
            case MENU_ADD_ITEM:
                Intent intent = new Intent(this, AddressActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void changeFragment(Fragment fragment, String tag) {

        FragmentManager frgManager2 = getSupportFragmentManager();
        frgManager2.beginTransaction().replace(R.id.frgmCont, fragment, tag)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        menu.add(Menu.NONE, MENU_ADD_ITEM, 0, "ADD ITEMS")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            int reload = data.getIntExtra("reload", 0);

            switch (reload) {
                case 1:

                    break;

                case 2:
                    showProfileFragment();
                    break;
            }
        }


    }

    public void showMainFragment() {

        if (getFragmentManager().findFragmentByTag("main") != null)
            return;
        changeFragment(mapFragment, "main");
        setTitle("Ship Bob");
        showOption(MENU_ADD_ITEM);
    }

    public void showProfileFragment() {
        if (getFragmentManager().findFragmentByTag("profile") != null)
            return;
        changeFragment(profileFragment, "profile");
        setTitle("My Profile");
        hideOption(MENU_ADD_ITEM);
    }

    public void showOrdersFragment() {
        if (getFragmentManager().findFragmentByTag("Orders") != null)
            return;
        changeFragment(ordersFragment, "Orders");
        setTitle("My Orders");
        hideOption(MENU_ADD_ITEM);
    }

}