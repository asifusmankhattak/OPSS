package pbs.sme.survey.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.BuildConfig;
import pbs.sme.survey.DB.Database;
import pbs.sme.survey.DB.DatabaseBackup;
import pbs.sme.survey.R;
import pbs.sme.survey.api.ApiClient;
import pbs.sme.survey.api.ApiInterface;
import pbs.sme.survey.helper.BlockAdapter;
import pbs.sme.survey.helper.DateHelper;
import pbs.sme.survey.helper.NetworkHelper;
import pbs.sme.survey.model.Assignment;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Contact;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.NCH;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.model.Settings;
import pbs.sme.survey.model.House;
import pbs.sme.survey.model.Import;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImportActivity extends MyActivity {
    TextView user, txt_error,  network;
    TextView impDate, entDate, synDate;
    ProgressBar p_tblk, p_sblk, p_fblk, p_uhh, p_thh, p_th;
    TextView tblk, sblk, fblk, uhh, thh, th;
    ListView block_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        setDrawer(this,"IAC Listing Home");
        init();
    }

    public void showlast(){
        impDate.setText("Import: "+settings.getString(Constants.LAST_IMPORT,"None"));
        entDate.setText("Entry: "+settings.getString(Constants.LAST_ENTRY,"None"));
        synDate.setText("Sync: "+settings.getString(Constants.LAST_SYNC,"None"));

    }
    public void showNetwork(){
        try {
            TelephonyManager tManager = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            String mname = tManager.getNetworkOperatorName();
            NetworkHelper net=new NetworkHelper(getBaseContext());
            String wname="";
            if(net.isWifiEnabled() && net.isWifiConnected()){
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                wname = wifiInfo.getSSID();
            }

            if(mname!=null && !mname.isEmpty()){
                network.setText("Network: "+mname);
            }
            else if(wname!=null && !wname.isEmpty()){
                network.setText("Wifi: "+wname);
            }
            else{
                network.setText("Not Connected");
            }
        }
        catch (Exception e){
            network.setText("No Network");
        }


    }

    public void showStats(){
        long tb=dbHandler.getCount(Assignment.class,"ISDELETED=0");
        long sb=dbHandler.getCount(Assignment.class, "ISDELETED=0 AND start_list_time is not null");
        long cb=dbHandler.getCount(Assignment.class, "ISDELETED=0 AND end_list_time is not null");
        long s=dbHandler.getCount(Section12.class, "emp_count<=50 and emp_count>0 and is_deleted=0 and env='"+env+"' and blk_desc in (select distinct blk_desc from  "+Assignment.class.getSimpleName()+" a where a.isdeleted=0) ");
        long m=dbHandler.getCount(Section12.class, "emp_count>50 and is_deleted=0 and env='"+env+"' and blk_desc in (select distinct blk_desc from  "+Assignment.class.getSimpleName()+" a where a.isdeleted=0) ");
        long u=dbHandler.getCount(Section12.class, "sync_time is not null and is_deleted=0 and env='"+env+"' and blk_desc in (select distinct blk_desc from  "+Assignment.class.getSimpleName()+" a where a.isdeleted=0) ");

        tblk.setText(("00" + tb).substring(String.valueOf(tb).length()));
        sblk.setText(("00" + sb).substring(String.valueOf(sb).length()));
        fblk.setText(("00" + cb).substring(String.valueOf(cb).length()));
        th.setText(("000" + s).substring(String.valueOf(s).length()));
        thh.setText(("000" + m).substring(String.valueOf(m).length()));
        uhh.setText(("000" + u).substring(String.valueOf(u).length()));
    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
        updateAlert();
        uploadEverything();
    }

    public void refresh(){
        showNetwork();
        showblocks();
        showStats();
        showlast();
    }

    private void init() {
        user=findViewById(R.id.user);
        user.setText("User: "+settings.getString(Constants.ENUMERATOR,""));


        impDate=findViewById(R.id.impDate);
        network = findViewById(R.id.network);
        txt_error=findViewById(R.id.txt_error);
        entDate=findViewById(R.id.entDate);
        entDate.setGravity(View.TEXT_ALIGNMENT_CENTER);
        synDate=findViewById(R.id.synDate);

        user.setTypeface(null, Typeface.BOLD);
        impDate.setTypeface(null, Typeface.BOLD);
        user.setTypeface(null, Typeface.BOLD);
        impDate.setTypeface(null, Typeface.BOLD);
        block_list=findViewById(R.id.block_list);

        tblk=findViewById(R.id.tblk);
        sblk=findViewById(R.id.sblk);
        fblk=findViewById(R.id.fblk);
        uhh=findViewById(R.id.uhh);
        thh=findViewById(R.id.thh);
        th=findViewById(R.id.th);

        p_tblk=findViewById(R.id.p_tblk);
        p_sblk=findViewById(R.id.p_sblk);
        p_fblk=findViewById(R.id.p_fblk);
        p_uhh=findViewById(R.id.p_uhh);
        p_thh=findViewById(R.id.p_thh);
        p_th=findViewById(R.id.p_th);

    }

    public void showblocks(){
        List<Block> listData = new ArrayList<>();
        int ncount=dbHandler.queryInteger("SELECT count(*) FROM NCH where (is_deleted='false' or is_deleted=0);");
        if(ncount>0){
            String startd=dbHandler.queryString("SELECT min(start_date) FROM NCH where (is_deleted='false' or is_deleted=0)");
            String endd=dbHandler.queryString("SELECT max(end_date) FROM NCH  where (is_deleted='false' or is_deleted=0)");
            int listed=dbHandler.queryInteger("SELECT count(*) from NCH where (is_deleted='false' or is_deleted=0) and id in (select distinct flag from "+Section12.class.getSimpleName()+" where is_deleted=0 and env='"+env+"')");
            int synced=dbHandler.queryInteger("SELECT count(*) from NCH where (is_deleted='false' or is_deleted=0) and id in (select distinct flag from  "+Section12.class.getSimpleName()+" where sync_time is not null and is_deleted=0 and env='"+env+"')");
            Assignment a=new Assignment();
            a.setBlk_desc("OTHER_LIST");
            a.setCount(ncount);
            a.setStart_date(startd);
            a.setEnd_date(endd);
            a.setStatus(synced+" U / " +listed+" L / "+ncount+" T");
            a.blk_type="List of Establishment";
            listData.add(new Block(a));
        }
        List<Assignment> assignments = dbHandler.query(Assignment.class, "isdeleted=0");
        if (assignments.size() > 0) {
            for (Assignment a : assignments){
                listData.add(new Block(a));
            }
        }
        if(listData.size()>0){
            BlockAdapter adapter = new BlockAdapter(this, listData);
            block_list.setAdapter(adapter);
        }
        else{
            if(settings.getString(Constants.LAST_IMPORT,null)==null){
                restoreDisplay();
            }
            else{
                BlockAdapter adapter = new BlockAdapter(this, listData);
                block_list.setAdapter(adapter);
            }

        }

    }

    public void restoreDisplay(){
        DatabaseBackup.restoreDatabase(this,this,dbHandler.getDatabaseName(), mUXToolkit);
        try {
            SharedPreferences.Editor editor = settings.edit();
            env=dbHandler.queryString("SELECT ENV from "+ Settings.class.getSimpleName()+" where app='IACL'");
            if(env!=null && !env.isEmpty()){
                editor.putString(Constants.ENV, env);
            }
            editor.putString(Constants.LAST_IMPORT, DateHelper.toDate("yyyy-MM-dd'T'HH:mm", "dd MMM yy, HH:mm", getTimeNow()));
            editor.commit();
        }
        catch (Exception e){}

        List<Block> listData = new ArrayList<>();
        int ncount=dbHandler.queryInteger("SELECT count(*) FROM NCH where (is_deleted='false' or is_deleted=0);");
        if(ncount>0){
            String startd=dbHandler.queryString("SELECT min(start_date) FROM NCH where (is_deleted='false' or is_deleted=0)");
            String endd=dbHandler.queryString("SELECT max(end_date) FROM NCH  where (is_deleted='false' or is_deleted=0)");
            int listed=dbHandler.queryInteger("SELECT count(*) from NCH where (is_deleted='false' or is_deleted=0) and id in (select distinct flag from "+Section12.class.getSimpleName()+" where is_deleted=0 and env='"+env+"')");
            int synced=dbHandler.queryInteger("SELECT count(*) from NCH where (is_deleted='false' or is_deleted=0) and id in (select distinct flag from  "+Section12.class.getSimpleName()+" where sync_time is not null and is_deleted=0 and env='"+env+"')");
            Assignment a=new Assignment();
            a.setBlk_desc("OTHER_LIST");
            a.setCount(ncount);
            a.setStart_date(startd);
            a.setEnd_date(endd);
            a.setStatus(synced+" U / " +listed+" L / "+ncount+" T");
            a.blk_type="List of Establishment";
            listData.add(new Block(a));
        }
        List<Assignment> assignments = dbHandler.query(Assignment.class, "isdeleted=0");
        if (assignments.size() > 0) {
            for (Assignment a : assignments){
                listData.add(new Block(a));
            }
        }
        BlockAdapter adapter = new BlockAdapter(this, listData);
        block_list.setAdapter(adapter);
        showStats();
    }


    public void setMsg(String m, int c){
        txt_error.setTextColor(c);
        txt_error.setText(m);
        txt_error.setVisibility(View.VISIBLE);
    }



    public void ImportData(final View view) {
        if(env==null){
            env="";
        }
        view.setEnabled(false);
        resetProgress();
        int app=Constants.APP_ID;
        String vcode= BuildConfig.VERSION_NAME;
        long sid=settings.getLong(Constants.SID,0);
        Integer uid=settings.getInt(Constants.UID,0);
        Integer mid=dbHandler.queryInteger("SELECT max(id) FROM ALERTS;");
        String utime=dbHandler.queryString("SELECT max(updatedTime) FROM SETTINGS");
        Long ht=dbHandler.getCount(Household.class,"is_deleted=? and env=?","0",env);
        if(utime=="" || utime==null){
            utime="2000-01-01";
        }
        String ctime=dbHandler.queryString("SELECT max(updated) as updated FROM CONTACT");
        if(ctime=="" || ctime==null){
            ctime="2010-01-01";
        }
        ctime="2011-01-01";
        Call<Import> call= ApiClient.getApiClient().create(ApiInterface.class).fetch(app,vcode,uid,sid,mid,utime,ctime,ht);
        call.enqueue(new Callback<Import>() {
            @Override
            public void onResponse(Call<Import> call, Response<Import> response) {
                Import u=response.body();
                if(u==null){
                    setMsg("Server Side Error: "+response.message(), Color.RED);
                }
                else{
                    SharedPreferences.Editor editor = settings.edit();
                    deleteExistingAssignments();
                    if(u.getAssignments()!=null){
                        if(u.getAssignments().size()>0){
                            try{
                                for(Assignment a: u.getAssignments()){
                                    if(dbHandler.updateExcluding(a,new String[]{"start_list_time","end_list_time"})<=0){
                                        dbHandler.insert(a);
                                    }
                                }
                                setMsg("Assignments Imported Successfully", getColor(R.color.success));
                            } catch (SQLException e) {
                                setMsg("Local IMPORT(1): "+e.getMessage(), getColor(R.color.red));
                            } catch (IllegalAccessException e) {
                                setMsg("Local IMPORT(2): "+e.getMessage(), getColor(R.color.red));
                            }
                        }
                        else{
                            setMsg("NO Block Assignment Data, Try Again or Contact Relevant Person.", getColor(R.color.red));
                        }
                    }
                    else{
                        setMsg("NO Block Assignment Data, Try Again or Contact Relevant Person.", getColor(R.color.red));
                    }

                    if(u.getProfile()!=null){
                        try{
                            dbHandler.replaceOrThrow(u.getProfile());
                            editor.putString(Constants.ENUMERATOR,u.getProfile().getNAME());
                            editor.putString(Constants.ROLE,u.getProfile().getROLE());
                        } catch (SQLException e) {
                            Toast.makeText(getApplicationContext(),"Profile Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }

                    if(u.getSettings()!=null){
                        if(u.getSettings().size()>0) {
                            try {
                                dbHandler.replaceOrThrow(u.getSettings().toArray());
                                env=dbHandler.queryString("SELECT ENV from "+ Settings.class.getSimpleName()+" where app='IACL'");
                                if(env!=null && !env.isEmpty()){
                                    editor.putString(Constants.ENV, env);
                                    dbHandler=Database.getInstance(getApplicationContext());
                                    deleteNCH();
                                }
                                editor.putString(Constants.LAST_IMPORT, DateHelper.toDate("yyyy-MM-dd'T'HH:mm", "dd MMM yy, HH:mm", getTimeNow()));
                            } catch (SQLException e) {
                                Toast.makeText(getApplicationContext(), "Settings Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    if(u.getAlerts()!=null){
                        if(u.getAlerts().size()>0){
                            try{
                                dbHandler.replaceOrThrow(u.getAlerts().toArray());
                            } catch (SQLException e) {
                                Toast.makeText(getApplicationContext(),"Alerts Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    if(u.getContacts()!=null){
                        if(u.getContacts().size()>0){
                            try{
                                dbHandler.replaceOrThrow(u.getContacts().toArray());
                            } catch (SQLException e) {
                                Toast.makeText(getApplicationContext(),"Contact Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    if(u.getNchList()!=null){
                        if(u.getNchList().size()>0){
                            try{
                                dbHandler.replaceOrThrow(u.getNchList().toArray());
                            } catch (SQLException e) {
                                Toast.makeText(getApplicationContext(),"NCH List Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    if(u.getNblocks()!=null){
                        if(u.getNblocks().size()>0){
                            try{
                                dbHandler.replaceOrThrow(u.getNblocks().toArray());
                            } catch (SQLException e) {
                                Toast.makeText(getApplicationContext(),"NCH Block List Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    if(u.getHouseholds()!=null){
                        if(u.getHouseholds().size()>0){
                            try{
                                dbHandler.replaceOrThrow(u.getHouseholds().toArray());
                                dbHandler.execSql("delete from "+Household.class.getSimpleName()+" where hh_uid is null;");
                                for(Household h: u.getHouseholds()){
                                    House house=new House();
                                    house.house_uid = h.house_uid;
                                    house.blk_desc = h.blk_desc;
                                    house.hno = h.hno;
                                    house.sid=h.sid;
                                    house.userid=h.userid;
                                    house.area_name = h.area_name;
                                    house.lat=h.lat;
                                    house.lon=h.lon;
                                    house.alt=h.alt;
                                    house.vacc=h.vacc;
                                    house.hacc=h.hacc;
                                    house.provider=h.provider;
                                    house.mlat=h.mlat;
                                    house.mlon=h.mlon;
                                    house.zoom=h.zoom;
                                    house.map_type=h.map_type;
                                    house.is_outside=h.is_outside;
                                    house.meter_outside=h.meter_outside;
                                    house.reason_outside=h.reason_outside;
                                    house.access_time=h.access_time;
                                    house.nchid= h.nchid;
                                    house.created_time=h.created_time;
                                    house.modified_time=h.modified_time;
                                    house.is_deleted=0;
                                    house.deleted_time=null;
                                    house.sync_time=h.sync_time;
                                    house.env=h.env;
                                    dbHandler.replace(house);
                                }
                            } catch (SQLException e) {
                                Toast.makeText(getApplicationContext(),"Disaster Recovery Failed: "+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    editor.commit();
                }
                view.setEnabled(true);
                setProgress();
                refresh();
                updateAlert();
            }
            @Override
            public void onFailure(Call<Import> call, Throwable t) {
                view.setEnabled(true);
                setProgress();
                refresh();
                txt_error.setVisibility(View.VISIBLE);
                txt_error.setTextColor(Color.RED);
                new NetworkHelper(getApplicationContext(),txt_error, t);
            }
        });
    }

    public void updateAlert(){
        List<Settings> sg=dbHandler.query(Settings.class,"app=?","IACL");
        if(sg!=null && sg.size()>0){
            float next=sg.get(0).getVersion();
            float current = Float.parseFloat(BuildConfig.VERSION_NAME);
            if(next>current && sg.get(0).getUpdateOption()==2){
                AlertDialog.Builder d= new AlertDialog.Builder(this);
                d.setIcon(R.drawable.ic_update);
                d.setTitle("Update Application");
                d.setMessage("Current Listing Version: "+current+"\nNew Available Version: "+next+"\nPlease Update Your Application");
                d.setCancelable(false);
                d.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity(new Intent(ImportActivity.this, UpdateActivity.class));
                    }
                });
                d.setNeutralButton("LATER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                d.show();
            }
            else if(next>current && sg.get(0).getUpdateOption()==3){
                AlertDialog.Builder d= new AlertDialog.Builder(this);
                d.setIcon(R.drawable.ic_update);
                d.setTitle("Update Application");
                d.setMessage("Current Enumeration Version: "+current+"\nNew Available Version: "+next+"\nPlease Update Your Application");
                d.setCancelable(false);
                d.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity(new Intent(ImportActivity.this, UpdateActivity.class));
                    }
                });
                d.show();
            }
        }

    }

    private void resetProgress(){
        p_fblk.setVisibility(View.VISIBLE);
        p_sblk.setVisibility(View.VISIBLE);
        p_tblk.setVisibility(View.VISIBLE);
        p_thh.setVisibility(View.VISIBLE);
        p_uhh.setVisibility(View.VISIBLE);
        p_th.setVisibility(View.VISIBLE);

        fblk.setVisibility(View.GONE);
        sblk.setVisibility(View.GONE);
        tblk.setVisibility(View.GONE);
        thh.setVisibility(View.GONE);
        uhh.setVisibility(View.GONE);
        th.setVisibility(View.GONE);
    }

    private void setProgress(){
        p_fblk.setVisibility(View.GONE);
        p_sblk.setVisibility(View.GONE);
        p_tblk.setVisibility(View.GONE);
        p_thh.setVisibility(View.GONE);
        p_uhh.setVisibility(View.GONE);
        p_th.setVisibility(View.GONE);

        fblk.setVisibility(View.VISIBLE);
        sblk.setVisibility(View.VISIBLE);
        tblk.setVisibility(View.VISIBLE);
        thh.setVisibility(View.VISIBLE);
        uhh.setVisibility(View.VISIBLE);
        th.setVisibility(View.VISIBLE);
    }

    private void deleteExistingAssignments(){
        dbHandler.execSql("DELETE FROM "+ Contact.class.getSimpleName());
        dbHandler.execSql("UPDATE "+Assignment.class.getSimpleName()+" SET isdeleted=1");
        deleteNCH();
    }
    private void deleteNCH(){
        if(env.equalsIgnoreCase("field")){
            dbHandler.execSql("UPDATE "+ NCH.class.getSimpleName()+" SET is_deleted=1 where id not in (select nchid from "+Household.class.getSimpleName()+" where env='"+env+"' and nchid is not null)");
            dbHandler.execSql("UPDATE "+ NCH.class.getSimpleName()+" SET is_deleted=1 where id>50000");
        }
    }


}
