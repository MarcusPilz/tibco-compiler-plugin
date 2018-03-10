package com.dbsystel.maven.plugins.tibco.deployment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jdom2.JDOMException;

import COM.TIBCO.hawk.console.hawkeye.ConsoleInitializationException;
import COM.TIBCO.hawk.talon.MicroAgentData;
import COM.TIBCO.hawk.talon.MicroAgentException;
import COM.TIBCO.hawk.talon.Subscription;

import com.dbsystel.maven.plugins.tibco.common.AbstractTibcoBwDeployMojo;
import com.dbsystel.maven.plugins.tibco.server.DefaultSubscriptionHandler;
import com.dbsystel.maven.plugins.tibco.server.MethodSubscriber;
import com.dbsystel.maven.plugins.tibco.server.MicroAgent;
import com.dbsystel.platform.dev.descriptors.tibco.ApplicationManagement;

/**
 * 
 * <p>
 * This goal starts a TIBCO BusinessWorks application deployed on a TIBCO domain.
 * </p>
 *
 */
@Mojo(name = "start-bw", defaultPhase = LifecyclePhase.DEPLOY, threadSafe = true)
public class TibcoBwStartEarMojo extends AbstractTibcoBwDeployMojo {
    protected final static String STARTING_INSTANCES_FAILED = "Some instances failed to be started.";

    protected final static String STARTING_INSTANCES = "Starting instances of the application...";

    protected final static String WAITING_FOR_INSTANCES = "Waiting for instances to be started...";

    protected final static String SUBSCRIBING_TO_HAWK_METHODS = "Subscribing to Hawk methods:";

    protected final static String ALL_INSTANCES_STARTED = "All instances successfully started.";

    protected final static String SOME_INSTANCES_NOT_STARTED = "Some instances failed to be started (or timeout was reached).";

    /**
     * Whether to wait for instances to be started or not. <br />
     * The waiting process is performed with Hawk methods subscriptions. <br />
     * <br />
     * <b>NB</b>: it is mandatory to configure Hawk properly.
     */
    @Parameter(property = "waitForRunningInstances", defaultValue = "false")
    private Boolean waitForRunningInstances;

    /**
     * Whether to fail the Maven build when timeout is reached or not.
     */
    @Parameter(property = "failWhenTimeoutReached", defaultValue = "false")
    private Boolean failWhenTimeoutReached;

    @Parameter(property = "bw.start.running.statuses.number", defaultValue = "3")
    private int numberOfRunningStatuses;

    private MicroAgent microAgent;

    /**
     * 
     * @author MarcusPilz
     *
     */
    private static class RunningInstanceSubscriptionHandler extends DefaultSubscriptionHandler {
        private Log logger;

        private MethodSubscriber methodSubscriber;

        private enum StatusHistory {
            STOPPED,
            INITIALIZING,
            RUNNING
        }

        private Map<Integer, StatusHistory> statusesHistory;

        private int numberOfRunningStatuses;

        public RunningInstanceSubscriptionHandler(Log log, MethodSubscriber methodSubscriber,
                int numberOfRunningStatuses) {
            this.logger = log;
            this.methodSubscriber = methodSubscriber;
            this.statusesHistory = new LinkedHashMap<Integer, StatusHistory>();
            if (numberOfRunningStatuses <= 0) {
                numberOfRunningStatuses = 1;
            }
            this.numberOfRunningStatuses = numberOfRunningStatuses;
        }

        protected Log getLog() {
            return logger;
        }

        @Override
        public Object getResult() {
            return result;
        }

        @Override
        public void onSubscribe() {
            String instance = methodSubscriber.getArguments().get(0).getValue() + "-"
                    + methodSubscriber.getArguments().get(1).getValue();

            Integer timeout = methodSubscriber.getInterval() * methodSubscriber.getNumberOfRetry() / 1000;
            getLog().info(
                    "Subscription to the Hawk method started for instance '" + instance + "' (timeout is set to "
                            + timeout + (timeout > 1 ? " seconds" : " second") + ").");
        }

