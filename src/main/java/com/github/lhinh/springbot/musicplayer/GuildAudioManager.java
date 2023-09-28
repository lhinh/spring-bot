package com.github.lhinh.springbot.musicplayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import discord4j.common.util.Snowflake;
import lombok.Getter;

@Getter
@Component
public class GuildAudioManager{

    private final AudioPlayer player;
    private final AudioTrackScheduler scheduler;
    private final LavaPlayerAudioProvider provider;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Snowflake, GuildAudioManager> guildAudioManagers = new ConcurrentHashMap<>();

    public GuildAudioManager of(Snowflake guildId) {
        return guildAudioManagers.computeIfAbsent(guildId, ignored -> new GuildAudioManager(audioPlayerManager));
    }

    private GuildAudioManager(final AudioPlayerManager audioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager;
        player = audioPlayerManager.createPlayer();
        scheduler = new AudioTrackScheduler(player);
        provider = new LavaPlayerAudioProvider(player);

        player.addListener(scheduler);
    }
}
