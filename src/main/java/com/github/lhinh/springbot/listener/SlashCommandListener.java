package com.github.lhinh.springbot.listener;

import com.github.lhinh.springbot.commands.SlashCommand;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import lombok.NonNull;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.List;

@Component
public class SlashCommandListener implements EventListener<ChatInputInteractionEvent> {
    private final Collection<SlashCommand> commands;
    
    public SlashCommandListener(@NonNull List<SlashCommand> slashCommands) {
        commands = slashCommands;
    }

    @Override
    public Class<ChatInputInteractionEvent> getEventType() {
        return ChatInputInteractionEvent.class;
    }


    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        //Convert our list to a flux that we can iterate through
        return Flux.fromIterable(commands)
            //Filter out all commands that don't match the name this event is for
            .filter(command -> command.getName().equals(event.getCommandName()))
            //Get the first (and only) item in the flux that matches our filter
            .next()
            //Have our command class handle all logic related to its specific command.
            .flatMap(command -> command.handle(event));
    }
}
