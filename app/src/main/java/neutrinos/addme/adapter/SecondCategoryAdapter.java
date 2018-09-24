package neutrinos.addme.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import neutrinos.addme.R;
import neutrinos.addme.activity.CategoryDashboardActivity;
import neutrinos.addme.activity.Offers;
import neutrinos.addme.fragment.GridModelClass;
import neutrinos.addme.utilities.Logger;

/**
 * Created by mahiti on 31/03/18.
 */

public class SecondCategoryAdapter extends RecyclerView.Adapter<SecondCategoryAdapter.ViewHolder> {

    private Activity activity;
    ArrayList<GridModelClass> detailsListCategory;

    public SecondCategoryAdapter(Activity activity, ArrayList<GridModelClass> mAll) {
        this.detailsListCategory = mAll;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.categorygridadapter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        setAnimation(viewHolder.itemView, position);
        if (!detailsListCategory.get(position).equals("")) {
            Picasso.get()
                    .load(detailsListCategory.get(position).getImageUrl())
                    .placeholder(R.drawable.logo)
                    .fit()
                    .error(R.drawable.logo)
                    .into(viewHolder.imageView);
        }
        viewHolder.textView.setText(detailsListCategory.get(position).getProduct_description());
        if (detailsListCategory.get(position).isWishstatus()) {
            viewHolder.wishlistbtn.setLiked(true);
        } else {
            viewHolder.wishlistbtn.setLiked(false);
        }
        viewHolder.wishlistbtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                setUserListToServer(detailsListCategory.get(position), 100);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                setUserListToServer(detailsListCategory.get(position), 200);
            }
        });
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.logD("imagecheck", "" + String.valueOf(detailsListCategory.get(position).getImageUrl()));
                Bundle bundle = new Bundle();
                bundle.putString("image", String.valueOf(detailsListCategory.get(position).getImageUrl()));
                bundle.putSerializable("modelcalss", detailsListCategory.get(position));
                Intent intent = new Intent(activity, Offers.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });
    }

    private void setUserListToServer(GridModelClass gridListWishlist, int flag) {
        if (gridListWishlist != null) {
            try {
                JSONArray array = new JSONArray();
                JSONObject item = new JSONObject();
                item.put("uniquename", gridListWishlist.getFileName());
                array.put(item);
                Log.d("jsonarray", String.valueOf(array));
                if (flag == 100)
                    callwishlistserverSave("test@liferay.com", "test", array, getDeviceId(), "save");
                else {
                    callwishlistserverSave("test@liferay.com", "test", array, getDeviceId(), "delete");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "List is empty", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * @return to fetch the device id
     */
    private String getDeviceId() {
        /* String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);*/
        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private void callwishlistserverSave(final String username, final String password, JSONArray jsonStructure, String deviceId, String flag) {
        String url = "http://216.98.9.235:8080/api/jsonws/addMe-portlet.user_favourite/User-favourites/uniquename/"
                + jsonStructure + "/deviceaddress/" + deviceId + "/status/" + flag + "/key/wishlist";
        String convertedURL = url.replace(" ", "%20");
        Logger.logV("favapi", " callServerToSendParams " + convertedURL);

        StringRequest postRequest = new StringRequest(Request.Method.POST, convertedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.logV("favresponse", " Response -->" + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Logger.logV("Location", " ERROR " + error);
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = username + ":" + password;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        Volley.newRequestQueue(activity).add(postRequest);
    }

    private int mLastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > mLastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return detailsListCategory.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;
        LikeButton wishlistbtn;


        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.grid_image);
            textView = (TextView) view.findViewById(R.id.grid_text);
            wishlistbtn = view.findViewById(R.id.wishlist_category_id);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
