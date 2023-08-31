package com.github.lhinh.springbot.musicplayer;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public final class TrackScheduler implements AudioLoadResultHandler {
	
    private final AudioPlayer player;
    
    public TrackScheduler(final AudioPlayer player) { this.player = player; }

    @Override
    public void trackLoaded(final AudioTrack track) {
    	//TODO add queue for music list
		// LavaPlayer found an audio source for us to play
        player.playTrack(track);
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist) {
        // LavaPlayer found multiple AudioTracks from some playlist
    }

    @Override
    public void noMatches() {
        // LavaPlayer did not find any audio to extract
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        // LavaPlayer could not parse an audio source for some reason
    	log.error("Link was not able to load:");
    	log.error(exception.getMessage());
    }
}