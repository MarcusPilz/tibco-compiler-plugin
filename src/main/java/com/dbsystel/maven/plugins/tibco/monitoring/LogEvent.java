package com.dbsystel.maven.plugins.tibco.monitoring;

public class LogEvent extends AbstractEvent {
    private String match;

    /**
     * 
     * @return
     */
    public String getMatch() {
        return match;
    }

    /**
     * 
     * @param match
     */
    public void setMatch(String match) {
        this.match = match;
    }

}
