package neutrinos.addme.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import neutrinos.addme.R;
import neutrinos.addme.utilities.SharedPrefManager;
import neutrinos.addme.utilities.SmsReceivers;

public class SplashScreen extends AppCompatActivity {
    SharedPrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SmsReceivers connectionReceiver = new SmsReceivers();
        registerReceiver(connectionReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        prefManager = new SharedPrefManager(getApplicationContext());
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    if (prefManager.getISLogged_IN()) {
                        Intent intent = new Intent(SplashScreen.this, HomePage.class);
                        intent.putExtra("callAPI", 1);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashScreen.this, RegistrationActivity.class);
                        intent.putExtra("callAPI", 1);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
