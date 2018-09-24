package neutrinos.addme.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.ModelClass.CategoryModelClass;
import neutrinos.addme.R;
import neutrinos.addme.adapter.MySubBaseAdapter;
import neutrinos.addme.utilities.Logger;

public class SubCategoryListActivity extends AppCompatActivity {

    private ListView myListView;
    private static final String TAG = "SubCategoryListActivity";
    List<CategoryModelClass> myCategoryModelClassList = new ArrayList<>();
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("id");
        }
        Logger.logD("id", "id" + id);
        myListView = findViewById(R.id.sub_category_listview_id);
        callNearMeApiFromServer();
    }


    private void callNearMeApiFromServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Category List...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        Logger.logD("checkid", "" + id);
        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.useractivities/get-all-main-category-of-organiations/vocabulary-id/" + id + "/vocabulary-name/subOrganization/appuniqueid/20828/organizationid/0/language-name/english";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Logger.logV("LocationChetck", " Response " + response);
                        checkTheResponse(response);
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

    private void checkTheResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int getStatus = jsonObject.getInt("status");
            Logger.logD("checkStatus", "status" + getStatus);
            if (getStatus == 2) {
                try {
                    parceResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (getStatus == 0) {
                Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void parceResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("sub_organization");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                CategoryModelClass categoryModelClass = new CategoryModelClass(String.valueOf(jsonObject1.getInt("id")), jsonObject1.getString("sub_organization_name"), jsonObject1.getString("image"), jsonObject1.getString("sub_organization_description"), jsonObject1.getString("createdate"), jsonObject1.getString("appuniqueid"), String.valueOf(jsonObject1.getInt("organizationid")));
                myCategoryModelClassList.add(categoryModelClass);
            }
            MySubBaseAdapter mySubBaseAdapter = new MySubBaseAdapter(SubCategoryListActivity.this, myCategoryModelClassList, SubCategoryListActivity.this);
            myListView.setAdapter(mySubBaseAdapter);




            /*else{
            //TODO display one toast
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }*/


        } catch (JSONException e) {
            e.printStackTrace();
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