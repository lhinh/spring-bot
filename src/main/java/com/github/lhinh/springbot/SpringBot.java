package com.github.lhinh.springbot;

import com.github.lhinh.springbot.listeners.SlashCommandListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.interaction.SlashCommandEvent;
import discord4j.rest.RestClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBot {
	//REMOVE THIS WHEN DONE TESTING!!!
//	final static String TOKEN = "ODg1OTE0NzYyMDc1MDc4Njc3.YTt-ag.DkfdbYzQKu3mw_kYgDsIx3Bzv94";
	//REMOVE THIS WHEN DONE TESTING!!!
	
	public static void main(String[] args) {
		
		//Start spring application
        ApplicationContext springContext = new SpringApplicationBuilder(SpringBot.class)
            .build()
            .run(args);

        //Login
        DiscordClientBuilder.create(System.getenv("BOT_TOKEN")).build()
//        DiscordClientBuilder.create(TOKEN).build()
            .withGateway(gatewayClient -> {
                SlashCommandListener slashCommandListener = new SlashCommandListener(springContext);

                Mono<Void> onSlashCommandMono = gatewayClient
                    .on(SlashCommandEvent.class, slashCommandListener::handle)
                    .then();

                return Mono.when(onSlashCommandMono);
            }).block();
	}
	
	@Bean
    public RestClient discordRestClient() {
        return RestClient.create(System.getenv("BOT_TOKEN"));
//		return RestClient.create(TOKEN);
    }
}
