import org.apache.camel.standalone.StandaloneRunner
import org.crsh.cli.Command
import org.crsh.cli.Man
import org.crsh.cli.Usage

@Usage('Prints the status of Camel Standalone')
@Man('Prints the status of Camel Standalone. \
      Possible values are: New, Initialized, Started, Stopped, Suspended, and Shut Down.')
class status {
    StandaloneRunner instance

    @Command
    public void main() {
        out.println "Camel Standalone is: ${instance.status}"
    }
}
