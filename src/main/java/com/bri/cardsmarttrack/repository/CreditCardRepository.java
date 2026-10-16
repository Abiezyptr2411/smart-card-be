package com.bri.cardsmarttrack.repository;

import com.bri.cardsmarttrack.entity.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    @Query("SELECT c FROM CreditCard c LEFT JOIN FETCH c.subscriptions WHERE c.customer.id = :customerId ORDER BY c.id ASC")
    List<CreditCard> findByCustomerIdWithSubscriptions(@Param("customerId") Long customerId);

    List<CreditCard> findByCustomerIdOrderByIdAsc(Long customerId);

    Optional<CreditCard> findByIdAndCustomerId(Long id, Long customerId);

    boolean existsByIdAndCustomerId(Long id, Long customerId);
}
