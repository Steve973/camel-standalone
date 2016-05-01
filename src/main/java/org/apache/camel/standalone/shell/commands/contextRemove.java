package org.apache.camel.standalone.shell.commands;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.*;
import org.crsh.command.BaseCommand;
import org.crsh.text.Color;
import org.springframework.beans.factory.annotation.Autowired;

@Usage("Removes a Camel Context")
@Man("Removes a Camel Context from Camel Standalone.  Supply the name of the context to remove.")
public class contextRemove extends BaseCommand {
    @Autowired
    StandaloneRunner standalone;

    @Command
    public void main(@Required @Argument String name) {
        try {
            standalone.removeContext(name);
        } catch (Exception e) {
            out.println("Could not remove the context: " + e.getMessage(), Color.red);
        }
    }
}
