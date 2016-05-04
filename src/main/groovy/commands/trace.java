package org.apache.camel.standalone.shell.commands;

import org.apache.camel.CamelContext;
import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.*;
import org.crsh.command.BaseCommand;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

@Usage("Enables or disables tracing for all registered contexts")
@Man("Enables or disables tracing for all registered contexts.  Use the parameter --enable=<true|false> to " +
        "enable or disable tracing.")
public class trace extends BaseCommand {
    @Autowired
    StandaloneRunner standalone;

    @Command
    public void main(@Required @Argument Boolean enable) {
        Map<String, CamelContext> contexts = standalone.getContexts();
        out.print((enable ? "Enabling" : "Disabling") + " tracing for: ");
        out.println(contexts.keySet().stream().collect(Collectors.joining(", ")));
        contexts.forEach((name, camelContext) -> camelContext.setTracing(enable));
        standalone.setTrace(enable);
    }
}
