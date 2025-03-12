package pbs.sme.survey.activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.bonuspack.kml.KmlPlacemark;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;


import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.BuildConfig;
import pbs.sme.survey.R;
import pbs.sme.survey.helper.DialogHelper;
import pbs.sme.survey.helper.GPSHelper;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.House;
import pk.gov.pbs.geomap.LocationUtils;
import pk.gov.pbs.geomap.utils.CustomMapListener;
import pk.gov.pbs.geomap.utils.CustomMapUtils;
import pk.gov.pbs.geomap.views.CustomOfflineMapView;

public class BlockActivity extends MyActivity implements LocationListener, CustomMapListener {
    private static final String TAG = "BlockActivity";
    final DialogHelper dh=new DialogHelper();
    LocationManager manager;
    Location gps, net, best;
    GPSHelper helper;
    Marker currentLocationMarker;
    Polygon accuracyCircle;
    CustomOfflineMapView mapOffline;
    MapView mapOnline, map;
    TextView blockCode, status, startDate, endDate, address, mapTextOverlay;
    FloatingActionButton btnLocate, btnBoundary;
    Block mBlock;
    KmlDocument boundary;
    Polyline distanceLine;
    List<GeoPoint> distancePoints;

    String boundaryGeoJson;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        setDrawer(this,"Block Information");
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        init();
    }

    private void init(){
        mBlock = (Block) getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
        boundaryGeoJson=dbHandler.queryString("SELECT boundary from Assignment where blk_desc=?",mBlock.getBlockCode());
        blockCode = findViewById(R.id.tv_block_code);
        status = findViewById(R.id.tv_status);
        startDate = findViewById(R.id.tv_start_date);
        endDate = findViewById(R.id.tv_end_date);
        address = findViewById(R.id.tv_address);

        mapTextOverlay = findViewById(R.id.map_text_overlay);
        blockCode.setText(mBlock.getBlockCode());
        status.setText(mBlock.getStatus());
        startDate.setText(String.valueOf(mBlock.getStartDate()));
        endDate.setText(String.valueOf(mBlock.getEndDate()));
        address.setText(mBlock.getAddressWithLabel());
        btnLocate = findViewById(R.id.btnLocateCurrentPosition);
        btnBoundary = findViewById(R.id.btnLocateBoundary);

        btnLocate.setOnClickListener(v -> {
            if (map != null && best != null){
                map.getController().animateTo(new GeoPoint(best.getLatitude(),best.getLongitude()));
                updateCurrentLocationMarker();
            }
        });

        btnBoundary.setOnClickListener(v -> {
            if (map != null && boundary != null)
                flyToBoundary();
        });
    }

    public void setMap(){
        if (map != null)
            return;


        mapOffline = findViewById(R.id.mapviewOffline);
        mapOnline = findViewById(R.id.mapviewOnline);
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
        showTaggedLocation();
    }

    public void continueBlock(View view) {
        if(settings.getString(Constants.ENV,"field").equalsIgnoreCase("field") && !settings.getString(Constants.ROLE,"enumerator").equalsIgnoreCase("enumerator")){
            getUXToolkit().showAlertDialogue("آپکا رول شمار کنندہ نہیں ہے","صرف شمار کنندہ ہی بلاک کی لسٹنگ شروع کرسکتا ہے. سپروائزر یا دوسرا کوئی رول اس سکرین سے آگئے نہیں جا سکتا۔");
            return;
        }

        if(boundary!=null || mBlock.getNonDigitized()==1){
            final Intent intent = new Intent(BlockActivity.this, SummaryActivity.class);
            intent.putExtra(Constants.EXTRA.IDX_BLOCK, mBlock);
            startActivity(intent);
            finish();
        }
        else{
            getUXToolkit().showAlertDialogue("بلاک کی باؤنڈری (حدود) نہیں ہے","اس بلاک کی باؤنڈری دستیاب نہیں ہے، برائے کرم پاکستان ادارہ شماریات کے جی۔ایس سیکشن یا  ایپلیکیشن ڈویلپمنٹ ٹیم سے رابطہ کریں۔ پیچھے سے باؤنڈری لگا دینے کے بعد آپکو لسٹنگ میں دوبارہ امپورٹ کرنا ہوگا تو بلاک کی باؤنڈری آ جائے گی");
        }

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

        distancePoints = new ArrayList<>(2);
        distanceLine = new Polyline(map);
        distanceLine.getOutlinePaint().setStrokeWidth(2);
        distanceLine.getOutlinePaint().setColor(Color.parseColor("#993322DD"));
        map.getOverlays().add(distanceLine);

        if(boundaryGeoJson!=null && !boundaryGeoJson.isEmpty()){
            try {
                boundary = CustomOfflineMapView.MapUtils.parseGeoJson(boundaryGeoJson);
                CustomOfflineMapView.MapUtils.addKmlDocumentOverlay(map, boundary);
            }
            catch (Exception e){
                boundary=null;
                getUXToolkit().showToast("بلاک باؤنڈری درست نہیں، پاکستان ادارہ شماریات ہیڈکوارٹر سے رابطہ کریں");
            }
        }

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

        flyToBoundary();
        updateCurrentLocationMarker();
        map.invalidate();
    }
    public void flyToBoundary(){
        try{
            if(boundary!=null){
                //map.getController().animateTo(boundary.mKmlRoot.mItems.get(0).getBoundingBox().getCenterWithDateLine());
                map.getController().animateTo(((KmlPlacemark) boundary.mKmlRoot.mItems.get(0)).mGeometry.getBoundingBox().getCenterWithDateLine());
            }
            else{
                mUXToolkit.showToast(" بلاک باؤنڈری دستیاب نہیں");
            }
        }
        catch (Exception e){
            getUXToolkit().showToast("بلاک باؤنڈری درست نہیں، پاکستان ادارہ شماریات ہیڈکوارٹر سے رابطہ کریں");
        }
    }

    public void updateCurrentLocationMarker(){
        if(best!=null && currentLocationMarker != null){
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

            map.invalidate();
        }
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

                    String msg = "آپ بلاک سے "+ String.format("%.2f", distanceLine.getDistance()/1000) +" کلو میٹر دور ہیں۔";
                    currentLocationMarker.setTitle(String.format("%.2f", distanceLine.getDistance()/1000) + " KM");
                    currentLocationMarker.setSubDescription(msg);
                    mapTextOverlay.setText(msg);
                } else {
                    distancePoints.clear();
                    distanceLine.setVisible(false);
                    mapTextOverlay.setText("آپ کا موجودہ مقام بلاک باؤنڈری کے اندر ہے۔");
                    currentLocationMarker.setTitle("مقام");
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

    //----------------- Custom Map Listener ----------------------------
    @Override
    public void mapLoadSuccess(MapView mapView, CustomMapUtils mapUtils) {
        setupMap(mapView);
    }

    @Override
    public void mapLoadFailed(String ex) {
    }

    //--------------------- Activity Events ----------------------
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


    }

    @Override
    public void onStop() {
        super.onStop();
        if(manager!=null){
            manager.removeUpdates(this);
        }

    }

    //---------------------- Location Listener ------------------
    @Override
    public void onLocationChanged(Location location) {
        if(location.getAccuracy()<100) {
            best = location;
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

    //-------------------- Location Permission ---------------
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

    private void RequestAgain(String perm) {
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
        dh.SingleClickDialogError(this,"لوکیشن آن نہیں ہے","سیٹنگ میں لوکیش آن کریں۔", "سیٹنگ میں جائیں", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dh.hideSingleError();
                final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private void displayError(String e){
        dh.SingleClickDialogError(this,"LOCATION ERROR",e, "OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dh.hideSingleError();
            }
        });
    }

    public void showTaggedLocation(){
        try{
            GeoPoint cp=null;
            List<House> houseList=dbHandler.query(House.class,"env=? and is_deleted=? and blk_desc=?",env, "0",mBlock.getBlockCode());
            for (House a:houseList) {
                Marker m = new Marker(map);
                m.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_location));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                cp = new GeoPoint(a.lat,a.lon);
                m.setTitle("House No. "+a.hno);
                m.setId(a.house_uid);
                m.setSubDescription("Street / Area: "+a.area_name);
                m.setPosition(cp);
                map.getOverlays().add(m);
            }
            if(boundary==null && cp!=null){
                map.getController().animateTo(cp);
                map.invalidate();
            }
        }
        catch (Exception e){}
    }
}