package org.apache.camel.standalone.fsm.actions;

import org.apache.camel.standalone.Standalone;
import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.model.Action;

public class StartAction implements Action<Standalone> {
    @Override
    public void execute(Standalone standalone, String event, Object... args) throws RetryException {
        standalone.getListeners().forEach(standaloneListener -> standaloneListener.beforeStart(standalone));
        standalone.start();
        standalone.getListeners().forEach(standaloneListener -> standaloneListener.afterStart(standalone));
    }
}
