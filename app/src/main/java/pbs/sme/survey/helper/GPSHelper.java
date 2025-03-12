package pbs.sme.survey.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

public class GPSHelper {
    LocationManager locationManager;
    int mtime=1000*20;
    Boolean GPS;
    Boolean NET;

    public GPSHelper(Context mContext, LocationListener listener){
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        GPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        NET = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!GPS && !NET) {
        } else {
            try {
                if (NET) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mtime, 10, listener);
                }
                if (GPS) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mtime, 10, listener);
                }
            } catch (SecurityException e) {

            }
        }
    }

    public int checkProviders(){
        if(GPS && NET){
            return 3;
        }
        else if(GPS){
            return 1;
        }
        else if(NET){
            return 2;
        }
        else{
            return 0;
        }
    }

    public LocationManager getManager(){
        return locationManager;
    }


    public Location getLocation(final Activity mContext, LocationListener listener){
        Location loc = null;

        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        Boolean gps = locationManager .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean net = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps && !net) {
            final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        } else {
            try{
                if(net){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,5,listener);
                    loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (loc != null) {
                        return loc;
                    }
                }
                if(gps){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,5, listener);
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }catch (SecurityException e) {
            }
        }
        return loc;
    }



    public Location getCurrentLocation(final Activity mContext, LocationListener listener){
        Location loc = null;

        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        Boolean gps = locationManager .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean net = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!gps && !net) {
            final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(intent);
        } else {
            try{
                if(net){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,5,listener);

                    loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (loc != null) {
                        return loc;
                    }
                }
                if(gps){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,5, listener);
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }catch (SecurityException e) {
            }
        }
        return loc;
    }



    public Location getGPSLocation(final Context mContext, LocationListener listener){
        Location loc = null;
        Boolean gps = locationManager .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gps){
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,5, listener);
                loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }catch (SecurityException e) {
            }

        }
        return loc;
    }

    public Location getNetLocation(final Context mContext, LocationListener listener){
        Location loc = null;
        Boolean net = locationManager .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(net){
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,5, listener);
                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }catch (SecurityException e) {
            }
        }
        return loc;
    }

    public static String AllowedOtuside(int flag, int margin,int disance){
        if(flag==0 && disance>margin){
            return " آپ بلاک سے "+disance+" میٹر دور ہیں جبکہ اجازت صرف "+margin+" میٹر ہے۔";
        }
        return null;
    }



}
