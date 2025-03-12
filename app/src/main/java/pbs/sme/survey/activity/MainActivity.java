package pbs.sme.survey.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pbs.sme.survey.BuildConfig;
import pbs.sme.survey.R;
import pbs.sme.survey.model.Constants;

public class MainActivity extends AppCompatActivity {
    ImageView logo, banner;
    TextView t_version;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        logo=findViewById(R.id.logo);
        String env="";
        try{
            settings = getSharedPreferences(Constants.PREF, MODE_PRIVATE);
            env=settings.getString(Constants.ENV,"");
        }
        catch (Exception e){}
        //banner=findViewById(R.id.banner);
        t_version=findViewById(R.id.t_version);
        t_version.setText(env.toUpperCase()+" "+BuildConfig.VERSION_NAME);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        ViewGroup.LayoutParams params = logo.getLayoutParams();
        params.height = width/2-150;
        params.width = width/2;
        logo.setLayoutParams(params);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 3000); //temporarily decreased time
    }
}