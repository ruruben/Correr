package com.example.p.correr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador; // POSICION EN LA CUAL ESTAMOS
    double lat = 0.0, lng = 0.0;
    double dis=0;
    DecimalFormat df = new DecimalFormat("0.00");
    List<LatLng> latlong = new ArrayList<LatLng>();
    List<Location> loc = new ArrayList<Location>();
    Button inicio,parar,fin;
    Chronometer cronometro;
    long Time=0;
    TextView distancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cronometro = (Chronometer) findViewById(R.id.cronometro);
        inicio = (Button) findViewById(R.id.inicio);
        parar = (Button) findViewById(R.id.pausar);
        fin = (Button) findViewById(R.id.fin);
        distancia = (TextView) findViewById(R.id.distancia);

        inicio.setEnabled(true);
        parar.setEnabled(false);
        fin.setEnabled(false);

        inicio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                inicio.setEnabled(false);
                parar.setEnabled(true);
                fin.setEnabled(true);
                cronometro.setBase(SystemClock.elapsedRealtime());
                cronometro.start();
            }
        });

        parar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                inicio.setEnabled(true);
                parar.setEnabled(false);
                fin.setEnabled(true);
                cronometro.stop();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();


    }


    private void agregarMarcador(LatLng l) {

        LatLng coordenadas = new LatLng(l.latitude, l.longitude);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                        .position(coordenadas)
                        .title("Posicion actual")
                //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)


        );

        mMap.animateCamera(miUbicacion);

        // PROCEDEMOS A AÑADIR LAS UBICACIONES
        latlong.add(l);
        mMap.addPolyline(new PolylineOptions().addAll(latlong)
                .width(10)
                .color(Color.BLUE)
        );


    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();

           if(loc.size()>0){
               double f = location.distanceTo(loc.get(loc.size()-1));
               dis+=(double)f/1000;
               distancia.setText(df.format(dis)+" Km");
           }

            loc.add(location);

            agregarMarcador(new LatLng(location.getLatitude(), location.getLongitude()));
        }

    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
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

    private void miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);//ACTUALIZAMOS LA UBICACION
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15000,0,locListener); //llamaremos al GPS CADA 15 SEG

    }




}


