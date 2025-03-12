package pbs.sme.survey.activity;

import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.R;
import pbs.sme.survey.model.Section12;
//import pbs.sme.survey.model.Section34;
import pk.gov.pbs.utils.StaticUtils;

public class S5Activity extends FormActivity {

    private Button sbtn;
    private  Section12 modelDatabase;

    private final String[] inputValidationOrder= new String[]{
            "q501", "q5021","q5022","q503rs","q503perc"
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s5);
        setDrawer(this,"Section 5: VALUE OF INVENTORIES");
        setParent(this, S6Activity.class);
        scrollView = findViewById(R.id.scrollView);
        sbtn = findViewById(R.id.btns);
        sbtn.setOnClickListener(v -> {
            sbtn.requestFocus();
            StaticUtils.getHandler().post(this::saveForm);
        });
    }
    private void saveForm() {
        sbtn.setEnabled(false);
        Section12 s5;


        try {
            s5 = (Section12) extractValidatedModelFromForm(this, resumeModel,true, inputValidationOrder,"", Section12.class,false, this.findViewById(android.R.id.content));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        if (s5 == null) {
            mUXToolkit.showAlertDialogue("Failed","فارم کو محفوظ نہیں کر سکتے، براہ کرم آگے بڑھنے سے پہلے تمام ڈیٹا درج کریں۔خالی اندراج یا غلط جوابات دیکھنے کے لیے \"OK\" پر کلک کریں۔"  , alertForEmptyFieldEvent);
            sbtn.setEnabled(true);
            return;
        }


        /////TODO CHECKS////////////////////////////


        Long iid = dbHandler.replace(s5);

        if (iid != null && iid > 0) {
            mUXToolkit.showToast("Success");
            btnn.callOnClick();
        }else{
            mUXToolkit.showToast("Failed");
        }
        sbtn.setEnabled(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadForm();
    }
    private void loadForm(){
        List<Section12> s5= dbHandler.query(Section12.class,"uid='"+resumeModel.uid+"' AND (is_deleted=0 OR is_deleted is null)");
        if(s5.size() == 1){
            modelDatabase = s5.get(0);
            //Part1TextWatcher.IGNORE_TEXT_WATCHER = true;
            setFormFromModel(this, modelDatabase, inputValidationOrder, "", false, this.findViewById(android.R.id.content));
        }

    }
}