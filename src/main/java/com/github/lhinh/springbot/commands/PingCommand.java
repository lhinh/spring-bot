package com.github.lhinh.springbot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.common.util.Snowflake;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PingCommand implements SlashCommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        //We reply to the command with "Pong!" and make sure it is ephemeral (only the command user can see it)
        String guildId = event.getInteraction().getGuildId()
                .map(Snowflake::asString).get();
        return event.reply()
            .withEphemeral(true)
            .withContent("GuildId = " + guildId);
    }
}
