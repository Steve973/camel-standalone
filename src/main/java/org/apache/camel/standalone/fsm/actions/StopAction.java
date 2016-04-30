package org.apache.camel.standalone.fsm.actions;

import org.apache.camel.standalone.Standalone;
import org.statefulj.fsm.RetryException;
import org.statefulj.fsm.model.Action;

public class StopAction implements Action<Standalone> {

    @Override
    public void execute(Standalone standalone, String event, Object... args) throws RetryException {
        standalone.getListeners().forEach(standaloneListener -> standaloneListener.beforeStop(standalone));
        standalone.stop();
        standalone.getListeners().forEach(standaloneListener -> standaloneListener.afterStop(standalone));
    }
}
