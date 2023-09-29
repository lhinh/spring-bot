package com.github.lhinh.springbot.musicplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class AudioTrackLoadResultHandler implements AudioLoadResultHandler {

    private final AudioTrackScheduler scheduler;

    public AudioTrackLoadResultHandler(AudioTrackScheduler scheduler) { this.scheduler = scheduler; }

    @Override
    public void trackLoaded(AudioTrack track) {
        scheduler.play(track);
        log.info("Loaded track: " + track.toString());
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        playlist.getTracks().stream().forEach(track -> scheduler.play(track));
        log.info("Playlist loaded: " + playlist.toString());
    }

    @Override
    public void noMatches() {
        log.error("No matches...");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        log.error("Load failed, Exception: " + exception.getMessage());
    }
}
