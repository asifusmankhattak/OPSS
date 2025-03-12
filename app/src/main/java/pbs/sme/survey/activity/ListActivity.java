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

import java.util.List;

import pbs.sme.survey.DB.Database;
import pbs.sme.survey.R;
import pbs.sme.survey.helper.DateHelper;
import pbs.sme.survey.helper.RecyclerListAdapter;
import pbs.sme.survey.helper.SyncScheduler;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Section12;

public class ListActivity extends MyActivity {

    RecyclerView list;
    RecyclerListAdapter adapter;
    TextView tvH, tvHu, tvBlock;
    Block mBlock;

    BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(SyncScheduler.BROADCAST_LISTING_SYNCED)) {
                mUXToolkit.showToast("Household synced!");
                if (dbHandler == null)
                    dbHandler = Database.getInstance(ListActivity.this);
                init();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        setDrawer(this,"List of Buildings in Block");
        mBlock = (Block) getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
        list=findViewById(R.id.list);
        env=settings.getString(Constants.ENV,"");
    }

    public void addNewHouse(View view) {
        String cmsg=DateHelper.DateCheck(mBlock.getStartDate(),mBlock.getEndDate(),mBlock.getBeforeDate(), mBlock.getAfterDate());
        if(cmsg!=null){
            mUXToolkit.showToast(cmsg);
            return;
        }

        final Intent intent = new Intent(this, GeoActivity.class);
        intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
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

    public void updateStats(){
        tvH = findViewById(R.id.tv_total);
        tvBlock = findViewById(R.id.tv_block_code);



        tvHu = findViewById(R.id.tv_upload);

        tvBlock.setText(mBlock.getBlockCode());
        Long hCount = dbHandler.getCount(Section12.class, "env=? and blk_desc=? and is_deleted=?",env, mBlock.getBlockCode(),"0");
        tvH.setText(String.valueOf(hCount));

        Long hCount_S = dbHandler.getCount(Section12.class, "env=? and blk_desc=? and is_deleted=? and sync_time is not null",env, mBlock.getBlockCode(),"0");
        tvHu.setText(String.valueOf(hCount_S));
    }

    public void init(){
        updateStats();
        list.setLayoutManager(new LinearLayoutManager(this));
        List<Section12> hl = dbHandler.queryRawSql(Section12.class, "select * from "+Section12.class.getSimpleName()+" where env='"+env+"' and blk_desc='"+mBlock.getBlockCode()+"' and is_deleted=0 order by sno");
        adapter = new RecyclerListAdapter(this,  hl, dbHandler,env);
        list.setAdapter(adapter);
    }

}