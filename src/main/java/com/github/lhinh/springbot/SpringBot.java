package com.github.lhinh.springbot;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.RestClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBot {
	
	public static void main(String[] args) {
		
		new SpringApplicationBuilder(SpringBot.class)
            .build()
            .run(args);

        // //Login
        // DiscordClientBuilder.create(System.getenv("BOT_TOKEN")).build()
        //     .withGateway(gatewayClient -> {
        //         SlashCommandListener slashCommandListener = new SlashCommandListener(springContext);

        //         Mono<Void> onSlashCommandMono = gatewayClient
        //             .on(ChatInputInteractionEvent.class, slashCommandListener::handle)
        //             .then();

        //         return Mono.when(onSlashCommandMono);
        //     }).block();
	}

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        return DiscordClientBuilder.create(System.getenv("BOT_TOKEN")).build()
            .gateway()
            .login()
            .block();
    }
	
	@Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }
}
