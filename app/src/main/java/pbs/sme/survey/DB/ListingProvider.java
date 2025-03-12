package pbs.sme.survey.DB;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.User;

public class ListingProvider extends ContentProvider {
    public static final String AUTHORITY = "pbs.iac.listing";

    private static final String PATH_ASSIGNMENTS = "ASSIGNMENTS";
    private static final String PATH_HOUSE = "HOUSE";
    private static final String PATH_USER = "USER";
    private static final String PATH_NCH = "NCH";
    private static final String PATH_ALERTS = "ALERTS";
    private static final String PATH_CONTACT = "CONTACT";
    private static final String PATH_SETTINGS = "SETTINGS";
    private static final String PATH_NBLOCKS = "NBLOCKS";
    private static final String PATH_HOUSEHOLDS = "HOUSEHOLDS";
    private static final String PATH_UNSYNCED_HOUSEHOLD = "UNSYNCED_HOUSEHOLD";
    private static final String PATH_UNSYNCED_HOUSE_UID = "UNSYNCED_HOUSE_UID";
    private static final String PATH_UNSYNCED_HOUSEHOLD_COUNT = "UNSYNCED_HOUSEHOLD_COUNT";
    private static final String PATH_LOCATIONS = "LOCATIONS";
    private static final String PATH_UNSYNCED_LOCATION = "UNSYNCED_LOCATION";
    private static final String PATH_UNSYNCED_LOCATION_COUNT = "UNSYNCED_LOCATION_COUNT";
    private static final String PATH_SET_SYNCED = "SET_SYNCED";
    private static final String PATH_SYNCED_COUNT = "SYNCED_COUNT";

    private static final int UNSYNCED_HOUSEHOLD = 1;
    private static final int UNSYNCED_HOUSEHOLD_COUNT = 2;
    private static final int UNSYNCED_LOCATION = 3;
    private static final int UNSYNCED_LOCATION_COUNT = 4;
    private static final int SYNCED_COUNT = 5;
    private static final int ASSIGNMENTS = 6;
    private static final int HOUSE = 7;
    private static final int USER = 8;
    private static final int NCH = 10;
    private static final int ALERTS = 11;
    private static final int CONTACT = 12;
    private static final int SETTINGS = 13;
    private static final int NBLOCKS = 14;
    private static final int HOUSEHOLDS = 15;
    private static final int LOCATIONS = 16;
    private static final int UNSYNCED_HOUSE_UID = 17;
    private static final int SET_SYNCED = 50;

    //MIME TYPES
    private static final String MT_ASSIGNMENT = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.assignment";
    private static final String MT_HOUSE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.house";
    private static final String MT_USER = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.user";
    private static final String MT_NCH = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.nch";
    private static final String MT_ALERTS = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.alerts";
    private static final String MT_CONTACT = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.contact";
    private static final String MT_SETTINGS = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.settings";
    private static final String MT_NBLOCKS = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.nblocks";
    private static final String MT_HOUSEHOLD = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.household";
    private static final String MT_LOCATION = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+"vnd.pbs.iac.listing.location";
    private static final String MT_COUNT = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+"vnd.pbs.iac.listing.count";
    private static final String MT_HOUSE_UID = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+"vnd.pbs.iac.listing.house_uid";

