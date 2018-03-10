package com.dbsystel.maven.plugins.tibco.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;

import COM.TIBCO.hawk.console.hawkeye.AgentManager;
import COM.TIBCO.hawk.console.hawkeye.ConsoleInitializationException;
import COM.TIBCO.hawk.talon.DataElement;
import COM.TIBCO.hawk.talon.MicroAgentException;
import COM.TIBCO.hawk.talon.MicroAgentID;

public abstract class AbstractMethodAction {
    private MicroAgent microAgent;

    private String methodName;

    private List<DataElement> arguments;

    /**
     * 
     * @param microAgent the microAgent
     */
    public AbstractMethodAction(MicroAgent microAgent) {
        this.microAgent = microAgent;
    }

    /**
     * 
     * @param hawkDomain the hawkDomain
     * @param rvService	the remoteService
     * @param rvNetwork	the remoteNetwork
     * @param rvDaemon	the remoteDaemon
     * @throws ConsoleInitializationException if initialization failed
     */
    public AbstractMethodAction(String hawkDomain, String rvService, String rvNetwork, String rvDaemon)
            throws ConsoleInitializationException {
        this(new MicroAgent(hawkDomain, rvService, rvNetwork, rvDaemon));
    }

    /**
     * 
     * @param hawkDomain the hawkDomain
     * @param rvService the remoteService
     * @param rvNetwork the remoteNetwork
     * @param rvDaemon the remoteDaemon
     * @param microAgentName the name of the microAgent
     * @throws ConsoleInitializationException if initialization failed
     * @throws MicroAgentException if error occured
     */
    public AbstractMethodAction(String hawkDomain, String rvService, String rvNetwork, String rvDaemon,
            String microAgentName) throws ConsoleInitializationException, MicroAgentException {
        this(new MicroAgent(hawkDomain, rvService, rvNetwork, rvDaemon, microAgentName));
    }

    /**
     * 
     * @param microAgent the microAgent
     */
    public void setMicroAgent(MicroAgent microAgent) {
        this.microAgent = microAgent;
    }

    /**
     * 
     * @return AgentManeger
     */
    public AgentManager getAgentManager() {
        return this.microAgent.getAgentManager();
    }

    /**
     * 
     * @return MicroAgentID
     */
    public MicroAgentID getMicroAgentID() {
        return this.microAgent.getMicroAgentID();
    }

    /**
     * 
     * @param arguments commandline arguments
     */
    public void setArguments(String... arguments) {
        List<ImmutablePair<String, String>> _arguments = new ArrayList<ImmutablePair<String, String>>();

        Integer i = 0;
        for (String argument : arguments) {
            i++;
            _arguments.add(new ImmutablePair<String, String>("arg" + i.toString(), argument));
        }

        this.setArguments(_arguments);
    }

    /**
     * 
     * @return DataElement
     */
    public List<DataElement> getArguments() {
        return arguments;
    }

    /**
     * 
     * @param arguments commandline arguments
     */
    public void setArguments(List<ImmutablePair<String, String>> arguments) {
        if (arguments == null) {
            return;
        }

        this.arguments = new ArrayList<DataElement>();

        for (ImmutablePair<String, String> pair : arguments) {
            String key = pair.left;
            String value = pair.right;

            this.arguments.add(new DataElement(key, value));
        }

        setMethodName(this.getMethodName(), this.arguments);
    }

    /**
     * 
     * @return MethodName
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * 
     * @param methodName the name of method to call
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 
     * @param methodName the name of method to call
     * @param arguments the arguments
     */
    public abstract void setMethodName(String methodName, List<DataElement> arguments);
}
