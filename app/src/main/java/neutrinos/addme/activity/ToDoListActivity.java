package neutrinos.addme.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import neutrinos.addme.ModelClass.ToDoList;
import neutrinos.addme.R;
import neutrinos.addme.adapter.myItemAdapter;
import neutrinos.addme.utilities.Logger;

public class ToDoListActivity extends AppCompatActivity {


    private Spinner mySpinner;
    private EditText userEnterEditText;
    private EditText userEnterQTy;
    private Button addBtn;
    List<ToDoList> myMainList = new ArrayList<>();
    private myItemAdapter adapter;
    private ListView myListView;
    //private Context context;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        init();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterText = userEnterEditText.getText().toString();
                String enteredQTY = userEnterQTy.getText().toString() + mySpinner.getSelectedItem().toString();
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String formattedDate = df.format(c);
                formattedDate = formattedDate.substring(0, formattedDate.length() - 1);
                //  String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                String uniqueId = UUID.randomUUID().toString();
                Log.d("EnterText", enterText);
                Log.d("enteredQTY", enteredQTY);
                Log.d("currentDateTimeString", formattedDate);
                Log.d("uniqueId", uniqueId);
                boolean checkValidation = checkValidation();
                if (checkValidation) {
                    ToDoList toDoList = new ToDoList();
                    toDoList.setitemText(enterText);
                    toDoList.setItemQTy(enteredQTY);
                    //toDoList.setModifiedDate("10-03-2018 3:18:55");
                    toDoList.setModifiedDate(formattedDate);
                    toDoList.setUUID(uniqueId);
                    myMainList.add(toDoList);
                    Log.d("myMainList size", myMainList.size() + "");
                    clearAllTheFileds();
                    adapter.notifyDataSetChanged();

                }
            }
        });

        // Spinner Drop down elements
        List<String> mySpinnerList = new ArrayList<String>();
        mySpinnerList.add("kg");
        mySpinnerList.add("grams");
        mySpinnerList.add("litres");
        mySpinnerList.add("ml");
        mySpinnerList.add("count");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mySpinnerList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mySpinner.setAdapter(dataAdapter);


    }

    // TODO
    private void clearAllTheFileds() {
        userEnterEditText.setText("");
        userEnterQTy.setText("");
    }

    //TODO
    private boolean checkValidation() {
        if (userEnterEditText.getText().toString().isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.ItemName, Toast.LENGTH_SHORT).show();
            return false;
        } else if (userEnterQTy.getText().toString().isEmpty()) {
            Toast.makeText(getBaseContext(), R.string.ItemQuantity, Toast.LENGTH_SHORT).show();
            return false;
        } else if (mySpinner.toString().equals("")) {
            Toast.makeText(getBaseContext(), "Kg/Litre is mandatory", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void init() {
        mySpinner = (Spinner) findViewById(R.id.spinner_id);
        userEnterEditText = (EditText) findViewById(R.id.item_name);
        userEnterQTy = (EditText) findViewById(R.id.item_qty);
        addBtn = (Button) findViewById(R.id.addbtn);
        myListView = (ListView) findViewById(R.id.list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUserListToServer(myMainList);
            }
        });
        adapter = new myItemAdapter(this, myMainList);
        myListView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUserListToServer(List<ToDoList> myMainList) {
        if (!myMainList.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray array = new JSONArray();
                for (int i = 0; i < myMainList.size(); i++) {
                    JSONObject item = new JSONObject();
                    item.put("item_uuid", myMainList.get(i).getUUID());
                    item.put("item_name", myMainList.get(i).getitemText());
                    item.put("item_quantity", myMainList.get(i).getItemQTy());
                    item.put("created_date", myMainList.get(i).getModifiedDate());
                    item.put("done", "true");
                    array.put(item);
                }
                jsonObject.put("items", array);
                Log.d("jsonObject", jsonObject.toString());

                callServerToSendParams(jsonObject.toString(), getDeviceId());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "List is empty", Toast.LENGTH_SHORT).show();
        }

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
                        Logger.logV("Location", " Response " + response);
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
//        RetryPolicy retryPolicy = new Re
        Volley.newRequestQueue(this).add(postRequest);
    }

    private void checkTheResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int getStatus = jsonObject.getInt("Status");
            if (getStatus == 2) {
                Intent callintent = new Intent(this, Checklist.class);
                startActivity(callintent);
                finish();
            } else {
                Toast.makeText(this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
