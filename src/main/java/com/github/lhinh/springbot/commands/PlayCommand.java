package com.github.lhinh.springbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.GuildAudioManager;
import com.github.lhinh.springbot.util.HttpLinkUtil;

import reactor.core.publisher.Mono;

@Component
public class PlayCommand implements SlashCommand {

    private final GuildAudioManager guildAudioManager;

    public PlayCommand(@NonNull final GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
    }

    @Override
    public String getName() {
        return "play";
    }

    private Mono<Void> joinMemberChannel(ChatInputInteractionEvent event) {
        return Mono.justOrEmpty(event.getInteraction().getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(channel -> {
                    return channel.join().withProvider(guildAudioManager.of(channel.getGuildId()).getProvider());
                }).then();
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Snowflake guildId = event.getInteraction().getGuildId().orElseThrow();
        Snowflake channelId = event.getInteraction().getChannelId();
        return event.deferReply()
                .then(joinMemberChannel(event))
                .then(Mono.fromCallable(() -> {
                    String inputOption = event.getOption("link")
                            .flatMap(ApplicationCommandInteractionOption::getValue)
                            .map(ApplicationCommandInteractionOptionValue::asString)
                            .orElseThrow();

                    return getLinkOrSearchQuery(inputOption, "ytsearch:");
                })
                        .flatMap(link -> {
                            GuildAudioManager currentGAM = guildAudioManager.of(guildId);
                            currentGAM.setGuildAndChannelId(guildId, channelId);
                            currentGAM.loadItem(link);
                            boolean isValidHttpLink = HttpLinkUtil.isValidHttpLink(link);

                            String replyMessage = "";
                            String linkToEmbed = link;
                            if (!isValidHttpLink)
                                linkToEmbed = currentGAM.getLastSearchedLink();

                            if (currentGAM.isPlaylistEmpty()) {
                                replyMessage = "Now Playing: " + linkToEmbed;
                            } else {
                                int trackPosition = currentGAM.getPlaylistSize();
                                replyMessage = "#" + trackPosition + " in playlist.\n" + linkToEmbed;
                            }

                            return event.editReply(replyMessage);
                        }).then());
    }

    private String getLinkOrSearchQuery(String inputOption, String searchTag) {
        if (HttpLinkUtil.isValidHttpLink(inputOption)) {
            return inputOption;
        } else {
            return searchTag + inputOption;
        }
    }

}
