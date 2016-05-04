package org.apache.camel.standalone.config;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.spring.SpringBootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class StandaloneConfig {
    @Bean
    public SpringBootstrap springBootstrap() {
        SpringBootstrap springBootstrap = new SpringBootstrap();
        springBootstrap.setCmdMountPointConfig("classpath:/org/apache/camel/standalone/shell/commands");
        springBootstrap.setConfMountPointConfig("classpath:/camel-standalone-crash.properties");
        Properties properties = new Properties();
        properties.put("crash.vfs.refresh_period", "1");
        properties.put("crash.ssh.port", "2000");
        properties.put("crash.ssh.auth_timeout", "300000");
        properties.put("crash.ssh.idle_timeout", "300000");
        properties.put("crash.telnet.port", "5000");
        properties.put("crash.auth", "simple");
        properties.put("crash.auth.simple.username", "admin");
        properties.put("crash.auth.simple.password", "admin");
        springBootstrap.setConfig(properties);
        return springBootstrap;
    }

    @Bean
    public StandaloneRunner standaloneRunner() {
        return StandaloneRunner.getInstance();
    }
}
