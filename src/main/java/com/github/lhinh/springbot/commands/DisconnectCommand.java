package com.github.lhinh.springbot.commands;


import org.springframework.stereotype.Component;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Mono;

@Component
public class DisconnectCommand implements SlashCommand {

    @Override
    public String getName() { return "disconnect"; }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {

        Mono.justOrEmpty(event.getInteraction().getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(VoiceState::getChannel)
            .flatMap(VoiceChannel::getVoiceConnection)
            .flatMap(VoiceConnection::disconnect)
            .subscribe();

        return event.reply("Disconnected!")
                .withEphemeral(true);
    }

}
