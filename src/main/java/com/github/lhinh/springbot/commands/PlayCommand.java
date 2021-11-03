package com.github.lhinh.springbot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.common.util.Snowflake;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
//	@Autowired
//	private TrackScheduler scheduler;
	
	@Override
	public String getName() {
		return "play";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		
		final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
		
//		Mono<VoiceConnection> voiceMono = Mono.justOrEmpty(event.getInteraction().getMember())
//				.flatMap(Member::getVoiceState)
//				.flatMap(VoiceState::getChannel)
//				.flatMap(channel -> channel.join().withProvider(provider));
//		
//		LOGGER.info("Joining voice channel.");
		
		TrackScheduler scheduler = new TrackScheduler(player);
		
		String link = event.getOption("link")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.get();
		
		long guildId = event.getInteraction().getGuildId()
				.map(Snowflake::asLong)
				.get();
		
		playerManager.loadItem(link, scheduler);
		
		LOGGER.info("Playing link: " + link);
		
		return event.reply("Now Playing: " + link);
//				.and(voiceMono);
	}

}
