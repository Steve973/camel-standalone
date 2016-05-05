import org.apache.camel.standalone.logging.StandaloneLogEventAppender
import org.crsh.cli.Command
import org.crsh.cli.Man
import org.crsh.cli.Option
import org.crsh.cli.Usage

@Usage('Displays the log')
@Man('All logging is written go <workdir>/logs/camel-standalone.log. This command prints the contents of the log.')
class log {
    StandaloneLogEventAppender appender;

    @Command
    public void main() {
        appender.logEvents.each { out.println "${it.timeMillis} ${it.level} ${it.loggerName} ${it.message}" }
    }

    @Command
    public void tail(@Option(names = ['n', 'lines']) int lines,
                     @Option(names = ['f', 'follow']) Boolean follow,
                     @Option(names = ['s', 'search']) String searchString) {
        if (follow) {
            while (!Thread.currentThread().interrupted) {
                appender.logEvents.each { out.println "${it.timeMillis} ${it.level} ${it.loggerName} ${it.message}" }
            }
        }
    }

}
