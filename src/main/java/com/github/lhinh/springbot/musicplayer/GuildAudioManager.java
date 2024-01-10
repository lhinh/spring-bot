package com.github.lhinh.springbot.musicplayer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.util.HttpLinkUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import discord4j.common.util.Snowflake;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Component
public class GuildAudioManager{

    private final AudioPlayer player;
    private final AudioTrackScheduler scheduler;
    private final LavaPlayerAudioProvider provider;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Snowflake, GuildAudioManager> guildAudioManagers = new ConcurrentHashMap<>();
    private final HttpLinkUtil httpLinkUtil;

    public GuildAudioManager of(Snowflake guildId) {
        return guildAudioManagers.computeIfAbsent(guildId, ignored -> new GuildAudioManager(audioPlayerManager));
    }

    private GuildAudioManager(final AudioPlayerManager audioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager;
        player = audioPlayerManager.createPlayer();
        scheduler = new AudioTrackScheduler(player);
        provider = new LavaPlayerAudioProvider(player);
        httpLinkUtil = new HttpLinkUtil();

        player.addListener(scheduler);
    }

    /**
     * Extracts playable audio track from {@code link} and loads into audio player.
     * This is a blocking operation.
     * @param link the url containing playable audio
     */
    public Void loadItem(String link) {
        try {
            String linkOrSearchQuery = getLinkOrSearchQuery(link, "ytsearch:");
            audioPlayerManager.loadItem(linkOrSearchQuery, new AudioTrackLoadResultHandler(scheduler)).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        return (Void)null;
    }

    private String getLinkOrSearchQuery(String link, String searchTag) {
        if (httpLinkUtil.isValidHttpLink(link)) {
            return link;
        } else {
            String searchQuery = searchTag + link;
            return searchQuery;
        }
    }

    public List<AudioTrack> getPlaylist() {
        return scheduler.getQueue();
    }

    public int getPlaylistSize() {
        List<AudioTrack> playlist = getPlaylist();
        synchronized (playlist) {
            return playlist.size();
        }
    }

    public boolean isPlaylistEmpty() {
        List<AudioTrack> playlist = getPlaylist();
        synchronized (playlist) {
            return playlist.isEmpty();
        }
    }

    public boolean isCurrentlyPlaying() {
        return scheduler.isCurrentlyPlaying();
    }

    public String getPlayingTrackAsString() {
        return player.getPlayingTrack().getInfo().uri;
    }

    public void cleanUp() {
        scheduler.stop();
    }
}
