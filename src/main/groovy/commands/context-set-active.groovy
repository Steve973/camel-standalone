import org.apache.camel.standalone.StandaloneRunner
import org.crsh.cli.*
import org.crsh.text.Color

@Usage('Sets a Camel Context as the active context in the shell')
@Man('Sets a Camel Context as the active context in the shell for operations. \
      Supply the name of the context to remove.')
class contextSetActive {
    StandaloneRunner standalone;

    @Command
    public void main(@Required @Argument String name) {
        try {
            standalone.activeContext = name
            context.session['activeContext'] = name
        } catch (Exception e) {
            out.println "Could set the active context to '${name}': ${e.message}", Color.red
        }
    }
}
