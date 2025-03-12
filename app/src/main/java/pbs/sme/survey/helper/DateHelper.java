package pbs.sme.survey.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    public static String toDate(String informat, String tofromat, String time){
        SimpleDateFormat in = new SimpleDateFormat(informat);
        try {
            Date date = in.parse(time);
            SimpleDateFormat out = new SimpleDateFormat(tofromat, Locale.getDefault());
            time = out.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static Date toDate(String informat,  String time){
        SimpleDateFormat in = new SimpleDateFormat(informat);
        try {
            return in.parse(time);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String DateCheck(String s, String e, int b, int a){
        try{
            Date c = Calendar.getInstance().getTime();
            long sdiff = c.getTime()-DateHelper.toDate("dd MMM, yyyy",s).getTime();
            if(b==0 && sdiff<0){
                return " مقررہ تاریخ سے پہلے انٹری کی اجازت نہیں ہے فرق "+((sdiff/86400000)+1)+" دن ";
            }
            long ediff = c.getTime()-86400000-DateHelper.toDate("dd MMM, yyyy",e).getTime() ;
            if(a==0 && ediff>0){
                return " مقررہ تاریخ کے بعد انٹری کی اجازت نہیں ہے فرق "+((ediff/86400000)+1)+" دن ";
            }
        }
        catch (Exception ex){}
        return null;
    }

    public static Long TimeSpent(String s){
        try{
            Date c = Calendar.getInstance().getTime();
            return ((c.getTime()-DateHelper.toDate("yyyy-MM-dd'T'HH:mm:ss.sss",s).getTime())/1000);
        }
        catch (Exception ex){}
        return null;
    }
}
