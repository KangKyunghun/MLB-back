package com.football.Football_back.Global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FootballApiProperties.class)
public class AppConfig {
    
}
