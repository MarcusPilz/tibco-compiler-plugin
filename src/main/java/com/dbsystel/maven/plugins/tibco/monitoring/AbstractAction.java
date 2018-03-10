package com.dbsystel.maven.plugins.tibco.monitoring;

public abstract class AbstractAction {
    private String performPolicy;

    private Boolean enabled;

    /**
     * 
     * @return
     */
    public String getPerformPolicy() {
        return performPolicy;
    }

    /**
     * 
     * @param performPolicy
     */
    public void setPerformPolicy(String performPolicy) {
        this.performPolicy = performPolicy;
    }

    /**
     * 
     * @return
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 
     * @param enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
