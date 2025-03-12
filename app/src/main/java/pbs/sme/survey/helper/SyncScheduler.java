package pbs.sme.survey.helper;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.PersistableBundle;

import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.DB.ListingProvider;
import pbs.sme.survey.api.ApiClient;
import pbs.sme.survey.api.ApiInterface;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.online.Returning;
import pbs.sme.survey.online.Sync;
import pk.gov.pbs.database.DatabaseUtils;
import pk.gov.pbs.utils.ExceptionReporter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncScheduler extends JobService {
    int dev = 1;
    private static final String TAG = "SyncScheduler";
    public static final String BROADCAST_LISTING_SYNCED = SyncScheduler.class.getCanonicalName() + ".ListingHouseSynced";
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        PersistableBundle bundle = jobParameters.getExtras();
        String[] houseUIDs = bundle.getStringArray("listing_house_uid");
        String[] enumHouseUIDs = bundle.getStringArray("enum_household_uid");

        if (houseUIDs != null && houseUIDs.length > 0){
            for (String uid : houseUIDs){
                try {
                    Sync unsyncedHouse = getHouse(uid);
                    if (unsyncedHouse != null){
                        syncHouse(unsyncedHouse);
                        StatsUtil.updateSyncTimeToNow();
                    }

                } catch (Exception e) {
                    ExceptionReporter.handle(e);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void syncHouse(final Sync house){
        Call<Returning> call= ApiClient.getApiClient().create(ApiInterface.class).upload(house);
        call.enqueue(new Callback<Returning>() {
            @Override
            public void onResponse(Call<Returning> call, Response<Returning> response) {
                Returning resp = response.body();
                if(resp != null && resp.getCode()==1){
                    setSyncedListingHouse(house);
                    Intent intent = new Intent();
                    intent.setAction(BROADCAST_LISTING_SYNCED);
                    sendBroadcast(intent);

                }
            }
            @Override
            public void onFailure(Call<Returning> call, Throwable t) {
            }
        });
    }


    private Sync getHouse(String UID) throws IllegalAccessException, InstantiationException {
        Cursor hhCursor = getContentResolver().query(ListingProvider.URI_UNSYNCED_HOUSEHOLD, null, "house_uid=?", new String[]{ UID }, null);
        if(hhCursor != null && hhCursor.getCount() > 0) {
            int hhCount = hhCursor.getCount();
            List<Household> list = new ArrayList<>(hhCount);
            while (hhCursor.moveToNext()) {
                Household household = DatabaseUtils.extractObjectFromCursor(Household.class, hhCursor, false);
                list.add(household);
            }
            hhCursor.close();
            Sync unsyncedHouse = new Sync();
            unsyncedHouse.setList_hh(list);

            return unsyncedHouse;
        }

        return null;
    }

    private int setSyncedListingHouse(Sync sync){
        ContentValues cv = new ContentValues();
        if(sync.getList_hh().size()>0){
            cv.put("House", sync.getList_hh().get(0).house_uid);
            StringBuilder sb = new StringBuilder();
            for (Household hh : sync.getList_hh())
                sb.append("|").append(hh.hh_uid);
            sb.deleteCharAt(0);
            cv.put("Household", sb.toString());
            return getContentResolver().update(ListingProvider.URI_SET_SYNCED, cv, null, null);
        }
        else
            return 0;

    }
}
