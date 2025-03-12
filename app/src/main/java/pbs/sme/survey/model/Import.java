package pbs.sme.survey.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;
@Keep
public class Import {
    @SerializedName("iac_listing")
    private List<Assignment> assignments;
    private List<Contact> contacts;
    private List<Alerts> alerts;
    @SerializedName("NCHList")
    private List<NCH> nchList;

    @SerializedName("NCHBlockList")
    private List<nBlocks> nblocks;

    @SerializedName("listing_disaster")
    private List<Household> households;

    private User profile;
    private List<Settings> settings;


    @Override
    public String toString() {
        return "Import{" +
                "assignments=" + assignments +
                ", contacts=" + contacts +
                ", alerts=" + alerts +
                ", nchList=" + nchList +
                ", households=" + households +
                ", profile=" + profile +
                ", settings=" + settings +
                ", nblocks=" + nblocks +
                '}';
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<Alerts> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alerts> alerts) {
        this.alerts = alerts;
    }

    public List<NCH> getNchList() {
        return nchList;
    }

    public void setNchList(List<NCH> nchList) {
        this.nchList = nchList;
    }

    public List<Household> getHouseholds() {
        return households;
    }

    public void setHouseholds(List<Household> households) {
        this.households = households;
    }



    public User getProfile() {
        return profile;
    }

    public void setProfile(User profile) {
        this.profile = profile;
    }

    public List<Settings> getSettings() {
        return settings;
    }

    public void setSettings(List<Settings> settings) {
        this.settings = settings;
    }

    public List<nBlocks> getNblocks() {
        return nblocks;
    }

    public void setNblocks(List<nBlocks> nblocks) {
        this.nblocks = nblocks;
    }
}
