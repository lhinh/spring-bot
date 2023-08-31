package com.github.lhinh.springbot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PlayCommand implements SlashCommand {

	private final AudioPlayerManager playerManager;
	
	private final AudioPlayer player;
	
	private TrackScheduler scheduler;

	PlayCommand(AudioPlayerManager playerManager, AudioPlayer player) {
		this.playerManager = playerManager;
		this.player = player;
	}
	
	@Override
	public String getName() {
		return "play";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {		
//		Mono<VoiceConnection> voiceMono = Mono.justOrEmpty(event.getInteraction().getMember())
//				.flatMap(Member::getVoiceState)
//				.flatMap(VoiceState::getChannel)
//				.flatMap(channel -> channel.join().withProvider(provider));
//		
//		LOGGER.info("Joining voice channel.");
		
		scheduler = new TrackScheduler(player);
		
		String link = event.getOption("link")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.get();
		
		// long guildId = event.getInteraction().getGuildId()
		// 		.map(Snowflake::asLong)
		// 		.get();
		
		playerManager.loadItem(link, scheduler);
		
		log.info("Playing link: " + link);
		
		return event.reply("Now Playing: " + link);
//				.and(voiceMono);
	}

}
