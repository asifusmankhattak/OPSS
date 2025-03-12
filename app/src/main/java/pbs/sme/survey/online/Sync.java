package pbs.sme.survey.online;

import androidx.annotation.Keep;

import java.util.List;

import pbs.sme.survey.model.Household;
@Keep
public class Sync {
    protected List<Household> list_hh;

    public List<Household> getList_hh() {
        return list_hh;
    }

    public void setList_hh(List<Household> list_hh) {
        this.list_hh = list_hh;
    }

}
