package pbs.sme.survey.DB;

import static pbs.sme.survey.activity.MyActivity.getTimeNow;
import static pk.gov.pbs.database.DatabaseUtils.getPrimaryKeyField;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import pbs.sme.survey.activity.CustomApplication;
import pbs.sme.survey.helper.StatsUtil;
import pbs.sme.survey.meta.MetaManifest;
import pbs.sme.survey.model.Alerts;
import pbs.sme.survey.model.Assignment;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Contact;
import pbs.sme.survey.model.FormTable;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.NCH;
import pbs.sme.survey.model.Settings;
import pbs.sme.survey.model.Table;
import pbs.sme.survey.model.User;
import pbs.sme.survey.model.House;
import pk.gov.pbs.database.ModelBasedDatabaseHelper;
import pk.gov.pbs.database.exceptions.UnsupportedDataType;
import pk.gov.pbs.utils.ExceptionReporter;

public class Database extends ModelBasedDatabaseHelper {
    private static final int dbVersion = 2;
    private static Database instance;
    protected Context mContext;


    private Database(Context context, User user) {
        super(context, "SME_"+user.getID() + "_" + Constants.ENV, MetaManifest.getInstance().getVersion() + dbVersion);
        mContext = context;
    }

    public synchronized static Database getInstance(Context context, User user){
        if (instance == null){
            if (user!=null && user.getID() != 0)
                instance = new Database(context.getApplicationContext(), user);
        }

        return instance;
    }

    public synchronized static Database getInstance(Context context){
        if (instance == null){
            String us = CustomApplication.getSharedPreferences().getString("user", null);
            User user = new User(us);
            if (user!=null && user.getID() != 0)
                instance = new Database(context.getApplicationContext(), user);
        }
        return instance;
    }


