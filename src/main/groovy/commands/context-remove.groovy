import org.apache.camel.standalone.StandaloneRunner
import org.crsh.cli.*
import org.crsh.text.Color

@Usage('Removes a Camel Context')
@Man('Removes a Camel Context from Camel Standalone.  Supply the name of the context to remove.')
class contextRemove {
    StandaloneRunner standalone

    @Command
    public void main(@Required @Argument String name) {
        try {
            standalone.removeContext name
        } catch (Exception e) {
            out.println "Could not remove the context: ${e.message}", Color.red
        }
    }
}
