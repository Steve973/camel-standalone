import org.apache.camel.CamelContext
import org.apache.camel.standalone.StandaloneRunner
import org.crsh.cli.*

@Usage('Enables or disables tracing for all registered contexts')
@Man('Enables or disables tracing for all registered contexts.  Use the parameter --enable=<true|false> to ' +
        'enable or disable tracing.')
class trace {
    StandaloneRunner standalone

    @Command
    public void main(@Required @Argument Boolean enable) {
        Map<String, CamelContext> contexts = standalone.contexts
        out.print "${enable ? 'Enabling' : 'Disabling'} tracing for: "
        out.println contexts.keySet().join(', ')
        contexts.values().each { it.tracing = enable }
        standalone.trace = enable
    }
}
