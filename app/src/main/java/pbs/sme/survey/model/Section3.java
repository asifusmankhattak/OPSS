package pbs.sme.survey.model;

import pk.gov.pbs.database.annotations.PrimaryKey;
import pk.gov.pbs.database.annotations.SqlPrimaryKey;

public class Section3 extends FormTable {
    public Integer  id;
    @PrimaryKey
    @SqlPrimaryKey
    public Integer section;
    @PrimaryKey
    @SqlPrimaryKey
    public Integer survey_id;
    public String code;
    public Integer value;
    public Integer male;
    public Integer female;
    public Integer persons;
    public Integer wages;
    public Integer other_cash_payment;
    public Integer payment_in_kind;

    public Integer total;
    public Integer emp_cost;
    public String other;
    @Override
    public String toString() {
        return "Section3{" +
                "id=" + id +
                ", section=" + section +
                ", survey_id=" + survey_id +
                ", code='" + code + '\'' +
                ", value=" + value +
                ", male=" + male +
                ", female=" + female +
                ", persons=" + persons +
                ", wages=" + wages +
                ", other_cash_payment=" + other_cash_payment +
                ", payment_in_kind=" + payment_in_kind +
                ", total=" + total +
                ", emp_cost=" + emp_cost +
                ", other='" + other + '\'' +
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
                ", survey='" + survey + '\'' +
                ", status=" + status +
                '}';
    }



}
