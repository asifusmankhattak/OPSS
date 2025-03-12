package pbs.sme.survey.model;

import androidx.annotation.Keep;

@Keep
public class Result {
    private int code;
    private String msg;
    private long apiTime;
    private String rTime;
    private String sTime;

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

    public long getApiTime() {
        return apiTime;
    }

    public void setApiTime(long apiTime) {
        apiTime = apiTime;
    }

    public String getrTime() {
        return rTime;
    }

    public void setrTime(String rTime) {
        this.rTime = rTime;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }
}
