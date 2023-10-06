// package com.github.lhinh.springbot.listeners;

// import java.time.Duration;

// import org.reactivestreams.Publisher;
// import org.springframework.stereotype.Component;

// import discord4j.common.util.Snowflake;
// import discord4j.core.event.domain.VoiceStateUpdateEvent;
// import discord4j.core.object.VoiceState;
// import discord4j.core.object.entity.channel.VoiceChannel;
// import discord4j.voice.VoiceConnection;
// import reactor.core.publisher.Mono;

// @Component
// public class AutoDisconnectListener implements EventListener<VoiceStateUpdateEvent> {

//     @Override
//     public Class<VoiceStateUpdateEvent> getEventType() {
//         return VoiceStateUpdateEvent.class;
//     }

//     @Override
//     public Mono<Void> handle(VoiceStateUpdateEvent event) {
//         // The bot itself has a VoiceState; 1 VoiceState signals bot is alone
//         Publisher<Boolean> voiceStateCounter = event.getCurrent().getChannel()
//             .map(VoiceChannel::getVoiceStates)
//             .flatMap(voiceStates -> voiceStates.count())
//             .map(count -> {
//                 log.info("VC counter: " + count);
//                 return 1L == count;
//             });

//         // After 10 seconds, check if the bot is alone. This is useful if
//         // the bot joined alone, but no one else joined since connecting
//         // Mono<Void> onDelay = Mono.delay(Duration.ofSeconds(5L))
//         //     .filterWhen(ignored -> voiceStateCounter)
//         //     .switchIfEmpty(Mono.never())
//         //     .then();

//         Snowflake currentGuildId = event.getCurrent().getGuildId();

//         Mono<Snowflake> botChannelId = event.getClient().getVoiceConnectionRegistry().getVoiceConnection(currentGuildId)
//                     .flatMap(VoiceConnection::getChannelId);
        
//         Mono<Snowflake> oldChannelId = Mono.justOrEmpty(event.getOld().flatMap(VoiceState::getChannelId));

//         Mono<Void> onEvent = oldChannelId.zipWith(botChannelId)
//             .filter(tuple -> {
//                 log.info("Old channel id " + tuple.getT1());
//                 log.info("bot channel id " + tuple.getT2());
//                 boolean isEqual = tuple.getT1().equals(tuple.getT2());
//                 log.info("Is equal: " + String.valueOf(isEqual));
//                 return tuple.getT1().equals(tuple.getT2());
//             })
//             .filterWhen(ignored -> voiceStateCounter)
//             .then();

//         // Mono<Void> onEvent = Mono.justOrEmpty(event.getCurrent().getChannelId())
//         //     .filter(currentChannelId -> event.getOld().flatMap(VoiceState::getChannelId).map(currentChannelId::equals).orElse(false))
//         //     .filterWhen(ignored -> voiceStateCounter)
//         //     .then();

//         Mono<Void> disconnect = event.getCurrent().getChannel()
//             .flatMap(VoiceChannel::getVoiceConnection)
//             .flatMap(VoiceConnection::disconnect);

//         // Disconnect the bot if either onDelay or onEvent are completed!
//         return Mono.firstWithSignal(onEvent).then(disconnect);
//         // return Mono.empty();
//     }
    
// }
