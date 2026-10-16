package com.bri.cardsmarttrack.repository;

import com.bri.cardsmarttrack.entity.Subscription;
import com.bri.cardsmarttrack.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByCreditCardIdOrderByNextBillingDateAsc(Long creditCardId);

    List<Subscription> findByCreditCardIdAndStatusOrderByNextBillingDateAsc(Long creditCardId, SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s JOIN FETCH s.creditCard c JOIN FETCH c.customer cust WHERE s.id = :id")
    Optional<Subscription> findByIdWithCardAndCustomer(@Param("id") Long id);

    long countByCreditCardIdAndStatus(Long creditCardId, SubscriptionStatus status);
}
