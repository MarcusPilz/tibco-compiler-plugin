package com.dbsystel.maven.plugins.tibco.common;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * TODO : REMOVE iF USED
 * @author MarcusPilz
 *
 */
public class SortedProperties extends Properties {
    private static Logger log = Logger.getLogger(SortedProperties.class);

    /**
     * 
     */
    private static final long serialVersionUID = 5083469595051318533L;

    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
    }

    @Override
    public synchronized Object setProperty(String key, String value) {
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getName() + " setProperty key:" + key + " value: " + value);
        }
        if (value != null) {
            return super.setProperty(key, value);
        }
        return null;
    }

    /**
     * 
     * @param key a key
     * @param value a BigInteger
     * @return an Object
     */
    public synchronized Object setProperty(String key, BigInteger value) {
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getName() + " setProperty key:" + key + " value: " + value.toString());
        }
        if (value != null) {
            return super.setProperty(key, value.toString());
        }
        return null;
    }

    /**
     * 
     * @param key a key
     * @param value the value correnpon dto key
     * @return an Object
     */
    public synchronized Object setProperty(String key, Boolean value) {
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getName() + " setProperty key:" + key + " value: " + value.toString());
        }
        if (value != null) {
            return super.setProperty(key, value.toString());
        }
        return null;
    }

}
