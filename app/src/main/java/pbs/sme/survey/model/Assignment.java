package pbs.sme.survey.model;

import androidx.annotation.Keep;

import pk.gov.pbs.database.annotations.Default;
import pk.gov.pbs.database.annotations.PrimaryKey;
import pk.gov.pbs.database.annotations.SqlExclude;

@Keep
public class Assignment {

    public int id;
    @PrimaryKey
    public String blk_desc;
    public String prv_desc;
    public String div_desc;
    public String dist_desc;
    public String teh_desc;
    public String qh_desc;
    public String pc_desc;
    public String mz_desc;
    //urban or rural is the value
    public String blk_type;

    public String start_date;
    public String end_date;

    public int userid;

    //flags outside
    public int allow_outside;
    public int outside_margin;

    //flags entry before after date
    public int before_date;
    public int after_date;

    // polygon geojson data
    public String boundary;
    @Default("0")
    public int isDeleted;

    public String start_list_time;
    public String end_list_time;

    public int NonDigitized;
    public int hh23;
    @SqlExclude
    public int count;

    @Default("1")
    @SqlExclude
    public String status; // 0=pending, 1=started, 2=completed

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", blk_desc='" + blk_desc + '\'' +
                ", prv_desc='" + prv_desc + '\'' +
                ", div_desc='" + div_desc + '\'' +
                ", dist_desc='" + dist_desc + '\'' +
                ", teh_desc='" + teh_desc + '\'' +
                ", qh_desc='" + qh_desc + '\'' +
                ", pc_desc='" + pc_desc + '\'' +
                ", mz_desc='" + mz_desc + '\'' +
                ", blk_type='" + blk_type + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", userid=" + userid +
                ", allow_outside=" + allow_outside +
                ", outside_margin=" + outside_margin +
                ", before_date=" + before_date +
                ", after_date=" + after_date +
                ", boundary='" + boundary + '\'' +
                ", isDeleted=" + isDeleted +
                ", hh23=" + hh23 +
                ", NonDigitized=" + NonDigitized +
                ", start_list_time='" + start_list_time + '\'' +
                ", end_list_time='" + end_list_time + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsDeleted() {
        return isDeleted;
    }


    public String getBlk_desc() {
        return blk_desc;
    }

    public void setBlk_desc(String blk_desc) {
        this.blk_desc = blk_desc;
    }



    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
