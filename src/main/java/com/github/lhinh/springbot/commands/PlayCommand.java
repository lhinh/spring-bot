package com.github.lhinh.springbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PlayCommand implements SlashCommand {

    private final AudioPlayerManager playerManager;
    private final TrackScheduler scheduler;
    private final AudioProvider provider;

    PlayCommand(@NonNull AudioPlayerManager playerManager, @NonNull TrackScheduler scheduler, @NonNull AudioProvider provider
        ) {
        this.playerManager = playerManager;
        this.scheduler = scheduler;
        this.provider = provider;
    }
    
    @Override
    public String getName() {
        return "play";
    }

    public Snowflake getSnowflakeId(Snowflake channelId) { return channelId; }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Snowflake guildId = event.getInteraction().getGuildId().orElseThrow();

        Mono<Snowflake> memberVoiceChannelId = Mono.justOrEmpty(event.getInteraction().getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(VoiceState::getChannel)
            .flatMap(VoiceChannel::getVoiceConnection)
            .flatMap(VoiceConnection::getChannelId);

        Mono<Snowflake> currentVoiceChannelId = Mono.justOrEmpty(event.getClient().getVoiceConnectionRegistry())
            .flatMap(voiceConnectionRegistry -> voiceConnectionRegistry.getVoiceConnection(guildId))
            .flatMap(VoiceConnection::getChannelId);
        
        memberVoiceChannelId.zipWith(currentVoiceChannelId)
            .flatMap(tuple -> {
                if (tuple.getT1().equals(tuple.getT2()))
                    return Mono.empty();
                
                log.info("Joining voice channel.");
                return Mono.justOrEmpty(event.getInteraction().getMember())
                    .flatMap(Member::getVoiceState)
                    .flatMap(VoiceState::getChannel)
                    .flatMap(channel -> channel.join().withProvider(provider))
                    .then();
            })
            .subscribe();
                
        String link = event.getOption("link")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .get();
        
        // playerManager.loadItem(link, scheduler);
        
        log.info("Playing link: " + link);
        
        return event.reply("Now Playing: " + link).withEphemeral(true);
    }

}
