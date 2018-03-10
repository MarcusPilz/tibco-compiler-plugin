package com.dbsystel.maven.plugins.tibco.common;

public class AbstractEvent {
    private boolean restart;

    private String description;

    private Actions actions;

    public boolean getRestart() {
        return restart;
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Actions getActions() {
        return actions;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }
}
