package com.example.sleepy.hikerwatchapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Geocoder geocoder;
    String resultAddress;
    LocationManager locationManager;
    LocationListener locationListener;
    TextView latTextView, longTextView, accTextView, altTextView, addressTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultAddress = "";
        latTextView = (TextView) findViewById(R.id.latTextView);
        longTextView = (TextView) findViewById(R.id.longTextView);
        accTextView = (TextView) findViewById(R.id.accTextView);
        altTextView = (TextView) findViewById(R.id.altTextView);
        addressTextView = (TextView) findViewById(R.id.addressTextView);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener  = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location",location.toString());
                Double latValue = location.getLatitude();
                latTextView.setText("Latitude: " + latValue.toString());

                Double longValue = location.getLongitude();
                longTextView.setText("Longitude: " + longValue.toString());

                Double altValue = location.getAltitude();
                altTextView.setText("Altitude: " + altValue.toString());

                Float accValue = location.getAccuracy();
                accTextView.setText("Accuracy: " + accValue.toString());

                try{
                    List<Address> addressList =  geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                    if(addressList != null && addressList.size()>0){
                        Log.i("address", addressList.get(0).toString());
                        //addressTextView.setText(addressList.get(0).getAddressLine(0).toString());
                        //addressTextView.setText(addressList.get(0).getLocality().toString());

                        if(addressList.get(0).getLocality().toString() != null)
                            resultAddress += addressList.get(0).getLocality().toString() + "\n";
                        if(addressList.get(0).getAdminArea().toString() != null)
                            resultAddress += addressList.get(0).getAdminArea().toString() + "\n";
                        if(addressList.get(0).getPostalCode().toString() != null)
                            resultAddress += addressList.get(0).getPostalCode().toString() + "\n";
                        if(addressList.get(0).getCountryName().toString() != null)
                            resultAddress += addressList.get(0).getCountryName().toString() + "\n";

                        addressTextView.setText("Address: " + resultAddress);
                        resultAddress = "";

                    }else{
                        addressTextView.setText("Address: No Valid Address Available");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastKnownAdd = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownAdd != null){
                    Log.i("Location",lastKnownAdd.toString());
                    Double latValue = lastKnownAdd.getLatitude();
                    latTextView.setText("Latitude: " + latValue.toString());

                    Double longValue = lastKnownAdd.getLongitude();
                    longTextView.setText("Longitude: " + longValue.toString());

                    Double altValue = lastKnownAdd.getAltitude();
                    altTextView.setText("Altitude: " + altValue.toString());

                    Float accValue = lastKnownAdd.getAccuracy();
                    accTextView.setText("Accuracy: " + accValue.toString());
                }


            }
        }



    }


}