        @Override
        public void onData(Subscription s, MicroAgentData mad) {
            super.onData(s, mad);
            String data = formatData(mad.getData());
            getLog().debug(data);

            if (data != null && data.contains("STOPPED")) {
                statusesHistory.put(statusesHistory.size(), StatusHistory.STOPPED);
            } else if (data != null && data.contains("INITIALIZING")) {
                statusesHistory.put(statusesHistory.size(), StatusHistory.INITIALIZING);
            } else if (data != null && data.contains("RUNNING")) {
                statusesHistory.put(statusesHistory.size(), StatusHistory.RUNNING);
            }

            getLog().debug(statusesHistory.toString());
            if (failedToStart()) {
                s.cancel();
                result = "STOPPED";
                decreaseReferenceCount();
            } else if (isStarted()) {
                s.cancel();
                result = "RUNNING";
                decreaseReferenceCount();
                getLog().debug("A subscription was canceled");
                getLog().debug(numberOfSubscriptions.toString());
                getLog().debug(RunningInstanceSubscriptionHandler.numberOfSubscriptions.toString());
            }
        }

        private Integer getLastKeyForValue(Map<Integer, StatusHistory> map, StatusHistory value) {
            Integer result = -1;

            for (Integer key : map.keySet()) {
                if (value.equals(map.get(key))) {
                    result = key;
                }

            }
            return result;
        }

        /**
         * 
         * @return
         */
        private boolean failedToStart() {
            if (statusesHistory.containsValue(StatusHistory.RUNNING)
                    && statusesHistory.containsValue(StatusHistory.STOPPED)) {
                Integer stoppedIndex = getLastKeyForValue(statusesHistory, StatusHistory.STOPPED);
                getLog().debug(stoppedIndex.toString());
                Integer runningIndex = getLastKeyForValue(statusesHistory, StatusHistory.RUNNING);
                getLog().debug(runningIndex.toString());
                return stoppedIndex > runningIndex;
            }
            return false;
        }

        /**
         * 
         * @return
         */
        private boolean isStarted() {
            return Collections.frequency(statusesHistory.values(), StatusHistory.RUNNING) > numberOfRunningStatuses;
        }

        @Override
        public void decreaseReferenceCount() {
            super.decreaseReferenceCount();
            getLog().debug(numberOfSubscriptions.toString());
        }

        @Override
        public void increaseReferenceCount() {
            super.increaseReferenceCount();
            getLog().debug(numberOfSubscriptions.toString());
        }

        @Override
        public void onError(Subscription s, MicroAgentException e) {
            getLog().debug("A subscription caused an error");
        }

        @Override
        public void onErrorCleared(Subscription s) {
            getLog().debug("A subscription cleared an error");
        }

        @Override
        public void onTermination(Subscription s, MicroAgentException e) {
            getLog().debug("A subscription terminated");
        }

    }

    /**
     * This methods subscribe to the Hawk method "COM.TIBCO.admin.TRA:getComponentInstanceStatus" for all
     * {@code instances} being started.
     * 
     * @param instances
     * @return a list of {@link MethodSubscriber} objects
     * @throws ConsoleInitializationException
     * @throws MicroAgentException
     */
    private List<MethodSubscriber> monitorInstances(List<ImmutablePair<String, String>> instances)
            throws ConsoleInitializationException, MicroAgentException {

        List<MethodSubscriber> result = new ArrayList<MethodSubscriber>();

        microAgent = new MicroAgent(domainName, hawkRvService, hawkRvNetwork, hawkRvDaemon, "COM.TIBCO.admin.TRA");

        getLog().info("");
        getLog().info(SUBSCRIBING_TO_HAWK_METHODS);

        for (ImmutablePair<String, String> instance : instances) {
            MethodSubscriber ms = new MethodSubscriber(microAgent);
            ms.setMethodName("getComponentInstanceStatus");
            ms.setArguments(instance.left, instance.right);
            ms.setInterval(hawkSubscribeInterval);
            ms.setNumberOfRetry(hawkSubscribeNumberOfRetry);
            ms.subscribe(new RunningInstanceSubscriptionHandler(getLog(), ms, numberOfRunningStatuses));

            result.add(ms);
        }

        return result;
    }

