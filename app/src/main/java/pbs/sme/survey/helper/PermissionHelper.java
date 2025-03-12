package pbs.sme.survey.helper;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.model.Constants;

public class PermissionHelper {

    public static boolean checkAll(Context context, Activity activity){
        List<String> per=new ArrayList<>();
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            per.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            per.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            per.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            per.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            per.add(Manifest.permission.READ_PHONE_STATE);

        if(per.size()==0){
            return true;
        }
        else{
            activity.requestPermissions(per.toArray(new String[0]), Constants.PERMISSION_ALL_CODE);
            return false;
        }
    }


    public static boolean checkPhone(Context context, Activity activity){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else if(activity.shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){
            activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.PERMISSION_PHONE_CODE);
            return false;
        }
        else{
            RequestAgain("Phone", context, activity);
            return false;
        }
    }


    public static void RequestAgain(String perm, Context context, Activity activity)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 1000);
    }
}
