package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, QuerydslPredicateExecutor<Card> {

    @Modifying
    @Query("UPDATE #{#entityName} SET status = :status WHERE id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") CardStatus status);

    Optional<Card> findByIdAndOwnerId(Long id, Long ownerId);

    /**
     * Метод для переводов. Пессимистическая WRITE блокировка. order by id для предотвращения deadlock
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Card> findAllByIdInAndOwnerIdOrderByIdAsc(List<Long> ids, Long ownerId);

}
