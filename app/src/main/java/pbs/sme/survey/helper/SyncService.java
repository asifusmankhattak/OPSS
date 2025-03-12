package pbs.sme.survey.helper;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.DB.ListingProvider;
import pbs.sme.survey.R;
import pbs.sme.survey.activity.MainActivity;
import pbs.sme.survey.model.Constants;
import pk.gov.pbs.utils.StaticUtils;

public class SyncService extends Service {
    private static SyncService instance;
    private static final String TAG = "SyncService";
    private static final int SERVICE_NOTIFICATION_ID = 99;
    private static final int SYNC_JOB_KEY = 101;
    private static int SINGLE_SYNC_JOB_KEY = 0;
    public static final int RESCHEDULE_PERIOD = 30 * 60 * 1000;
    public static final String EXTRA_HOUSE_UID = "house_uid";
    public static final String EXTRA_HOUSEHOLD_UID = "hh_uid";
    private final SyncServiceBinder mBinder = new SyncServiceBinder();
    private JobScheduler mScheduler;
    private ContentResolver mResolver;
    private Runnable messageLoop;
    public static final String ENUM_SYNC_BROADCAST = "pbs.iac.enumeration.activity.SyncBroadcast";
    public static final String BROADCAST_HOUSEHOLD_CHANGE = "pbs.iac.listing.activity.SyncBroadcast";
    private final BroadcastReceiver syncBroadcastReceiver = new SyncBroadcastReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        mResolver = getContentResolver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_HOUSEHOLD_CHANGE);
        filter.addAction(ENUM_SYNC_BROADCAST);
        registerReceiver(syncBroadcastReceiver, filter);
        instance = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, Constants.SYNC_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Sync Service")
                .setContentText("Data Synchronization Service")
                .setSmallIcon(R.drawable.ic_sync)
                .setContentIntent(
                        PendingIntent.getActivity(
                                this, 0,
                                new Intent(this, MainActivity.class),
                                PendingIntent.FLAG_IMMUTABLE
                        )
                )
                .build();
        startForeground(SERVICE_NOTIFICATION_ID, notification);
        syncMessageLoop();
        return START_STICKY;
    }

    private void syncMessageLoop(){
        messageLoop = () -> {
            StaticUtils.getHandler().postDelayed(
                    messageLoop,
                    RESCHEDULE_PERIOD
            );
            scheduleSyncTask();
        };
        messageLoop.run();
    }

    private void scheduleSyncTask(){
        mScheduler.cancelAll();
        String[] unsynced_uid = getUnsyncedUID();
        if (unsynced_uid != null) {
            ComponentName task = new ComponentName(this, SyncScheduler.class);
            PersistableBundle bundle = new PersistableBundle();

            bundle.putStringArray("listing_house_uid", unsynced_uid);
            JobInfo.Builder builder = new JobInfo.Builder(SYNC_JOB_KEY, task)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setExtras(bundle);
            mScheduler.schedule(builder.build());
        }
    }

    private void singleSyncRequest(String hh_uid){
        if (hh_uid != null) {
            ComponentName task = new ComponentName(this, SyncScheduler.class);
            PersistableBundle bundle = new PersistableBundle();
            bundle.putStringArray("listing_house_uid", new String[]{hh_uid});
            JobInfo.Builder builder = new JobInfo.Builder(++SINGLE_SYNC_JOB_KEY, task)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setExtras(bundle);
            mScheduler.schedule(builder.build());
        }
    }

    private void singleEnumSyncRequest(String uid){
        if (uid != null) {
            ComponentName task = new ComponentName(this, SyncScheduler.class);
            PersistableBundle bundle = new PersistableBundle();
            bundle.putStringArray("enum_household_uid", new String[]{uid});
            JobInfo.Builder builder = new JobInfo.Builder(++SINGLE_SYNC_JOB_KEY, task)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setExtras(bundle);
            mScheduler.schedule(builder.build());
        }
    }

    private String[] getUnsyncedUID(){
        Cursor cursor = mResolver.query(ListingProvider.URI_UNSYNCED_HOUSE_UID, null, null, null, null);
        if (cursor != null){
            List<String> uids = new ArrayList<>();
            if (cursor.getCount() > 0){
                while (cursor.moveToNext()) {
                    uids.add(cursor.getString(0));
                }
            }
            cursor.close();
            return uids.toArray(new String[0]);
        }
        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(syncBroadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class SyncServiceBinder extends Binder implements ISyncService {
        public SyncService getService(){
            return SyncService.this;
        }

        @Override
        public void syncListing(String uid) {
            singleSyncRequest(uid);
        }

        @Override
        public void syncEnumeration(String uid) {
            singleEnumSyncRequest(uid);
        }
    }

    public static class SyncBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(BROADCAST_HOUSEHOLD_CHANGE)){
                String uid = intent.getStringExtra(EXTRA_HOUSE_UID);
                if (uid != null && !uid.isEmpty()){
                    instance.singleSyncRequest(uid);
                }
                else
                {
                    instance.scheduleSyncTask();
                }

            } else if (intent.getAction().equalsIgnoreCase(ENUM_SYNC_BROADCAST)){
                String uid = intent.getStringExtra(EXTRA_HOUSEHOLD_UID);
                if (uid != null && !uid.isEmpty()){
                    instance.singleEnumSyncRequest(uid);
                } else {
                    instance.scheduleSyncTask();
                }
            }
        }
    }
}