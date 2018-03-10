package com.dbsystel.maven.plugins.tibco.common;

public class AbstractAction {
    private String performPolicy;

    private Boolean enabled;

    public String getPerformPolicy() {
        return performPolicy;
    }

    public void setPerformPolicy(String performPolicy) {
        this.performPolicy = performPolicy;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
