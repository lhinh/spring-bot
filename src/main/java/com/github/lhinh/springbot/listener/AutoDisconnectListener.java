package com.github.lhinh.springbot.listener;

import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.GuildAudioManager;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.channel.VoiceChannel;
import reactor.core.publisher.Mono;

@Component
public class AutoDisconnectListener implements EventListener<VoiceStateUpdateEvent> {

    private final GuildAudioManager guildAudioManager;

    public AutoDisconnectListener(final GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public Class<VoiceStateUpdateEvent> getEventType() {
        return VoiceStateUpdateEvent.class;
    }

    @Override
    public Mono<Void> handle(VoiceStateUpdateEvent event) {
        Snowflake currentGuildId = event.getCurrent().getGuildId();
        GuildAudioManager currentGAM = guildAudioManager.of(currentGuildId);

        Snowflake voiceChannelId;
        if (event.isJoinEvent()) {
            voiceChannelId = event.getCurrent().getChannelId().orElseThrow();
        } else {
            voiceChannelId = event.getOld().orElseThrow()
                    .getChannelId().orElseThrow();
        }

        return event.getClient().getVoiceConnectionRegistry().getVoiceConnection(currentGuildId)
                .flatMap(voiceConnection -> voiceConnection.getChannelId())
                .filter(voiceChannelId::equals)
                .flatMapMany(ignored -> event.getClient().getChannelById(voiceChannelId)
                        .cast(VoiceChannel.class)
                        .flatMapMany(VoiceChannel::getVoiceStates))
                .count()
                .filter(count -> count == 1)
                .flatMap(ignored -> {
                    currentGAM.cleanUp();
                    return event.getClient().getVoiceConnectionRegistry().getVoiceConnection(currentGuildId)
                            .flatMap(voiceConnection -> voiceConnection.disconnect());
                });
    }
}
