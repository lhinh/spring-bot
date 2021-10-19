package com.github.lhinh.springbot.commands;

import org.springframework.stereotype.Component;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

@Component
public class PlayCommand implements SlashCommand {

	@Override
	public String getName() {
		return "play";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {

		
		return null;
	}

}
