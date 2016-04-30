package org.apache.camel.standalone.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RoutesDefinition;

import java.io.File;
import java.io.InputStream;

public class XmlDropInRouteBuilder extends RouteBuilder {
    private final String xmlRouteDir;
    private final CamelContext context;

    public XmlDropInRouteBuilder(final String dropInXmlRouteDir, final CamelContext context) {
        this.xmlRouteDir = dropInXmlRouteDir + "/" + context.getName();
        this.context = context;
        File xmlRouteDir = new File(dropInXmlRouteDir, context.getName());
        if (!xmlRouteDir.mkdirs()) {
            throw new RuntimeException("Could not create xml drop-in route directories");
        }
    }

    @Override
    public void configure() throws Exception {
        from("file://" + xmlRouteDir + "?noop=true&idempotent=true&delay=5000&include=.*\\.xml")
                .routeId(this.context.getName() + "-xmlDropInRoute")
                .description("Consumes Spring xml routes from the routes/xml/" + this.context.getName() + " directory")
                .process(
                        exchange -> {
                            try (InputStream is = exchange.getIn().getBody(InputStream.class)) {
                                RoutesDefinition routes = context.loadRoutesDefinition(is);
                                context.addRouteDefinitions(routes.getRoutes());
                            }
                        }
                );
    }
}
