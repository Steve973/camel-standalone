import org.apache.camel.CamelContext
import org.apache.camel.Route
import org.apache.camel.standalone.StandaloneRunner
import org.crsh.cli.*

@Usage('Prints info about Camel Standalone\'s registered contexts')
@Man('Prints info about Camel Standalone\'s registered contexts. Supply the name of a context by using the ' +
        '--name=<contextName> parameter for info on a single context, or get information about all registered ' +
        'contexts by using the command without any parameters.')
class contextInfo {
    private def String INDENT = '  '
    StandaloneRunner standalone

    @Command
    public void main(@Argument String name) {
        if (!name) {
            printAllContextInfo()
        } else {
            out.println 'Context info:'
            printContextInfo 2, standalone.contexts[name]
        }
    }

    private void printAllContextInfo() {
        out.println('All registered contexts:')
        standalone.contexts.values().each { printContextInfo 1, it }
    }

    private void printContextInfo(int indentLevel, CamelContext camelContext) {
        def status = camelContext.status.name()
        printWithIndent indentLevel, "Context (${camelContext.name}):"
        printWithIndent indentLevel, "  Status: ${status}"
        printWithIndent indentLevel, "  Uptime: ${camelContext.uptime}"
        printWithIndent indentLevel, "  Routes: ${camelContext.routes.size()}"
        camelContext.routes.each { printRouteInfo indentLevel + 2, it }
    }

    private void printRouteInfo(int indentLevel, Route route) {
        printWithIndent indentLevel, "Route (${route.id}):"
        printWithIndent indentLevel, "  description: ${route.description}"
        printWithIndent indentLevel, "  uri: ${route.endpoint.endpointUri}"
        printWithIndent indentLevel, "  uptime: ${route.uptime}"
    }

    private void printWithIndent(int level, String message) {
        def builder = new StringBuilder()
        level.times { builder.append INDENT }
        builder.append message
        out.println builder.toString()
    }
}
