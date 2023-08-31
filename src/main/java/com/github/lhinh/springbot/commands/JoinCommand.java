package com.github.lhinh.springbot.commands;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Member;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JoinCommand implements SlashCommand {
	
	private final AudioProvider provider;

	JoinCommand(@NonNull AudioProvider provider) { this.provider = provider; }
	
	@Override
	public String getName() {
		return "join";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		
		Mono<VoiceConnection> voiceMono = Mono.justOrEmpty(event.getInteraction().getMember())
				.flatMap(Member::getVoiceState)
				.flatMap(VoiceState::getChannel)
				.flatMap(channel -> channel.join().withProvider(provider));
//				.doOnNext(voiceConnection -> Mono.justOrEmpty(event.getInteraction().getClient())
//						.flatMap(client -> client.getVoiceConnectionRegistry()
//								.registerVoiceConnection(voiceConnection.getGuildId(), voiceConnection)));
				// Deprecated method
//				.flatMap(channel -> channel.join(Spec -> Spec.setProvider(provider)));

		log.info("Joining voice channel.");

		return event.reply("Joined voice channel!")
				.withEphemeral(true)
				.and(voiceMono);
	}

}
