package org.project.evconnectbackend.repository;

import org.project.evconnectbackend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByBookingIdOrderByCreatedAtAsc(Long bookingId);
    List<ChatMessage> findByBookingIdAndIsReadFalseOrderByCreatedAtAsc(Long bookingId);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.booking.id = :bookingId AND m.isRead = false AND m.sender.email != :email")
    List<ChatMessage> findByBookingIdAndIsReadFalseAndSenderEmailNot(@Param("bookingId") Long bookingId, @Param("email") String email);
} 