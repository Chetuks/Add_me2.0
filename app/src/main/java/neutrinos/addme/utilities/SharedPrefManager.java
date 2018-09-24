package neutrinos.addme.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import neutrinos.addme.alarmclass.Alaram;


/**
 * Created by Gino Osahon on 04/03/2017.
 */
public class SharedPrefManager {

    SharedPreferences sharedPreferences;
    Context mContext;
    // shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String PREF_NAME = "sessionPref";
    SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }



    public void saveIsLoggedIn(Context context, Boolean isLoggedIn) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IS_LOGGED_IN", isLoggedIn);
        editor.commit();

    }
    public void saveAlarmTone(Context context, List<Alaram> alarmBean) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("AlarmTone", alarmBean.toString());
        editor.apply();

    }

    public boolean getISLogged_IN() {
        //mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return sharedPreferences.getBoolean("IS_LOGGED_IN", false);
    }
    public void saveContentData(Context context, String serverContent) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MAINCONTENT", serverContent);
        editor.apply();
    }

    public String getContent() {
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return sharedPreferences.getString("MAINCONTENT", "");
    }



    public void saveEmail(Context context, String email) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("EMAIL", email);
        editor.apply();
    }

    public String getUserEmail() {
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return sharedPreferences.getString("EMAIL", null);
    }

    public void saveLanguage(Context context, int language) {
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("language", language);
        editor.apply();
    }

    public int getLanguage() {
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return sharedPreferences.getInt("language", 1);
    }



}
