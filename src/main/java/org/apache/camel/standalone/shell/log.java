package org.apache.camel.standalone.shell;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.*;
import org.crsh.command.BaseCommand;
import org.crsh.text.Decoration;
import org.crsh.text.RenderPrintWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

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
            // TODO: do something with the exception
            e.printStackTrace();
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
                    // TODO: Do something with this exception
                    e.printStackTrace();
                }
            }
        }
    }
}
