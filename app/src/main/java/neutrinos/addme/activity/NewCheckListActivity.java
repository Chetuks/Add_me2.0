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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import neutrinos.addme.adapter.ChecklistAdapter;
import neutrinos.addme.databaseclass.DatabaseHelper;
import neutrinos.addme.Interface.ChecklistInterface;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.ModelClass.ModelClassChecklist;
import neutrinos.addme.R;
import neutrinos.addme.ModelClass.ToDoList;
import neutrinos.addme.utilities.Utils;

public class NewCheckListActivity extends AppCompatActivity implements ChecklistInterface {
    public ListView myListView;
    public EditText nameEdittext;
    CheckBox checkBox;
    List<ModelClassChecklist> modelClassList = new ArrayList<>();
    private String uniqueId;
    private String name;
    private CheckBox abcd;
    private Spinner spinner;
    private ChecklistAdapter checklistAdapter;
    private Button removeBtn;
    private EditText qtyItem;
    List<ToDoList> myMainList = new ArrayList<>();
    private Spinner qtySpinner;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newchecklist_activity_main);
        myDb = new DatabaseHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.logD("namee", "" + myListView.getAdapter());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * initializing fields
     */
    public void init() {
        nameEdittext = findViewById(R.id.name_id);
        myListView = findViewById(R.id.checked_list_id);
        qtyItem = findViewById(R.id.item_qty);
        qtySpinner = findViewById(R.id.spinner_checklist);
        checkBox = findViewById(R.id.chkbox_id);
        Button b1 = findViewById(R.id.rmv_btn);
        spinner = (Spinner) findViewById(R.id.spinner_checklist);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.checkNetworkConnection(NewCheckListActivity.this)) {
                    if (!modelClassList.isEmpty()) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            JSONArray array = new JSONArray();
                            for (int i = 0; i < modelClassList.size(); i++) {
                                JSONObject item = new JSONObject();
                                item.put("item_uuid", modelClassList.get(i).getId());
                                item.put("item_name", modelClassList.get(i).getName());
                                item.put("item_quantity", modelClassList.get(i).getQuantity() + " " + modelClassList.get(i).getQuantitySpinser());
                                //item.put("item_quantity_spinner", myMainList.get(i).getQuantitySpinser());
                                item.put("created_date", modelClassList.get(i).getModifiedDate());
                                item.put("done", "true");
                                array.put(item);
                                boolean inserted = myDb.insertData(modelClassList.get(i).getName(), modelClassList.get(i).getQuantity() + " " + modelClassList.get(i).getQuantitySpinser(), modelClassList.get(i).getModifiedDate(), modelClassList.get(i).getId());
                                if (inserted) {
                                    Toast.makeText(NewCheckListActivity.this, "Data inserted to table", Toast.LENGTH_SHORT).show();
                                }
                            }
                            jsonObject.put("items", array);
                            Log.d("jsonObject", jsonObject.toString());
                            callServerToSendParams(jsonObject.toString(), getDeviceId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(NewCheckListActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(NewCheckListActivity.this, Checklist.class);
                    startActivity(intent);
                    finish();
                } else {
                    setUserListToServer(modelClassList);
                }

            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checklist();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdittext.setText("");
                qtyItem.setText("");
            }
        });

        List<String> mySpinnerList = new ArrayList<String>();
        mySpinnerList.add("Kg");
        mySpinnerList.add("litres");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mySpinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


    }

    private void setUserListToServer(List<ModelClassChecklist> myMainList) {
        if (!myMainList.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray array = new JSONArray();
                for (int i = 0; i < myMainList.size(); i++) {
                    JSONObject item = new JSONObject();
                    item.put("item_uuid", myMainList.get(i).getId());
                    item.put("item_name", myMainList.get(i).getName());
                    item.put("item_quantity", myMainList.get(i).getQuantity() + " " + myMainList.get(i).getQuantitySpinser());
                    //item.put("item_quantity_spinner", myMainList.get(i).getQuantitySpinser());
                    item.put("created_date", myMainList.get(i).getModifiedDate());
                    item.put("done", "true");
                    array.put(item);
                    boolean inserted = myDb.insertData(myMainList.get(i).getName(), myMainList.get(i).getQuantity() + " " + myMainList.get(i).getQuantitySpinser(), myMainList.get(i).getModifiedDate(), myMainList.get(i).getId());
                    if (inserted) {
                        Toast.makeText(NewCheckListActivity.this, "Data inserted to table", Toast.LENGTH_SHORT).show();
                    }
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
                        Logger.logV("checklistresp", " Response " + response);
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
        return Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private void checklist() {
        if (checkBox.isChecked()) {
            uniqueId = UUID.randomUUID().toString();
            Boolean namecheck = checkingText(nameEdittext.getText().toString());
            if (namecheck) {
                name = nameEdittext.getText().toString();
                Logger.logD("namecehckshfdfgdf", "" + name);
            } else {
                try {
                    name = "";
                    name = callUrlAndParseResult("ar", "en", nameEdittext.getText().toString());
                    Logger.logD("namecehckshf", "" + name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //name = nameEdittext.getText().toString();
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Logger.logD("datechecksd", "" + df);
            String formattedDate = df.format(c);
            Logger.logD("datechecksdasf", "" + formattedDate);
            formattedDate = formattedDate.substring(0, formattedDate.length() - 1);
            String newdate = /*"01-06-2018 16:15:30"*/dateformattoarabic(formattedDate);
            newdate = newdate;
            Logger.logD("datecheckarabic", "" + newdate);
            Logger.logD("datecheck", "" + formattedDate);
            if (validation()) {
                ModelClassChecklist modelClass = new ModelClassChecklist(uniqueId, name, true, qtyItem.getText().toString(), spinner.getSelectedItem().toString(), newdate);
                modelClassList.add(modelClass);

                nameEdittext.setText("");
                qtyItem.setText("");
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(false);

            }
        } else {
            Toast.makeText(this, "enter details", Toast.LENGTH_SHORT).show();
        }
        checklistAdapter = new ChecklistAdapter(NewCheckListActivity.this, modelClassList, NewCheckListActivity.this);
        myListView.setAdapter(checklistAdapter);
    }


    private String callUrlAndParseResult(String langFrom, String langTo,
                                         String word) throws Exception {
        final String[] responseName = new String[1];
        Logger.logD("wordcheck", "" + word);
        final String[] url = {"https://translate.googleapis.com/translate_a/single?" +
                "client=gtx&" +
                "sl=" + langFrom +
                "&tl=" + langTo +
                "&dt=t&q=" + URLEncoder.encode(word, "UTF-8")};
        Logger.logD("URLname", "" + url[0]);
       /* URL obj = new URL(url);

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();*/
        Log.v("the result of like", "the result is" + url[0]);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url[0],
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // ProgressUtils.CancelProgress(progressDialog);
                        try {


                            responseName[0] = parseResult(response.toString());
                            Logger.logD("nameresonce", "->" + response);
                            Logger.logD("nameresonce", "->" + responseName[0]);
                            //parceAndSetCommentAdapter(response);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.v("the result of like", "the result is" + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        Volley.newRequestQueue(NewCheckListActivity.this).add(postRequest);

        return parseResult(responseName[0]);
    }

    private String parseResult(String inputJson) throws Exception {
        /*
         * inputJson for word 'hello' translated to language Hindi from English-
         * [[["नमस्ते","hello",,,1]],,"en"]
         * We have to get 'नमस्ते ' from this json.
         */
        JSONArray jsonArray = new JSONArray(inputJson);
        JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
        JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);
        return jsonArray3.get(0).toString();
    }

    private boolean checkingText(String s) {
       /* for (int i = 0; i != s.length(); ++i) {
            if (!Character.isLetter(s.charAt(i))) {
                return false;
            }
        }
        return true;*/
        char[] ch = s.toCharArray();
        int flag = 0;
        for (int i = 0; i != ch.length; i++) {
            if ((ch[i] >= 'a' && ch[i] <= 'z') || (ch[i] >= 'A' && ch[i] <= 'Z')) {
                System.out.print(ch + " is an Alphabet");
            } else {
                flag = 1;
            }
            if (flag == 1) {
                return false;
            }
/*
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;*/
        }
        return true;
    }

    private String dateformattoarabic(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }


    private boolean validation() {
        if (nameEdittext.getText().toString().equals("")) {
            Toast.makeText(this, "Enter the item name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (qtyItem.getText().toString().equals("")) {
            Toast.makeText(this, "Enter the quantity", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
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

    @Override
    public void OurMethod(ModelClassChecklist modelClassChecklist, int modelClassPosition) {
        Logger.logD("Interface checking", modelClassChecklist.getName());
        Logger.logD("Interface checking", "position" + modelClassPosition);
        if (modelClassChecklist != null) {
            checkBox.setChecked(false);
            nameEdittext.setText(modelClassChecklist.getName());
            qtyItem.setText(modelClassChecklist.getQuantity());
            modelClassList.remove(modelClassPosition);
            checklistAdapter = new ChecklistAdapter(NewCheckListActivity.this, modelClassList, NewCheckListActivity.this);
            myListView.setAdapter(checklistAdapter);
        }
    }
}
