package com.github.lhinh.springbot.listeners;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.TextChannelCreateSpec;
import reactor.core.publisher.Mono;

public class PinMessageListener implements EventListener<ReactionAddEvent>{

    @Override
    public Class<ReactionAddEvent> getEventType() {
        return ReactionAddEvent.class;
    }

    @Override
    public Mono<Void> handle(ReactionAddEvent event) {
        final String channelName = "new-channel";
        return event.getClient().getGuildById(event.getGuildId().orElseThrow())
            .flatMapMany(Guild::getChannels)
            .filter(channel -> channel.getName().equals(channelName) && channel.getType() == Channel.Type.GUILD_TEXT)
            .cast(TextChannel.class)
            .next()
            .switchIfEmpty(Mono.defer(() -> event.getClient().getGuildById(event.getGuildId().orElseThrow())
                .flatMap(guild -> guild.createTextChannel(TextChannelCreateSpec.builder()
                    .name(channelName)
                    .build()))
                .cast(TextChannel.class)))
            .flatMap(channel -> channel.createMessage("This is a message in the channel."))
            .then();
    }
    
}
