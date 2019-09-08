package com.example.reto1cristian;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleMap.OnMapLongClickListener  {

    private GoogleMap mMap;
    private Marker user;
    private ArrayList<Marker> places;
    private Button add_Location;
    private TextView information;
    private boolean acess;
    private RelativeLayout dialog;
    private Button continueB;
    private Button cancel;
    private EditText nameE;
    private LatLng lugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        places = new ArrayList<Marker>();

        add_Location = findViewById(R.id.Add_Location_Btn);
        information = findViewById(R.id.Information_Tv);
        continueB = findViewById(R.id.Continue_Btn);
        cancel = findViewById(R.id.Cancel_Btn);
        dialog = findViewById(R.id.Dialog_Rl);
        nameE = findViewById(R.id.Name_Et);

        acess = false;

        // Asks for permissions
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        }, 11);

        add_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (acess) {
                    acess = false;
                    add_Location.setText("+");
                } else {
                    acess = true;
                    add_Location.setText("x");
                }
            }
        });

        continueB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Marker m = mMap.addMarker(new MarkerOptions().position(lugar).title(nameE.getText().toString()));
                places.add(m);
                nameE.setText("");
                dialog.setVisibility(View.GONE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameE.setText("");
                dialog.setVisibility(View.GONE);
            }
        });
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        // Add a marker in Sydney and move the camera
        LatLng colombia = new LatLng(4.0000000, -74.035302);
        user = mMap.addMarker(new MarkerOptions().position(colombia).title("Mi ubicación").snippet("hola").icon(BitmapDescriptorFactory.fromResource(R.drawable.yo)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombia, 5));

        LocationManager manager = (LocationManager) getSystemService((LOCATION_SERVICE));
        manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng coord = new LatLng(location.getLatitude(), location.getLongitude());
        user.setPosition(coord);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord,15));
        dataChanged();
        Geocoder geo = new Geocoder(this);
        try {
            List<Address> cambio = geo.getFromLocation(user.getPosition().latitude,user.getPosition().longitude,1);
            user.setSnippet(cambio.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(acess){
            lugar = latLng;
            dialog.setVisibility(View.VISIBLE);
        }
    }

    public void dataChanged(){
        double miniDis = -1;
        int miniIn = -1;
        for(int i = 0; i<places.size();i++){
            double latDiff = places.get(i).getPosition().latitude - user.getPosition().latitude;
            double longDiff = places.get(i).getPosition().latitude - user.getPosition().latitude;
            double distancia = Math.sqrt(Math.pow(latDiff,2)+Math.pow(longDiff,2));
            distancia = distancia*111.12*1000;
            if(i==0){
                miniDis = distancia;
                miniIn = i;
            }
            places.get(i).setSnippet("Usted está a "+distancia+" metros de "+ places.get(i).getTitle());
            if(distancia<miniDis){
                miniDis = distancia;
                miniIn = i;
            }
        }
        if(miniDis>=0&&miniIn!=-1){
            if(miniDis<300){
                information.setText("Usted está en "+places.get(miniIn).getTitle());
            }else{
                information.setText("El lugar más cercano es "+places.get(miniIn).getTitle());
            }
        }
    }
}