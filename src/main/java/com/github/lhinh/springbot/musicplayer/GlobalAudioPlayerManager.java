package com.github.lhinh.springbot.musicplayer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.lhinh.springbot.config.DiscordConfigProperties;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.Web;

@Configuration
public class GlobalAudioPlayerManager {

    private final DiscordConfigProperties discordConfigProperties;

    public GlobalAudioPlayerManager(DiscordConfigProperties discordConfigProperties) {
        this.discordConfigProperties = discordConfigProperties;
        Web.setPoTokenAndVisitorData(this.discordConfigProperties.getPoToken(), this.discordConfigProperties.getVisitorData());
    }

    @Bean
    public AudioPlayerManager audioPlayerManager() {
        AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
        audioPlayerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        // Register remote sources
        audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager(true, true, true));
        audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new GetyarnAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));
        return audioPlayerManager;
    }
    
}
