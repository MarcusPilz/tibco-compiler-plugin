package com.dbsystel.maven.plugins.tibco.common;

public class CustomAction extends AbstractAction {
    private String command;

    private String arguments;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

}
