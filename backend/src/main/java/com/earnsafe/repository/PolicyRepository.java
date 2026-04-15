package com.earnsafe.repository;

import com.earnsafe.entity.Policy;
import com.earnsafe.entity.User;
import com.earnsafe.enums.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByUser(User user);
    List<Policy> findByUserAndStatus(User user, PolicyStatus status);
    Optional<Policy> findFirstByUserAndStatusOrderByCreatedAtDesc(User user, PolicyStatus status);
    List<Policy> findByStatus(PolicyStatus status);

    @Query("SELECT COALESCE(SUM(p.premiumAmount), 0) FROM Policy p")
    Double getTotalPremiumCollected();
}
