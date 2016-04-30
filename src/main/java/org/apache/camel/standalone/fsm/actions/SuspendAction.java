package org.apache.camel.standalone.fsm.actions;

import org.apache.camel.standalone.Standalone;
import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.model.Action;

public class SuspendAction implements Action<Standalone> {
    @Override
    public void execute(Standalone standalone, String event, Object... args) throws RetryException {
        standalone.getListeners().forEach(standaloneListener -> standaloneListener.beforeSuspend(standalone));
        standalone.suspend();
        standalone.getListeners().forEach(standaloneListener -> standaloneListener.afterSuspend(standalone));
    }
}
