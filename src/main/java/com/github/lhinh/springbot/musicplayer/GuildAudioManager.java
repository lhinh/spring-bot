package com.github.lhinh.springbot.musicplayer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
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
    private String lastSearchedLink = "";
    

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

    /**
     * Extracts playable audio track from {@code link} and loads into audio player.
     * This is a blocking operation.
     * @param link the url containing playable audio
     */
    public Void loadItem(String link) {
        try {
            if (link.startsWith("ytsearch:")) {
                AudioSourceManager ytAudioSourceManager = audioPlayerManager.getSourceManagers().stream()
                        .filter(source -> source instanceof YoutubeAudioSourceManager)
                        .findFirst()
                        .orElseThrow();

                AudioItem item = ytAudioSourceManager.loadItem(audioPlayerManager, new AudioReference(link, null));

                if (item instanceof AudioPlaylist) {
                    AudioTrack firstResult = ((AudioPlaylist)item).getTracks().stream()
                    .findFirst()
                    .orElseThrow();

                    lastSearchedLink = firstResult.getInfo().uri;
                    scheduler.play(firstResult);
                } else {
                    scheduler.play((AudioTrack)item);
                }
            } else {
                audioPlayerManager.loadItem(link, new AudioTrackLoadResultHandler(scheduler)).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
        return (Void)null;
    }

    public void clearPlaylist() {
        getPlaylist().clear();
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

    public AudioTrack getPlayingTrack() {
        return player.getPlayingTrack();
    }

    public String getPlayingTrackUri() {
        return getPlayingTrack().getInfo().uri;
    }

    public void cleanUp() {
        scheduler.stop();
    }
}
