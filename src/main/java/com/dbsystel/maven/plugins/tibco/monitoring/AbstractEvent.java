package com.dbsystel.maven.plugins.tibco.monitoring;

public abstract class AbstractEvent {
    private boolean restart;

    private String description;

    private Actions actions;

    /**
     * 
     * @return
     */
    public boolean getRestart() {
        return restart;
    }

    /**
     * 
     * @param restart
     */
    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    /**
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     */
    public Actions getActions() {
        return actions;
    }

    /**
     * 
     * @param actions
     */
    public void setActions(Actions actions) {
        this.actions = actions;
    }
}
