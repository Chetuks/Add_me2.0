package neutrinos.addme.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
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
import neutrinos.addme.utilities.Logger;

public class Offers extends AppCompatActivity {
    GridModelClass gridModelClass;
    ProgressDialog newdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView offerimage = findViewById(R.id.offer_image);
        TextView organizationName=findViewById(R.id.organization_offers_name);
        TextView offercode=findViewById(R.id.offer_code);
        LikeButton offerLikebtn=findViewById(R.id.offer_like_btn);
        newdialog = new ProgressDialog(Offers.this);
        Objects.requireNonNull(newdialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
        newdialog.setMessage("Loading please wait....");
        newdialog.setCancelable(false);
        newdialog.show();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String offerbundleimage = bundle.getString("image");
            gridModelClass = (GridModelClass) bundle.getSerializable("modelcalss");
            assert gridModelClass != null;
            String orgName=String.valueOf(gridModelClass.getOrganizationname());
            Picasso.get()
                    .load(gridModelClass.getImageUrl())
                    .fit()
                    .into(offerimage);
            organizationName.setText(orgName);
            offercode.setText(gridModelClass.getOffercode());
        }
        newdialog.dismiss();
        if(gridModelClass.isFavstatus()){
            offerLikebtn.setLiked(true);
        }
        else {
            offerLikebtn.setLiked(false);
        }
        offerLikebtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                setUserListToServer(gridModelClass, 100);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                setUserListToServer(gridModelClass, 200);
            }
        });
    }
    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(Offers.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private void setUserListToServer(GridModelClass gridModelClass, int i) {
        if (gridModelClass != null) {
            try {
                JSONArray array = new JSONArray();
                JSONObject item = new JSONObject();
                item.put("organization", gridModelClass.getOrganizationid());
                array.put(item);
                Log.d("jsonarray", String.valueOf(array));
                if (i == 100)
                    callFavouriteapi("test@liferay.com", "test", array, getDeviceId(), "save");
                else
                    callFavouriteapi("test@liferay.com", "test", array, getDeviceId(), "delete");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(Offers.this, "List is empty", Toast.LENGTH_SHORT).show();
        }
    }
    private void callFavouriteapi(final String s, final String test, JSONArray jsonStructure,
                                     String deviceId, final String apiFlag) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.user_favourite/User-favourites/uniquename/"
                + jsonStructure + "/deviceaddress/" + deviceId + "/status/"+apiFlag+"/key/favourites";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("favclickapi", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("favclickresponse", " Response -->" + apiFlag + response);
                        newdialog.dismiss();
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
                String credentials = s + ":" + test;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(Offers.this).add(postRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
