package com.github.lhinh.springbot.commands;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.GuildAudioManager;

import discord4j.core.object.entity.Member;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import reactor.core.publisher.Mono;

@Component
public class JoinCommand implements SlashCommand {
    
    private final GuildAudioManager guildAudioManager;

    JoinCommand(@NonNull final GuildAudioManager guildAudioManager) { this.guildAudioManager = guildAudioManager; }
    
    @Override
    public String getName() { return "join"; }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        
        Mono.justOrEmpty(event.getInteraction().getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(VoiceState::getChannel)
            .flatMap(channel -> channel.join().withProvider(guildAudioManager.of(channel.getGuildId()).getProvider()))
            .subscribe();

        return event.reply("Joined voice channel!")
                .withEphemeral(true);
    }

}
