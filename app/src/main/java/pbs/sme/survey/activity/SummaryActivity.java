package pbs.sme.survey.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import pbs.sme.survey.DB.DatabaseBackup;
import pbs.sme.survey.R;
import pbs.sme.survey.model.Assignment;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.Section12;

public class SummaryActivity extends MyActivity {

    Block mBlock;
    Button btn_start;
    Long hhCount;
    TextView tvTotalUploaded, tvTotalHouseholds, tvBlockCode;
    String stime, etime;
    TextView l_land, l_anim, l_machine, l_other, l_total;
    TextView n_land, n_anim, n_machine, n_other, n_total;
    TextView m_land, m_anim, m_machine, m_other, m_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        setDrawer(this,"Block Summary");
        mBlock = (Block) getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
        btn_start = findViewById(R.id.btn_start);
        tvTotalUploaded = findViewById(R.id.tv_total_uploaded_household);
        tvTotalHouseholds = findViewById(R.id.tv_total_household);
        tvBlockCode = findViewById(R.id.tv_block_code);
        tvBlockCode.setText(mBlock.getBlockCode());


        l_land=findViewById(R.id.l_land);
        l_anim=findViewById(R.id.l_anim);
        l_machine=findViewById(R.id.l_machine);
        l_other=findViewById(R.id.l_other);
        l_total=findViewById(R.id.l_total);

        n_land=findViewById(R.id.n_land);
        n_anim=findViewById(R.id.n_anim);
        n_machine=findViewById(R.id.n_machine);
        n_other=findViewById(R.id.n_other);
        n_total=findViewById(R.id.n_total);
        m_land=findViewById(R.id.m_land);
        m_anim=findViewById(R.id.m_anim);
        m_machine=findViewById(R.id.m_machine);
        m_other=findViewById(R.id.m_other);
        m_total=findViewById(R.id.m_total);



    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        try{
            //DatabaseBackup.backupDatabase(this,this,dbHandler.getDatabaseName(), mUXToolkit);
        }
        catch (Exception e){
            mUXToolkit.showToast("Backup Error...");
        }
        Integer lland=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and land_flag=1 and blk_desc=?",mBlock.getBlockCode());
        Integer lanim=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and animal_flag=1 and blk_desc=?",mBlock.getBlockCode());
        Integer lmach=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and machine_flag=1 and blk_desc=?",mBlock.getBlockCode());
        Integer lother=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and land_flag=0 and animal_flag=0 and machine_flag=0 and blk_desc=?",mBlock.getBlockCode());
        Integer ltotal=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and blk_desc=?",mBlock.getBlockCode());


        l_land.setText(String.valueOf(lland));
        l_anim.setText(String.valueOf(lanim));
        l_machine.setText(String.valueOf(lmach));
        l_total.setText(String.valueOf(ltotal));
        l_other.setText(String.valueOf(lother));

        Integer nland=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and land_flag=1 and MNCH='NCH' and blk_desc=?",mBlock.getBlockCode());
        Integer nanim=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and animal_flag=1 and MNCH='NCH' and blk_desc=?",mBlock.getBlockCode());
        Integer nmach=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and machine_flag=1 and MNCH='NCH' and blk_desc=?",mBlock.getBlockCode());
        Integer nother=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and land_flag=0 and animal_flag=0 and machine_flag=0 and MNCH='NCH' and blk_desc=?",mBlock.getBlockCode());
        Integer ntotal=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0  and MNCH='NCH' and blk_desc=?",mBlock.getBlockCode());

        n_land.setText(String.valueOf(nland));
        n_anim.setText(String.valueOf(nanim));
        n_machine.setText(String.valueOf(nmach));
        n_total.setText(String.valueOf(ntotal));
        n_other.setText(String.valueOf(nother));

        Integer mland=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and land_flag=1 and MNCH='MCH' and blk_desc=?",mBlock.getBlockCode());
        Integer manim=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and animal_flag=1 and MNCH='MCH' and blk_desc=?",mBlock.getBlockCode());
        Integer mmach=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and machine_flag=1 and MNCH='MCH' and blk_desc=?",mBlock.getBlockCode());
        Integer mother=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0 and land_flag=0 and animal_flag=0 and machine_flag=0 and MNCH='MCH' and blk_desc=?",mBlock.getBlockCode());
        Integer mtotal=dbHandler.queryInteger("SELECT count(*) from "+Household.class.getSimpleName()+" where env='"+env+"' and is_deleted=0  and MNCH='MCH' and blk_desc=?",mBlock.getBlockCode());

        m_land.setText(String.valueOf(mland));
        m_anim.setText(String.valueOf(manim));
        m_machine.setText(String.valueOf(mmach));
        m_total.setText(String.valueOf(mtotal));
        m_other.setText(String.valueOf(mother));


        stime = dbHandler.queryString( "select start_list_time from "+ Assignment.class.getSimpleName() +" where blk_desc=?",mBlock.getBlockCode());
        etime = dbHandler.queryString( "select end_list_time from "+ Assignment.class.getSimpleName() +" where blk_desc=?",mBlock.getBlockCode());
        hhCount = dbHandler.getCount(Section12.class, "env=? and blk_desc=? and is_deleted=?",env,mBlock.getBlockCode(), "0");
        Long hhUploadedCount = dbHandler.getCount(Section12.class, "env=? and blk_desc=? AND sync_time is not null and is_deleted=?",env,mBlock.getBlockCode(),"0");
        if(stime==null){
            btn_start.setText("Start Block");
        }
        else{
            btn_start.setText("Continue Block");
        }
        tvTotalHouseholds.setText(String.valueOf(hhCount));
        tvTotalUploaded.setText(String.valueOf(hhUploadedCount));


    }
   public void start(View view) {

        if (stime == null) {
            dbHandler.execSql("update " + Assignment.class.getSimpleName() + " set start_list_time='" + getTimeNow() + "' where blk_desc=?", mBlock.getBlockCode());
        }
         // final Intent intent = new Intent(SummaryActivity.this, ListActivity.class);
         final Intent intent = new Intent(SummaryActivity.this, SurveysListActivity.class);
        intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
        startActivity(intent);
        finish();
    }
    public void Finish(View view) {
        if(stime!=null){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("بلاک لسٹنگ مکمل کریں ؟");
            Integer hh23=mBlock.getHh23();
            String census=" آپ نے اب تک گھرانے  "+hhCount+" شمار کئیے ہیں، کیا آپ کو یقین ہے آپ نے تمام گھرانے شمار کرلئیے ہیں ؟";
            if(hh23 !=null && hh23>hhCount){
                hh23=(hh23/10)*10;
                census=" اس بلاک میں حالیہ مردم شماری میں "+hh23+" گھرانے تھے ";
                census+="\n";
                census+=" آپ نے اب تک "+hhCount+" گھرانے لسٹ کئیے ہیں۔\n کیا آپکو یقین ہے تمام گھرانے لسٹ کر لئیے ہیں ؟ ";
            }
            builder.setMessage(census);
            builder.setPositiveButton("  مکمل کریں", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbHandler.execSql("update "+Assignment.class.getSimpleName()+" set end_list_time='"+getTimeNow()+"' where blk_desc=?",mBlock.getBlockCode());
                    Toast.makeText(getApplicationContext(),"بلاک کی لسٹنگ مکمل کرلی گئی ہے",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("نہیں", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        else{
            Toast.makeText(getApplicationContext(),"بلاک ابھی شروع ہی نہیں ہوا۔ مکمل نہیں ہوسکتا", Toast.LENGTH_LONG).show();
        }

    }
}