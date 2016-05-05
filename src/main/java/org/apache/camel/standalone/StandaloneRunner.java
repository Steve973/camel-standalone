package org.apache.camel.standalone;

import org.apache.camel.CamelContext;
import org.apache.camel.standalone.cli.StandaloneCommandLineInterface;
import org.apache.camel.standalone.config.StandaloneConfig;
import org.apache.camel.standalone.fsm.StandaloneStateMachine;
import org.apache.camel.standalone.listener.StandaloneListener;
import org.apache.camel.standalone.routes.JarDropInRouteBuilder;
import org.apache.camel.standalone.routes.XmlDropInRouteBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.crsh.plugin.CRaSHPlugin;
import org.crsh.spring.SpringBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StandaloneRunner extends Standalone implements StandaloneListener {
    private static final Logger LOGGER = LogManager.getLogger(StandaloneRunner.class);
    private static final StandaloneRunner instance = new StandaloneRunner();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String workDir = ".";

    private StandaloneRunner() {
        LOGGER.info("Creating standalone runner");
    }

    public static StandaloneRunner getInstance() {
        return instance;
    }

    public static void main(String... args) throws Exception {
        instance.initializeStateMachine();
        new StandaloneCommandLineInterface().processCommandLineArgs(args);
        instance.configureLogging();
        instance.registerListener(instance);
        instance.run();
        instance.fsm.onEvent(instance, StandaloneStateMachine.shutdownEvent);
    }

    private void configureContextDropIn(CamelContext context) throws Exception {
        File xmlRouteDir = new File(workDir + "/routes/xml");
        File jarRouteDir = new File(workDir + "/routes/jar");
        new XmlDropInRouteBuilder(xmlRouteDir.getAbsolutePath(), context)
                .addRoutesToCamelContext(context);
        new JarDropInRouteBuilder(jarRouteDir.getAbsolutePath(), context)
                .addRoutesToCamelContext(context);
    }

    @SuppressWarnings("finally")
    public void run() {
        Runnable standaloneTask = () -> {
            try {
                instance.fsm.onEvent(instance, StandaloneStateMachine.initializeEvent);
                instance.fsm.onEvent(instance, StandaloneStateMachine.startEvent);
                instance.waitUntilCompleted();
            } catch (Exception e) {
                LOGGER.warn("Problem encountered while running standalone", e);
                instance.exitCode.set(ExitCode.ERROR.value);
            } finally {
                System.exit(instance.exitCode.get());
            }
        };
        executor.execute(standaloneTask);
    }

    @Override
    public void configure(CamelContext context) {
        try {
            configureContextDropIn(context);
        } catch (Exception e) {
            LOGGER.warn("Problem encountered while configuring context: " + context.getName(), e);
        }
    }
}
