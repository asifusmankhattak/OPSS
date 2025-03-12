package pbs.sme.survey.helper;

import static android.graphics.Color.RED;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.Toast;

import pbs.sme.survey.R;
import pbs.sme.survey.api.ApiClient;
import pbs.sme.survey.api.ApiInterface;
import pbs.sme.survey.model.Result;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkHelper {
    Context context;
    TextView terror;
    Throwable throwable;

    public NetworkHelper(Context c, TextView t){
        context=c;
        terror=t;
        FailureChecks();
    }

    public NetworkHelper(Context c){
        context=c;
    }

    public NetworkHelper(Context c, TextView t, Throwable issue){
        context=c;
        terror=t;
        throwable=issue;
        FailureChecks();
    }
    private void FailureChecks(){
        boolean w = isWifiEnabled();
        boolean m = isMobileDataEnabled();
        if(w==false && m==false){
            displayError("Please 'Turn On' Wifi or Mobile Data");
        }
        else if (w == true && m==false) {
            boolean wC = isWifiConnected();
            if (wC == false) {
                displayError("Wifi is Enabled but not Connected to any Network.");
            }
            else{
                boolean i = isInternetAvailable();
                if (i == false) {
                    displayError("Wifi is Enabled & Connected but No Internet.");
                }
                else {
                    ApiResponse();
                }
            }
        }
        else if(m == true && w==false) {
            boolean mC = isMobileDataConnected();
            if (mC == false) {
                displayError("Mobile Data is Enabled but not Connected to any Network.");
            }
            else{
                boolean i = isInternetAvailable();
                if (i == false) {
                    displayError("Mobile Data is Enabled & Connected but No Internet");
                } else {
                    ApiResponse();
                }
            }
        }
        else{
            boolean wC = isWifiConnected();
            boolean mC = isMobileDataConnected();
            if (wC == false && mC==false) {
                displayError("Mobile Data and Wifi is Enabled but not Connected to any Network.");
            }
            else{
                if(isInternetAvailable()){
                    ApiResponse();
                }
                else{
                    displayError("Internet is Not Available");
                }
            }

        }
    }

    public boolean isWifiEnabled() {
        boolean isWifi = false;
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled()){
            isWifi = true;
        }
        return isWifi;
    }

    public boolean isMobileDataEnabled() {
        boolean isMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try{
            Class cmClass=Class.forName(cm.getClass().getName());
            Method method=cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            isMobile=(Boolean) method.invoke(cm);
        }
        catch (Exception e){

        }
        return isMobile;
    }

    public boolean isWifiConnected() {
        boolean haveConnectedWifi = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
        }
        return haveConnectedWifi;
    }

    public boolean isMobileDataConnected() {
        boolean haveConnectedMobileData = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(activeNetwork != null){
            haveConnectedMobileData = true;
        }
        return haveConnectedMobileData;
    }

    public boolean isConnected() {
        try {
            InetAddress ipAddr = InetAddress.getByName("https://www.google.com");
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInternetAvailable() {
        try{
            String command = "ping -c 1 http://iacapi.pbos.gov.pk/";
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        }
        catch (Exception e){
            return false;
        }
    }

    private void ApiResponse(){
        Call<Result> call= ApiClient.getApiClient().create(ApiInterface.class).apiResponse();
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(response.code()==200){
                    displayError(throwable.getMessage());
                }
                else{
                    displayError("Test Server Connection FAILED, Error Code: "+response.code());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                displayError("Test Server is not Responding.");
            }
        });
    }




    public void displayError(String msg){
        terror.setTextColor(RED);
        terror.setText(msg);
        Toast.makeText(context,throwable.getMessage(),Toast.LENGTH_LONG).show();
    }



}
