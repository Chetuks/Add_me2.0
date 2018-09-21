package neutrinos.addme.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import neutrinos.addme.utilities.GPSTracker;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.R;
import neutrinos.addme.utilities.SetLocality;
import neutrinos.addme.utilities.SharedPrefManager;
import neutrinos.addme.utilities.Utils;

public class
RegistrationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "RegistrationApi";
    //@InjectView(R.id.personName)
    EditText userName;
    //TextInputLayout  personname_hint;
    private EditText phoneNumber;
    private TextView enterDetails;
    private EditText cityName;
    private Button btnRegistration;
    private Context context;
    private RadioButton maletxt;
    private RadioButton femaletxt;
    private TextView nationalitytxt;
    private EditText dobhint;
    private TextView selectgendertxt;
    int getSeletedLanguageId;
    private Spinner spinner;
    private Spinner nationalityspinner;
    String[] languagesList = {"Select language", "English", "عربى "};
    String[] nationalityList = {"Indian", "Qatar"};
    String[] nationalityListArabic = {"هندي", "دولة قطر"};
    SharedPrefManager prefManager;
    Configuration config;
    private String currentLanguage;
    private GPSTracker gpsTracker;
    private Activity activity;
    private String deviceId = "";
    private String location = "";
    private String deviceName = "";
    private String firebaseTokenId = "text";
    private String deviceType = "";
    private int value;
    private RadioGroup gender;
    private Button dateOfBirth;
    String getSelectedGender;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Spinner mobileSpinner;
    private String date = "";
    private GoogleApiClient googleApiClient;
    ProgressBar progressspinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        bindView();
        ButterKnife.inject(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        requestingPermission();
        progressspinner = (ProgressBar) findViewById(R.id.progressBar1);
        initPreferences();
        turnGPSOn();
        setDefaultLanguage(prefManager.getLanguage());
        //   setAdapters();
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                Logger.logD("month", "" + mMonth);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateOfBirth.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                dateOfBirth.setError(null);
                                date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                Logger.logD("datechecking", dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                              @Override
                                              public void onCheckedChanged(RadioGroup group, int checkedId) {
                                                  femaletxt.setError(null);
                                                  RadioButton radioButton = (RadioButton) findViewById(checkedId);
                                                  getSelectedGender = radioButton.getText().toString();
                                              }
                                          }
        );
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressspinner.bringToFront();
                progressspinner.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                try {
                    if (requestPermission()) {
                        callRegistrationProcess();
                    }
                } catch (Exception e) {
                    Toast.makeText(RegistrationActivity.this, "Permission is not given to register", Toast.LENGTH_SHORT).show();
                    progressspinner.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    try {
                        requestPermission();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    private void requestingPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {

            } else {
                try {
                   requestPermission();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setDefaultLanguage(int position) {
        switch (position) {
            case 1:
                SetLocality.setLocale(this, "en");
                if (position == 1) {
                    ArrayAdapter<String> bb = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nationalityList);
                    bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    nationalityspinner.setAdapter(bb);
                }
                setTexts();
                break;
            case 2:
                SetLocality.setLocale(this, "ar");
                if (position == 2) {
                    ArrayAdapter<String> bb = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nationalityListArabic);
                    bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    nationalityspinner.setAdapter(bb);
                }
                setTexts();
                break;
            default:
                SetLocality.setLocale(this, "en");
                // preferences.edit().putInt(Constants.SELECTEDLANGUAGE, position).apply();
                setTexts();
                if (position == 0) {
                    ArrayAdapter<String> bb = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nationalityList);
                    bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //Setting the ArrayAdapter data on the Spinner
                    nationalityspinner.setAdapter(bb);
                }
                break;
        }

    }

    private void setTexts() {
        userName.setHint(getResources().getString(R.string.name));
        cityName.setHint(getResources().getString(R.string.place));
        phoneNumber.setHint(getResources().getString(R.string.mobile_number));
        //enterDetails.setText(getResources().getString(R.string.enter_your_details));
        btnRegistration.setText(getResources().getString(R.string.btnRegistrationText));
        selectgendertxt.setText(getResources().getString(R.string.select_gender));
        dateOfBirth.setHint(getResources().getString(R.string.select_date_of_birth));
        maletxt.setText(getResources().getString(R.string.male));
        femaletxt.setText(getResources().getString(R.string.female));
        nationalitytxt.setText(getResources().getString(R.string.select_nationality));
    }

    private void initPreferences() {
        context = this;
        activity = RegistrationActivity.this;
        prefManager = new SharedPrefManager(context);
        gpsTracker = new GPSTracker(context);
        FirebaseApp.initializeApp(context);
    }

    private void bindView() {
        userName = (EditText) findViewById(R.id.personName);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        cityName = (EditText) findViewById(R.id.cityName);
        btnRegistration = (Button) findViewById(R.id.btnRegistartion);
        spinner = (Spinner) findViewById(R.id.spinner);
        nationalityspinner = (Spinner) findViewById(R.id.nationality_spinner);
        //enterDetails = (TextView) findViewById(R.id.enterDetails);
        gender = (RadioGroup) findViewById(R.id.gender_id);
        dateOfBirth = (Button) findViewById(R.id.dateofbirth);
        mobileSpinner = (Spinner) findViewById(R.id.mobile_spinner);
        selectgendertxt = (TextView) findViewById(R.id.selectgender_text);
        maletxt = (RadioButton) findViewById(R.id.male);
        femaletxt = (RadioButton) findViewById(R.id.female);
        nationalitytxt = (TextView) findViewById(R.id.nationality_text);
        //  setLanguage();
        setSpinnerListener();
        Logger.logD("Languagecspinner", "" + spinner.getSelectedItemPosition());

      /*  btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callRegistrationProcess();
            }
        });*/
    }

    private void setSpinnerListener() {
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languagesList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //noting to handle
                setDefaultLanguage(i);
                prefManager.saveLanguage(context, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //noting to handle
            }
        });


        List<String> mySpinnerList = new ArrayList<String>();
        mySpinnerList.add("+91");
        mySpinnerList.add("+974");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mySpinnerList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mobileSpinner.setAdapter(dataAdapter);
        mobileSpinner.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private void callRegistrationProcess() {
        if (isAllFieldsValid()) {
            if (!Utils.checkNetworkConnection(context)) {
                Toast.makeText(context, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            } else {
                deviceId = Utils.getDeviceId(activity);
                location = Utils.getLocation(gpsTracker, activity);
                deviceName = Utils.getDeviceName();
                firebaseTokenId = FirebaseInstanceId.getInstance().getToken();
                deviceType = Utils.getDeviceDimention(activity);
                Logger.logV("Location", " Lat->Lon " + location);
                Logger.logV("Location", " device id " + deviceId);
                Logger.logV("Location", " firebaseTokenId " + firebaseTokenId);
                Logger.logV("Location", " firebaseTokenId " + firebaseTokenId);
                Logger.logV("Location", " device Name " + deviceName);
                Logger.logV("Location", " deviceType " + deviceType);
                callRegistrationApi();
                btnRegistration.setEnabled(false);
                btnRegistration.getBackground().setAlpha(100);
            }
        }
    }

    private void callRegistrationApi() {
       /* Intent intent = new Intent(activity, OtpActivity.class);
        intent.putExtra("name", userName.getText().toString());
        intent.putExtra("place", cityName.getText().toString());
        intent.putExtra("phone", phoneNumber.getText().toString());
        intent.putExtra("location", location);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("deviceName", deviceName);
        intent.putExtra("language", getSeletedLanguageId);
        intent.putExtra("firebaseTokenId", firebaseTokenId);
        intent.putExtra("deviceType", deviceType);
        intent.putExtra("mobilespinner", mobileSpinner.getSelectedItem().toString());
        intent.putExtra("gender", getSelectedGender);
        intent.putExtra("dateofbirth", date);
        startActivity(intent);*/
        getSelectedGender = getGender();
        getSeletedLanguageId = getLanguage();
        String convertedUsername;
        convertedUsername = getTranslatedusername(userName.getText().toString());
//        Logger.logD("getuserSelectedLanguage",getSeletedLanguageId+"");
        /*String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.device/save-and-update-users-details-remotely/username/" + convertedUsername + "/mobile/" + mobileSpinner.getSelectedItem().toString() + phoneNumber.getText().toString() + "/placename/" + cityName.getText().toString() +
                "/deviceaddress/" + deviceId + "/devicelocation/" + location + "/status/Activated/devicename/" + deviceName + "/device-type/"
                + deviceType + "/apk-type/20825/token/" + firebaseTokenId + "/language/" + getSeletedLanguageId + "/gender/" + getSelectedGender + "/date-of-birth/" + date;*/
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.device/save-and-update-users-details-remotely/username/" + convertedUsername + "/mobile/" + mobileSpinner.getSelectedItem().toString() + phoneNumber.getText().toString() + "/placename/" + cityName.getText().toString() +
                "/deviceaddress/" + deviceId + "/devicelocation/" + location + "/status/Activated/devicename/" + deviceName + "/device-type/"
                + deviceType + "/apk-type/54752/token/" + firebaseTokenId + "/language/" + getSeletedLanguageId + "/gender/" + getSelectedGender + "/date-of-birth/" + date;
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " convertedURLregistration " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Logger.logD("regresponse", "" + response);
                            if (2 == (jsonObject.getInt("response")) || 1 == (jsonObject.getInt("response"))) {
                                prefManager.saveIsLoggedIn(context, true);
                                Intent intent = new Intent(activity, HomePage.class);
                                intent.putExtra("phone", phoneNumber.getText().toString().trim());
                                startActivity(intent);
                                finish();
                            }

                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
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
                });
