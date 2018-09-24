package neutrinos.addme.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import neutrinos.addme.ModelClass.NearbyModelClass;
import neutrinos.addme.R;
import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.utilities.GPSTracker;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.utilities.MyCurrentLocationTracker;

public class NewNearByActivity extends AppCompatActivity {
    private ArrayList<NearbyModelClass> nearbyList = new ArrayList<>();
    private GPSTracker gpsTracker;
    RecyclerView adapterrecycler;
    NearbyModelClass gridModelClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_near_by);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            gridModelClass = (NearbyModelClass) bundle.getSerializable("modeldata");
            assert gridModelClass != null;
            String orgid = String.valueOf(gridModelClass.getOrganizationID());
            Logger.logD("orgcheckfgdfg",""+orgid);
            gpsTracker = new GPSTracker(NewNearByActivity.this);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            callNearbyServer(setLocation(), getDeviceId(),orgid);
        }
        //adapterrecycler = findViewById(R.id.nearby_setting);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(NewNearByActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public String setLocation() {
        String stringLatitude = "0.0";
        String stringLongitude = "0.0";
        if (Double.doubleToRawLongBits(gpsTracker.latitude) == 0 || Double.doubleToRawLongBits(gpsTracker.longitude) == 0) {
            MyCurrentLocationTracker tracker = new MyCurrentLocationTracker(NewNearByActivity.this, null, null);
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

    private void callNearbyServer(String s, String deviceId,String orgid) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-nearby-local-details/device-address/" + deviceId + "/appuniqueid/54752/latlong/" + s + "/addme/%22%22/pageno/2/orgid/"+orgid;
       // String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-nearby-local-details/device-address/" + deviceId + "/appuniqueid/54752/latlong/" + s + "/addme/addme/pageno/2/orgid/"+orgid;
        String convertedURL = url.replace(",", "%2C");
        Logger.logV("nearbyapisecond", " callServerToSendParams " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.GET, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("nearbysecondpage", " Response " + response);
                        try {
                            new Parseresponse(response).execute();
                            //spinner.setVisibility(View.GONE);
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
        Volley.newRequestQueue(NewNearByActivity.this).add(postRequest);
    }

    private void checkresponse(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        int getStatus = jsonObject.getInt("response");
        if (getStatus == 2) {
            try {
                parseresponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(NewNearByActivity.this, "No nearby places", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseresponse(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("LocalValues")) {
            JSONArray jsonArray = jsonObject.getJSONArray("LocalValues");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                String organizationimage = jsonObject2.getString("orgimage");
                String latlong = jsonObject2.getString("latlong");
                String organizationname = jsonObject2.getString("Organization");
                int orgid = jsonObject2.getInt("orgid");
                //String address = jsonObject2.getString("Address");
                JSONArray jsonArray1 = jsonObject2.getJSONArray("contents");
                for (int i1 = 0; i1 < jsonArray1.length(); i1++) {
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i1);
                    NearbyModelClass nearbyModelClass = new NearbyModelClass(jsonObject1.getInt("fileId"),
                            jsonObject1.getString("organizationImage"), organizationname,
                            jsonObject1.getString("validity"), jsonObject1.getString("contentimage"),
                            jsonObject1.getString("filename"), orgid,
                            jsonObject1.getBoolean("favourite"), jsonObject1.getBoolean("wishlist"), jsonObject1.getString("product_description"), latlong, jsonObject1.getString("offercode"));
                    nearbyList.add(nearbyModelClass);
                }
               /* NearbyAdapter nearbyAdapter = new NearbyAdapter(NewNearByActivity.this, nearbyList);
                adapterrecycler.setAdapter(nearbyAdapter);*/
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


    private class Parseresponse extends AsyncTask<String, String, String> {
        String response;

        public Parseresponse(String responsenew) {
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
            try {
               /* NearbyAdapter nearbyAdapter = new NearbyAdapter(NewNearByActivity.this, nearbyList);
                adapterrecycler.setAdapter(nearbyAdapter);*/
                GridLayout favwishlistLayout = findViewById(R.id.favwishlist_layout);
                favwishlistLayout.removeAllViews();
                for (int j = 0; j < nearbyList.size(); j++) {
                    View imageChild = getLayoutInflater().inflate(R.layout.wishlist_adapter, favwishlistLayout, false);
                    ImageView wishlistimage = imageChild.findViewById(R.id.wishlist_image_adapter);
                    TextView name = imageChild.findViewById(R.id.wishlist_text);
                    TextView validity = imageChild.findViewById(R.id.grid_text_time);
                    LikeButton wishlistLike = imageChild.findViewById(R.id.wishlistadapter_id);
                    final int finalJ = j;
                    imageChild.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(NewNearByActivity.this, NearByNext.class);
                            Bundle b = new Bundle();
                            b.putSerializable("modelcalss", nearbyList.get(finalJ));
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    });
                    wishlistLike.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            setUserListToServer(nearbyList.get(finalJ), 100);
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            setUserListToServer(nearbyList.get(finalJ), 200);
                        }
                    });
                    if (nearbyList.get(j).isWishliststatus()) {
                        wishlistLike.setLiked(true);
                    } else {
                        wishlistLike.setLiked(false);
                    }
                    name.setText(nearbyList.get(j).getProductdescription());
                    validity.setText("Validity :" + nearbyList.get(j).getValidity());
                    Picasso.get()
                            .load(nearbyList.get(j).getContentImage())
                            .placeholder(R.drawable.emptynew)
                            .error(R.drawable.emptynew)
                            .into(wishlistimage);
                    favwishlistLayout.addView(imageChild);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void setUserListToServer(NearbyModelClass gridlist, int flag) {
        if (gridlist != null) {
            try {
                JSONArray array = new JSONArray();
                JSONObject item = new JSONObject();
                item.put("uniquename", gridlist.getFilename());
                array.put(item);
                Log.d("jsonarray", String.valueOf(array));
                if (flag == 100)
                    callWishlistapiSave(array, getDeviceId(), "save");
                else
                    callWishlistapiSave(array, getDeviceId(), "delete");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(NewNearByActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
        }
    }
    private void callWishlistapiSave(JSONArray jsonStructure,
                                     String deviceId, final String apiFlag) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.user_favourite/User-favourites/uniquename/"
                + jsonStructure + "/deviceaddress/" + deviceId + "/status/" + apiFlag + "/key/wishlist";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("wishapi", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("wishresponse", " Response -->" + apiFlag + response);
                        //checkTheResponse(response);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "test@liferay.com" + ":" + "test";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(NewNearByActivity.this).add(postRequest);
    }
}