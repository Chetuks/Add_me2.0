package neutrinos.addme.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import neutrinos.addme.ModelClass.CategoryModelClass;
import neutrinos.addme.ModelClass.HotSearchModelclass;
import neutrinos.addme.ModelClass.SearchModelclass;
import neutrinos.addme.R;
import neutrinos.addme.utilities.Logger;

public class SearchActivity extends AppCompatActivity {
    AutoCompleteTextView autoText;
    private ArrayList<SearchModelclass> searchbytextList = new ArrayList<>();
    private ArrayList<HotSearchModelclass> hotsearchbytextList = new ArrayList<>();
    private ArrayList<CategoryModelClass> hotwordslist = new ArrayList<>();
    String products[] = {"Dell Inspiron", "HTC One X", "HTC Wildfire S", "HTC Sense", "HTC Sensation XE",
            "iPhone 4S", "Samsung Galaxy Note 800",
            "Samsung Galaxy S3", "MacBook Air", "Mac Mini", "MacBook Pro", "shirt"};
    List<String> hotwordsarraylist = new ArrayList<>();
    ArrayAdapter<String> adapter;
    String responsetext;
    LinearLayout errortext;
    LinearLayout searchlayout;
    LinearLayout hotsearchlayout;
    ProgressBar spinner;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = SearchActivity.this;
        autoText = findViewById(R.id.inputSearch);
        final LinearLayout hotkeywordslayout = findViewById(R.id.hotkeywordslayout);
        ImageView search_btn = findViewById(R.id.search_image_btn);
        errortext = findViewById(R.id.error_searchtext);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.bringToFront();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinner.setVisibility(View.VISIBLE);
        callmainApiFromServer();
        autoText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchingtext = String.valueOf(autoText.getText());
                    searchbytextList.clear();
                    spinner.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    callapiforenteredwords("test@liferay.com", "test", getDeviceId(), searchingtext);
                    //hotkeywordslayout.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchingtext = String.valueOf(autoText.getText());
                Logger.logD("testing_text", "" + searchingtext);
                Toast.makeText(SearchActivity.this, "" + searchingtext, Toast.LENGTH_SHORT).show();
                callapiforenteredwords("test@liferay.com", "test", getDeviceId(), searchingtext);
                hotkeywordslayout.setVisibility(View.GONE);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * calling api to set the data for main category adds
     */
    private void callmainApiFromServer() {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-all-main-category-of-organiations/vocabulary-id/0/vocabulary-name/organizationCategory/appuniqueid/54752/language-name/english";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("mainapi", " callServerToSendParams " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("testingresponse", " Response " + response);
                        checkresponsesearch(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Logger.logV("volleyerror", " ERROR " + error);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(Objects.requireNonNull(SearchActivity.this)).add(postRequest);
    }

    /**
     * @param response check the response of save and update api
     */
    private void checkresponsesearch(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("status");
            Logger.logD("checkStatus", "status" + getStatus);
            if (getStatus == 2) {
                try {
                    spinner.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    parseresponsesearch(response);
                    parseresponseMainhotwords(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                spinner.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(SearchActivity.this, "No data in the server", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseresponseMainhotwords(String Response) throws Exception {
        JSONObject jsonObject = new JSONObject(Response);
        JSONArray jsonArray = jsonObject.getJSONArray("hotwords");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            CategoryModelClass categoryModelClass = new CategoryModelClass((jsonObject1.getInt("hotwordid")), jsonObject1.getString("hotwordname"));
            hotwordslist.add(categoryModelClass);
        }
        LinearLayout commentsDynamicLayout = (LinearLayout) findViewById(R.id.hotsearch);
        commentsDynamicLayout.removeAllViews();
        for (int j = 0; j < hotwordslist.size(); j++) {
            Logger.logD("listsizesearch", "" + hotwordslist.size());
            View imageChild = getLayoutInflater().inflate(R.layout.hot_searchlayout, commentsDynamicLayout, false);
            final TextView name = imageChild.findViewById(R.id.circle_text);
            name.setText(hotwordslist.get(j).getHotwordname());
            commentsDynamicLayout.addView(imageChild);
            final int finalJ = j;
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    autoText.setText(name.getText());
                    autoText.dismissDropDown();
                    hotsearchbytextList.clear();
                    callapiforenteredwords("test@liferay.com", "test", getDeviceId(), hotwordslist.get(finalJ).getHotwordname());
                    //callhotketwordsapi(hotwordslist.get(finalJ).getHotwordname());
                }
            });
        }
    }

    private void callhotketwordsapi(String hotkeywordid) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-contents-based-on-hot-keywords/appunique-id/54752/hot-keyword-id/" + hotkeywordid + "/macid/" + getDeviceId();
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("hotkeywordapi", " callServerToSendParams " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("hotkeywordresponse", " Response " + response);
                        checkTheResponsebyHotKeyword(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Logger.logV("volleyerror", " ERROR " + error);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(Objects.requireNonNull(SearchActivity.this)).add(postRequest);
    }

    private void checkTheResponsebyHotKeyword(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("response");
            Logger.logD("checkStatus", "status" + getStatus);
            if (getStatus == 1) {
                try {
                    parceResponseHotkeywordtype(response);
                    errortext.setVisibility(View.GONE);
                    hideSoftKeyboard(SearchActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                errortext.setVisibility(View.VISIBLE);
                hotsearchbytextList.clear();
                try {
                    hotsearchlayout.removeAllViewsInLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(SearchActivity.this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parceResponseHotkeywordtype(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        String organizationname = String.valueOf(jsonObject.get("organization"));
        JSONArray jsonArray = jsonObject.getJSONArray("urls");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            HotSearchModelclass hotSearchModelclass = new HotSearchModelclass(jsonObject1.getInt("fileId"), jsonObject1.getString("filename"), jsonObject1.getString("url"));
            hotsearchbytextList.add(hotSearchModelclass);
        }
        hotsearchlayout = (LinearLayout) findViewById(R.id.searched_layout);
        hotsearchlayout.removeAllViews();
        for (int j = 0; j < hotsearchbytextList.size(); j++) {
            View imageChild = getLayoutInflater().inflate(R.layout.searchlayout, hotsearchlayout, false);
            ImageView searchimage = imageChild.findViewById(R.id.searched_image);
            TextView name = imageChild.findViewById(R.id.nameview);
            name.setText(organizationname);
            Picasso.get()
                    .load(hotsearchbytextList.get(j).getImageurl())
                    .fit()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(searchimage);
            hotsearchlayout.addView(imageChild);
        }
    }

    private void parseresponsesearch(String Response) throws Exception {
        JSONObject jsonObject = new JSONObject(Response);
        String hotwords = (String) jsonObject.get("hotwordentered");
        String[] hotwordsarry = hotwords.split(",");
        for (String aHotwordsarry : hotwordsarry) {
            Logger.logD("searchhot", "" + aHotwordsarry);
            aHotwordsarry.replace("[", "");
            aHotwordsarry.replace("]", "");
            hotwordsarraylist.add(String.valueOf(aHotwordsarry));
        }
        if (hotwordsarraylist != null) {
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, hotwordsarraylist);
            autoText.setAdapter(adapter);
            autoText.setThreshold(1);
        }
    }

    /**
     * @return to fetch the device id
     */
    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(SearchActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private void callapiforenteredwords(final String s, final String test, String deviceId, String searchingtext) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-contents-based-onwords-entered/appunique-id/54752/hot-keyword/" + searchingtext + "/macid/" + deviceId;
        String convertedURL = url.replace(" ", "");
        Logger.logV("Location", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("searchresponse", " Response " + response);
                        checkTheResponseOne(response);
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        try {
                            checkTheResponsebyKeyword(response);
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
        Volley.newRequestQueue(SearchActivity.this).add(postRequest);
    }

    private void checkTheResponsebyKeyword(String responsetext) {
        try {
            JSONObject jsonObject = new JSONObject(responsetext);
            int getStatus = jsonObject.getInt("response");
            Logger.logD("checkStatus", "status" + getStatus);
            if (getStatus == 1) {
                try {
                    parceResponsekeywordtype(responsetext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (searchlayout != null) {
                    searchlayout.removeAllViewsInLayout();
                }
                Toast.makeText(SearchActivity.this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parceResponsekeywordtype(String responsetext) throws Exception {
        JSONObject jsonObject = new JSONObject(responsetext);
        String organizationname = String.valueOf(jsonObject.get("organization"));
        JSONArray jsonArray = jsonObject.getJSONArray("urls");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            SearchModelclass searchModelclass = new SearchModelclass((jsonObject1.getInt("fileId")), jsonObject1.getString("filename"), jsonObject1.getString("url"));
            searchbytextList.add(searchModelclass);
        }
        searchlayout = (LinearLayout) findViewById(R.id.searched_layout);
        searchlayout.removeAllViews();
        for (int j = 0; j < searchbytextList.size(); j++) {
            View imageChild = getLayoutInflater().inflate(R.layout.searchlayout, searchlayout, false);
            ImageView searchimage = imageChild.findViewById(R.id.searched_image);
            TextView name = imageChild.findViewById(R.id.nameview);
            name.setText(organizationname);
            Picasso.get()
                    .load(searchbytextList.get(j).getImageurl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(searchimage);
            searchlayout.addView(imageChild);
        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void checkTheResponseOne(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("response");
            Logger.logD("checkStatus", "status" + getStatus);
            if (getStatus == 1) {
                try {
                    parceResponseOne(response);
                    errortext.setVisibility(View.GONE);
                    searchbytextList.clear();
                    hideSoftKeyboard(SearchActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    errortext.setVisibility(View.VISIBLE);

                    hotsearchlayout.removeAllViews();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                searchbytextList.clear();
                Toast.makeText(SearchActivity.this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parceResponseOne(String Response) throws Exception {
        JSONObject jsonObject = new JSONObject(Response);
        String organizationname = String.valueOf(jsonObject.get("organization"));
        JSONArray jsonArray = jsonObject.getJSONArray("urls");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            SearchModelclass searchModelclass = new SearchModelclass(jsonObject1.getInt("fileId"), jsonObject1.getString("filename"), jsonObject1.getString("url"));
            searchbytextList.add(searchModelclass);
        }
        searchlayout = (LinearLayout) findViewById(R.id.searched_layout);
        searchlayout.removeAllViews();
        for (int j = 0; j < searchbytextList.size(); j++) {
            View imageChild = getLayoutInflater().inflate(R.layout.searchlayout, searchlayout, false);
            ImageView searchimage = imageChild.findViewById(R.id.searched_image);
            TextView name = imageChild.findViewById(R.id.nameview);
            name.setText(organizationname);
            Picasso.get()
                    .load(searchbytextList.get(j).getImageurl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(searchimage);
            searchlayout.addView(imageChild);
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
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}






