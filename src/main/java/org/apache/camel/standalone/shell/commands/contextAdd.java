package org.apache.camel.standalone.shell.commands;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.*;
import org.crsh.command.BaseCommand;
import org.crsh.text.Color;
import org.springframework.beans.factory.annotation.Autowired;

@Usage("Adds a Camel Context")
@Man("Adds a Camel Context to Camel Standalone.  Supply the name of the context, and supply the --start parameter to " +
        "start the context upon creation, and supply the --active parameter to make this context the active context " +
        "in the shell.")
public class contextAdd extends BaseCommand {
    @Autowired
    StandaloneRunner standalone;

    @Command
    public void main(@Required @Argument String name,
                     @Option(names = {"s", "start"}) Boolean start,
                     @Option(names = {"a", "active"}) Boolean active) {
        try {
            standalone.addCamelContext(
                    name,
                    start == null ? true : start,
                    active == null ? false : active);
        } catch (Exception e) {
            out.println("Could not add the context: " + e.getMessage(), Color.red);
        }
    }
}
