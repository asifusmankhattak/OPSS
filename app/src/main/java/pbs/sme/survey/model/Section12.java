package pbs.sme.survey.model;
import pk.gov.pbs.database.annotations.Table;
@Table(version = 4)
public class Section12 extends FormTable {

    public Integer  id;

    public String prcode;
    public Integer survey_id;
    public Integer srEstb;

    public String title;
    public String owner;
    public Integer gender;
    public String prv;
    public String dist;
    public String teh;
    public String city;
    public String vil;
    public String respondent_name;
    public Integer respondent_designation;
    public Integer building_establishment;
    public String tel;
    public Integer phone_type;
    public String phone_code;
    public String phone_number;
    public Integer reason_no_phone;
    public String email;
    public String website;
    public Integer interview_status;
    public Integer year;
    public Integer is_registered;
    public String agency;

    public Integer maintaining_accounts;
    public Integer major_activities;
    public Integer emp_count;
    public Integer emp_cost;
    public String description_psic;
    public Integer started_year;
    public String psic;
    public Integer type_org;
    public Integer is_seasonal;
    public Integer jan;
    public Integer feb;
    public Integer mar;
    public Integer apr;
    public Integer may;
    public Integer jun;
    public Integer jul;
    public Integer aug;
    public Integer sep;
    public Integer oct;
    public Integer nov;
    public Integer dec;
    public Integer months;
    public Integer hostel_facilty;
    public String food_laundry_other;
    public Integer q501;
    public String q5021;
    public String q5022;
    public Integer q503rs;
    public Integer q503perc;
    public Double lat;
    public Double lon;
    public Double alt;
    public Double hac;
    public Double vac;
    public String provider;
    public String access_time;
    public Integer zoom_level;
    public String map_type;
    public Integer is_outside;
    public Integer m_outside;
    public String r_outside;
    public String env;
    public String vcode;
    public Double mlat;
    public Double mlon;
    public String monthly;
    public Integer survey;

    @Override
    public String toString() {
        return "Section12{" +
                "id=" + id +
                ", prcode='" + prcode + '\'' +
                ", survey_id=" + survey_id +
                ", srEstb=" + srEstb +
                ", title='" + title + '\'' +
                ", owner='" + owner + '\'' +
                ", gender=" + gender +
                ", prv='" + prv + '\'' +
                ", dist='" + dist + '\'' +
                ", teh='" + teh + '\'' +
                ", city='" + city + '\'' +
                ", vil='" + vil + '\'' +
                ", respondent_name='" + respondent_name + '\'' +
                ", respondent_designation=" + respondent_designation +
                ", building_establishment=" + building_establishment +
                ", tel='" + tel + '\'' +
                ", phone_type=" + phone_type +
                ", phone_code='" + phone_code + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", reason_no_phone=" + reason_no_phone +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", interview_status=" + interview_status +
                ", year=" + year +
                ", is_registered=" + is_registered +
                ", agency='" + agency + '\'' +
                ", maintaining_accounts=" + maintaining_accounts +
                ", major_activities=" + major_activities +
                ", emp_count=" + emp_count +
                ", emp_cost=" + emp_cost +
                ", description_psic='" + description_psic + '\'' +
                ", started_year=" + started_year +
                ", psic='" + psic + '\'' +
                ", type_org=" + type_org +
                ", is_seasonal=" + is_seasonal +
                ", jan=" + jan +
                ", feb=" + feb +
                ", mar=" + mar +
                ", apr=" + apr +
                ", may=" + may +
                ", jun=" + jun +
                ", jul=" + jul +
                ", aug=" + aug +
                ", sep=" + sep +
                ", oct=" + oct +
                ", nov=" + nov +
                ", dec=" + dec +
                ", months=" + months +
                ", hostel_facilty=" + hostel_facilty +
                ", food_laundry_other='" + food_laundry_other + '\'' +
                ", q501=" + q501 +
                ", q5021='" + q5021 + '\'' +
                ", q5022='" + q5022 + '\'' +
                ", q503rs=" + q503rs +
                ", q503perc=" + q503perc +
                ", lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                ", hac=" + hac +
                ", vac=" + vac +
                ", provider='" + provider + '\'' +
                ", access_time='" + access_time + '\'' +
                ", zoom_level=" + zoom_level +
                ", map_type='" + map_type + '\'' +
                ", is_outside=" + is_outside +
                ", m_outside=" + m_outside +
                ", r_outside='" + r_outside + '\'' +
                ", env='" + env + '\'' +
                ", vcode='" + vcode + '\'' +
                ", mlat=" + mlat +
                ", mlon=" + mlon +
                ", monthly='" + monthly + '\'' +
                ", survey=" + survey +
                ", blk_desc='" + blk_desc + '\'' +
                ", sno=" + sno +
                ", uid='" + uid + '\'' +
                ", userid=" + userid +
                ", created_time='" + created_time + '\'' +
                ", modified_time='" + modified_time + '\'' +
                ", sync_time='" + sync_time + '\'' +
                ", deleted_time='" + deleted_time + '\'' +
                ", is_deleted=" + is_deleted +
                ", integrityCheck='" + integrityCheck + '\'' +
                ", pcode='" + pcode + '\'' +
                ", sid=" + sid +
                ", time_spent=" + time_spent +
                ", rcol1=" + rcol1 +
                ", rcol2=" + rcol2 +
                ", remarks='" + remarks + '\'' +
                ", flag=" + flag +
                ", survey=" + survey +
                ", status=" + status +
                '}';
    }

    public Section12(){}

    public Section12(String blockCode, int houseNo, String title, int survey_id){
        this.blk_desc = blockCode;
        this.sno = houseNo;
        this.title = title;
       // this.emp_count=emp;
        this.survey_id=survey_id;
        generateHouseUID();
    }
}
