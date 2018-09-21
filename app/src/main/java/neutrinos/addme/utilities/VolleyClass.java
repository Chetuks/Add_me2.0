package neutrinos.addme.utilities;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import neutrinos.addme.Interface.VolleyResults;

/**
 * Created by mahiti on 13/3/18.
 */

public class VolleyClass {


    public static void getRequest(String mUrl, Context context, final VolleyResults resultInterface, final int i)
    {

        RequestQueue queue = Volley.newRequestQueue(context);


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        resultInterface.onSuccess(response,i);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Display the first 500 characters of the response string.
                resultInterface.onCancel(error.getMessage(),i);

            }
        });

// Add the request to the RequestQueue.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}
