package neutrinos.addme.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neutrinos.addme.ModelClass.ModelClassChecklist;
import neutrinos.addme.R;
import neutrinos.addme.adapter.myCheckListAdapter;
import neutrinos.addme.databaseclass.DatabaseHelper;
import neutrinos.addme.utilities.Logger;

public class Checklist extends AppCompatActivity {
    DatabaseHelper myDb;
    private ListView myListView;
    List<ModelClassChecklist> MymainCheckList = new ArrayList<>();
   // public neutrinos.addme.adapter.myCheckListAdapter  myCheckListAdapter;
    private Button savecheckList;
    private Button createButton;
    public static HashMap<String, ModelClassChecklist> sendUncheckedList = new HashMap<>();

    Cursor cursor;
    List<String> fetchUUID;
    List<ModelClassChecklist> getDatabaseList;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_list_main);
        myDb = new DatabaseHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        String getDeviceID = getDeviceId();
        //if (!Utils.checkNetworkConnection(Checklist.this)) {
            getDatabaseList = myDb.getDatadb();
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < getDatabaseList.size(); i++) {
                try {
                    JSONObject item = new JSONObject();
                    Logger.logD("dabretrieve", "" + getDatabaseList.get(i).getName());
                    item.put("item_uuid", getDatabaseList.get(i).getId());
                    item.put("item_name", getDatabaseList.get(i).getName());
                    item.put("item_quantity", getDatabaseList.get(i).getQuantity());
                    //item.put("item_quantity_spinner", myMainList.get(i).getQuantitySpinser());
                    item.put("created_date", getDatabaseList.get(i).getModifiedDate());
                    Logger.logD("databasedate",""+ getDatabaseList.get(i).getModifiedDate());
                    item.put("done", "true");
                    array.put(item);
                    jsonObject.put("items", array);
                    Log.d("jsonObject", jsonObject.toString());
                    callServerToSendParams(jsonObject.toString(), getDeviceId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setAdapter(getDatabaseList);
            myDb.close();
        //}
        /* else {
            getDatabaseList = myDb.getDatadb();
            //Logger.logD("dabretrieve", "" + getDatabaseList.get(i).getName());
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (int i = 0; i < getDatabaseList.size(); i++) {
                try {
                    JSONObject item = new JSONObject();
                    Logger.logD("dabretrieve", "" + getDatabaseList.get(i).getName());
                    item.put("item_uuid", getDatabaseList.get(i).getId());
                    item.put("item_name", getDatabaseList.get(i).getName());
                    item.put("item_quantity", getDatabaseList.get(i).getQuantity());
                    //item.put("item_quantity_spinner", myMainList.get(i).getQuantitySpinser());
                    item.put("created_date", getDatabaseList.get(i).getModifiedDate());
                    Logger.logD("databasedate",""+ getDatabaseList.get(i).getModifiedDate());
                    item.put("done", "true");
                    array.put(item);
                    jsonObject.put("items", array);
                    Log.d("jsonObject", jsonObject.toString());
                    callServerToSendParams(jsonObject.toString(), getDeviceId());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }*/
        //callServerToGetCheckList(getDeviceID);
        savecheckList.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (!sendUncheckedList.isEmpty()) {
                    try {
                        createJsonStructure();
                        for (int j = 0; j < fetchUUID.size(); j++) {
                            myDb.deletePreviousRecord(fetchUUID.get(j));
                        }
                        startActivity(getIntent());
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Checklist.this, "Uncheck an item to delete from list", Toast.LENGTH_SHORT).show();
                }
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Checklist.this, NewCheckListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void callServerToSendParams(String jsonStructure, String macAddress) {
        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.check_list/Store-check-list/array/" + jsonStructure + "/macaddress/" + macAddress + "/appuniqueid/20825/status/save";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //count = 0;
                        Logger.logV("checklistresp", " Response " + response);
                        checkTheResponsetodo(response);
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
                String credentials = "test1@liferay.com" + ":" + "test1";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    private void checkTheResponsetodo(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int getStatus = jsonObject.getInt("Status");
            if (getStatus == 2) {

            } else {
                Toast.makeText(this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * @throws JSONException creating json for unchecked list
     */
    private void createJsonStructure() throws JSONException {
        fetchUUID = new ArrayList<>();
        Set keys = sendUncheckedList.keySet();
        JSONArray ja = new JSONArray();
        for (Iterator i = keys.iterator(); i.hasNext(); ) {
            JSONObject jo = new JSONObject();

            String key = (String) i.next();
            String value = (String) sendUncheckedList.get(key).getId();
            jo.put("item_uuid", sendUncheckedList.get(key).getId());
            fetchUUID.add(sendUncheckedList.get(key).getId());
            Logger.logD("uncheckuuid", "" + sendUncheckedList.get(key).getId());
            ja.put(jo);
            Logger.logD("valuelist", "" + value);
        }
        JSONObject mainObj = new JSONObject();
        mainObj.put("items", ja);
        Logger.logD("ShowJson", mainObj.toString());
        Logger.logD("ShowJsonmain", mainObj.toString());

        callApiToDeleteCheckList(mainObj, getDeviceId());
    }

    /**
     * @param mainObj  calling api for deleting the selected  unchecked items
     * @param deviceID
     */
    private void callApiToDeleteCheckList(JSONObject mainObj, String deviceID) {
        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.check_list/Store-check-list/array/" + mainObj + "/macaddress/" + deviceID + "/appuniqueid/20825/status/delete";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " convertedURL-delete " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //count = 0;
                        Logger.logV("Location", "get deleted response " + response);
                        parceResponse(response);
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
                String credentials = "test1@liferay.com" + ":" + "test1";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    /**
     * @param response getting response from delete api and parsing it
     */
    private void parceResponse(String response) {
        try {
            //Delete
            for (int j = 0; j < fetchUUID.size(); j++) {
                myDb.deletePreviousRecord(fetchUUID.get(j));
            }
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString("Status").equals("2")) {
                MymainCheckList.clear();
                String getDeviceID = getDeviceId();
                callServerToGetCheckList(getDeviceID);
            } else {
                Toast.makeText(this, "Update", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * setting data to checklist adapter
     *
     * @param getDatabaseList
     */
    private void setAdapter(List<ModelClassChecklist> getDatabaseList) {
        myCheckListAdapter myCheckListAdapter = new myCheckListAdapter(this, getDatabaseList);
        myListView.setAdapter(myCheckListAdapter);
    }

    private void setAdapter() {
        myCheckListAdapter myCheckListAdapter = new myCheckListAdapter(this, MymainCheckList);
        myListView.setAdapter(myCheckListAdapter);
    }

    private void callServerToGetCheckList(String getMAC) {
        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.check_list/Store-check-list/array/{}/macaddress/" + getMAC + "/appuniqueid/20825/status/retrieve";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " convertedURL-retrieve " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //count = 0;
                        Logger.logV("Location", "Check List Response " + response);
                        checkTheResponse(response);

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
                String credentials = "test1@liferay.com" + ":" + "test1";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }


    private void checkTheResponse(String response) {
        if (response != null) {
            try {
                String dateformat= "";
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                   /* boolean inserted = myDb.insertData(jsonObject.getString("item_name"), jsonObject.getString("item_quantity"), jsonObject.getString("created_date"), jsonObject.getString("item_uuid"));
                    if (inserted) {
                        //Toast.makeText(Checklist.this, "Data inserted", Toast.LENGTH_SHORT).show();
*/

                        ModelClassChecklist modelClassChecklist = new ModelClassChecklist();
                        modelClassChecklist.setModifiedDate(jsonObject.getString("created_date"));
                        modelClassChecklist.setId(jsonObject.getString("item_uuid"));
                        modelClassChecklist.setQuantity(jsonObject.getString("item_quantity"));
                        modelClassChecklist.setName(jsonObject.getString("item_name"));
                        modelClassChecklist.setCheckstatus(true);
                        MymainCheckList.add(modelClassChecklist);
                        setAdapter();
                    //} else {
                       // Toast.makeText(Checklist.this, "Data not inserted", Toast.LENGTH_LONG).show();
                   // }

                }
                Log.d("MymainCheckList", MymainCheckList.size() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private void init() {
        myListView = (ListView) findViewById(R.id.check_list_id);
        savecheckList = (Button) findViewById(R.id.list_save);
        createButton = (Button) findViewById(R.id.create_btn);
    }

    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(getContentResolver(),
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
