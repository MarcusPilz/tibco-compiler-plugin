package com.dbsystel.maven.plugins.tibco.common;

public class FailureEvent extends AbstractEvent {
    private String failure;

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }

    public void set(String failure) {
        setFailure(failure);
    }

}
