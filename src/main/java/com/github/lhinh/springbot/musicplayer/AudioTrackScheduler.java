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
  private boolean isPlaying;

  public AudioTrackScheduler(AudioPlayer player) {
    // The queue may be modifed by different threads so guarantee memory safety
    // This does not, however, remove several race conditions currently present
    queue = Collections.synchronizedList(new LinkedList<>());
    this.player = player;
    isPlaying = false;
  }

  public List<AudioTrack> getQueue() {
    return queue;
  }

  public boolean play(AudioTrack track) {
    isPlaying = play(track, false);
    return isPlaying;
  }

  public boolean play(AudioTrack track, boolean force) {
    isPlaying = player.startTrack(track, !force);

    if (!isPlaying) 
      queue.add(track);
    
    return isPlaying;
  }

  public boolean skip() {
    return !queue.isEmpty() && play(queue.remove(0), true);
  }

  public void stop() {
    queue.clear();
    player.stopTrack();
  }

  public boolean isPlaying() {
    return isPlaying;
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Advance the player if the track completed naturally (FINISHED) or if the track cannot play (LOAD_FAILED)
    if (endReason.mayStartNext) {
      skip();
    }
  }
}
