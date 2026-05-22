package com.hilti.booking.repository;

import com.hilti.booking.entity.AnchorCapacityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AnchorCapacityConfigRepository extends JpaRepository<AnchorCapacityConfig, Long> {
    Optional<AnchorCapacityConfig> findByAnchorSize(String anchorSize);
}
