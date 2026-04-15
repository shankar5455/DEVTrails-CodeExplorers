package com.earnsafe.repository;

import com.earnsafe.entity.Claim;
import com.earnsafe.entity.User;
import com.earnsafe.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByUser(User user);
    List<Claim> findByUserOrderByCreatedAtDesc(User user);
    List<Claim> findByStatus(ClaimStatus status);
    Long countByUser(User user);
    Long countByStatus(ClaimStatus status);
    Long countByIsFraudulentTrue();

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.user = :user AND c.createdAt > :since")
    Long countRecentClaimsByUser(@Param("user") User user, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.user = :user AND c.disruptionType = :type AND c.createdAt > :since")
    Long countRecentClaimsByUserAndType(@Param("user") User user, @Param("type") com.earnsafe.enums.DisruptionType type, @Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(c.claimAmount), 0) FROM Claim c WHERE c.status = 'PAID'")
    Double getTotalPayouts();

    List<Claim> findTop10ByOrderByCreatedAtDesc();
}
