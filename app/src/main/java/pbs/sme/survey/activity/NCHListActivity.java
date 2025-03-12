package pbs.sme.survey.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.DB.Database;
import pbs.sme.survey.R;
import pbs.sme.survey.helper.NCHAdapter;
import pbs.sme.survey.helper.SyncScheduler;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.NCH;
import pbs.sme.survey.model.House;

public class NCHListActivity extends MyActivity {

    RecyclerView list;
    NCHAdapter adapter;
    TextView tv_nch, tv_listed, tv_upload;

    BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(SyncScheduler.BROADCAST_LISTING_SYNCED)) {
                mUXToolkit.showToast("Household synced!");
                if (dbHandler == null)
                    dbHandler = Database.getInstance(NCHListActivity.this);
                init();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nchlist);
        setDrawer(this,"List of NCH");
        list=findViewById(R.id.list);
        tv_nch = findViewById(R.id.tv_nch);
        tv_listed = findViewById(R.id.tv_listed);
        tv_upload = findViewById(R.id.tv_upload);
    }

    public void addNewHouse(View view) {
        /*final Intent intent = new Intent(this, HouseActivity.class);
        startActivity(intent);
        finish();*/
    }


    public void updateStats(){
        int ncount=dbHandler.queryInteger("SELECT count(*) FROM "+NCH.class.getSimpleName()+" where (is_deleted='false' or is_deleted=0) ;");
        int listed=dbHandler.queryInteger("SELECT count(*) from "+ Household.class.getSimpleName()+" where ENV='"+env+"' AND is_deleted=0 and nchid in (select distinct id from NCH where (is_deleted='false' or is_deleted=0))");

        tv_nch.setText(String.valueOf(ncount));
        tv_listed.setText(String.valueOf(listed));
        updateUpload();

    }

    public void init(){
        updateStats();
        list.setLayoutManager(new LinearLayoutManager(this));
        List<NCH> hl = dbHandler.query(NCH.class, "is_deleted='false' or is_deleted=0");
        List<House> li = new ArrayList<>();
        House a;
        for (NCH h : hl) {
            int c=dbHandler.queryInteger("SELECT count(*) FROM "+Household.class.getSimpleName()+" where ENV='"+env+"' AND nchid="+h.getId());
            int hh=dbHandler.queryInteger("SELECT count(*) FROM "+House.class.getSimpleName()+" where ENV='"+env+"' AND nchid="+h.getId());
            if(hh==0){
                a=new House(null, 0, ((h.getArea_name()==null?h.getArea():(h.getArea()+" \n(address: "+h.getArea_name()+") "))));
            }
            else{
                String hno=dbHandler.queryString("SELECT max(hno) FROM "+House.class.getSimpleName()+" where ENV='"+env+"' AND  nchid="+h.getId());
                a=dbHandler.querySingle(House.class,"env=? and hno=? and nchid=?", env, hno, String.valueOf(h.getId()));
            }
            a.setNchid(h.getId());
            a.setHHs(c==1 ? 1 : c);
            li.add(a);
        }
        adapter = new NCHAdapter(this,  li, dbHandler, env);
        list.setAdapter(adapter);
    }
    public void updateUpload() {
        int upload=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where ENV='"+env+"' AND  is_deleted=0 and sync_time is not null and nchid in (select distinct id from NCH where (is_deleted='false' or is_deleted=0))");
        tv_upload.setText(String.valueOf(upload));
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(SyncScheduler.BROADCAST_LISTING_SYNCED);
        registerReceiver(syncReceiver, filter);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(syncReceiver);
    }

}