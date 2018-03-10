package com.dbsystel.maven.plugins.tibco.monitoring;

import java.util.ArrayList;
import java.util.List;

public class Actions {
    private List<AlertAction> alerts;

    private List<CustomAction> customs;

    private List<EmailAction> emails;

    /**
     * 
     * @return
     */
    public List<AlertAction> getAlerts() {
        if (alerts == null) {
            alerts = new ArrayList<AlertAction>();
        }
        return alerts;
    }

    /**
     * 
     * @param alerts
     */
    public void setAlerts(List<AlertAction> alerts) {
        this.alerts = alerts;
    }

    /**
     * 
     * @return
     */
    public List<CustomAction> getCustoms() {
        if (customs == null) {
            customs = new ArrayList<CustomAction>();
        }
        return customs;
    }

    /**
     * 
     * @param customs
     */
    public void setCustoms(List<CustomAction> customs) {
        this.customs = customs;
    }

    /**
     * 
     * @return
     */
    public List<EmailAction> getEmails() {
        if (emails == null) {
            emails = new ArrayList<EmailAction>();
        }
        return emails;
    }

    /**
     * 
     * @param emails
     */
    public void setEmails(List<EmailAction> emails) {
        this.emails = emails;
    }
}
