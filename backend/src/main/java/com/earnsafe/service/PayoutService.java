package com.earnsafe.service;

import com.earnsafe.entity.*;
import com.earnsafe.enums.ClaimStatus;
import com.earnsafe.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutService {

    private final PayoutRepository payoutRepository;
    private final ClaimRepository claimRepository;
    private final RestTemplate restTemplate;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public void processPayout(Claim claim, User user) {
        try {
            String razorpayPayoutId = "pout_" + UUID.randomUUID().toString().substring(0, 14);

            Payout payout = Payout.builder()
                    .claim(claim)
                    .user(user)
                    .amount(claim.getClaimAmount())
                    .razorpayPayoutId(razorpayPayoutId)
                    .razorpayStatus("processed")
                    .transactionDetails("Razorpay test mode payout for claim #" + claim.getId())
                    .processedAt(LocalDateTime.now())
                    .build();

            payoutRepository.save(payout);

            claim.setStatus(ClaimStatus.PAID);
            claimRepository.save(claim);

            log.info("Payout processed: {} for claim {}, amount: {}", razorpayPayoutId, claim.getId(), claim.getClaimAmount());
        } catch (Exception e) {
            log.error("Payout processing failed for claim {}: {}", claim.getId(), e.getMessage());
        }
    }

    public List<Payout> getUserPayouts(Long userId) {
        return payoutRepository.findByUserId(userId);
    }
}
