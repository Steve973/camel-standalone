package org.apache.camel.standalone.shell;

import org.apache.camel.standalone.StandaloneRunner;

public class StandalonePromptProvider {
    private StandaloneRunner standalone;

    public String getPrompt() {
        return "standalone-shell (" + standalone.getActiveContext().getName() + ")>";
    }

    public String getProviderName() {
        return "Camel Standalone shell prompt provider";
    }

}