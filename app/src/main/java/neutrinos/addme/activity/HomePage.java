package neutrinos.addme.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.alarmclass.AlarmMe;
import neutrinos.addme.fragment.DashBoardActivity;
import neutrinos.addme.fragment.DashboardFragment;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.PreferenceClass.Preferences;
import neutrinos.addme.R;
import neutrinos.addme.utilities.SetLocality;
import neutrinos.addme.utilities.SharedPrefManager;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final int PREFERENCES_ACTIVITY = 2;
    SharedPrefManager prefManager;
    private TextView dashboard;
    DashBoardActivity lFrag;
    DashboardFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefManager = new SharedPrefManager(getApplicationContext());
        int languagevalue = setDefaultLanguage(prefManager.getLanguage());
        Logger.logD("langvaluehome", "" + languagevalue);
        //setTexts(languagevalue);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();
        TextView name = (TextView) header.findViewById(R.id.app_title);
        TextView companyname = (TextView) header.findViewById(R.id.textView);
        // find MenuItem you want to change
        MenuItem profile = menu.findItem(R.id.customer_profile_id);
        MenuItem checklist = menu.findItem(R.id.check_list_id);
        MenuItem setalarm = menu.findItem(R.id.set_alaram_id);
        MenuItem configuration = menu.findItem(R.id.configuration);
        MenuItem settings = menu.findItem(R.id.settings_id);
        List<String> mainList = setTexts(languagevalue);
        // set new title to the MenuItem
        profile.setTitle(mainList.get(1));
        checklist.setTitle(mainList.get(2));
        setalarm.setTitle(mainList.get(3));
        configuration.setTitle(mainList.get(4));
        settings.setTitle(mainList.get(5));
        name.setText(mainList.get(6));
        companyname.setText(mainList.get(7));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
       if (turnGPSOn()){
           displayFragment();
       }
    }

    private boolean turnGPSOn() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(HomePage.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DashBoardActivity.REQUEST_LOCATION) {
            fragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        turnGPSOn();
    }

    private void displayFragment() {
        DashboardFragment fragment = new DashboardFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    private int setDefaultLanguage(int position) {
        switch (position) {
            case 1:
                SetLocality.setLocale(getApplicationContext(), "en");
                return 1;
            case 2:
                SetLocality.setLocale(getApplicationContext(), "ar");
                return 2;
            default:
                SetLocality.setLocale(getApplicationContext(), "en");
                //setTexts();
                return 1;
        }
    }

    private List<String> setTexts(int langvalue) {
        List<String> itemlist = new ArrayList<>();
        switch (langvalue) {
            case 1:
                itemlist.add("Dashboard");
                itemlist.add("Profile");
                itemlist.add("Checklist");
                itemlist.add("Set alarm");
                itemlist.add("Configuration");
                itemlist.add("Settings");
                itemlist.add("Add me");
                itemlist.add("Neutrinos Solutions Pvt Ltd.");
                return itemlist;
            case 2:
                itemlist.add("لوحة القيادة");
                itemlist.add("الملف الشخصي");
                itemlist.add("قائمة تدقيق");
                itemlist.add("ضبط المنبه");
                itemlist.add("ترتيب");
                itemlist.add("إعدادات");
                itemlist.add("إضافة أنا");
                itemlist.add("النيوترونات محاليل نشر محدود");
                return itemlist;
        }
        return itemlist;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (R.id.menu_settings == item.getItemId()) {
            Intent intent = new Intent(getBaseContext(), Preferences.class);
            startActivityForResult(intent, PREFERENCES_ACTIVITY);
            return true;
        } else if (R.id.menu_alarm == item.getItemId()) {
            Intent intent = new Intent(getBaseContext(), AlarmMe.class);
            startActivity(intent);
            return true;
        } else if (R.id.customer_profile == item.getItemId()) {
            Intent intent1 = new Intent(getBaseContext(), CustomerProfile.class);
            startActivity(intent1);
            return true;
        } else if (R.id.checklist == item.getItemId()) {
            Intent intent1 = new Intent(getBaseContext(), Checklist.class);
            startActivity(intent1);
            return true;
        } else if (R.id.todo == item.getItemId()) {
            Intent intent1 = new Intent(getBaseContext(), ToDoListActivity.class);
            startActivity(intent1);
            return true;
        } else if (R.id.near_me == item.getItemId()) {
            Intent intent = new Intent(getBaseContext(), CategoryListActivity.class);
            startActivity(intent);
            return true;
        } else if (R.id.near_by == item.getItemId()) {
            Intent intent = new Intent(getBaseContext(), NearbyActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        if (id == R.id.new_dashboard) {
            // if (fragment!=null) {
            fragment = new DashboardFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();*/
        } else if (id == R.id.customer_profile_id) {
           /* fragment = new CustomerProfile();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();*/
            Intent intent = new Intent(HomePage.this, CustomerProfile.class);
            startActivity(intent);
        } else if (id == R.id.check_list_id) {
            Intent intent = new Intent(HomePage.this, Checklist.class);
            startActivity(intent);
        } else if (id == R.id.set_alaram_id) {
            Intent intent = new Intent(HomePage.this, AlarmMe.class);
            startActivity(intent);
        } else if (id == R.id.settings_id) {
            Intent intent = new Intent(HomePage.this, Preferences.class);
            startActivityForResult(intent, PREFERENCES_ACTIVITY);
        }/* else if (id == R.id.near_me) {
            Intent intent = new Intent(HomePage.this, CategoryListActivity.class);
            startActivity(intent);
        } else if (id == R.id.near_by) {
            Intent intent = new Intent(HomePage.this, NearbyActivity.class);
            startActivity(intent);
        } else if (id == R.id.new_dashboard) {
            fragment = new DashboardFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }*/ else if (id == R.id.wishlist_navigation_id) {
            Intent intent = new Intent(HomePage.this, WishlistActivity.class);
            startActivity(intent);
        } else if (id == R.id.favourite_navigation_id) {
            Intent intent = new Intent(HomePage.this, FavouriteActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

