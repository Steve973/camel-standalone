package org.apache.camel.standalone.shell;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.standalone.StandaloneRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StandaloneShellCommands {
    private static final Logger LOGGER = LogManager.getLogger(StandaloneShellCommands.class);
    private static final String INDENT = "  ";

    private StandaloneRunner standalone;

    public StandaloneShellCommands() {
    }

    //    @CliCommand(value = "log", help = "Prints the Camel Standalone log")
    public void log() {
        try (BufferedReader r = new BufferedReader(new FileReader("logs/camel-standalone.log"))) {
            String logLine;
            while ((logLine = r.readLine()) != null) {
                System.out.println(logLine);
            }
        } catch (Exception e) {
            LOGGER.warn("Problem displaying contents of log file", e);
        }
    }

    //    @CliCommand(value = "status", help = "Prints Camel Standalone's status")
    public void status() {
        System.out.println("Camel Standalone is " + standalone.getStatus());
    }

    //    @CliCommand(value = "context-info", help = "Prints info about Camel Standalone's registered contexts")
    public void contextInfo(
//            @CliOption(key = {"name"}, help = "Name of the Camel Context")
            final String name) {
        if (name == null || name.isEmpty()) {
            printAllContextInfo();
        } else {
            System.out.println("Context info:");
            printContextInfo(2, standalone.getContexts().get(name));
        }
    }

    //    @CliCommand(value = "trace", help = "Enables or disables tracing for all registered contexts")
    public void trace(
//            @CliOption(key = {"enable"}, mandatory = true, help = "Enables (true) or disables (false) tracing")
            final boolean enable) {
        standalone.setTrace(enable);
        Map<String, CamelContext> contexts = standalone.getContexts();
        System.out.print((standalone.isTrace() ? "Enabling" : "Disabling") + " tracing for: ");
        System.out.println(contexts.keySet().stream().collect(Collectors.joining(", ")));
        contexts.forEach((name, camelContext) -> camelContext.setTracing(standalone.isTrace()));
    }

    //    @CliCommand(value = "context-add", help = "Add a Camel Context")
    public void contextAdd(
//            @CliOption(key = {"name"}, mandatory = true, help = "Name of the Camel Context to create")
            final String addName,
//            @CliOption(key = {"active"}, unspecifiedDefaultValue = "false", help = "Name of the Camel Context to create")
            final boolean active,
//            @CliOption(key = {"start"}, unspecifiedDefaultValue = "true", help = "Start context after creation")
            final boolean start) {
        try {
            standalone.addCamelContext(addName, start, active);
        } catch (Exception e) {
            System.out.println("Could not add the context: " + e.getMessage());
        }
    }

    //    @CliCommand(value = "context-remove", help = "Add or remove a Camel Context")
    public void contextRemove(
//            @CliOption(key = {"name"}, mandatory = true, help = "Name of the Camel Context to remove")
            final String removeName) {
        try {
            standalone.removeContext(removeName);
        } catch (Exception e) {
            System.out.println("Could not remove the context: " + e.getMessage());
        }
    }

    //    @CliCommand(value = "set-active-context", help = "Add or remove a Camel Context")
    public void setActiveContext(
//            @CliOption(key = {"name"}, mandatory = true, help = "Name of the Camel Context to remove")
            final String name) {
        try {
            standalone.setActiveContext(name);
        } catch (Exception e) {
            System.out.println("Could not remove the context: " + e.getMessage());
        }
    }

//    @CliCommand(value = "help", help = "List all commands usage")
//    public void obtainHelp(
//            @CliOption(key = {"", "command"}, optionContext = "disable-string-converter availableCommands",
//                    help = "Command name to provide help for")
//            final String buffer) {
//        SimpleParser parser = shell.getSimpleParser();
//        parser.obtainHelp(buffer);
//    }

//    @CliCommand(value = {"exit", "quit"}, help = "Exits the shell")
//    public ExitShellRequest quit() {
//        return ExitShellRequest.NORMAL_EXIT;
//    }

    private void printAllContextInfo() {
        System.out.println("All registered contexts:");
        standalone.getContexts().values().forEach(context -> printContextInfo(1, context));
    }

    private void printContextInfo(int indentLevel, CamelContext camelContext) {
        String status;
        if (camelContext.getStatus().isStarted()) {
            status = ServiceStatus.Started.name();
        } else if (camelContext.getStatus().isSuspended()) {
            status = ServiceStatus.Suspended.name();
        } else {
            status = ServiceStatus.Stopped.name();
        }
        printWithIndent(indentLevel, "Context (" + camelContext.getName() + "):");
        printWithIndent(indentLevel, "  Status: " + status);
        printWithIndent(indentLevel, "  Uptime: " + camelContext.getUptime());
        printWithIndent(indentLevel, "  Routes: " + camelContext.getRoutes().size());
        camelContext.getRoutes().forEach(route -> printRouteInfo(indentLevel + 2, route));
    }

    private void printRouteInfo(int indentLevel, Route route) {
        printWithIndent(indentLevel, "Route (" + route.getId() + "):");
        printWithIndent(indentLevel, "  description: " + route.getDescription());
        printWithIndent(indentLevel, "  uri: " + route.getEndpoint().getEndpointUri());
        printWithIndent(indentLevel, "  uptime: " + route.getUptime());
    }

    private void printWithIndent(int level, String message) {
        StringBuilder builder = new StringBuilder();
        IntStream.range(0, level).forEach(i -> builder.append(INDENT));
        builder.append(message);
        System.out.println(builder.toString());
    }
}
