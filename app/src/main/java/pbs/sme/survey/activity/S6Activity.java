package pbs.sme.survey.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import pbs.sme.survey.model.Section6;
import pbs.sme.survey.R;
//import pbs.sme.survey.helper.AdditionTextWatcher;
import pk.gov.pbs.utils.StaticUtils;

public class S6Activity extends FormActivity {

    private Button sbtn;
    private List<Section6> modelDatabase;

    private final String[] inputValidationOrder= new String[]{
            "value"
    };

    private final String[] codeList= new String[]{
            "601","602","603","604","605","606","600"

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s6);
        setDrawer(this,"Section 6: Taxes ");
        setParent(this, S7Activity.class);
        scrollView = findViewById(R.id.scrollView);

        EditText totalEditText = findViewById(R.id.value__600);

//        AdditionTextWatcher additionTextWatcher = new AdditionTextWatcher(totalEditText);
//
//        for(int i = 0; i < codeList.length-2; i++) {
//            EditText et = findViewById(getResources().getIdentifier("value__"+codeList[i], "id", getPackageName()));
//            et.removeTextChangedListener(additionTextWatcher);
//            et.addTextChangedListener(additionTextWatcher);
//        }

        sbtn = findViewById(R.id.btns);
        sbtn.setOnClickListener(v -> {
            sbtn.requestFocus();
            StaticUtils.getHandler().post(this::saveForm);
        });
    }
    private void saveForm() {
        sbtn.setEnabled(false);
        List<Section6> list=new ArrayList<>();

        for(int i = 0; i < codeList.length; i++) {

            Section6 m = null;
            if(modelDatabase != null && modelDatabase.size() == codeList.length){
                m = modelDatabase.get(i);
            }

            try {
                m = (Section6) extractValidatedModelFromForm(this, m, true, inputValidationOrder, codeList[i], Section6.class, false, this.findViewById(android.R.id.content));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            if (m == null) {
                mUXToolkit.showAlertDialogue("Failed", "فارم کو محفوظ نہیں کر سکتے، براہ کرم آگے بڑھنے سے پہلے تمام ڈیٹا درج کریں۔خالی اندراج یا غلط جوابات دیکھنے کے لیے \"OK\" پر کلک کریں۔", alertForEmptyFieldEvent);
                sbtn.setEnabled(true);
                return;
            }

            list.add(m);
            setCommonFields(m);
            m.section=6;
            m.code=codeList[i];

        }


        /////TODO CHECKS////////////////////////////


        List<Long> iid = dbHandler.replace(list);
        for(Long i:iid){
            if (i != null && i < 0) {
                mUXToolkit.showToast("Failed");
                sbtn.setEnabled(true);
                return;
            }
        }
        mUXToolkit.showToast("Success");
        sbtn.setEnabled(true);
        btnn.callOnClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadForm();
    }


    private void loadForm(){
        List<Section6> list= dbHandler.query(Section6.class,"section="+6+" AND uid='"+resumeModel.uid+"' AND (is_deleted=0 OR is_deleted is null)");
        for(Section6 s: list){
            setFormFromModel(this, s, inputValidationOrder, s.code, false, this.findViewById(android.R.id.content));
        }

    }
}