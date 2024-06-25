package com.github.lhinh.springbot.listeners;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.config.DiscordConfigProperties;
import com.github.lhinh.springbot.util.HttpLinkUtil;

import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.TextChannelCreateSpec;
import discord4j.rest.util.Color;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@RequiredArgsConstructor
@Component
public class PinMessageListener implements EventListener<ReactionAddEvent>{

    private final DiscordConfigProperties discordConfigProperties;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    // For posterity
    // private final Color nutMeat = Color.of(222, 197, 183);

    @Override
    public Class<ReactionAddEvent> getEventType() {
        return ReactionAddEvent.class;
    }

    @Override
    public Mono<Void> handle(ReactionAddEvent event) {
        if (!ReactionEmoji.unicode("📌").equals(event.getEmoji()))
            return Mono.empty();

        final String channelName = discordConfigProperties.getPinChannelName();

        return event.getMessage()
            .filterWhen(message -> checkIfAlreadyPinned(message, event, channelName).map(isPinned -> !isPinned))
            .flatMap(message -> createPinContentEmbed(message, event))
            .flatMap(embed -> sendMessageToChannel(embed, event, channelName))
            .then();
    }

    private Mono<EmbedCreateSpec> createPinContentEmbed(Message message, ReactionAddEvent event) {
        String messageUrl = String.format(
            "https://discord.com/channels/%s/%s/%s",
            event.getGuildId().orElseThrow().asString(),
            event.getChannelId().asString(),
            message.getId().asString());
        String messageDate = message.getTimestamp()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DATE_FORMATTER);
        String messageHyperlink = String.format("[%s](%s)", "Jump!", messageUrl);
        EmbedCreateSpec.Builder pinContentEmbed = EmbedCreateSpec.builder()
            .author(message.getAuthor().orElseThrow().getUsername(), null, message.getAuthor().orElseThrow().getAvatarUrl())
            .color(Color.TAHITI_GOLD)
            .footer(message.getId().asString(), null)
            .timestamp(Instant.now())
            .addField("**Source**", messageHyperlink, true)
            .addField("**Date Posted**", messageDate, true);
    
        if (HttpLinkUtil.isImage(message.getContent())) {

            pinContentEmbed.image(message.getContent());
        } else {
            pinContentEmbed.description(message.getContent());

            List<String> imageUrls = extractUrls(message.getAttachments(), true);
            imageUrls.stream().findFirst().ifPresent(pinContentEmbed::image);
        }

        List<String> attachmentUrls = extractUrls(message.getAttachments(), false);
        if (!attachmentUrls.isEmpty()) {
            String attachmentsField = String.join("\n", attachmentUrls);
            pinContentEmbed.addField("Attachments", attachmentsField, false);
        }
    
        return Mono.just(pinContentEmbed.build());
    }

    private List<String> extractUrls(List<Attachment> attachments, boolean isImage) {
        return attachments.stream()
            .filter(attachment -> attachment.getContentType()
                .map(type -> (isImage ? type.startsWith("image/") : !type.startsWith("image/")))
                .orElse(false))
            .map(Attachment::getUrl)
            .collect(Collectors.toList());
    }
    
    private Mono<Object> sendMessageToChannel(EmbedCreateSpec embed, ReactionAddEvent event, String channelName) {
        return event.getClient().getGuildById(event.getGuildId().orElseThrow())
            .flatMapMany(Guild::getChannels)
            .filter(channel -> channel.getName().equals(channelName) && channel.getType() == Channel.Type.GUILD_TEXT)
            .cast(TextChannel.class)
            .next()
            .switchIfEmpty(Mono.defer(() -> createChannel(event, channelName)))
            .flatMap(channel -> channel.createMessage(embed));
    }
    
    private Mono<TextChannel> createChannel(ReactionAddEvent event, String channelName) {
        return event.getClient().getGuildById(event.getGuildId().orElseThrow())
            .flatMap(guild -> guild.createTextChannel(TextChannelCreateSpec.builder().name(channelName).build()))
            .cast(TextChannel.class);
    }

    private Mono<Boolean> checkIfAlreadyPinned(Message message, ReactionAddEvent event, String channelName) {
        return event.getClient().getGuildById(event.getGuildId().orElseThrow())
            .flatMapMany(Guild::getChannels)
            .filter(channel -> channel.getName().equals(channelName) && channel.getType() == Channel.Type.GUILD_TEXT)
            .cast(TextChannel.class)
            .flatMap(channel -> channel.getMessagesBefore(channel.getLastMessageId().orElseThrow()))
            .flatMapIterable(msg -> msg.getEmbeds())
            .any(embed -> embed.getFooter().map(footer -> footer.getText().equals(message.getId().asString())).orElse(false));
    }
}
