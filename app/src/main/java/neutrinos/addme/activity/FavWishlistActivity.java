package neutrinos.addme.activity;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import neutrinos.addme.ModelClass.NearbyModelClass;
import neutrinos.addme.R;
import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.utilities.Logger;

public class FavWishlistActivity extends AppCompatActivity {
    private ArrayList<GridModelClass> favorglist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_wishlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String orgid = String.valueOf(bundle.getInt("orgid"));
        callserverOrgContents("test@liferay.com", "test", orgid, getDeviceId());
    }

    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    private void callserverOrgContents(final String s, final String test, String organizationID, String deviceId) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-contents-based-on-app-selection" +
                "/parentcategoryid/0/organizationid/" + organizationID + "/appuniqueid/54752/macaddress/" + deviceId + "/pageno/1";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("orgspecific", " callServerToSendParams " + url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("orgspecificresp", " Response " + response);
                        try {
                            checkresponse(response);
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
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(FavWishlistActivity.this).add(postRequest);
    }

    private void checkresponse(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        int getstatus = jsonObject.getInt("Status");
        if (getstatus == 2) {
            parseresponse(response);
        } else {
            Toast.makeText(FavWishlistActivity.this, "No contents for the selected organization", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseresponse(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("Urls");
        for (int i1 = 0; i1 < jsonArray.length(); i1++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i1);
          /*  NearbyModelClass nearbyModelClass = new NearbyModelClass(jsonObject1.getInt("fileId"),
                    jsonObject1.getString("organizationImage"), jsonObject1.getString("organizationName"),
                    jsonObject1.getString("validity"), jsonObject1.getString("contentimage"),
                    jsonObject1.getString("filename"), jsonObject1.getInt("organizationId"),
                    jsonObject1.getBoolean("favourite"), jsonObject1.getBoolean("wishlist"));*/

            GridModelClass gridModelClass = new GridModelClass(jsonObject1.getInt("fileId"),
                    jsonObject1.getString("filename"), jsonObject1.getString("organizationName"),
                    jsonObject1.getString("organizationImage"), jsonObject1.getInt("organizationId"),
                    jsonObject1.getString("contentimage"), jsonObject1.getBoolean("favourite"),
                    jsonObject1.getBoolean("wishlist"),jsonObject1.getString("product_description"),jsonObject1.getString("offercode"),jsonObject1.getString("validity"));
            favorglist.add(gridModelClass);
        }
        GridLayout favwishlistLayout = findViewById(R.id.favwishlist_layout);
        favwishlistLayout.removeAllViews();
        for (int j = 0; j < favorglist.size(); j++) {
            View imageChild = getLayoutInflater().inflate(R.layout.wishlist_adapter, favwishlistLayout, false);
            ImageView wishlistimage = imageChild.findViewById(R.id.wishlist_image_adapter);
            TextView name = imageChild.findViewById(R.id.wishlist_text);
            TextView validity = imageChild.findViewById(R.id.grid_text_time);
            LikeButton wishlistLike = imageChild.findViewById(R.id.wishlistadapter_id);
            final int finalJ = j;
            imageChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FavWishlistActivity.this, Offers.class);
                    Bundle b = new Bundle();
                    b.putSerializable("modelcalss", favorglist.get(finalJ));
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
            wishlistLike.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    setUserListToServer(favorglist.get(finalJ), 100);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    setUserListToServer(favorglist.get(finalJ), 200);
                }
            });
            if (favorglist.get(j).isWishstatus()) {
                wishlistLike.setLiked(true);
            } else {
                wishlistLike.setLiked(false);
            }
            name.setText(favorglist.get(j).getProduct_description());
            validity.setText("Validity :"+favorglist.get(j).getValidity());
            Picasso.get()
                    .load(favorglist.get(j).getImageUrl())
                    .placeholder(R.drawable.emptynew)
                    .error(R.drawable.emptynew)
                    .into(wishlistimage);
            favwishlistLayout.addView(imageChild);
        }
    }

    private void setUserListToServer(GridModelClass gridlist, int flag) {
        if (gridlist != null) {
            try {
                JSONArray array = new JSONArray();
                JSONObject item = new JSONObject();
                item.put("uniquename", gridlist.getFileName());
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
            Toast.makeText(FavWishlistActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
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
        Volley.newRequestQueue(FavWishlistActivity.this).add(postRequest);
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
