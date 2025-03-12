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

public class S7Activity extends FormActivity {

    private Button sbtn;
    private List<Section47> modelDatabase;

    private final String[] inputValidationOrder= new String[]{
            "month","student","year"
    };

    private final String[] codeList= new String[]{
            "701","702","703","704","705","706","707","715","716",
            "717","718","719","720","721","722","723","1715","1716",
            "1717","1718","1719","1720","1721","1722","1723","1724","2715","2716",
            "2717","2718","2719","2720","2721","2722","2723","2724","3715","3716",
            "3717","3718","3719","3720","3721","3722","3723","3724",
            "3725","3726","3727","3728","3729","3730","3731","3732",
            "3733","3734","3735","3736","3737","3738","3739","3740","3741","4715","4716",
            "5715","5716", "5717","5718","5719","5720","5721","5722","5723","5724",
            "5725","709","710","711","712","713","714","700"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s7);
        setDrawer(this,"Section 7:Output/Sales During Last 3 Calender Month and Year 2023-24");
        setParent(this, S8Activity.class);
        scrollView = findViewById(R.id.scrollView);

        EditText totalEditText = findViewById(R.id.year_700);

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
                mUXToolkit.showAlertDialogue("Failed", "ÙØ§Ø±Ù… Ú©Ùˆ Ù…Ø­ÙÙˆØ¸ Ù†ÛÛŒÚº Ú©Ø± Ø³Ú©ØªÛ’ØŒ Ø¨Ø±Ø§Û Ú©Ø±Ù… Ø¢Ú¯Û’ Ø¨Ú‘Ú¾Ù†Û’ Ø³Û’ Ù¾ÛÙ„Û’ ØªÙ…Ø§Ù… ÚˆÛŒÙ¹Ø§ Ø¯Ø±Ø¬ Ú©Ø±ÛŒÚºÛ”Ø®Ø§Ù„ÛŒ Ø§Ù†Ø¯Ø±Ø§Ø¬ ÛŒØ§ ØºÙ„Ø· Ø¬ÙˆØ§Ø¨Ø§Øª Ø¯ÛŒÚ©Ú¾Ù†Û’ Ú©Û’ Ù„ÛŒÛ’ \"OK\" Ù¾Ø± Ú©Ù„Ú© Ú©Ø±ÛŒÚºÛ”", alertForEmptyFieldEvent);
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