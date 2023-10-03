package com.github.lhinh.springbot.musicplayer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class AudioTrackScheduler extends AudioEventAdapter {

  private final List<AudioTrack> queue;
  private final AudioPlayer player;
  private boolean isCurrentlyPlaying;

  public AudioTrackScheduler(AudioPlayer player) {
    // The queue may be modifed by different threads so guarantee memory safety
    // This does not, however, remove several race conditions currently present
    queue = Collections.synchronizedList(new LinkedList<>());
    this.player = player;
    isCurrentlyPlaying = false;
  }

  public List<AudioTrack> getQueue() {
    return queue;
  }

  public boolean play(AudioTrack track) {
    return play(track, false);
  }

  public boolean play(AudioTrack track, boolean force) {
    boolean isPlayingThisTrack = player.startTrack(track, !force);

    if (!isPlayingThisTrack) {
      synchronized (queue) {
        queue.add(track);
      }
    } else {
      isCurrentlyPlaying = true;
    }
    
    return isPlayingThisTrack;
  }

  public boolean skip() {
    if (!queue.isEmpty()) {
      synchronized (queue) {
        AudioTrack nextTrack = queue.remove(0);
        return play(nextTrack, true);
      }
    } else {
      stop();
      return isCurrentlyPlaying;
    }
  }

  public void stop() {
    synchronized (queue) {
      queue.clear();
      player.stopTrack();
      isCurrentlyPlaying = false;
    }
  }

  public boolean isCurrentlyPlaying() {
    return isCurrentlyPlaying;
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Advance the player if the track completed naturally (FINISHED) or if the track cannot play (LOAD_FAILED)
    if (endReason.mayStartNext) {
      skip();
    }
  }
}
