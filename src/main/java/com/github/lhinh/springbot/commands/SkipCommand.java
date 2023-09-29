package com.github.lhinh.springbot.commands;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.AudioTrackScheduler;
import com.github.lhinh.springbot.musicplayer.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

@Component
public class SkipCommand implements SlashCommand {

    private final GuildAudioManager guildAudioManager;

    public SkipCommand(@NonNull final GuildAudioManager guildAudioManager) { this.guildAudioManager = guildAudioManager; }
    
    @Override
    public String getName() { return "skip"; }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Snowflake guildId = event.getInteraction().getGuildId().orElseThrow();

        GuildAudioManager currentGuildAudioManager = guildAudioManager.of(guildId);
        AudioTrackScheduler scheduler = currentGuildAudioManager.getScheduler();
        List<AudioTrack> playlist = scheduler.getQueue();

        if (playlist.isEmpty()) {
            scheduler.stop();
            return event.reply("Nothing left to play :(");
        }

        String nextTrack = playlist.get(0).getInfo().uri;
        scheduler.skip();
        return event.reply("[" + playlist.size() + "] Remaining tracks\n" +
            "Playing next track: " + nextTrack);
    }
    
}
