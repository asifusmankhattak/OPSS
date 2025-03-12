package pbs.sme.survey.activity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import pbs.sme.survey.helper.SyncService;
import pbs.sme.survey.model.Constants;
import pk.gov.pbs.utils.SystemUtils;

public class CustomApplication extends Application {
    protected static SharedPreferences preferences;
    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(Constants.PREF,MODE_PRIVATE);
        SystemUtils.createNotificationChannel(
                this
                , Constants.SYNC_SERVICE_NOTIFICATION_CHANNEL_Name
                , Constants.SYNC_SERVICE_NOTIFICATION_CHANNEL_ID
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, SyncService.class));
        } else {
            startService(new Intent(this, SyncService.class));
        }
    }

    public static SharedPreferences getSharedPreferences(){
        return preferences;
    }
}
