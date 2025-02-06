package com.tuanloc.order_service.listener;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.tuanloc.order_service.event.OrderPlacedEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPlacedEventListener {
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final ObservationRegistry observationRegistry;

    @EventListener
    public void handleOrderPlacedEvent(OrderPlacedEvent event){
        log.info("Order placed Event received, sending OrderPlacedEvent to notificationTopic: {}", event.getOrderNumber());

        try {
            Observation.createNotStarted("notification-topic", this.observationRegistry).observeChecked(()->{
                CompletableFuture<org.springframework.kafka.support.SendResult<String, OrderPlacedEvent>> future = kafkaTemplate.send("notificationTopic",
                new OrderPlacedEvent(event.getOrderNumber()));
                return future.handle((result, throwable) -> CompletableFuture.completedFuture(result));
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error while sending message to Kafka", e);
        }
    }
}
