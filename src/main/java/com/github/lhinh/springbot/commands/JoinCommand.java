package com.github.lhinh.springbot.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.interaction.SlashCommandEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.VoiceChannelJoinSpec;
import discord4j.core.object.VoiceState;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;

@Component
public class JoinCommand implements SlashCommand {
	
	@Autowired
	private AudioProvider provider;

	@Override
	public String getName() {
		return "join";
	}

	@Override
	public Mono<Void> handle(SlashCommandEvent event) {
		
		Mono<VoiceConnection> voiceMono = Mono.justOrEmpty(event.getInteraction().getMember())
				.flatMap(Member::getVoiceState)
				.flatMap(VoiceState::getChannel)
				.flatMap(channel -> channel.join().withProvider(provider));
				// Deprecated method
//				.flatMap(channel -> channel.join(Spec -> Spec.setProvider(provider)));

		// Need to return an InteractionEventCallbackReplyMono or whatever it is
		//  Returning a Mono<Void> replies in Discord with 'Interaction failed' message
		return voiceMono.then();
	}

}
