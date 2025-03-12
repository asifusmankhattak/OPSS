package pbs.sme.survey.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SyncServiceInitiate extends BroadcastReceiver {
    private static final String TAG = "SyncServiceInitiate";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(new Intent(context, SyncService.class)));
        }
    }
}
