package com.hilti.booking.repository;

import com.hilti.booking.entity.FELeave;
import com.hilti.booking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FELeaveRepository extends JpaRepository<FELeave, Long> {
    List<FELeave> findByFieldExecutive(User fieldExecutive);
}
