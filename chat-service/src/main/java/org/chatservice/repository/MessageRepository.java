package org.chatservice.repository;

import org.bson.types.ObjectId;
import org.chatservice.model.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends ReactiveMongoRepository<Message, ObjectId> {

    Flux<Message> findByChatId(ObjectId chatId, Pageable pageable);
    Flux<Message> findByChatIdAndSenderId(ObjectId chatId, Long senderId);
    Mono<Message> findFirstByChatIdOrderByCreatedAtDesc(ObjectId chatId);

}
