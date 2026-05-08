package com.football.Football_back.Global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "football.api")
public class FootballApiProperties {

    private String baseUrl;
    private String key;
    private Map<String, String> leagues;
    private List<String> seasons;
    
}
