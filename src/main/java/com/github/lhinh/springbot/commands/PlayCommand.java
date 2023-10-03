package com.github.lhinh.springbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.voice.VoiceConnection;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.AudioTrackLoadResultHandler;
import com.github.lhinh.springbot.musicplayer.AudioTrackScheduler;
import com.github.lhinh.springbot.musicplayer.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import reactor.core.publisher.Mono;

@Component
public class PlayCommand implements SlashCommand {

    private final GuildAudioManager guildAudioManager;

    public PlayCommand(@NonNull final GuildAudioManager guildAudioManager) { this.guildAudioManager = guildAudioManager; }
    
    @Override
    public String getName() { return "play"; }

    private Mono<VoiceConnection> joinMemberChannel(ChatInputInteractionEvent event) {
        return Mono.justOrEmpty(event.getInteraction().getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(VoiceState::getChannel)
            .flatMap(channel -> {
                return channel.join().withProvider(guildAudioManager.of(channel.getGuildId()).getProvider());
            });
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Snowflake guildId = event.getInteraction().getGuildId().orElseThrow();

        Mono<Snowflake> memberVoiceChannelId =  Mono.justOrEmpty(event.getInteraction().getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(voiceState -> Mono.justOrEmpty(voiceState.getChannelId()));

        Mono<Snowflake> currentVoiceChannelId = Mono.justOrEmpty(event.getClient().getVoiceConnectionRegistry())
            .flatMap(voiceConnectionRegistry -> voiceConnectionRegistry.getVoiceConnection(guildId))
            .flatMap(VoiceConnection::getChannelId);
        
        memberVoiceChannelId.zipWith(currentVoiceChannelId)
            .flatMap(tuple -> {
                if (tuple.getT1().equals(tuple.getT2()))
                    return Mono.just("Same channel, don't join again.");

                return joinMemberChannel(event);
            })
            .switchIfEmpty(Mono.defer(() -> joinMemberChannel(event)))
            .block();

        String link = event.getOption("link")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .orElseThrow();

        GuildAudioManager currentGAM = guildAudioManager.of(guildId);

        currentGAM.loadItem(link);
        
        if (currentGAM.isPlaylistEmpty())
            return event.reply("Now Playing: " + link);

        int trackPosition = currentGAM.getPlaylistSize();
        return event.reply("#" + trackPosition + " in playlist\n" + link);
    }

}
