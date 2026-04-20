package com.example.bankcards.repository;

import com.example.bankcards.entity.BlockCardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockCardRequestRepository extends JpaRepository<BlockCardRequest, Long> {

    Page<BlockCardRequest> findAllByCompletedFalse(Pageable pageable);

    @Modifying
    @Query("UPDATE #{#entityName} SET completed = true WHERE id = :cardId AND completed = false")
    void markCompletedByCardId(@Param("cardId") Long cardId);

}
