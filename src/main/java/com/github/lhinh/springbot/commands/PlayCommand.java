package com.github.lhinh.springbot.commands;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Member;
import discord4j.voice.VoiceConnection;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.lhinh.springbot.musicplayer.GuildAudioManager;
import com.github.lhinh.springbot.util.HttpLinkUtil;

import reactor.core.publisher.Mono;

@Component
public class PlayCommand implements SlashCommand {

    private final GuildAudioManager guildAudioManager;
    private final HttpLinkUtil httpLinkUtil;

    public PlayCommand(@NonNull final GuildAudioManager guildAudioManager) {
        this.guildAudioManager = guildAudioManager;
        this.httpLinkUtil = new HttpLinkUtil();
    }
    
    @Override
    public String getName() { return "play"; }

    private Mono<VoiceConnection> joinMemberChannel(ChatInputInteractionEvent event) {
        return Mono.justOrEmpty(event.getInteraction().getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(VoiceState::getChannel)
            .flatMap(channel -> {
                return channel.join().withProvider(guildAudioManager.of(channel.getGuildId()).getProvider());
            });
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        Snowflake guildId = event.getInteraction().getGuildId().orElseThrow();

        Mono<Snowflake> memberVoiceChannelId =  Mono.justOrEmpty(event.getInteraction().getMember())
            .flatMap(Member::getVoiceState)
            .flatMap(voiceState -> Mono.justOrEmpty(voiceState.getChannelId()));

        Mono<Snowflake> currentVoiceChannelId = Mono.justOrEmpty(event.getClient().getVoiceConnectionRegistry())
            .flatMap(voiceConnectionRegistry -> voiceConnectionRegistry.getVoiceConnection(guildId))
            .flatMap(VoiceConnection::getChannelId);
        
        Mono<Void> joinEvent = memberVoiceChannelId.zipWith(currentVoiceChannelId)
            .flatMap(tuple -> {
                if (tuple.getT1().equals(tuple.getT2()))
                    return Mono.just("Same channel, don't join again.");

                return joinMemberChannel(event);
            })
            .switchIfEmpty(Mono.defer(() -> joinMemberChannel(event)))
            .then();

        String inputOption = event.getOption("link")
            .flatMap(ApplicationCommandInteractionOption::getValue)
            .map(ApplicationCommandInteractionOptionValue::asString)
            .orElseThrow();

        String link = getLinkOrSearchQuery(inputOption, "ytsearch:");

        GuildAudioManager currentGAM = guildAudioManager.of(guildId);

        Mono<Void> extractAndLoadAudio = Mono.justOrEmpty(currentGAM.loadItem(link));
        
        Mono<Void> editReplyOnPlaylistCount = Mono.justOrEmpty(httpLinkUtil.isValidHttpLink(link))
            .flatMap(isValidHttpLink -> {
                    String replyMessage = "";
                    String linkToEmbed = link;
                    if (!isValidHttpLink)
                        linkToEmbed = currentGAM.getLastSearchedLink();

                    if (currentGAM.isPlaylistEmpty()) {
                        replyMessage = "Now Playing: " + linkToEmbed;
                    } else {
                        int trackPosition = currentGAM.getPlaylistSize();
                        replyMessage = "#" + trackPosition + " in playlist\n" + linkToEmbed;
                    }
                    return event.editReply(replyMessage);
            }).then();

        // Mono<Void> editReplyOnSearchQuery = Mono.justOrEmpty("nothing")
        //     .flatMap(ignore -> {
        //         currentGAM.clearPlaylist();
        //         return event.editReply("Now Playing: " + currentGAM.getPlayingTrack().getInfo().uri);
        //     }).then();
        
        return event.deferReply()
            .then(joinEvent)
            .then(extractAndLoadAudio)
            .then(editReplyOnPlaylistCount);
    }

    private String getLinkOrSearchQuery(String inputOption, String searchTag) {
        if (httpLinkUtil.isValidHttpLink(inputOption)) {
            return inputOption;
        } else {
            String searchQuery = searchTag + inputOption;
            return searchQuery;
        }
    }

}
