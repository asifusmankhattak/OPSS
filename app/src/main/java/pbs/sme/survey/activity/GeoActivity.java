package pbs.sme.survey.activity;

import static pbs.sme.survey.helper.SyncService.BROADCAST_HOUSEHOLD_CHANGE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import egolabsapps.basicodemine.offlinemap.Interfaces.GeoPointListener;
import pbs.sme.survey.BuildConfig;
import pbs.sme.survey.R;
import pbs.sme.survey.helper.DateHelper;
import pbs.sme.survey.helper.DialogHelper;
import pbs.sme.survey.helper.GPSHelper;
import pbs.sme.survey.helper.SyncScheduler;
import pbs.sme.survey.helper.SyncService;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.House;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.utils.DonutProgress;
import pk.gov.pbs.geomap.LocationUtils;
import pk.gov.pbs.geomap.utils.CustomMapListener;
import pk.gov.pbs.geomap.utils.CustomMapUtils;
import pk.gov.pbs.geomap.views.CustomOfflineMapView;

public class GeoActivity extends MyActivity implements LocationListener, GeoPointListener, CustomMapListener {
    final DialogHelper dh=new DialogHelper();
    LocationManager manager;
    Location gps, net, best;
    GeoPoint manualTagLocation;
    boolean manualTagEnabled = false;
    GPSHelper helper;
    CustomOfflineMapView mapOffline;
    MapView mapOnline, map;

    Block mBlock;
    Section12 resumeModel;
    EditText tv_title, tv_emp;
    TextView tvBlockCode, tvSno, mapTextOverlay;
    int newHouseNumber = 1;
    Marker currentLocationMarker;
    Polygon accuracyCircle;

    Spinner survey_id;
    FloatingActionButton btnLocate, btnBoundary;
    KmlDocument boundary;
    Polyline distanceLine;
    List<GeoPoint> distancePoints;

    String boundaryGeoJson;
    private CountDownTimer countDownTimer;
    private DonutProgress countDownProgress;
    private final long startTime = 20000;
    private final long interval = 100;
    int outside_in_meters=0;
    Long hhCount=0L;

