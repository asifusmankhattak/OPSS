package pbs.sme.survey.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;

import pk.gov.pbs.database.annotations.PrimaryKey;
import pk.gov.pbs.database.annotations.SqlExclude;
import pk.gov.pbs.utils.ExceptionReporter;
@Keep
public class User implements Serializable {

    @SerializedName("sid")
    protected int sid;

    @SqlExclude
    @SerializedName("code")
    protected int code;

    @SqlExclude
    @SerializedName("msg")
    protected String msg;

    @PrimaryKey
    @SerializedName("id")
    protected int ID;

    @SerializedName("cnic")
    protected String CNIC;

    @SerializedName("pno")
    protected int PNO;
    @SerializedName("name")
    protected String NAME;
    @SerializedName("gender")
    protected String GENDER;
    @SerializedName("designation")
    protected String DESIGNATION;
    @SerializedName("email")
    protected String EMAIL;
    @SerializedName("mobile")
    protected String MOBILE;
    @SerializedName("whatsapp")
    protected String WHATSAPP;
    @SerializedName("of_id")
    protected String FO_ID;
    @SerializedName("fo_office")
    protected String FO_OFFICE;

    @SerializedName("updated")
    protected String UPDATED;

    @SerializedName("status")
    protected int STATUS;

    @SerializedName("role")
    protected String ROLE;


    @SerializedName("token")
    protected String TOKEN;

    public User(HashMap<String,String> values){
        for (String col : values.keySet()) {
            try {
                Field f = getClass().getDeclaredField(col);
                switch (f.getType().getSimpleName()) {
                    case "int":
                    case "Integer":
                        f.set(this, Integer.parseInt(values.get(col)));
                        break;
                    default:
                        f.set(this, values.get(col));
                }
            } catch (Exception e) {
                ExceptionReporter.handle(e);
            }
        }
    }

    public User(){}

    public User(String userStr){
        if (userStr!=null&&!userStr.isEmpty()) {
            for (String params : userStr.split("&")) {
                if (params.isEmpty() || params.length() == 0)
                    continue;
                String[] p = params.split("=");
                try {
                    Field f = getClass().getDeclaredField(p[0]);
                    switch (f.getType().getSimpleName()) {
                        case "int":
                        case "Integer":
                            f.set(this, Integer.parseInt(p[1]));
                            break;
                        default:
                            if (p.length == 2)
                                f.set(this, p[1]);
                    }
                } catch (Exception e) {
                    ExceptionReporter.handle(e);
                }
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Field f : getClass().getDeclaredFields()) {
            try {
                sb.append(f.getName()).append("=").append(f.get(this)).append("&");
            } catch (Exception e) {
                ExceptionReporter.handle(e);
            }
        }
        return sb.toString();
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCNIC() {
        return CNIC;
    }

    public void setCNIC(String CNIC) {
        this.CNIC = CNIC;
    }

    public int getPNO() {
        return PNO;
    }

    public void setPNO(int PNO) {
        this.PNO = PNO;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getGENDER() {
        return GENDER;
    }

    public void setGENDER(String GENDER) {
        this.GENDER = GENDER;
    }

    public String getDESIGNATION() {
        return DESIGNATION;
    }

    public void setDESIGNATION(String DESIGNATION) {
        this.DESIGNATION = DESIGNATION;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public void setMOBILE(String MOBILE) {
        this.MOBILE = MOBILE;
    }

    public String getWHATSAPP() {
        return WHATSAPP;
    }

    public void setWHATSAPP(String WHATSAPP) {
        this.WHATSAPP = WHATSAPP;
    }

    public String getFO_ID() {
        return FO_ID;
    }

    public void setFO_ID(String FO_ID) {
        this.FO_ID = FO_ID;
    }

    public String getFO_OFFICE() {
        return FO_OFFICE;
    }

    public void setFO_OFFICE(String FO_OFFICE) {
        this.FO_OFFICE = FO_OFFICE;
    }


    public String getUPDATED() {
        return UPDATED;
    }

    public void setUPDATED(String UPDATED) {
        this.UPDATED = UPDATED;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public String getROLE() {
        return ROLE;
    }

    public void setROLE(String ROLE) {
        this.ROLE = ROLE;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }
}
