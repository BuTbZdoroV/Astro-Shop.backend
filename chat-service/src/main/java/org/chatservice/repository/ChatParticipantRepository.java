package org.chatservice.repository;

import org.bson.types.ObjectId;
import org.chatservice.model.entity.ChatParticipant;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatParticipantRepository extends ReactiveMongoRepository<ChatParticipant, ObjectId> {
    Mono<ChatParticipant> findByChatIdAndUserId(ObjectId chatId, Long userId);

    Flux<ChatParticipant> findByUserId(Long userId);

    Flux<ChatParticipant> findByChatId(ObjectId chatId);
}