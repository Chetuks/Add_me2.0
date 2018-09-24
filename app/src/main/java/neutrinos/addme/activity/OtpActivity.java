package neutrinos.addme.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import neutrinos.addme.utilities.HttpHandler;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.R;
import neutrinos.addme.Interface.SMSCallBackInterface;
import neutrinos.addme.utilities.SharedPrefManager;
import neutrinos.addme.utilities.SmsReceivers;

/**
 * Created by mahiti on 13/3/18.
 */

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "";
    private String name;
    private int language;
    private String phone;
    private String place;
    private String location;
    private String deviceId;
    private String deviceName;
    private String firebaseTokenId;
    private OtpActivity activity;
    private OtpActivity context;
    private SharedPrefManager prefManager;
    private String deviceType;
    private String otpCratedTime = "";
    private String secrateKey = "";
    String url;
    EditText otpText;
    String otpString;
    EditText otpTextOne;
    EditText otpTexttwo;
    EditText otpTextthree;
    EditText otpTextfour;
    private String starttime;
    public static final String DATE_AND_TIME_FORMAT = "EE MMM dd HH:mm:ss Z yyyy";
    private String gender;
    private String dateofbirth = "";
    private String mobilespinner;
    TextView secondtext;
    LinearLayout resendbtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_validation);
        initPreferences();
        bindSmsReceiver();
        getDataFromActivity();
        timer();
        resendbtn=findViewById(R.id.resend_btn);
        secondtext = findViewById(R.id.secondstext);
        recall();

    }

    private void recall() {
        resendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromActivity();
                startActivity(getIntent());
            }
        });
    }


    private void timer() {
        int seconds = 30;
        CountDownTimer countDownTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondtext.setText("Seconds :" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                secondtext.setText("Finished Resend OTP");
            }
        }.start();
    }


    private void initPreferences() {
        context = this;
        activity = OtpActivity.this;
        prefManager = new SharedPrefManager(context);

        FirebaseApp.initializeApp(context);
    }

    private void callValidateUrl(String otpString, String secrateKey, String otpCratedTime) {
        /* *//* SimpleDateFormat sdf = new SimpleDateFormat(DATE_AND_TIME_FORMAT, Locale.US);
        starttime = sdf.format(new Date());*//*
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss GMT yyyy");
        starttime = df.format(c);
        Logger.logD("datecehck",""+starttime);*/
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss");
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String starttime = df.format(c);
        Logger.logD("year", "" + year);
        Logger.logD("concatinate", "" + starttime + " GMT " + year);
        String concatinate = starttime + " GMT " + year;
        concatinate = concatinate.replace(" ", "%20");
        Logger.logD("otpcreatedtime", "" + otpCratedTime);
        Logger.logD("datecehck", "" + starttime);
        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.check_list/otp-validation-for-mobile/entered-otp/" + otpString + "/start-timefor-otp/" + concatinate + "/secret-shared-key/" + secrateKey + "/otp-created-time/" + otpCratedTime;
        String convertedURL = url.replace(" ", "%20");
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //count = 0;
                        Logger.logV("OTPvalid", " Response " + response);
                        checkTheResponseOTP(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Logger.logV("OTPvaliderror", " ERROR " + error);

                    }
                }
        ) {

        };
        Volley.newRequestQueue(activity).add(postRequest);
        // VolleyClass.getRequest(url, OtpActivity.this, OtpActivity.this, 100);
    }

    private void checkTheResponseOTP(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int getStatus = jsonObject.getInt("response");
            if (getStatus == 2) {
                String message = jsonObject.getString("message");
                if (!message.equals("") || message.isEmpty()) {
                    Toast.makeText(this, "" + message, Toast.LENGTH_LONG).show();
                    callNextActivity();
                }
            } else {
                Toast.makeText(this, "Wrong OTP entered", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getDataFromActivity() {
        Bundle data = getIntent().getExtras();
        if (data != null) {
            name = data.getString("name");
            language = data.getInt("language");
            phone = data.getString("phone");
            place = data.getString("place");
            location = data.getString("location");
            deviceId = data.getString("deviceId");
            deviceName = data.getString("deviceName");
            firebaseTokenId = data.getString("firebaseTokenId");
            deviceType = data.getString("deviceType");
            gender = data.getString("gender");
            dateofbirth = data.getString("dateofbirth");
            mobilespinner = data.getString("mobilespinner");

            if (phone != null && !phone.isEmpty()) {
                String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.check_list/otp-generation-for-mobile/mobile/" + phone;
                String convertedURL = url.replace(" ", "%20");
                StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //count = 0;
                                Logger.logV("OTPgeneration", " Response " + response);
                                checkTheResponse(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                Logger.logV("OTPgenerationerror", " ERROR " + error);

                            }
                        }
                ) {

                };
                Volley.newRequestQueue(activity).add(postRequest);
                // VolleyClass.getRequest(url, OtpActivity.this, OtpActivity.this, 100);
            }
        }
    }

    private void checkTheResponse(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int getStatus = jsonObject.getInt("response");
            if (getStatus == 1) {
                parseOTP(jsonObject);
            } else {
                Toast.makeText(this, "Fail to send to server please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

  /*  @Override
    public void onSuccess(String result, int i) {
        if (i == 100) {

            // parse json of send otp url

            // [{"otpCreatedTime":"Wed Mar 14 22:14:15 GMT 2018","response":1,"secretSharedKey":"vrF64GBNobJeeaKm8tbd4g==","otp":"6332"}]
            Logger.logV("TAGResult", result);
            new GetJson().execute();
        }
        if (i == 200) {
            //Json parsing has to be done validate otp
            // {"message":"No JSON web service action associated with path /check_list/otp-validation-for-mobile and method GET for //addMe-portlet","exception":"java.lang.RuntimeException"}
            Logger.logV("TAGResultValidation", result);
            callNextActivity();
        }

    }*/

    private void parseOTP(JSONObject jsonObject) {
        try {

            otpCratedTime = jsonObject.getString("otpCreatedTime");
            secrateKey = jsonObject.getString("secretSharedKey");
            otpTextOne = findViewById(R.id.editTextOtp_One);
            otpTexttwo = findViewById(R.id.editTextOtp_two);
            otpTextthree = findViewById(R.id.editTextOtp_three);
            otpTextfour = findViewById(R.id.editTextOtp_four);
            findViewById(R.id.buttonConfirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String otpStringOne = otpTextOne.getText().toString().trim();
                    String otpStringtwo = otpTexttwo.getText().toString().trim();
                    String otpStringthree = otpTextthree.getText().toString().trim();
                    String otpStringfour = otpTextfour.getText().toString().trim();


                    StringBuilder sb = new StringBuilder();
                    sb.append(otpStringOne);
                    sb.append(otpStringtwo);
                    sb.append(otpStringthree);
                    sb.append(otpStringfour);
                    otpString = sb.toString();
                    Logger.logD("otpstringcheck", "" + otpString);

                    if (otpString.isEmpty() && otpString.length() < 4) {
                        Toast.makeText(OtpActivity.this, "Otp is mismatch", Toast.LENGTH_SHORT).show();
                    } else if (!otpString.equals("") && !secrateKey.equals("") && !otpText.toString().equals("")) {
                        callValidateUrl(otpString, secrateKey, otpCratedTime);
                    }
                }
            });
            //callValidateUrl(otpText.toString(),otpCratedTime,secrateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void callNextActivity() {
        url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.device/save-and-update-users-details-remotely/username/" + name
                + "/mobile/" + phone + "/placename/" + place + "/deviceaddress/" + deviceId + "/devicelocation/"
                + location + "/status/Activated/devicename/" + deviceName + "/device-type/" + deviceType
                + "/apk-type/20825/token/" + firebaseTokenId + "/language/"
                + language + "/gender/" + gender + "/date-of-birth/" + dateofbirth;
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " convertedURL " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("Registration", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (2 == (jsonObject.getInt("response")) || 1 == (jsonObject.getInt("response"))) {
                                prefManager.saveIsLoggedIn(context, true);
                                Intent intent = new Intent(activity, HomePage.class);
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
        Volley.newRequestQueue(context).add(postRequest);
    }


   /* @Override
    public void onCancel(String result, int i) {

    }*/

    private class GetJson extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);


            return null;
        }
    }


    /**
     * bindSmsReceiver method
     */
    private void bindSmsReceiver() {

        SmsReceivers.bindListener(new SMSCallBackInterface() {
            @Override
            public void populateSMSOtp(String smsOtp) {
                char[] otp = smsOtp.toCharArray();
                String one = String.valueOf(otp[0]);
                String two = String.valueOf(otp[1]);
                String three = String.valueOf(otp[2]);
                String four = String.valueOf(otp[3]);

                otpTextOne.setText(one);
                otpTexttwo.setText(two);
                otpTextthree.setText(three);
                otpTextfour.setText(four);
            }
        });

    }

}
