package neutrinos.addme.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neutrinos.addme.alarmclass.Alaram;
import neutrinos.addme.utilities.GPSTracker;
import neutrinos.addme.Interface.FetchNextURLInterface;
import neutrinos.addme.Interface.ISetTextInFragment;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.utilities.MyCurrentLocationTracker;
import neutrinos.addme.PreferenceClass.Preferences;
import neutrinos.addme.R;
import neutrinos.addme.utilities.SetLocality;
import neutrinos.addme.utilities.SharedPrefManager;
import neutrinos.addme.beenclass.UrlsBeen;
import neutrinos.addme.utilities.Utils;
import neutrinos.addme.activity.Checklist;
import neutrinos.addme.activity.CustomerProfile;
import neutrinos.addme.activity.HomePage;
import neutrinos.addme.activity.NewNearByActivity;
import neutrinos.addme.activity.ToDoListActivity;


public class DashBoardActivity extends android.support.v4.app.Fragment implements FetchNextURLInterface, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ISetTextInFragment {

    private static final String TAG = "VideoActivity";
    private static final String ALARAM_TONES = "alaram";
    private String response = "";
    private ProgressDialog mProgressDialog;
    private Context activity;
    public static final String MULTIMEDIA_FOLDER_NAME = "AddMe";
    private VideoView videoView;
    int currentTime = 0;
    List<UrlsBeen> urlsBeenList;
    List<UrlsBeen> urlsBeenListMain = new ArrayList<>();
    public static List<Alaram> alarmBean = new ArrayList<>();
    FrameLayout imageLayout;
    RelativeLayout videoLayout;
    neutrinos.addme.PreferenceClass.ZoomableImageView imageView;
    Toolbar mToolbar;
    int count = 0;
    int downloadCount = 0;
    private Button btnAdd;
    SharedPrefManager prefManager;
    boolean isOnline = false;
    private GPSTracker gpsTracker;
    Handler handel;
    List<UrlsBeen> filePathDownloadedList = new ArrayList<>();
    boolean callAPIFlage = true;
    private boolean isVideoPlaying = false;
    int sizeOfUrlMain = 0;
    private final int NEW_ALARM_ACTIVITY = 0;
    private final int EDIT_ALARM_ACTIVITY = 1;
    private final int PREFERENCES_ACTIVITY = 2;
    private final int ABOUT_ACTIVITY = 3;
    private final int CONTEXT_MENU_EDIT = 0;
    private final int CONTEXT_MENU_DELETE = 1;
    private final int CONTEXT_MENU_DUPLICATE = 2;
    private String getDeletedPath = "";
    private String appID = "20825";
    private TextView contentViewCount;
    private MyReceiver myReceiver;
    private IntentFilter filter;
    private Handler imageHandler;
    final long[] timeRemaining = {10000};
    private CountDownTimer timer;
    private Runnable imageRun;
    private GoogleApiClient googleApiClient;
    Context context;
    private View root;
    private TextView dashboard;
   // public final static int REQUEST_LOCATION = 199;
    public final static int REQUEST_LOCATION = 199;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public DashBoardActivity() {
    }

    public static DashBoardActivity newInstance() {
        DashBoardActivity fragment = new DashBoardActivity();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.activity_dash_board, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        gpsTracker = new GPSTracker(getActivity());
        videoView = (VideoView) root.findViewById(R.id.VideoViewfull);
        prefManager = new SharedPrefManager(getActivity());
        imageLayout = (FrameLayout) root.findViewById(R.id.imageViewLayout);
        videoLayout = (RelativeLayout) root.findViewById(R.id.videoViewLayout);
        imageView = (neutrinos.addme.PreferenceClass.ZoomableImageView) root.findViewById(R.id.imageView);
        contentViewCount = (TextView) root.findViewById(R.id.view_count);
        //dashboard = (TextView) root.findViewById(R.id.activity_dash_board_id);
        activity = getActivity();
        context = getActivity();
        handel = new Handler();
        int languagevalue = setDefaultLanguage(prefManager.getLanguage());
        Logger.logD("langvalue", "" + languagevalue);
        setTexts(languagevalue);
        int getSeletedLanguageId = prefManager.getLanguage();
        // initToolBar();
        myReceiver = new MyReceiver();
        filter = new IntentFilter("IsPressedReceiver");
        //reHitToServer();
        turnGPSOn();
        return root;
    }