    public static Database getInstance(){
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for (Class<?> m : MetaManifest.getInstance().getModels()){
            try {
                createTable(m, sqLiteDatabase);
            } catch (UnsupportedDataType e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //onCreate(sqLiteDatabase);
        String dropIndexHouse="DROP INDEX IF EXISTS "+House.class.getSimpleName()+"_unique_index_blk_hhno_unique;";
        String dropIndexHousehold="DROP INDEX IF EXISTS "+Household.class.getSimpleName()+"_unique_index_blk_hhno_unique;";
        try{
            sqLiteDatabase.execSQL(dropIndexHouse);
            sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS "+House.class.getSimpleName()+"_unique_index_blk_hhno_unique on "+House.class.getSimpleName()+" (blk_desc, hno, env);");
        }
        catch (Exception e){
            Log.d("Drop_index","House="+e.getMessage());
        }

        try{
            sqLiteDatabase.execSQL(dropIndexHousehold);
            sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS "+Household.class.getSimpleName()+"_unique_index_blk_hhno_unique on "+Household.class.getSimpleName()+" (blk_desc, hno, hnno, env);");
        }
        catch (Exception e){
            Log.d("Drop_index","Household="+e.getMessage());
        }
    }

    @Override
    public Long replace(@NonNull Object model) {
        StatsUtil.updateLastEntryTimeToNow();
        return super.replace(model);
    }

    public Long LoginReplace(@NonNull Object model) {
        return super.replace(model);
    }

    @Override
    public List<Long> replace(List<?> models) {
        StatsUtil.updateLastEntryTimeToNow();
        return super.replace(models);
    }

    @Override
    public List<Long> replace(Object[] models) {
        StatsUtil.updateLastEntryTimeToNow();
        return super.replace(models);
    }

    @Override
    public List<Long> insert(Object[] models) {
        StatsUtil.updateLastEntryTimeToNow();
        return super.insert(models);
    }

    @Override
    public Long insert(@NonNull Object model) {
        StatsUtil.updateLastEntryTimeToNow();
        return super.insert(model);
    }

    @Override
    public Integer update(Object object) throws SQLException, IllegalAccessException {
        StatsUtil.updateLastEntryTimeToNow();
        return super.update(object);
    }

    public int softDelete(Table model){
        StatsUtil.updateLastEntryTimeToNow();
        int result = -1;
        model.setIs_Deleted(1);
        model.deleted_time = getTimeNow();
        model.sync_time = null;
        try {
            result = super.update(model);
        } catch (IllegalAccessException e) {
            ExceptionReporter.handle(e);
        }
        return result;
    }

    public int softDeleteForm(FormTable model){
        StatsUtil.updateLastEntryTimeToNow();
        int result = -1;
        model.is_deleted=1;
        model.deleted_time = getTimeNow();
        model.sync_time = null;
        try {
            result = super.update(model);
        } catch (IllegalAccessException e) {
            ExceptionReporter.handle(e);
        }
        return result;
    }

    public int setSynced(Table model){
        int result = -1;
        model.sync_time = getTimeNow();
        try {
            result = super.update(model);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public int setSyncedHouse(String uid){
        try {
            super.execSql("UPDATE HOUSE SET sync_time='"+ getTimeNow()+"' where house_uid='"+uid+"';");
            return 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int setSyncedHousehold(String uid){
        try {
            super.execSql("UPDATE HOUSEHOLD SET sync_time='"+ getTimeNow()+"' where hh_uid='"+uid+"';");
            return 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int setSyncedLocation(String uid){
        try {
            super.execSql("UPDATE LOCATION SET sync_time='"+ getTimeNow()+"' where house_uid='"+uid+"';");
            return 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Cursor getHouseholds(){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM "+ Household.class.getSimpleName(),
                null
        );
    }



    public Cursor getAssignments(){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM `"+ Assignment.class.getSimpleName()+"` WHERE `isDeleted` = 0",
                null
        );
    }

    public Cursor getHouses(){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM `"+ House.class.getSimpleName()+"` WHERE `is_deleted` = 0",
                null
        );
    }

    public Cursor getUsers(){
        Cursor cursor=getReadableDatabase().rawQuery(
                "SELECT * FROM "+ User.class.getSimpleName(),
                null
        );
        return cursor;
    }

    public Cursor getNCH(){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM `"+ NCH.class.getSimpleName()+"` WHERE `is_deleted` = 0",
                null
        );
    }

    public Cursor getAlerts(){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM "+ Alerts.class.getSimpleName(),
                null
        );
    }

    public Cursor getContacts(){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM "+ Contact.class.getSimpleName(),
                null
        );
    }

    public Cursor getSettings(){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM "+ Settings.class.getSimpleName(),
                null
        );
    }

   

    public Cursor getUnsyncedHouseUID() {
        Cursor c = getReadableDatabase().rawQuery (
                "SELECT DISTINCT house_uid FROM " + Household.class.getSimpleName() + " WHERE `sync_time` is NULL",
                null
        );
        if (c.getCount() > 0)
            return c;
        else {
            c.close();
            return null;
        }
    }

    public Cursor getUnsyncedHouseholds(){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM `"+ Household.class.getSimpleName()+"` WHERE `sync_time` is NULL",
                null
        );
    }

    public Cursor getUnsyncedHouseholds(String house_uid){
        return getReadableDatabase().rawQuery(
                "SELECT * FROM "+ Household.class.getSimpleName() + " WHERE house_uid = ? AND `sync_time` is NULL",
                new String[]{ house_uid }
        );
    }

    public Cursor getUnsyncedHouseholdCount(){
        return getReadableDatabase().rawQuery(
                "SELECT COUNT(*) as count FROM `"+ Household.class.getSimpleName()+"` WHERE `sync_time` is NULL",
                null
        );
    }


    public Cursor getSyncedHouseCount(){
        return getReadableDatabase().rawQuery(
                "SELECT COUNT(*) as count FROM `"+ House.class.getSimpleName()+"` WHERE `sync_time` is not NULL",
                null
        );
    }

    public void destroy(){
        instance = null;
        mContext = null;
    }

    protected final ContentValues getContentValuesExcluding(ContentValues values, String[] exclude){
        for (String field : exclude){
            if(values.get(field)==null){
                values.remove(field);
            }
        }
        return values;
    }

    public Integer updateExcluding(Object object, String[] exclude) throws SQLException, IllegalAccessException {
        Field pk = getPrimaryKeyField(object.getClass());

        if (pk == null)
            throw new SQLException("Provided object has no primary key, Can not proceed to update record");
        return getWritableDatabase().update(
                object.getClass().getSimpleName(),
                getContentValuesExcluding(getContentValuesFromModel(object),exclude),
                pk.getName() + "= ?",
                new String[]{ pk.get(object).toString() }
        );
    }

    public List<Long> replaceExcluding(Object[] models, String[] exclude) throws SQLException{
        List<Long> result = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            for (int i = 0; i < models.length; i++){
                result.add(db.replaceOrThrow(
                        models[i].getClass().getSimpleName(),
                        null,
                        getContentValuesExcluding(getContentValuesFromModel(models[i]),exclude)
                ));
            }
            db.setTransactionSuccessful();
        } catch (SQLException sqlException){
            db.endTransaction();
            throw sqlException;
        } finally {
            db.endTransaction();
        }
        return result;
    }

}
