package pbs.sme.survey.model;


import androidx.annotation.Keep;

import java.io.Serializable;

import pbs.sme.survey.helper.DateHelper;
import pk.gov.pbs.database.annotations.SqlExclude;

@Keep
public class Block implements Serializable {
    private String BlockCode;
    private String Status;
    private String StartDate;
    private String EndDate;
    private String UR;
    private String Address;
    private int AllowOutside;
    private int OutSideMargin;
    private int BeforeDate;
    private int AfterDate;
    private int hh23;
    private int NonDigitized;

    @SqlExclude
    private  String type;
    @SqlExclude
    private  Integer count;

    public Block() {
    }

    public Block(Assignment assignment){
        this.BlockCode = assignment.blk_desc;
        this.StartDate= DateHelper.toDate("yyyy-MM-dd'T'HH:mm:ss","dd MMM, yyyy",assignment.start_date);
        this.EndDate=DateHelper.toDate("yyyy-MM-dd'T'HH:mm:ss","dd MMM, yyyy",assignment.end_date);
        this.BeforeDate=assignment.before_date;
        this.AfterDate=assignment.after_date;
        this.AllowOutside=assignment.allow_outside;
        this.OutSideMargin=assignment.outside_margin;
        this.UR=assignment.blk_type;
        this.hh23=assignment.hh23;
        this.NonDigitized=assignment.NonDigitized;
        if(this.BlockCode.length()<=9){
            if(assignment.end_list_time!=null){
                this.Status = "Completed";
            }
            else if(assignment.start_list_time!=null){
                this.Status="Started";
            }
            else{
                this.Status="Pending";
            }
        }
        else{
            this.Status= assignment.getStatus();
        }
        this.type=assignment.blk_type;
        this.count=assignment.getCount();


        String urban="";
        if(this.UR!=null && this.UR.equalsIgnoreCase("1")){
            urban="(Rural) ";
        }
        else if(this.UR!=null && this.UR.equalsIgnoreCase("2")){
            urban="(Urban) ";
        }



        this.Address = urban +assignment.mz_desc + ", " + assignment.pc_desc + ", " + assignment.qh_desc + ", " + assignment.teh_desc + ", " + assignment.dist_desc + ", " + assignment.div_desc + ", " + assignment.prv_desc;
    }

    public String getBlockCode() {
        return BlockCode;
    }


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStartDate() {
        return StartDate;
    }


    public String getEndDate() {
        return EndDate;
    }


    public String getUR() {
        return UR;
    }


    public String getAddress() {
        return Address;
    }

    public String getAddressWithLabel(){
        return "Address: " + Address;
    }

    public int getAllowOutside() {
        return AllowOutside;
    }

    public int getOutSidemMargin() {
        return OutSideMargin;
    }

    public int getBeforeDate() {
        return BeforeDate;
    }

    public int getAfterDate() {
        return AfterDate;
    }

    public int getOutSideMargin() {
        return OutSideMargin;
    }

    public int getHh23() {
        return hh23;
    }

    public int getNonDigitized() {
        return NonDigitized;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
