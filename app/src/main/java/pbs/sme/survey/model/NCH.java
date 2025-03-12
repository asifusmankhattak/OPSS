package pbs.sme.survey.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import pk.gov.pbs.database.annotations.PrimaryKey;
@Keep
public class NCH
{
    protected String env;
    protected String start_date;
    protected String end_date;
    protected String area;
    @PrimaryKey
    @SerializedName("nchid")
    protected int id;
    protected String blk_desc;
    protected int userid;
    protected long sid;
    protected String area_name;
    protected int house_uid;
    protected int hno;

    protected int count_hh;
    protected int hh_uid;
    protected int hhno;
    protected String cnic;
    protected int type;
    protected String head;
    protected short phone_type;
    protected String phone_code;
    protected String phone_number;
    protected int area_acre;
    protected int area_kanal;
    protected int land_flag;
    protected int cow;
    protected int camel;
    protected int sheep;
    protected int yak;
    protected int horse;
    protected int animal_flag;
    protected int chicken;
    protected int duck;
    protected int tractor;
    protected int harvester;
    protected int bulldozer;
    protected int tubewell;
    protected int machine_flag;
    protected String MNCH;
    protected int hh_flag;
    protected String remarks;
    protected String created_time;
    protected String modified_time;
    protected String sync_time;
    protected String deleted_time;
    protected boolean is_deleted;

    @Override
    public String toString() {
        return "NCH{" +
                "env='" + env + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", area='" + area + '\'' +
                ", id=" + id +
                ", blk_desc='" + blk_desc + '\'' +
                ", userid=" + userid +
                ", sid=" + sid +
                ", area_name='" + area_name + '\'' +
                ", house_uid=" + house_uid +
                ", hno=" + hno +
                ", count_hh=" + count_hh +
                ", hh_uid=" + hh_uid +
                ", hhno=" + hhno +
                ", cnic='" + cnic + '\'' +
                ", type='" + type + '\'' +
                ", head='" + head + '\'' +
                ", phone_type=" + phone_type +
                ", phone_code='" + phone_code + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", area_acre=" + area_acre +
                ", area_kanal=" + area_kanal +
                ", land_flag=" + land_flag +
                ", cow=" + cow +
                ", camel=" + camel +
                ", sheep=" + sheep +
                ", yak=" + yak +
                ", horse=" + horse +
                ", animal_flag=" + animal_flag +
                ", chicken=" + chicken +
                ", duck=" + duck +
                ", tractor=" + tractor +
                ", harvester=" + harvester +
                ", bulldozer=" + bulldozer +
                ", tubewell=" + tubewell +
                ", machine_flag=" + machine_flag +
                ", MNCH='" + MNCH + '\'' +
                ", hh_flag=" + hh_flag +
                ", remarks='" + remarks + '\'' +
                ", created_time='" + created_time + '\'' +
                ", modified_time='" + modified_time + '\'' +
                ", sync_time='" + sync_time + '\'' +
                ", deleted_time='" + deleted_time + '\'' +
                ", is_deleted=" + is_deleted +
                '}';
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBlk_desc() {
        return blk_desc;
    }

    public void setBlk_desc(String blk_desc) {
        this.blk_desc = blk_desc;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public int getHouse_uid() {
        return house_uid;
    }

    public void setHouse_uid(int house_uid) {
        this.house_uid = house_uid;
    }

    public int getHno() {
        return hno;
    }

    public void setHno(int hno) {
        this.hno = hno;
    }

    public int getCount_hh() {
        return count_hh;
    }

    public void setCount_hh(int count_hh) {
        this.count_hh = count_hh;
    }

    public int getHh_uid() {
        return hh_uid;
    }

    public void setHh_uid(int hh_uid) {
        this.hh_uid = hh_uid;
    }

    public int getHhno() {
        return hhno;
    }

    public void setHhno(int hhno) {
        this.hhno = hhno;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Short getPhone_type() {
        return phone_type;
    }

    public void setPhone_type(short phone_type) {
        this.phone_type = phone_type;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getArea_acre() {
        return area_acre;
    }

    public void setArea_acre(int area_acre) {
        this.area_acre = area_acre;
    }

    public int getArea_kanal() {
        return area_kanal;
    }

    public void setArea_kanal(int area_kanal) {
        this.area_kanal = area_kanal;
    }

    public int getLand_flag() {
        return land_flag;
    }

    public void setLand_flag(int land_flag) {
        this.land_flag = land_flag;
    }

    public int getCow() {
        return cow;
    }

    public void setCow(int cow) {
        this.cow = cow;
    }

    public int getCamel() {
        return camel;
    }

    public void setCamel(int camel) {
        this.camel = camel;
    }

    public int getSheep() {
        return sheep;
    }

    public void setSheep(int sheep) {
        this.sheep = sheep;
    }

    public int getYak() {
        return yak;
    }

    public void setYak(int yak) {
        this.yak = yak;
    }

    public int getHorse() {
        return horse;
    }

    public void setHorse(int horse) {
        this.horse = horse;
    }

    public int getAnimal_flag() {
        return animal_flag;
    }

    public void setAnimal_flag(int animal_flag) {
        this.animal_flag = animal_flag;
    }

    public int getChicken() {
        return chicken;
    }

    public void setChicken(int chicken) {
        this.chicken = chicken;
    }

    public int getDuck() {
        return duck;
    }

    public void setDuck(int duck) {
        this.duck = duck;
    }

    public int getTractor() {
        return tractor;
    }

    public void setTractor(int tractor) {
        this.tractor = tractor;
    }

    public int getHarvester() {
        return harvester;
    }

    public void setHarvester(int harvester) {
        this.harvester = harvester;
    }

    public int getBulldozer() {
        return bulldozer;
    }

    public void setBulldozer(int bulldozer) {
        this.bulldozer = bulldozer;
    }

    public int getTubewell() {
        return tubewell;
    }

    public void setTubewell(int tubewell) {
        this.tubewell = tubewell;
    }

    public int getMachine_flag() {
        return machine_flag;
    }

    public void setMachine_flag(int machine_flag) {
        this.machine_flag = machine_flag;
    }

    public String getMNCH() {
        return MNCH;
    }

    public void setMNCH(String MNCH) {
        this.MNCH = MNCH;
    }

    public int getHh_flag() {
        return hh_flag;
    }

    public void setHh_flag(int hh_flag) {
        this.hh_flag = hh_flag;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getModified_time() {
        return modified_time;
    }

    public void setModified_time(String modified_time) {
        this.modified_time = modified_time;
    }

    public String getSync_time() {
        return sync_time;
    }

    public void setSync_time(String sync_time) {
        this.sync_time = sync_time;
    }

    public String getDeleted_time() {
        return deleted_time;
    }

    public void setDeleted_time(String deleted_time) {
        this.deleted_time = deleted_time;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
}
