package org.apache.camel.standalone.fsm.actions;

import org.apache.camel.standalone.Standalone;
import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.model.Action;

public class ShutdownAction implements Action<Standalone> {
    @Override
    public void execute(Standalone standalone, String event, Object... args) throws RetryException {
        standalone.getListeners().forEach(standaloneListener -> standaloneListener.beforeShutdown(standalone));
        standalone.shutdown();
        standalone.getListeners().forEach(standaloneListener -> standaloneListener.afterShutdown(standalone));
    }
}
