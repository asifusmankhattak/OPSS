package pbs.sme.survey.helper;


import static pbs.sme.survey.activity.MyActivity.getTimeNow;

import android.content.SharedPreferences;

import pbs.sme.survey.activity.CustomApplication;
import pbs.sme.survey.model.Constants;


public class StatsUtil {

    public static void updateLastEntryTimeToNow(){
        SharedPreferences.Editor editor = getPreferenceManager().edit();
        editor.putString(Constants.LAST_ENTRY, DateHelper.toDate("yyyy-MM-dd'T'HH:mm","dd MMM yy, HH:mm",getTimeNow()));
        editor.commit();
    }

    public static void updateSyncTimeToNow(){
        SharedPreferences.Editor editor = getPreferenceManager().edit();
        editor.putString(Constants.LAST_SYNC, DateHelper.toDate("yyyy-MM-dd'T'HH:mm","dd MMM yy, HH:mm",getTimeNow()));
        editor.commit();
    }

    public static SharedPreferences getPreferenceManager(){
        return CustomApplication.getSharedPreferences();
    }


}
