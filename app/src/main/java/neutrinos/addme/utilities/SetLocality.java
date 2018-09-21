package neutrinos.addme.utilities;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Created by mahiti on 7/4/17.
 */

public class SetLocality
{
    public SetLocality() {
    }

    public static void setLocale(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}
