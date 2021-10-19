package com.github.lhinh.springbot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.common.util.Snowflake;
import discord4j.voice.VoiceConnection;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

@Component
public class DisconnectCommand implements SlashCommand {

	@Override
	public String getName() {
		return "disconnect";
	}

	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
//		final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
		
//		GatewayDiscordClient client = event.getClient();
//		Snowflake guildId = event.getInteraction().getGuildId().get();
//		Mono<Object> test = Mono.justOrEmpty(client.getVoiceConnectionRegistry().disconnect(guildId));
//		Mono<Guild> voiceMono = Mono.justOrEmpty(event.getInteraction().getGuild())
//				.flatMap(Guild::getVoiceConnection);

//		Mono<Object> voiceMono = Mono.justOrEmpty(event.getClient())
//				.flatMap(gatewayDiscordClient -> Mono.justOrEmpty(event.getInteraction().getGuildId())
//						.flatMap(guildId -> gatewayDiscordClient.getVoiceConnectionRegistry().disconnect(guildId)));
//		Mono<Object> voiceMono = Mono.justOrEmpty(event.getInteraction().getGuildId())
//				.flatMap(guildId -> Mono.justOrEmpty(event.getInteraction().getClient())
//						.flatMap(client -> {
//							LOGGER.info("Guild ID =" + guildId.asString());
//							return client.getVoiceConnectionRegistry().disconnect(guildId);
//						}));
//		LOGGER.info("Disconnecting.");
//		Mono<Void> voiceMono = Mono.justOrEmpty(event.getInteraction().getMember())
//				.flatMap(Member::getVoiceState)
//				.flatMap(voiceState -> clientMono.getVoiceConnection())
//				.flatMap(VoiceChannel::getVoiceConnection)
//				.flatMap(connection -> connection.disconnect());

		return event.reply()
				.withEphemeral(true)
				.withContent("Disconnected!");
//				.and(test);
	}

}
