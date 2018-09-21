package neutrinos.addme.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutrinos.addme.utilities.Logger;
import neutrinos.addme.ModelClass.NavigationDetails;
import neutrinos.addme.ModelClass.Url;
import neutrinos.addme.R;

public class SelectedCategoryActivity extends AppCompatActivity {

    private int id;
    private int organization_id;
    private ImageView image;
    private VideoView video;
    private Button showNavigationButton;
    Handler handler;
    NavigationDetails navigationDetails;
    private Context activity;
    String latlong;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gettingBundle();
        init();
        /*String tempResponse="{\n" +
                "  \"message\": \"success\",\n" +
                "  \"status\": 2,\n" +
                "  \"address\": \"Bengaluru\",\n" +
                "  \"organizationName\": \"Apollo Pharmacy\",\n" +
                "  \"urls\": [\n" +
                "    {\n" +
                "      \"latlong\": \"11.0632247,77.5212177\",\n" +
                "      \"filename\": \"25001.jpg\",\n" +
                "      \"filetype\": \"image\",\n" +
                "      \"url\": \"http://216.98.9.235:8180/documents/20181/21244/25001.jpg/91786ddf-d956-47d1-8d76-853783669141\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"latlong\": \"19.0632247,77.5212177\",\n" +
                "      \"filename\": \"25001.jpg\",\n" +
                "      \"filetype\": \"image\",\n" +
                "      \"url\": \"http://216.98.9.235:8180/documents/20194/24352/cipla.jpg/258f1b5e-322d-4c1b-a7e3-14644d9bde97\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"latlong\": \"13.0722247,77.5256475\",\n" +
                "      \"filename\": \"25001.jpg\",\n" +
                "      \"filetype\": \"image\",\n" +
                "      \"url\": \"http://216.98.9.235:8180/documents/20181/21244/25001.jpg/91786ddf-d956-47d1-8d76-853783669141\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        parseResponse(tempResponse);*/

        callAPI("test1@liferay.com", "test1");
        showNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("latlong", latlong);
                Logger.logD("checking_lat", "" + latlong);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void parseResponse(String tempResponse) {
      /*  Gson gson=new Gson();

        navigationDetails = gson.fromJson(tempResponse, NavigationDetails.class);
        Logger.logD("tempresponseee",""+tempResponse);*/
        try {
            JSONArray jsonarray = new JSONArray(tempResponse);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                int status = jsonobject.getInt("Status");
                Logger.logD("abcsgfd", "" + status);
                if (status == 2) {
                    latlong = jsonobject.getString("latlong");
                    JSONArray jsonArray=jsonobject.getJSONArray("Urls");
                    Logger.logD("jsonarraycheck",""+jsonArray);
                    for(int j=0;j<jsonArray.length();j++){
                       JSONObject jsonObject=jsonArray.getJSONObject(j);
                        url = jsonObject.getString("url");
                        Logger.logD("imageurl",""+url);
                    }
                    Logger.logD("chekui",""+latlong);
                    video.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    Picasso.get().load(url).fit().centerCrop()
                            .placeholder(R.mipmap.addme_logo)
                            .error(R.mipmap.addme_logo)
                            .into(image);
                }
                else{
                    Toast.makeText(this,"Error in response",Toast.LENGTH_LONG).show();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // if (navigationDetails.getStatus() == 2) {
           /* Thread thread = new Thread(new MyThread(navigationDetails.getUrls()));
            thread.start();
            handler= new Handler(){
                @Override
                public void handleMessage(Message msg) {
                  Bundle bundle= msg.getData();
                    video.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                    Picasso.with(SelectedCategoryActivity.this).load(bundle.getString("ImageURL")).fit().centerCrop()
                            .placeholder(R.mipmap.addme_logo)
                            .error(R.mipmap.addme_logo)
                            .into(image);
                }
            };
*//*else {
            Toast.makeText(this, "Error in resoponse", Toast.LENGTH_SHORT).show();*/


    }

    /**
     * Calling the API for the Adds
     *
     * @param s
     * @param test1
     */
    private void callAPI(final String s, final String test1) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating List........");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.useractivities/get-contents-based-on-app-selection/parentcategoryid/" + id + "/orgid/" + organization_id + "/appuniqueid/20828/language-name/english";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " callServerToSendParamslast " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Logger.logV("Getting", " Details of navigation Response " + response);
                        //parseDetailsCategoryLIstResponse(response);
                        parseResponse(response);
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
                String credentials = s + ":" + test1;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(activity).add(postRequest);
    }


    /**
     * initializing the values of XML
     */
    private void init() {
        image = (ImageView) findViewById(R.id.image_container);
        video = (VideoView) findViewById(R.id.video_container);
        showNavigationButton = (Button) findViewById(R.id.view_navigation);
        activity = SelectedCategoryActivity.this;
    }

    /**
     * getting bundle data from previous activity
     */
    private void gettingBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("id");
            organization_id = bundle.getInt("organization_id");
            Logger.logD("ID first", "" + id);
            Logger.logD("ID org", "" + organization_id);

        }
    }

    private class MyThread implements Runnable {
        List<Url> url;

        public MyThread(List<Url> url) {
            this.url = url;
        }

        @Override
        public void run() {
            Message message = Message.obtain();

            for (int i = 0; i < url.size(); i++) {
                switch (url.get(i).getFiletype()) {
                    case "image":
                        Bundle bundle = new Bundle();
                        bundle.putString("ImageURL", url.get(i).getUrl());
                        message.setData(bundle);
                        handler.handleMessage(message);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;

                }
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
           /* Intent i= new Intent(this, CategoryListActivity.class);
           // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();*/
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
