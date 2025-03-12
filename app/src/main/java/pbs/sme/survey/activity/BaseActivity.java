package pbs.sme.survey.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import pbs.sme.survey.R;
import pbs.sme.survey.model.Baseline;
import pbs.sme.survey.model.Section12;
import pk.gov.pbs.utils.StaticUtils;

public class BaseActivity extends FormActivity {

    private Button sbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setDrawer(this,"Baseline");
        setParent(this,HomeActivity.class);
        scrollView = findViewById(R.id.scrollView);

        sbtn = findViewById(R.id.btns);
        sbtn.setOnClickListener(v -> {
            sbtn.requestFocus();
            StaticUtils.getHandler().post(this::saveForm);
        });

    }


    private void loadForm(){


    }



    private void saveForm() {
        sbtn.setEnabled(false);
        Baseline model;
        btnn.callOnClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadForm();
    }
}