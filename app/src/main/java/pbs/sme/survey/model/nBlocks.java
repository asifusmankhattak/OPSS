package pbs.sme.survey.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import pk.gov.pbs.database.annotations.PrimaryKey;
@Keep
public class nBlocks {
    @PrimaryKey
    @SerializedName("bl")
    protected String id;
    protected boolean agri;

    @Override
    public String toString() {
        return "nBlocks{" +
                "id='" + id + '\'' +
                ", agri=" + agri +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAgri() {
        return agri;
    }

    public void setAgri(boolean agri) {
        this.agri = agri;
    }
}
