package org.apache.camel.standalone.shell.commands;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.Command;
import org.crsh.cli.Man;
import org.crsh.cli.Usage;
import org.crsh.command.BaseCommand;
import org.springframework.beans.factory.annotation.Autowired;

@Usage("Prints the status of Camel Standalone")
@Man("Prints the status of Camel Standalone. " +
        "Possible values are: New, Initialized, Started, Stopped, Suspended, and Shut Down.")
public class status extends BaseCommand {
    @Autowired
    StandaloneRunner instance;

    @Command
    public void main() {
        out.println("Camel Standalone is: " + instance.getStatus());
    }
}
