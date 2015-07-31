package app.nevvea.nomnom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnTaskFinishedListener{

    GoogleApiClient mGoogleApiClient;
    GoogleMap mMap;

    TextView yelpResultTextView;
    Button getResultButton;

    double curLongitude;
    double curLatitude;
    LatLng mapCameraLatLng;

    // to store names of the last returned results
    HashMap<String, String> restaurants = new HashMap<>();

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        yelpResultTextView = (TextView) rootView.findViewById(R.id.cur_location_result);

        getResultButton = (Button) rootView.findViewById(R.id.get_location_button);
        getResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGoogleApiClient.isConnected()) {

                    mapCameraLatLng = mMap.getCameraPosition().target;

                    curLatitude = mapCameraLatLng.latitude;
                    curLongitude = mapCameraLatLng.longitude;

                    onLocationChaged();
//                    new AsyncTask<Void, Void, String>() {
//                        @Override
//                        protected String doInBackground(Void... params) {
//                            Yelp yelp = Yelp.getYelp(getActivity());
//                            String businesses = yelp.search("restaurants", curLatitude, curLongitude);
//                            try {
//                                return Utility.processJson(businesses);
//                            } catch (JSONException e) {
//                                Log.e("json error", e.toString());
//                                return businesses;
//                            }
//                        }
//
//                        @Override
//                        protected void onPostExecute(String result) {
//                            yelpResultTextView.setText(result);
//                        }
//                    }.execute();
                }
            }
        });

        return rootView;
    }


    public void onLocationChaged() {
        FetchRestaurantsTask fetchRestaurantsTask = new FetchRestaurantsTask(getActivity(), this);
        fetchRestaurantsTask.execute(curLatitude, curLongitude);
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }


    public void setmGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public void setmMap(GoogleMap googleMap) {
        mMap = googleMap;
    }

    // called by FetchRestaurantsTask in PostExecute
    @Override
    public void onTaskFinished(HashMap<String, String> result) {
        restaurants = result;
        for (String key : restaurants.keySet()) {
            Log.d("map check", key);
        }

    }
}