    @Override
    public void showText(String text) {
        try {
            // dashboard= (TextView) root.getRootView().findViewById(R.id.activity_dash_board_id);
            //dashboard.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void onBackPressed() {
        Toast.makeText(getActivity(), "Back press disabled!", Toast.LENGTH_SHORT).show();
    }*/

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu)
     {
         MenuInflater menuInflater = getMenuInflater();
         menuInflater.inflate(R.menu.menu, menu);
         return true;
     }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_settings == item.getItemId()) {
            Intent intent = new Intent(getContext(), Preferences.class);
            startActivityForResult(intent, PREFERENCES_ACTIVITY);
            return true;
        } else if (R.id.menu_alarm == item.getItemId()) {
          /*  Intent intent = new Intent(getBaseContext(), AlarmMe.class);
            startActivity(intent);*/
            Intent intent = new Intent(getContext(), HomePage.class);
            startActivity(intent);
            return true;
        } else if (R.id.customer_profile == item.getItemId()) {
            Intent intent1 = new Intent(getContext(), CustomerProfile.class);
            startActivity(intent1);
            return true;
        } else if (R.id.checklist == item.getItemId()) {
            Intent intent1 = new Intent(getContext(), Checklist.class);
            startActivity(intent1);
            return true;
        } else if (R.id.todo == item.getItemId()) {
            Intent intent1 = new Intent(getContext(), ToDoListActivity.class);
            startActivity(intent1);
            return true;
        } else if (R.id.near_by == item.getItemId()) {
            Intent intent = new Intent(getContext(), NewNearByActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private int setDefaultLanguage(int position) {
        switch (position) {
            case 1:
                SetLocality.setLocale(getContext(), "en");
                return 1;
            case 2:
                SetLocality.setLocale(getContext(), "ar");
                return 2;
            default:
                SetLocality.setLocale(getContext(), "en");
                // preferences.edit().putInt(Constants.SELECTEDLANGUAGE, position).apply();
                //setTexts();
                return 1;
        }
    }

    private void setTexts(int langvalue) {
        switch (langvalue) {
            case 1:
                // TextView abcd=(TextView)root.findViewById(R.id.activity_dash_board_id);
                // abcd.setText("لوحة القيادة");
        }
        //dashboard.setText(getResources().getText(R.string.dashboard));
    }

    private void checkTheResponse(String response) {
        // storing the server response to SharePreference
        prefManager.saveContentData(context, response);
        // getting the respone SP and following the functionality.
        String getStringFromSP = prefManager.getContent();
        urlsBeenList = new ArrayList<>();
        if (getStringFromSP.isEmpty()) {
            isOnline = false;
            getFiles();
        } else {
            List<String> getServerCallFlag = new ArrayList<>();
            isOnline = true;
            try {
                JSONObject object = new JSONObject(getStringFromSP);
                JSONArray jsonArray = object.getJSONArray("urls");
                if (jsonArray.length() != 0)
                    parseJson(getStringFromSP);
                else {
                    callDefaultApi();
                    Toast.makeText(getActivity(), "No response from server", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*private void initToolBar() {
        mToolbar = (Toolbar) root.findViewById(R.id.custom_toolbar_add_reminder);
        gsetSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }*/
    private void parseJson(String response) {
        urlsBeenList.clear();
        try {
            JSONObject object = new JSONObject(response);
            JSONArray jsonArray = object.getJSONArray("urls");
            JSONArray jsonArrayDeleted = object.getJSONArray("deletedurls");
            JSONArray jsonArrayAlaram = object.getJSONArray("alarm");
            downloadAlaramToneFrmServer(jsonArrayAlaram);
            //getDeletedPath = deletUrlExist(jsonArrayDeleted);
            //callServerToDeletePath();
            int sizeOfUrl = jsonArray.length();
            sizeOfUrlMain = sizeOfUrl;
            for (int i = 0; i < sizeOfUrl; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String fileType = jsonObject.getString("filetype");
                String url = jsonObject.getString("url");
                UrlsBeen urlsBeenTEmp = new UrlsBeen(fileType, url, jsonObject.getInt("view_count"), jsonObject.getString("filename"));
                urlsBeenList.add(urlsBeenTEmp);
                urlsBeenListMain = urlsBeenList;
                Logger.logD("Size", "size if " + urlsBeenList.size());
                Logger.logD("Size", "size if " + urlsBeenList.get(i).getUrl());
                Logger.logD("Size", "size if " + urlsBeenList.get(i).getFiletype());
                if (urlsBeenList.size() == sizeOfUrl) {
                    displayInView();
                    UrlsBeen urlsBeen = urlsBeenList.get(0);
                    if (urlsBeen != null) {
                        switch (urlsBeen.getFiletype()) {
                            case "audio":
                                new AudioProgressBack().execute(urlsBeen.getUrl());
                                break;
                            case "video":
                                new ProgressBack().execute(urlsBeen.getUrl());

                                break;
                            case "image":
                                new ImageProgressBack().execute(urlsBeen.getUrl());

                                break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Server issue, Please contact admin", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadAlaramToneFrmServer(JSONArray jsonArrayAlaram) {
        Logger.logV(TAG, " downloadAlaramToneFrmServer " + jsonArrayAlaram.toString());
        alarmBean.clear();
        if (false) {
            //if (jsonArrayAlaram.length()>0) {
            for (int i = 0; i < jsonArrayAlaram.length(); i++) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArrayAlaram.getJSONObject(i);
                    String fileType = jsonObject.getString("filename");
                    String url = jsonObject.getString("url");
                    new ProgressBackAlaram().execute(url, fileType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Alaram alarm = new Alaram();
            alarm.setFiletype("Default alaram tone ");
            alarm.setUrl("android.resource://neutrinos.anandaguruji/" + R.raw.alarm_sms);
            alarmBean.add(alarm);
        }
    }

    private void callServerToDeletePath() {
        String deviceId = getDeviceId();
        String location = setLocation();
        Logger.logV("Location", " Lat->Lon " + location);
        Logger.logV("Location", " device id " + deviceId);
        String deviceName = getDeviceName();
        Logger.logV("Location", " device Name " + deviceName);
        if (!getDeletedPath.isEmpty())
            callDeleteApi("test1@liferay.com", "test1", deviceId, location, getDeletedPath);
    }

    private void callDeleteApi(final String usernameString, final String passwordString, String deviceId, String location, String getDeletedPath) {
        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.device/get-device-acknowledgment-remotely/unique-name/" + getDeletedPath + "/device-address/" + deviceId + "/status/delete";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("Location", " convertedURL-delete " + convertedURL);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //count = 0;
                        Logger.logV("Location", " Response " + response);
                        // checkTheResponse(response);
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
        Volley.newRequestQueue(activity).add(postRequest);
    }

    private String deletUrlExist(JSONArray jsonArrayDeleted) {
        String deletedfileName = "";
        try {
            if (jsonArrayDeleted.length() > 0) {
                for (int i = 0; i < jsonArrayDeleted.length(); i++) {
                    JSONObject jsonObject = jsonArrayDeleted.getJSONObject(i);
                    String fileType = jsonObject.getString("filename");
                    String url = jsonObject.getString("url");
                    String rootDir = Environment.getExternalStorageDirectory()
                            + File.separator + MULTIMEDIA_FOLDER_NAME;
                    File rootFile = new File(rootDir);
                    if (rootFile.exists()) {
                        String filename = convertToBase64(url);
                        File videoFile = new File(rootDir + File.separator + filename + ".mp4");
                        if (videoFile.exists()) {
                            Logger.logD("deleted", "Deleted URL-->" + videoFile.getPath());
                            videoFile.delete();
                            if (i == 0)
                                deletedfileName = fileType;
                            else
                                deletedfileName = deletedfileName + "," + fileType;
                            Logger.logD("deleted", "Deleted URL Deleted-->" + videoFile.getPath());
                            Logger.logD("deleted", "Deleted URL Deleted-->" + deletedfileName);
                        } else {
                            Logger.logD("deleted", "Deleted URL NOT found-->");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deletedfileName;
    }

    private void displayInView() {
        try {
            UrlsBeen urlsBeen = null;
            urlsBeen = getNextMedia();
            Logger.logD("Count", "Count -->" + count);
            if (urlsBeen != null && count != sizeOfUrlMain) {
                String fileType = urlsBeen.getFiletype();
                if (fileType.equals("image")) {
                    if (videoView.isPlaying()) {
                        videoView.stopPlayback();
                    }
                    if (!isFirstFileAvailabble(urlsBeen).equals("false")) {
                        showImage(isFirstFileAvailabble(urlsBeen), 0, urlsBeen.getViewCount(), urlsBeen.getImagetype());
                    } else {
                        showImage(urlsBeen.getUrl(), 0, urlsBeen.getViewCount(), urlsBeen.getImagetype());
                    }
                }
                if (fileType.equals("video")) {
                    if (videoView.isPlaying()) {
                        videoView.stopPlayback();
                    }
                    if (!isFirstFileAvailabble(urlsBeen).equals("false")) {
                        showVideo(isFirstFileAvailabble(urlsBeen), 0, urlsBeen.getViewCount(), urlsBeen.getImagetype());
                    } else {
                        showVideo(urlsBeen.getUrl(), 0, urlsBeen.getViewCount(), urlsBeen.getImagetype());
                    }
                }
                if (fileType.equals("audio")) {
                    if (videoView.isPlaying()) {
                        videoView.stopPlayback();
                    }
                    if (!isFirstFileAvailabble(urlsBeen).equals("false")) {

                        showAudio(isFirstFileAvailabble(urlsBeen), 0, urlsBeen.getViewCount());
                    } else {
                        showAudio(urlsBeen.getUrl(), 0, urlsBeen.getViewCount());
                    }
                }
            } else {
                Logger.logD("server", "Count -->" + count + "so stoping video");
                count = 0;
                //  reHitToServer();
                String getStringFromSP = prefManager.getContent();
                checkTheResponse(getStringFromSP);
            }
        } catch (Exception e) {
            e.printStackTrace();
            count = 0;
            displayInView();
          /* new Handler().postDelayed(
                   new Runnable() {
                       @Override
                       public void run() {
                           reHitToServer();
                       }
                   }, 3000*60);*/

        }
    }

    private void reHitToServer() {
        if (Utils.checkNetworkConnection(getActivity())) {
            String deviceId = getDeviceId();
            String location = setLocation();
            Logger.logV("Location", " Lat->Lon " + location);
            Logger.logV("Location", " device id " + deviceId);
            String deviceName = getDeviceName();
            Logger.logV("Location", " device Name " + deviceName);
            //callLoginApi("test@liferay.com","test",deviceId,location,deviceName);
            callLoginApi("test1@liferay.com", "test1", deviceId, location, deviceName);
        } else {
            String getStringFromSP = prefManager.getContent();
            if (getStringFromSP.isEmpty()) {
                isOnline = false;
                getFiles();
            }
        }
    }

    private UrlsBeen getNextMedia() {
        try {
            return urlsBeenList.get(count);
        } catch (Exception e) {
            return null;
        }
    }

    private void showVideo(String url, int flag, int viewCount, String contentPath) {
        Logger.logD("playing", "Vide playiung --->" + String.valueOf(viewCount));
        //   contentViewCount.setText(String.valueOf(viewCount));
        if (flag == 1) {
            new ProgressBack().execute(url);
        } else {
            videoLayout.setVisibility(View.VISIBLE);
            imageLayout.setVisibility(View.GONE);
            videoView.setBackgroundDrawable(null);
            contentViewCount.setText(String.valueOf(viewCount));
            Logger.logD("Countview", "" + viewCount);
            callApiAndUpdateViewCount(contentPath);
            playVideo(url, String.valueOf(viewCount));
        }
    }

    private void showImage(String url, int flag, int viewCount, String getServerImagePath) {
        Logger.logD("playing ", "showImage");
        if (flag == 1) {
            new ImageProgressBack().execute(url);
        } else {
            videoLayout.setVisibility(View.GONE);
            imageLayout.setVisibility(View.VISIBLE);
            displayImage(viewCount, getServerImagePath, url);
        }
    }

    private void showAudio(String url, int flag, int viewCount) {
        Logger.logD("playing", "Audio");
        Logger.logD("viewCount", "viewCount---->" + viewCount);
        contentViewCount.setText(String.valueOf(viewCount));
        if (flag == 1) {
            new AudioProgressBack().execute(url);
        } else {
          /*  Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.logo, null);
            videoView.setBackgroundDrawable(vectorDrawable);*/
            videoLayout.setVisibility(View.VISIBLE);
            imageLayout.setVisibility(View.GONE);
            playVideo(url, String.valueOf(viewCount));
        }
    }

    private void playAudio(String url) {
        if (isOnline)
            new AudioProgressBack().execute(url);
        else
            startAudioPlaying();
    }

    private void startAudioPlaying() {
    }


    private void displayImage(int viewCount, String getServerPath, String url) {
        Logger.logD("Images", "image playiung --->" + String.valueOf(viewCount));
        timeRemaining[0] = 10000;
        Glide.with(activity).load(url).asBitmap().into(imageView);
        contentViewCount.setText(String.valueOf(viewCount));
        callApiAndUpdateViewCount(getServerPath);
        imageHandler = new Handler();
        imageRun = new Runnable() {
            @Override
            public void run() {
                count++;
                timeRemaining[0] = 10000;
                displayInView();
            }
        };
        imageHandler.postDelayed(imageRun, timeRemaining[0]);
        timer = new CountDownTimer(timeRemaining[0], 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do something after
                timeRemaining[0] = timeRemaining[0] - 500;
            }

            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
                // do something end times 5s
            }
        };
        timer.start();
    }

    private void playVideo(String Url, final String viewCount) {
        Logger.logD("URL", "URL pATH" + Url);
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        isVideoPlaying = true;
        Uri video = Uri.parse(Url);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        mProgressDialog.setMessage("Downloading video...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer arg0) {
                videoView.start();
                mProgressDialog.dismiss();
            }
        });
        //contentViewCount.setText(String.valueOf(viewCount));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                isVideoPlaying = false;
                count++;
                displayInView();
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("video", "setOnErrorListener ");
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made
                        Toast.makeText(getActivity(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                        //turnGPSOn();
                        if (Utils.checkNetworkConnection(context)) {
                            reHitToServer();
                        } else {
                            String getStringFromSP = prefManager.getContent();
                            checkTheResponse(getStringFromSP);
                        }
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

        //reHitToServer();
        //turnGPSOn();
    }

    @Override
    public void onSuccessResonse(String downloadSuccessfulRespose, String fileTypeTemp) {
        Logger.logD("onSuccessResonse", "Interface calling.. " + downloadCount);
        Logger.logD("onSuccessResonse", "urlsBeenList " + urlsBeenList.size());
        if (downloadCount != 0)
            filePathDownloadedList.add(new UrlsBeen(fileTypeTemp, downloadSuccessfulRespose, 0, ""));
//        Logger.logD("onSuccessResonse","filePathDownloadedList "+filePathDownloadedList.get(downloadCount).getUrl());
       /* if (downloadCount==4){
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            Logger.logD("onSuccessResonse","Interface calling..--> "+downloadCount);
                            reHitToServer();
                        }
                    }, 3000*60);
        }*/
        try {
            if (downloadCount < urlsBeenList.size()) {
                downloadCount++;
                UrlsBeen urlsBeenTemp = urlsBeenList.get(downloadCount - 1);
                if (urlsBeenTemp != null && downloadCount <= urlsBeenList.size()) {
                    String fileType = urlsBeenTemp.getFiletype();
                    if (fileType.equals("image")) {

                        showImage(urlsBeenTemp.getUrl(), 1, urlsBeenTemp.viewCount, urlsBeenTemp.getUrl());
                    }
                    if (fileType.equals("video")) {

                        showVideo(urlsBeenTemp.getUrl(), 1, urlsBeenTemp.viewCount, urlsBeenTemp.getImagetype());
                    }
                    if (fileType.equals("audio")) {
                        showAudio(urlsBeenTemp.getUrl(), 1, urlsBeenTemp.viewCount);
                    }

                } else {
                    Logger.logD("server", "downloadCompleted -->calling API");
                    Runnable run1 = new Runnable() {
                        @Override
                        public void run() {
                            //  reHitToServer();
                        }
                    };
                    handel.postDelayed(run1, 1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        turnGPSOn();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        turnGPSOn();
    }

    /*ProgressBack PB = new ProgressBack();
        PB.execute("");*/
    private class ProgressBack extends AsyncTask<String, Integer, String> {
        private ProgressDialog PD = new ProgressDialog(activity);
        String filePath = "";

        @Override
        public void onPreExecute() {
           /* PD.setMax(100);
            PD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            PD.show();*/
        }

        @Override
        public String doInBackground(String... arg0) {
            File videoFile = null;
            try {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + MULTIMEDIA_FOLDER_NAME;
                File rootFile = new File(rootDir);
                final boolean mkdir = rootFile.mkdir();
                if (rootFile.exists()) {
                    Logger.logV(TAG, "Video Url : " + arg0[0]);
                    String videoUrl = arg0[0];
                    URL url = new URL(videoUrl);
                    String filename = convertToBase64(videoUrl);
                    Logger.logV(TAG, "FileNameVideo : " + filename);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                    videoFile = new File(rootDir + File.separator + filename + ".mp4");
                    if (videoFile.exists()) {
                        return videoFile.getPath();
                    }
                    videoFile.createNewFile();
                    //prefs.edit().putString(Constants.ABOUT_VIDEO_PATH, videoFile.getPath()).apply();
                    int fileLength = c.getContentLength();
                    FileOutputStream f = new FileOutputStream(videoFile);
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    long total = 0;
                    int count;
                    while ((len1 = in.read(buffer)) > 0) {
                        f.write(buffer, 0, len1);
                        total += len1;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) ((total * 100) / fileLength));
                    }
                    f.close();
                } else {
                    Toast.makeText(activity, "Couldn't create OneBridge Folder", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
            if (videoFile == null) {
                return "";
            }
            return videoFile.getPath();
        }

        public void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            Logger.logV(TAG, String.valueOf(progress[0]));
            //PD.setTitle("Downloading Video (" + String.valueOf(progress[0] + "% )"));

        }

        public void onPostExecute(String filePath) {
            //PD.dismiss();
            // PD.dismiss();
           /* filePathDownloadedList.add(new UrlsBeen("video",filePath));
            playVideo(filePath);*/
            onSuccessResonse(filePath, "video");
        }
    }

    private class ProgressBackAlaram extends AsyncTask<String, Integer, String> {
        ProgressDialog PD;
        String filePath = "";

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            File videoFile = null;
            try {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + MULTIMEDIA_FOLDER_NAME + ALARAM_TONES;
                File rootFile = new File(rootDir);
                final boolean mkdir = rootFile.mkdir();
                if (rootFile.exists()) {
                    Alaram alarm = new Alaram();
                    alarm.setFiletype(arg0[1]);
                    alarm.setUrl(arg0[0]);
                    alarmBean.add(alarm);
                    prefManager.saveAlarmTone(getActivity(), alarmBean);
                    Logger.logV(TAG, "Alaram Tone Url : " + arg0[0]);
                    Logger.logV(TAG, "Alaram Tone name : " + arg0[1]);
                    String videoUrl = arg0[0];
                    URL url = new URL(videoUrl);
                    String filename = convertToBase64(videoUrl);
                    Logger.logV(TAG, "FileNameVideo : " + filename);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                    videoFile = new File(rootDir + File.separator + filename + ".mp3");
                    if (videoFile.exists()) {
                        return videoFile.getPath();
                    }
                    videoFile.createNewFile();
                    int fileLength = c.getContentLength();
                    FileOutputStream f = new FileOutputStream(videoFile);
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    long total = 0;
                    int count;
                    while ((len1 = in.read(buffer)) > 0) {
                        f.write(buffer, 0, len1);
                        total += len1;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) ((total * 100) / fileLength));
                    }
                    f.close();
                } else {
                    Toast.makeText(activity, "Couldn't create OneBridge Folder", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
            if (videoFile == null) {
                return "";
            }
            return videoFile.getPath();
        }

        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            Logger.logV(TAG, String.valueOf(progress[0]));
            mProgressDialog.setTitle("Downloading MP4 (" + String.valueOf(progress[0] + "% )"));
        }

        protected void onPostExecute(String filePath) {
            //PD.dismiss();
            mProgressDialog.dismiss();
           /* filePathDownloadedList.add(new UrlsBeen("video",filePath));
            playVideo(filePath);*/
            onSuccessResonse(filePath, "video");
        }
    }

    public String convertToBase64(String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("Error", "Exception: ", e);
        }
        return "";
    }


    private void getFiles() {
        try {
            urlsBeenList = new ArrayList<>();
//            File folder = new File(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES), "MyCameraApp");
            File folder = new File(Environment.getExternalStorageDirectory() + "/", MULTIMEDIA_FOLDER_NAME);
            File[] tempsortedByDate;
            tempsortedByDate = folder.listFiles();
            if (tempsortedByDate != null && tempsortedByDate.length > 1) {
                Arrays.sort(tempsortedByDate, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                        return (int) ((object1.lastModified() > object2.lastModified()) ? object1.lastModified() : object2.lastModified());
                    }
                });
            }
            Logger.logV(TAG, " offlineFile size" + tempsortedByDate.length);
            if (tempsortedByDate != null) {
                for (int i = 0; i < tempsortedByDate.length; i++) {
                    File tempFile = tempsortedByDate[i];
                    if (tempFile != null) {
                        Logger.logV(TAG, " offlineFile path" + tempFile.getPath());
                        if (tempFile.getPath().contains("jpg")) {
                            urlsBeenList.add(new UrlsBeen("image", tempFile.getPath(), 0, ""));
                        }
                        if (tempFile.getPath().contains("mp4")) {
                            urlsBeenList.add(new UrlsBeen("video", tempFile.getPath(), 0, ""));
                        }
                    }
                    if (i == (tempsortedByDate.length - 1)) {
                        displayInView();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", "Exception: ", e);
        }
    }


    private class ImageProgressBack extends AsyncTask<String, Integer, String> {
        ProgressDialog PD;
        String filePath = "";

        @Override
        protected void onPreExecute() {
            // Create progress dialog
            //PD = new ProgressDialog(activity);
            // Set your progress dialog Title
            //mProgressDialog.setTitle("");
            // Set your progress dialog Message
            // PD.setMessage("Downloading, Please Wait!");
            //PD.setIndeterminate(false);
            //PD.setMax(100);
            // PD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // Show progress dialog
            try {
                // PD.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... arg0) {
            File videoFile = null;
            try {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + MULTIMEDIA_FOLDER_NAME;
                File rootFile = new File(rootDir);
                final boolean mkdir = rootFile.mkdir();
                if (rootFile.exists()) {
                    Logger.logV(TAG, "Image Url : " + arg0[0]);
                    String videoUrl = arg0[0];
                    URL url = new URL(videoUrl);
                    String filename = convertToBase64(videoUrl);
                    Logger.logV(TAG, "FileNameImage : " + filename);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                    videoFile = new File(rootDir + File.separator + filename + ".jpg");
                    if (videoFile.exists())
                        return videoFile.getPath();
                    videoFile.createNewFile();
                    //prefs.edit().putString(Constants.ABOUT_VIDEO_PATH, videoFile.getPath()).apply();
                    int fileLength = c.getContentLength();
                    FileOutputStream f = new FileOutputStream(videoFile);
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    long total = 0;
                    int count;
                    while ((len1 = in.read(buffer)) > 0) {
                        f.write(buffer, 0, len1);
                        total += len1;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) ((total * 100) / fileLength));
                    }
                    f.close();
                } else {
                    Toast.makeText(activity, "Couldn't create OneBridge Folder", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
            if (videoFile == null) {
                return "";
            }
            return videoFile.getPath();
        }

        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            Logger.logV(TAG, String.valueOf(progress[0]));
            //PD.setTitle("Downloading image (" + String.valueOf(progress[0]) + "%)");
        }

        protected void onPostExecute(String filePath) {
            //PD.dismiss();

          /*  Logger.logV(TAG," : " +filePath);
            filePathDownloadedList.add(new UrlsBeen("image",filePath));*/
            onSuccessResonse(filePath, "image");
            //PD.dismiss();
        }
    }

    private class AudioProgressBack extends AsyncTask<String, Integer, String> {
        ProgressDialog PD = new ProgressDialog(activity);
        String filePath = "";

        @Override
        protected void onPreExecute() {
            // Create progress dialog
            mProgressDialog = new ProgressDialog(activity);
            // Set your progress dialog Title
            mProgressDialog.setTitle("Downloading..mp3");
            // Set your progress dialog Message
            mProgressDialog.setMessage("Downloading, Please Wait!");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // Show progress dialog
            try {
                mProgressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... arg0) {
            File videoFile = null;
            try {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + MULTIMEDIA_FOLDER_NAME;
                File rootFile = new File(rootDir);
                final boolean mkdir = rootFile.mkdir();
                if (rootFile.exists()) {
                    Logger.logV(TAG, "Image Url : " + arg0[0]);
                    String videoUrl = arg0[0];
                    URL url = new URL(videoUrl);
                    String filename = convertToBase64(videoUrl);
                    Logger.logV(TAG, "FileNameImage : " + filename);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setDoOutput(true);
                    c.connect();
                    videoFile = new File(rootDir + File.separator + filename + ".mp3");
                    if (videoFile.exists())
                        return videoFile.getPath();
                    videoFile.createNewFile();
                    //prefs.edit().putString(Constants.ABOUT_VIDEO_PATH, videoFile.getPath()).apply();
                    int fileLength = c.getContentLength();
                    FileOutputStream f = new FileOutputStream(videoFile);
                    InputStream in = c.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len1 = 0;
                    long total = 0;
                    int count;
                    while ((len1 = in.read(buffer)) > 0) {
                        f.write(buffer, 0, len1);
                        total += len1;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) ((total * 100) / fileLength));
                    }
                    f.close();
                } else {
                    Toast.makeText(activity, "Couldn't create OneBridge Folder", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
            if (videoFile == null) {
                return "";
            }
            return videoFile.getPath();
        }

        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            Logger.logV(TAG, String.valueOf(progress[0]));
            mProgressDialog.setTitle("Downloading mp3 (" + String.valueOf(progress[0]) + "%)");
        }

        protected void onPostExecute(String filePath) {
            //PD.dismiss();
            mProgressDialog.dismiss();
            Logger.logV(TAG, " : " + filePath);
            onSuccessResonse(filePath, "audio");
        }
    }


    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public boolean checkNetworkConnection(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
        } catch (Exception e) {
            Logger.logE("Error", "check network", e);
        }
        return isConnected;
    }


    private void callLoginApi(final String usernameString, final String passwordString, String deviceId, String location, String deviceName) {
        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.device/save-and-update-device-details-remotely/device-address/" + deviceId + "/device-location/" + location + "/device-status/Activated/device-name/" + deviceName + "/appunique-id/" + appID;
        String convertedURL = url.replace(" ", "%20");
        Logger.logD("saveapi", "" + convertedURL);
        Logger.logD("saveapi", "" + url);
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
                String credentials = usernameString + ":" + passwordString;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(activity).add(postRequest);
    }


    private void callDefaultApi() {
        String url = "http://localhost:8180/api/jsonws/addMe-portlet.device/push-new-content-to-newuser/appunique-id/" + appID;
        String convertedURL = url.replace(" ", "%20");
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
                //String credentials = "test@liferay.com:test";
                String credentials = "test1@liferay.com:test1";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(activity).add(postRequest);
    }

    private void callApiAndUpdateViewCount(String contentPath) {
        String deviceId = getDeviceId();
        String url = "http://216.98.9.235:8180/api/jsonws/addMe-portlet.device/get-device-acknowledgment-remotely/unique-name/" + contentPath + "/device-address/" + deviceId + "/status/viewed";
        String convertedURL = url.replace(" ", "%20");
        Logger.logD("countapi",""+url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //count = 0;
                        Logger.logV("Location", " countResponse--- " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            jsonObject.getInt("noOfViews");
                            Logger.logV("Location", " no of count--- " + jsonObject.getInt("noOfViews"));
                            contentViewCount.setText(String.valueOf(jsonObject.getInt("noOfViews")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // checkTheResponse(response);
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
                //String credentials = "test@liferay.com:test";
                String credentials = "test1@liferay.com:test1";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(activity).add(postRequest);
    }


    private String getDeviceName() {
        return android.os.Build.MODEL;
    }

    public String setLocation() {
        String stringLatitude = "0.0";
        String stringLongitude = "0.0";
        if (Double.doubleToRawLongBits(gpsTracker.latitude) == 0 || Double.doubleToRawLongBits(gpsTracker.longitude) == 0) {
            MyCurrentLocationTracker tracker = new MyCurrentLocationTracker(getActivity(), null, null);
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

    @Override
    public void onResume() {
        super.onResume();
        if (isVideoPlaying) {
            videoView.seekTo(currentTime);
        }
        getActivity().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isVideoPlaying) {
            currentTime = videoView.getCurrentPosition();
        }
    }

    private String isFirstFileAvailabble(UrlsBeen urlsBeen) {
        String filename = "";
        if (urlsBeen != null) {
            switch (urlsBeen.getFiletype()) {
                case "audio":
                    filename = convertToBase64(urlsBeen.getUrl()) + ".mp3";
                    break;
                case "video":
                    filename = convertToBase64(urlsBeen.getUrl()) + ".mp4";
                    break;
                case "image":
                    filename = convertToBase64(urlsBeen.getUrl()) + ".jpg";
                    break;
            }
            File videoFile = null;
            try {
                String rootDir = Environment.getExternalStorageDirectory()
                        + File.separator + MULTIMEDIA_FOLDER_NAME;
                File rootFile = new File(rootDir);
                final boolean mkdir = rootFile.mkdir();
                if (rootFile.exists()) {
                    videoFile = new File(rootDir + File.separator + filename);
                    if (videoFile.exists())
                        return videoFile.getPath();
                    else
                        return "false";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "false";
            }
        }
        return "false";
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(myReceiver);
        } catch (Exception e) {
            Logger.logE(TAG, e.getMessage(), e);
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean isPressed = intent.getBooleanExtra("isPressed", false);
            try {
                if (isPressed) {
                    timer.cancel();
                    imageHandler.removeCallbacks(imageRun);
                } else {
                    timer.start();
                    imageHandler.postDelayed(imageRun, timeRemaining[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void turnGPSOn() {
        int REQUEST_CHECK_SETTINGS = 100;
        googleApiClient = new GoogleApiClient.Builder(getActivity())
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
                        if (Utils.checkNetworkConnection(context)) {
                            reHitToServer();
                        } else {
                            String getStringFromSP = prefManager.getContent();
                            checkTheResponse(getStringFromSP);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                       /* if (Utils.checkNetworkConnection(context)) {
                            reHitToServer();
                        } else {
                            String getStringFromSP = prefManager.getContent();
                            checkTheResponse(getStringFromSP);
                        }*/
                        break;
                }
            }
        });

    }
}
