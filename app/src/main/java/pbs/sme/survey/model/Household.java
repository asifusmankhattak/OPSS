package pbs.sme.survey.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import pk.gov.pbs.database.annotations.SqlDataType;
import pk.gov.pbs.database.annotations.SqlExclude;
import pk.gov.pbs.database.annotations.Table;
import pk.gov.pbs.database.annotations.Unique;
@Table(version = 11)
@Keep
public class Household extends pbs.sme.survey.model.Table {
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


    @SqlDataType("[VARCHAR](100)")
    public String hh_uid;

    @Unique(index = "blk_hhno_unique")
    public Integer hhno;

    @SerializedName("head")
    @SqlDataType("VARCHAR(200)")
    public String head_father_tribe;

    @SqlDataType("VARCHAR(64)")
    public Short phone_type;

    public String phone_code;

    public String phone_number;

    public Integer reason_no_phone;

    public Integer area_acre;

    public Integer area_kanal;

    public Short land_flag;

    public Integer cow;

    public Integer camel;

    public Integer sheep;

    public Integer yak;

    public Integer horse;

    public Short animal_flag;

    public Integer chicken;

    public Integer duck;

    public Integer tractor;

    public Integer harvester;

    public Integer bulldozer;

    public Integer tubewell;

    public Short machine_flag;

    public String MNCH;

    public Short hh_flag;

    public String remarks;

    @SerializedName("nch_type")
    public Integer type;
    @SerializedName("nch_cnic")
    public String cnic;

    @Unique(index = "blk_hhno_unique")
    public String env;
    public Long time_spent;




    public String generateUUID(){
        return hh_uid = UUID.randomUUID().toString();
    }



    public void setHouse(House house){
        this.house_uid = house.house_uid;
        this.blk_desc = house.blk_desc;
        this.hno = house.hno;
        this.sid=house.sid;
        this.userid=house.userid;
        this.area_name = house.area_name;
        this.lat=house.lat;
        this.lon=house.lon;
        this.alt=house.alt;
        this.vacc=house.vacc;
        this.hacc=house.hacc;
        this.provider=house.provider;
        this.mlat=house.mlat;
        this.mlon=house.mlon;
        this.zoom=house.zoom;
        this.map_type=house.map_type;
        this.is_outside=house.is_outside;
        this.meter_outside=house.meter_outside;
        this.reason_outside=house.reason_outside;
        this.access_time=house.access_time;
        this.nchid= house.getNchid();
        this.env=house.env;
    }


    public void setNchid(Integer nchid) {
        this.nchid = nchid;
    }


}
