package neutrinos.addme.activity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.alarmclass.AlaramTone;
import neutrinos.addme.R;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView getAlaramTone;
    private List<AlaramTone> getAllTones;
    private MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playBeep();

        getAlaramTone = (ListView) findViewById(R.id.tonelist);
        SharedPreferences getAlarms = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());

        getAllTones = listRingtones();
        if (!getAllTones.isEmpty()) {
            ArrayAdapter adapter = new ArrayAdapter<AlaramTone>(this,
                    R.layout.activity_listview, getAllTones);
            getAlaramTone.setAdapter(adapter);

        } else {
            Toast.makeText(this, "No tone found!", Toast.LENGTH_SHORT).show();
        }

        getAlaramTone.setOnItemClickListener(this);


    }
    public void playBeep() {
         MediaPlayer m= new MediaPlayer();
        try {
            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd("alaram.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.setLooping(true);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<AlaramTone> listRingtones() {
       List<AlaramTone> getAlaramToneTemp= new ArrayList<>();
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
            Log.d("title-->",title);
            // Do something with the title and the URI of ringtone
            AlaramTone alaramTone= new AlaramTone();
            alaramTone.setAlaramName(title);
            alaramTone.setAlaramURL(uri);
            getAlaramToneTemp.add(alaramTone);
        }
        return getAlaramToneTemp;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Toast toast = Toast.makeText(getApplicationContext(), "Item " + (position + 1) + ": " + getAllTones.get(position),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        AlaramTone alaramTone= (AlaramTone)   getAllTones.get(position);
        SharedPreferences getAlarms = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        String alarms = getAlarms.getString("ringtone", alaramTone.getAlaramURL()+"/122");
        Uri uri = Uri.parse(alarms);
        Log.d("Tone URL-->",alarms);

        Ringtone r = RingtoneManager.getRingtone(MainActivity.this,uri);
        r.play();
        toast.show();
        // playSound(this, uri);


    }


    private boolean checkPermission() {

        int externalread = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int externalwrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int gpsACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int gpsACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int READ_PHONE_STATE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int manageDocuments = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS);
//        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
//        int record = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
//        int calender = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
//        int calenderWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
//        int smsRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
//        int smsWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
//        int smsReceive = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
//
//        int setAlarm = ContextCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM);
//        int vibrate = ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE);


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
//        } else if (camera != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (record != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (setAlarm != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (calender != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (calenderWrite != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (smsRead != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (smsReceive != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (smsWrite != PackageManager.PERMISSION_GRANTED) {
//            return false;
//        } else if (vibrate != PackageManager.PERMISSION_GRANTED) {
//            return false;
        }
        else {
            return true;
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission() {
        List<String> list = new ArrayList<>();
        activity = MainActivity.this;
        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION))
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE))
//            list.add(Manifest.permission.READ_PHONE_STATE);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS))
//            list.add(Manifest.permission.READ_CONTACTS);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CONTACTS))
//            list.add(Manifest.permission.WRITE_CONTACTS);
        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.MANAGE_DOCUMENTS))
            list.add(Manifest.permission.MANAGE_DOCUMENTS);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA))
//            list.add(Manifest.permission.CAMERA);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO))
//            list.add(Manifest.permission.RECORD_AUDIO);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SET_ALARM))
//            list.add(Manifest.permission.SET_ALARM);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.VIBRATE))
//            list.add(Manifest.permission.VIBRATE);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALENDAR))
//            list.add(Manifest.permission.READ_CALENDAR);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CALENDAR))
//            list.add(Manifest.permission.WRITE_CALENDAR);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS))
//            list.add(Manifest.permission.READ_SMS);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECEIVE_SMS))
//            list.add(Manifest.permission.RECEIVE_SMS);
//        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS))
//            list.add(Manifest.permission.SEND_SMS);

        //Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        if (list.isEmpty())
            return;
        String[] stockArr = new String[list.size()];
        stockArr = list.toArray(stockArr);
        if (stockArr.length != 0) {
            ActivityCompat.requestPermissions(MainActivity.this, stockArr, 1);
        }

    }
}
