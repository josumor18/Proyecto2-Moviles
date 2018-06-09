package moviles.apps.proyecto2.friendtec.Business;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        getMyLocation(true);

        ExecuteGetFriendsLocations executeGetFriendsLocations = new ExecuteGetFriendsLocations();
        executeGetFriendsLocations.execute();
    }

    private void getMyLocation(boolean init){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, 0);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            mMap.clear();

            LatLng location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            Bitmap markerBitmap = (Usuario_Singleton.getInstance().getFoto_rounded());
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(markerBitmap, 80, 80, false);

            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)).position(location).title("Tú"));
            if (init){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(7));
            }
        }
    }

    public void cargarLocations(JSONObject response) {
        Usuario_Singleton user = Usuario_Singleton.getInstance();
        try {
            JSONArray jsonLocations= response.getJSONArray("locations");
            for(int i = 0; i < jsonLocations.length(); i++){
                JSONObject location = (JSONObject) jsonLocations.get(i);

                int id_amigo = location.getInt("id_user");
                double latitud = location.getDouble("latitud");
                double longitud = location.getDouble("longitud");

                Usuario amigo = Usuario_Singleton.getInstance().getAmigo(id_amigo);
                LatLng user_location = new LatLng(latitud, longitud);

                HttpGetBitmap request = new HttpGetBitmap();
                Bitmap userImage = null;
                try {
                    userImage = request.execute(amigo.getLink_rfoto()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(userImage != null){
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(userImage, 80, 80, false);

                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)).position(user_location).title(amigo.getNombre()));
                }else{
                    mMap.addMarker(new MarkerOptions().position(user_location).title(amigo.getNombre()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetFriendsLocations extends AsyncTask<String, Void, String> {
        private boolean isOk = false;

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();
            isOk = api.getFriendsLocations(Usuario_Singleton.getInstance().getId());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarLocations(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener ubicaciones de amigos";
                try {
                    mensaje = (API_Access.getInstance().getJsonObjectResponse()).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MapsActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        }
    }
}