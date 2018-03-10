package com.dbsystel.maven.plugins.tibco.monitoring;

public class FailureEvent extends AbstractEvent {
    private String failure;

    /**
     * 
     * @return
     */
    public String getFailure() {
        return failure;
    }

    /**
     * 
     * @param failure
     */
    public void setFailure(String failure) {
        this.failure = failure;
    }

    /**
     * 
     * @param failure
     */
    public void set(String failure) {
        setFailure(failure);
    }

}
