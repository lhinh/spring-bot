package com.github.lhinh.springbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.RestClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class DiscordConfig {

    private final DiscordConfigProperties discordConfigProperties;

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        return DiscordClientBuilder.create(discordConfigProperties.getBotToken()).build()
            .gateway()
            .login()
            .block();
    }
    
    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }
}
