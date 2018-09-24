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
import neutrinos.addme.adapter.MyBaseAdapter;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.R;

public class CategoryListActivity extends AppCompatActivity {

    private static final String TAG = "CategoryListActivity";
    private ListView myListView;
    List<CategoryModelClass> myCategoryModelClassList = new ArrayList<>();
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Category List...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        callNearMeApiFromServer();
    }

    private void callNearMeApiFromServer() {
        myListView = findViewById(R.id.category_listview_id);

        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.useractivities/get-all-main-category-of-organiations/vocabulary-id/0/vocabulary-name/organizationCategory/appuniqueid/20828/organizationid/0/language-name/hindi";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Logger.logV("Category", " Response " + response);

                        checkTheResponse(response);
                        progressDialog.dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Logger.logV("Location", " ERROR " + error);
                        progressDialog.dismiss();

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
            } else {
                Toast.makeText(this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parceResponse(String Response) throws Exception {

        JSONObject jsonObject = new JSONObject(Response);
        JSONArray jsonArray = jsonObject.getJSONArray("organization");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
          //  CategoryModelClass categoryModelClass = new CategoryModelClass(String.valueOf(jsonObject1.getInt("id")), jsonObject1.getString("organization_name"), jsonObject1.getString("image"), jsonObject1.getString("created_date"), jsonObject1.getString("appuniqueid"), String.valueOf(jsonObject1.getInt("organizationid")));
            Logger.logD("organization_name", jsonObject1.getString("organization_name"));
           // myCategoryModelClassList.add(categoryModelClass);
        }

        MyBaseAdapter myBaseAdapter = new MyBaseAdapter(CategoryListActivity.this, myCategoryModelClassList, CategoryListActivity.this);
        myListView.setAdapter(myBaseAdapter);


    } /*else {
            //TODO display one toast
            Logger.logV("Response_first",Response);
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }*/

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



