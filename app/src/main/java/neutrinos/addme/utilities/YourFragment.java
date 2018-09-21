package neutrinos.addme.utilities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import neutrinos.addme.R;

/**
 * Created by NS_USER on 24-Mar-18.
 */

public class YourFragment extends android.support.v4.app.Fragment {

    public YourFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
