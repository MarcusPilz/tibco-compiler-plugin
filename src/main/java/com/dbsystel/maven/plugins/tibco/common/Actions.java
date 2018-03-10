package com.dbsystel.maven.plugins.tibco.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Actions {
    private List<AlertAction> alerts;

    private List<CustomAction> customs;

    private List<EmailAction> emails;

    public List<AlertAction> getAlerts() {
        if (alerts == null) {
            alerts = new ArrayList<AlertAction>();
        }
        return alerts;
    }

    public void setAlerts(List<AlertAction> alerts) {
        this.alerts = alerts;
    }

    public List<CustomAction> getCustoms() {
        if (customs == null) {
            customs = new ArrayList<CustomAction>();
        }
        return customs;
    }

    public void setCustoms(List<CustomAction> customs) {
        this.customs = customs;
    }

    public List<EmailAction> getEmails() {
        if (emails == null) {
            emails = new ArrayList<EmailAction>();
        }
        return emails;
    }

    public void setEmails(List<EmailAction> emails) {
        this.emails = emails;
    }

    public BigInteger getAction() {
        // TODO Auto-generated method stub
        return null;
    }
}
