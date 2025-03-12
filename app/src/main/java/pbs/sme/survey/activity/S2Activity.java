package pbs.sme.survey.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.List;

import pbs.sme.survey.R;
import pbs.sme.survey.model.Section12;
import pk.gov.pbs.utils.StaticUtils;

public class S2Activity extends FormActivity {

    private Button sbtn;
    private Section12 section2_database;
    private RadioGroup is_registered, is_hostel_available;
    private Spinner survey_type, major_activity, organization_type;
    private LinearLayout l_seasonal;
    private EditText establishment_months;
    private CheckBox[] monthCheckboxes;
    private final String[] inputValidationOrder = new String[]{
            "year", "is_registered", "agency", "maintaining_accounts", "type_org", "description_psic", "psic", "hostel_facilty",
            "food_laundry_other", "major_activities", "months"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s2);
        setDrawer(this, "Section 2");
        setParent(this, S3Activity.class);
        scrollView = findViewById(R.id.scrollView);

        sbtn = findViewById(R.id.btns);
        sbtn.setOnClickListener(v -> {
            sbtn.requestFocus();
            StaticUtils.getHandler().post(this::saveForm);
        });

        is_registered = findViewById(R.id.is_registered);
        is_registered.setOnCheckedChangeListener((group, checkedId) -> {
            findViewById(R.id.l_agency).setVisibility(checkedId == R.id.is_registered1 ? View.VISIBLE : View.GONE);
        });

        is_hostel_available = findViewById(R.id.hostel_facilty);
        is_hostel_available.setOnCheckedChangeListener((group, checkedId) -> {
            findViewById(R.id.l_food_services).setVisibility(checkedId == R.id.hostel_facilty1 ? View.VISIBLE : View.GONE);
        });

        survey_type = findViewById(R.id.survey_type);
        major_activity = findViewById(R.id.major_activities);
        organization_type = findViewById(R.id.type_org);
        l_seasonal = findViewById(R.id.l_seasonal);
        establishment_months = findViewById(R.id.establishment_months);
        establishment_months.setKeyListener(null); // Make it non-editable

        monthCheckboxes = new CheckBox[]{
                findViewById(R.id.jan), findViewById(R.id.feb), findViewById(R.id.mar), findViewById(R.id.apr),
                findViewById(R.id.may), findViewById(R.id.jun), findViewById(R.id.jul), findViewById(R.id.aug),
                findViewById(R.id.sep), findViewById(R.id.oct), findViewById(R.id.nov), findViewById(R.id.dec)
        };

        for (CheckBox monthCheckbox : monthCheckboxes) {
            monthCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> updateMonthCount());
        }

        survey_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMajorActivitySpinner(position);
                toggleSeasonalVisibility(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateMajorActivitySpinner(int surveyPosition) {
        int arrayId;
        switch (surveyPosition) {
            case 0:
                arrayId = R.array.spn_major_activity_education;
                break;
            case 1:
                arrayId = R.array.spn_major_activity_health;
                break;
            case 2:
                arrayId = R.array.spn_major_activity_publishing;
                break;
            case 3:
                arrayId = R.array.spn_major_activity_personal_services;
                break;
            case 4:
                arrayId = R.array.spn_major_activity_accommodation_food;
                break;
            default:
                arrayId = R.array.spn_major_activity_education;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        major_activity.setAdapter(adapter);
    }

    private void toggleSeasonalVisibility(int surveyPosition) {
        if (surveyPosition == 3 || surveyPosition == 4 || surveyPosition == 2) {
            l_seasonal.setVisibility(View.VISIBLE);
        } else {
            l_seasonal.setVisibility(View.GONE);
        }
    }

    private void updateMonthCount() {
        int count = 0;
        for (CheckBox monthCheckbox : monthCheckboxes) {
            if (monthCheckbox.isChecked()) {
                count++;
            }
        }
        establishment_months.setText(String.valueOf(count));
    }

    private void loadForm() {
        List<Section12> s2 = dbHandler.query(Section12.class, "uid='" + resumeModel.uid + "' AND (is_deleted=0 OR is_deleted is null)");
        if (s2.size() == 1) {
            section2_database = s2.get(0);
            setFormFromModel(this, section2_database, inputValidationOrder, "", false, this.findViewById(android.R.id.content));
        }
    }

    private void saveForm() {
        sbtn.setEnabled(false);
        Section12 s2;

        try {
            s2 = (Section12) extractValidatedModelFromForm(this, resumeModel, true, inputValidationOrder, "", Section12.class, false, this.findViewById(android.R.id.content));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (s2 == null) {
            mUXToolkit.showAlertDialogue("Failed", "ÙØ§Ø±Ù… Ú©Ùˆ Ù…Ø­ÙÙˆØ¸ Ù†ÛÛŒÚº Ú©Ø± Ø³Ú©ØªÛ’ØŒ Ø¨Ø±Ø§Û Ú©Ø±Ù… Ø¢Ú¯Û’ Ø¨Ú‘Ú¾Ù†Û’ Ø³Û’ Ù¾ÛÙ„Û’ ØªÙ…Ø§Ù… ÚˆÛŒÙ¹Ø§ Ø¯Ø±Ø¬ Ú©Ø±ÛŒÚºÛ”", alertForEmptyFieldEvent);
            sbtn.setEnabled(true);
            return;
        }

        Long iid = dbHandler.replace(s2);

        if (iid != null && iid > 0) {
            mUXToolkit.showToast("Success");
            btnn.callOnClick();
        } else {
            mUXToolkit.showToast("Failed");
        }
        sbtn.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadForm();
    }
}
