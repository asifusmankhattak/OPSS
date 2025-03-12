package pbs.sme.survey.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pbs.sme.survey.R;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.model.Section8;
import pbs.sme.survey.utils.Utils;

public class S8Activity extends MyActivity {
    Block mBlock;
    Section12 section1;
    List<Section8> resumeModel;
    List<String> txtIds = Arrays.asList("acquisition","addition", "sales", "own", "life", "scrap");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s8);
        setDrawer(this, "Section 8");
        mBlock = (Block) getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
        section1 = (Section12) getIntent().getSerializableExtra(Constants.EXTRA.IDX_HOUSE);
        assert section1 != null;
        resumeModel = dbHandler.query(Section8.class, "uid=?", section1.uid);
        resumeModel = resumeModel.isEmpty() ? null : resumeModel;
        findViewById(R.id.btnp).setOnClickListener(MoveToPrevious());
        findViewById(R.id.btns).setOnClickListener(SaveData());
        findViewById(R.id.btnn).setOnClickListener(MoveToNext());
        if(resumeModel!=null)
            loadFarmData();
        //applyValidation();
    }

    private void loadFarmData() {
        for(Section8 s8 : resumeModel){
            for(String txtId : txtIds){
                EditText et = (EditText) findViewById(getResources().getIdentifier(txtId+s8.code, "id", getPackageName()));
                switch (txtId){
                    case "acquisition":
                        et.setText(String.valueOf(s8.acq_fixed_assets));
                        break;
                    case "addition":
                        et.setText(String.valueOf(s8.major_improvements));
                        break;
                    case "sales":
                        et.setText(String.valueOf(s8.sales_proceeds));
                        break;
                    case "own":
                        et.setText(String.valueOf(s8.own_account_capital));
                        break;
                    case "life":
                        et.setText(String.valueOf(s8.exp_life));
                        break;
                    case "scrap":
                        et.setText(String.valueOf(s8.scrap_value));
                        break;
                }
            }
        }
    }

    private View.OnClickListener MoveToNext() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getApplicationContext(), Section8Activity.class);
                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
                startActivity(intent);*/
            }
        };
    }

    private View.OnClickListener SaveData() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateSection8();
                Intent intent = new Intent(S8Activity.this, S9Activity.class);
                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
                intent.putExtra(Constants.EXTRA.IDX_HOUSE, section1);
                startActivity(intent);
                finish();
            }
        };
    }

    private void addOrUpdateSection8() {
        List<Section8> list = getSection8List();
        if (resumeModel == null) {
            for(Section8 section8 : list){
                try {
                    dbHandler.insertOrThrow(section8);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception on Section3 Insert: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            updateWithNewValues(resumeModel);
            for(Section8 s8 : resumeModel){
                try {
                    dbHandler.update(s8);
                } catch (Exception e) {
                    mUXToolkit.showToast("Update Error: " + e.getMessage());
                }
            }
        }
    }

    private void updateWithNewValues(List<Section8> list) {
        for(Section8 s8 : list){
            for(String txtId : txtIds){
                EditText et = (EditText) findViewById(getResources().getIdentifier(txtId+s8.code, "id", getPackageName()));
                switch (txtId){
                    case "acquisition":
                        s8.acq_fixed_assets = Utils.GetInteger(et.getText().toString());
                        break;
                    case "addition":
                        s8.major_improvements = Utils.GetInteger(et.getText().toString());
                        break;
                    case "sales":
                        s8.sales_proceeds = Utils.GetInteger(et.getText().toString());
                        break;
                    case "own":
                        s8.own_account_capital = Utils.GetInteger(et.getText().toString());
                        break;
                    case "life":
                        s8.exp_life = Utils.GetInteger(et.getText().toString());
                        break;
                    case "scrap":
                        s8.scrap_value = Utils.GetInteger(et.getText().toString());
                        break;
                }
            }
            s8.userid = getCurrentUser().getID();
            s8.sid = settings.getLong(Constants.SID, 0);
            s8.modified_time = getTimeNowwithSeconds();
            s8.sync_time = null;
            //s8.status = Constants.COMPLETED;
        }
    }

    private List<Section8> getSection8List() {
        List<Section8> list = new ArrayList<>();
        for (int i = 801; i < 814; i++) {
            Section8 s8 = new Section8();
            s8.uid = section1.uid;
            s8.code = String.valueOf(i);
            for(String txtId : txtIds){
                EditText et = (EditText) findViewById(getResources().getIdentifier(txtId+i, "id", getPackageName()));
                switch (txtId){
                    case "acquisition":
                        s8.acq_fixed_assets = Utils.GetInteger(et.getText().toString());
                        break;
                    case "addition":
                        s8.major_improvements = Utils.GetInteger(et.getText().toString());
                        break;
                    case "sales":
                        s8.sales_proceeds = Utils.GetInteger(et.getText().toString());
                        break;
                    case "own":
                        s8.own_account_capital = Utils.GetInteger(et.getText().toString());
                        break;
                    case "life":
                        s8.exp_life = Utils.GetInteger(et.getText().toString());
                        break;
                    case "scrap":
                        s8.scrap_value = Utils.GetInteger(et.getText().toString());
                        break;
                }
            }
            s8.created_time = getTimeNowwithSeconds();
            s8.userid = getCurrentUser().getID();
            s8.sid = settings.getLong(Constants.SID, 0);
            s8.setupDataIntegrity();
            s8.status = 2;
            list.add(s8);
        }
        return list;
    }

    private View.OnClickListener MoveToPrevious() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(S8Activity.this, S9Activity.class);
                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
                intent.putExtra(Constants.EXTRA.IDX_HOUSE, section1);
                startActivity(intent);
                finish();
            }
        };
    }
}
