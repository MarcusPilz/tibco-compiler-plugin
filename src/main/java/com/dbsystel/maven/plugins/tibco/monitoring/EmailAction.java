package com.dbsystel.maven.plugins.tibco.monitoring;

public class EmailAction extends AbstractAction {
    private String message;

    private String to;

    private String cc;

    private String subject;

    private String smtpServer;

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

    /**
     * 
     * @return
     */
    public String getTo() {
        return to;
    }

    /**
     * 
     * @param to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * 
     * @return
     */
    public String getCc() {
        return cc;
    }

    /**
     * 
     * @param cc
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * 
     * @return
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * 
     * @return
     */
    public String getSmtpServer() {
        return smtpServer;
    }

    /**
     * 
     * @param smtpServer
     */
    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

}
