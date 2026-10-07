package com.petcare.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    @Query("SELECT m FROM ChatMessage m WHERE m.id IN (SELECT MAX(c.id) FROM ChatMessage c GROUP BY c.sessionId) ORDER BY m.createdAt DESC")
    List<ChatMessage> findLatestMessagePerSession();
}

