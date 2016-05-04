package org.apache.camel.standalone.shell.commands;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.apache.camel.standalone.StandaloneRunner;
import org.apache.camel.standalone.logging.LogLineProcessor;
import org.crsh.cli.Command;
import org.crsh.cli.Man;
import org.crsh.cli.Option;
import org.crsh.cli.Usage;
import org.crsh.command.BaseCommand;
import org.crsh.text.Color;
import org.crsh.text.RenderPrintWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Usage("Displays the log")
@Man("All logging is written go <workdir>/logs/camel-standalone.log. This command prints the contents of the log.")
public class log extends BaseCommand {
    @Autowired
    StandaloneRunner instance;

    String file = "camel-standalone.log";

    private static long getNumberOfLinesInFile(String filePath) throws IOException {
        return Files.readLines(
                Paths.get(filePath).toFile(), Charsets.UTF_8,
                new LineProcessor<Long>() {
                    long numberOfLinesInTextFile = 0;

                    @Override
                    public boolean processLine(String line) throws IOException {
                        numberOfLinesInTextFile++;
                        return true;
                    }

                    @Override
                    public Long getResult() {
                        return numberOfLinesInTextFile;
                    }
                });
    }

    private static void printLogLines(String file, LogLineProcessor processor, RenderPrintWriter out) throws IOException {
        Files.readLines(new File(file), Charsets.UTF_8, processor)
                .forEach(out::println);
    }

    @Command
    public void main() {
        try {
            printLogLines(file, new LogLineProcessor(0, null), out);
        } catch (Exception e) {
            out.println("Encountered an error while printing the Camel Standalone log: " + e.getMessage(), Color.red);
        }
    }

    @Command
    public void tail(@Option(names = {"n", "lines"}) int lines,
                     @Option(names = {"f", "follow"}) Boolean follow,
                     @Option(names = {"s", "search"}) String searchString) {
        LogLineProcessor processor = new LogLineProcessor(lines, searchString);
        if (follow) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    printLogLines(file, processor, out);
                } catch (IOException e) {
                    out.println("Encountered an error while printing the Camel Standalone log: " + e.getMessage(), Color.red);
                }
            }
        }
    }
}