    //intended to be used by CR
    public static final Uri URI_ASSIGNMENTS = Uri.parse("content://"+AUTHORITY+"/"+ PATH_ASSIGNMENTS);
    public static final Uri URI_HOUSE =  Uri.parse("content://"+AUTHORITY+"/"+ PATH_HOUSE);
    public static final Uri URI_USER =  Uri.parse("content://"+AUTHORITY+"/"+ PATH_USER);
    public static final Uri URI_NCH = Uri.parse("content://"+AUTHORITY+"/"+ PATH_NCH);
    public static final Uri URI_ALERTS = Uri.parse("content://"+AUTHORITY+"/"+ PATH_ALERTS);
    public static final Uri URI_CONTACT = Uri.parse("content://"+AUTHORITY+"/"+ PATH_CONTACT);
    public static final Uri URI_SETTINGS = Uri.parse("content://"+AUTHORITY+"/"+ PATH_SETTINGS);
    public static final Uri URI_NBLOCKS =  Uri.parse("content://"+AUTHORITY+"/"+ PATH_NBLOCKS);
    public static final Uri URI_HOUSEHOLDS = Uri.parse("content://"+AUTHORITY+"/"+ PATH_HOUSEHOLDS);
    public static final Uri URI_UNSYNCED_HOUSEHOLD = Uri.parse("content://"+AUTHORITY+"/"+ PATH_UNSYNCED_HOUSEHOLD);
    public static final Uri URI_UNSYNCED_HOUSE_UID = Uri.parse("content://"+AUTHORITY+"/"+ PATH_UNSYNCED_HOUSE_UID);
    public static final Uri URI_UNSYNCED_HOUSEHOLD_COUNT = Uri.parse("content://"+AUTHORITY+"/"+ PATH_UNSYNCED_HOUSEHOLD_COUNT);
    public static final Uri URI_LOCATIONS = Uri.parse("content://"+AUTHORITY+"/"+ PATH_LOCATIONS);
    public static final Uri URI_UNSYNCED_LOCATION = Uri.parse("content://"+AUTHORITY+"/"+ PATH_UNSYNCED_LOCATION);
    public static final Uri URI_UNSYNCED_LOCATION_COUNT = Uri.parse("content://"+AUTHORITY+"/"+ PATH_UNSYNCED_LOCATION_COUNT);
    public static final Uri URI_SET_SYNCED = Uri.parse("content://"+AUTHORITY+"/"+ PATH_SET_SYNCED);
    public static final Uri URI_SYNCED_COUNT = Uri.parse("content://"+AUTHORITY+"/"+ PATH_SYNCED_COUNT);

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, PATH_ASSIGNMENTS, ASSIGNMENTS);
        MATCHER.addURI(AUTHORITY, PATH_HOUSE, HOUSE);
        MATCHER.addURI(AUTHORITY, PATH_USER, USER);
        MATCHER.addURI(AUTHORITY, PATH_NCH, NCH);
        MATCHER.addURI(AUTHORITY, PATH_ALERTS, ALERTS);
        MATCHER.addURI(AUTHORITY, PATH_CONTACT, CONTACT);
        MATCHER.addURI(AUTHORITY, PATH_SETTINGS, SETTINGS);
        MATCHER.addURI(AUTHORITY, PATH_NBLOCKS, NBLOCKS);
        MATCHER.addURI(AUTHORITY, PATH_UNSYNCED_HOUSEHOLD, UNSYNCED_HOUSEHOLD);
        MATCHER.addURI(AUTHORITY, PATH_UNSYNCED_HOUSE_UID, UNSYNCED_HOUSE_UID);
        MATCHER.addURI(AUTHORITY, PATH_HOUSEHOLDS, HOUSEHOLDS);
        MATCHER.addURI(AUTHORITY, PATH_UNSYNCED_HOUSEHOLD_COUNT, UNSYNCED_HOUSEHOLD_COUNT);
        MATCHER.addURI(AUTHORITY, PATH_LOCATIONS, LOCATIONS);
        MATCHER.addURI(AUTHORITY, PATH_UNSYNCED_LOCATION, UNSYNCED_LOCATION);
        MATCHER.addURI(AUTHORITY, PATH_UNSYNCED_LOCATION_COUNT, UNSYNCED_LOCATION_COUNT);
        MATCHER.addURI(AUTHORITY, PATH_SET_SYNCED, SET_SYNCED);
        MATCHER.addURI(AUTHORITY, PATH_SYNCED_COUNT, SYNCED_COUNT);
    }

    Database dbHandler = null;

    @Override
    public boolean onCreate() {
        if (Database.getInstance() == null) {
            String us = getContext().getSharedPreferences(Constants.PREF, MODE_PRIVATE).getString("user", "");
            if (!us.isEmpty()) {
                User user = new User(us);
                if (user.getID() > 0)
                    dbHandler = Database.getInstance(getContext(), user);
            }
        } else
            dbHandler = Database.getInstance();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (dbHandler == null)
            return null;

        switch (MATCHER.match(uri)){
            case ASSIGNMENTS: return dbHandler.getAssignments();
            case HOUSE: return dbHandler.getHouses();
            case USER: return dbHandler.getUsers();
            case NCH: return dbHandler.getNCH();
            case ALERTS: return dbHandler.getAlerts();
            case CONTACT: return dbHandler.getContacts();
            case SETTINGS: return dbHandler.getSettings();
            case HOUSEHOLDS:
                return dbHandler.getHouseholds();
            case UNSYNCED_HOUSEHOLD:
                if (selection == null || selection.isEmpty())
                    return dbHandler.getUnsyncedHouseholds();
                else if (selectionArgs != null && selectionArgs.length > 0 && selectionArgs[0] != null && !selectionArgs[0].isEmpty())
                    return dbHandler.getUnsyncedHouseholds(selectionArgs[0]);
            case UNSYNCED_HOUSE_UID: return dbHandler.getUnsyncedHouseUID();
            case UNSYNCED_HOUSEHOLD_COUNT: return dbHandler.getUnsyncedHouseholdCount();

            case SYNCED_COUNT: return dbHandler.getSyncedHouseCount();
            case SET_SYNCED:
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)) {
            case ASSIGNMENTS: return MT_ASSIGNMENT;
            case HOUSE: return MT_HOUSE;
            case USER: return MT_USER;
            case NCH: return MT_NCH;
            case ALERTS: return MT_ALERTS;
            case CONTACT: return MT_CONTACT;
            case SETTINGS: return MT_SETTINGS;
            case NBLOCKS: return MT_NBLOCKS;
            case HOUSEHOLDS:
            case UNSYNCED_HOUSEHOLD:
                return MT_HOUSEHOLD;
            case LOCATIONS:
            case UNSYNCED_LOCATION:
                return MT_LOCATION;
            case UNSYNCED_HOUSEHOLD_COUNT:
            case SYNCED_COUNT:
            case SET_SYNCED:
                return MT_COUNT;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        if (dbHandler == null || contentValues == null)
            return -1;
        int res = 0;
        if (MATCHER.match(uri) == SET_SYNCED) {
            if (contentValues.containsKey("House")){
                res += dbHandler.setSyncedHouse(contentValues.getAsString("House"));
            }


            if (contentValues.containsKey("Household")){
                for (String hh_uid : contentValues.getAsString("Household").split("\\|")){
                    res += dbHandler.setSyncedHousehold(hh_uid);
                }

            }

        }
        return res;
    }
}
