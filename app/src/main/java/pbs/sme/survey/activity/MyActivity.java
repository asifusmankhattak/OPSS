package pbs.sme.survey.activity;

import static pbs.sme.survey.helper.SyncService.BROADCAST_HOUSEHOLD_CHANGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import pbs.sme.survey.BuildConfig;
import pbs.sme.survey.DB.Database;
import pbs.sme.survey.api.ApiClient;
import pbs.sme.survey.api.ApiInterface;
import pbs.sme.survey.helper.DateHelper;
import pbs.sme.survey.model.Constants;

import com.google.android.material.navigation.NavigationView;
import pbs.sme.survey.R;
import pbs.sme.survey.model.House;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.User;
import pbs.sme.survey.online.Returning;
import pbs.sme.survey.online.Sync;
import pk.gov.pbs.utils.CustomActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyActivity extends CustomActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected SharedPreferences settings;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected int tcolor;
    TextView version;

    protected Database dbHandler;
    protected User user;
    String env;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings=getSharedPreferences(Constants.PREF,MODE_PRIVATE);
        String us = settings.getString("user", "");
        if (!us.isEmpty()) {
            user = new User(us);
            if (user.getID() > 0)
                dbHandler = Database.getInstance(this);
        }


        tcolor=settings.getInt(Constants.THEME,3);
        if(isTablet(this)){
            if(tcolor==1){
                setTheme(R.style.ThemeBlackTablet);
            }
            else if(tcolor==2){
                setTheme(R.style.ThemeBlueTablet);
            }
            else{
                setTheme(R.style.ThemeRedTablet);
            }
        }
        else{
            if(tcolor==1){
                setTheme(R.style.ThemeBlackMobile);
            }
            else if(tcolor==2){
                setTheme(R.style.ThemeBlueMobile);
            }
            else{
                setTheme(R.style.ThemeRedMobile);
            }
        }
        env=settings.getString(Constants.ENV,"");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHandler != null) {
            dbHandler = null;
        }
    }

    public User getCurrentUser(){
        return user;
    }
    public Database getDatabaseHandler(){
        return dbHandler;
    }
    protected void setDrawer(Activity activity, String title){
        getSupportActionBar().setTitle(title);

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try{
            version=navigationView.getHeaderView(0).findViewById(R.id.t_version);
            String env=settings.getString(Constants.ENV,"");
            version.setText(env.toUpperCase()+" "+ BuildConfig.VERSION_NAME);
        }
        catch (Exception e){}

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Context c=getApplicationContext();
        Intent intent=null;
        int id=item.getItemId();
        if(id==R.id.pbsHome){
            /*intent=new Intent(c, ImportActivity.class);
            startActivity(intent);
            finishAffinity();*/
        }
        /*
        else if(id==R.id.nav_guide){
            intent=new Intent(c, HelpActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_backup){
            intent=new Intent(c, BackupActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_settings){
            intent=new Intent(c, SettingActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_update){
            intent=new Intent(c, UpdateActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_profile){
            intent=new Intent(c, ProfileActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_alert){
            intent=new Intent(c, AlertsActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_sync){
            mUXToolkit.showToast("Please Wait, If you have internet, we will upload");
            uploadEverything();
            intent = new Intent();
            intent.setAction(BROADCAST_HOUSEHOLD_CHANGE);
            sendBroadcast(intent);

        }
        else if(id==R.id.nav_logout){
            int total=dbHandler.queryInteger("SELECT count(*) from "+ Household.class.getSimpleName()+" where is_deleted=? and env=?","0",env);
            int upload=dbHandler.queryInteger("SELECT count(*) from "+ Household.class.getSimpleName()+" where is_deleted=0 and env='"+env+"' and sync_time is not null");

            int ht=dbHandler.queryInteger("SELECT count(*) from "+ House.class.getSimpleName()+" where is_deleted=? and env=?","0",env);
            int hu=dbHandler.queryInteger("SELECT count(*) from "+ House.class.getSimpleName()+" where is_deleted=0 and env='"+env+"' and sync_time is not null");

            if(settings.getString(Constants.ENV,"").equalsIgnoreCase("field") && upload<total && hu<ht){
                mUXToolkit.showToast("Cannot logout, "+(total-upload)+" Household(s) are not uploaded");
            }
            else{
                try{
                    DatabaseBackup.backupDatabase(this,this,dbHandler.getDatabaseName(), mUXToolkit);
                }
                catch (Exception e){
                    mUXToolkit.showToast("Backup Error...");
                }
                SharedPreferences.Editor e=settings.edit();
                e.putBoolean(Constants.IS_LOGGED,false);
                e.putInt(Constants.UID,0);
                e.putInt(Constants.SID,0);
                e.putString(Constants.USERNAME,null);
                e.putString(Constants.ENUMERATOR,null);
                e.putString(Constants.LAST_LOGIN,null);
                //e.putString(Constants.LAST_IMPORT,null);
                e.putString("user", null);

                e.commit();
                finishAndRemoveTask();
                System.exit(0);
            }

        }

        else if(id==R.id.nav_help){
            intent=new Intent(c, ContactActivity.class);
            startActivity(intent);
        }

         */
        else if(id==R.id.nav_exit){
            finishAndRemoveTask();
            System.exit(0);
        }
        return true;
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public static String getTimeNow(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date time = Calendar.getInstance().getTime();
        return format.format(time);
    }

    public static String getTimeNowwithSeconds(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        Date time = Calendar.getInstance().getTime();
        return format.format(time);
    }

    public void updateLastEntryTimeToNow(){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.LAST_ENTRY, DateHelper.toDate("yyyy-MM-dd'T'HH:mm","dd MMM yy, HH:mm",getTimeNow()));
        editor.commit();
    }

    public static String getDateForFolder(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
        Date time = Calendar.getInstance().getTime();
        return format.format(time);
    }

    public void updateSyncTimeToNow(){
        SharedPreferences.Editor editor=settings.edit();
        editor.putString(Constants.LAST_SYNC, DateHelper.toDate("yyyy-MM-dd'T'HH:mm","dd MMM yy, HH:mm",getTimeNow()));
        editor.commit();
    }

    public void uploadEverything(){
        Sync s=new Sync();
        List<Household> hh=new ArrayList<>();
        List<House> unsync=dbHandler.queryRawSql(House.class,"SELECT * FROM "+ House.class.getSimpleName()+" WHERE  env='"+env+"' and  (sync_time is null) and house_uid not in (select distinct house_uid from "+Household.class.getSimpleName()+");");
        if(unsync.size()>0){
            for (House h:unsync) {
                Household a=new Household();
                a.created_time=h.created_time;
                a.modified_time=h.modified_time;
                a.setHouse(h);
                hh.add(a);
                s.setList_hh(hh);
            }
            Call<Returning> call= ApiClient.getApiClient().create(ApiInterface.class).upload(s);
            call.enqueue(new Callback<Returning>() {
                @Override
                public void onResponse(Call<Returning> call, Response<Returning> response) {
                    Returning u=response.body();
                    if(u!=null){
                        if(u.getCode()==1){
                            for (Household h:hh) {
                                dbHandler.execSql("UPDATE "+ House.class.getSimpleName()+" SET sync_time='"+ getTimeNow()+"' where  env='"+env+"' and  house_uid='"+h.house_uid+"';");
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<Returning> call, Throwable t) {
                }
            });
        }
    }
}
