package com.dbsystel.maven.plugins.tibco.common;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;

/**
 * A CallbackHandler implementation to supply the username and password if required when connecting to the server - if
 * these are not available the user will be prompted to supply them.
 */
public class ClientCallbackHandler implements CallbackHandler {

    private boolean promptShown = false;

    private String username;

    private char[] password;

    ClientCallbackHandler(final String username, final String password) {
        this.username = username;
        if (password != null) {
            this.password = password.toCharArray();
        }
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        // Special case for anonymous authentication to avoid prompting user for their name.
        if (callbacks.length == 1 && callbacks[0] instanceof NameCallback) {
            ((NameCallback) callbacks[0]).setName("anonymous demo user");
            return;
        }

        for (Callback current : callbacks) {
            if (current instanceof RealmCallback) {
                RealmCallback rcb = (RealmCallback) current;
                String defaultText = rcb.getDefaultText();
                rcb.setText(defaultText); // For now just use the realm suggested.

                prompt(defaultText);
            } else if (current instanceof RealmChoiceCallback) {
                throw new UnsupportedCallbackException(current, "Realm choice not currently supported.");
            } else if (current instanceof NameCallback) {
                NameCallback ncb = (NameCallback) current;
                String userName = obtainUsername("Username:");

                ncb.setName(userName);
            } else if (current instanceof PasswordCallback) {
                PasswordCallback pcb = (PasswordCallback) current;
                char[] password = obtainPassword("Password:");

                pcb.setPassword(password);
            } else {
                throw new UnsupportedCallbackException(current);
            }
        }
    }

    private void prompt(final String realm) {
        if (promptShown == false) {
            promptShown = true;
            System.out.println("Authenticating against security realm: " + realm);
        }
    }

    private String obtainUsername(final String prompt) {
        if (username == null) {
            username = System.console().readLine(prompt);
        }
        return username;
    }

    private char[] obtainPassword(final String prompt) {
        if (password == null) {
            password = System.console().readPassword(prompt);
        }

        return password;
    }

}