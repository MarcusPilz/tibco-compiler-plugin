package com.dbsystel.maven.plugins.tibco.monitoring;

public class AlertAction extends AbstractAction {
    private String level;

    private String message;

    /**
     * 
     * @return
     */
    public String getLevel() {
        return level;
    }

    /**
     * 
     * @param level
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * 
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
