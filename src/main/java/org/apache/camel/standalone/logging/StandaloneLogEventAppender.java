package org.apache.camel.standalone.logging;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.util.concurrent.LinkedBlockingQueue;

@Plugin(name = "StandaloneLogEventAppender", category = "Core", elementType = "appender", printObject = true)
public class StandaloneLogEventAppender extends AbstractAppender {
    private final LinkedBlockingQueue<LogEvent> logEventQueue;
    private final int maxSize;

    public StandaloneLogEventAppender(String name) {
        this(name, 1000);
    }

    public StandaloneLogEventAppender(String name, int maxSize) {
        super(name,
                null,
                null,
                false);
        this.logEventQueue = new LinkedBlockingQueue<>(maxSize);
        this.maxSize = maxSize;
    }

    @Override
    public void append(LogEvent event) {
        if (logEventQueue.size() == maxSize) {
            logEventQueue.poll();
        }
        logEventQueue.offer(event);
    }

    @PluginFactory
    public static StandaloneLogEventAppender createAppender(@PluginAttribute("name") String name,
                                                            @PluginAttribute("maxSize") int maxSize) {
        if (name == null) {
            LOGGER.error("No name provided for StandaloneLogEventAppender");
            return null;
        }
        return new StandaloneLogEventAppender(name, maxSize);
    }
}
