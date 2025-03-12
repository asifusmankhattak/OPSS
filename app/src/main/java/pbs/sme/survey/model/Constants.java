package pbs.sme.survey.model;


import pbs.sme.survey.activity.S1Activity;
import pbs.sme.survey.activity.S2Activity;
import pbs.sme.survey.activity.S3Activity;
import pbs.sme.survey.activity.S4Activity;
import pbs.sme.survey.activity.S5Activity;
import pbs.sme.survey.activity.S6Activity;
import pbs.sme.survey.activity.S7Activity;
import pbs.sme.survey.activity.S8Activity;
import pbs.sme.survey.activity.S9Activity;

public class Constants {
     public static final String VTYPE_DEF = "MT";
     public static final String ENV = "dev";
     public static final int APP_ID=1;
     public static final String THEME="THEME";
     public static final String PREF="MYPREF";
     public static final String LAST_IMPORT="LAST_IMPORT";
     public static final String LAST_ENTRY="LAST_ENTRY";
     public static final String LAST_SYNC="LAST_SYNC";
     public static final int PERMISSION_ALL_CODE=111;
     public static final int PERMISSION_LOCATION_CODE=990;
     public static final int PERMISSION_PHONE_CODE=880;
     public static final int PERMISSION_STORAGE_CODE=770;

     public static final String IS_LOGGED="IS_LOGGED";
     public static final String SHARED_DIRECTORY="/EXPORT/IACL";
     public static final String MAP_DIRECTORY="EXPORT/IAC/MAP";
     public static final String MAP_FILE_NAME = "my_map";
     public static final String MAP_FILE_EXTENSION = ".sqlite";
     public static final String UID="UID";
     public static final String SID="SID";
     public static final String ROLE="ROLE";
     public static final String USERNAME="USERNAME";
     public static final String ENUMERATOR="ENUMERATOR";
     public static final String LAST_LOGIN="LAST_LOGIN";
     public static final String SMS_CODE="SMS_CODE";
     public static final String SMS_EXPIRY="SMS_EXPIRY";

     public static final String SYNC_SERVICE_NOTIFICATION_CHANNEL_ID = "IAC_Sync_Notifications";
     public static final String SYNC_SERVICE_NOTIFICATION_CHANNEL_Name = "IAC Sync Notifications";
     public static final String HTTP_CALL_COUNT = "httpCount";
     public static final String HTTP_SUCCESS_COUNT = "httpSuccessCallCount";
     public static final String HTTP_FAILURE_COUNT = "httpFailedCallCount";
     public static final String HTTP_CALL_TIME = "httpCallTime";
     public static final String HTTP_CALL_SUCCESS_TIME = "successfulHttpCallTime";
     public static final String HTTP_CALL_FAILURE_TIME = "failedHttpCallTime";

     public static boolean isDevEnv(){
          return Constants.ENV == "dev" && pk.gov.pbs.utils.Constants.DEBUG_MODE;
     }

     public static class EXTRA {
          public static final String IDX_BLOCK = "model.assignment";
          public static final String IDX_HOUSE = "model.house";
          public static final String IDX_TITLE = "IDX_TITLE";
          public static final String IDX_ID = "IDX_ID";
          public static final String IDX_EMP = "IDX_EMP";

     }
     public static final String[] SECTION_CODES={"1","2","3","4","5","6","7","8","9","BL"};
     public static final String[] SECTION_NAMES={
             "Section-1: Basic Info",
             "Section-2: Economic Activity",
             "Section-3: Emplpyment and Employment Cose",
             "Section-4: Inputs",
             "Section-5:Digital Intermediary Platforms(DIP)",
             "Section-6:Taxes ",
             "Section-7: Income",
             "Section-8: Gross Fixed Captial",
             "Section-9: Value of Inventories",
             "Baseline"};

     public static final Class<?>[] FORM_ACTIVITIES={
             S1Activity.class,
             S2Activity.class,
             S3Activity.class,
             S4Activity.class,
             S5Activity.class,
             S6Activity.class,
             S7Activity.class,
             S8Activity.class,
             S9Activity.class,
             Baseline.class
     };
}
