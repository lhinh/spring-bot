package com.github.lhinh.springbot.musicplayer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.voice.AudioProvider;

@Configuration
public class AudioProviderConfig {
	
	@Bean
	public AudioPlayerManager playerManager() {
		// Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        // This is an optimization strategy that Discord4J can utilize. It is not important to understand
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);
        return playerManager;
	}
	
	@Bean
	public AudioPlayer player() {
		final AudioPlayer player = playerManager().createPlayer();
        return player;
	}
	
	@Bean
	public AudioProvider provider() {
        // We will be creating LavaPlayerAudioProvider in the next step
		return new LavaPlayerAudioProvider(player());
	}
	
	@Bean
	public TrackScheduler scheduler() {
		final TrackScheduler scheduler = new TrackScheduler(player());
		return scheduler;
	}
	
//	@Bean
//	public HashMap<Long, TrackScheduler>
}
