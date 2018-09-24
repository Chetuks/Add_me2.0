package neutrinos.addme.utilities;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED;


public class GPSTracker extends Service implements LocationListener {
	private static final int REQUEST_ACCESS_FINE_LOCATION = 0;
	private Context mContext;

	// flag for GPS Status
	public boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	public boolean canGetLocationFlag = false;
	public int gps_tracker = 0;

	Location location = null;
	public double latitude;
	public double longitude;

	final private int REQUEST_CODE_ASK_PERMISSIONS = 124;

	// The minimum distance to change updates in metters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 0; // 1 minute

//	int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

	// Declaring a Location Manager
	protected LocationManager locationManager;
	SharedPreferences sp;

	public GPSTracker(Context context) {
		this.mContext = context;
		sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		getLocation();

	}

	public GPSTracker() {
		// default constructor
	}

	public Location getLocation() {

		try {
			location = null;
			ConnectivityManager connManager = (ConnectivityManager) ((ContextWrapper) mContext).getBaseContext().getSystemService(mContext.CONNECTIVITY_SERVICE);

			State mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

			//wifi
			State wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
			Logger.logV("the prefferenece value is", "the prefreence values are" + sp.getString("LocationSelectionMode", ""));
			if ("1".equals(sp.getString("LocationSelectionMode", ""))) {
				Logger.logV("GPS", "internet location capturing its going first if block");
				// getting network status
				isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				// if GPS Enabled get lat/long using GPS Services
				if (isNetworkEnabled && (mobile == State.CONNECTED || mobile == State.CONNECTING || wifi == State.CONNECTED || wifi == State.CONNECTING)) {
					Logger.logV("GPS", "internet location capturing");
					this.canGetLocationFlag = true;
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());
					getLocationUpdates();

				} else {
					Logger.logV("GPS", "internet location capturing its going else  block");

					if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
							PackageManager.PERMISSION_GRANTED &&
							ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
									PackageManager.PERMISSION_GRANTED) {

					} else {
						turnGPSOn();
						// getting GPS status
						isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
						double lat = 0.0;
						// First get location from Network Provider
						if (location == null || (Double.compare(location.getLatitude(), lat) == 0 && Double.compare(location.getLongitude(), lat) == 0)) {

							if (isGPSEnabled) {
								Logger.logV("GPS", "gps location capturing");
								this.canGetLocationFlag = true;
								locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
										MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());


								if (locationManager != null) {
									location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
									if (location != null) {
										gps_tracker = 0;
										updateGPSCoordinates(location);
										Logger.logV("GPS", "gps location captured" + location.getLatitude());
										Logger.logV("GPS", "gps location captured" + location.getLongitude());
									}
								}
							} else {
								location = null;
							}
						}
					}
				}
			} else {
				Logger.logV("GPS", "internet location capturing its going else  block");

				turnGPSOn();
				// getting GPS status
				isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				double lat = 0.0;
				// First get location from Network Provider
				if (location == null || (Double.compare(location.getLatitude(), lat) == 0 && Double.compare(location.getLongitude(), lat) == 0)) {

					if (isGPSEnabled) {
						Logger.logV("GPS", "gps location capturing");
						this.canGetLocationFlag = true;
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());


						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								gps_tracker = 0;
								updateGPSCoordinates(location);
								Logger.logV("GPS", "gps location captured" + location.getLatitude());
								Logger.logV("GPS", "gps location captured" + location.getLongitude());
							}
						}
					} else {
						location = null;
					}
				}


			}
			/*// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			// if GPS Enabled get lat/long using GPS Services
			if (isNetworkEnabled && (mobile== State.CONNECTED||mobile== State.CONNECTING || wifi== State.CONNECTED|| wifi== State.CONNECTING))
			{
				Logger.logV("GPS", "internet location capturing");
				this.canGetLocationFlag = true;
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());
				getLocationUpdates();

			}
			double lat = 0.0;
			// First get location from Network Provider
			if(location==null || (Double.compare(location.getLatitude(),lat)==0 && Double.compare(location.getLongitude(),lat)==0))
			{

				if (isGPSEnabled)
				{
					Logger.logV("GPS","gps location capturing");
					this.canGetLocationFlag = true;
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this, Looper.getMainLooper());


					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if(location!=null){
							gps_tracker = 0;
							updateGPSCoordinates(location);
							Logger.logV("GPS", "gps location captured" + location.getLatitude());
							Logger.logV("GPS", "gps location captured"+location.getLongitude());
						}
					}
				}
				else
				{
					location=null;
				}
			}
			*/
		} catch (Exception e) {
			Logger.logE(GPSTracker.class.getSimpleName(), "Exception in getLocation method ", e);

		}

		return location;
	}


	/*private void requestPermission(String location,int i) {
		List<String> list = new ArrayList();

		if (!ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.ACCESS_FINE_LOCATION))
			list.add(Manifest.permission.ACCESS_FINE_LOCATION);
		if (!ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.ACCESS_COARSE_LOCATION))
			list.add(Manifest.permission.ACCESS_COARSE_LOCATION);

		//Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

		String[] stockArr = new String[list.size()];
		stockArr = list.toArray(stockArr);
		if (stockArr.length != 0) {
			ActivityCompat.requestPermissions(mContext, stockArr, 1);
		}

	}*/

	/**
	 * Get list of address by latitude and longitude
	 *
	 * @return null or List<Address>
	 */
	public List<Address> getGeocoderAddress(Context context) {
		if (location != null) {
			Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
			try {
				List<Address> addresses = geocoder.getFromLocation(latitude,
						longitude, 1);
				return addresses;
			} catch (IOException e) {
				// e.printStackTrace();
				Log.e("Error : Geocoder", "Impossible to connect to Geocoder",
						e);
			}
		}

		return null;
	}

	/**
	 * Try to get AddressLine
	 *
	 * @return null or addressLine
	 */
	public String getAddressLine(Context context) {
		List<Address> addresses = getGeocoderAddress(context);
		if (addresses != null && addresses.size() > 0) {
			Address address = addresses.get(0);
			String addressLine = address.getAddressLine(0);

			return addressLine;
		} else {
			return null;
		}
	}

	private boolean checkPermission() {

		int gpsACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
		if (gpsACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
			return false;
		} else {
			return true;
		}

	}


	private void turnGPSOn() {
		String provider = Settings.Secure.getString(mContext.getContentResolver(), LOCATION_PROVIDERS_ALLOWED);
		try {
			if (!provider.contains("gps")) { //if gps is disabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				sendBroadcast(poke);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getLocationUpdates() {
		if (locationManager != null) {
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				gps_tracker = 1;
				updateGPSCoordinates(location);
			}
		}
	}

	public void updateGPSCoordinates(Location location) {
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 */

	public void stopUsingGPS() {
		if (locationManager != null) {
			if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			locationManager.removeUpdates(GPSTracker.this);
		}
	}


	/**
	 * Function to check GPS/wifi enabled
	 */
	public boolean canGetLocation() {
		return this.canGetLocationFlag;
	}


	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		updateGPSCoordinates(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		/*Nothing to do in this method   */
	}

	@Override
	public void onProviderEnabled(String provider) {
				/*Nothing to do in this method   */

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
				/*Nothing to do in this method   */


	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
