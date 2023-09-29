package com.github.lhinh.springbot.commands;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.AudioTrackScheduler;
import com.github.lhinh.springbot.musicplayer.GuildAudioManager;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

@Component
public class StopCommand implements SlashCommand {

    private final GuildAudioManager guildAudioManager;

    public StopCommand(@NonNull final GuildAudioManager guildAudioManager) { this.guildAudioManager = guildAudioManager; }

    @Override
    public String getName() { return "stop"; }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Snowflake guildId = event.getInteraction().getGuildId().orElseThrow();
        GuildAudioManager currentGuildAudioManager = guildAudioManager.of(guildId);
        AudioTrackScheduler scheduler = currentGuildAudioManager.getScheduler();
        if (scheduler.isPlaying()) {
            scheduler.stop();
            return event.reply("Stop playback & clear playlist.");
        }
        return event.reply("Nothing is playing.");
    }
    
}