    /**
     * 
     * @param instances
     * @return
     * @throws ConsoleInitializationException
     * @throws MicroAgentException
     */
    private boolean doWaitForRunningInstances(List<ImmutablePair<String, String>> instances)
            throws ConsoleInitializationException, MicroAgentException {
        this.getLog().info("TibcoBwStartEarMojo::doWaitForRunningInstances()");
        List<MethodSubscriber> result = monitorInstances(instances); // start monitoring of "RUNNING" statuses for instances

        getLog().info("");
        getLog().info(WAITING_FOR_INSTANCES);

        while (RunningInstanceSubscriptionHandler.numberOfSubscriptions > 0) {
            getLog().debug(RunningInstanceSubscriptionHandler.numberOfSubscriptions.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*
             * RunningInstanceSubscriptionHandler.numberOfSubscriptions will always come down to 0. In worst case after
             * timeout.
             */
        }

        microAgent.shutdown();

        // check that all instances are in "RUNNING" state
        for (MethodSubscriber ms : result) {
            if (ms.getResult() == null || !ms.getResult().equals("RUNNING")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getInitMessage() {
        this.getLog().info("TibcoBwStartEarMojo::getInitMessage()");
        return STARTING_INSTANCES;
    }

    @Override
    public String getFailureMessage() {
        this.getLog().info("TibcoBwStartEarMojo::getFailureMessage()");
        return STARTING_INSTANCES_FAILED;
    }

    @Override
    public ArrayList<String> arguments() {
        this.getLog().info("TibcoBwStartEarMojo::arguments()");
        ArrayList<String> arguments = super.defaultArguments();
        arguments.add("-start");

        return arguments;
    }

    @Override
    public void postAction() throws MojoExecutionException {
        this.getLog().info("TibcoBwStartEarMojo::postAction()");
        try {
            if (waitForRunningInstances) {
                if (!initHawk(false)) {
                    return;
                }

                List<ImmutablePair<String, String>> instances = new ArrayList<ImmutablePair<String, String>>();

                ApplicationManagement application = new ApplicationManagement(deploymentDescriptor);
                String applicationName = application.getName();
                List<String> servicesNames = application.getInstancesNames(true);

                for (String serviceName : servicesNames) {
                    instances.add(new ImmutablePair<String, String>(applicationName, serviceName));
                }

                if (doWaitForRunningInstances(instances)) {
                    getLog().info("");
                    getLog().info(ALL_INSTANCES_STARTED);
                } else {
                    getLog().info("");
                    if (failWhenTimeoutReached) {
                        throw new MojoExecutionException(SOME_INSTANCES_NOT_STARTED);
                    } else {
                        getLog().info(SOME_INSTANCES_NOT_STARTED);
                    }
                }
            }
        } catch (ConsoleInitializationException e) {
            throw new MojoExecutionException(SOME_INSTANCES_NOT_STARTED);
        } catch (MicroAgentException e) {
            throw new MojoExecutionException(SOME_INSTANCES_NOT_STARTED);
        } catch (JDOMException e) {
            // TODO Auto-generated catch block
            throw new MojoExecutionException(SOME_INSTANCES_NOT_STARTED);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new MojoExecutionException(SOME_INSTANCES_NOT_STARTED);
        }

    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (isSkip()) {
            getLog().debug(String.format("Skipping Starting of %s:%s", project.getGroupId(), project.getArtifactId()));
        }

    }

    @Override
    public String goal() {
        return "start-bw";
    }

}
