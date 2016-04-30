package org.apache.camel.standalone.cli;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.Command;
import org.crsh.cli.Man;
import org.crsh.cli.Named;
import org.crsh.cli.Usage;
import org.springframework.beans.factory.annotation.Autowired;

@Named("trace")
@Usage("Enables CamelContext tracing")
@Man("Camel supports a tracer interceptor that is used for logging the route executions at INFO level. " +
        "Enabling this option will provide a lot of debug information about your routes in the log at " +
        "<workdir>/logs/camel-standalone.log.")
public class TraceCommand {
    @Autowired
    StandaloneRunner instance;

    @Command
    public void main() {
        instance.setTrace(true);
    }
}
