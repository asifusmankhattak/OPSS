package pbs.sme.survey.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pbs.sme.survey.BuildConfig;
import pbs.sme.survey.DB.Database;
import pbs.sme.survey.R;
import pbs.sme.survey.api.ApiClient;
import pbs.sme.survey.api.ApiInterface;
import pbs.sme.survey.helper.DialogHelper;
import pbs.sme.survey.helper.FilterHelper;
import pbs.sme.survey.helper.GPSHelper;
import pbs.sme.survey.helper.NetworkHelper;
import pbs.sme.survey.helper.PermissionHelper;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.User;
import pbs.sme.survey.utils.Utils;
import pk.gov.pbs.utils.FileManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements LocationListener {
    SharedPreferences settings;
    GPSHelper helper;
    LocationManager manager;
    Location gps, net, best;
    EditText uname, pass;
    TextView txt_error;
    RelativeLayout content;
    ImageView logo, show_pass_btn;
    View view;
    String did, dname, mac;
    DialogHelper gdh=new DialogHelper();

    Context context;

    FileManager mFileManager;
    Database dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        context=this;
        //mFileManager = new FileManager(this);
        settings=getSharedPreferences(Constants.PREF,MODE_PRIVATE);
        if(settings.getBoolean(Constants.IS_LOGGED,false)){
            nextActivity();
        }

        logo=findViewById(R.id.logo);
        view=findViewById(R.id.view);
        content=findViewById(R.id.content);
        show_pass_btn=findViewById(R.id.show_pass_btn);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        ViewGroup.LayoutParams params = logo.getLayoutParams();
        params.width = (int) (width/2.2);
        params.height = (int) ((width/2.2)/1.84);
        logo.setLayoutParams(params);

        params = view.getLayoutParams();
        params.height = (int) (height*0.4);
        view.setLayoutParams(params);

        params = content.getLayoutParams();
        params.width = (int) (width*0.90);
        //params.height = (int) (height*0.55);
        content.setLayoutParams(params);

        uname=findViewById(R.id.uname);
        pass=findViewById(R.id.pass);
        txt_error=findViewById(R.id.txt_error);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(PermissionHelper.checkAll(context,LoginActivity.this)){
            LocationSettings();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(manager!=null){
            manager.removeUpdates(this);
            manager=null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        gdh.hideProgressDialog();
        gdh.hideSingleError();
    }


    private void RequestAgain(String perm)
    {
        final DialogHelper d=new DialogHelper();
        d.SingleClickDialogError(this,"ALLOW PERMISSION","Allow "+perm+" Permissions in Settings", "Goto Setting", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.hideSingleError();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 1000);

            }
        });
    }

    private void RequestSettingDialog(){
        gdh.SingleClickDialogError(this,"LOCATION IS DISABLED","Location is turned 'Off' Kindly 'Turn On' the Location in the Location Settings", "Goto Setting", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gdh.hideSingleError();
                final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }


    public void Login(final View view) {
        final String username=uname.getText().toString();
        final String password=pass.getText().toString();

        String ufil= FilterHelper.checkInjection(username);
        String upass=FilterHelper.checkInjection(password);

        final String os="Android "+ Build.VERSION.SDK_INT;
        final String referer= BuildConfig.APPLICATION_ID+" "+ BuildConfig.VERSION_NAME;

        double lat=0.0;
        double lon=0.0;
        txt_error.setText("");
        boolean lp=LocationSettings();
        if(!lp){
            return;
        }
        else if(best!=null){
            lat=best.getLatitude();
            lon=best.getLongitude();
        }

        if(!getPhoneState()){
            displayError("Read Phone State Failed. Check Setting");
        }
        else if(username.trim().length()==0){
            displayError("Username is Empty.");
            uname.requestFocus();
        }
        else if(ufil!=null){
            displayError("Invalid Username, Remove: "+ufil);
            uname.requestFocus();
        }
        else if(password.trim().length()==0){
            displayError("Password is Empty.");
            pass.requestFocus();
        }
        else if(upass!=null){
            displayError("Invalid Password, Remove: "+upass);
            pass.requestFocus();
        }
        else{
            view.setEnabled(false);
            gdh.showProgressDialog(this,"Connecting to Server", "Please Wait! Login Request is under process.",false);
            Call<List<User>> call= ApiClient.getApiClient().create(ApiInterface.class).authenticate(username,password,  did, dname, mac, os,  referer, lat, lon);
            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    try{
                        List<User> u=response.body();
                        if(!response.isSuccessful()){
                            displayError("Server Side Error: "+response.code()+", Message: "+response.message());
                        }
                        else if(u==null){
                            displayError("Server Failed with Null Response");
                        }
                        else if(u.size()==0){
                            displayError("Server Failed with Empty Response (No User Found)");
                        }
                        else if(u.size()==1){
                            User user=u.get(0);
                            if(user.getCode()!=1){
                                displayError(user.getMsg());
                            }
                            else {
                                SharedPreferences.Editor editor=settings.edit();
                                editor.putBoolean(Constants.IS_LOGGED,true);
                                editor.putInt(Constants.UID,user.getID());
                                editor.putLong(Constants.SID,user.getSid());
                                editor.putString(Constants.ENUMERATOR,user.getNAME());
                                editor.putString(Constants.ROLE,user.getROLE());
                                editor.putString(Constants.LAST_LOGIN,getTimeNow());
                                editor.putString("user",user.toString());
                                editor.commit();
                                Toast.makeText(context,"Welcome! "+user.getNAME(),Toast.LENGTH_LONG).show();
                                if (dbHandler == null){
                                    dbHandler = Database.getInstance(context, user);
                                }

                                if(dbHandler.LoginReplace(user)>0){
                                    nextActivity();
                                }
                                else{
                                    displayError("Local Database Operation Failure");
                                }
                            }
                        }
                        else{
                            displayError("Duplicate User Error");
                        }
                    }
                    catch (Exception e){
                        displayError("Local Error on Login Response\n"+e.getMessage());
                    }
                    gdh.hideProgressDialog();
                    view.setEnabled(true);

                }
                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    new NetworkHelper(getApplicationContext(),txt_error, t);
                    gdh.hideProgressDialog();
                    view.setEnabled(true);
                    //REMOVE THIS LINE BEFORE RELEASE
                    //performDummyLogin();
                }
            });
        }
    }

    private void nextActivity(){
        Intent intent=new Intent(LoginActivity.this,ImportActivity.class);
        startActivity(intent);
        finish();
    }

    private void displayError(String e){
        txt_error.setTextColor(Color.RED);
        txt_error.setText(e);
        RunAnimation();
    }

    @Override
    public void onLocationChanged(Location location) {
        txt_error.setText("Location Available ("+location.getLatitude()+", "+location.getLongitude()+")");
        txt_error.setTextColor(Color.BLACK);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        txt_error.setText("");
        gdh.hideSingleError();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public boolean LocationSettings(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                //permission is granted
                int l = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
                if(l!=0){
                    //location is enabled
                    if(helper==null){
                        helper=new GPSHelper(this,this);
                        manager=helper.getManager();
                    }

                    if(helper.checkProviders()==3){
                        gps=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        net=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(gps!=null && net!=null){
                            if(gps.getAccuracy()<net.getAccuracy()){
                                best=net;
                            }
                            else{
                                best=gps;
                            }
                        }
                        else if(gps!=null){
                            best=gps;
                        }

                        else if(net!=null){
                            best=net;
                        }
                        else{
                            displayError("No Location Available So Far... Try Changing Position");
                            return false;
                        }
                        return true;
                    }
                    else if(helper.checkProviders()==2){
                        net=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(net!=null){
                            best=net;
                        }
                        return true;
                    }
                    else if(helper.checkProviders()==1){
                        gps=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(gps!=null){
                            best=gps;
                        }
                        return true;
                    }
                    else{
                        displayError("No Location Provider Available. GPS or Network Location Disabled");
                        return false;
                    }
                }
                else{
                    displayError("Location Status is Disabled");
                    RequestSettingDialog();
                    return false;
                }
            } catch (Settings.SettingNotFoundException e) {
                displayError(e.getMessage());
                return false;
            }
        }
        else if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
            displayError("Permission Not Granted");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE},100);
            return false;

        }
        else{
            displayError("Permission Not Granted");
            RequestAgain("Location");
            return false;
        }
    }




    private boolean getPhoneState(){
        if(PermissionHelper.checkPhone(context, LoginActivity.this)){
            mac= Utils.getMacAddress(this);
            did= Utils.getIMEIDeviceId(this);
            dname= Utils.getDeviceName();
            return true;
        }
        return false;
    }

    public void forgotPassword(View view) {
        /*Intent intent=new Intent(LoginActivity.this,PasswordActivity.class);
        startActivity(intent);*/
    }

    private void RunAnimation()
    {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.fadein);
        a.reset();
        txt_error.clearAnimation();
        txt_error.startAnimation(a);
    }

    public void ShowHidePass(View view){

        if(view.getId()==R.id.show_pass_btn){

            if(pass.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.hide_password);
                pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.show_password);
                pass.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    public static String getTimeNow(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date time = Calendar.getInstance().getTime();
        return format.format(time);
    }

    private void performDummyLogin(){
        User u =new User();
        u.setID(14498);
        u.setROLE("Enumerator");
        u.setNAME("Test Login");
        u.setCNIC("5555555555555");
        SharedPreferences.Editor editor=settings.edit();
        editor.putBoolean(Constants.IS_LOGGED,true);
        editor.putInt(Constants.UID,u.getID());
        editor.putLong(Constants.SID,u.getSid());
        editor.putString(Constants.ENUMERATOR,u.getNAME());
        editor.putString(Constants.ROLE,u.getROLE());
        editor.putString(Constants.LAST_LOGIN,getTimeNow());
        editor.putString("user",u.toString());
        editor.commit();
        Toast.makeText(context,"Welcome! "+u.getNAME(),Toast.LENGTH_LONG).show();
        if (dbHandler == null){
            dbHandler = Database.getInstance(context, u);
        }

        if(dbHandler.LoginReplace(u)>0){
            nextActivity();
        }
        else{
            displayError("Local Database Operation Failure");
        }
    }
}
