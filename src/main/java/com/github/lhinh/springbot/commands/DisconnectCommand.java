package com.github.lhinh.springbot.commands;


import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.GuildAudioManager;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

@Component
public class DisconnectCommand implements SlashCommand {

    private final GuildAudioManager guildAudioManager;

    public DisconnectCommand(final GuildAudioManager guildAudioManager) { this.guildAudioManager = guildAudioManager; }

    @Override
    public String getName() { return "disconnect"; }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Snowflake currentGuildId = event.getInteraction().getGuildId().orElseThrow();
        GuildAudioManager currentGAM = guildAudioManager.of(currentGuildId);
        event.getClient().getVoiceConnectionRegistry().getVoiceConnection(currentGuildId)
            .flatMap(voiceConnection -> {
                currentGAM.cleanUp();
                return voiceConnection.disconnect();
            })
            .subscribe();

        return event.reply("Disconnect.")
                .withEphemeral(true);
    }

}
