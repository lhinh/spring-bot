package com.github.lhinh.springbot.config;

import java.time.Duration;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lhinh.springbot.listeners.EventListener;

import discord4j.common.ReactorResources;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.rest.RestClient;
import discord4j.rest.request.RouteMatcher;
import discord4j.rest.response.ResponseFunction;
import io.netty.channel.unix.Errors.NativeIoException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.netty.resources.ConnectionProvider;
import reactor.retry.Retry;

@RequiredArgsConstructor
@Configuration
public class DiscordConfig {

    private final DiscordConfigProperties discordConfigProperties;

    @Bean
    public <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListener) {
        GatewayDiscordClient discordClient = DiscordClientBuilder.create(discordConfigProperties.getBotToken())
            .onClientResponse(ResponseFunction.retryWhen(
                RouteMatcher.any(),
                Retry.anyOf(NativeIoException.class)))
            .setReactorResources(ReactorResources.builder()
                .httpClient(ReactorResources.newHttpClient(ConnectionProvider.builder("custom")
                    .maxIdleTime(Duration.ofMinutes(5))
                    .build()))
                .build())
            .build()
            .gateway()
            .withEventDispatcher(dispatcher -> {
                return Flux.fromIterable(eventListener)
                    .flatMap(listener -> {
                        return dispatcher.on(listener.getEventType()).flatMap(listener::handle).onErrorResume(listener::handleError);
                    });
            })
            .login()
            .block();

        return discordClient;   
    }
    
    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }
}
