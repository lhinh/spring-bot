package com.github.lhinh.springbot.listeners;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.GuildAudioManager;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;

@Component
public class AutoDisconnectListener implements EventListener<VoiceStateUpdateEvent> {

    private final GuildAudioManager guildAudioManager;

    public AutoDisconnectListener(final GuildAudioManager guildAudioManager) { this.guildAudioManager = guildAudioManager; }

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

        Publisher<Boolean> voiceStateCounter = event.getClient().getChannelById(voiceChannelId)
            .cast(VoiceChannel.class)
            .map(VoiceChannel::getVoiceStates)
            .flatMap(voiceStates -> voiceStates.count())
            .map(count -> 1L == count);

        Mono<Snowflake> botVoiceChannelId = event.getClient().getVoiceConnectionRegistry().getVoiceConnection(currentGuildId)
            .flatMap(VoiceConnection::getChannelId);

        Mono<Void> onEvent = botVoiceChannelId
            .filter(voiceChannelId::equals)
            .filterWhen(ignore -> voiceStateCounter)
            .switchIfEmpty(Mono.never())
            .then();

        Mono<Void> disconnect = event.getClient().getVoiceConnectionRegistry().getVoiceConnection(currentGuildId)
            .flatMap(voiceChannel -> {
                currentGAM.cleanUp();
                return voiceChannel.disconnect();
            });

        return onEvent.then(disconnect);
    }
}
