package com.dbsystel.maven.plugins.tibco.common;

import java.util.List;

public class Events {
    private List<FailureEvent> failures;

    private List<LogEvent> events;

    public List<FailureEvent> getFailures() {
        return failures;
    }

    public void setFailures(List<FailureEvent> failures) {
        this.failures = failures;
    }

    public List<LogEvent> getLogs() {
        return events;
    }

    public void setEvents(List<LogEvent> events) {
        this.events = events;
    }
}
