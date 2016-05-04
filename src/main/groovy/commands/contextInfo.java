package org.apache.camel.standalone.shell.commands;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.*;
import org.crsh.command.BaseCommand;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.IntStream;

@Usage("Prints info about Camel Standalone's registered contexts")
@Man("Prints info about Camel Standalone's registered contexts. Supply the name of a context by using the " +
        "--name=<contextName> parameter for info on a single context, or get information about all registered " +
        "contexts by using the command without any parameters.")
public class contextInfo extends BaseCommand {
    private static final String INDENT = "  ";

    @Autowired
    StandaloneRunner standalone;

    @Command
    @Named("context-info")
    public void main(@Argument String name) {
        if (name == null || name.isEmpty()) {
            printAllContextInfo();
        } else {
            out.println("Context info:");
            printContextInfo(2, standalone.getContexts().get(name));
        }
    }

    private void printAllContextInfo() {
        out.println("All registered contexts:");
        standalone.getContexts().values().forEach(context -> printContextInfo(1, context));
    }

    private void printContextInfo(int indentLevel, CamelContext camelContext) {
        String status = camelContext.getStatus().name();
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
        out.println(builder.toString());
    }
}
