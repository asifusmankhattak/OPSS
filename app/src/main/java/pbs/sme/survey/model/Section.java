package pbs.sme.survey.model;

import androidx.annotation.Keep;

@Keep
public class Section {
    private String code;
    private String name;
    private String created;
    private Integer status;
    private Class<?> activity;

    public Section(String code, String name, Integer status, String created, Class<?> c){
        this.code=code;
        this.name=name;
        this.status=status;
        this.created=created;
        this.activity=c;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCreated() {
        return created;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public Integer getStatus() {
        return status;
    }
}
