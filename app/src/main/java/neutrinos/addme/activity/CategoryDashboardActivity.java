package neutrinos.addme.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


import neutrinos.addme.ModelClass.CategoryModelClass;
import neutrinos.addme.ModelClass.SortedModelclass;
import neutrinos.addme.R;
import neutrinos.addme.adapter.SecondCategoryAdapter;
import neutrinos.addme.adapter.SubCategoryAdapter;
import neutrinos.addme.adapter.ViewpagerAdapter;
import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.fragment.PremiumModelClass;
import neutrinos.addme.utilities.GPSTracker;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.utilities.MyCurrentLocationTracker;
import neutrinos.addme.utilities.Utils;

import static neutrinos.addme.utilities.Utils.getDeviceName;

public class CategoryDashboardActivity extends AppCompatActivity {
    private ArrayList<CategoryModelClass> mAll = new ArrayList<>();
    private ArrayList<SortedModelclass> sidecategoryList = new ArrayList<>();
    private ArrayList<PremiumModelClass> categorypremiumList = new ArrayList<>();
    private ArrayList<GridModelClass> categorygridlist = new ArrayList<>();
    ViewPager viewPager;
    RecyclerView recyclerView;
    RecyclerView recyclermainpage;
    ListView listView;
    SubCategoryAdapter recycleviewAdapter;
    List<CategoryModelClass> myCategoryModelClassList = new ArrayList<>();
    LinearLayout linearLayout;
    private GPSTracker gpsTracker;
    Bundle bundle;
    String id;
    Context context;
    private RecyclerView recyclersecondView;
    //private GridcategoryAdapter adapter;
    private SecondCategoryAdapter adapter;
    GridLayoutManager layoutManagergrid;
    ProgressBar spinner;
    TextView organizationname;
    private String organisationId = "";
    int selectedposition = 0;
    LinearLayout orgname_layout_category;
    RelativeLayout categorydashboard_all_layout ;
    LinearLayout errorcontent ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorydashboard_main);
        organizationname = findViewById(R.id.organiz_category_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = CategoryDashboardActivity.this;
        initRecyclerview();
        viewPager = findViewById(R.id.view_pager_category);
        orgname_layout_category = findViewById(R.id.orgname_layout_category);
        categorydashboard_all_layout=findViewById(R.id.categorydashboard_all_layout);
        errorcontent = findViewById(R.id.errorcontent);
        bundle = getIntent().getExtras();
        assert bundle != null;
        id = bundle.getString("id");
        Logger.logD("idgetting", "" + id);
        linearLayout = findViewById(R.id.search_layout);
        gpsTracker = new GPSTracker(this);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.bringToFront();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        spinner.setVisibility(View.VISIBLE);

        orgname_layout_category.setVisibility(View.GONE);
        callServer();
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryDashboardActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * calling api to get the data
     */
    private void callServer() {
        if (Utils.checkNetworkConnection(this)) {
            String deviceId = getDeviceId();
            String location = setLocation();
            String deviceName = getDeviceName();
            categorypremiumContentsApi("test@liferay.com", "test",
                    deviceId, location, deviceName);
            callCategoryApiFromServer(id);
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            spinner.setVisibility(View.GONE);
        }
    }

    /**
     * @param usernameString
     * @param passwordString
     * @param deviceId
     * @param location
     * @param deviceName     Calling api to get the category premium contents
     */
    private void categorypremiumContentsApi(final String usernameString, final String passwordString,
                                            String deviceId, String location, String deviceName) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.device/save-and-update-device-details-remotely/device-address/"
                + deviceId + "/device-location/" + location + "/device-status/Activated/device-name/"
                + deviceName + "/appunique-id/54752/num/1";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("premium", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("categorypremiumrespo", " Response " + response);
                        categorypremiumResponse(response);
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
                String credentials = usernameString + ":" + passwordString;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(Objects.requireNonNull(CategoryDashboardActivity.this)).add(postRequest);
    }

    /**
     * @param response checking the response of premium contents api
     */
    private void categorypremiumResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("response");
            Logger.logD("checkStatuspremium", "status" + getStatus);
            if (getStatus == 2) {
                try {
                    parseCategoryPremium(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param response
     * @throws Exception parse the response of category premium response
     */
    private void parseCategoryPremium(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject jsonObject1 = jsonObject.getJSONObject("categorypremium");
        JSONArray jsonArray = jsonObject1.getJSONArray("urls");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            PremiumModelClass premiumModelClass = new PremiumModelClass(jsonObject2.getString("filename"),
                    jsonObject2.getString("filetype"), jsonObject2.getString("url"));
            categorypremiumList.add(premiumModelClass);
        }
        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(CategoryDashboardActivity.this, categorypremiumList);
        viewPager.setAdapter(viewpagerAdapter);
        try {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new SliderTimer(), 1000, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SliderTimer extends TimerTask {
        @Override
        public void run() {
            (CategoryDashboardActivity.this).runOnUiThread(new Runnable() {

                @Override

                public void run() {
                    if (viewPager.getCurrentItem() < categorypremiumList.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }

            });
        }
    }


    /**
     * @param parentcategoryid calling the api for getting the contents of organization(side content)
     * @param insideID
     */
    private void callOrganizationapi(final String parentcategoryid, final String insideID) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-all-main-category-of-organiations/vocabulary-id/"
                + parentcategoryid + "/vocabulary-name/categoryorganization/appuniqueid/54752/organizationid/0/language-name/english";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("sidecategorycheck", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("sortedresponsecategory", " Response " + response);
                        sidecategoryList.clear();
                        checkTheResponseofOrganizationapi(response, parentcategoryid, insideID);
                        //spinner.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Logger.logV("Location", " ERROR " + error);
                    }
                }
        );
        Volley.newRequestQueue(this).add(postRequest);
    }

    /**
     * @param id calling api to get the contents of category page contents
     */
    private void callCategoryApiFromServer(String id) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-all-main-category-of-organiations/vocabulary-id/"
                + id + "/vocabulary-name/subOrganization/appuniqueid/54752/language-name/english";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("secondcategory", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("Categorypageresponse", " Response " + response);
                        checkTheResponseOne(response);
                        //spinner.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Logger.logV("Location", " ERROR " + error);
                    }
                }
        );
        Volley.newRequestQueue(this).add(postRequest);
    }

    /**
     * @param response
     * @param parentcategoryid checking the response of organization api
     * @param insideID
     */
    private void checkTheResponseofOrganizationapi(String response, String parentcategoryid, String insideID) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("status");
            Logger.logD("checkStatus", "status" + getStatus);
            if (getStatus == 2) {
                try {
                    parceResponseofOrganizationapi(response, parentcategoryid, insideID);
                    errorcontent.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
                spinner.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                errorcontent.setVisibility(View.VISIBLE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param response
     * @param parentcategoryid
     * @param insidenewid
     * @throws Exception parsing the response of Organization api
     */
    private void parceResponseofOrganizationapi(final String response, final String parentcategoryid, final String insidenewid) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("sub_organization_shops");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            SortedModelclass sortedModelclass = new SortedModelclass(String.valueOf(jsonObject1.getInt("organizationid")),
                    jsonObject1.getString("organization_name"), jsonObject1.getString("image"), true, jsonObject1.getBoolean("favourite"));
            sidecategoryList.add(sortedModelclass);
        }
        final LinearLayout sidecategorycontent = (LinearLayout) findViewById(R.id.side_layout);
        sidecategorycontent.removeAllViews();
        String orgid = null;
        final LikeButton favbtn = findViewById(R.id.category_fav_btn);

        for (int j = 0; j < sidecategoryList.size(); j++) {
            Logger.logD("listsize", "" + sidecategoryList.size());
            View imageChild = getLayoutInflater().inflate(R.layout.adapter_side, sidecategorycontent, false);
            ImageView circleImageView = imageChild.findViewById(R.id.image_view_adapter_id);
            TextView name = imageChild.findViewById(R.id.name);
            name.setText(sidecategoryList.get(j).getCategoryname());
            Picasso.get()
                    .load(getResources().getString(R.string.test) + sidecategoryList.get(j).getImage())
                    .fit()
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(circleImageView);
            sidecategorycontent.addView(imageChild);

            orgid = sidecategoryList.get(j).getSortedid();

            Logger.logD("orgin", "" + orgid);
            Logger.logD("checkingorgid", "" + sidecategoryList.get(j).getOrganizationid());
            final String finalOrgid1 = orgid;
            if (sidecategoryList.get(j).isCheck()) {
                favbtn.setLiked(true);
            } else {
                favbtn.setLiked(false);
            }
            final int finalJ = j;

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        selectedposition = finalJ;
                        organisationId = sidecategoryList.get(selectedposition).getOrganizationid();
                        organizationname.setText(sidecategoryList.get(finalJ).getCategoryname());
                        spinner.setVisibility(View.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                      /*  if (sidecategoryList.get(selectedposition).isCheck() == 1) {
                            favbtn.setLiked(true);
                        } else {
                            favbtn.setLiked(false);
                        }*/
                        categorygridlist.clear();
                        if (layoutManagergrid != null) {
                            layoutManagergrid.removeAllViews();
                        }
                        callserverforgridcontents("test@liferay.com", "test", insidenewid, finalOrgid1, getDeviceId(),"");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        categorydashboard_all_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callserverforgridcontents("test@liferay.com", "test", "0", sidecategoryList.get(0).getSortedid(), getDeviceId(),"");
            }
        });
        //TODO Need to handle emplty listselectedposition
        if (!sidecategoryList.isEmpty()) {
            categorygridlist.clear();
            //callserverforgridcontents("test@liferay.com", "test", "0", sidecategoryList.get(0).getSortedid(), getDeviceId(),"allforsecondpage");
            callserverforgridcontents("test@liferay.com", "test", insidenewid, sidecategoryList.get(0).getSortedid(), getDeviceId(),"allforsecondpage");
            organizationname.setText(sidecategoryList.get(0).getCategoryname());
            //
            organisationId = sidecategoryList.get(0).getOrganizationid();
           /* if (sidecategoryList.get(0).getIsFav() == 1)
                Logger.logD("", "fav clicked");
            else
                Logger.logD("", "fav not clicked");*/
            favbtn.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    setUserListToServer(sidecategoryList.get(selectedposition), 100);
                    // sidecategoryList.get(selectedposition).setIsFav(1);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    setUserListToServer(sidecategoryList.get(selectedposition), 200);
                    //sidecategoryList.get(selectedposition).setIsFav(2);
                }
            });
        } else {
            Toast.makeText(CategoryDashboardActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * @param gridlist
     * @param flag     if 100 calling favourite, 200 calling wishlist
     */
    private void setUserListToServer(SortedModelclass gridlist, int flag) {
        if (gridlist != null) {
            try {
                JSONArray array = new JSONArray();
                JSONObject item = new JSONObject();
                item.put("organization", gridlist.getSortedid());
                array.put(item);
                Log.d("jsonarray", String.valueOf(array));
                if (flag == 100)
                    callFavouriteapi("test@liferay.com", "test", array, getDeviceId(), "save");
                else
                    callFavouriteapi("test@liferay.com", "test", array, getDeviceId(), "delete");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(CategoryDashboardActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void callFavouriteapi(final String s, final String test, JSONArray jsonStructure,
                                  String deviceId, final String apiFlag) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.user_favourite/User-favourites/uniquename/"
                + jsonStructure + "/deviceaddress/" + deviceId + "/status/" + apiFlag + "/key/favourites";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("favapi", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("favresponse", " Response -->" + apiFlag + response);
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
        Volley.newRequestQueue(CategoryDashboardActivity.this).add(postRequest);
    }

    /**
     * @param s
     * @param test
     * @param id
     * @param organizationID
     * @param deviceId       calling api to get the contents for grid contents
     */
    private void callserverforgridcontents(final String s, final String test, String id, String organizationID, String deviceId,String allforsecondpage) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.useractivities/get-contents-based-on-app-selection/parentcategoryid/"
                + id + "/organizationid/" + organizationID + "/appuniqueid/54752/pageno/1/macaddress/"+deviceId;
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("gridsecond", " callServerToSendParams " + url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("gridsecondresponse", " Response " + response);
                        checkResponseGrid(response);
                        spinner.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        Volley.newRequestQueue(CategoryDashboardActivity.this).add(postRequest);
    }

    /**
     * @param response checking the response for grid contents
     */
    private void checkResponseGrid(String response) {


        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("Status");
            Logger.logD("checkStatus", "response" + getStatus);
            if (getStatus == 2) {
                try {
                    categorygridlist.clear();
                    parceResponseGrid(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                categorygridlist.clear();
                try {
                    layoutManagergrid.removeAllViews();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(CategoryDashboardActivity.this, "No data in the server", Toast.LENGTH_SHORT).show();
                orgname_layout_category.setVisibility(View.GONE);

                errorcontent.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param response
     * @throws Exception parsing the response for grid api
     */
    private void parceResponseGrid(String response) throws Exception {
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
                categorygridlist.add(gridModelClass);
            } else {
                Toast.makeText(CategoryDashboardActivity.this, "Status no response from server", Toast.LENGTH_SHORT).show();
            }
        }
        settingAdapter(categorygridlist);
        orgname_layout_category.setVisibility(View.VISIBLE);
    }

    /**
     * @param categorygridlist setting adapter class for the grid contents
     */
    private void settingAdapter(ArrayList<GridModelClass> categorygridlist) {
        if (!categorygridlist.isEmpty()) {
            layoutManagergrid = new GridLayoutManager(this, 2);
            recyclersecondView.setLayoutManager(layoutManagergrid);
            adapter = new SecondCategoryAdapter(CategoryDashboardActivity.this, categorygridlist);
            recyclersecondView.setAdapter(adapter);
            recyclersecondView.setHasFixedSize(true);
        }
    }

    /**
     * @param response checking the response for the category contents
     */
    private void checkTheResponseOne(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("status");
            Logger.logD("checkStatus", "status" + getStatus);
            if (getStatus == 2) {
                try {
                    parceResponseOne(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param Response
     * @throws Exception parse the response for the category response
     */
    private void parceResponseOne(String Response) throws Exception {
        JSONObject jsonObject = new JSONObject(Response);
        final int parentcategoryid = jsonObject.getInt("parentcategoryid");
        Logger.logD("parentcategoryid", "" + parentcategoryid);
        JSONArray jsonArray = jsonObject.getJSONArray("sub_organization");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            CategoryModelClass categoryModelClass = new CategoryModelClass(String.valueOf(jsonObject1.getInt("id")),
                    jsonObject1.getString("category_name"), jsonObject1.getString("image"));
            mAll.add(categoryModelClass);
          /*  Sortedadapter sortedadapter = new Sortedadapter(CategoryDashboardActivity.this, sortedcategorylist);
            ListView recyclerView = findViewById(R.id.recycle_view);
            recyclerView.setAdapter(sortedadapter);*/
        }
        LinearLayout commentsDynamicLayout = findViewById(R.id.category_content);
        commentsDynamicLayout.removeAllViews();
        for (int j = 0; j < mAll.size(); j++) {
            Logger.logD("listsize", "" + mAll.size());
            View imageChild = getLayoutInflater().inflate(R.layout.layout_listitem, commentsDynamicLayout, false);
            ImageView circleImageView = imageChild.findViewById(R.id.circle_image);
            TextView name = imageChild.findViewById(R.id.circle_text);
            name.setText(mAll.get(j).getName());
            Picasso.get()
                    .load(getResources().getString(R.string.test) + mAll.get(j).getImage())
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(circleImageView);
            commentsDynamicLayout.addView(imageChild);
            final int finalJ = j;
            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callOrganizationapi(String.valueOf(parentcategoryid), String.valueOf(mAll.get(finalJ).getId()));
                    Logger.logD("idddddd", "" + String.valueOf(mAll.get(finalJ).getId()));
                }
            });
        }
        callOrganizationapi(String.valueOf(parentcategoryid), String.valueOf(mAll.get(0).getId()));


    }

    private void initRecyclerview() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclersecondView = findViewById(R.id.side_right);
    }

    /**
     * @return to get the device location latitude longitude
     */
    public String setLocation() {
        String stringLatitude = "0.0";
        String stringLongitude = "0.0";
        if (Double.doubleToRawLongBits(gpsTracker.latitude) == 0 || Double.doubleToRawLongBits(gpsTracker.longitude) == 0) {
            MyCurrentLocationTracker tracker = new MyCurrentLocationTracker(this, null, null);
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
        return Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
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
