import org.apache.camel.standalone.StandaloneRunner
import org.crsh.cli.*
import org.crsh.text.Color

@Usage('Adds a Camel Context')
@Man('Adds a Camel Context to Camel Standalone.  Supply the name of the context, and supply the ' +
        '--start parameter to start the context upon creation, and supply the --active parameter ' +
        'to make this context the active context in the shell.')
class contextAdd {
    StandaloneRunner standalone;

    @Command
    public void main(@Required @Argument String name,
                     @Option(names = ['s', 'start']) Boolean start = true,
                     @Option(names = ['a', 'active']) Boolean active = false) {
        try {
            standalone.addCamelContext name, start, active
        } catch (Exception e) {
            out.println "Could not add the context: ${e.message}", Color.red
        }
    }
}
