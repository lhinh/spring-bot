package com.github.lhinh.springbot.musicplayer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.lhinh.springbot.service.MessageSenderService;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import static org.junit.jupiter.api.Assertions.*;

public class AudioTrackSchedulerTest {
        
    @Test
    void testAudioTrackSchedulerPlay_EmptyPlaylist() {
        boolean force = true;
        AudioPlayer player = Mockito.mock(AudioPlayer.class);
        MessageSenderService messageSenderService = Mockito.mock(MessageSenderService.class);
        AudioTrackScheduler scheduler = new AudioTrackScheduler(player, messageSenderService);
        
        AudioTrack track = Mockito.mock(AudioTrack.class);
        Mockito.when(player.startTrack(track, force)).thenReturn(true);
        boolean isPlayingThisTrack = scheduler.play(track);
        boolean isCurrentlyPlaying = scheduler.isCurrentlyPlaying();
        
        assertTrue(isPlayingThisTrack);
        assertTrue(isCurrentlyPlaying);
    }

    @Test
    void testAudioTrackSchedulerPlay_AddOneToPlaylist() {
        boolean force = true;
        AudioPlayer player = Mockito.mock(AudioPlayer.class);
        MessageSenderService messageSenderService = Mockito.mock(MessageSenderService.class);
        AudioTrackScheduler scheduler = new AudioTrackScheduler(player, messageSenderService);
        
            
        AudioTrack track1 = Mockito.mock(AudioTrack.class);
        AudioTrack track2 = Mockito.mock(AudioTrack.class);

        Mockito.when(player.startTrack(track1, force)).thenReturn(true);

        boolean isPlayingThisTrack = scheduler.play(track1);
        boolean isCurrentlyPlaying = scheduler.isCurrentlyPlaying();
        assertTrue(isPlayingThisTrack);
        assertTrue(isCurrentlyPlaying);

        isPlayingThisTrack = scheduler.play(track2);
        isCurrentlyPlaying = scheduler.isCurrentlyPlaying();
        assertFalse(isPlayingThisTrack);
        assertTrue(isCurrentlyPlaying);
    }

    @Test
    void testAudioTrackSchedulerPlay_AddPlaylistToPlaylist() {
        //TODO
    }

    @Test
    void testAudioTrackSchedulerPlay_YTSearchQueryEmptyPlaylist() {
        //TODO
    }

    @Test
    void testAudioTrackSchedulerPlay_YTSearchQueryPlaylistLoaded() {
        //TODO
    }
    
    @Test
    void testAudioTrackSchedulerSkip_SkipToStop() {
        boolean force = true;
        AudioPlayer player = Mockito.mock(AudioPlayer.class);
        MessageSenderService messageSenderService = Mockito.mock(MessageSenderService.class);
        AudioTrackScheduler scheduler = new AudioTrackScheduler(player, messageSenderService);
        
            
        AudioTrack track1 = Mockito.mock(AudioTrack.class);
        AudioTrack track2 = Mockito.mock(AudioTrack.class);

        Mockito.when(player.startTrack(track1, force)).thenReturn(true);

        scheduler.play(track1);
        boolean isCurrentlyPlaying = scheduler.isCurrentlyPlaying();
        assertTrue(isCurrentlyPlaying);

        scheduler.play(track2);
        isCurrentlyPlaying = scheduler.isCurrentlyPlaying();
        assertTrue(isCurrentlyPlaying);
        
        Mockito.when(player.startTrack(Mockito.any(AudioTrack.class), Mockito.eq(!force))).thenReturn(true);

        boolean skipResult = scheduler.skip();
        isCurrentlyPlaying = scheduler.isCurrentlyPlaying();
        assertTrue(skipResult);
        assertTrue(isCurrentlyPlaying);

        skipResult = scheduler.skip();
        isCurrentlyPlaying = scheduler.isCurrentlyPlaying();
        assertFalse(skipResult);
        assertFalse(isCurrentlyPlaying);
    }
    
    @Test
    void testAudioTrackSchedulerStop() {
        boolean force = true;
        AudioPlayer player = Mockito.mock(AudioPlayer.class);
        MessageSenderService messageSenderService = Mockito.mock(MessageSenderService.class);
        AudioTrackScheduler scheduler = new AudioTrackScheduler(player, messageSenderService);
        
        AudioTrack track = Mockito.mock(AudioTrack.class);
        Mockito.when(player.startTrack(track, force)).thenReturn(true);
        scheduler.play(track);
        
        scheduler.stop();
        assertTrue(scheduler.getQueue().size() == 0);
        assertFalse(scheduler.isCurrentlyPlaying());
    }
    
}