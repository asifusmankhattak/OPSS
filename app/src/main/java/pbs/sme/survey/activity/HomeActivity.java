package pbs.sme.survey.activity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.R;
import pbs.sme.survey.helper.DialogHelper;
import pbs.sme.survey.helper.GPSHelper;
import pbs.sme.survey.helper.SectionAdapter;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Section;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.model.Section3;

public class HomeActivity extends FormActivity {

    GPSHelper helper;
    LocationManager manager;
    Location gps, net, best;
    RecyclerView list;
    SectionAdapter adapter;
    TextView tv_progress, tv_synctime;
    int progress, total;
    String[] times;
    Integer[] status;
    DialogHelper gdh=new DialogHelper();
    String missing="";
    String stime=null;

    public static final String UPDATE_SYNC_STATUS = ListActivity.class.getCanonicalName() + "UPDATE_SYNC_STATUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setDrawer(this,"Home Screen");
        setParent(this, S1Activity.class);
        list=findViewById(R.id.list);
        tv_progress=findViewById(R.id.tv_progress);
        tv_synctime=findViewById(R.id.tv_synctime);
        btnn=findViewById(R.id.btnn);
        setSection();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    public void setSection(){
        List<Section> l=new ArrayList<>();
        String[] times=new String[Constants.SECTION_CODES.length];
        Integer[] status=new Integer[Constants.SECTION_CODES.length];
        total = times.length;

        List<Section12> s2= dbHandler.query(Section12.class,"uid='"+resumeModel.uid+"' AND (is_deleted=0 OR is_deleted is null)");
        if(s2!=null){
            Section12 o=s2.get(0);
            if(o.owner!=null){
                times[0]=o.created_time;
                status[0]=R.drawable.ic_tick;
            }
            if(o.started_year!=null){
                times[1]=o.modified_time;
                status[1]=R.drawable.ic_tick;
            }
        }
        String s3time=dbHandler.queryString("SELECT min(created_time) from "+ Section3.class.getSimpleName()+" where uid='"+resumeModel.uid+"' and  (is_deleted=0 or is_deleted is null)",null);
        if(s3time!=null && !s3time.isEmpty()){
            times[2]=s3time;
            status[2]=R.drawable.ic_tick;
        }
        progress=0;
        missing="";
        for(int i=0; i<Constants.SECTION_CODES.length; i++){
            if(status[i]!=null){
                if(status[i]==R.drawable.ic_block || status[i]==R.drawable.ic_tick) {
                    progress++;
                }
                else{
                    missing+="<br/>"+Constants.SECTION_NAMES[i];
                }
            }
            else{
                missing+="<br/>"+Constants.SECTION_NAMES[i];
            }
            l.add(new Section(Constants.SECTION_CODES[i],Constants.SECTION_NAMES[i],status[i],times[i],Constants.FORM_ACTIVITIES[i]));
        }
        tv_progress.setText(progress+"/"+total);
        if(progress==0){
            tv_progress.setText("No Started");
        }
        else if(progress==total){
            tv_progress.setText(+progress+"/ "+total+"     Completed  ");
        }
        else{
            tv_progress.setText(+progress+"/ "+total+"    Remaining  ");
        }

        adapter = new SectionAdapter(this,  l, dbHandler, intent);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        //setSyncTime(enumerationHousehold.hh_uid);

    }

    public void goNext(final View view){
        Intent intent=new Intent(HomeActivity.this, S1Activity.class);
        startActivity(intent);
        finish();

    }



