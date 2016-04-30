package org.apache.camel.standalone.cli;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.*;
import org.springframework.beans.factory.annotation.Autowired;

@Named("workdir")
@Usage("Sets the working directory")
@Man("The work directory is where all Camel Standalone data resides: it is where logs are stored, " +
        "and where the drop-in directories are created for dynamically adding routes.")
public class WorkDirCommand {
    @Autowired
    StandaloneRunner instance;

    @Command
    public void main(@Required @Argument String workdir) {
        instance.setWorkDir(workdir);
    }
}
