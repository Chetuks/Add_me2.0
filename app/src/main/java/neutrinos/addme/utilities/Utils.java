package neutrinos.addme.utilities;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;


public class Utils {
    public static boolean checkNetworkConnection(Context context) {
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

    public static String getDeviceId(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getLocation(GPSTracker gpsTracker, Activity context) {
        String stringLatitude = "0.0";
        String stringLongitude = "0.0";
        if (Double.doubleToRawLongBits(gpsTracker.latitude) == 0 || Double.doubleToRawLongBits(gpsTracker.longitude) == 0) {
            MyCurrentLocationTracker tracker = new MyCurrentLocationTracker(context, null, null);
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
        return stringLatitude+","+stringLongitude;
    }

    @org.jetbrains.annotations.Contract(pure = true)
    public static String getDeviceName() {
        return Build.MODEL;
    }


    public static String getDeviceDimention(Activity activity) {
        String sendDeviceType = "";
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            Logger.logD("the device width-->Tab", "Tab");
            sendDeviceType = "Tab";
        } else if (diagonalInches >= 10.0) {
            Logger.logD("the device width-->TV", "TV");
            sendDeviceType = "TV";
        } else {
            Logger.logD("the device width-->mobile", "mobile");
            sendDeviceType = "Mobile";
        }
        return sendDeviceType;
    }

}