////        RetryPolicy retryPolicy = new Re
        Volley.newRequestQueue(context).add(postRequest);

    }

    private String getTranslatedusername(String s) {
        //String word = http.callUrlAndParseResult("ar", "en", "مرحبا");
        return null;
    }

    private String getGender() {
        int checkedRadioButtonId = gender.getCheckedRadioButtonId();
        Logger.logD("genderid", "" + checkedRadioButtonId);
        String gender = "";
        if (checkedRadioButtonId == 2131820738)
            gender = "male";
        else
            gender = "female";
        return gender;
    }

    private int getLanguage() {
        switch (spinner.getSelectedItemPosition()) {
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                return 1;
        }
    }

    private boolean isAllFieldsValid() {
        if (userName.getText().toString().isEmpty()) {
            Toast.makeText(context, getResources().getString(R.string.usernameMandatoryText), Toast.LENGTH_SHORT).show();
            userName.setError(getResources().getString(R.string.usernameMandatoryText));
            userName.requestFocus();
            progressspinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return false;
        }
        if (dateOfBirth.getText().toString().isEmpty()) {
            Toast.makeText(context, getResources().getString(R.string.dateofbirthmandatory), Toast.LENGTH_SHORT).show();
            dateOfBirth.setError(getResources().getString(R.string.dateofbirthmandatory));
            dateOfBirth.requestFocus();
            progressspinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return false;
        }
        if (phoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(context, getResources().getString(R.string.phoneMandatoryText), Toast.LENGTH_SHORT).show();
            phoneNumber.setError(getResources().getString(R.string.phoneMandatoryText));
            dateOfBirth.setError(null);
            phoneNumber.requestFocus();
            progressspinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return false;
        }
        if (phoneNumber.getText().toString().length() < 10) {
            Toast.makeText(context, getResources().getString(R.string.phoneMandatoryTextlength), Toast.LENGTH_SHORT).show();
            phoneNumber.setError(getResources().getString(R.string.phoneMandatoryTextlength));
            phoneNumber.requestFocus();
            progressspinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return false;
        }
        if (gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(context, "GenderMandatory", Toast.LENGTH_SHORT).show();
            femaletxt.setError("GenderMandatory");
            gender.requestFocus();
            progressspinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return false;
        }
        if (cityName.getText().toString().isEmpty()) {
            Toast.makeText(context, getResources().getString(R.string.cityMandatoryText), Toast.LENGTH_SHORT).show();
            cityName.setError(getResources().getString(R.string.cityMandatoryText));
            femaletxt.setError(null);
            //cityName.requestFocus();
            progressspinner.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return false;
        }
        progressspinner.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        return true;

    }

    private boolean checkPermission() {
        int externalread = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int externalwrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int gpsACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int gpsACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int READ_PHONE_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int manageDocuments = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS);

        if (externalread != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (externalwrite != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (gpsACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (gpsACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (READ_PHONE_STATE != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (manageDocuments != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (sms != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean requestPermission() throws Exception {
        List<String> list = new ArrayList<>();
        activity = RegistrationActivity.this;
        if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION))
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.MANAGE_DOCUMENTS))
            list.add(Manifest.permission.MANAGE_DOCUMENTS);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_SMS))
            list.add(Manifest.permission.READ_SMS);
        if (list.isEmpty()) {
            return false;
        }
        String[] stockArr = new String[list.size()];
        stockArr = list.toArray(stockArr);
        if (stockArr.length != 0) {
            ActivityCompat.requestPermissions(RegistrationActivity.this, stockArr, 1);
        }
        return true;
    }


    private void turnGPSOn() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(RegistrationActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        turnGPSOn();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}