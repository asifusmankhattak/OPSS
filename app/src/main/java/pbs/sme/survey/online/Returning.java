package pbs.sme.survey.online;

import androidx.annotation.Keep;

@Keep
public class Returning {
    protected int code;
    protected String msg;
    //protected String[] synced;
    protected int location;

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


    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
