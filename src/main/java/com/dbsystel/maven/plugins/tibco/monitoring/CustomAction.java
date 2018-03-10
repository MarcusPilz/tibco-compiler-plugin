package com.dbsystel.maven.plugins.tibco.monitoring;

public class CustomAction extends AbstractAction {
    private String command;

    private String arguments;

    /**
     * 
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * 
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * 
     * @return
     */
    public String getArguments() {
        return arguments;
    }

    /**
     * 
     * @param arguments
     */
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }
}
