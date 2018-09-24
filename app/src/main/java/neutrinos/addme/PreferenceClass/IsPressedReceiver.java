package neutrinos.addme.PreferenceClass;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;



public class IsPressedReceiver extends IntentService {
    public IsPressedReceiver() {
        super("MyIntentServiceFacility");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent in= new Intent();
        Boolean model ;
        if (intent != null) {
            model = intent.getBooleanExtra("isPressed", false);
            in.setAction("IsPressedReceiver");
            in.putExtra("isPressed", model);
            sendBroadcast(in);
        }
    }
}
