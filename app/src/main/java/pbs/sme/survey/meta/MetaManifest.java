package pbs.sme.survey.meta;

import pbs.sme.survey.model.Alerts;
import pbs.sme.survey.model.Assignment;
import pbs.sme.survey.model.Baseline;
import pbs.sme.survey.model.Contact;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.NCH;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.model.Section3;
import pbs.sme.survey.model.Section47;
import pbs.sme.survey.model.Section6;
import pbs.sme.survey.model.Section8;
import pbs.sme.survey.model.Section9;
import pbs.sme.survey.model.Settings;
import pbs.sme.survey.model.User;
import pbs.sme.survey.model.House;
import pk.gov.pbs.database.annotations.Table;

public class MetaManifest {
    private static MetaManifest instance;
    private static final int mVersion = 3;
    private static final Class<?>[] models = new Class[]{

            House.class,  Household.class, Section12.class, Section3.class, Section47.class, Baseline.class,
            Assignment.class, User.class, NCH.class,
            Alerts.class, Contact.class, Settings.class,
             Section6.class, Section9.class, Section8.class,
    };

    private MetaManifest(){}

    public synchronized static MetaManifest getInstance(){
        if (instance == null)
            instance = new MetaManifest();
        return instance;
    }
    public Class<?>[] getModels() {
        return models;
    }

    public int getVersion(){
        int sumTableVersion = 0;
        for (Class<?> c : getModels()) {
            Table table = c.getAnnotation(Table.class);
            if (table != null)
                sumTableVersion += table.version();
            else
                sumTableVersion++;
        }
        return mVersion + sumTableVersion;
    }
}
