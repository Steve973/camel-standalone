package org.apache.camel.standalone;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.standalone.fsm.StandaloneStateMachine;
import org.apache.camel.standalone.listener.StandaloneListener;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.statefulj.fsm.FSM;
import org.statefulj.persistence.annotations.State;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Standalone {
    private static final Logger LOGGER = LogManager.getLogger(Standalone.class);
    private static final String MAIN_CONTEXT_NAME = "main";
    protected final AtomicInteger exitCode = new AtomicInteger(ExitCode.NORMAL.value);
    private final CountDownLatch latch = new CountDownLatch(1);
    private final AtomicBoolean complete = new AtomicBoolean(false);
    protected FSM<Standalone> fsm;
    protected String workDir = ".";
    private StandaloneStateMachine ssm;
    private SimpleRegistry registry;
    private Map<String, CamelContext> contextMap;
    private ImmutablePair<String, CamelContext> activeContext;
    private Set<StandaloneListener> listeners = new HashSet<>();
    private long duration;
    private TimeUnit timeUnit;
    private boolean trace;
    @State
    private String state;

    protected Standalone() {
        LOGGER.info("Creating Camel Standalone instance");
    }

    protected void initializeStateMachine() {
        if (ssm == null) {
            LOGGER.info("Initializing Camel Standalone State Machine");
            ssm = new StandaloneStateMachine(this);
            fsm = ssm.getFsmInstance();
        } else {
            throw new IllegalStateException("State machine is already initialized");
        }
    }

    public void initialize() {
        LOGGER.info("Initializing Camel Standalone");
        this.registry = new SimpleRegistry();
        this.contextMap = new HashMap<>();
        try {
            addCamelContext(MAIN_CONTEXT_NAME, true, true);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize the main Camel Context", e);
        }
        this.duration = 0;
        this.timeUnit = TimeUnit.SECONDS;
    }

    public void start() {
        contextMap.entrySet().forEach(contextEntry -> {
            try {
                LOGGER.info("Starting context: " + contextEntry.getKey());
                contextEntry.getValue().start();
            } catch (Exception e) {
                LOGGER.warn("Problem encountered while trying to start context: " + contextEntry.getKey(), e);
            }
        });
    }

    public void suspend() {
        contextMap.entrySet().forEach(contextEntry -> {
            try {
                LOGGER.info("Suspending context: " + contextEntry.getKey());
                contextEntry.getValue().suspend();
            } catch (Exception e) {
                LOGGER.warn("Problem encountered while trying to suspend context: " + contextEntry.getKey(), e);
            }
        });
    }

    public void resume() {
        contextMap.entrySet().forEach(contextEntry -> {
            try {
                LOGGER.info("Resuming context: " + contextEntry.getKey());
                contextEntry.getValue().resume();
            } catch (Exception e) {
                LOGGER.warn("Problem encountered while trying to resume context: " + contextEntry.getKey(), e);
            }
        });
    }

    public void stop() {
        contextMap.entrySet().forEach(contextEntry -> {
            try {
                LOGGER.info("Stopping context: " + contextEntry.getKey());
                contextEntry.getValue().stop();
            } catch (Exception e) {
                LOGGER.warn("Problem encountered while trying to stop context: " + contextEntry.getKey(), e);
            }
        });
    }

    public void shutdown() {
        LOGGER.info("Shutting down Camel Standalone");
        stop();
        complete.compareAndSet(false, true);
        latch.countDown();
        LOGGER.info("Camel Standalone is shut down");
    }

    public CamelContext getMainContext() {
        return this.contextMap.get(MAIN_CONTEXT_NAME);
    }

    public ImmutableMap<String, CamelContext> getContexts() {
        return ImmutableMap.copyOf(contextMap);
    }

    public void addCamelContext(String name, boolean start, boolean setActive) throws Exception {
        if (contextMap.get(name) != null) {
            throw new IllegalArgumentException("A context named \"" + name + "\" already exists. If you want " +
                    "to replace it, remove it first.");
        }
        LOGGER.info("Adding Camel Context: " + name);
        DefaultCamelContext context = new DefaultCamelContext(registry);
        context.setName(name);
        context.setTracing(trace);
        this.contextMap.put(name, context);
        if (setActive) {
            setActiveContext(name);
        }
        if (start) {
            contextMap.get(name).start();
        }
        listeners.forEach(standaloneListener -> standaloneListener.configure(context));
    }

    public CamelContext getActiveContext() {
        return this.activeContext.getRight();
    }

    public void setActiveContext(String name) {
        LOGGER.info("Setting active context to: " + name);
        CamelContext context = contextMap.get(name);
        if (context != null) {
            this.activeContext = new ImmutablePair<>(name, context);
        }
    }

    public void removeContext(String name) throws Exception {
        CamelContext context = contextMap.get(name);
        if (context == null) {
            throw new NoSuchElementException("Context does not exist with name: " + name);
        }
        if (MAIN_CONTEXT_NAME.equals(name)) {
            throw new IllegalArgumentException("The main context cannot be deleted");
        }
        if (name.equals(activeContext.getLeft())) {
            setActiveContext(MAIN_CONTEXT_NAME);
        }
        contextMap.remove(name);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isTrace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    protected void waitUntilCompleted() {
        while (!this.complete.get()) {
            try {
                if (duration > 0) {
                    if (!latch.await(duration, timeUnit)) {
                        exitCode.compareAndSet(ExitCode.NORMAL.value, ExitCode.TIMEOUT_REACHED.value);
                        complete.set(true);
                    }
                } else {
                    latch.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public ImmutableSet<StandaloneListener> getListeners() {
        return ImmutableSet.copyOf(listeners);
    }

    public void registerListener(StandaloneListener listener) {
        this.listeners.add(listener);
    }

    public String getStatus() {
        return this.state;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    protected void configureLogging() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Layout layout = PatternLayout.createLayout(PatternLayout.SIMPLE_CONVERSION_PATTERN, null, config, null, null,
                true, false, null, null);
        Appender appender = FileAppender.createAppender(workDir + "/logs/camel-standalone.log", "false", "false", "File", "true",
                "false", "false", "4000", layout, null, "false", null, config);
        appender.start();
        config.addAppender(appender);
        AppenderRef ref = AppenderRef.createAppenderRef("File", null, null);
        AppenderRef[] refs = new AppenderRef[] {ref};
        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.INFO, "StandaloneFileLoggerConfig",
                "true", refs, null, config, null );
        loggerConfig.addAppender(appender, null, null);
        config.addLogger("StandaloneFileLoggerConfig", loggerConfig);
        ctx.updateLoggers();
    }

    public enum ExitCode {
        NORMAL(0),
        TIMEOUT_REACHED(1),
        ERROR(2);

        int value;

        ExitCode(int i) {
            this.value = i;
        }
    }
}
