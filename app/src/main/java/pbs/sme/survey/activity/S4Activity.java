package pbs.sme.survey.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import pbs.sme.survey.model.Section47;
import pbs.sme.survey.R;
//import pbs.sme.survey.helper.AdditionTextWatcher;
import pk.gov.pbs.utils.StaticUtils;

public class S4Activity extends FormActivity {

    private Button sbtn;
    private List<Section47> modelDatabase;

    private final String[] inputValidationOrder= new String[]{
            "month","student","year"
    };

    private final String[] codeList= new String[]{
            "401","402","403","404","405","406","407","400","408","409",
            "410","411","412","413","414","415","416","417","418","419",
            "420","421","422","423","424","425","426","427","428","429",
          "430","431","432","433","434","435","436","437","1426","1427","1428","1429",
           "1430","1431","1432","1433","1434","1435","1436","2426","2427","2428","2429",
            "2430","2431","2432","3426","3427","3428","3429",
            "3430","3431","3432","3433","4426","4427","4428","4429",
            "4430","4431","4432","5426","5427","5428","5429",
            "5430","5431","5432","5433","5434","5435","5436","5437","5438","5439"
            ,"5440","5441","5442","5443","5444","400"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s4);
        setDrawer(this,"Section 4: INTERMEDIATE INPUTS DURING LAST YEAR ");
        setParent(this, S5Activity.class);
        scrollView = findViewById(R.id.scrollView);

        EditText totalEditText = findViewById(R.id.year_400);

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
        List<Section47> list=new ArrayList<>();

        for(int i = 0; i < codeList.length; i++) {

            Section47 m = null;
            if(modelDatabase != null && modelDatabase.size() == codeList.length){
                m = modelDatabase.get(i);
            }

            try {
                m = (Section47) extractValidatedModelFromForm(this, m, true, inputValidationOrder, codeList[i], Section47.class, false, this.findViewById(android.R.id.content));
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
            m.section=4;
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
        List<Section47> list= dbHandler.query(Section47.class,"section="+4+" AND uid='"+resumeModel.uid+"' AND (is_deleted=0 OR is_deleted is null)");
        for(Section47 s: list){
            setFormFromModel(this, s, inputValidationOrder, s.code, false, this.findViewById(android.R.id.content));
        }

    }
}