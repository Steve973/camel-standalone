package org.apache.camel.standalone.listener;

import org.apache.camel.CamelContext;
import org.apache.camel.standalone.Standalone;

public interface StandaloneListener {
    default void beforeInitialize(Standalone standalone) {
        // NOOP
    }

    default void afterInitialize(Standalone standalone) {
        // NOOP
    }

    /**
     * Callback before the CamelContext(s) is being created and started.
     *
     * @param main the main INSTANCE
     */
    default void beforeStart(Standalone main) {
        // NOOP
    }

    /**
     * Callback to configure <b>each</b> created CamelContext.
     * <p/>
     * Notice this callback will be invoked for <b>each</b> CamelContext and therefore can be invoked
     * multiple times if there is 2 or more {@link CamelContext}s being created.
     *
     * @param context the created CamelContext
     */
    default void configure(CamelContext context) {
        // NOOP
    }

    /**
     * Callback after the CamelContext(s) has been started.
     *
     * @param main the main INSTANCE
     */
    default void afterStart(Standalone main) {
        // NOOP
    }

    /**
     * Callback before the CamelContext(s) is being stopped.
     *
     * @param main the main INSTANCE
     */
    default void beforeStop(Standalone main) {
        // NOOP
    }

    /**
     * Callback after the CamelContext(s) has been stopped.
     *
     * @param main the main INSTANCE
     */
    default void afterStop(Standalone main) {
        // NOOP
    }

    default void beforeSuspend(Standalone standalone) {
        // NOOP
    }

    default void afterSuspend(Standalone standalone) {
        // NOOP
    }

    default void beforeResume(Standalone standalone) {
        // NOOP
    }

    default void afterResume(Standalone standalone) {
        // NOOP
    }

    default void beforeShutdown(Standalone standalone) {
        // NOOP
    }

    default void afterShutdown(Standalone standalone) {
        // NOOP
    }
}
