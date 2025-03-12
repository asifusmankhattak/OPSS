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
import pbs.sme.survey.helper.OtherListAdapter;
import pbs.sme.survey.helper.SyncScheduler;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.NCH;
import pbs.sme.survey.model.House;
import pbs.sme.survey.model.Section12;

public class OtherListActivity extends MyActivity {

    RecyclerView list;
    OtherListAdapter adapter;
    TextView tvH, tvHu, tvBlock;
    String list_type="Establishments";
    Block mBlock;

    BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(SyncScheduler.BROADCAST_LISTING_SYNCED)) {
                mUXToolkit.showToast("Household synced!");
                if (dbHandler == null)
                    dbHandler = Database.getInstance(OtherListActivity.this);
                init();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_list);
        setDrawer(this,"List of Establishments");
        mBlock = (Block) getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
        list=findViewById(R.id.list);
    }
    public void updateStats(){
        tvH = findViewById(R.id.tv_total);
        tvBlock = findViewById(R.id.tv_block_code);

        tvHu = findViewById(R.id.tv_upload);

        tvBlock.setText(mBlock.getBlockCode());
        Long hCount = dbHandler.getCount(Section12.class, "env=? and is_deleted=?",env,"0");
        tvH.setText(String.valueOf(hCount));

        Long hCount_S = dbHandler.getCount(Section12.class, "env=? and blk_desc=? and sync_time is not null",env,"0");
        tvHu.setText(String.valueOf(hCount_S));
    }
    public void addNewHouse(View view) {
        /*final Intent intent = new Intent(this, HouseActivity.class);
        startActivity(intent);
        finish();*/
    }



    public void init(){
        updateStats();
        list.setLayoutManager(new LinearLayoutManager(this));
        List<NCH> hl = dbHandler.query(NCH.class, "is_deleted='false' or is_deleted=0");
        List<Section12> li = new ArrayList<>();
        Section12 a;
        for (NCH h : hl) {
            int hh=dbHandler.queryInteger("SELECT count(*) FROM "+Section12.class.getSimpleName()+" where ENV='"+env+"' AND flag="+h.getId());
            if(hh==0){
                a=new Section12(null, 0, h.getHead(),h.getArea_acre());
            }
            else{
                a=dbHandler.querySingle(Section12.class,"env=? and flag=?", env, String.valueOf(h.getId()));
            }
            a.flag=h.getId();
            li.add(a);
        }
        adapter = new OtherListAdapter(this,  li, dbHandler, env);
        list.setAdapter(adapter);
    }
    public void updateUpload() {
        int upload=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where ENV='"+env+"' AND  is_deleted=0 and sync_time is not null and nchid in (select distinct id from NCH where (is_deleted='false' or is_deleted=0))");
        tvHu.setText(String.valueOf(upload));
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