package com.earnsafe.repository;

import com.earnsafe.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, Long> {
    List<Payout> findByUserId(Long userId);
}
