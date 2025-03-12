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

public class BasicActivity extends MyActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        setDrawer(this,"Basic Summary");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}