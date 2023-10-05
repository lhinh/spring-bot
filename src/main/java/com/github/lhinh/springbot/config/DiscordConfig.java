package com.github.lhinh.springbot.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lhinh.springbot.listeners.EventListener;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.rest.RestClient;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Configuration
public class DiscordConfig {

    private final DiscordConfigProperties discordConfigProperties;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListener) {
        GatewayDiscordClient discordClient = DiscordClientBuilder.create(discordConfigProperties.getBotToken()).build()
            .gateway()
            .withEventDispatcher(dispatcher -> {
                return Flux.fromIterable(eventListener)
                    .flatMap(listener -> {
                        return dispatcher.on(listener.getEventType()).flatMap(listener::handle).onErrorResume(listener::handleError);
                    });
            })
            .login()
            .block();

        // for (EventListener<T> listener : eventListener) {
        //     discordClient.on(listener.getEventType(), listener::handle)
        //         .onErrorResume(listener::handleError)
        //         .subscribe();
        // }

        return discordClient;   
    }
    
    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }
}
