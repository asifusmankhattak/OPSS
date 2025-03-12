package pbs.sme.survey.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import pk.gov.pbs.database.annotations.SqlDataType;
import pk.gov.pbs.database.annotations.SqlExclude;
import pk.gov.pbs.database.annotations.Unique;
@Keep
public class House extends Table {
    @SqlDataType("[VARCHAR](100)")
    public String house_uid;

    @SqlDataType("[VARCHAR](10)")
    @Unique(index = "blk_hhno_unique")
    public String blk_desc;

    @Unique(index = "blk_hhno_unique")
    public Integer hno;

    @SqlDataType("VARCHAR(200)")
    public String area_name;


    public Double lat;
    public Double lon;
    public Double alt;
    @SerializedName("hac")
    public Float hacc;
    @SerializedName("vac")
    public Float vacc;

    @SerializedName("acess_time")
    public String access_time;
    public Double mlat;
    public Double mlon;
    public Integer zoom;
    public String provider;
    public String map_type;
    public Integer is_outside;
    @SerializedName("m_outside")
    public Integer meter_outside;
    @SerializedName("r_outside")
    public String reason_outside;

    @SqlExclude
    private Integer HHs;


    @SerializedName("nch_id")
    @SqlDataType("Integer")
    public Integer nchid;

    @Unique(index = "blk_hhno_unique")
    public String env;

    public House(){
    }

    public House(String blockCode, int houseNo, String address){
        blk_desc = blockCode;
        hno = houseNo;
        area_name = address;
        generateHouseUID();
    }
    public Integer getHHs() {
        return HHs;
    }

    public Integer getNchid() {
        return nchid;
    }

    public void setNchid(Integer nchid) {
        this.nchid = nchid;
    }

    public void setHHs(Integer HHs) {
        this.HHs = HHs;
    }

    public String generateHouseUID(){
        return house_uid = UUID.randomUUID().toString();
    }


}
