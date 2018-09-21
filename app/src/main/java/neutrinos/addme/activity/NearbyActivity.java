package neutrinos.addme.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import neutrinos.addme.ModelClass.NearbyModelClass;
import neutrinos.addme.R;
import neutrinos.addme.utilities.GPSTracker;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.utilities.MyCurrentLocationTracker;

public class NearbyActivity extends AppCompatActivity {
    private ArrayList<NearbyModelClass> nearbyList = new ArrayList<>();
    LinearLayout nearbylayout;
    TextView description;
    private GPSTracker gpsTracker;
    TextView erroetextnearby;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        description = findViewById(R.id.headingnearby);
        erroetextnearby = findViewById(R.id.errortext_nearby);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.bringToFront();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinner.setVisibility(View.VISIBLE);
        description.setText("Nearby Places");
        gpsTracker = new GPSTracker(NearbyActivity.this);
        callingNearbyAPI(setLocation(), getDeviceId());
    }

    public String setLocation() {
        String stringLatitude = "0.0";
        String stringLongitude = "0.0";
        if (Double.doubleToRawLongBits(gpsTracker.latitude) == 0 || Double.doubleToRawLongBits(gpsTracker.longitude) == 0) {
            MyCurrentLocationTracker tracker = new MyCurrentLocationTracker(NearbyActivity.this, null, null);
            Location loc = tracker.getLocation(null, null);
            if (loc != null) {
                stringLatitude = String.valueOf(loc.getLatitude());
                stringLongitude = String.valueOf(loc.getLongitude());
            }
        } else {
            stringLatitude = String.valueOf(gpsTracker.latitude);
            stringLongitude = String.valueOf(gpsTracker.longitude);
        }
        gpsTracker.stopUsingGPS();
        return stringLatitude + "," + stringLongitude;
    }

    /**
     * @return to fetch the device id
     */
    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(NearbyActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private void callingNearbyAPI(String setLocation, String deviceId) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-nearby-local-details/device-address/" + deviceId + "/appuniqueid/54752/latlong/" + setLocation + "/addme/addme/pageno/1/orgid/0";
       // String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-nearby-local-details/device-address/" + deviceId + "/appuniqueid/54752/latlong/" + setLocation + "/addme/%22%22/pageno/1/orgid/0";
        String convertedURL = url.replace(",", "%2C");
        Logger.logV("nearby", " callServerToSendParams " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.GET, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("nearby", " Response " + response);
                        try {
                            new Parseresponse(response).execute();
                            Logger.logD("responsellll", "" + response);
                            spinner.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Logger.logV("Location", " ERROR " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String credentials = "test@liferay.com" + ":" + "test";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(NearbyActivity.this).add(postRequest);
    }

    class Parseresponse extends AsyncTask<String, String, String> {
        String response;

        public Parseresponse(String responsenew) throws Exception {
            response = responsenew;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                checkresponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            spinner.setVisibility(View.GONE);
            LinearLayout nearbylayout = findViewById(R.id.inflator_layout);
            //nearbylayout.removeAllViews();
            for (int j = 0; j < nearbyList.size(); j++) {
                View imageChild = getLayoutInflater().inflate(R.layout.nearbyinflator, nearbylayout, false);
                ImageView circleImageView = imageChild.findViewById(R.id.content_image);
                TextView address = imageChild.findViewById(R.id.address_view);
                TextView name = imageChild.findViewById(R.id.place_name);
                Button mapbutton = imageChild.findViewById(R.id.map_btn);
                final String finalLatlong = nearbyList.get(j).getLatlong();
                mapbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("latlong", finalLatlong);
                        Intent intent = new Intent(NearbyActivity.this, MapsActivity.class);
                        Logger.logD("checking_lat", "" + finalLatlong);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                final int finalJ = j;
                imageChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("modeldata", nearbyList.get(finalJ));
                        Intent intent = new Intent(NearbyActivity.this, NewNearByActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                name.setText(nearbyList.get(j).getOrganizationname());
                address.setText(nearbyList.get(j).getAddress());
                Picasso.get()
                        .load(nearbyList.get(j).getOrganizationimage())
                        .placeholder(R.drawable.emptyimgnew)
                        .fit()
                        .error(R.drawable.emptyimgnew)
                        .into(circleImageView);
                nearbylayout.addView(imageChild);
            }
        }
    }

    private void checkresponse(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        int getStatus = jsonObject.getInt("response");
        if (getStatus == 2) {
            try {
                parseresponse(response);
            } catch (Exception e) {
                Toast.makeText(NearbyActivity.this, "No nearby places", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            erroetextnearby.setVisibility(View.GONE);
        } else {
            Toast.makeText(NearbyActivity.this, "No nearby places", Toast.LENGTH_SHORT).show();
            spinner.setVisibility(View.GONE);
            erroetextnearby.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void parseresponse(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("LocalValues")) {
            JSONArray jsonArray = jsonObject.getJSONArray("LocalValues");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                NearbyModelClass nearbyModelClass = new NearbyModelClass((jsonObject1.getString("orgimage")),
                        jsonObject1.getString("Organization"), jsonObject1.getInt("orgid"),
                        jsonObject1.getString("latlong"), jsonObject1.getString("Address"));
                nearbyList.add(nearbyModelClass);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}