package pbs.sme.survey.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pbs.sme.survey.R;
import pbs.sme.survey.helper.SyncService;
import pk.gov.pbs.utils.CustomActivity;

public class DebugActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        checkAllPermissions();

    }
}