    public void upload(View view) {
    }
}
//package pbs.sme.survey.activity;
//
//import android.content.Intent;
//import android.content.res.ColorStateList;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.core.widget.ImageViewCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
//
//import pbs.sme.survey.R;
//import pbs.sme.survey.api.ApiClient;
//import pbs.sme.survey.api.ApiInterface;
//import pbs.sme.survey.helper.DialogHelper;
//import pbs.sme.survey.helper.GPSHelper;
//import pbs.sme.survey.helper.SectionAdapter;
//import pbs.sme.survey.helper.StatsUtil;
//import pbs.sme.survey.model.Assignment;
//import pbs.sme.survey.model.Baseline;
//import pbs.sme.survey.model.Constants;
//import pbs.sme.survey.model.House;
//import pbs.sme.survey.model.Household;
//import pbs.sme.survey.model.Section;
//import pbs.sme.survey.model.Section12;
//import pbs.sme.survey.model.Section34;
//import pbs.sme.survey.online.Returning;
//import pbs.sme.survey.online.Sync;
//import pbs.sme.survey.online.Sync2;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class HomeActivity extends FormActivity {
//
//    RecyclerView list;
//    SectionAdapter adapter;
//    TextView tv_progress, tv_synctime;
//    int progress, total;
//    String[] times;
//    Integer[] status;
//    DialogHelper gdh=new DialogHelper();
//    String missing="";
//    String stime=null;
//
//    public static final String UPDATE_SYNC_STATUS = ListActivity.class.getCanonicalName() + "UPDATE_SYNC_STATUS";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//        setDrawer(this,"Home Screen");
//        setParent(this, S1Activity.class);
//        list=findViewById(R.id.list);
//        tv_progress=findViewById(R.id.tv_progress);
//        tv_synctime=findViewById(R.id.tv_synctime);
//        setSection();
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        setSection();
//    }
//
//
//
//    public void setSection(){
//        List<Section> l=new ArrayList<>();
//        String[] times=new String[Constants.SECTION_CODES.length];
//        Integer[] status=new Integer[Constants.SECTION_CODES.length];
//        total = times.length;
//
//        List<Section12> s2= dbHandler.query(Section12.class,"uid='"+resumeModel.uid+"' AND (is_deleted=0 OR is_deleted is null)");
//        long s3=dbHandler.getCount(Section34.class, "uid='"+resumeModel.uid+"' AND section=3 and (is_deleted=0 OR is_deleted is null)");
//        long s4=dbHandler.getCount(Section34.class, "uid='"+resumeModel.uid+"' AND section=4 and (is_deleted=0 OR is_deleted is null)");
//        String s3time=dbHandler.queryString("SELECT min(created_time) from "+Section34.class.getSimpleName()+" where section=3 and uid='"+resumeModel.uid+"' and (is_deleted=0 OR is_deleted is null)");
//        String s4time=dbHandler.queryString("SELECT min(created_time) from "+Section34.class.getSimpleName()+" where section=4 and uid='"+resumeModel.uid+"' and (is_deleted=0 OR is_deleted is null)");
//
//        if(s2!=null){
//            Section12 o=s2.get(0);
//            if(o.sync_time!=null && o.sync_time.equalsIgnoreCase("")){
//                stime=o.sync_time;
//            }
//            if(o.owner!=null){
//                times[0]=o.created_time;
//                status[0]=R.drawable.ic_tick;
//            }
//            if(o.started_year!=null){
//                times[1]=o.modified_time;
//                status[1]=R.drawable.ic_tick;
//            }
//            if(s3time!=null && !s3time.equalsIgnoreCase("")){
//                times[2]=o.created_time;
//                status[2]=R.drawable.ic_tick;
//            }
//            if(s4time!=null && !s4time.equalsIgnoreCase("")){
//                times[3]=o.modified_time;
//                status[3]=R.drawable.ic_tick;
//            }
//        }
//        times[4]="";
//        status[4]=R.drawable.ic_block;
//
//        progress=0;
//        missing="";
//        for(int i=0; i<Constants.SECTION_CODES.length; i++){
//            if(status[i]!=null){
//                if(status[i]==R.drawable.ic_block || status[i]==R.drawable.ic_tick) {
//                    progress++;
//                }
//                else{
//                    missing+="<br/>"+Constants.SECTION_NAMES[i];
//                }
//            }
//            else{
//                missing+="<br/>"+Constants.SECTION_NAMES[i];
//            }
//            l.add(new Section(Constants.SECTION_CODES[i],Constants.SECTION_NAMES[i],status[i],times[i],Constants.FORM_ACTIVITIES[i]));
//        }
//        tv_progress.setText(progress+"/"+total);
//        if(progress==0){
//            tv_progress.setText("No Started");
//        }
//        else if(progress==total){
//            tv_progress.setText(+progress+"/ "+total+"     Completed  ");
//        }
//        else{
//            tv_progress.setText(+progress+"/ "+total+"    Remaining  ");
//        }
//
//        adapter = new SectionAdapter(this,  l, dbHandler, intent);
//        list.setLayoutManager(new LinearLayoutManager(this));
//        list.setAdapter(adapter);
//        //setSyncTime(enumerationHousehold.hh_uid);
//
//    }
//
//    public void goNext(){
//        btnn.callOnClick();
//    }
//
//
//
//    public void upload(View view){
//        if(stime!=null && progress>=5){
//            Toast.makeText(getApplicationContext(),"Uploading Please Wait",Toast.LENGTH_SHORT).show();
//            List<Section12> s12=dbHandler.queryRawSql(Section12.class,"SELECT * FROM "+ Section12.class.getSimpleName()+" WHERE ENV='"+env+"' AND  UID='"+resumeModel.uid+"' and sync_time is null;");
//            List<Section34> s34=dbHandler.queryRawSql(Section34.class,"SELECT * FROM "+ Section34.class.getSimpleName()+" WHERE ENV='"+env+"' AND  UID='"+resumeModel.uid+"' and sync_time is null;");
//            List<Baseline> base=dbHandler.queryRawSql(Baseline.class,"SELECT * FROM "+ Baseline.class.getSimpleName()+" WHERE ENV='"+env+"' AND  UID='"+resumeModel.uid+"' and sync_time is null;");
//            if(s12.size()>0 && s34.size()>0){
//                Sync2 s=new Sync2();
//                s.setS12(s12);
//                s.setS34(s34);
//                s.setBase(base);
//                view.setEnabled(false);
//                Call<Returning> call= ApiClient.getApiClient().create(ApiInterface.class).upload2(s);
//                call.enqueue(new Callback<Returning>() {
//                    @Override
//                    public void onResponse(Call<Returning> call, Response<Returning> response) {
//                        view.setEnabled(true);
//                        Returning u=response.body();
//                        if(u!=null){
//                            Toast.makeText(getApplicationContext(),u.getMsg(),Toast.LENGTH_SHORT).show();
//                            if(u.getCode()==1){
//                                resumeModel.sync_time=getTimeNow();
//                                dbHandler.execSql("UPDATE "+ Section12.class.getSimpleName()+" SET sync_time='"+ getTimeNowwithSeconds()+"' where ENV='"+env+"' AND  uid='"+resumeModel.uid+"';");
//                                dbHandler.execSql("UPDATE "+ Section34.class.getSimpleName()+" SET sync_time='"+ getTimeNowwithSeconds()+"' where ENV='"+env+"' AND  house_uid='"+resumeModel.uid+"';");
//                                dbHandler.execSql("UPDATE "+ Baseline.class.getSimpleName()+" SET sync_time='"+ getTimeNowwithSeconds()+"' where ENV='"+env+"' AND  house_uid='"+resumeModel.uid+"';");
//                                setSection();
//                                StatsUtil.updateSyncTimeToNow();
//                            }
//                        }
//                        else {
//                            Toast.makeText(getApplicationContext(),"Error Response Format",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    @Override
//                    public void onFailure(Call<Returning> call, Throwable t) {
//                        view.setEnabled(true);
//                        if((t instanceof UnknownHostException)){
//                            Toast.makeText(getApplicationContext(),"Network Issue, Unable to connect to server",Toast.LENGTH_SHORT).show();
//                        }
//                        else{
//                            Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//            else{
//                Toast.makeText(getApplicationContext(),"Section12 or Section23 is Not Complete",Toast.LENGTH_SHORT).show();
//            }
//
//        }
//        else if(stime!=null) {
//            Toast.makeText(getApplicationContext(),"Already Synced",Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Toast.makeText(getApplicationContext(),"Complete Before Uploading",Toast.LENGTH_SHORT).show();
//        }
//    }
//}