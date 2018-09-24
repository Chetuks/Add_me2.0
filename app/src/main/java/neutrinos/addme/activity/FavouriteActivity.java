package neutrinos.addme.activity;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import neutrinos.addme.ModelClass.Wishlistmodelclass;
import neutrinos.addme.R;
import neutrinos.addme.adapter.SecondCategoryAdapter;
import neutrinos.addme.fragment.DashboardFragment;
import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.utilities.Logger;

public class FavouriteActivity extends AppCompatActivity {
    private ArrayList<Wishlistmodelclass> favlist = new ArrayList<>();
    private ArrayList<GridModelClass> favwishList = new ArrayList<>();
    //RecyclerView favwishListrecycler;
    private SecondCategoryAdapter adapter;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //favwishListrecycler = findViewById(R.id.favwishlist);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.bringToFront();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinner.setVisibility(View.VISIBLE);
        callApiTOGetFavResponse(getDeviceId());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * @return to fetch the device id
     */
    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(FavouriteActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    private void callApiTOGetFavResponse(String deviceId) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.user_favourite/User-favourites/-uniquename/deviceaddress/" + deviceId + "/status/retrieve/key/favourites";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("favouriteapi", " callServerToSendParams " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("favresponse", " Response " + response);
                        try {
                            //TODO
                            checkresponseFavourite(response);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "test@liferay.com" + ":" + "test";
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(FavouriteActivity.this).add(postRequest);
    }

    private void checkresponseFavourite(String response) throws Exception {
        JSONArray jsonArray = new JSONArray(response);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        int responsecheck = jsonObject.getInt("response");
        if (responsecheck == 0) {
            Toast.makeText(FavouriteActivity.this, "Favourite list is empty", Toast.LENGTH_SHORT).show();
        } else {
            parseresponseFavourite(response);
        }
    }

    private void parseresponseFavourite(String response) throws Exception {
        JSONArray jsonArray = new JSONArray(response);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Wishlistmodelclass wishlistmodelclass = new Wishlistmodelclass(jsonObject.getInt("id"), jsonObject.getString("location"), jsonObject.getString("name"), jsonObject.getString("image"),"India 9/616, Chitrakoot Area, Vaishali Nagar, Bangalore");
            favlist.add(wishlistmodelclass);
        }
        LinearLayout favlayout = findViewById(R.id.favourite_layout);
        favlayout.removeAllViews();
        for (int j = 0; j < favlist.size(); j++) {
            View imageChild = getLayoutInflater().inflate(R.layout.favourite_inflator, favlayout, false);
            ImageView favOrganizationimage = imageChild.findViewById(R.id.organization_image);
            TextView favOrgName = imageChild.findViewById(R.id.organization_name);
            TextView address = imageChild.findViewById(R.id.organization_description);
            LikeButton favLikeBtn = imageChild.findViewById(R.id.fav_orgBtn);
            Button mapButton = imageChild.findViewById(R.id.fav_map_btn);
            final int finalJ = j;
            try {
                mapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String latlong = favlist.get(finalJ).getFavlocation();
                        Intent intent = new Intent(FavouriteActivity.this, MapsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("latlong", latlong);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            favLikeBtn.setLiked(true);
            favLikeBtn.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    setUserListToServer(favlist.get(finalJ));
                }
            });
            favOrgName.setText(favlist.get(j).getFavOrgName());
            address.setText(favlist.get(j).getAddress());
            Picasso.get()
                    .load(favlist.get(j).getFavOrgImage())
                    .fit()
                    .placeholder(R.drawable.emptynew)
                    .error(R.drawable.emptynew)
                    .into(favOrganizationimage);
            final int organizationid = favlist.get(finalJ).getFavid();
            imageChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FavouriteActivity.this, FavWishlistActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("orgid", organizationid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    // callWishlistOrgapi("test@liferay.com", "test", favlist.get(finalJ).getFavid(), getDeviceId());
                }
            });
            favlayout.addView(imageChild);
        }
    }

    private void setUserListToServer(Wishlistmodelclass favlist) {
        if (favlist != null) {
            try {
                JSONArray array = new JSONArray();
                JSONObject item = new JSONObject();
                item.put("uniquename", favlist.getFavid());
                array.put(item);
                Log.d("jsonarray", String.valueOf(array));
                callFavDeleteapi("test@liferay.com", "test", array, getDeviceId(), "delete");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(FavouriteActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
        }

    }

    private void callFavDeleteapi(final String s, final String test, JSONArray array, String deviceId, String delete) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.user_favourite/User-favourites/uniquename/"
                + array + "/deviceaddress/" + deviceId + "/status/" + delete + "/key/wishlist";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("favapi", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("favresponse", " Response -->" + response);
                        try {
                            checkresponseofFavouritedelete(response);
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = s + ":" + test;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(FavouriteActivity.this).add(postRequest);
    }

    private void checkresponseofFavouritedelete(String response) throws Exception {
        JSONArray jsonArray = new JSONArray(response);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        int responsecheck = jsonObject.getInt("response");
        if (responsecheck == 2) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else {
            Toast.makeText(FavouriteActivity.this, "Selected item has not deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void callWishlistOrgapi(final String s, final String test, int favid, String deviceId) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-contents-based-on-app-selection/parentcategoryid/0/organizationid/" + favid + "/appuniqueid/54752/macaddress/" + deviceId + "/pageno/1";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("favwish", " callServerToSendParams " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("favwishlist", " Response " + response);
                        checkresponsefavwish(response);
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
                String credentials = s + ":" + test;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(FavouriteActivity.this).add(postRequest);
    }

    private void checkresponsefavwish(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("Status");
            Logger.logD("checkStatus", "response" + getStatus);
            if (getStatus == 2) {
                try {
                    parceResponsefavwish(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(FavouriteActivity.this, "No data in the server", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parceResponsefavwish(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("Urls");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            String status = jsonObject.getString("Status");
            if (!status.isEmpty()) {
                GridModelClass gridModelClass = new GridModelClass(jsonObject2.getInt("fileId"),
                        jsonObject2.getString("filename"), jsonObject2.getString("organizationName"),
                        jsonObject2.getString("organizationImage"), jsonObject2.getInt("organizationId"),
                        jsonObject2.getString("contentimage"), jsonObject2.getBoolean("favourite"),
                        jsonObject2.getBoolean("wishlist"), jsonObject2.getString("product_description"),jsonObject2.getString("offercode"),jsonObject2.getString("validity"));
                favwishList.add(gridModelClass);
            } else {
                Toast.makeText(FavouriteActivity.this, "Status no response from server", Toast.LENGTH_SHORT).show();
            }
        }
        settingAdapter(favwishList);
    }

    private void settingAdapter(ArrayList<GridModelClass> favwishList) {
        if (!favwishList.isEmpty()) {
            GridLayoutManager layoutManagergrid = new GridLayoutManager(this, 2);
            //favwishListrecycler.setLayoutManager(layoutManagergrid);
            adapter = new SecondCategoryAdapter(FavouriteActivity.this, favwishList);
            // favwishListrecycler.setAdapter(adapter);
            // favwishListrecycler.setHasFixedSize(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
