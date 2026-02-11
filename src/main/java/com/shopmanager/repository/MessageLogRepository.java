package com.shopmanager.repository;

import com.shopmanager.entity.MessageLog;
import com.shopmanager.entity.enums.MessageChannel;
import com.shopmanager.entity.enums.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {

    @Query("SELECT m FROM MessageLog m " +
            "WHERE LOWER(m.messageContent) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<MessageLog> search(String query, Pageable pageable);

//    Optional<MessageLog> findTopByCustomerIdAndTypeAndChannelOrderBySentAtDesc(
//            Long customerId,
//            MessageType type,
//            MessageChannel channel
//    );
    Optional<MessageLog> findTopByCustomerIdAndTypeAndChannelOrderBySentAtDesc(
            Long customerId,
            MessageType type,
            MessageChannel channel
    );


}