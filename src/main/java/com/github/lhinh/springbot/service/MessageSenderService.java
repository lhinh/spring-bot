package com.github.lhinh.springbot.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;

import reactor.core.publisher.Mono;


@Component
public class MessageSenderService {

    private final GatewayDiscordClient gatewayDiscordClient;
    private final String ALL_DONE_MSG = "Nothing left to play :(";

    public MessageSenderService(@Lazy @NonNull final GatewayDiscordClient gatewayDiscordClient) {
        this.gatewayDiscordClient = gatewayDiscordClient;
    }

    public Mono<Object> sendMessageToChannel(Snowflake guildId, Snowflake channelId, String message) {

        return gatewayDiscordClient.getGuildById(guildId)
                .flatMapMany(Guild::getChannels)
                .filter(channel -> channel.getId().equals(channelId)
                        && channel.getType() == Channel.Type.GUILD_TEXT)
                .cast(TextChannel.class)
                .next()
                .flatMap(channel -> channel.createMessage(message));
    }

    public Mono<Object> sendCurrentlyPlayingTrack(
            Snowflake guildId, Snowflake channelId, int trackPosition, String link) {
        String message;
        if (trackPosition <= 1) {
            message = String.format("%d track in playlist.\nNext track playing: %s", trackPosition, link);
        } else {
            message = String.format("%d tracks in playlist.\nNext track playing: %s", trackPosition, link);
        }
        return sendMessageToChannel(guildId, channelId, message);
    }

    public Mono<Object> sendAllDone(
            Snowflake guildId, Snowflake channelId) {
        return sendMessageToChannel(guildId, channelId, ALL_DONE_MSG);
    }
}
