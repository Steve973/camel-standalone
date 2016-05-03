package org.apache.camel.standalone.shell.commands;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.*;
import org.crsh.command.BaseCommand;
import org.crsh.text.Color;
import org.springframework.beans.factory.annotation.Autowired;

@Usage("Sets a Camel Context as the active context in the shell")
@Man("Sets a Camel Context as the active context in the shell for operations. " +
        "Supply the name of the context to remove.")
public class contextSetActive extends BaseCommand {
    @Autowired
    StandaloneRunner standalone;

    @Command
    public void main(@Required @Argument String name) {
        try {
            standalone.setActiveContext(name);
            context.getSession().put("activeContext", name);
        } catch (Exception e) {
            out.println("Could set the active context to '" + name + "': " + e.getMessage(), Color.red);
        }
    }
}
