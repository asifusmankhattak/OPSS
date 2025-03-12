package pbs.sme.survey.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pbs.sme.survey.R;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.model.Section3;
import pbs.sme.survey.utils.Utils;

public class S3Activity extends FormActivity {
    private Button sbtn;
    private List<Section3> modelDatabase;
    Block mBlock;
    Section12 section1;
    List<Section3> resumeModel;
    List<String> txtIds = Arrays.asList("Male","Female", "Wage", "Cash", "kind");
    private final String[] inputValidationOrder= new String[]{
            "male","female", "wages", "other_cash_payment", "payment_in_kind"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s3);
        setDrawer(this, "Section 3");
        setParent(this, S4Activity.class);
        scrollView = findViewById(R.id.scrollView);
        mBlock = (Block) getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
        section1 = (Section12) getIntent().getSerializableExtra(Constants.EXTRA.IDX_HOUSE);
        assert section1 != null;
        resumeModel = dbHandler.query(Section3.class, "uid=?", section1.uid);
        resumeModel = resumeModel.isEmpty() ? null : resumeModel;
     //   findViewById(R.id.btnp).setOnClickListener(MoveToPrevious());
     //   findViewById(R.id.btns).setOnClickListener(SaveData());
       // findViewById(R.id.btnn).setOnClickListener(MoveToNext());
        if(resumeModel!=null)
            loadFarmData();
    }



    private void loadFarmData() {
        for(Section3 s3 : resumeModel){
            for(String txtId : txtIds){
                EditText et = (EditText) findViewById(getResources().getIdentifier(txtId+s3.code, "id", getPackageName()));
                switch (txtId){
                    case "Male":
                        et.setText(String.valueOf(s3.male));
                        break;
                    case "Female":
                        et.setText(String.valueOf(s3.female));
                        break;
                    case "Wage":
                        et.setText(String.valueOf(s3.wages));
                        break;
                    case "Cash":
                        et.setText(String.valueOf(s3.other_cash_payment));
                        break;
                    case "kind":
                        et.setText(String.valueOf(s3.payment_in_kind));
                        break;
                }
            }
//            EditText et = (EditText) findViewById(getResources().getIdentifier("Total"+s3.code, "id", getPackageName()));
//            et.setText(String.valueOf(s3.Male + s3.Female));
        }
    }

    private View.OnClickListener MoveToNext() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), Section4Activity.class);
//                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
//                startActivity(intent);
            }
        };
    }

    private View.OnClickListener SaveData() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateSection3();
                Intent intent = new Intent(S3Activity.this, S4Activity.class);
                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
                intent.putExtra(Constants.EXTRA.IDX_HOUSE, section1);
                startActivity(intent);
                finish();
            }
        };
    }

    private void addOrUpdateSection3() {
        List<Section3> list = getSection3List();
        if (resumeModel == null) {
            for(Section3 section3 : list){
                section3.created_time = getTimeNowwithSeconds();
                section3.userid = getCurrentUser().getID();
                section3.sid = settings.getLong(Constants.SID, 0);
                section3.setupDataIntegrity();
                try {
                    dbHandler.insertOrThrow(section3);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception on Section3 Insert: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            updateSection3List(resumeModel);
            for(Section3 s3 : resumeModel){
                try {
                    dbHandler.update(s3);
                } catch (Exception e) {
                    mUXToolkit.showToast("Update Error: " + e.getMessage());
                }
            }
        }
    }

    private List<Section3> getSection3List() {
        List<Section3> list = new ArrayList<>();
        for (int i = 301; i < 308; i++) {
            Section3 section3 = new Section3();
            section3.code = String.valueOf(i);
            for(String txtId : txtIds){
                EditText et = (EditText) findViewById(getResources().getIdentifier(txtId+i, "id", getPackageName()));
                switch (txtId){
                    case "Male":
                        section3.male = Utils.GetInteger(et.getText().toString());
                        break;
                    case "Female":
                        section3.female = Utils.GetInteger(et.getText().toString());
                        break;
                    case "Wage":
                        section3.wages = Utils.GetInteger(et.getText().toString());
                        break;
                    case "Cash":
                        section3.other_cash_payment = Utils.GetInteger((et.getText().toString()));
                        break;
                    case "kind":
                        section3.payment_in_kind = Utils.GetInteger((et.getText().toString()));
                        break;
                }
            }
            section3.uid = section1.uid;
            section3.status = 2;
            list.add(section3);
        }
        return list;
    }
    private void updateSection3List(List<Section3> list) {
        for(Section3 s3 : list){
            for(String txtId : txtIds){
                EditText et = (EditText) findViewById(getResources().getIdentifier(txtId+s3.code, "id", getPackageName()));
                switch (txtId){
                    case "Male":
                        s3.male = Utils.GetInteger(et.getText().toString());
                        break;
                    case "Female":
                        s3.female = Utils.GetInteger(et.getText().toString());
                        break;
                    case "Wage":
                        s3.wages = Utils.GetInteger((et.getText().toString()));
                        break;
                    case "Cash":
                        s3.other_cash_payment = Utils.GetInteger((et.getText().toString()));
                        break;
                    case "kind":
                        s3.payment_in_kind = Utils.GetInteger((et.getText().toString()));
                        break;
                }
            }
            s3.userid = getCurrentUser().getID();
            s3.sid = settings.getLong(Constants.SID, 0);
            s3.modified_time = getTimeNowwithSeconds();
            s3.sync_time = null;
            s3.uid = section1.uid;
            s3.status = 2;
        }
    }

    private View.OnClickListener MoveToPrevious() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), S2Activity.class);
                intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
                intent.putExtra(Constants.EXTRA.IDX_HOUSE, section1);
                //Section2 s2 = dbHandler.querySingle(Section2.class, "section1Id=? and establishmentUid=?", section1.id.toString(), section1.uuid);
                //intent.putExtra(Constants.EXTRA.IDX_SECTION2, s2);
                startActivity(intent);
            }
        };
    }
}
