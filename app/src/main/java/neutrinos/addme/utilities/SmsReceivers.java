package neutrinos.addme.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import neutrinos.addme.Interface.SMSCallBackInterface;

public class SmsReceivers extends BroadcastReceiver {
    String smsFormNumber = "";
    String smsMessageBody = "";
    SharedPreferences preferences;
    private static final String TAG = "SMS RECEiVER";
    private static SMSCallBackInterface mListener;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Call OnReceive-");
        if ( intent != null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Logger.logD(TAG, "inside onReceive reading sms");
            //---get the SMS message passed in---
            Bundle bundle = intent.getExtras();
            StringBuilder finalSMSBody = new StringBuilder();
            if (bundle != null) {
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus == null)
                    return;
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                for (int i = 0; i < msgs.length; i++) {
                    smsFormNumber = msgs[i].getOriginatingAddress(); // get the number from sender
                    Log.d(TAG, "smsFormNumber--" + smsFormNumber);
                    finalSMSBody.append(msgs[i].getMessageBody()); // append the message body to final string
                }
                if (smsFormNumber != null) {
                    smsMessageBody = finalSMSBody.toString(); // getting sms body and storing into final string
                    Log.d(TAG, "smsMessageBody--" + smsMessageBody);

                }
                if (smsMessageBody != null && !smsMessageBody.isEmpty()) {
                    Logger.logD(TAG, "inside final if reading sms");
                    Pattern p = Pattern.compile("(|^)\\d{4}");
                    Matcher m = p.matcher(smsMessageBody);
                    if (m.find()) {
                        String otp = m.group(0);
                        if (mListener != null)
                            mListener.populateSMSOtp(otp);

                    }
                }

            }
        }
    }



    public static void bindListener(SMSCallBackInterface listener) {
        mListener = listener;
    }


}
