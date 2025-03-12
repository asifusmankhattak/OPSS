package pbs.sme.survey.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.R;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.model.Section9;
import pbs.sme.survey.utils.Utils;

public class S9Activity extends MyActivity {
    Block mBlock;
    Section12 section1;
    List<Section9> resumeModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s9);
        setDrawer(this, "Section 9");
        mBlock = (Block) getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
        section1 = (Section12) getIntent().getSerializableExtra(Constants.EXTRA.IDX_HOUSE);
        assert section1 != null;
        resumeModel = dbHandler.query(Section9.class, "section=9 and uid=?", section1.uid);
        resumeModel = resumeModel.isEmpty() ? null : resumeModel;
        findViewById(R.id.btnp).setOnClickListener(MoveToPrevious());
        findViewById(R.id.btns).setOnClickListener(SaveData());
        findViewById(R.id.btnn).setOnClickListener(MoveToNext());
        if(resumeModel!=null)
            loadFarmData();
    }

    private void loadFarmData() {
        int sum = 0;
        for(Section9 s9 : resumeModel){
            EditText et = (EditText) findViewById(getResources().getIdentifier("code"+s9.code, "id", getPackageName()));
            et.setText(String.valueOf(s9.rupees));
            sum += s9.rupees;
        }
        EditText et = (EditText) findViewById(getResources().getIdentifier("code903", "id", getPackageName()));
        et.setText(String.valueOf(sum));
    }

    private View.OnClickListener MoveToNext() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(S9Activity.this,ListActivity.class);
                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener SaveData() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateSection9();
                Intent intent = new Intent(S9Activity.this, ListActivity.class);
                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
                startActivity(intent);
                finish();
            }
        };
    }

    private void addOrUpdateSection9() {
        List<Section9> list = getSection9List();
        if (resumeModel == null) {
            for(Section9 section9 : list){
                try {
                    dbHandler.insertOrThrow(section9);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception on Section3 Insert: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            updateWithNewValues(resumeModel);
            for(Section9 s9 : resumeModel){
                try {
                    dbHandler.update(s9);
                } catch (Exception e) {
                    mUXToolkit.showToast("Update Error: " + e.getMessage());
                }
            }
        }
    }

    private void updateWithNewValues(List<Section9> list) {
        for(Section9 s9 : list){
            EditText et = (EditText) findViewById(getResources().getIdentifier("code"+s9.code, "id", getPackageName()));
            s9.rupees = Utils.GetInteger(et.getText().toString());
            s9.section = 9;
            s9.userid = getCurrentUser().getID();
            s9.sid = settings.getLong(Constants.SID, 0);
            s9.modified_time = getTimeNowwithSeconds();
            s9.sync_time = null;
            s9.status = 2;
        }
    }

    private List<Section9> getSection9List() {
        List<Section9> list = new ArrayList<>();
        for (int i = 901; i < 903; i++) {
            Section9 s9 = new Section9();
            s9.uid = section1.uid;
            s9.section = 9;
            s9.code = String.valueOf(i);
            EditText et = (EditText) findViewById(getResources().getIdentifier("code"+i, "id", getPackageName()));
            s9.rupees = Utils.GetInteger(et.getText().toString());
            s9.created_time = getTimeNowwithSeconds();
            s9.userid = getCurrentUser().getID();
            s9.sid = settings.getLong(Constants.SID, 0);
            s9.setupDataIntegrity();
            s9.status = 2;
            list.add(s9);
        }
        return list;
    }

    private View.OnClickListener MoveToPrevious() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(S9Activity.this, Baseline.class);
                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
                intent.putExtra(Constants.EXTRA.IDX_HOUSE, section1);
                startActivity(intent);
                finish();
            }
        };
    }
}
