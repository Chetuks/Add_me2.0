package neutrinos.addme.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import neutrinos.addme.utilities.Logger;
import neutrinos.addme.ModelClass.DetailsListCategory;
import neutrinos.addme.R;
import neutrinos.addme.adapter.GridcategoryAdapter;

public class DetailsCategoryListActivity extends AppCompatActivity {

    private static final String TAG ="DetailsCategoryListActivity" ;
    private String id;
    Activity activity;
    private RecyclerView subBRecyclerView;
    private GridcategoryAdapter adapter;
    private String organization_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_category_list);
        subBRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        subBRecyclerView.setHasFixedSize(true);
        activity=DetailsCategoryListActivity.this;
        gettingBundle();
        callAPIForGetDetailsCategoryList();

    }

    /**
     * Volly Library to get Detail category List From SErver
     */
    private void callAPIForGetDetailsCategoryList() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Updating List........");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.useractivities/get-all-main-category-of-organiations/vocabulary-id/"+id+"/vocabulary-name/childcategory/appuniqueid/20828/organizationid/"+organization_id+"/language-name/english";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Logger.logV("Getting", " Details CVategory LIsting Response " + response);
                        parseDetailsCategoryLIstResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Logger.logV("Location", " ERROR " + error);

                    }
                }
        );
        Volley.newRequestQueue(this).add(postRequest);
    }

    /**
     * @param response getting from server fro Details list
     */
    private void parseDetailsCategoryLIstResponse(String response) {
        //1. Convert object to JSON string
        Gson gson = new Gson();
        // Convert JSON to Java Object
        DetailsListCategory detailsListCategory = gson.fromJson(response, DetailsListCategory.class);

        Logger.logD("responsedetails",""+response);
        Logger.logD("details",""+detailsListCategory.getCategories());
        Logger.logD("Status",""+detailsListCategory.getStatus());
        Logger.logD("Response message",""+detailsListCategory.getMessage());
        Logger.logD("Response LIst Size",""+detailsListCategory.getCategories().size());
        if(detailsListCategory.getStatus()==2){
            settingAdapter(detailsListCategory);

        }else{
            Toast.makeText(activity,"List is empty",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param detailsListCategory getting detailsListCategory bean class frm server .
     */
    private void settingAdapter(DetailsListCategory detailsListCategory) {
        if (!detailsListCategory.getCategories().isEmpty()){
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            subBRecyclerView.setLayoutManager(layoutManager);
            //adapter = new GridcategoryAdapter(activity, detailsListCategory);
            subBRecyclerView.setAdapter(adapter);
            subBRecyclerView.setHasFixedSize(true);
        }

    }


    /**
     * This methos is for getting bundle from intent
     */
    private void gettingBundle() {
        try{
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                id = bundle.getString("id");
                organization_id = bundle.getString("organization_id");

            }
            Logger.logD("id", "id" + id);
        }catch (Exception e){
            Logger.logE(TAG,"Exception in parcing the bundle",e);
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
           onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
