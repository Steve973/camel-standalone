package org.apache.camel.standalone.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import java.io.File;

public class JarDropInRouteBuilder extends RouteBuilder {
    private final String jarRouteDir;
    private final CamelContext context;

    public JarDropInRouteBuilder(final String dropInJarRouteDir, final CamelContext camelContext) {
        this.jarRouteDir = dropInJarRouteDir + "/" + camelContext.getName();
        this.context = camelContext;
        File jarRouteDir = new File(dropInJarRouteDir, context.getName());
        if (!jarRouteDir.mkdirs()) {
            throw new RuntimeException("Could not create jar drop-in route directories");
        }
    }

    @Override
    public void configure() throws Exception {
        from("file://" + jarRouteDir + "?noop=true&idempotent=true&delay=5000&include=.*\\.jar")
                .routeId(this.context.getName() + "-jarDropInRoute")
                .description("Consumes jars containing RouteBuilder classes from the routes/jar/"
                        + this.context.getName() + " directory")
                .process(
                        exchange -> {
                        }
                );
    }
}
