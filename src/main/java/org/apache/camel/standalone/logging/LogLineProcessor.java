package org.apache.camel.standalone.logging;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.io.LineProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogLineProcessor implements LineProcessor<List<String>> {
    private final List<String> resultLines;
    private final String searchString;
    private int currentLineNumber = 0;
    private long startLineNumber;

    public LogLineProcessor(long startLineNumber, String searchString) {
        resultLines = new ArrayList<>();
        this.startLineNumber = startLineNumber;
        this.searchString = searchString;
    }

    @Override
    public boolean processLine(String line) throws IOException {
        if (this.currentLineNumber > this.startLineNumber) {
            if (Strings.isNullOrEmpty(this.searchString) || line.contains(this.searchString)) {
                this.resultLines.add(line);
            }
            startLineNumber++;
        }
        currentLineNumber++;
        return true;
    }

    @Override
    public List<String> getResult() {
        List<String> result = ImmutableList.copyOf(this.resultLines);
        this.resultLines.clear();
        return result;
    }
}

