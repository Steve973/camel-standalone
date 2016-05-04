package org.apache.camel.standalone.cli;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.cli.impl.bootstrap.CommandProvider;
import org.crsh.cli.impl.descriptor.HelpDescriptor;
import org.crsh.cli.impl.invocation.InvocationMatch;
import org.crsh.cli.impl.invocation.InvocationMatcher;
import org.crsh.cli.impl.lang.CommandFactory;
import org.crsh.cli.impl.lang.Instance;
import org.crsh.cli.impl.lang.ObjectCommandDescriptor;
import org.crsh.cli.impl.lang.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class StandaloneCommandLineInterface {
    private static final Logger LOGGER = LogManager.getLogger(StandaloneCommandLineInterface.class);
    private final StandaloneRunner instance;

    public StandaloneCommandLineInterface(StandaloneRunner instance) {
        this.instance = instance;
    }

    private static <T> void handle(Class<T> commandClass, String line) throws Exception {
        ObjectCommandDescriptor<T> descriptor = CommandFactory.DEFAULT.create(commandClass);
        HelpDescriptor<Instance<T>> helpDescriptor = HelpDescriptor.create(descriptor);
        InvocationMatcher<Instance<T>> matcher = helpDescriptor.matcher();
        InvocationMatch<Instance<T>> match = matcher.parse(line);
        final T instance = commandClass.newInstance();
        Object o = match.invoke(Util.wrap(instance));
        if (o != null) {
            LOGGER.info(o.toString());
        }
    }

    public void processCommandLineArgs(String[] args) throws Exception {
        ServiceLoader<CommandProvider> loader = ServiceLoader.load(CommandProvider.class);
        Iterator<CommandProvider> iterator = loader.iterator();
        String line = Arrays.stream(args).
                map(this::escape).
                collect(Collectors.joining(" "));
        if (iterator.hasNext()) {
            CommandProvider commandProvider = iterator.next();
            Class<?> commandClass = commandProvider.getCommandClass();
            handle(commandClass, line);
        }
    }

    private String escape(CharSequence s) {
        return s.chars()
                .mapToObj(c -> (char) c)
                .map(c -> {
                    switch (c) {
                        case ' ':
                        case '"':
                        case '\'':
                        case '\\':
                            return "" + '\\' + c;
                    }
                    return "" + c;
                })
                .collect(Collectors.joining(""));
    }
}
