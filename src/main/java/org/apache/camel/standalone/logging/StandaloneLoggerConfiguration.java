package org.apache.camel.standalone.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public class StandaloneLoggerConfiguration extends AbstractConfiguration {
    public static final String CONFIG_NAME = "StandaloneLoggerConfiguration";

    public static final String DEFAULT_LEVEL = "org.apache.logging.log4j.level";

    private static final long serialVersionUID = 1L;

    public StandaloneLoggerConfiguration() {
        this(ConfigurationSource.NULL_SOURCE);
    }

    /**
     * Constructor to create the default configuration.
     */
    public StandaloneLoggerConfiguration(ConfigurationSource source) {
        super(source);

        setName(CONFIG_NAME);
        final Appender appender = StandaloneLogEventAppender.createAppender("StandaloneLogAppender", 1000);
        appender.start();
        addAppender(appender);
        final LoggerConfig root = getRootLogger();
        root.addAppender(appender, null, null);

        final String levelName = PropertiesUtil.getProperties().getStringProperty(DEFAULT_LEVEL);
        final Level level = levelName != null && Level.valueOf(levelName) != null ?
                Level.valueOf(levelName) : Level.ALL;
        root.setLevel(level);
    }

    @Override
    protected void doConfigure() {
    }
}