    Integer idx_id,  idx_emp; String idx_title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);
        setDrawer(this,"Add Building (Geo-Tagging)");
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        //tv_emp = findViewById(R.id.tv_emp);
        survey_id = findViewById(R.id.survey_id);
        tv_title = findViewById(R.id.tv_title);
        mapTextOverlay = findViewById(R.id.map_text_overlay);
        countDownProgress = findViewById(R.id.countdown_progress);
        tvSno = findViewById(R.id.tv_sno);
        tvBlockCode = findViewById(R.id.tv_block_code);

        btnLocate = findViewById(R.id.btnLocationMode);
        btnBoundary = findViewById(R.id.btnLocateBoundary);
        init();
    }

    public void updateStats(){
        tvBlockCode.setText(mBlock.getBlockCode());
        if (resumeModel != null){
            newHouseNumber = resumeModel.sno;
            tvSno.setText(" SME "+("000" + resumeModel.sno).substring(String.valueOf(resumeModel.sno).length()));

        } else {

            tvSno.setText(" SME "+("000" + newHouseNumber).substring(String.valueOf(newHouseNumber).length()));
        }
    }

    private void init(){
        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        mBlock = (Block) getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
        idx_title = getIntent().getStringExtra(Constants.EXTRA.IDX_TITLE);
        idx_id =  getIntent().getIntExtra(Constants.EXTRA.IDX_ID,0);
        idx_emp = getIntent().getIntExtra(Constants.EXTRA.IDX_EMP,0);
        boundaryGeoJson=dbHandler.queryString("SELECT boundary from Assignment where blk_desc=?",mBlock.getBlockCode());
        resumeModel = (Section12) getIntent().getSerializableExtra(Constants.EXTRA.IDX_HOUSE);


        //get complete object from db
        if (resumeModel != null) {
            resumeModel = dbHandler.querySingle(
                    Section12.class,
                    "blk_desc=? and uid=? and env=?",
                    resumeModel.blk_desc,
                    resumeModel.uid,
                    env
            );
            tv_title.setText(resumeModel.title);
            if(resumeModel.survey_id!=null)
                survey_id.setSelection(resumeModel.survey_id);
//            tv_emp.setText(String.valueOf(resumeModel.emp_count));
            mapTextOverlay.setText("Ø¢Ù¾ Ø§Ø³ Ú¯Ú¾Ø± Ú©Ùˆ Ù¾ÛÙ„Û’ ÛÛŒ Ø¬ÛŒÙˆÙ¹ÛŒÚ¯ Ú©Ø± Ú©Û’ ÛÛŒÚº");
        }
        else if(idx_title!=null && !idx_title.isEmpty()){
            tv_title.setText(idx_title);
            tv_emp.setText(String.valueOf(idx_emp));
        }

        List<String[]> maxH = dbHandler.queryRowsAsList("select max(sno) as hcount from "+ Section12.class.getSimpleName()+" where env=? and blk_desc=?",env,mBlock.getBlockCode());
        if (maxH.size()>1 && maxH.get(1).length > 0)
            newHouseNumber = maxH.get(1)[0] != null ? Integer.parseInt(maxH.get(1)[0]) + 1 : 1;

        updateStats();




        btnLocate.setOnClickListener(v -> {
            if (isResuming()){
                map.getController().animateTo(currentLocationMarker.getPosition());
            }

            if (map != null && best != null){
                map.getController().animateTo(new GeoPoint(best.getLatitude(),best.getLongitude()));
                updateCurrentLocationMarker();
            }
        });

        btnLocate.setOnLongClickListener(v -> {
            if (countDownProgress.getVisibility() == View.GONE && !isResuming()) {
                switchLocationMode();
                mapTextOverlay.setText("Ø¨Ø±Ø§Û Ú©Ø±Ù… Ø§Ù¾Ù†Ø§ Ù…ÙˆØ¬ÙˆØ¯Û Ù…Ù‚Ø§Ù… Ø®ÙˆØ¯ Ù…Ù†ØªØ®Ø¨ Ú©Ø±ÛŒÚºÛ”");
            }
            return true;
        });

        btnBoundary.setOnClickListener(v -> {
            if (map != null)
                flyToBoundary();
        });

        if (isResuming()) {
            countDownProgress.setVisibility(View.GONE);
        }
    }



    public void startSurvey(View view) {

        if(!isResuming() && countDownProgress.getProgress()>0){
            return;
        }
        String cmsg=DateHelper.DateCheck(mBlock.getStartDate(),mBlock.getEndDate(),mBlock.getBeforeDate(), mBlock.getAfterDate());
        if(cmsg!=null){
            mUXToolkit.showToast(cmsg);
            return;
        }
        if(best!=null){
            if(boundary!=null && !LocationUtils.isPointInPolygon(new GeoPoint(best), boundary)) {
                String omsg=GPSHelper.AllowedOtuside(mBlock.getAllowOutside(), mBlock.getOutSidemMargin(),outside_in_meters);
                if(omsg!=null){
                    mUXToolkit.showToast(omsg);
                    return;
                }
            }
        }
        else if(manualTagLocation!=null){
            if(boundary!=null && !LocationUtils.isPointInPolygon(new GeoPoint(manualTagLocation), boundary)) {
                String omsg=GPSHelper.AllowedOtuside(mBlock.getAllowOutside(), mBlock.getOutSidemMargin(),outside_in_meters);
                if(omsg!=null){
                    mUXToolkit.showToast(omsg);
                    return;
                }
            }
        }
        else if(!isResuming()){
            mUXToolkit.showToast("Ù„ÙˆÚ©ÛŒØ´Ù† Ú©Ø§ Ø§Ù†ØªØ¸Ø§Ø± Ú©Ø±ÛŒÚº ÛŒØ§ Ù…ÛŒÙ†ÙˆØ¦Ù„ Ø¬ÛŒÙˆÙ¹ÛŒÚ¯ Ú©Ø±ÛŒÚº");
            return;
        }



        Section12 nHno = addOrUpdateHouse();
        if (nHno != null) {
            Intent intent = new Intent(GeoActivity.this, HomeActivity.class);
            intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
            intent.putExtra(Constants.EXTRA.IDX_HOUSE, nHno);
            startActivity(intent);
            finish();
        } else {
            mUXToolkit.showToast("Ù†Ø¦Û’ Ú¯Ú¾Ø± Ú©Ø§ ÚˆÛŒÙ¹Ø§ Ù…Ø­ÙÙˆØ¸ Ú©Ø±Ù†Û’ Ù…ÛŒÚº Ù†Ø§Ú©Ø§Ù…");
        }
    }

    /**
     * @return new House object if db insertion is successful else null
     */
    private Section12 addOrUpdateHouse(){
        long res=0;
        String address = tv_title.getText().toString();
        if (address.isEmpty()){
            tv_title.setError("Enter Name of the Entity");
            tv_title.requestFocus();
            return null;
        }

//       String emp = tv_emp.getText().toString();
        String emp="100";
        if (survey_id.getSelectedItemPosition()==0){
            mUXToolkit.showToast("Kindly select Survey");
            return null;
        }
        else if((best==null && manualTagLocation==null) && resumeModel==null){
            mUXToolkit.showToast("Ù„ÙˆÚ©ÛŒØ´Ù† Ú©Ø§ Ø§Ù†ØªØ¸Ø§Ø± Ú©Ø±ÛŒÚº ÛŒØ§ Ù…ÛŒÙ†ÙˆØ¦Ù„ Ø¬ÛŒÙˆÙ¹ÛŒÚ¯ Ú©Ø±ÛŒÚº");
            return null;
        }
        else if((best!=null || manualTagLocation!=null) && resumeModel==null){
            try {
                Section12 h = new Section12(mBlock.getBlockCode(), newHouseNumber, tv_title.getText().toString(),Integer.parseInt(emp));
                h.sno=newHouseNumber;
                if(idx_id!=null && idx_id>0){
                    h.flag=idx_id;
                }
                h.created_time=getTimeNowwithSeconds();
                h.userid = getCurrentUser().getID();
                h.sid=settings.getLong(Constants.SID,0);
                h.is_deleted=0;
                h.env=env;
                h.survey_id=survey_id.getSelectedItemPosition();
                h.setupDataIntegrity();
                if(best!=null){
                    h=setLocationAttributes(h);
                    res=dbHandler.insertOrThrow(h);
                    h.id = Math.toIntExact(res);
                    return (res > 0) ? h : null;
                }
                else{
                    mUXToolkit.showToast("Ù„ÙˆÚ©ÛŒØ´Ù† Ú©Ø§ Ø§Ù†ØªØ¸Ø§Ø± Ú©Ø±ÛŒÚº ÛŒØ§ Ù…ÛŒÙ†ÙˆØ¦Ù„ Ø¬ÛŒÙˆÙ¹ÛŒÚ¯ Ú©Ø±ÛŒÚº");
                    return null;
                }
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Exception on House Insert: "+e.getMessage(),Toast.LENGTH_LONG).show();
                return null;
            }
        }
        else if(resumeModel!=null) {
            Section12 h = resumeModel;
            h.userid = getCurrentUser().getID();
            h.sid = settings.getLong(Constants.SID, 0);
            h.modified_time = getTimeNowwithSeconds();
            h.env=env;

            int em=0;
            try {
                em=Integer.parseInt(emp);
            }
            catch (Exception e){
            }

            if (!h.title.equalsIgnoreCase(tv_title.getText().toString()) || h.survey_id==null
                    || h.survey_id!= survey_id.getSelectedItemPosition()) {
                h.survey_id=survey_id.getSelectedItemPosition();
                if (best != null) {
                    h = setLocationAttributes(h);
                }
                h.title = tv_title.getText().toString();
                try {
                    h.sync_time = null;
                    res=dbHandler.update(h);
                } catch (Exception e) {
                    mUXToolkit.showToast("Update Error: " + e.getMessage());
                }
            }
            return h;
        }
        else{
            return null;
        }
    }



    public void setMap(){
        if (map != null)
            return;

        mapOffline = findViewById(R.id.mapOffline);
        mapOnline = findViewById(R.id.mapOnline);
        if (CustomMapUtils.hasOfflineSources()) {
            mapOffline.init(this, this);
            map = mapOffline.getMapUtils().getMap();
            ((ViewGroup) mapOnline.getParent()).removeView(mapOnline);
            mapOnline = null;
        } else {
            mapOnline.setTileSource(TileSourceFactory.MAPNIK);
            map = mapOnline;
            if(mapOffline!=null){
                ((ViewGroup) mapOffline.getParent()).removeView(mapOffline);
            }
            setupMap(mapOnline);
            mapOffline = null;
        }
        map.setHasTransientState(true);
    }

    public void setupMap(MapView map){
        if(env.equalsIgnoreCase("field")){
            map.setMinZoomLevel(14d);
            map.setMaxZoomLevel(18d);
            map.getController().setZoom(16f);
        }
        else{
            map.setMinZoomLevel(6d);
            map.setMaxZoomLevel(14d);
            map.getController().setZoom(6f);
        }
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        if(boundaryGeoJson!=null && !boundaryGeoJson.isEmpty()){
            try {
                boundary = CustomOfflineMapView.MapUtils.parseGeoJson(boundaryGeoJson);
                CustomOfflineMapView.MapUtils.addKmlDocumentOverlay(map, boundary);
            }
            catch (Exception e){
                boundary=null;
                getUXToolkit().showToast("Ø¨Ù„Ø§Ú© Ø¨Ø§Ø¤Ù†ÚˆØ±ÛŒ Ø¯Ø±Ø³Øª Ù†ÛÛŒÚºØŒ ÛÛŒÚˆÚ©ÙˆØ§Ø±Ù¹Ø± Ø³Û’ Ø±Ø§Ø¨Ø·Û Ú©Ø±ÛŒÚº");
            }
        }

        distancePoints = new ArrayList<>(2);
        distanceLine = new Polyline(map);
        distanceLine.getOutlinePaint().setStrokeWidth(2);
        distanceLine.getOutlinePaint().setColor(Color.parseColor("#993322DD"));
        map.getOverlays().add(distanceLine);

        accuracyCircle = new Polygon(map);
        accuracyCircle.getFillPaint().setColor(Color.parseColor("#663322DD"));
        accuracyCircle.getOutlinePaint().setColor(Color.parseColor("#003322DD"));
        accuracyCircle.getOutlinePaint().setStrokeWidth(2);
        accuracyCircle.setTitle("Your Position:");
        accuracyCircle.setSubDescription("Determining your current location");

        currentLocationMarker = new Marker(map);
        currentLocationMarker.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_gps_d));
        currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

        map.getOverlays().add(currentLocationMarker);
        map.getOverlays().add(accuracyCircle);

        if (!isResuming()) {
            flyToBoundary();
            MapEventsReceiver eventsReceiver = new MapEventsReceiver() {
                @Override
                public boolean singleTapConfirmedHelper(GeoPoint p) {
                    if (manualTagEnabled) {
                        manualTagLocation = p;
                        Location location = new Location("manual");
                        location.setLatitude(p.getLatitude());
                        location.setLongitude(p.getLongitude());
                        location.setAltitude(p.getAltitude());
                        location.setAccuracy(5f);
                        best = location;
                        updateCurrentLocationMarker();
                    }
                    return true;
                }

                @Override
                public boolean longPressHelper(GeoPoint p) {
                    return false;
                }
            };
            map.getOverlays().add(new MapEventsOverlay(eventsReceiver));
            countDownTimer = new CountDownTimer(startTime, interval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    countDownProgress.setProgress((int) (millisUntilFinished - interval));
                }
                @Override
                public void onFinish() {
                    countDownProgress.setProgress(0);
                    if (best == null)
                        switchLocationMode();
                    else{
                        countDownProgress.setVisibility(View.GONE);
                        updateCurrentLocationMarker();
                    }


                }
            };
            countDownTimer.start();
        } else {
            GeoPoint currentPoint = new GeoPoint(resumeModel.lat,resumeModel.lon);
            if (currentPoint == null && best!=null)
                currentPoint = new GeoPoint(best.getLatitude(),best.getLongitude());
            currentLocationMarker.setPosition(currentPoint);
            accuracyCircle.setSubDescription("<b>Lat: </b>"+String.format("%.6f",currentPoint.getLatitude())+", <b>Lon: </b>"+String.format("%.6f",currentPoint.getLongitude()));
            accuracyCircle.setPoints(Polygon.pointsAsCircle(currentPoint, resumeModel.hac));
            map.getController().setCenter(currentLocationMarker.getPosition());
            if(boundary!=null) {
                if (!LocationUtils.isPointInPolygon(currentPoint, boundary)) {
                    distancePoints.add(currentPoint);
                    GeoPoint nearest = LocationUtils.getAccurateNearestPointFromBoundary(currentPoint, boundary);
                    distancePoints.add(nearest);
                    distanceLine.setPoints(distancePoints);
                    outside_in_meters=(int) distanceLine.getDistance();
                    currentLocationMarker.setTitle(String.format("%.2f", distanceLine.getDistance() / 1000) + " KM");
                    mapTextOverlay.setText("Ø§Ø³ Ú¯Ú¾Ø± Ú©Ùˆ Ø¨Ù„Ø§Ú© Ø³Û’ " + String.format("%.2f", distanceLine.getDistance() / 1000) + " Ú©Ù„ÙˆÙ…ÛŒÙ¹Ø± Ø¯ÙˆØ± Ù†Ø´Ø§Ù† Ø²Ø¯ Ú©ÛŒØ§ Ú¯ÛŒØ§ ØªÚ¾Ø§Û”");
                } else {
                    mapTextOverlay.setText("Ø§Ø³ Ú¯Ú¾Ø± Ú©Ùˆ Ø¨Ù„Ø§Ú© Ø¨Ø§Ø¤Ù†ÚˆØ±ÛŒ Ú©Û’ Ø§Ù†Ø¯Ø± Ù†Ø´Ø§Ù† Ø²Ø¯ Ú©ÛŒØ§ Ú¯ÛŒØ§ ØªÚ¾Ø§Û”");
                }
            }else{
                try{
                    mapTextOverlay.setText("NonDigitized-Block Lat: "+String.valueOf(best.getLatitude()).substring(0,5)+", Lon:"+String.valueOf(best.getLongitude()).substring(0,5));
                }
                catch (Exception e){}
            }
        }

        map.invalidate();
    }

    public void switchLocationMode(){
        if (isResuming())
            return;

        manualTagEnabled = !manualTagEnabled;
        if (manualTagEnabled) {
            btnLocate.setImageResource(R.drawable.baseline_touch_app_24);
            mapTextOverlay.setText("Ù…Ù‚Ø±Ø±Û ÙˆÙ‚Øª Ú©Û’ Ø§Ù†Ø¯Ø± Ø¢Ù¾ Ú©Û’ Ù…ÙˆØ¬ÙˆØ¯Û Ù…Ù‚Ø§Ù… Ú©Ø§ ØªØ¹ÛŒÙ† Ù†ÛÛŒÚº Ú©Ø± Ø³Ú©Û’ØŒ\n Ø¨Ø±Ø§Û Ú©Ø±Ù… Ø§Ù¾Ù†Ø§ Ù…ÙˆØ¬ÙˆØ¯Û Ù…Ù‚Ø§Ù… Ø®ÙˆØ¯ Ù…Ù†ØªØ®Ø¨ Ú©Ø±ÛŒÚºÛ”");
            if (manualTagLocation != null) {
                Location location = new Location("manual");
                location.setLatitude(manualTagLocation.getLatitude());
                location.setLongitude(manualTagLocation.getLongitude());
                location.setAltitude(manualTagLocation.getAltitude());
                location.setAccuracy(5f);
                best = location;
                updateCurrentLocationMarker();
            }
        } else {
            btnLocate.setImageResource(R.drawable.ic_gps_d);
            updateCurrentLocationMarker();
        }
    }

    public void flyToBoundary(){
        try{
            if(boundary!=null){
                map.getController().animateTo(((KmlPlacemark) boundary.mKmlRoot.mItems.get(0)).mGeometry.getBoundingBox().getCenterWithDateLine());
            }
            else{
                mUXToolkit.showToast(" Ø¨Ù„Ø§Ú© Ø¨Ø§Ø¤Ù†ÚˆØ±ÛŒ Ø¯Ø³ØªÛŒØ§Ø¨ Ù†ÛÛŒÚº");
            }
        }
        catch (Exception e){
            getUXToolkit().showToast("Ø¨Ù„Ø§Ú© Ø¨Ø§Ø¤Ù†ÚˆØ±ÛŒ Ø¯Ø±Ø³Øª Ù†ÛÛŒÚºØŒ Ù¾Ø§Ú©Ø³ØªØ§Ù† Ø§Ø¯Ø§Ø±Û Ø´Ù…Ø§Ø±ÛŒØ§Øª ÛÛŒÚˆÚ©ÙˆØ§Ø±Ù¹Ø± Ø³Û’ Ø±Ø§Ø¨Ø·Û Ú©Ø±ÛŒÚº");
        }
    }


    public void updateCurrentLocationMarker(){
        if(!isResuming()){
            if ((best != null || manualTagLocation!=null) && currentLocationMarker != null) {
                if(countDownProgress!=null && countDownProgress.getProgress()>0){
                    countDownProgress.setVisibility(View.GONE);
                    countDownProgress.setProgress(0);

                    if(countDownTimer!=null){
                        countDownTimer.cancel();
                    }
                }
                GeoPoint currentPoint = new GeoPoint(best);
                currentLocationMarker.setPosition(currentPoint);
                accuracyCircle.setSubDescription(
                        String.format("<b>Lat: </b>%.6f, <b>Lon: </b>%.6f, <b>Accuracy: </b>%.1f meters",
                                best.getLatitude(),
                                best.getLongitude(),
                                best.getAccuracy()
                        )
                );
                map.getController().animateTo(currentLocationMarker.getPosition());
                accuracyCircle.setPoints(Polygon.pointsAsCircle(currentPoint, best.getAccuracy()));

                checkInsideOutside(currentPoint);
            }
        }
        map.invalidate();
    }

    public void checkInsideOutside(GeoPoint currentPoint){
        try{
            if(boundary!=null){
                if (!LocationUtils.isPointInPolygon(currentPoint, boundary)) {
                    distancePoints.clear();
                    distancePoints.add(currentPoint);
                    GeoPoint nearest = LocationUtils.getAccurateNearestPointFromBoundary(currentPoint, boundary);
                    distancePoints.add(nearest);
                    distanceLine.setPoints(distancePoints);
                    distanceLine.setVisible(true);
                    outside_in_meters=(int) distanceLine.getDistance();

                    String msg = "Ø¢Ù¾ Ø¨Ù„Ø§Ú© Ø³Û’ "+ String.format("%.2f", distanceLine.getDistance()/1000) +" Ú©Ù„Ùˆ Ù…ÛŒÙ¹Ø± Ø¯ÙˆØ± ÛÛŒÚºÛ”";
                    currentLocationMarker.setTitle(String.format("%.2f", distanceLine.getDistance()/1000) + " KM");
                    currentLocationMarker.setSubDescription(msg);
                    mapTextOverlay.setText(msg);
                } else {
                    distancePoints.clear();
                    distanceLine.setVisible(false);
                    mapTextOverlay.setText("Ø¢Ù¾ Ú©Ø§ Ù…ÙˆØ¬ÙˆØ¯Û Ù…Ù‚Ø§Ù… Ø¨Ù„Ø§Ú© Ø¨Ø§Ø¤Ù†ÚˆØ±ÛŒ Ú©Û’ Ø§Ù†Ø¯Ø± ÛÛ’Û”");
                    currentLocationMarker.setTitle("Ù…Ù‚Ø§Ù…");
                    currentLocationMarker.setSubDescription("lat:"+String.valueOf(best.getLatitude()).substring(0,5)+", Lon:"+String.valueOf(best.getLongitude()).substring(0,5));
                }
            }else{
                mapTextOverlay.setText("[No Block-Boundary] Lat: "+String.valueOf(currentPoint.getLatitude()).substring(0,5)+", Lon:"+String.valueOf(currentPoint.getLongitude()).substring(0,5));
            }
        }
        catch (Exception e){
            mapTextOverlay.setText("Location or Boundary Issue: "+e.getMessage());
        }
    }

    @Override
    public void mapLoadSuccess(MapView mapView, CustomMapUtils mapUtils) {
        setupMap(mapView);
    }

    @Override
    public void mapLoadFailed(String ex) {
    }

    @Override
    public void onResume(){
        super.onResume();

        dh.hideSingleError();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            helper=new GPSHelper(this, this);
            manager=helper.getManager();
            LocationSettings();
            setMap();
        }
        else{
            RequestAgain("Location");
        }

        IntentFilter filter = new IntentFilter(SyncScheduler.BROADCAST_LISTING_SYNCED);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(manager!=null){
            manager.removeUpdates(this);
        }

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
                    }
                    if(manager==null){
                        manager=helper.getManager();
                    }

                    if(helper.checkProviders()==3){
                        gps=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        net=manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(gps!=null && net!=null){
                            if(gps.getAccuracy()<net.getAccuracy()){
                                best=gps;
                            }
                            else{
                                best=net;
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



    private void RequestAgain(String perm)
    {
        dh.SingleClickDialogError(this,"ALLOW PERMISSION","Allow "+perm+" Permissions in Settings", "Goto Setting", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dh.hideSingleError();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 1000);
            }
        });
    }

    private void RequestSettingDialog(){
        dh.SingleClickDialogError(this,"Ù„ÙˆÚ©ÛŒØ´Ù† Ø¢Ù† Ù†ÛÛŒÚº ÛÛ’","Ø³ÛŒÙ¹Ù†Ú¯ Ù…ÛŒÚº Ù„ÙˆÚ©ÛŒØ´ Ø¢Ù† Ú©Ø±ÛŒÚºÛ”", "Ø³ÛŒÙ¹Ù†Ú¯ Ù…ÛŒÚº Ø¬Ø§Ø¦ÛŒÚº", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dh.hideSingleError();
                final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onGeoPointRecieved(GeoPoint geoPoint) {

    }
    @Override
    public void onLocationChanged(Location location) {
        if (isResuming())
            return;
        if(location.getAccuracy()<100) {
            best = location;
            if (manualTagLocation == null)
                updateCurrentLocationMarker();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void displayError(String e){
        dh.SingleClickDialogError(this,"LOCATION ERROR",e, "OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dh.hideSingleError();
            }
        });
    }

    public boolean isResuming(){
        return resumeModel != null;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private Section12 setLocationAttributes(Section12 h) {
        try{
            h.lat=best.getLatitude();
            h.lon=best.getLongitude();
            h.alt=best.getAltitude();
            h.provider=best.getProvider();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            h.access_time= format.format(best.getTime());
            h.hac=(double) best.getAccuracy();
            h.map_type=BuildConfig.VERSION_NAME+"A";
            h.zoom_level=(int) map.getZoomLevelDouble();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                h.vac=(double) best.getVerticalAccuracyMeters();
            }
            if (manualTagLocation!=null) {
                try {
                    h.mlat = manualTagLocation.getLatitude();
                    h.mlon = manualTagLocation.getLongitude();
                }catch (Exception e){
                    h.mlat = best.getLatitude();
                    h.mlon = best.getLongitude();
                }
            }
            else{
                h.mlat=null;
                h.mlon=null;
            }
            if (boundary!=null){
                if(!LocationUtils.isPointInPolygon(new GeoPoint(best), boundary)) {
                    h.m_outside=outside_in_meters;
                    h.is_outside=1;
                }
                else{
                    h.m_outside=0;
                    h.is_outside=0;
                }
            } else{
                h.m_outside=null;
                h.is_outside=null;
                h.r_outside="NonDigitized";
            }
        }
        catch (Exception e){}
        return h;
    }
}