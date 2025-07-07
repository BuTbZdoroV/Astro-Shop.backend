package org.chatservice.repository;

import org.bson.types.ObjectId;
import org.chatservice.model.entity.Chat;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatRepository extends ReactiveMongoRepository<Chat, ObjectId> {

    Mono<Chat> findByUser1IdAndUser2IdAndOfferId(Long user1Id, Long user2Id, Long productId);

    Flux<Chat> findAllByUser1IdOrUser2Id(Long user1Id, Long user2Id);

    @Query("{'$or': [{'user1Id': ?0}, {'user2Id': ?0}]}")
    Flux<Chat> findAllByUserId(Long userId);

    // Добавьте этот метод для эффективного обновления
    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'unreadCounts.?1' : ?2 } }")
    Mono<Void> decrementUnreadCount(ObjectId chatId, String userId, int decrementValue);
}
