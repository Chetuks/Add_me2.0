package neutrinos.addme.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


import neutrinos.addme.ModelClass.Wishlistmodelclass;
import neutrinos.addme.R;
import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.utilities.Utils;

public class WishlistActivity extends AppCompatActivity {
    private ArrayList<GridModelClass> wishlistList = new ArrayList<>();
    GridLayout wishlistlayout;
    ProgressBar spinner;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wishlistlayout = findViewById(R.id.wishlist_layout);
        context = WishlistActivity.this;
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.bringToFront();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinner.setVisibility(View.VISIBLE);
        callServer();
    }

    private void callServer() {
        if (Utils.checkNetworkConnection(this)) {
            callWishlistapi("test@liferay.com", "test", getDeviceId());
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @return to fetch the device id
     */
    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(WishlistActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private void callWishlistapi(final String s, final String test, String deviceId) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.user_favourite/User-favourites/-uniquename/deviceaddress/" + deviceId + "/status/retrieve/key/wishlist";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("wishlistapi", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("wishlistresponse", " Response " + response);
                        try {
                            parseresponseWishlist(response);
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
                String credentials = s + ":" + test;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(WishlistActivity.this).add(postRequest);
    }

    private void parseresponseWishlist(String response) throws Exception {
        JSONArray jsonArray = new JSONArray(response);
        JSONObject object = jsonArray.getJSONObject(0);
        int responsecheck = object.getInt("response");
        if (responsecheck == 2) {
            try {
                JSONArray jsonArray1 = object.getJSONArray("urls");
                if (jsonArray1.length() != 0) {
                    parseresponse(response);
                } else {
                    Toast.makeText(WishlistActivity.this, "Url is empty", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(WishlistActivity.this, "Wishlist is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseresponse(String response) throws JSONException {
        JSONArray jsonArray = new JSONArray(response);
        JSONObject object = jsonArray.getJSONObject(0);
        JSONArray jsonArray1 = object.getJSONArray("urls");
        for (int i = 0; i < jsonArray1.length(); i++) {
            JSONObject jsonObject = jsonArray1.getJSONObject(i);
            GridModelClass gridModelClass = new GridModelClass(jsonObject.getInt("fileId"),
                    jsonObject.getString("filename"), jsonObject.getString("organizationName"),
                    "", jsonObject.getInt("organizationId"),
                    jsonObject.getString("contentimage"), jsonObject.getBoolean("favourite"),
                    false, jsonObject.getString("product_description"), jsonObject.getString("offercode"),jsonObject.getString("validity"));
            wishlistList.add(gridModelClass);
        }
        wishlistlayout.removeAllViews();
        for (int j = 0; j < wishlistList.size(); j++) {
            View imageChild = getLayoutInflater().inflate(R.layout.wishlist_adapter, wishlistlayout, false);
            ImageView wishlistimage = imageChild.findViewById(R.id.wishlist_image_adapter);
            TextView name = imageChild.findViewById(R.id.wishlist_text);
            TextView validity = imageChild.findViewById(R.id.grid_text_time);
            LikeButton wishlistLike = imageChild.findViewById(R.id.wishlistadapter_id);
            wishlistLike.setLiked(true);
            final int finalJ = j;
            imageChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("modelcalss", wishlistList.get(finalJ));
                    Intent intent = new Intent(WishlistActivity.this, Offers.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            wishlistLike.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {

                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    spinner.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    setUserListtoserverdelete(wishlistList.get(finalJ));
                }
            });
            name.setText(wishlistList.get(j).getProduct_description());
            validity.setText("Offer validity: "+wishlistList.get(j).getValidity());
            Picasso.get()
                    .load(wishlistList.get(j).getImageUrl())
                    .placeholder(R.drawable.emptynew)
                    .error(R.drawable.emptynew)
                    .into(wishlistimage);
            wishlistlayout.addView(imageChild);
        }
    }

    private void setUserListtoserverdelete(GridModelClass wishlistList) {
        if (wishlistList != null) {
            try {
                JSONArray array = new JSONArray();
                JSONObject item = new JSONObject();
                item.put("uniquename", wishlistList.getFileName());
                array.put(item);
                Log.d("jsonarray", String.valueOf(array));
                callwishlistdeleteapi("test@liferay.com", "test", array, getDeviceId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(WishlistActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void callwishlistdeleteapi(final String s, final String test, JSONArray array, String deviceId) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.user_favourite/User-favourites/uniquename/"
                + array + "/deviceaddress/" + deviceId + "/status/delete/key/wishlist";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("favapi", " callServerToSendParams " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("wishlistdelete", " Response " + response);
                        spinner.setVisibility(View.GONE);
                        try {
                            checkresponseofWishlistdelete(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        spinner.setVisibility(View.GONE);
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
        Volley.newRequestQueue(WishlistActivity.this).add(postRequest);
    }

    private void checkresponseofWishlistdelete(String response) throws JSONException {
        JSONArray jsonArray = new JSONArray(response);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        int responsecheck = jsonObject.getInt("response");
        if (responsecheck == 2) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else {
            Toast.makeText(WishlistActivity.this, "Selected item has not deleted", Toast.LENGTH_SHORT).show();
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
