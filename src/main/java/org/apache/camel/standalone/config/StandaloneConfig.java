package org.apache.camel.standalone.config;

import org.apache.camel.standalone.StandaloneRunner;
import org.crsh.spring.SpringBootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StandaloneConfig {
    private static final SpringBootstrap springBootstrap = new SpringBootstrap();

    @Bean
    public SpringBootstrap springBootstrap() {
        return springBootstrap;
    }

    @Bean(name = "StandaloneRunner")
    public StandaloneRunner standaloneRunner() {
        return StandaloneRunner.getInstance();
    }
}
