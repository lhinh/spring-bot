package com.github.lhinh.springbot.commands;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.AudioTrackScheduler;
import com.github.lhinh.springbot.musicplayer.GuildAudioManager;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

@Component
public class SkipCommand implements SlashCommand {

    private final GuildAudioManager guildAudioManager;

    public SkipCommand(@NonNull final GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }
    
    @Override
    public String getName() { return "skip"; }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Snowflake guildId = event.getInteraction().getGuildId().orElseThrow();

        GuildAudioManager currentGAM = guildAudioManager.of(guildId);
        AudioTrackScheduler scheduler = currentGAM.getScheduler();

        if (scheduler.skip()) {
            String nextTrack = currentGAM.getPlayingTrackUri();
            StringBuilder sb = new StringBuilder("Next track playing: %s");
            String replyMessage = "";
            if (!currentGAM.isPlaylistEmpty()) {
                int playlistSize = currentGAM.getPlaylistSize();
                if (playlistSize == 1)
                    sb.insert(0, "%d track in playlist.\n");
                else
                    sb.insert(0, "%d tracks in playlist.\n");
                replyMessage = String.format(sb.toString(), playlistSize, nextTrack);
            } else {
                sb.insert(0, "None left in playlist.\n");
                replyMessage = String.format(sb.toString(), nextTrack);
            }
            
            return event.reply(replyMessage);
        }

        return event.reply("Nothing left to play :(");
    }
    
}
