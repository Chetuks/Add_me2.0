package neutrinos.addme.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import neutrinos.addme.utilities.Logger;
import neutrinos.addme.R;
import neutrinos.addme.utilities.SharedPrefManager;
import neutrinos.addme.activity.SplashScreen;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    SharedPrefManager preferenceManager;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        Set<String> keys = data.keySet();

        preferenceManager = new SharedPrefManager(this);

        for (String key : keys) {
            Logger.logD(key, " " + data.get(key));
        }
        if (data.get("urls") != null) {
            updatePreference(data.get("urls"));
        }
        sendNotification(data);
    }

    /**
     * @param urls content update after pushing the data to device
     */
    private void updatePreference(String urls) {
        try {
            JSONArray jsonArray = new JSONArray(urls);
            String oldResponse = preferenceManager.getContent();
            JSONObject jsonResponseOld = new JSONObject(oldResponse);
            if (jsonResponseOld.has("urls")) {
                jsonResponseOld.remove("urls");
                jsonResponseOld.put("urls", jsonArray);
                Logger.logD("jsonResponseOldStrValue", " " + String.valueOf(jsonResponseOld));
                preferenceManager.saveContentData(this, String.valueOf(jsonResponseOld));
                Logger.logD("getContent", " " + preferenceManager.getContent());
            }
        } catch (Exception e) {
            Logger.logE(e.getLocalizedMessage(), e.getMessage(), e);
        }
    }

    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param data FCM data payload received.
     */
    private void sendNotification(Map<String, String> data) {
        String CHANNEL_ID = "my_channel_01";
        Drawable tempImage = getResources().getDrawable(R.mipmap.addme_logo);
        Bitmap icon = ((BitmapDrawable) tempImage).getBitmap();
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(data.get("message"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo("Add me")
                .setLargeIcon(icon)
                .setColor(Color.RED)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.addme_logo);
        try {
            String picture_url = data.get("picture_url");
            if (picture_url != null && !"".equals(picture_url)) {
                URL url = new URL(picture_url);
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText("Summuries")
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("channel description");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}