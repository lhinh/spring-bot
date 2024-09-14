package com.github.lhinh.springbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {
    Logger log = LoggerFactory.getLogger(EventListener.class);

    Class<T> getEventType();
    Mono<Void> handle(T event);

    default Mono<Void> handleError(Throwable error) {
        log.error("Unable to process event" + getEventType().getSimpleName(), error);
        return Mono.empty();
    }
}
