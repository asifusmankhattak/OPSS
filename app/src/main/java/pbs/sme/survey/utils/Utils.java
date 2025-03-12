package pbs.sme.survey.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static String getMacAddress(Context context) {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : all) {
                if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes == null) {
                    Toast.makeText(context.getApplicationContext(),"nope",Toast.LENGTH_LONG).show();
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().replace(":","-");
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public static String getIMEIDeviceId(Context context) {
        String deviceId="";
        try{
            if (Build.VERSION.SDK_INT >= 29)
            {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return "";
                    }
                }
                assert mTelephony != null;
                if (mTelephony.getDeviceId() != null)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        deviceId = mTelephony.getImei();
                    }else {
                        deviceId = mTelephony.getDeviceId();
                    }
                } else {
                    deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            }
        }
        catch(SecurityException e){
            return "Security: "+e.getMessage().substring(0,35);
        }
        catch (Exception e){
            return "Exception: "+e.getMessage().substring(0,35);
        }

        return deviceId;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public static Integer GetInteger(String toString) {
        Integer value = null;
        return value;
    }
}
