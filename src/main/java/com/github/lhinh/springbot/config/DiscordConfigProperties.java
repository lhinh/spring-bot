package com.github.lhinh.springbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "discord")
public class DiscordConfigProperties {
    private String botToken;
    private String pinChannelName;
    private String visitorData;
    private String poToken;
}
