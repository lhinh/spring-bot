package com.github.lhinh.springbot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import reactor.core.publisher.Mono;

@Component
public class PlayCommand implements SlashCommand {

	@Autowired
	private AudioPlayerManager playerManager;
	
	@Autowired
	private AudioPlayer player;
	
	@Override
	public String getName() {
		return "play";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		
		TrackScheduler scheduler = new TrackScheduler(player);
		
		String link = event.getOption("link")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.get();
		
		playerManager.loadItem(link, scheduler);
		
		return event.reply("Now Playing: " + link)
				.withEphemeral(true);
	}

}
